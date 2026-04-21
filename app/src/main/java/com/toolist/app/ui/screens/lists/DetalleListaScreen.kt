package com.toolist.app.ui.screens.lists

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DriveFileRenameOutline
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.MoveDown
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.TaskAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.toolist.app.R
import com.toolist.app.domain.model.Product
import com.toolist.app.domain.model.ProductStatus
import com.toolist.app.domain.model.ShoppingList
import com.toolist.app.ui.components.ProductItem
import com.toolist.app.ui.components.formatAsCurrency
import com.toolist.app.ui.theme.BottomSheetCornerRadius
import com.toolist.app.ui.theme.BottomSheetHandleHeight
import com.toolist.app.ui.theme.BottomSheetHandleWidth
import com.toolist.app.ui.theme.BottomSheetItemHeight
import com.toolist.app.ui.theme.ButtonHeight
import com.toolist.app.ui.theme.ButtonRadius
import com.toolist.app.ui.theme.IconLg
import com.toolist.app.ui.theme.IconMd
import com.toolist.app.ui.theme.ProgressBarHeight
import com.toolist.app.ui.theme.ProgressBarRadius
import com.toolist.app.ui.theme.SpacingLg
import com.toolist.app.ui.theme.SpacingMd
import com.toolist.app.ui.theme.SpacingSm
import com.toolist.app.ui.theme.SpacingXl
import com.toolist.app.ui.theme.SpacingXs
import com.toolist.app.ui.theme.SpacingXxs
import com.toolist.app.ui.theme.ToolistTheme

// Pantalla

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleListaScreen(
    uiState: DetalleListaUiState,
    onNavigateBack: () -> Unit,
    onNavigateToAddProduct: () -> Unit,
    onToggleProductStatus: (Product) -> Unit,
    onDeleteProduct: (Product) -> Unit,
    onDuplicateProduct: (Product) -> Unit,
    onDeleteList: () -> Unit,
    onDuplicateList: () -> Unit,
    onResetList: () -> Unit,
    onTabSelected: (String) -> Unit,
    onErrorShown: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listColor = remember(uiState.list?.colorHex) {
        runCatching { Color(android.graphics.Color.parseColor(uiState.list?.colorHex ?: "#16A34A")) }
            .getOrDefault(Color(0xFF16A34A))
    }
    val snackbarHostState = remember { SnackbarHostState() }

    // Bottom sheet state
    var showListSheet by rememberSaveable { mutableStateOf(false) }
    var showProductSheet by rememberSaveable { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var showDeleteListDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteProductDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            snackbarHostState.showSnackbar(uiState.error)
            onErrorShown()
        }
    }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddProduct,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = SpacingMd),
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(IconMd))
                    Spacer(modifier = Modifier.size(SpacingXs))
                    Text(
                        text = stringResource(R.string.list_detail_btn_add_product),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()),
        ) {
            ListHeader(
                list = uiState.list,
                listColor = listColor,
                onNavigateBack = onNavigateBack,
                onShowOptions = { showListSheet = true },
            )

            if (uiState.tabs.size > 1) {
                CategoryTabsRow(
                    tabs = uiState.tabs,
                    selectedTab = uiState.selectedTab,
                    onTabSelected = onTabSelected,
                )
            }

            when {
                uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    androidx.compose.material3.CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }

                uiState.filteredProducts.isEmpty() && !uiState.isLoading ->
                    EmptyListContent(onAddProduct = onNavigateToAddProduct)

                else -> ProductsContent(
                    products = uiState.filteredProducts,
                    isCompleted = uiState.isCompleted,
                    list = uiState.list,
                    onToggleStatus = onToggleProductStatus,
                    onProductClick = { product ->
                        selectedProduct = product
                        showProductSheet = true
                    },
                    onResetList = onResetList,
                )
            }
        }
    }

    if (showListSheet) {
        ModalBottomSheet(
            onDismissRequest = { showListSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            shape = RoundedCornerShape(topStart = BottomSheetCornerRadius, topEnd = BottomSheetCornerRadius),
        ) {
            ListOptionsSheet(
                onDuplicate = {
                    showListSheet = false
                    onDuplicateList()
                },
                onDelete = {
                    showListSheet = false
                    showDeleteListDialog = true
                },
            )
        }
    }

    if (showProductSheet && selectedProduct != null) {
        val product = selectedProduct!!
        ModalBottomSheet(
            onDismissRequest = { showProductSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            shape = RoundedCornerShape(topStart = BottomSheetCornerRadius, topEnd = BottomSheetCornerRadius),
        ) {
            ProductOptionsSheet(
                product = product,
                onToggleStatus = {
                    showProductSheet = false
                    onToggleProductStatus(product)
                },
                onDuplicate = {
                    showProductSheet = false
                    onDuplicateProduct(product)
                },
                onDelete = {
                    showProductSheet = false
                    showDeleteProductDialog = true
                },
            )
        }
    }

    if (showDeleteListDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteListDialog = false },
            title = { Text(stringResource(R.string.dialog_delete_list_title)) },
            text = {
                Text(
                    stringResource(
                        R.string.dialog_delete_list_message,
                        uiState.list?.name ?: "",
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteListDialog = false
                        onDeleteList()
                    }
                ) {
                    Text(stringResource(R.string.dialog_btn_delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteListDialog = false }) {
                    Text(stringResource(R.string.dialog_btn_cancel))
                }
            },
        )
    }

    if (showDeleteProductDialog && selectedProduct != null) {
        val product = selectedProduct!!
        AlertDialog(
            onDismissRequest = { showDeleteProductDialog = false },
            title = { Text(stringResource(R.string.dialog_delete_product_title)) },
            text = { Text(stringResource(R.string.dialog_delete_product_message, product.name)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteProductDialog = false
                        selectedProduct = null
                        onDeleteProduct(product)
                    }
                ) {
                    Text(stringResource(R.string.dialog_btn_delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteProductDialog = false }) {
                    Text(stringResource(R.string.dialog_btn_cancel))
                }
            },
        )
    }
}

