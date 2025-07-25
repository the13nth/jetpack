package com.example.rotiie.launcher.ui.generator

import com.example.rotiie.launcher.data.models.AppInfo
import com.example.rotiie.launcher.data.models.RoutineType
import com.example.rotiie.launcher.data.mock.ActionPrediction
import com.example.rotiie.launcher.data.mock.MockDataProvider
import org.junit.Assert.*
import org.junit.Test

/**
 * Test suite for contextual UI generation functionality
 */
class UIGeneratorTest {

    @Test
    fun `generateLauncherUI creates appropriate UI for morning routine`() {
        // Given
        val predictions = MockDataProvider.getMockActionPredictions(RoutineType.MORNING)
        val apps = MockDataProvider.mockApps
        val routineType = RoutineType.MORNING

        // When
        val uiState = UIGenerator.generateLauncherUI(predictions, routineType, apps)

        // Then
        assertNotNull("UI state should not be null", uiState)
        assertEquals("Should have 3 grid columns for morning", 3, uiState.layout.gridColumns)
        assertTrue("Should show prediction cards", uiState.layout.showPredictionCards)
        assertTrue("Should show contextual widgets", uiState.layout.showContextualWidgets)
        assertEquals("Theme should be Morning Fresh", "Morning Fresh", uiState.theme.name)
        assertTrue("Should have primary actions", uiState.primaryActions.isNotEmpty())
    }

    @Test
    fun `generateLauncherUI creates appropriate UI for afternoon routine`() {
        // Given
        val predictions = MockDataProvider.getMockActionPredictions(RoutineType.AFTERNOON)
        val apps = MockDataProvider.mockApps
        val routineType = RoutineType.AFTERNOON

        // When
        val uiState = UIGenerator.generateLauncherUI(predictions, routineType, apps)

        // Then
        assertNotNull("UI state should not be null", uiState)
        assertEquals("Should have 4 grid columns for afternoon", 4, uiState.layout.gridColumns)
        assertEquals("Theme should be Productive Focus", "Productive Focus", uiState.theme.name)
        assertTrue("Should prioritize work apps", 
            uiState.appGrid.apps.take(5).any { it.category == "Work" || it.category == "Productivity" })
    }

    @Test
    fun `generateUIUpdate preserves stable elements and updates changed ones`() {
        // Given
        val initialPredictions = MockDataProvider.getMockActionPredictions(RoutineType.MORNING)
        val apps = MockDataProvider.mockApps
        val routineType = RoutineType.MORNING
        val currentUI = UIGenerator.generateLauncherUI(initialPredictions, routineType, apps)

        // Create new predictions with different confidence levels
        val newPredictions = initialPredictions.mapIndexed { index, prediction ->
            if (index == 0) {
                prediction.copy(confidence = prediction.confidence + 0.2f)
            } else {
                prediction
            }
        }

        // When
        val updatedUI = UIGenerator.generateUIUpdate(currentUI, newPredictions, routineType, apps)

        // Then
        assertNotNull("Updated UI should not be null", updatedUI)
        assertEquals("Layout should remain the same", currentUI.layout.gridColumns, updatedUI.layout.gridColumns)
        assertEquals("Theme should remain the same", currentUI.theme.name, updatedUI.theme.name)
        // Primary actions should be updated due to confidence change
        assertNotEquals("Primary actions should be updated", 
            currentUI.primaryActions.first().confidence, 
            updatedUI.primaryActions.first().confidence)
    }

    @Test
    fun `prediction analysis correctly identifies high confidence scenarios`() {
        // Given
        val highConfidencePredictions = listOf(
            ActionPrediction("Test Action 1", 0.9f, emptyList(), "High confidence", 1),
            ActionPrediction("Test Action 2", 0.85f, emptyList(), "High confidence", 2),
            ActionPrediction("Test Action 3", 0.8f, emptyList(), "High confidence", 3)
        )
        val apps = MockDataProvider.mockApps
        val routineType = RoutineType.MORNING

        // When
        val uiState = UIGenerator.generateLauncherUI(highConfidencePredictions, routineType, apps)

        // Then
        assertTrue("Should have high confidence primary actions", 
            uiState.primaryActions.all { it.confidence >= 0.7f })
        assertTrue("Should show prediction cards for high confidence", 
            uiState.layout.showPredictionCards)
    }

