package com.example.rotiie.launcher.ui.generator

import com.example.rotiie.launcher.data.models.AppInfo
import com.example.rotiie.launcher.data.models.RoutineType
import com.example.rotiie.launcher.data.mock.ActionPrediction
import org.junit.Test
import org.junit.Assert.*

/**
 * Test suite for contextual UI generation functionality
 * Verifies that UI adapts correctly based on predictions and routine context
 */
class ContextualUIGeneratorTest {

    private val mockApps = listOf(
        AppInfo("com.headspace.android", "Headspace", usageCount = 56, category = "Health"),
        AppInfo("com.slack", "Slack", usageCount = 145, category = "Work"),
        AppInfo("com.netflix.mediaclient", "Netflix", usageCount = 189, category = "Entertainment"),
        AppInfo("com.spotify.music", "Spotify", usageCount = 267, category = "Music")
    )

    @Test
    fun `test UI generation adapts to morning routine`() {
        val morningPredictions = listOf(
            ActionPrediction(
                action = "Start meditation session",
                confidence = 0.89f,
                associatedApps = listOf(mockApps[0]),
                reasoning = "User typically starts morning with mindfulness",
                priority = 1
            ),
            ActionPrediction(
                action = "Check work messages",
                confidence = 0.75f,
                associatedApps = listOf(mockApps[1]),
                reasoning = "Morning communication check pattern",
                priority = 2
            )
        )

        val uiState = UIGenerator.generateLauncherUI(
            predictions = morningPredictions,
            routineType = RoutineType.MORNING,
            availableApps = mockApps
        )

        // Verify morning-specific adaptations
        assertEquals("Morning Fresh", uiState.theme.name)
        assertEquals(200f, uiState.theme.primaryColorHue, 0.1f) // Blue tones for morning
        assertEquals(3, uiState.layout.gridColumns) // Focused morning layout
        assertTrue(uiState.layout.showPredictionCards)
        assertTrue(uiState.layout.showContextualWidgets)
        
        // Verify primary actions are correctly generated
        assertEquals(2, uiState.primaryActions.size)
        assertEquals("Start meditation session", uiState.primaryActions[0].action)
        assertEquals(0.89f, uiState.primaryActions[0].confidence, 0.01f)
        
        // Verify widgets are appropriate for morning routine
        assertTrue(uiState.widgets.any { it.type == WidgetType.TIME })
        assertTrue(uiState.widgets.any { it.type == WidgetType.WELLNESS })
    }

    @Test
    fun `test UI generation adapts to afternoon routine`() {
        val afternoonPredictions = listOf(
            ActionPrediction(
                action = "Join team meeting",
                confidence = 0.94f,
                associatedApps = listOf(mockApps[1]),
                reasoning = "Scheduled meeting time",
                priority = 1
            ),
            ActionPrediction(
                action = "Work on documentation",
                confidence = 0.82f,
                associatedApps = listOf(mockApps[1]),
                reasoning = "Deep work session follows meetings",
                priority = 2
            )
        )

        val uiState = UIGenerator.generateLauncherUI(
            predictions = afternoonPredictions,
            routineType = RoutineType.AFTERNOON,
            availableApps = mockApps
        )

        // Verify afternoon-specific adaptations
        assertEquals("Productive Focus", uiState.theme.name)
        assertEquals(260f, uiState.theme.primaryColorHue, 0.1f) // Purple tones for productivity
        assertEquals(4, uiState.layout.gridColumns) // Productive afternoon layout
        
        // Verify high confidence predictions are prioritized
        assertEquals(2, uiState.primaryActions.size)
        assertEquals("Join team meeting", uiState.primaryActions[0].action)
        assertEquals(0.94f, uiState.primaryActions[0].confidence, 0.01f)
        
        // Verify productivity widgets are present
        assertTrue(uiState.widgets.any { it.type == WidgetType.PRODUCTIVITY })
    }