// Header verde

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListHeader(
    list: ShoppingList?,
    listColor: Color,
    onNavigateBack: () -> Unit,
    onShowOptions: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(listColor),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(bottom = SpacingMd),
        ) {
            // Back + title + overflow
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(R.string.cd_back),
                        tint = Color.White,
                    )
                }
                Text(
                    text = list?.name ?: "",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                )
                IconButton(onClick = onShowOptions) {
                    Icon(
                        Icons.Rounded.MoreVert,
                        contentDescription = stringResource(R.string.cd_more_options),
                        tint = Color.White,
                    )
                }
            }

            if (list != null) {
                // Progreso
                Text(
                    text = stringResource(
                        R.string.list_detail_progress,
                        list.purchasedCount,
                        list.totalCount,
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.padding(horizontal = SpacingMd),
                )
                Spacer(modifier = Modifier.height(SpacingXxs))

                // Barra de progreso
                LinearProgressIndicator(
                    progress = { list.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = SpacingMd)
                        .height(ProgressBarHeight)
                        .clip(RoundedCornerShape(ProgressBarRadius)),
                    color = Color.White,
                    trackColor = MaterialTheme.colorScheme.primaryContainer,
                )
                Spacer(modifier = Modifier.height(SpacingMd))

                // Total
                Column(modifier = Modifier.padding(horizontal = SpacingMd)) {
                    Text(
                        text = stringResource(R.string.list_detail_total_label),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.75f),
                    )
                    Text(
                        text = list.totalEstimated.formatAsCurrency(),
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                    )
                }
            }
        }
    }
}

// Tabs de categorías

