package com.example.myapplication.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.*
import com.example.myapplication.data.LocalAuthManager
import com.example.myapplication.data.MockData
import com.example.myapplication.data.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

val Context.normsDataStore: androidx.datastore.core.DataStore<Preferences> by preferencesDataStore(name = "norms_store")

class LocalNormRepository(
    private val context: Context,
    private val localAuthManager: LocalAuthManager
) : NormRepository {

    private val normsKey = stringPreferencesKey("norms")
    private val testsKey = stringSetPreferencesKey("tests")

    private val _norms = MutableStateFlow<List<Norm>>(emptyList())
    val norms: StateFlow<List<Norm>> = _norms.asStateFlow()

    private val _tests = MutableStateFlow<List<PhysicalTest>>(emptyList())
    val tests: StateFlow<List<PhysicalTest>> = _tests.asStateFlow()

    init {
        kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            loadFromStore()
            if (_norms.value.isEmpty()) {
                seedMockData()
            }
        }
    }

    private suspend fun loadFromStore() {
        val prefs = context.normsDataStore.data.first()
        _norms.value = parseNorms(prefs[normsKey])
        _tests.value = parseTests(prefs[testsKey])
    }

    private suspend fun saveNormsToStore() {
        context.normsDataStore.edit { prefs ->
            prefs[normsKey] = JSONArray(_norms.value.map { JSONObject(it.toMap()).toString() }).toString()
        }
    }

    private suspend fun saveTestsToStore() {
        val set = _tests.value.map { JSONObject(it.toMap()).toString() }.toSet()
        context.normsDataStore.edit { prefs ->
            prefs[testsKey] = set
        }
    }

    private fun parseNorms(json: String?): List<Norm> {
        if (json == null) return emptyList()
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                val standardsArray = obj.optJSONArray("standards") ?: JSONArray()
                val standards = (0 until standardsArray.length()).map { j ->
                    val s = standardsArray.getJSONObject(j)
                    com.example.myapplication.data.models.StandardEntry(
                        age = s.optInt("age", 0),
                        excellent = s.optString("excellent", ""),
                        good = s.optString("good", ""),
                        satisfactory = s.optString("satisfactory", ""),
                        pass = s.optString("pass", "")
                    )
                }
                Norm(
                    id = obj.optString("id", ""),
                    name = obj.optString("name", ""),
                    category = com.example.myapplication.data.models.NormCategory.valueOf(obj.optString("category", com.example.myapplication.data.models.NormCategory.STRENGTH.name)),
                    unit = obj.optString("unit", ""),
                    gender = Gender.valueOf(obj.optString("gender", Gender.MALE.name)),
                    minAge = obj.optInt("minAge", 0),
                    maxAge = obj.optInt("maxAge", 100),
                    standards = standards
                )
            }
        } catch (e: Exception) { emptyList() }
    }

    private fun parseTests(set: Set<String>?): List<PhysicalTest> {
        if (set == null) return emptyList()
        return try {
            set.mapNotNull { str ->
                val obj = JSONObject(str)
                PhysicalTest(
                    id = obj.optString("id", ""),
                    userId = obj.optString("userId", ""),
                    normId = obj.optString("normId", ""),
                    value = obj.optString("value", ""),
                    unit = obj.optString("unit", ""),
                    date = obj.optLong("date", 0L),
                    score = obj.optString("score", "")
                )
            }
        } catch (e: Exception) { emptyList() }
    }

    override suspend fun getNorms(): Result<List<Norm>> = try {
        Result.success(_norms.value)
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun saveTestResult(test: PhysicalTest): Result<Unit> = try {
        val current = _tests.value.toMutableList()
        val newTest = if (test.id.isBlank()) test.copy(id = System.currentTimeMillis().toString()) else test
        current.add(newTest)
        _tests.value = current
        saveTestsToStore()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    override suspend fun getTestResults(userId: String): Result<List<PhysicalTest>> = try {
        Result.success(_tests.value.filter { it.userId == userId })
    } catch (e: Exception) {
        Result.failure(DataError.Unknown(e))
    }

    private suspend fun seedMockData() {
        val mockNorms = MockData.norms
        val mockTests = MockData.physicalTests
        _norms.value = mockNorms
        _tests.value = mockTests
        saveNormsToStore()
        saveTestsToStore()
    }
}
