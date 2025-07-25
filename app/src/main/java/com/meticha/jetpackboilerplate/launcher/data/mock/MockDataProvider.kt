package com.meticha.jetpackboilerplate.launcher.data.mock

import com.meticha.jetpackboilerplate.launcher.data.models.*
import com.meticha.jetpackboilerplate.launcher.utils.IdGenerator
import kotlinx.serialization.json.Json

/**
 * Provides comprehensive mock data for UI development and testing
 * Includes sample routines, intentions, actions, and behavior patterns for all routine types
 */
object MockDataProvider {
    
    private val json = Json { ignoreUnknownKeys = true }
    
    // Mock Apps Data - Comprehensive app list for different routine contexts
    val mockApps = listOf(
        // Productivity & Work
        AppInfo("com.slack", "Slack", usageCount = 145, category = "Work", lastUsed = System.currentTimeMillis() - 3600000),
        AppInfo("com.microsoft.teams", "Teams", usageCount = 89, category = "Work", lastUsed = System.currentTimeMillis() - 7200000),
        AppInfo("com.google.android.gm", "Gmail", usageCount = 234, category = "Email", lastUsed = System.currentTimeMillis() - 1800000),
        AppInfo("com.todoist", "Todoist", usageCount = 67, category = "Productivity", lastUsed = System.currentTimeMillis() - 5400000),
        AppInfo("com.google.android.calendar", "Calendar", usageCount = 78, category = "Productivity", lastUsed = System.currentTimeMillis() - 10800000),
        AppInfo("com.notion.id", "Notion", usageCount = 45, category = "Productivity", lastUsed = System.currentTimeMillis() - 14400000),
        AppInfo("com.trello", "Trello", usageCount = 23, category = "Productivity", lastUsed = System.currentTimeMillis() - 18000000),
        
        // Health & Wellness
        AppInfo("com.headspace.android", "Headspace", usageCount = 56, category = "Health", lastUsed = System.currentTimeMillis() - 21600000),
        AppInfo("com.calm", "Calm", usageCount = 34, category = "Health", lastUsed = System.currentTimeMillis() - 25200000),
        AppInfo("com.strava", "Strava", usageCount = 89, category = "Fitness", lastUsed = System.currentTimeMillis() - 28800000),
        AppInfo("com.myfitnesspal.android", "MyFitnessPal", usageCount = 67, category = "Health", lastUsed = System.currentTimeMillis() - 32400000),
        AppInfo("com.nike.ntc", "Nike Training", usageCount = 45, category = "Fitness", lastUsed = System.currentTimeMillis() - 36000000),
        
        // Communication & Social
        AppInfo("com.whatsapp", "WhatsApp", usageCount = 345, category = "Communication", lastUsed = System.currentTimeMillis() - 900000),
        AppInfo("com.instagram.android", "Instagram", usageCount = 234, category = "Social", lastUsed = System.currentTimeMillis() - 3600000),
        AppInfo("com.twitter.android", "Twitter", usageCount = 156, category = "Social", lastUsed = System.currentTimeMillis() - 7200000),
        AppInfo("com.linkedin.android", "LinkedIn", usageCount = 78, category = "Professional", lastUsed = System.currentTimeMillis() - 10800000),
        AppInfo("com.discord", "Discord", usageCount = 67, category = "Communication", lastUsed = System.currentTimeMillis() - 14400000),
        
        // Entertainment & Media
        AppInfo("com.netflix.mediaclient", "Netflix", usageCount = 189, category = "Entertainment", lastUsed = System.currentTimeMillis() - 18000000),
        AppInfo("com.spotify.music", "Spotify", usageCount = 267, category = "Music", lastUsed = System.currentTimeMillis() - 1800000),
        AppInfo("com.youtube.android", "YouTube", usageCount = 345, category = "Entertainment", lastUsed = System.currentTimeMillis() - 5400000),
        AppInfo("com.amazon.kindle", "Kindle", usageCount = 89, category = "Reading", lastUsed = System.currentTimeMillis() - 21600000),
        AppInfo("com.audible.application", "Audible", usageCount = 56, category = "Reading", lastUsed = System.currentTimeMillis() - 25200000),
        
        // Utilities & Tools
        AppInfo("com.android.chrome", "Chrome", usageCount = 456, category = "Browser", lastUsed = System.currentTimeMillis() - 1800000),
        AppInfo("com.google.android.apps.maps", "Maps", usageCount = 123, category = "Navigation", lastUsed = System.currentTimeMillis() - 7200000),
        AppInfo("com.uber", "Uber", usageCount = 45, category = "Transportation", lastUsed = System.currentTimeMillis() - 32400000),
        AppInfo("com.weather.Weather", "Weather", usageCount = 234, category = "Utilities", lastUsed = System.currentTimeMillis() - 10800000),
        
        // Learning & Education
        AppInfo("com.duolingo", "Duolingo", usageCount = 78, category = "Education", lastUsed = System.currentTimeMillis() - 28800000),
        AppInfo("com.coursera.android", "Coursera", usageCount = 34, category = "Education", lastUsed = System.currentTimeMillis() - 36000000),
        AppInfo("com.khanacademy.android", "Khan Academy", usageCount = 23, category = "Education", lastUsed = System.currentTimeMillis() - 39600000)
    )
    
