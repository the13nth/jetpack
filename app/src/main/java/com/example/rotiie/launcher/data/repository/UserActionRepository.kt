package com.example.rotiie.launcher.data.repository

import com.example.rotiie.launcher.data.models.UserAction
import com.example.rotiie.launcher.data.models.ActionType
import kotlinx.coroutines.flow.Flow

interface UserActionRepository {
    suspend fun insertUserAction(userAction: UserAction)
    suspend fun getUserActionById(actionId: String): UserAction?
    fun getUserActionsByRoutineId(routineId: String): Flow<List<UserAction>>
    fun getUserActionsByType(actionType: ActionType): Flow<List<UserAction>>
    fun getRecentUserActions(limit: Int = 50): Flow<List<UserAction>>
    suspend fun getUserActionsInTimeRange(startTime: Long, endTime: Long): List<UserAction>
    suspend fun deleteOldUserActions(olderThan: Long)
}