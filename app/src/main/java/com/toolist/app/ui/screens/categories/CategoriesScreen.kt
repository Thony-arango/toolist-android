package com.toolist.app.ui.screens.categories

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.toolist.app.R
import com.toolist.app.domain.model.Category
import com.toolist.app.ui.components.ToolistBottomNavBar
import com.toolist.app.ui.components.ToolistTab
import com.toolist.app.ui.theme.IconLg
import com.toolist.app.ui.theme.IconMd
import com.toolist.app.ui.theme.RadiusMd
import com.toolist.app.ui.theme.SpacingLg
import com.toolist.app.ui.theme.SpacingMd
import com.toolist.app.ui.theme.SpacingSm
import com.toolist.app.ui.theme.SpacingXs
import com.toolist.app.ui.theme.SpacingXxl
import com.toolist.app.ui.theme.TextFieldRadius
import com.toolist.app.ui.theme.ToolistTheme

// ---------------------------------------------------------------------------
// Pantalla
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    uiState: CategoriesUiState,
    onNavigateBack: () -> Unit,
    onNavigateToSearch: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToCredits: () -> Unit = {},
    onShowCreateDialog: () -> Unit,
    onDismissCreateDialog: () -> Unit,
    onCreateCategory: (String) -> Unit,
    onRequestDelete: (Category) -> Unit,
    onDismissDeleteDialog: () -> Unit,
    onConfirmDelete: () -> Unit,
    onErrorShown: () -> Unit,
    onSnackShown: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            snackbarHostState.showSnackbar(uiState.error)
            onErrorShown()
        }
    }
    LaunchedEffect(uiState.snackMessage) {
        if (uiState.snackMessage != null) {
            snackbarHostState.showSnackbar(uiState.snackMessage)
            onSnackShown()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.categories_title),
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = onShowCreateDialog,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
            ) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = stringResource(R.string.cd_add))
            }
        },
        bottomBar = {
            ToolistBottomNavBar(
                activeTab = ToolistTab.HOME,
                onNavigateToHome = onNavigateBack,
                onNavigateToSearch = onNavigateToSearch,
                onNavigateToSettings = onNavigateToSettings,
                onNavigateToCredits = onNavigateToCredits,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .navigationBarsPadding(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = SpacingMd,
                    end = SpacingMd,
                    top = SpacingXs,
                    bottom = SpacingXxl,
                ),
                verticalArrangement = Arrangement.spacedBy(SpacingXs),
            ) {
                // ── Tus categorías ─────────────────────────────────────────
                item {
                    SectionHeader(text = stringResource(R.string.categories_section_mine))
                }

                if (uiState.userCategories.isEmpty()) {
                    item {
                        EmptyUserCategories(
                            onCreateClick = onShowCreateDialog,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                } else {
                    items(uiState.userCategories, key = { it.id }) { category ->
                        CategoryItem(
                            category = category,
                            showDelete = true,
                            onDeleteClick = { onRequestDelete(category) },
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(SpacingMd)) }

                // ── Categorías del sistema ─────────────────────────────────
                item {
                    SectionHeader(text = stringResource(R.string.categories_section_system))
                }

                items(uiState.systemCategories, key = { it.id }) { category ->
                    CategoryItem(
                        category = category,
                        showDelete = false,
                        onDeleteClick = {},
                    )
                }
            }
        }
    }

    // ── Diálogo crear categoría ────────────────────────────────────────────
    if (uiState.showCreateDialog) {
        CreateCategoryDialog(
            onDismiss = onDismissCreateDialog,
            onCreate = onCreateCategory,
        )
    }

    // ── Diálogo confirmar eliminación ──────────────────────────────────────
    uiState.pendingDeleteCategory?.let { category ->
        AlertDialog(
            onDismissRequest = onDismissDeleteDialog,
            title = { Text(stringResource(R.string.dialog_delete_category_title)) },
            text = {
                Text(
                    stringResource(
                        R.string.dialog_delete_category_message,
                        category.name,
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = onConfirmDelete) {
                    Text(
                        text = stringResource(R.string.dialog_btn_delete),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissDeleteDialog) {
                    Text(stringResource(R.string.dialog_btn_cancel))
                }
            },
        )
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
        modifier = modifier.padding(vertical = SpacingXs),
    )
}

// ---------------------------------------------------------------------------
// Ítem de categoría
// ---------------------------------------------------------------------------

@Composable
private fun CategoryItem(
    category: Category,
    showDelete: Boolean,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(RadiusMd),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SpacingMd, vertical = SpacingSm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Ícono emoji en círculo de fondo
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = category.icon,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }

            Spacer(modifier = Modifier.width(SpacingMd))

            // Nombre y contador
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                val countText = when (category.productCount) {
                    0 -> "0 productos"
                    1 -> stringResource(R.string.categories_label_product_count_single)
                    else -> stringResource(R.string.categories_label_products_count, category.productCount)
                }
                Text(
                    text = countText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // Botón eliminar (solo en categorías del usuario)
            if (showDelete) {
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = stringResource(R.string.cd_delete),
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(IconMd),
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Estado vacío de categorías del usuario
// ---------------------------------------------------------------------------

@Composable
private fun EmptyUserCategories(
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(vertical = SpacingLg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SpacingXs),
    ) {
        Text(
            text = stringResource(R.string.categories_empty_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = stringResource(R.string.categories_empty_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(SpacingXs))
        TextButton(onClick = onCreateClick) {
            Text(stringResource(R.string.categories_btn_new))
        }
    }
}

// ---------------------------------------------------------------------------
// Diálogo crear categoría
// ---------------------------------------------------------------------------

@Composable
private fun CreateCategoryDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit,
) {
    var name by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.new_category_title)) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.new_category_label_name)) },
                placeholder = { Text(stringResource(R.string.new_category_hint_name)) },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done,
                ),
                singleLine = true,
                shape = RoundedCornerShape(TextFieldRadius),
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onCreate(name) },
            ) {
                Text(stringResource(R.string.new_category_btn_create))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_btn_cancel))
            }
        },
    )
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@Preview(showSystemUi = true)
@Composable
private fun CategoriesScreenPreview() {
    ToolistTheme {
        CategoriesScreen(
            uiState = CategoriesUiState(
                isLoading = false,
                userCategories = listOf(
                    Category(id = "1", name = "Granos", icon = "🫘", isSystem = false, productCount = 3),
                ),
                systemCategories = listOf(
                    Category(id = "sys_0", name = "Lácteos", icon = "🥛", isSystem = true, productCount = 5),
                    Category(id = "sys_1", name = "Carnes", icon = "🥩", isSystem = true, productCount = 2),
                ),
            ),
            onNavigateBack = {},
            onShowCreateDialog = {},
            onDismissCreateDialog = {},
            onCreateCategory = {},
            onRequestDelete = {},
            onDismissDeleteDialog = {},
            onConfirmDelete = {},
            onErrorShown = {},
            onSnackShown = {},
        )
    }
}
