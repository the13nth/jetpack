package com.example.rotiie.launcher.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.example.rotiie.launcher.data.repository.FirebaseAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: FirebaseAuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    private val _authState = MutableStateFlow<FirebaseUser?>(null)
    val authState: StateFlow<FirebaseUser?> = _authState.asStateFlow()
    
    init {
        viewModelScope.launch {
            authRepository.getAuthStateFlow().collect { user ->
                _authState.value = user
                _uiState.value = _uiState.value.copy(
                    isLoggedIn = user != null,
                    currentUser = user
                )
            }
        }
    }
    
    // fun signIn(email: String, password: String) {
    //     viewModelScope.launch {
    //         _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
    //         val result = authRepository.signInWithEmailAndPassword(email, password)
    //         result.fold(
    //             onSuccess = { user ->
    //                 _uiState.value = _uiState.value.copy(
    //                     isLoading = false,
    //                     isLoggedIn = true,
    //                     currentUser = user
    //                 )
    //             },
    //             onFailure = { exception ->
    //                 _uiState.value = _uiState.value.copy(
    //                     isLoading = false,
    //                     error = exception.message ?: "Sign in failed"
    //                 )
    //             }
    //         )
    //     }
    // }
    
    // fun signUp(email: String, password: String, displayName: String) {
    //     viewModelScope.launch {
    //         _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
    //         val result = authRepository.createUserWithEmailAndPassword(email, password)
    //         result.fold(
    //             onSuccess = { user ->
    //                 // Update user profile with display name
    //                 authRepository.updateUserProfile(displayName)
    //                 _uiState.value = _uiState.value.copy(
    //                     isLoading = false,
    //                     isLoggedIn = true,
    //                     currentUser = user
    //                 )
    //             },
    //             onFailure = { exception ->
    //                 _uiState.value = _uiState.value.copy(
    //                     isLoading = false,
    //                     error = exception.message ?: "Sign up failed"
    //                 )
    //             }
    //         )
    //     }
    // }
    
    // fun signOut() {
    //     authRepository.signOut()
    //     _uiState.value = _uiState.value.copy(
    //         isLoggedIn = false,
    //         currentUser = null
    //     )
    // }
    
    // fun resetPassword(email: String) {
    //     viewModelScope.launch {
    //         _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
    //         val result = authRepository.sendPasswordResetEmail(email)
    //         result.fold(
    //             onSuccess = {
    //                 _uiState.value = _uiState.value.copy(
    //                     isLoading = false,
    //                     message = "Password reset email sent"
    //                 )
    //             },
    //             onFailure = { exception ->
    //                 _uiState.value = _uiState.value.copy(
    //                     isLoading = false,
    //                     error = exception.message ?: "Failed to send reset email"
    //                 )
    //             }
    //         )
    //     }
    // }
    
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val result = authRepository.signInWithEmailAndPassword(email, password)
            result.fold(
                onSuccess = { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        currentUser = user
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Sign in failed"
                    )
                }
            )
        }
    }
    
    fun signUp(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val result = authRepository.createUserWithEmailAndPassword(email, password)
            result.fold(
                onSuccess = { user ->
                    // Update user profile with display name
                    authRepository.updateUserProfile(displayName)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        currentUser = user
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Sign up failed"
                    )
                }
            )
        }
    }
    
    fun signOut() {
        authRepository.signOut()
        _uiState.value = _uiState.value.copy(
            isLoggedIn = false,
            currentUser = null
        )
    }
    
    fun resetPassword(email: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val result = authRepository.sendPasswordResetEmail(email)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        message = "Password reset email sent"
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Failed to send reset email"
                    )
                }
            )
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}

data class SettingsUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val currentUser: FirebaseUser? = null,
    val error: String? = null,
    val message: String? = null
) 