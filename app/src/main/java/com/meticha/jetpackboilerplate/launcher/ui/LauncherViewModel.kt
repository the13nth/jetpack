package com.meticha.jetpackboilerplate.launcher.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meticha.jetpackboilerplate.launcher.data.models.AppInfo
import com.meticha.jetpackboilerplate.launcher.data.models.Routine
import com.meticha.jetpackboilerplate.launcher.data.models.RoutineType
import com.meticha.jetpackboilerplate.launcher.data.models.UserContext
import com.meticha.jetpackboilerplate.launcher.data.mock.ActionPrediction
import com.meticha.jetpackboilerplate.launcher.data.mock.MockDataProvider
import com.meticha.jetpackboilerplate.launcher.data.repository.PredictionRepository
import com.meticha.jetpackboilerplate.launcher.data.repository.RoutineRepository
import com.meticha.jetpackboilerplate.launcher.ui.generator.LauncherUIState
import com.meticha.jetpackboilerplate.launcher.ui.generator.UIGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class LauncherViewModel @Inject constructor(
    private val routineRepository: RoutineRepository,
    private val predictionRepository: PredictionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LauncherUiState())
    val uiState: StateFlow<LauncherUiState> = _uiState.asStateFlow()

    init {
        observeRoutines()
        loadCurrentRoutine()
        loadAppsAndPredictions()
        startPredictionUpdates()
    }
    
    /**
     * Start periodic prediction updates for real-time UI adaptation
     */
    private fun startPredictionUpdates() {
        viewModelScope.launch {
            // Observe prediction updates from repository
            predictionRepository.observePredictionUpdates().collect { updatedPredictions ->
                val currentRoutine = _uiState.value.currentRoutine
                if (currentRoutine != null && updatedPredictions.isNotEmpty()) {
                    updateUIWithNewPredictions(updatedPredictions, currentRoutine.type)
                }
            }
        }
    }
    
    /**
     * Update UI with new predictions while maintaining smooth transitions
     */
    private suspend fun updateUIWithNewPredictions(
        newPredictions: List<ActionPrediction>,
        routineType: RoutineType
    ) {
        try {
            val currentUI = _uiState.value.generatedUI
            if (currentUI != null && shouldUpdateUI(newPredictions, _uiState.value.predictions)) {
                val updatedUI = UIGenerator.generateUIUpdate(
                    currentUI = currentUI,
                    newPredictions = newPredictions,
                    routineType = routineType,
                    availableApps = _uiState.value.availableApps
                )
                
                _uiState.value = _uiState.value.copy(
                    predictions = newPredictions,
                    generatedUI = updatedUI
                )
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                error = "Failed to update UI: ${e.message}"
            )
        }
    }
    
    /**
     * Determine if UI should be updated based on prediction changes
     */
    private fun shouldUpdateUI(
        newPredictions: List<ActionPrediction>,
        currentPredictions: List<ActionPrediction>
    ): Boolean {
        if (newPredictions.size != currentPredictions.size) return true
        
        // Check for significant confidence changes or new actions
        return newPredictions.zip(currentPredictions).any { (new, current) ->
            new.action != current.action || 
            kotlin.math.abs(new.confidence - current.confidence) > 0.15f
        }
    }

    private fun observeRoutines() {
        viewModelScope.launch {
            combine(
                routineRepository.getActiveRoutines(),
                routineRepository.getAllRoutines()
            ) { activeRoutines, allRoutines ->
                _uiState.value = _uiState.value.copy(
                    routines = allRoutines,
                    activeRoutines = activeRoutines,
                    isLoading = false
                )
            }
        }
    }

    private fun loadCurrentRoutine() {
        viewModelScope.launch {
            try {
                val currentRoutine = routineRepository.getCurrentRoutine()
                _uiState.value = _uiState.value.copy(
                    currentRoutine = currentRoutine,
                    isLoading = false
                )
                // Load predictions when routine changes
                loadPredictionsForRoutine(currentRoutine?.type ?: RoutineType.MORNING)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }

    private fun loadAppsAndPredictions() {
        viewModelScope.launch {
            try {
                // Load mock apps data
                val apps = MockDataProvider.mockApps
                _uiState.value = _uiState.value.copy(
                    availableApps = apps
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }

    private fun loadPredictionsForRoutine(routineType: RoutineType) {
        viewModelScope.launch {
            try {
                val userContext = createUserContext(routineType)
                val predictions = predictionRepository.generateActionPredictions(userContext)
                
                // Generate contextual UI based on predictions with real-time updates
                val currentUI = _uiState.value.generatedUI
                val generatedUI = if (currentUI != null) {
                    // Use incremental update for better performance
                    UIGenerator.generateUIUpdate(
                        currentUI = currentUI,
                        newPredictions = predictions,
                        routineType = routineType,
                        availableApps = _uiState.value.availableApps
                    )
                } else {
                    // Generate fresh UI state
                    UIGenerator.generateLauncherUI(
                        predictions = predictions,
                        routineType = routineType,
                        availableApps = _uiState.value.availableApps
                    )
                }
                
                _uiState.value = _uiState.value.copy(
                    predictions = predictions,
                    generatedUI = generatedUI
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }

    private fun createUserContext(routineType: RoutineType): UserContext {
        val currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
        return UserContext(
            currentRoutine = routineType,
            timeOfDay = currentTime,
            recentActions = emptyList(), // Would be populated from user action repository
            activeIntentions = emptyList() // Would be populated from intention repository
        )
    }

    fun switchRoutine(routineType: RoutineType) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                // Find the routine with the specified type
                val targetRoutine = _uiState.value.routines.find { it.type == routineType }
                if (targetRoutine != null) {
                    // Update the routine to be active
                    routineRepository.updateRoutine(targetRoutine.copy(isActive = true))
                    // Deactivate other routines
                    _uiState.value.routines.filter { it.type != routineType }.forEach { routine ->
                        routineRepository.updateRoutine(routine.copy(isActive = false))
                    }
                    _uiState.value = _uiState.value.copy(
                        currentRoutine = targetRoutine,
                        isLoading = false
                    )
                    // Load new predictions for the switched routine
                    loadPredictionsForRoutine(routineType)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }

    fun launchApp(appInfo: AppInfo) {
        viewModelScope.launch {
            try {
                // In a real implementation, this would launch the app using Android's PackageManager
                // For now, we'll simulate app launch and update usage statistics
                val updatedApps = _uiState.value.availableApps.map { app ->
                    if (app.packageName == appInfo.packageName) {
                        app.copy(
                            usageCount = app.usageCount + 1,
                            lastUsed = System.currentTimeMillis()
                        )
                    } else {
                        app
                    }
                }
                
                _uiState.value = _uiState.value.copy(
                    availableApps = updatedApps
                )
                
                // TODO: In real implementation, launch the app:
                // val intent = packageManager.getLaunchIntentForPackage(appInfo.packageName)
                // context.startActivity(intent)
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to launch ${appInfo.appName}: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class LauncherUiState(
    val routines: List<Routine> = emptyList(),
    val activeRoutines: List<Routine> = emptyList(),
    val currentRoutine: Routine? = null,
    val availableApps: List<AppInfo> = emptyList(),
    val predictions: List<ActionPrediction> = emptyList(),
    val generatedUI: LauncherUIState? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)