@Composable
private fun CategoryTabsRow(
    tabs: List<String>,
    selectedTab: String,
    onTabSelected: (String) -> Unit,
) {
    val selectedIndex = tabs.indexOf(selectedTab).coerceAtLeast(0)
    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        edgePadding = SpacingMd,
    ) {
        tabs.forEachIndexed { index, tab ->
            Tab(
                selected = index == selectedIndex,
                onClick = { onTabSelected(tab) },
                text = {
                    Text(
                        text = tab,
                        style = MaterialTheme.typography.labelLarge,
                    )
                },
            )
        }
    }
}

// Contenido: lista de productos

@Composable
private fun ProductsContent(
    products: List<Product>,
    isCompleted: Boolean,
    list: ShoppingList?,
    onToggleStatus: (Product) -> Unit,
    onProductClick: (Product) -> Unit,
    onResetList: () -> Unit,
) {
    val pending = products.filter { it.status == ProductStatus.PENDING }
    val purchased = products.filter { it.status == ProductStatus.PURCHASED }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
    ) {
        // Banner de lista completada
        if (isCompleted) {
            item {
                CompletedBanner(
                    totalEstimated = list?.totalEstimated ?: 0.0,
                    onResetList = onResetList,
                )
            }
        }

        // Sección PENDIENTES
        if (pending.isNotEmpty()) {
            item {
                SectionHeader(
                    title = stringResource(R.string.list_detail_section_pending, pending.size),
                    modifier = Modifier.padding(horizontal = SpacingMd, vertical = SpacingXs),
                )
            }
            items(pending, key = { it.id }) { product ->
                ProductItem(
                    product = product,
                    onClick = { onProductClick(product) },
                    onToggleStatus = { onToggleStatus(product) },
                )
                HorizontalDivider(
                    modifier = Modifier.padding(start = 56.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                )
            }
        }

        // Sección COMPRADOS
        if (purchased.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(SpacingXs))
                SectionHeader(
                    title = stringResource(R.string.list_detail_section_purchased, purchased.size),
                    modifier = Modifier.padding(horizontal = SpacingMd, vertical = SpacingXs),
                )
            }
            items(purchased, key = { it.id }) { product ->
                ProductItem(
                    product = product,
                    onClick = { onProductClick(product) },
                    onToggleStatus = { onToggleStatus(product) },
                )
                HorizontalDivider(
                    modifier = Modifier.padding(start = 56.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                )
            }
        }

        // Espacio para el FAB
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier,
    )
}

// Banner de lista completada

@Composable
private fun CompletedBanner(
    totalEstimated: Double,
    onResetList: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SpacingMd),
        shape = RoundedCornerShape(ButtonRadius),
        color = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Column(
            modifier = Modifier.padding(SpacingMd),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.list_detail_completed_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(SpacingXxs))
            Text(
                text = stringResource(R.string.list_detail_completed_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.75f),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(SpacingXs))
            Text(
                text = totalEstimated.formatAsCurrency(),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.secondary,
            )
            Spacer(modifier = Modifier.height(SpacingMd))
            OutlinedButton(
                onClick = onResetList,
                shape = RoundedCornerShape(ButtonRadius),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.5.dp,
                    color = MaterialTheme.colorScheme.secondary,
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.secondary,
                ),
            ) {
                Icon(
                    Icons.Rounded.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(IconMd),
                )
                Spacer(modifier = Modifier.size(SpacingXxs))
                Text(
                    text = stringResource(R.string.list_detail_btn_reset),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

// Estado vacío

@Composable
private fun EmptyListContent(onAddProduct: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(SpacingXl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            Icons.Rounded.ShoppingCart,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(IconLg),
        )
        Spacer(modifier = Modifier.height(SpacingMd))
        Text(
            text = stringResource(R.string.list_detail_empty_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(SpacingXs))
        Text(
            text = stringResource(R.string.list_detail_empty_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(SpacingXl))
        TextButton(onClick = onAddProduct) {
            Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(IconMd))
            Spacer(modifier = Modifier.size(SpacingXxs))
            Text(
                text = stringResource(R.string.list_detail_btn_add_first_product),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

// Bottom sheet — opciones de lista

@Composable
private fun ListOptionsSheet(
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = SpacingMd),
    ) {
        SheetHandle()
        SheetTitle(stringResource(R.string.list_options_title))
        SheetItem(Icons.Rounded.ContentCopy, stringResource(R.string.list_options_duplicate), onClick = onDuplicate)
        SheetItem(
            Icons.Rounded.Delete,
            stringResource(R.string.list_options_delete),
            tint = MaterialTheme.colorScheme.error,
            onClick = onDelete,
        )
    }
}

// Bottom sheet — opciones de producto

@Composable
private fun ProductOptionsSheet(
    product: Product,
    onToggleStatus: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
) {
    val isPurchased = product.status == ProductStatus.PURCHASED
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = SpacingMd),
    ) {
        SheetHandle()
        SheetTitle(stringResource(R.string.product_options_title))
        SheetItem(
            icon = Icons.Rounded.TaskAlt,
            label = if (isPurchased) stringResource(R.string.product_options_mark_pending)
            else stringResource(R.string.product_options_mark_purchased),
            onClick = onToggleStatus,
        )
        SheetItem(Icons.Rounded.ContentCopy, stringResource(R.string.product_options_duplicate), onClick = onDuplicate)
        SheetItem(
            Icons.Rounded.Delete,
            stringResource(R.string.product_options_delete),
            tint = MaterialTheme.colorScheme.error,
            onClick = onDelete,
        )
    }
}

// Helpers de bottom sheet

@Composable
private fun SheetHandle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SpacingMd),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(width = BottomSheetHandleWidth, height = BottomSheetHandleHeight)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.outline),
        )
    }
}

