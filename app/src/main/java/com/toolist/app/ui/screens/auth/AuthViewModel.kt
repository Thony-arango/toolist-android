package com.toolist.app.ui.screens.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest
import com.toolist.app.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.IOException
import javax.inject.Inject

// Estado compartido del ViewModel

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val nameError: String? = null,
    val confirmPasswordError: String? = null,
)

// ViewModel

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        val emailErr = validateEmail(email)
        val passErr = validatePassword(password)
        if (emailErr != null || passErr != null) {
            _uiState.update { it.copy(emailError = emailErr, passwordError = passErr) }
            return
        }

        viewModelScope.launch {
            _uiState.update { AuthUiState(isLoading = true) }
            try {
                firebaseAuth.signInWithEmailAndPassword(email.trim(), password).await()
                _uiState.update { AuthUiState(isSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { AuthUiState(error = mapFirebaseError(e)) }
            }
        }
    }

    fun register(name: String, email: String, password: String, confirmPassword: String) {
        val nameErr = validateName(name)
        val emailErr = validateEmail(email)
        val passErr = validatePassword(password)
        val confirmErr = validateConfirmPassword(password, confirmPassword)
        if (nameErr != null || emailErr != null || passErr != null || confirmErr != null) {
            _uiState.update {
                AuthUiState(
                    nameError = nameErr,
                    emailError = emailErr,
                    passwordError = passErr,
                    confirmPasswordError = confirmErr,
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { AuthUiState(isLoading = true) }
            try {
                val result = firebaseAuth
                    .createUserWithEmailAndPassword(email.trim(), password)
                    .await()
                val profileUpdate = UserProfileChangeRequest.Builder()
                    .setDisplayName(name.trim())
                    .build()
                result.user?.updateProfile(profileUpdate)?.await()
                _uiState.update { AuthUiState(isSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { AuthUiState(error = mapFirebaseError(e)) }
            }
        }
    }

    fun sendPasswordReset(email: String) {
        val emailErr = validateEmail(email)
        if (emailErr != null) {
            _uiState.update { AuthUiState(emailError = emailErr) }
            return
        }

        viewModelScope.launch {
            _uiState.update { AuthUiState(isLoading = true) }
            try {
                firebaseAuth.sendPasswordResetEmail(email.trim()).await()
                _uiState.update { AuthUiState(isSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { AuthUiState(error = mapFirebaseError(e)) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun resetState() {
        _uiState.update { AuthUiState() }
    }

    private fun validateName(name: String): String? {
        val trimmed = name.trim()
        return when {
            trimmed.isEmpty() -> context.getString(R.string.error_field_required)
            trimmed.length < 2 -> context.getString(R.string.error_name_too_short)
            else -> null
        }
    }

    private fun validateEmail(email: String): String? {
        val trimmed = email.trim()
        return when {
            trimmed.isEmpty() -> context.getString(R.string.error_field_required)
            !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmed).matches() ->
                context.getString(R.string.error_email_invalid)
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isEmpty() -> context.getString(R.string.error_field_required)
            password.length < 6 -> context.getString(R.string.error_password_too_short)
            else -> null
        }
    }

    private fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        return when {
            confirmPassword.isEmpty() -> context.getString(R.string.error_field_required)
            confirmPassword != password -> context.getString(R.string.error_passwords_do_not_match)
            else -> null
        }
    }

    private fun mapFirebaseError(e: Exception): String = when (e) {
        is FirebaseAuthInvalidCredentialsException ->
            context.getString(R.string.error_auth_wrong_credentials)
        is FirebaseAuthInvalidUserException ->
            context.getString(R.string.error_auth_user_not_found)
        is FirebaseAuthUserCollisionException ->
            context.getString(R.string.error_auth_email_already_in_use)
        is FirebaseAuthException -> when (e.errorCode) {
            "ERROR_TOO_MANY_REQUESTS",
            "TOO_MANY_ATTEMPTS_TRY_LATER" ->
                context.getString(R.string.error_auth_too_many_requests)
            else -> context.getString(R.string.error_generic)
        }
        is IOException ->
            context.getString(R.string.error_network)
        else -> context.getString(R.string.error_generic)
    }
}
