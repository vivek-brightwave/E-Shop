package com.example.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.User
import com.example.repository.AuthRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel (
    private val authRepository: AuthRepository
) : ViewModel() {

    val currentUser: StateFlow<User?> = authRepository.currentUser
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = authRepository.signInWithGoogle(context)
            if (result.isFailure) {
                _error.value = result.exceptionOrNull()?.message ?: "Sign in failed"
            }
            _isLoading.value = false
        }
    }

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = authRepository.signInWithEmail(email, password)
            if (result.isFailure) {
                _error.value = result.exceptionOrNull()?.message ?: "Sign in failed"
            }
            _isLoading.value = false
        }
    }

    fun signUpWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = authRepository.signUpWithEmail(email, password)
            if (result.isFailure) {
                _error.value = result.exceptionOrNull()?.message ?: "Sign up failed"
            }
            _isLoading.value = false
        }
    }

    fun signOut() {
        authRepository.signOut()
    }
}