    @Test
    fun `theme configuration adapts to routine type correctly`() {
        val routineTypes = listOf(
            RoutineType.MORNING,
            RoutineType.AFTERNOON,
            RoutineType.EVENING,
            RoutineType.WEEKEND,
            RoutineType.CUSTOM
        )

        routineTypes.forEach { routineType ->
            // Given
            val predictions = MockDataProvider.getMockActionPredictions(routineType)
            val apps = MockDataProvider.mockApps

            // When
            val uiState = UIGenerator.generateLauncherUI(predictions, routineType, apps)

            // Then
            assertNotNull("Theme should not be null for $routineType", uiState.theme)
            assertTrue("Theme name should be appropriate for $routineType", 
                uiState.theme.name.isNotEmpty())
            assertTrue("Primary color hue should be valid", 
                uiState.theme.primaryColorHue in 0f..360f)
            assertTrue("Saturation should be valid", 
                uiState.theme.saturation in 0f..1f)
            assertTrue("Brightness should be valid", 
                uiState.theme.brightness in 0f..1f)
        }
    }

    @Test
    fun `app grid prioritization works correctly for different routines`() {
        val apps = MockDataProvider.mockApps
        val predictions = MockDataProvider.getMockActionPredictions(RoutineType.MORNING)

        // Test morning routine prioritization
        val morningUI = UIGenerator.generateLauncherUI(predictions, RoutineType.MORNING, apps)
        val morningTopApps = morningUI.appGrid.apps.take(5)
        assertTrue("Morning should prioritize health/productivity apps",
            morningTopApps.any { it.category in listOf("Health", "Productivity", "Email") })

        // Test afternoon routine prioritization
        val afternoonPredictions = MockDataProvider.getMockActionPredictions(RoutineType.AFTERNOON)
        val afternoonUI = UIGenerator.generateLauncherUI(afternoonPredictions, RoutineType.AFTERNOON, apps)
        val afternoonTopApps = afternoonUI.appGrid.apps.take(5)
        assertTrue("Afternoon should prioritize work/productivity apps",
            afternoonTopApps.any { it.category in listOf("Work", "Productivity", "Professional") })
    }

    @Test
    fun `widget configuration adapts to routine and predictions`() {
        val routineTypes = listOf(RoutineType.MORNING, RoutineType.AFTERNOON, RoutineType.EVENING)

        routineTypes.forEach { routineType ->
            // Given
            val predictions = MockDataProvider.getMockActionPredictions(routineType)
            val apps = MockDataProvider.mockApps

            // When
            val uiState = UIGenerator.generateLauncherUI(predictions, routineType, apps)

            // Then
            assertTrue("Should have widgets for $routineType", uiState.widgets.isNotEmpty())
            assertTrue("All widgets should have valid priorities", 
                uiState.widgets.all { it.priority > 0 })
            assertTrue("Should have at least one visible widget", 
                uiState.widgets.any { it.isVisible })
        }
    }

    @Test
    fun `layout adapts to prediction confidence levels`() {
        val apps = MockDataProvider.mockApps

        // Test with high confidence predictions
        val highConfidencePredictions = listOf(
            ActionPrediction("High Confidence Action", 0.95f, emptyList(), "Very confident", 1)
        )
        val highConfidenceUI = UIGenerator.generateLauncherUI(
            highConfidencePredictions, RoutineType.MORNING, apps)

        // Test with low confidence predictions
        val lowConfidencePredictions = listOf(
            ActionPrediction("Low Confidence Action", 0.3f, emptyList(), "Not confident", 1)
        )
        val lowConfidenceUI = UIGenerator.generateLauncherUI(
            lowConfidencePredictions, RoutineType.MORNING, apps)

        // Then
        assertTrue("High confidence should show predictions", 
            highConfidenceUI.layout.showPredictionCards)
        assertFalse("Low confidence should not show predictions", 
            lowConfidenceUI.layout.showPredictionCards)
    }
}