@Composable
private fun SheetTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(horizontal = SpacingMd, vertical = SpacingXs),
    )
}

@Composable
private fun SheetItem(
    icon: ImageVector,
    label: String,
    tint: Color = Color.Unspecified,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(BottomSheetItemHeight)
            .clickable(onClick = onClick)
            .padding(horizontal = SpacingMd),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (tint == Color.Unspecified) MaterialTheme.colorScheme.onSurface else tint,
            modifier = Modifier.size(IconMd),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (tint == Color.Unspecified) MaterialTheme.colorScheme.onSurface else tint,
        )
    }
}

// Preview

@Preview(showSystemUi = true)
@Composable
private fun DetalleListaScreenPreview() {
    ToolistTheme {
        DetalleListaScreen(
            uiState = DetalleListaUiState(
                isLoading = false,
                list = ShoppingList("1", "Mercado semanal", "", "#F97316", 42500.0, 3, 8, 0L),
                products = listOf(
                    Product("p1", "1", "Leche entera", 2.0, "L", null, "Lácteos", 4800.0, ProductStatus.PENDING, null, null),
                    Product("p2", "1", "Pollo", 1.0, "kg", null, "Carnes", 12500.0, ProductStatus.PENDING, null, null),
                    Product("p3", "1", "Jabón de manos", 1.0, "Unidad", null, null, 8200.0, ProductStatus.PURCHASED, null, null),
                ),
                filteredProducts = listOf(
                    Product("p1", "1", "Leche entera", 2.0, "L", null, "Lácteos", 4800.0, ProductStatus.PENDING, null, null),
                    Product("p2", "1", "Pollo", 1.0, "kg", null, "Carnes", 12500.0, ProductStatus.PENDING, null, null),
                    Product("p3", "1", "Jabón de manos", 1.0, "Unidad", null, null, 8200.0, ProductStatus.PURCHASED, null, null),
                ),
                tabs = listOf("Todos", "Lácteos", "Carnes"),
                selectedTab = "Todos",
            ),
            onNavigateBack = {},
            onNavigateToAddProduct = {},
            onToggleProductStatus = {},
            onDeleteProduct = {},
            onDuplicateProduct = {},
            onDeleteList = {},
            onDuplicateList = {},
            onResetList = {},
            onTabSelected = {},
            onErrorShown = {},
        )
    }
}