    @Test
    fun `test UI generation adapts to evening routine`() {
        val eveningPredictions = listOf(
            ActionPrediction(
                action = "Watch entertainment content",
                confidence = 0.85f,
                associatedApps = listOf(mockApps[2]),
                reasoning = "Relaxation time pattern",
                priority = 1
            ),
            ActionPrediction(
                action = "Listen to music",
                confidence = 0.72f,
                associatedApps = listOf(mockApps[3]),
                reasoning = "Evening wind-down activity",
                priority = 2
            )
        )

        val uiState = UIGenerator.generateLauncherUI(
            predictions = eveningPredictions,
            routineType = RoutineType.EVENING,
            availableApps = mockApps
        )

        // Verify evening-specific adaptations
        assertEquals("Evening Calm", uiState.theme.name)
        assertEquals(30f, uiState.theme.primaryColorHue, 0.1f) // Orange/amber tones for evening
        assertEquals(3, uiState.layout.gridColumns) // Relaxed evening layout
        
        // Verify relaxation-focused predictions
        assertEquals(2, uiState.primaryActions.size)
        assertEquals("Watch entertainment content", uiState.primaryActions[0].action)
        
        // Verify evening widgets
        assertTrue(uiState.widgets.any { it.type == WidgetType.WELLNESS })
    }

    @Test
    fun `test UI generation adapts to weekend routine`() {
        val weekendPredictions = listOf(
            ActionPrediction(
                action = "Enjoy leisure activities",
                confidence = 0.78f,
                associatedApps = listOf(mockApps[2], mockApps[3]),
                reasoning = "Weekend leisure pattern",
                priority = 1
            )
        )

        val uiState = UIGenerator.generateLauncherUI(
            predictions = weekendPredictions,
            routineType = RoutineType.WEEKEND,
            availableApps = mockApps
        )

        // Verify weekend-specific adaptations
        assertEquals("Weekend Leisure", uiState.theme.name)
        assertEquals(120f, uiState.theme.primaryColorHue, 0.1f) // Green tones for weekend
        assertEquals(4, uiState.layout.gridColumns) // Diverse weekend layout
        
        // Verify leisure-focused predictions
        assertEquals(1, uiState.primaryActions.size)
        assertEquals("Enjoy leisure activities", uiState.primaryActions[0].action)
    }

    @Test
    fun `test UI updates intelligently with new predictions`() {
        val initialPredictions = listOf(
            ActionPrediction(
                action = "Initial action",
                confidence = 0.70f,
                associatedApps = listOf(mockApps[0]),
                reasoning = "Initial reasoning",
                priority = 1
            )
        )

        val initialUI = UIGenerator.generateLauncherUI(
            predictions = initialPredictions,
            routineType = RoutineType.MORNING,
            availableApps = mockApps
        )

        val newPredictions = listOf(
            ActionPrediction(
                action = "Updated action",
                confidence = 0.85f,
                associatedApps = listOf(mockApps[1]),
                reasoning = "Updated reasoning",
                priority = 1
            )
        )

        val updatedUI = UIGenerator.generateUIUpdate(
            currentUI = initialUI,
            newPredictions = newPredictions,
            routineType = RoutineType.MORNING,
            availableApps = mockApps
        )

        // Verify UI was updated with new predictions
        assertEquals("Updated action", updatedUI.primaryActions[0].action)
        assertEquals(0.85f, updatedUI.primaryActions[0].confidence, 0.01f)
        
        // Verify theme and layout remain consistent for same routine
        assertEquals(initialUI.theme.name, updatedUI.theme.name)
        assertEquals(initialUI.layout.gridColumns, updatedUI.layout.gridColumns)
    }