    // Mock Routines
    fun getMockRoutines(): List<Routine> {
        return listOf(
            Routine(
                id = IdGenerator.generateId(),
                name = "Morning Routine",
                type = RoutineType.MORNING,
                startTime = "06:00",
                endTime = "12:00",
                isActive = true,
                priority = 1
            ),
            Routine(
                id = IdGenerator.generateId(),
                name = "Afternoon Routine", 
                type = RoutineType.AFTERNOON,
                startTime = "12:00",
                endTime = "18:00",
                isActive = true,
                priority = 2
            ),
            Routine(
                id = IdGenerator.generateId(),
                name = "Evening Routine",
                type = RoutineType.EVENING,
                startTime = "18:00",
                endTime = "23:00",
                isActive = true,
                priority = 3
            ),
            Routine(
                id = IdGenerator.generateId(),
                name = "Weekend Routine",
                type = RoutineType.WEEKEND,
                startTime = "08:00",
                endTime = "22:00",
                isActive = true,
                priority = 4
            )
        )
    }
    
    // Mock Intentions for each routine type
    fun getMockIntentions(): List<Intention> {
        val routines = getMockRoutines()
        val morningRoutine = routines.first { it.type == RoutineType.MORNING }
        val afternoonRoutine = routines.first { it.type == RoutineType.AFTERNOON }
        val eveningRoutine = routines.first { it.type == RoutineType.EVENING }
        val weekendRoutine = routines.first { it.type == RoutineType.WEEKEND }
        
        return listOf(
            // Morning Intentions
            Intention.create(
                id = IdGenerator.generateId(),
                routineId = morningRoutine.id,
                description = "Start the day with mindfulness and focus",
                priority = 1,
                targetActionsList = listOf("meditation", "journaling", "planning"),
                successMetricsList = listOf("10 minutes meditation", "daily journal entry", "day planned")
            ),
            Intention.create(
                id = IdGenerator.generateId(),
                routineId = morningRoutine.id,
                description = "Stay informed and connected",
                priority = 2,
                targetActionsList = listOf("check_news", "read_emails", "team_updates"),
                successMetricsList = listOf("news briefing read", "inbox zero", "team sync complete")
            ),
            Intention.create(
                id = IdGenerator.generateId(),
                routineId = morningRoutine.id,
                description = "Maintain physical wellness",
                priority = 3,
                targetActionsList = listOf("exercise", "healthy_breakfast", "hydration"),
                successMetricsList = listOf("30 min workout", "nutritious meal", "water intake tracked")
            ),
            
            // Afternoon Intentions
            Intention.create(
                id = IdGenerator.generateId(),
                routineId = afternoonRoutine.id,
                description = "Maximize productivity and focus on deep work",
                priority = 1,
                targetActionsList = listOf("deep_work", "project_progress", "skill_development"),
                successMetricsList = listOf("2 hours focused work", "project milestone", "learning session")
            ),
            Intention.create(
                id = IdGenerator.generateId(),
                routineId = afternoonRoutine.id,
                description = "Collaborate effectively with team",
                priority = 2,
                targetActionsList = listOf("team_meetings", "code_reviews", "knowledge_sharing"),
                successMetricsList = listOf("meetings attended", "reviews completed", "knowledge shared")
            ),
            
            // Evening Intentions
            Intention.create(
                id = IdGenerator.generateId(),
                routineId = eveningRoutine.id,
                description = "Wind down and reflect on the day",
                priority = 1,
                targetActionsList = listOf("day_review", "gratitude_practice", "tomorrow_prep"),
                successMetricsList = listOf("day reflected", "gratitude noted", "tomorrow planned")
            ),
            Intention.create(
                id = IdGenerator.generateId(),
                routineId = eveningRoutine.id,
                description = "Enjoy personal time and relaxation",
                priority = 2,
                targetActionsList = listOf("entertainment", "reading", "social_connection"),
                successMetricsList = listOf("relaxation time", "pages read", "friends contacted")
            ),
            
            // Weekend Intentions
            Intention.create(
                id = IdGenerator.generateId(),
                routineId = weekendRoutine.id,
                description = "Pursue personal interests and hobbies",
                priority = 1,
                targetActionsList = listOf("hobby_time", "creative_projects", "skill_learning"),
                successMetricsList = listOf("hobby engaged", "project progress", "new skill practiced")
            ),
            Intention.create(
                id = IdGenerator.generateId(),
                routineId = weekendRoutine.id,
                description = "Maintain relationships and social connections",
                priority = 2,
                targetActionsList = listOf("family_time", "friend_meetups", "community_activities"),
                successMetricsList = listOf("family bonding", "social interaction", "community engagement")
            )
        )
    }
    
