package com.toolist.app.ui.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.toolist.app.R
import com.toolist.app.domain.model.Product
import com.toolist.app.domain.model.ProductStatus
import com.toolist.app.ui.components.ProductItem
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
// Pantalla
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    uiState: SearchUiState,
    onNavigateBack: () -> Unit,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onSelectRecentSearch: (String) -> Unit,
    onRemoveRecentSearch: (String) -> Unit,
    onClearAllRecent: () -> Unit,
    onSelectCategory: (String?) -> Unit,
    onProductClick: (listId: String, productId: String) -> Unit,
    onToggleProductStatus: (Product) -> Unit,
    onErrorShown: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            snackbarHostState.showSnackbar(uiState.error)
            onErrorShown()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    SearchField(
                        query = uiState.query,
                        onQueryChange = onQueryChange,
                        onSearch = { onSearch(uiState.query) },
                        modifier = Modifier.fillMaxWidth(),
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        if (!uiState.isSearching) {
            // ── Estado inicial: búsquedas recientes ────────────────────────
            RecentSearchesContent(
                recentSearches = uiState.recentSearches,
                onSelectSearch = onSelectRecentSearch,
                onRemoveSearch = onRemoveRecentSearch,
                onClearAll = onClearAllRecent,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .navigationBarsPadding(),
            )
        } else {
            // ── Estado activo: chips + resultados ──────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .navigationBarsPadding(),
            ) {
                // Chips de filtro por categoría
                if (uiState.availableCategories.isNotEmpty()) {
                    CategoryFilterRow(
                        categories = uiState.availableCategories,
                        selectedCategory = uiState.selectedCategory,
                        onSelectCategory = onSelectCategory,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = SpacingMd, vertical = SpacingXs),
                    )
                    HorizontalDivider()
                }

                if (uiState.results.isEmpty()) {
                    EmptySearchState(
                        query = uiState.query,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(SpacingXl),
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        item {
                            Text(
                                text = stringResource(R.string.search_section_results),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(
                                    horizontal = SpacingMd,
                                    vertical = SpacingXs,
                                ),
                            )
                        }
                        items(uiState.results, key = { it.product.id }) { result ->
                            SearchResultItem(
                                result = result,
                                onProductClick = {
                                    onProductClick(result.product.listId, result.product.id)
                                },
                                onToggleStatus = { onToggleProductStatus(result.product) },
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(start = SpacingXl + SpacingMd),
                            )
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Campo de búsqueda
// ---------------------------------------------------------------------------

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text(stringResource(R.string.search_hint)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = stringResource(R.string.cd_search),
                modifier = Modifier.size(IconMd),
            )
        },
        trailingIcon = if (query.isNotEmpty()) {
            {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Rounded.Cancel,
                        contentDescription = null,
                        modifier = Modifier.size(IconMd),
                    )
                }
            }
        } else null,
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            imeAction = androidx.compose.ui.text.input.ImeAction.Search,
        ),
        keyboardActions = androidx.compose.foundation.text.KeyboardActions(
            onSearch = { onSearch() },
        ),
        singleLine = true,
        shape = RoundedCornerShape(TextFieldRadius),
    )
}

// ---------------------------------------------------------------------------
// Fila de chips de categoría
// ---------------------------------------------------------------------------

@Composable
private fun CategoryFilterRow(
    categories: List<String>,
    selectedCategory: String?,
    onSelectCategory: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(SpacingXs),
    ) {
        FilterChip(
            selected = selectedCategory == null,
            onClick = { onSelectCategory(null) },
            label = { Text(stringResource(R.string.search_filter_all)) },
        )
        categories.forEach { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onSelectCategory(if (selectedCategory == category) null else category) },
                label = { Text(category) },
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Ítem de resultado con nombre de lista
// ---------------------------------------------------------------------------

@Composable
private fun SearchResultItem(
    result: SearchResult,
    onProductClick: () -> Unit,
    onToggleStatus: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        ProductItem(
            product = result.product,
            onClick = onProductClick,
            onToggleStatus = onToggleStatus,
        )
        Text(
            text = result.listName,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = SpacingXl + SpacingMd, bottom = SpacingXs),
        )
    }
}

// ---------------------------------------------------------------------------
// Contenido: búsquedas recientes
// ---------------------------------------------------------------------------

@Composable
private fun RecentSearchesContent(
    recentSearches: List<String>,
    onSelectSearch: (String) -> Unit,
    onRemoveSearch: (String) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (recentSearches.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.search_empty_hint),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(SpacingXl),
            )
        }
    } else {
        LazyColumn(modifier = modifier) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = SpacingMd, vertical = SpacingXs),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.search_section_recent),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    TextButton(onClick = onClearAll) {
                        Text(
                            text = stringResource(R.string.search_clear_recent),
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                }
            }

            items(recentSearches) { query ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectSearch(query) }
                        .padding(horizontal = SpacingMd, vertical = SpacingSm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.History,
                        contentDescription = null,
                        modifier = Modifier.size(IconMd),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = query,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = SpacingMd),
                    )
                    IconButton(onClick = { onRemoveSearch(query) }) {
                        Icon(
                            imageVector = Icons.Rounded.Cancel,
                            contentDescription = null,
                            modifier = Modifier.size(IconMd),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Estado sin resultados
// ---------------------------------------------------------------------------

@Composable
private fun EmptySearchState(
    query: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Rounded.Search,
            contentDescription = null,
            modifier = Modifier.size(IconLg),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(SpacingMd))
        Text(
            text = stringResource(R.string.search_no_results_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(SpacingXs))
        Text(
            text = stringResource(R.string.search_no_results_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

// ---------------------------------------------------------------------------
// Preview
// ---------------------------------------------------------------------------

@Preview(showSystemUi = true)
@Composable
private fun SearchScreenEmptyPreview() {
    ToolistTheme {
        SearchScreen(
            uiState = SearchUiState(
                recentSearches = listOf("Leche", "Pollo", "Arroz"),
            ),
            onNavigateBack = {},
            onQueryChange = {},
            onSearch = {},
            onSelectRecentSearch = {},
            onRemoveRecentSearch = {},
            onClearAllRecent = {},
            onSelectCategory = {},
            onProductClick = { _, _ -> },
            onToggleProductStatus = {},
            onErrorShown = {},
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun SearchScreenResultsPreview() {
    ToolistTheme {
        SearchScreen(
            uiState = SearchUiState(
                query = "Leche",
                isSearching = true,
                availableCategories = listOf("Lácteos", "Bebidas"),
                results = listOf(
                    SearchResult(
                        product = Product(
                            id = "1", listId = "l1", name = "Leche entera", quantity = 2.0,
                            unit = "L", categoryId = null, categoryName = "Lácteos",
                            estimatedPrice = 4800.0, status = ProductStatus.PENDING, notes = null, imageUrl = null,
                        ),
                        listName = "Mercado Semanal",
                    ),
                ),
            ),
            onNavigateBack = {},
            onQueryChange = {},
            onSearch = {},
            onSelectRecentSearch = {},
            onRemoveRecentSearch = {},
            onClearAllRecent = {},
            onSelectCategory = {},
            onProductClick = { _, _ -> },
            onToggleProductStatus = {},
            onErrorShown = {},
        )
    }
}