    @Test
    fun `test prediction analysis calculates focus level correctly`() {
        // High confidence predictions should result in high focus
        val highConfidencePredictions = listOf(
            ActionPrediction("Action 1", 0.95f, listOf(mockApps[0]), "High confidence", 1),
            ActionPrediction("Action 2", 0.90f, listOf(mockApps[1]), "High confidence", 2)
        )

        val highFocusUI = UIGenerator.generateLauncherUI(
            predictions = highConfidencePredictions,
            routineType = RoutineType.MORNING,
            availableApps = mockApps
        )

        // Should show fewer widgets for high focus
        val visibleWidgets = highFocusUI.widgets.filter { it.isVisible }
        assertTrue("High focus should show fewer widgets", visibleWidgets.size <= 2)

        // Low confidence predictions should result in low focus
        val lowConfidencePredictions = listOf(
            ActionPrediction("Action 1", 0.40f, listOf(mockApps[0]), "Low confidence", 1),
            ActionPrediction("Action 2", 0.35f, listOf(mockApps[1]), "Low confidence", 2)
        )

        val lowFocusUI = UIGenerator.generateLauncherUI(
            predictions = lowConfidencePredictions,
            routineType = RoutineType.MORNING,
            availableApps = mockApps
        )

        // Should show more widgets for low focus
        val lowFocusVisibleWidgets = lowFocusUI.widgets.filter { it.isVisible }
        assertTrue("Low focus should show more widgets", lowFocusVisibleWidgets.size >= visibleWidgets.size)
    }

    @Test
    fun `test app grid prioritization based on predictions`() {
        val predictions = listOf(
            ActionPrediction(
                action = "Use Slack",
                confidence = 0.85f,
                associatedApps = listOf(mockApps[1]), // Slack
                reasoning = "Work communication",
                priority = 1
            )
        )

        val uiState = UIGenerator.generateLauncherUI(
            predictions = predictions,
            routineType = RoutineType.AFTERNOON,
            availableApps = mockApps
        )

        // Verify predicted apps are prioritized in the grid
        val slackApp = uiState.appGrid.apps.find { it.packageName == "com.slack" }
        assertNotNull("Slack should be in the app grid", slackApp)
        
        // Predicted apps should appear earlier in the list
        val slackIndex = uiState.appGrid.apps.indexOf(slackApp)
        assertTrue("Predicted app should be prioritized", slackIndex < mockApps.size / 2)
        
        // Verify grid configuration
        assertTrue("Should highlight predicted apps", uiState.appGrid.highlightPredicted)
        assertTrue("Should animate changes", uiState.appGrid.animateChanges)
    }

    @Test
    fun `test theme configuration adapts to routine and predictions`() {
        val highConfidencePredictions = listOf(
            ActionPrediction("Focus action", 0.95f, listOf(mockApps[0]), "High focus", 1)
        )

        val uiState = UIGenerator.generateLauncherUI(
            predictions = highConfidencePredictions,
            routineType = RoutineType.AFTERNOON,
            availableApps = mockApps
        )

        // Verify theme adapts to high confidence (high focus)
        assertTrue("High focus should increase card elevation", uiState.theme.cardElevation >= 6f)
        assertTrue("High focus should increase corner radius", uiState.theme.cornerRadius >= 12f)
        
        // Verify saturation is adjusted for focus
        assertTrue("Saturation should be adjusted for focus", uiState.theme.saturation > 0f)
    }

    @Test
    fun `test adaptive spacing calculation`() {
        val manyPredictions = (1..6).map { i ->
            ActionPrediction("Action $i", 0.6f, listOf(mockApps[0]), "Many predictions", i)
        }

        val uiState = UIGenerator.generateLauncherUI(
            predictions = manyPredictions,
            routineType = RoutineType.MORNING,
            availableApps = mockApps
        )

        // With many predictions, spacing should be tighter
        assertTrue("Many predictions should result in tighter spacing", uiState.layout.adaptiveSpacing < 16f)

        val fewPredictions = listOf(
            ActionPrediction("Single action", 0.8f, listOf(mockApps[0]), "Few predictions", 1)
        )

        val sparseUI = UIGenerator.generateLauncherUI(
            predictions = fewPredictions,
            routineType = RoutineType.MORNING,
            availableApps = mockApps
        )

        // With few predictions, spacing should be looser
        assertTrue("Few predictions should result in looser spacing", sparseUI.layout.adaptiveSpacing >= uiState.layout.adaptiveSpacing)
    }
}