    // Mock User Actions - Comprehensive action history for all routine types
    fun getMockUserActions(): List<UserAction> {
        val routines = getMockRoutines()
        val currentTime = System.currentTimeMillis()
        val oneHour = 60 * 60 * 1000L
        val oneDay = 24 * oneHour
        
        val morningRoutine = routines.first { it.type == RoutineType.MORNING }
        val afternoonRoutine = routines.first { it.type == RoutineType.AFTERNOON }
        val eveningRoutine = routines.first { it.type == RoutineType.EVENING }
        val weekendRoutine = routines.first { it.type == RoutineType.WEEKEND }
        
        return listOf(
            // Today's Morning actions
            UserAction(
                id = IdGenerator.generateId(),
                type = ActionType.APP_LAUNCH,
                timestamp = currentTime - (8 * oneHour),
                routineId = morningRoutine.id,
                appPackageName = "com.headspace.android",
                contextData = """{"location": "home", "weather": "sunny", "day_of_week": "weekday"}""",
                outcome = ActionOutcome.SUCCESS,
                duration = 10 * 60 * 1000L
            ),
            UserAction(
                id = IdGenerator.generateId(),
                type = ActionType.APP_LAUNCH,
                timestamp = currentTime - (7.5f * oneHour).toLong(),
                routineId = morningRoutine.id,
                appPackageName = "com.google.android.gm",
                contextData = """{"location": "home", "notification_count": 5}""",
                outcome = ActionOutcome.SUCCESS,
                duration = 15 * 60 * 1000L
            ),
            UserAction(
                id = IdGenerator.generateId(),
                type = ActionType.APP_LAUNCH,
                timestamp = currentTime - (7 * oneHour),
                routineId = morningRoutine.id,
                appPackageName = "com.spotify.music",
                contextData = """{"location": "home", "activity": "getting_ready"}""",
                outcome = ActionOutcome.SUCCESS,
                duration = 25 * 60 * 1000L
            ),
            
            // Today's Afternoon actions
            UserAction(
                id = IdGenerator.generateId(),
                type = ActionType.APP_LAUNCH,
                timestamp = currentTime - (4 * oneHour),
                routineId = afternoonRoutine.id,
                appPackageName = "com.slack",
                contextData = """{"location": "office", "meeting_scheduled": true}""",
                outcome = ActionOutcome.SUCCESS,
                duration = 45 * 60 * 1000L
            ),
            UserAction(
                id = IdGenerator.generateId(),
                type = ActionType.APP_LAUNCH,
                timestamp = currentTime - (3.5f * oneHour).toLong(),
                routineId = afternoonRoutine.id,
                appPackageName = "com.notion.id",
                contextData = """{"location": "office", "task_type": "project_planning"}""",
                outcome = ActionOutcome.SUCCESS,
                duration = 30 * 60 * 1000L
            ),
            UserAction(
                id = IdGenerator.generateId(),
                type = ActionType.APP_LAUNCH,
                timestamp = currentTime - (3 * oneHour),
                routineId = afternoonRoutine.id,
                appPackageName = "com.android.chrome",
                contextData = """{"location": "office", "research_topic": "android_development"}""",
                outcome = ActionOutcome.SUCCESS,
                duration = 60 * 60 * 1000L
            ),
            
            // Today's Evening actions
            UserAction(
                id = IdGenerator.generateId(),
                type = ActionType.APP_LAUNCH,
                timestamp = currentTime - (2 * oneHour),
                routineId = eveningRoutine.id,
                appPackageName = "com.whatsapp",
                contextData = """{"location": "home", "social_context": "family_chat"}""",
                outcome = ActionOutcome.SUCCESS,
                duration = 20 * 60 * 1000L
            ),
            UserAction(
                id = IdGenerator.generateId(),
                type = ActionType.APP_LAUNCH,
                timestamp = currentTime - oneHour,
                routineId = eveningRoutine.id,
                appPackageName = "com.netflix.mediaclient",
                contextData = """{"location": "home", "content_type": "series", "mood": "relaxed"}""",
                outcome = ActionOutcome.SUCCESS,
                duration = 90 * 60 * 1000L
            ),
            
            // Yesterday's patterns for learning
            UserAction(
                id = IdGenerator.generateId(),
                type = ActionType.APP_LAUNCH,
                timestamp = currentTime - oneDay - (8 * oneHour),
                routineId = morningRoutine.id,
                appPackageName = "com.calm",
                contextData = """{"location": "home", "weather": "rainy"}""",
                outcome = ActionOutcome.SUCCESS,
                duration = 12 * 60 * 1000L
            ),
            UserAction(
                id = IdGenerator.generateId(),
                type = ActionType.APP_LAUNCH,
                timestamp = currentTime - oneDay - (4 * oneHour),
                routineId = afternoonRoutine.id,
                appPackageName = "com.microsoft.teams",
                contextData = """{"location": "office", "meeting_type": "standup"}""",
                outcome = ActionOutcome.SUCCESS,
                duration = 30 * 60 * 1000L
            ),
            
            // Weekend actions
            UserAction(
                id = IdGenerator.generateId(),
                type = ActionType.APP_LAUNCH,
                timestamp = currentTime - (2 * oneDay) - (10 * oneHour),
                routineId = weekendRoutine.id,
                appPackageName = "com.strava",
                contextData = """{"location": "park", "activity": "running", "weather": "sunny"}""",
                outcome = ActionOutcome.SUCCESS,
                duration = 45 * 60 * 1000L
            ),
            UserAction(
                id = IdGenerator.generateId(),
                type = ActionType.APP_LAUNCH,
                timestamp = currentTime - (2 * oneDay) - (6 * oneHour),
                routineId = weekendRoutine.id,
                appPackageName = "com.instagram.android",
                contextData = """{"location": "home", "social_context": "weekend_sharing"}""",
                outcome = ActionOutcome.SUCCESS,
                duration = 25 * 60 * 1000L
            )
        )
    }
    
