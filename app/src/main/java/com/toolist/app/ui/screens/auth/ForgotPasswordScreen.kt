package com.toolist.app.ui.screens.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.toolist.app.R
import com.toolist.app.ui.theme.ButtonHeight
import com.toolist.app.ui.theme.ButtonRadius
import com.toolist.app.ui.theme.IconLg
import com.toolist.app.ui.theme.IconMd
import com.toolist.app.ui.theme.SpacingLg
import com.toolist.app.ui.theme.SpacingMd
import com.toolist.app.ui.theme.SpacingSm
import com.toolist.app.ui.theme.SpacingXl
import com.toolist.app.ui.theme.SpacingXs
import com.toolist.app.ui.theme.TextFieldRadius
import com.toolist.app.ui.theme.ToolistTheme

// ---------------------------------------------------------------------------
// UiState local
// ---------------------------------------------------------------------------

data class ForgotPasswordUiState(
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val isSuccess: Boolean = false,
    val generalError: String? = null,
)

// ---------------------------------------------------------------------------
// Pantalla
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onSendClick: (email: String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    uiState: ForgotPasswordUiState = ForgotPasswordUiState(),
) {
    var email by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        if (uiState.isSuccess) {
            SuccessContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = SpacingMd)
                    .navigationBarsPadding(),
                onNavigateBack = onNavigateBack,
            )
        } else {
            FormContent(
                email = email,
                onEmailChange = { email = it },
                uiState = uiState,
                onSendClick = {
                    focusManager.clearFocus()
                    onSendClick(email)
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = SpacingMd)
                    .navigationBarsPadding(),
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Contenido del formulario
// ---------------------------------------------------------------------------

@Composable
private fun FormContent(
    email: String,
    onEmailChange: (String) -> Unit,
    uiState: ForgotPasswordUiState,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(SpacingMd))

        // ── Título ────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.forgot_password_title),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(SpacingXs))

        // ── Subtítulo ─────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.forgot_password_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(SpacingXl))

        // ── Campo correo electrónico ───────────────────────────────────
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.forgot_password_label_email)) },
            placeholder = { Text(stringResource(R.string.forgot_password_hint_email)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Email,
                    contentDescription = null,
                    modifier = Modifier.size(IconMd),
                )
            },
            isError = uiState.emailError != null,
            supportingText = uiState.emailError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = { if (!uiState.isLoading) onSendClick() },
            ),
            singleLine = true,
            enabled = !uiState.isLoading,
            shape = RoundedCornerShape(TextFieldRadius),
        )

        // ── Error general ─────────────────────────────────────────────
        if (uiState.generalError != null) {
            Spacer(modifier = Modifier.height(SpacingXs))
            Text(
                text = uiState.generalError,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = SpacingSm),
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // ── Botón primario: Enviar enlace ─────────────────────────────
        Button(
            onClick = onSendClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(ButtonHeight),
            shape = RoundedCornerShape(ButtonRadius),
            enabled = !uiState.isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(IconMd),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(
                    text = stringResource(R.string.forgot_password_btn_send),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }

        Spacer(modifier = Modifier.height(SpacingLg))
    }
}

// ---------------------------------------------------------------------------
// Contenido de éxito
// ---------------------------------------------------------------------------

@Composable
private fun SuccessContent(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SpacingMd),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // ── Ícono de confirmación ─────────────────────────────────
            Icon(
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(IconLg),
            )

            Spacer(modifier = Modifier.height(SpacingLg))

            // ── Mensaje de éxito ──────────────────────────────────────
            Text(
                text = stringResource(R.string.forgot_password_success),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(SpacingXl))

            // ── Botón: Volver a iniciar sesión ────────────────────────
            Button(
                onClick = onNavigateBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ButtonHeight),
                shape = RoundedCornerShape(ButtonRadius),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Text(
                    text = stringResource(R.string.forgot_password_back_to_login),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@Preview(showSystemUi = true)
@Composable
private fun ForgotPasswordScreenPreview() {
    ToolistTheme {
        ForgotPasswordScreen(
            onSendClick = {},
            onNavigateBack = {},
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun ForgotPasswordScreenLoadingPreview() {
    ToolistTheme {
        ForgotPasswordScreen(
            onSendClick = {},
            onNavigateBack = {},
            uiState = ForgotPasswordUiState(isLoading = true),
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun ForgotPasswordScreenSuccessPreview() {
    ToolistTheme {
        ForgotPasswordScreen(
            onSendClick = {},
            onNavigateBack = {},
            uiState = ForgotPasswordUiState(isSuccess = true),
        )
    }
}
