package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId

private const val TAG = "HealthConnect"

val PERMISSIONS: Set<String> = setOf(
    HealthPermission.getReadPermission(StepsRecord::class),
    HealthPermission.getReadPermission(HeartRateRecord::class),
    HealthPermission.getReadPermission(ExerciseSessionRecord::class),
    HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
    HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class)
)

enum class HealthConnectStatus {
    Supported,
    NotInstalled,
    UpdateRequired
}

class HealthConnectManager(private val context: Context) {

    private val _heartRate = MutableStateFlow<Int?>(null)
    val heartRate: StateFlow<Int?> = _heartRate.asStateFlow()

    private val _todaySteps = MutableStateFlow<Int?>(null)
    val todaySteps: StateFlow<Int?> = _todaySteps.asStateFlow()

    private val _weeklySteps = MutableStateFlow<List<Pair<LocalDate, Int>>>(emptyList())
    val weeklySteps: StateFlow<List<Pair<LocalDate, Int>>> = _weeklySteps.asStateFlow()

    private val _status = MutableStateFlow<HealthConnectStatus>(HealthConnectStatus.Supported)
    val status: StateFlow<HealthConnectStatus> = _status.asStateFlow()

    private val _hasPermission = MutableStateFlow(false)
    val hasPermission: StateFlow<Boolean> = _hasPermission.asStateFlow()

    private val _todayCalories = MutableStateFlow<Int?>(null)
    val todayCalories: StateFlow<Int?> = _todayCalories.asStateFlow()

    private val _todayActiveMinutes = MutableStateFlow<Int?>(null)
    val todayActiveMinutes: StateFlow<Int?> = _todayActiveMinutes.asStateFlow()

    private var client: HealthConnectClient? = null

    init {
        Log.d(TAG, "HealthConnectManager init - PERMISSIONS size: ${PERMISSIONS.size}")
        Log.d(TAG, "HealthConnectManager init - PERMISSIONS: $PERMISSIONS")
        Log.d(TAG, "HealthConnectManager init - Steps permission string: '${HealthPermission.getReadPermission(StepsRecord::class)}'")
        Log.d(TAG, "HealthConnectManager init - HeartRate permission string: '${HealthPermission.getReadPermission(HeartRateRecord::class)}'")
        Log.d(TAG, "HealthConnectManager init - Exercise permission string: '${HealthPermission.getReadPermission(ExerciseSessionRecord::class)}'")
        Log.d(TAG, "HealthConnectManager init - TotalCalories permission string: '${HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class)}'")
        Log.d(TAG, "HealthConnectManager init - ActiveCalories permission string: '${HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class)}'")
        try {
            client = HealthConnectClient.getOrCreate(context)
            Log.d(TAG, "HealthConnectManager init - client created successfully")
        } catch (e: Exception) {
            Log.e(TAG, "HealthConnectManager init - client creation failed", e)
            _status.value = HealthConnectStatus.NotInstalled
        }
    }

    fun checkStatus(): HealthConnectStatus {
        return try {
            HealthConnectClient.getOrCreate(context)
            Log.d(TAG, "checkStatus - getOrCreate succeeded, returning Supported")
            HealthConnectStatus.Supported
        } catch (e: Exception) {
            Log.e(TAG, "checkStatus - getOrCreate failed", e)
            if (e.message?.contains("ACTION_SHOW_REDIRECT_BANNER") == true) {
                Log.d(TAG, "checkStatus - returning UpdateRequired")
                HealthConnectStatus.UpdateRequired
            } else {
                Log.d(TAG, "checkStatus - returning NotInstalled")
                HealthConnectStatus.NotInstalled
            }
        }
    }