    // Mock Behavior Patterns - Comprehensive patterns for all routine types
    fun getMockBehaviorPatterns(): List<BehaviorPattern> {
        return listOf(
            // Morning patterns
            BehaviorPattern.create(
                id = IdGenerator.generateId(),
                sequenceList = listOf(ActionType.APP_LAUNCH, ActionType.APP_LAUNCH, ActionType.UI_INTERACTION),
                frequency = 25,
                successRate = 0.87f,
                contextFactorsList = listOf("morning", "weekday", "home", "meditation_first"),
                routineType = RoutineType.MORNING
            ),
            BehaviorPattern.create(
                id = IdGenerator.generateId(),
                sequenceList = listOf(ActionType.APP_LAUNCH, ActionType.APP_LAUNCH, ActionType.APP_LAUNCH),
                frequency = 18,
                successRate = 0.92f,
                contextFactorsList = listOf("morning", "weekday", "home", "email_check"),
                routineType = RoutineType.MORNING
            ),
            BehaviorPattern.create(
                id = IdGenerator.generateId(),
                sequenceList = listOf(ActionType.APP_LAUNCH, ActionType.UI_INTERACTION, ActionType.ROUTINE_SWITCH),
                frequency = 12,
                successRate = 0.78f,
                contextFactorsList = listOf("morning", "weekend", "home", "leisurely_start"),
                routineType = RoutineType.MORNING
            ),
            
            // Afternoon patterns
            BehaviorPattern.create(
                id = IdGenerator.generateId(),
                sequenceList = listOf(ActionType.APP_LAUNCH, ActionType.UI_INTERACTION, ActionType.APP_LAUNCH),
                frequency = 35,
                successRate = 0.94f,
                contextFactorsList = listOf("afternoon", "work_hours", "office", "deep_work_session"),
                routineType = RoutineType.AFTERNOON
            ),
            BehaviorPattern.create(
                id = IdGenerator.generateId(),
                sequenceList = listOf(ActionType.APP_LAUNCH, ActionType.APP_LAUNCH, ActionType.UI_INTERACTION),
                frequency = 28,
                successRate = 0.89f,
                contextFactorsList = listOf("afternoon", "work_hours", "office", "collaboration_time"),
                routineType = RoutineType.AFTERNOON
            ),
            BehaviorPattern.create(
                id = IdGenerator.generateId(),
                sequenceList = listOf(ActionType.UI_INTERACTION, ActionType.APP_LAUNCH, ActionType.APP_LAUNCH),
                frequency = 15,
                successRate = 0.85f,
                contextFactorsList = listOf("afternoon", "work_hours", "home", "remote_work"),
                routineType = RoutineType.AFTERNOON
            ),
            
            // Evening patterns
            BehaviorPattern.create(
                id = IdGenerator.generateId(),
                sequenceList = listOf(ActionType.APP_LAUNCH, ActionType.APP_LAUNCH, ActionType.ROUTINE_SWITCH),
                frequency = 22,
                successRate = 0.81f,
                contextFactorsList = listOf("evening", "home", "relaxation", "wind_down"),
                routineType = RoutineType.EVENING
            ),
            BehaviorPattern.create(
                id = IdGenerator.generateId(),
                sequenceList = listOf(ActionType.APP_LAUNCH, ActionType.UI_INTERACTION, ActionType.APP_LAUNCH),
                frequency = 19,
                successRate = 0.76f,
                contextFactorsList = listOf("evening", "home", "social_time", "entertainment"),
                routineType = RoutineType.EVENING
            ),
            BehaviorPattern.create(
                id = IdGenerator.generateId(),
                sequenceList = listOf(ActionType.UI_INTERACTION, ActionType.APP_LAUNCH, ActionType.ROUTINE_SWITCH),
                frequency = 14,
                successRate = 0.88f,
                contextFactorsList = listOf("evening", "home", "reflection", "planning_tomorrow"),
                routineType = RoutineType.EVENING
            ),
            
            // Weekend patterns
            BehaviorPattern.create(
                id = IdGenerator.generateId(),
                sequenceList = listOf(ActionType.APP_LAUNCH, ActionType.UI_INTERACTION, ActionType.APP_LAUNCH),
                frequency = 16,
                successRate = 0.83f,
                contextFactorsList = listOf("weekend", "home", "leisure", "hobby_time"),
                routineType = RoutineType.WEEKEND
            ),
            BehaviorPattern.create(
                id = IdGenerator.generateId(),
                sequenceList = listOf(ActionType.APP_LAUNCH, ActionType.APP_LAUNCH, ActionType.UI_INTERACTION),
                frequency = 12,
                successRate = 0.79f,
                contextFactorsList = listOf("weekend", "outdoor", "fitness", "social_activity"),
                routineType = RoutineType.WEEKEND
            ),
            BehaviorPattern.create(
                id = IdGenerator.generateId(),
                sequenceList = listOf(ActionType.UI_INTERACTION, ActionType.APP_LAUNCH, ActionType.APP_LAUNCH),
                frequency = 8,
                successRate = 0.91f,
                contextFactorsList = listOf("weekend", "home", "personal_projects", "learning"),
                routineType = RoutineType.WEEKEND
            )
        )
    }
    
