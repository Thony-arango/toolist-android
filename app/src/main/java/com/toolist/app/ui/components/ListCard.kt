package com.toolist.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.toolist.app.R
import com.toolist.app.domain.model.ShoppingList
import com.toolist.app.ui.theme.CardElevation
import com.toolist.app.ui.theme.ProgressBarHeight
import com.toolist.app.ui.theme.ProgressBarRadius
import com.toolist.app.ui.theme.RadiusLg
import com.toolist.app.ui.theme.SpacingMd
import com.toolist.app.ui.theme.SpacingSm
import com.toolist.app.ui.theme.SpacingXxs
import com.toolist.app.ui.theme.ToolistTheme
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ListCard(
    list: ShoppingList,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listColor = remember(list.colorHex) {
        runCatching { Color(android.graphics.Color.parseColor(list.colorHex)) }
            .getOrDefault(Color(0xFF16A34A))
    }
    val pendingCount = list.totalCount - list.purchasedCount

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(RadiusLg),
        elevation = CardDefaults.cardElevation(defaultElevation = CardElevation),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SpacingMd, vertical = SpacingSm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // ── Punto de color ────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(listColor),
            )

            // ── Nombre, contador y barra de progreso ──────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = SpacingMd),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = list.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                )
                Spacer(modifier = Modifier.height(SpacingXxs))
                Text(
                    text = stringResource(
                        R.string.my_lists_items_count,
                        list.totalCount,
                        pendingCount.coerceAtLeast(0),
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (list.totalCount > 0) {
                    Spacer(modifier = Modifier.height(SpacingXxs))
                    LinearProgressIndicator(
                        progress = { list.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(ProgressBarHeight)
                            .clip(RoundedCornerShape(ProgressBarRadius)),
                        color = listColor,
                        trackColor = MaterialTheme.colorScheme.outline,
                    )
                }
            }

            // ── Precio ────────────────────────────────────────────────────
            Text(
                text = list.totalEstimated.formatAsCurrency(),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Formato de precio (peso colombiano: $87.300)
// ---------------------------------------------------------------------------

fun Double.formatAsCurrency(): String {
    val formatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("es-CO"))
    formatter.maximumFractionDigits = 0
    return "$${formatter.format(this.toLong())}"
}

// ---------------------------------------------------------------------------
// Preview
// ---------------------------------------------------------------------------

@Preview(showBackground = true)
@Composable
private fun ListCardPreview() {
    ToolistTheme {
        ListCard(
            list = ShoppingList(
                id = "1",
                name = "Mercado semanal",
                description = "",
                colorHex = "#F97316",
                totalEstimated = 42500.0,
                purchasedCount = 3,
                totalCount = 8,
                createdAt = 0L,
            ),
            onClick = {},
            modifier = Modifier.padding(SpacingMd),
        )
    }
}
