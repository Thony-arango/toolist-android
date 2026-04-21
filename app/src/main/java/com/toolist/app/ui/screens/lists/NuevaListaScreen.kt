package com.toolist.app.ui.screens.lists

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.toolist.app.R
import com.toolist.app.ui.theme.ButtonHeight
import com.toolist.app.ui.theme.ButtonRadius
import com.toolist.app.ui.theme.ColorDotBorderWidth
import com.toolist.app.ui.theme.ColorDotSize
import com.toolist.app.ui.theme.ColorDotSizeSelected
import com.toolist.app.ui.theme.IconMd
import com.toolist.app.ui.theme.SpacingLg
import com.toolist.app.ui.theme.SpacingMd
import com.toolist.app.ui.theme.SpacingSm
import com.toolist.app.ui.theme.SpacingXl
import com.toolist.app.ui.theme.SpacingXs
import com.toolist.app.ui.theme.TextFieldRadius
import com.toolist.app.ui.theme.ToolistTheme

// Opciones de color: Color Compose + hex string

private val colorOptions: List<Pair<Color, String>> = listOf(
    Color(0xFF16A34A) to "#16A34A",
    Color(0xFF3B82F6) to "#3B82F6",
    Color(0xFF8B5CF6) to "#8B5CF6",
    Color(0xFFEC4899) to "#EC4899",
    Color(0xFFEF4444) to "#EF4444",
    Color(0xFFF97316) to "#F97316",
    Color(0xFFEAB308) to "#EAB308",
    Color(0xFF6B7280) to "#6B7280",
)

// Pantalla

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaListaScreen(
    onCreateClick: (name: String, colorHex: String, description: String) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    uiState: NuevaListaUiState = NuevaListaUiState(),
) {
    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var selectedColorHex by rememberSaveable { mutableStateOf(colorOptions.first().second) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            snackbarHostState.showSnackbar(uiState.error)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.new_list_title),
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = SpacingMd)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(SpacingMd))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.new_list_label_name)) },
                placeholder = { Text(stringResource(R.string.new_list_hint_name)) },
                isError = uiState.nameError != null,
                supportingText = uiState.nameError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next,
                ),
                singleLine = true,
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(TextFieldRadius),
            )

            Spacer(modifier = Modifier.height(SpacingLg))

            Text(
                text = stringResource(R.string.new_list_label_color),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(SpacingMd))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                colorOptions.forEach { (color, hex) ->
                    ColorDot(
                        color = color,
                        isSelected = selectedColorHex == hex,
                        onClick = { selectedColorHex = hex },
                        enabled = !uiState.isLoading,
                    )
                }
            }

            Spacer(modifier = Modifier.height(SpacingLg))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                label = { Text(stringResource(R.string.new_list_label_description)) },
                placeholder = { Text(stringResource(R.string.new_list_hint_description)) },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done,
                ),
                maxLines = 4,
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(TextFieldRadius),
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(SpacingXl))

            Button(
                onClick = { onCreateClick(name, selectedColorHex, description) },
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
                        text = stringResource(R.string.new_list_btn_create),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }

            Spacer(modifier = Modifier.height(SpacingXs))

            TextButton(
                onClick = onCancel,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ButtonHeight),
                enabled = !uiState.isLoading,
            ) {
                Text(
                    text = stringResource(R.string.new_list_btn_cancel),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(SpacingLg))
        }
    }
}

// Punto de color seleccionable

@Composable
private fun ColorDot(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val dotSize by animateDpAsState(
        targetValue = if (isSelected) ColorDotSizeSelected else ColorDotSize,
        label = "dotSize",
    )

    Box(
        modifier = modifier
            .size(dotSize)
            .clip(CircleShape)
            .background(color)
            .then(
                if (isSelected) Modifier.border(
                    width = ColorDotBorderWidth,
                    color = MaterialTheme.colorScheme.onBackground,
                    shape = CircleShape,
                ) else Modifier
            )
            .clickable(enabled = enabled, onClick = onClick),
    )
}

// Previews

@Preview(showSystemUi = true)
@Composable
private fun NuevaListaScreenPreview() {
    ToolistTheme {
        NuevaListaScreen(
            onCreateClick = { _, _, _ -> },
            onCancel = {},
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun NuevaListaScreenErrorPreview() {
    ToolistTheme {
        NuevaListaScreen(
            onCreateClick = { _, _, _ -> },
            onCancel = {},
            uiState = NuevaListaUiState(nameError = "El nombre de la lista es obligatorio"),
        )
    }
}