    // Mock User Context
    fun getMockUserContext(routineType: RoutineType = RoutineType.MORNING): UserContext {
        val intentions = getMockIntentions().filter { intention ->
            getMockRoutines().find { it.id == intention.routineId }?.type == routineType
        }
        
        val recentActions = getMockUserActions().takeLast(3)
        
        return UserContext(
            currentRoutine = routineType,
            timeOfDay = when (routineType) {
                RoutineType.MORNING -> "08:30"
                RoutineType.AFTERNOON -> "14:00"
                RoutineType.EVENING -> "19:30"
                RoutineType.WEEKEND -> "10:00"
                RoutineType.CUSTOM -> "12:00"
            },
            recentActions = recentActions,
            activeIntentions = intentions.take(2),
            locationContext = LocationContext(
                isHome = routineType in listOf(RoutineType.MORNING, RoutineType.EVENING, RoutineType.WEEKEND),
                isWork = routineType == RoutineType.AFTERNOON
            )
        )
    }
    
    // Mock Action Predictions - AI-generated predictions for different routine contexts
    fun getMockActionPredictions(routineType: RoutineType = RoutineType.MORNING): List<ActionPrediction> {
        return when (routineType) {
            RoutineType.MORNING -> listOf(
                ActionPrediction(
                    action = "Start meditation session",
                    confidence = 0.89f,
                    associatedApps = listOf(
                        mockApps.first { it.packageName == "com.headspace.android" },
                        mockApps.first { it.packageName == "com.calm" }
                    ),
                    reasoning = "User typically starts morning with mindfulness practice",
                    priority = 1
                ),
                ActionPrediction(
                    action = "Check emails and messages",
                    confidence = 0.92f,
                    associatedApps = listOf(
                        mockApps.first { it.packageName == "com.google.android.gm" },
                        mockApps.first { it.packageName == "com.slack" },
                        mockApps.first { it.packageName == "com.whatsapp" }
                    ),
                    reasoning = "High probability of checking communications after meditation",
                    priority = 2
                ),
                ActionPrediction(
                    action = "Plan the day",
                    confidence = 0.76f,
                    associatedApps = listOf(
                        mockApps.first { it.packageName == "com.google.android.calendar" },
                        mockApps.first { it.packageName == "com.todoist" },
                        mockApps.first { it.packageName == "com.notion.id" }
                    ),
                    reasoning = "User often reviews schedule and tasks in the morning",
                    priority = 3
                ),
                ActionPrediction(
                    action = "Listen to music while getting ready",
                    confidence = 0.68f,
                    associatedApps = listOf(
                        mockApps.first { it.packageName == "com.spotify.music" }
                    ),
                    reasoning = "Background music is common during morning routine",
                    priority = 4
                )
            )
            
            RoutineType.AFTERNOON -> listOf(
                ActionPrediction(
                    action = "Join team meeting",
                    confidence = 0.94f,
                    associatedApps = listOf(
                        mockApps.first { it.packageName == "com.slack" },
                        mockApps.first { it.packageName == "com.microsoft.teams" }
                    ),
                    reasoning = "Scheduled meeting time based on calendar patterns",
                    priority = 1
                ),
                ActionPrediction(
                    action = "Work on project documentation",
                    confidence = 0.87f,
                    associatedApps = listOf(
                        mockApps.first { it.packageName == "com.notion.id" },
                        mockApps.first { it.packageName == "com.android.chrome" }
                    ),
                    reasoning = "Deep work session typically follows meetings",
                    priority = 2
                ),
                ActionPrediction(
                    action = "Research and development",
                    confidence = 0.81f,
                    associatedApps = listOf(
                        mockApps.first { it.packageName == "com.android.chrome" },
                        mockApps.first { it.packageName == "com.youtube.android" }
                    ),
                    reasoning = "User often researches new technologies in afternoon",
                    priority = 3
                ),
                ActionPrediction(
                    action = "Quick break and social check",
                    confidence = 0.73f,
                    associatedApps = listOf(
                        mockApps.first { it.packageName == "com.instagram.android" },
                        mockApps.first { it.packageName == "com.twitter.android" }
                    ),
                    reasoning = "Short social media break pattern detected",
                    priority = 4
                )
            )
            
            RoutineType.EVENING -> listOf(
                ActionPrediction(
                    action = "Connect with family and friends",
                    confidence = 0.91f,
                    associatedApps = listOf(
                        mockApps.first { it.packageName == "com.whatsapp" },
                        mockApps.first { it.packageName == "com.instagram.android" }
                    ),
                    reasoning = "Evening social connection is a strong pattern",
                    priority = 1
                ),
                ActionPrediction(
                    action = "Watch entertainment content",
                    confidence = 0.85f,
                    associatedApps = listOf(
                        mockApps.first { it.packageName == "com.netflix.mediaclient" },
                        mockApps.first { it.packageName == "com.youtube.android" }
                    ),
                    reasoning = "Relaxation time typically involves video content",
                    priority = 2
                ),
                ActionPrediction(
                    action = "Read or listen to audiobook",
                    confidence = 0.67f,
                    associatedApps = listOf(
                        mockApps.first { it.packageName == "com.amazon.kindle" },
                        mockApps.first { it.packageName == "com.audible.application" }
                    ),
                    reasoning = "Evening reading habit observed on some days",
                    priority = 3
                ),
                ActionPrediction(
                    action = "Plan tomorrow's activities",
                    confidence = 0.59f,
                    associatedApps = listOf(
                        mockApps.first { it.packageName == "com.google.android.calendar" },
                        mockApps.first { it.packageName == "com.todoist" }
                    ),
                    reasoning = "Occasional evening planning sessions detected",
                    priority = 4
                )
            )
            
            RoutineType.WEEKEND -> listOf(
                ActionPrediction(
                    action = "Go for outdoor activity",
                    confidence = 0.88f,
                    associatedApps = listOf(
                        mockApps.first { it.packageName == "com.strava" },
                        mockApps.first { it.packageName == "com.google.android.apps.maps" }
                    ),
                    reasoning = "Weekend outdoor activities are highly predictable",
                    priority = 1
                ),
                ActionPrediction(
                    action = "Work on personal projects",
                    confidence = 0.79f,
                    associatedApps = listOf(
                        mockApps.first { it.packageName == "com.notion.id" },
                        mockApps.first { it.packageName == "com.android.chrome" }
                    ),
                    reasoning = "Weekend project work is a common pattern",
                    priority = 2
                ),
                ActionPrediction(
                    action = "Learn something new",
                    confidence = 0.72f,
                    associatedApps = listOf(
                        mockApps.first { it.packageName == "com.duolingo" },
                        mockApps.first { it.packageName == "com.coursera.android" },
                        mockApps.first { it.packageName == "com.youtube.android" }
                    ),
                    reasoning = "Weekend learning activities show consistent pattern",
                    priority = 3
                ),
                ActionPrediction(
                    action = "Social media and entertainment",
                    confidence = 0.84f,
                    associatedApps = listOf(
                        mockApps.first { it.packageName == "com.instagram.android" },
                        mockApps.first { it.packageName == "com.netflix.mediaclient" }
                    ),
                    reasoning = "Weekend leisure time includes social and entertainment apps",
                    priority = 4
                )
            )
            
            RoutineType.CUSTOM -> listOf(
                ActionPrediction(
                    action = "Check current tasks",
                    confidence = 0.65f,
                    associatedApps = listOf(
                        mockApps.first { it.packageName == "com.todoist" },
                        mockApps.first { it.packageName == "com.google.android.calendar" }
                    ),
                    reasoning = "Default action for custom routine",
                    priority = 1
                )
            )
        }
    }
    
