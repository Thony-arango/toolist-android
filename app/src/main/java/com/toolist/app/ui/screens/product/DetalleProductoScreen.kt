package com.toolist.app.ui.screens.product

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.BrokenImage
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.automirrored.rounded.FormatListBulleted
import androidx.compose.material.icons.automirrored.rounded.Notes
import androidx.compose.material.icons.rounded.PriceCheck
import androidx.compose.material.icons.rounded.Scale
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.toolist.app.R
import com.toolist.app.domain.model.Product
import com.toolist.app.domain.model.ProductStatus
import com.toolist.app.ui.theme.ButtonHeight
import com.toolist.app.ui.theme.ButtonRadius
import com.toolist.app.ui.theme.Green100
import com.toolist.app.ui.theme.Green700
import com.toolist.app.ui.theme.IconLg
import com.toolist.app.ui.theme.IconMd
import com.toolist.app.ui.theme.ProductImageSizeLg
import com.toolist.app.ui.theme.RadiusMd
import com.toolist.app.ui.theme.SpacingLg
import com.toolist.app.ui.theme.SpacingMd
import com.toolist.app.ui.theme.SpacingSm
import com.toolist.app.ui.theme.SpacingXs
import com.toolist.app.ui.theme.ToolistTheme
import com.toolist.app.ui.theme.WarningOrange
import com.toolist.app.ui.theme.WarningOrangeLight

// Pantalla

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(
    uiState: DetalleProductoUiState,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: () -> Unit,
    onToggleStatus: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) snackbarHostState.showSnackbar(uiState.error)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.product_detail_title),
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
                actions = {
                    IconButton(onClick = onNavigateToEdit) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = stringResource(R.string.cd_edit),
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
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.product == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.loading_generic),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            else -> {
                ProductDetailContent(
                    product = uiState.product,
                    listName = uiState.listName,
                    onToggleStatus = onToggleStatus,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .navigationBarsPadding(),
                )
            }
        }
    }
}

// Contenido principal

@Composable
private fun ProductDetailContent(
    product: Product,
    listName: String,
    onToggleStatus: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState()),
    ) {
        ProductImage(
            imageUrl = product.imageUrl,
            productName = product.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(ProductImageSizeLg)
                .padding(horizontal = SpacingMd)
                .clip(RoundedCornerShape(RadiusMd)),
        )

        Spacer(modifier = Modifier.height(SpacingMd))

        Column(
            modifier = Modifier.padding(horizontal = SpacingMd),
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.height(SpacingXs))

            Row(
                horizontalArrangement = Arrangement.spacedBy(SpacingSm),
            ) {
                if (product.categoryName != null) {
                    SuggestionChip(
                        onClick = {},
                        label = { Text(product.categoryName, style = MaterialTheme.typography.labelMedium) },
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.Category,
                                contentDescription = null,
                                modifier = Modifier.size(IconMd),
                            )
                        },
                    )
                }

                val isPurchased = product.status == ProductStatus.PURCHASED
                SuggestionChip(
                    onClick = {},
                    label = {
                        Text(
                            text = if (isPurchased)
                                stringResource(R.string.add_product_state_purchased)
                            else
                                stringResource(R.string.add_product_state_pending),
                            style = MaterialTheme.typography.labelMedium,
                        )
                    },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = if (isPurchased) Green100 else WarningOrangeLight,
                        labelColor = if (isPurchased) Green700 else WarningOrange,
                        iconContentColor = if (isPurchased) Green700 else WarningOrange,
                    ),
                )
            }

            Spacer(modifier = Modifier.height(SpacingMd))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(SpacingMd))

            Text(
                text = stringResource(R.string.product_detail_section_info),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
            )

            Spacer(modifier = Modifier.height(SpacingMd))

            // Lista
            if (listName.isNotEmpty()) {
                InfoRow(
                    icon = Icons.AutoMirrored.Rounded.FormatListBulleted,
                    label = stringResource(R.string.product_detail_label_list),
                    value = listName,
                )
            }

            // Cantidad
            InfoRow(
                icon = Icons.Rounded.Scale,
                label = stringResource(R.string.product_detail_label_quantity),
                value = formatQuantity(product.quantity, product.unit),
            )

            // Precio estimado
            if (product.estimatedPrice > 0) {
                InfoRow(
                    icon = Icons.Rounded.PriceCheck,
                    label = stringResource(R.string.product_detail_label_price),
                    value = formatPrice(product.estimatedPrice),
                )
            }

            // Notas
            if (!product.notes.isNullOrEmpty()) {
                InfoRow(
                    icon = Icons.AutoMirrored.Rounded.Notes,
                    label = stringResource(R.string.product_detail_label_notes),
                    value = product.notes,
                )
            }

            Spacer(modifier = Modifier.height(SpacingLg))

            Button(
                onClick = onToggleStatus,
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
                    text = if (product.status == ProductStatus.PURCHASED)
                        stringResource(R.string.product_detail_btn_mark_pending)
                    else
                        stringResource(R.string.product_detail_btn_mark_purchased),
                    style = MaterialTheme.typography.labelLarge,
                )
            }

            Spacer(modifier = Modifier.height(SpacingLg))
        }
    }
}

// Imagen del producto

@Composable
private fun ProductImage(
    imageUrl: String?,
    productName: String,
    modifier: Modifier = Modifier,
) {
    if (imageUrl != null) {
        AsyncImage(
            model = imageUrl,
            contentDescription = stringResource(R.string.cd_product_image),
            contentScale = ContentScale.Crop,
            modifier = modifier,
        )
    } else {
        Box(
            modifier = modifier
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.BrokenImage,
                contentDescription = stringResource(R.string.cd_product_image),
                modifier = Modifier.size(IconLg),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// Fila de información

@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = SpacingXs),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(SpacingSm),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .size(IconMd)
                .padding(top = 2.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}

// Helpers de formato

private fun formatQuantity(quantity: Double, unit: String): String {
    val qtyStr = if (quantity == quantity.toLong().toDouble()) quantity.toLong().toString() else quantity.toString()
    return "$qtyStr $unit"
}

private fun formatPrice(price: Double): String {
    return "$ ${"%,.0f".format(price)}"
}

// Preview

@Preview(showSystemUi = true)
@Composable
private fun DetalleProductoScreenPreview() {
    ToolistTheme {
        DetalleProductoScreen(
            uiState = DetalleProductoUiState(
                isLoading = false,
                product = Product(
                    id = "p1",
                    listId = "l1",
                    name = "Leche entera",
                    quantity = 2.0,
                    unit = "L",
                    categoryId = null,
                    categoryName = "Lácteos",
                    estimatedPrice = 5200.0,
                    status = ProductStatus.PENDING,
                    notes = "Comprar marca Colanta",
                    imageUrl = null,
                ),
                listName = "Mercado Semanal",
            ),
            onNavigateBack = {},
            onNavigateToEdit = {},
            onToggleStatus = {},
        )
    }
}
