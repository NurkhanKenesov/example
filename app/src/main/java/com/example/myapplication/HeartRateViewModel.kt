package com.example.myapplication

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HealthViewModel(private val healthConnectManager: HealthConnectManager) : ViewModel() {

    private val _todaySteps = MutableStateFlow<Int?>(null)
    val todaySteps: StateFlow<Int?> = _todaySteps.asStateFlow()

    private val _weeklySteps = MutableStateFlow<List<Pair<java.time.LocalDate, Int>>>(emptyList())
    val weeklySteps: StateFlow<List<Pair<java.time.LocalDate, Int>>> = _weeklySteps.asStateFlow()

    private val _heartRate = MutableStateFlow<Int?>(null)
    val heartRate: StateFlow<Int?> = _heartRate.asStateFlow()

    private val _todayCalories = MutableStateFlow<Int?>(null)
    val todayCalories: StateFlow<Int?> = _todayCalories.asStateFlow()

    private val _todayActiveMinutes = MutableStateFlow<Int?>(null)
    val todayActiveMinutes: StateFlow<Int?> = _todayActiveMinutes.asStateFlow()

    private val _hasPermission = MutableStateFlow(false)
    val hasPermission: StateFlow<Boolean> = _hasPermission.asStateFlow()

    private val _healthConnectStatus = MutableStateFlow<HealthConnectStatus>(HealthConnectStatus.Supported)
    val healthConnectStatus: StateFlow<HealthConnectStatus> = _healthConnectStatus.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        refreshAllMetrics()
    }

    fun refreshAllMetrics() {
        viewModelScope.launch {
            val status = healthConnectManager.checkStatus()
            _healthConnectStatus.value = status

            if (status == HealthConnectStatus.Supported && healthConnectManager.isHealthConnectAvailable()) {
                _isLoading.value = true
                val hasPerm = healthConnectManager.hasAllPermissions()
                _hasPermission.value = hasPerm
                
                if (hasPerm) {
                    healthConnectManager.readAllMetrics()
                    kotlinx.coroutines.delay(500)
                    _heartRate.value = healthConnectManager.heartRate.value
                    _todaySteps.value = healthConnectManager.todaySteps.value
                    _weeklySteps.value = healthConnectManager.weeklySteps.value
                    _todayCalories.value = healthConnectManager.todayCalories.value
                    _todayActiveMinutes.value = healthConnectManager.todayActiveMinutes.value
                }
            }
            _isLoading.value = false
        }
    }

    fun loadHealthData() {
        viewModelScope.launch {
            refreshAllMetrics()
        }
    }

    fun getInstallIntent() = healthConnectManager.getInstallIntent()
    fun getHealthPermissions(): Set<String> = healthConnectManager.getHealthPermissions()
    fun isHealthConnectAvailable() = healthConnectManager.isHealthConnectAvailable()
}

class HealthViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val manager = HealthConnectManager(context)
        @Suppress("UNCHECKED_CAST")
        return HealthViewModel(manager) as T
    }
}