    // Mock UI State Predictions - Predicted UI configurations for different contexts
    fun getMockUIStatePredictions(routineType: RoutineType = RoutineType.MORNING): UIStatePrediction {
        return when (routineType) {
            RoutineType.MORNING -> UIStatePrediction(
                primaryActions = getMockActionPredictions(routineType).take(2),
                secondaryActions = getMockActionPredictions(routineType).drop(2),
                suggestedLayout = "minimal_focus",
                themeMode = "light",
                widgetPriorities = listOf("weather", "calendar", "meditation_timer"),
                confidence = 0.87f
            )
            
            RoutineType.AFTERNOON -> UIStatePrediction(
                primaryActions = getMockActionPredictions(routineType).take(3),
                secondaryActions = getMockActionPredictions(routineType).drop(3),
                suggestedLayout = "productivity_grid",
                themeMode = "light",
                widgetPriorities = listOf("calendar", "tasks", "team_status"),
                confidence = 0.92f
            )
            
            RoutineType.EVENING -> UIStatePrediction(
                primaryActions = getMockActionPredictions(routineType).take(2),
                secondaryActions = getMockActionPredictions(routineType).drop(2),
                suggestedLayout = "relaxed_cards",
                themeMode = "dark",
                widgetPriorities = listOf("entertainment", "social", "reading"),
                confidence = 0.84f
            )
            
            RoutineType.WEEKEND -> UIStatePrediction(
                primaryActions = getMockActionPredictions(routineType).take(2),
                secondaryActions = getMockActionPredictions(routineType).drop(2),
                suggestedLayout = "leisure_flow",
                themeMode = "auto",
                widgetPriorities = listOf("fitness", "hobbies", "social", "learning"),
                confidence = 0.79f
            )
            
            RoutineType.CUSTOM -> UIStatePrediction(
                primaryActions = getMockActionPredictions(routineType),
                secondaryActions = emptyList(),
                suggestedLayout = "default_grid",
                themeMode = "auto",
                widgetPriorities = listOf("tasks", "calendar"),
                confidence = 0.65f
            )
        }
    }
}

// Data classes for mock predictions
data class ActionPrediction(
    val action: String,
    val confidence: Float,
    val associatedApps: List<AppInfo>,
    val reasoning: String,
    val priority: Int
)

data class UIStatePrediction(
    val primaryActions: List<ActionPrediction>,
    val secondaryActions: List<ActionPrediction>,
    val suggestedLayout: String,
    val themeMode: String,
    val widgetPriorities: List<String>,
    val confidence: Float
)