    fun getInstallIntent(): Intent {
        return try {
            context.packageManager.getLaunchIntentForPackage("com.google.android.apps.healthdata")
        } catch (_: Exception) {
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("market://details?id=com.google.android.apps.healthdata")
            }
        } ?: Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("market://details?id=com.google.android.apps.healthdata")
        }
    }

    fun getHealthPermissions(): Set<String> = PERMISSIONS

    fun isHealthConnectAvailable(): Boolean {
        return client != null && _status.value == HealthConnectStatus.Supported
    }

    suspend fun hasAllPermissions(): Boolean {
        Log.d(TAG, "hasAllPermissions called - client is null: ${client == null}")
        return try {
            val granted = client?.permissionController?.getGrantedPermissions() ?: emptySet()
            Log.d(TAG, "getGrantedPermissions returned: $granted")
            val hasAll = granted.containsAll(PERMISSIONS)
            _hasPermission.value = hasAll
            Log.d(TAG, "hasAllPermissions: $hasAll, granted: $granted, permissions: $PERMISSIONS")
            hasAll
        } catch (e: Exception) {
            Log.e(TAG, "hasAllPermissions exception", e)
            false
        }
    }

    fun readHeartRate() {
        if (!isHealthConnectAvailable()) return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val hasPerm = hasAllPermissions()
                if (!hasPerm) {
                    Log.d(TAG, "READ_HEART_RATE permission not granted - skipping read")
                    _heartRate.value = null
                    return@launch
                }
                
                val now = Instant.now()
                val startTime = now.minusSeconds(24 * 60 * 60)
                val request = ReadRecordsRequest(
                    recordType = HeartRateRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(
                        startTime = startTime,
                        endTime = now
                    )
                )
                val response = client!!.readRecords<HeartRateRecord>(request)
                Log.d(TAG, "Heart rate records count: ${response.records.size}")
                
                var latestBpm: Int? = null
                var latestTime: Instant? = null
                
                response.records.forEach { record ->
                    record.samples.forEach { sample ->
                        if (latestTime == null || sample.time.isAfter(latestTime)) {
                            latestTime = sample.time
                            latestBpm = sample.beatsPerMinute.toInt()
                        }
                    }
                }
                
                _heartRate.value = latestBpm ?: 0
                Log.d(TAG, "Latest heart rate: ${latestBpm ?: 0} bpm")
            } catch (e: Exception) {
                Log.e(TAG, "readHeartRate error: ${e.message}")
                _heartRate.value = null
            }
        }
    }

    fun readSteps() {
        if (!isHealthConnectAvailable()) return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val hasPerm = hasAllPermissions()
                if (!hasPerm) {
                    Log.d(TAG, "READ_STEPS permission not granted - skipping read")
                    _todaySteps.value = null
                    return@launch
                }
                
                val now = Instant.now()
                val startOfDay = now.atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()
                
                val request = ReadRecordsRequest(
                    recordType = StepsRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(
                        startTime = startOfDay, 
                        endTime = now
                    )
                )
                val response = client!!.readRecords<StepsRecord>(request)
                Log.d(TAG, "Steps records count: ${response.records.size}")
                
                response.records.forEachIndexed { index, record ->
                    Log.d(TAG, "Steps record $index: ${record.count} steps")
                }
                
                val totalSteps = response.records.sumOf { it.count }.toInt()
                _todaySteps.value = totalSteps
                Log.d(TAG, "Total steps today: $totalSteps")
            } catch (e: Exception) {
                Log.e(TAG, "readSteps error: ${e.message}")
                _todaySteps.value = null
            }
        }
    }

    private fun estimateCaloriesFromSteps(steps: Int): Int {
        return (steps * 0.04).toInt()
    }

    private fun estimateActiveMinutesFromSteps(steps: Int): Int {
        val estimated = (steps / 100.0).toInt()
        return minOf(estimated, 180)
    }

    fun readAllMetrics() {
        readHeartRate()
        readSteps()
        readWeeklySteps()
        readCalories()
        readActiveMinutes()
    }

    fun readCalories() {
        if (!isHealthConnectAvailable()) return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val hasPerm = hasAllPermissions()
                if (!hasPerm) {
                    Log.d(TAG, "READ_ACTIVE_CALORIES_BURNED permission not granted - skipping read")
                    _todayCalories.value = null
                    return@launch
                }

                val now = Instant.now()
                val startOfDay = now.atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()

                val request = ReadRecordsRequest(
                    recordType = ActiveCaloriesBurnedRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(
                        startTime = startOfDay,
                        endTime = now
                    )
                )
                val response = client!!.readRecords<ActiveCaloriesBurnedRecord>(request)
                Log.d(TAG, "ActiveCaloriesBurned records count: ${response.records.size}")

                if (response.records.isEmpty()) {
                    val stepsToday = _todaySteps.value
                    if (stepsToday != null && stepsToday > 0) {
                        val estimated = estimateCaloriesFromSteps(stepsToday)
                        _todayCalories.value = estimated
                        Log.d(TAG, "No active calorie records — estimated from steps: $estimated kcal")
                    } else {
                        Log.d(TAG, "No active calorie records available from Health Connect sources")
                        _todayCalories.value = null
                    }
                } else {
                    val totalKcal = response.records.sumOf { it.energy.inKilocalories }
                    _todayCalories.value = totalKcal.toInt()
                    Log.d(TAG, "Total calories today: ${totalKcal.toInt()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "readCalories error: ${e.message}")
                _todayCalories.value = null
            }
        }
    }

    fun readActiveMinutes() {
        if (!isHealthConnectAvailable()) return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val hasPerm = hasAllPermissions()
                if (!hasPerm) {
                    Log.d(TAG, "READ_EXERCISE permission not granted - skipping active minutes read")
                    _todayActiveMinutes.value = null
                    return@launch
                }

                val now = Instant.now()
                val startOfDay = now.atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()

                val request = ReadRecordsRequest(
                    recordType = ExerciseSessionRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(
                        startTime = startOfDay,
                        endTime = now
                    )
                )
                val response = client!!.readRecords<ExerciseSessionRecord>(request)
                Log.d(TAG, "ExerciseSession records count: ${response.records.size}")

                if (response.records.isEmpty()) {
                    val stepsToday = _todaySteps.value
                    if (stepsToday != null && stepsToday > 0) {
                        val estimated = estimateActiveMinutesFromSteps(stepsToday)
                        _todayActiveMinutes.value = estimated
                        Log.d(TAG, "No active minutes records — estimated from steps: $estimated minutes")
                    } else {
                        Log.d(TAG, "No active minutes records available from Health Connect sources")
                        _todayActiveMinutes.value = null
                    }
                } else {
                    var totalMinutes = 0L
                    response.records.forEach { record ->
                        val durationMinutes = Duration.between(record.startTime, record.endTime).toMinutes()
                        totalMinutes += durationMinutes
                        Log.d(TAG, "Exercise session duration: ${durationMinutes} minutes")
                    }
                    _todayActiveMinutes.value = totalMinutes.toInt()
                    Log.d(TAG, "Total active minutes today: ${totalMinutes.toInt()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "readActiveMinutes error: ${e.message}")
                _todayActiveMinutes.value = null
            }
        }
    }

    fun readWeeklySteps() {
        if (!isHealthConnectAvailable()) return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val now = Instant.now()
                val zoneId = ZoneId.systemDefault()
                val today = now.atZone(zoneId).toLocalDate()
                
                val weeklyData = mutableListOf<Pair<LocalDate, Int>>()
                    
                for (i in 6 downTo 0) {
                    val date = today.minusDays(i.toLong())
                    val startOfDay = date.atStartOfDay(zoneId).toInstant()
                    val startOfNextDay = date.plusDays(1).atStartOfDay(zoneId).toInstant()

                    val request = ReadRecordsRequest(
                        recordType = StepsRecord::class,
                        timeRangeFilter = TimeRangeFilter.between(startTime = startOfDay, endTime = startOfNextDay)
                    )
                    val response = client!!.readRecords<StepsRecord>(request)
                    val steps = response.records.sumOf { it.count }.toInt()
                    weeklyData.add(date to steps)
                }
                    
                _weeklySteps.value = weeklyData
            } catch (_: Exception) {
                _weeklySteps.value = emptyList()
            }
        }
    }
}