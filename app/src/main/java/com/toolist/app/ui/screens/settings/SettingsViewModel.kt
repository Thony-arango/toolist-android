package com.toolist.app.ui.screens.settings

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

// ---------------------------------------------------------------------------
// UiState
// ---------------------------------------------------------------------------

data class SettingsUiState(
    val userName: String = "",
    val userEmail: String = "",
    val userInitials: String = "",
    val isConnected: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val isLoggedOut: Boolean = false,
)

// ---------------------------------------------------------------------------
// ViewModel
// ---------------------------------------------------------------------------

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadUserInfo()
        checkConnectivity()
    }

    private fun loadUserInfo() {
        val user = auth.currentUser
        val name = user?.displayName ?: ""
        val email = user?.email ?: ""
        val initials = buildInitials(name, email)
        _uiState.update { it.copy(userName = name, userEmail = email, userInitials = initials) }
    }

    private fun buildInitials(name: String, email: String): String {
        if (name.isNotBlank()) {
            val parts = name.trim().split(" ").filter { it.isNotEmpty() }
            return when {
                parts.size >= 2 -> "${parts[0].first()}${parts[1].first()}".uppercase()
                parts.size == 1 -> parts[0].take(2).uppercase()
                else -> "?"
            }
        }
        return email.take(2).uppercase()
    }

    private fun checkConnectivity() {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork
        val caps = cm.getNetworkCapabilities(network)
        val connected = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        _uiState.update { it.copy(isConnected = connected) }
    }

    fun showLogoutDialog() {
        _uiState.update { it.copy(showLogoutDialog = true) }
    }

    fun dismissLogoutDialog() {
        _uiState.update { it.copy(showLogoutDialog = false) }
    }

    fun logout() {
        auth.signOut()
        _uiState.update { it.copy(showLogoutDialog = false, isLoggedOut = true) }
    }
}
