package com.example.myapplication.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import org.json.JSONArray
import org.json.JSONObject
import java.util.Date

val Context.syncDataStore: androidx.datastore.core.DataStore<Preferences> by preferencesDataStore(name = "sync_store")

data class PendingChange(
    val id: String = "",
    val entity: String = "",
    val action: String = "",
    val timestamp: Long = 0L,
    val payload: String = ""
)

data class SyncResult(
    val success: Boolean,
    val syncedCount: Int = 0,
    val conflicts: List<String> = emptyList(),
    val message: String? = null
)

class SyncManager(
    private val context: Context,
    private val dataSource: DataSource,
    private val localAuthManager: LocalAuthManager
) {
    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val _lastSync = MutableStateFlow(0L)
    val lastSync: StateFlow<Long> = _lastSync.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _syncLog = MutableStateFlow<List<String>>(emptyList())
    val syncLog: StateFlow<List<String>> = _syncLog.asStateFlow()

    private val lastSyncKey = longPreferencesKey("last_sync")
    private val pendingChangesKey = stringPreferencesKey("pending_changes")

    init {
        kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val prefs = context.syncDataStore.data.first()
            _lastSync.value = prefs[lastSyncKey] ?: 0L
            loadPendingChanges(prefs[pendingChangesKey])
            startNetworkMonitoring()
        }
    }

    // ── Public API ────────────────────────────────────────────────────────────

    suspend fun sync(): SyncResult {
        if (_isSyncing.value) return SyncResult(success = false, message = "Sync already in progress")
        _isSyncing.value = true

        return try {
            val pending = getPendingChanges()
            addLog("Starting sync: ${pending.size} pending changes")

            val synced = mutableListOf<String>()
            val conflicts = mutableListOf<String>()

            for (change in pending) {
                when (change.action) {
                    "create", "update" -> {
                        val result = pushChange(change)
                        if (result.isSuccess) {
                            synced.add(change.id)
                        } else {
                            conflicts.add(change.id)
                        }
                    }
                    "delete" -> {
                        val result = pushDelete(change)
                        if (result.isSuccess) {
                            synced.add(change.id)
                        } else {
                            conflicts.add(change.id)
                        }
                    }
                }
            }

            if (conflicts.isEmpty()) {
                clearPendingChanges()
                val now = System.currentTimeMillis()
                _lastSync.value = now
                context.syncDataStore.edit { it[lastSyncKey] = now }
                addLog("Sync completed: ${synced.size} changes synced")
                SyncResult(success = true, syncedCount = synced.size, conflicts = emptyList())
            } else {
                addLog("Sync completed with ${conflicts.size} conflicts")
                SyncResult(success = true, syncedCount = synced.size, conflicts = conflicts)
            }
        } catch (e: Exception) {
            addLog("Sync failed: ${e.message}")
            SyncResult(success = false, message = e.message)
        } finally {
            _isSyncing.value = false
        }
    }

    suspend fun markPending(entity: String, action: String, payload: Any) {
        val change = PendingChange(
            id = "${entity}_${System.currentTimeMillis()}",
            entity = entity,
            action = action,
            timestamp = System.currentTimeMillis(),
            payload = when (payload) {
                is String -> payload
                else -> JSONObject(payload.toString()).toString()
            }
        )
        addPendingChange(change)
        addLog("Pending: $action $entity")
    }

    suspend fun resolveConflict(changeId: String, strategy: ConflictResolution = ConflictResolution.LAST_WRITE_WINS): SyncResult {
        val pending = getPendingChanges()
        val change = pending.find { it.id == changeId } ?: return SyncResult(success = false, message = "Change not found")

        val resolved = when (strategy) {
            ConflictResolution.LAST_WRITE_WINS -> {
                val result = pushChange(change)
                if (result.isSuccess) {
                    removePendingChange(changeId)
                    addLog("Conflict resolved (last-write-wins): $changeId")
                    SyncResult(success = true, syncedCount = 1)
                } else {
                    SyncResult(success = false, message = "Push failed")
                }
            }
            ConflictResolution.KEEP_LOCAL -> {
                removePendingChange(changeId)
                addLog("Conflict resolved (keep local): $changeId")
                SyncResult(success = true, syncedCount = 1)
            }
            ConflictResolution.KEEP_REMOTE -> {
                // Just remove from pending, remote wins
                removePendingChange(changeId)
                addLog("Conflict resolved (keep remote): $changeId")
                SyncResult(success = true, syncedCount = 1)
            }
            ConflictResolution.MANUAL -> {
                addLog("Conflict flagged for manual review: $changeId")
                SyncResult(success = false, message = "Manual review required")
            }
        }
        return resolved
    }

    fun setOnlineState(isOnline: Boolean) {
        _isOnline.value = isOnline
        addLog("Network state: ${if (isOnline) "online" else "offline"}")
    }

    private suspend fun getPendingChanges(): List<PendingChange> {
        val prefs = context.syncDataStore.data.first()
        return parsePendingChanges(prefs[pendingChangesKey])
    }

    // ── Private ───────────────────────────────────────────────────────────────

    private suspend fun addPendingChange(change: PendingChange) {
        val current = getPendingChanges().toMutableList()
        current.add(change)
        savePendingChanges(current)
    }

    private suspend fun removePendingChange(changeId: String) {
        val current = getPendingChanges().toMutableList()
        current.removeAll { it.id == changeId }
        savePendingChanges(current)
    }

    private suspend fun clearPendingChanges() {
        context.syncDataStore.edit { it.remove(pendingChangesKey) }
    }

    private suspend fun savePendingChanges(changes: List<PendingChange>) {
        context.syncDataStore.edit { prefs ->
            prefs[pendingChangesKey] = JSONArray(changes.map { JSONObject(it.toString()) }).toString()
        }
    }

    private fun parsePendingChanges(json: String?): List<PendingChange> {
        if (json == null) return emptyList()
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                PendingChange(
                    id = obj.optString("id", ""),
                    entity = obj.optString("entity", ""),
                    action = obj.optString("action", ""),
                    timestamp = obj.optLong("timestamp", 0L),
                    payload = obj.optString("payload", "")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun loadPendingChanges(json: String?) {
        // Loaded into StateFlow via getPendingChanges()
    }

    private suspend fun pushChange(change: PendingChange): Result<Unit> {
        delay(300)
        return Result.success(Unit)
    }

    private suspend fun pushDelete(change: PendingChange): Result<Unit> {
        delay(200)
        return Result.success(Unit)
    }

    private fun addLog(message: String) {
        val timestamp = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(Date())
        val newLog = _syncLog.value.toMutableList()
        newLog.add("[$timestamp] $message")
        if (newLog.size > 50) newLog.removeAt(0)
        _syncLog.value = newLog
    }

    private fun startNetworkMonitoring() {
        // In a real app, use ConnectivityManager.NetworkCallback
        // For now, we simulate with a simple flow
        kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            while (true) {
                delay(30_000)
                // Pretend we check network state
                _isOnline.value = true
            }
        }
    }
}

enum class ConflictResolution {
    LAST_WRITE_WINS,
    KEEP_LOCAL,
    KEEP_REMOTE,
    MANUAL
}
