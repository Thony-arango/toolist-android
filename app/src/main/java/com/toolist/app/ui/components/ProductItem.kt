package com.toolist.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.toolist.app.R
import com.toolist.app.domain.model.Product
import com.toolist.app.domain.model.ProductStatus
import com.toolist.app.ui.theme.CardMinHeight
import com.toolist.app.ui.theme.IconMd
import com.toolist.app.ui.theme.RadiusSm
import com.toolist.app.ui.theme.SpacingMd
import com.toolist.app.ui.theme.SpacingXs
import com.toolist.app.ui.theme.SpacingXxs
import com.toolist.app.ui.theme.ToolistTheme
import com.toolist.app.ui.theme.WarningOrange
import com.toolist.app.ui.theme.WarningOrangeLight

@Composable
fun ProductItem(
    product: Product,
    onClick: () -> Unit,
    onToggleStatus: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isPurchased = product.status == ProductStatus.PURCHASED
    val contentAlpha = if (isPurchased) 0.55f else 1f

    Row(
        modifier = modifier
            .fillMaxWidth()
            .size(height = CardMinHeight, width = CardMinHeight) // ensures min height
            .clickable(onClick = onClick)
            .padding(horizontal = SpacingMd, vertical = SpacingXs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onToggleStatus, modifier = Modifier.size(IconMd + SpacingXs)) {
            Icon(
                imageVector = if (isPurchased) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                contentDescription = stringResource(
                    if (isPurchased) R.string.cd_mark_pending else R.string.cd_mark_purchased,
                ),
                tint = if (isPurchased) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(IconMd),
            )
        }

        Spacer(modifier = Modifier.width(SpacingXs))

        Column(
            modifier = Modifier
                .weight(1f)
                .alpha(contentAlpha),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    textDecoration = if (isPurchased) TextDecoration.LineThrough else TextDecoration.None,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(SpacingXxs),
            ) {
                if (product.categoryName != null) {
                    CategoryBadge(label = product.categoryName)
                }
                val qtyText = buildString {
                    append(formatQuantity(product.quantity))
                    append(" ")
                    append(product.unit)
                }
                Text(
                    text = qtyText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                // Badge de estado
                StatusBadge(status = product.status)
            }
        }

        Spacer(modifier = Modifier.width(SpacingXs))

        if (product.estimatedPrice > 0) {
            Text(
                text = product.estimatedPrice.formatAsCurrency(),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = contentAlpha),
            )
        }
    }
}

@Composable
fun StatusBadge(status: ProductStatus, modifier: Modifier = Modifier) {
    val isComprado = status == ProductStatus.PURCHASED
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(RadiusSm),
        color = if (isComprado) MaterialTheme.colorScheme.primaryContainer else WarningOrangeLight,
    ) {
        Text(
            text = stringResource(
                if (isComprado) R.string.add_product_state_purchased else R.string.add_product_state_pending,
            ),
            style = MaterialTheme.typography.labelMedium,
            color = if (isComprado) MaterialTheme.colorScheme.primary else WarningOrange,
            modifier = Modifier.padding(horizontal = SpacingXs, vertical = SpacingXxs),
        )
    }
}

@Composable
fun CategoryBadge(label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(RadiusSm),
        color = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = SpacingXxs + SpacingXxs, vertical = SpacingXxs),
        )
    }
}

private fun formatQuantity(qty: Double): String =
    if (qty == qty.toLong().toDouble()) qty.toLong().toString() else qty.toString()

@Preview(showBackground = true)
@Composable
private fun ProductItemPendingPreview() {
    ToolistTheme {
        ProductItem(
            product = Product(
                id = "1", listId = "l1", name = "Leche entera", quantity = 2.0,
                unit = "L", categoryId = null, categoryName = "Lácteos",
                estimatedPrice = 4800.0, status = ProductStatus.PENDING, notes = null, imageUrl = null,
            ),
            onClick = {}, onToggleStatus = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductItemPurchasedPreview() {
    ToolistTheme {
        ProductItem(
            product = Product(
                id = "2", listId = "l1", name = "Pollo", quantity = 1.0,
                unit = "kg", categoryId = null, categoryName = "Carnes",
                estimatedPrice = 12500.0, status = ProductStatus.PURCHASED, notes = null, imageUrl = null,
            ),
            onClick = {}, onToggleStatus = {},
        )
    }
}
