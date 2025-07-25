package com.meticha.jetpackboilerplate.launcher.data.repository

import com.meticha.jetpackboilerplate.launcher.data.models.UserAction
import com.meticha.jetpackboilerplate.launcher.data.models.ActionType
import com.meticha.jetpackboilerplate.launcher.data.mock.MockDataProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserActionRepositoryImpl @Inject constructor() : UserActionRepository {

    private val _userActions = MutableStateFlow<List<UserAction>>(MockDataProvider.getMockUserActions())
    private val userActions = _userActions.asStateFlow()

    override suspend fun insertUserAction(userAction: UserAction) {
        // Simulate action recording delay
        delay(50)
        val currentList = _userActions.value.toMutableList()
        currentList.add(userAction)
        
        // Keep only the most recent actions to prevent memory issues
        if (currentList.size > 1000) {
            currentList.sortByDescending { it.timestamp }
            _userActions.value = currentList.take(1000)
        } else {
            _userActions.value = currentList
        }
    }

    override suspend fun getUserActionById(actionId: String): UserAction? {
        // Simulate database query delay
        delay(30)
        return _userActions.value.find { it.id == actionId }
    }

    override fun getUserActionsByRoutineId(routineId: String): Flow<List<UserAction>> {
        return userActions.map { list -> 
            list.filter { it.routineId == routineId }
                .sortedByDescending { it.timestamp }
        }
    }

    override fun getUserActionsByType(actionType: ActionType): Flow<List<UserAction>> {
        return userActions.map { list -> 
            list.filter { it.type == actionType }
                .sortedByDescending { it.timestamp }
        }
    }

    override fun getRecentUserActions(limit: Int): Flow<List<UserAction>> {
        return userActions.map { list -> 
            list.sortedByDescending { it.timestamp }.take(limit)
        }
    }

    override suspend fun getUserActionsInTimeRange(startTime: Long, endTime: Long): List<UserAction> {
        // Simulate time range query delay
        delay(120)
        return _userActions.value.filter { 
            it.timestamp >= startTime && it.timestamp <= endTime 
        }.sortedByDescending { it.timestamp }
    }

    override suspend fun deleteOldUserActions(olderThan: Long) {
        // Simulate cleanup operation delay
        delay(200)
        val currentList = _userActions.value.toMutableList()
        currentList.removeAll { it.timestamp < olderThan }
        _userActions.value = currentList
    }
}