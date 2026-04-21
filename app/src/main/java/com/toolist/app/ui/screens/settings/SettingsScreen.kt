package com.toolist.app.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material.icons.rounded.SyncDisabled
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.toolist.app.R
import com.toolist.app.ui.components.ToolistBottomNavBar
import com.toolist.app.ui.components.ToolistTab
import com.toolist.app.ui.theme.AvatarLg
import com.toolist.app.ui.theme.Green100
import com.toolist.app.ui.theme.Green700
import com.toolist.app.ui.theme.IconMd
import com.toolist.app.ui.theme.RadiusSm
import com.toolist.app.ui.theme.SpacingLg
import com.toolist.app.ui.theme.SpacingMd
import com.toolist.app.ui.theme.SpacingSm
import com.toolist.app.ui.theme.SpacingXl
import com.toolist.app.ui.theme.SpacingXs
import com.toolist.app.ui.theme.ToolistTheme

// ---------------------------------------------------------------------------
// Pantalla
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onNavigateToHome: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToCredits: () -> Unit,
    onShowLogoutDialog: () -> Unit,
    onDismissLogoutDialog: () -> Unit,
    onConfirmLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_title),
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        bottomBar = {
            ToolistBottomNavBar(
                activeTab = ToolistTab.SETTINGS,
                onNavigateToHome = onNavigateToHome,
                onNavigateToSearch = onNavigateToSearch,
                onNavigateToSettings = { /* ya estamos aquí */ },
                onNavigateToCredits = onNavigateToCredits,
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(SpacingXl))

            // ── Avatar ─────────────────────────────────────────────────────
            UserAvatar(initials = uiState.userInitials)

            Spacer(modifier = Modifier.height(SpacingMd))

            // ── Nombre ─────────────────────────────────────────────────────
            if (uiState.userName.isNotEmpty()) {
                Text(
                    text = uiState.userName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = SpacingMd),
                )
            }

            // ── Correo ─────────────────────────────────────────────────────
            if (uiState.userEmail.isNotEmpty()) {
                Spacer(modifier = Modifier.height(SpacingXs))
                Text(
                    text = uiState.userEmail,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = SpacingMd),
                )
            }

            Spacer(modifier = Modifier.height(SpacingXl))
            HorizontalDivider()

            // ── Sección CUENTA ─────────────────────────────────────────────
            SectionHeader(
                text = stringResource(R.string.settings_section_account),
                modifier = Modifier.padding(horizontal = SpacingMd, vertical = SpacingSm),
            )

            SettingsRow(
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Logout,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(IconMd),
                    )
                },
                label = stringResource(R.string.settings_btn_logout),
                labelColor = MaterialTheme.colorScheme.error,
                onClick = onShowLogoutDialog,
            )

            HorizontalDivider()

            // ── Sección DATOS ──────────────────────────────────────────────
            SectionHeader(
                text = stringResource(R.string.settings_section_data),
                modifier = Modifier.padding(horizontal = SpacingMd, vertical = SpacingSm),
            )

            SettingsRow(
                icon = {
                    Icon(
                        imageVector = if (uiState.isConnected) Icons.Rounded.Sync else Icons.Rounded.SyncDisabled,
                        contentDescription = null,
                        tint = if (uiState.isConnected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(IconMd),
                    )
                },
                label = stringResource(R.string.settings_label_sync),
                trailing = {
                    SyncBadge(isConnected = uiState.isConnected)
                },
                onClick = null,
            )

            HorizontalDivider()
            Spacer(modifier = Modifier.height(SpacingLg))
        }
    }

    // ── Diálogo cerrar sesión ──────────────────────────────────────────────
    if (uiState.showLogoutDialog) {
        AlertDialog(
            onDismissRequest = onDismissLogoutDialog,
            title = { Text(stringResource(R.string.dialog_logout_title)) },
            text = { Text(stringResource(R.string.dialog_logout_message)) },
            confirmButton = {
                TextButton(onClick = onConfirmLogout) {
                    Text(
                        text = stringResource(R.string.dialog_btn_logout),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissLogoutDialog) {
                    Text(stringResource(R.string.dialog_btn_cancel))
                }
            },
        )
    }
}

// ---------------------------------------------------------------------------
// Avatar de usuario
// ---------------------------------------------------------------------------

@Composable
private fun UserAvatar(initials: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.size(AvatarLg),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = initials,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Encabezado de sección
// ---------------------------------------------------------------------------

@Composable
private fun SectionHeader(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.fillMaxWidth(),
    )
}

// ---------------------------------------------------------------------------
// Fila de configuración
// ---------------------------------------------------------------------------

@Composable
private fun SettingsRow(
    icon: @Composable () -> Unit,
    label: String,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    labelColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    trailing: (@Composable () -> Unit)? = null,
) {
    val rowModifier = if (onClick != null)
        modifier
            .fillMaxWidth()
            .then(Modifier.padding(0.dp))
            .also { /* clickable handled via TextButton or Surface */ }
    else
        modifier.fillMaxWidth()

    Surface(
        onClick = onClick ?: {},
        enabled = onClick != null,
        color = MaterialTheme.colorScheme.background,
        modifier = rowModifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SpacingMd, vertical = SpacingMd),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SpacingMd),
        ) {
            icon()
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = labelColor,
                modifier = Modifier.weight(1f),
            )
            trailing?.invoke()
        }
    }
}

// ---------------------------------------------------------------------------
// Badge de sincronización
// ---------------------------------------------------------------------------

@Composable
private fun SyncBadge(isConnected: Boolean) {
    Surface(
        shape = RoundedCornerShape(RadiusSm),
        color = if (isConnected) Green100 else MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Text(
            text = stringResource(
                if (isConnected) R.string.settings_sync_active else R.string.settings_sync_inactive,
            ),
            style = MaterialTheme.typography.labelMedium,
            color = if (isConnected) Green700 else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = SpacingMd, vertical = SpacingXs),
        )
    }
}

// ---------------------------------------------------------------------------
// Preview
// ---------------------------------------------------------------------------

@Preview(showSystemUi = true)
@Composable
private fun SettingsScreenPreview() {
    ToolistTheme {
        SettingsScreen(
            uiState = SettingsUiState(
                userName = "Anthony Arango",
                userEmail = "anthony@upb.edu.co",
                userInitials = "AA",
                isConnected = true,
            ),
            onNavigateToHome = {},
            onNavigateToSearch = {},
            onNavigateToCredits = {},
            onShowLogoutDialog = {},
            onDismissLogoutDialog = {},
            onConfirmLogout = {},
        )
    }
}
