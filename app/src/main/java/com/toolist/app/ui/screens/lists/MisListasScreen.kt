package com.toolist.app.ui.screens.lists

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.toolist.app.R
import com.toolist.app.domain.model.ShoppingList
import com.toolist.app.ui.components.ListCard
import com.toolist.app.ui.components.ToolistBottomNavBar
import com.toolist.app.ui.components.ToolistTab
import com.toolist.app.ui.components.formatAsCurrency
import com.toolist.app.ui.theme.AvatarMd
import com.toolist.app.ui.theme.ButtonHeight
import com.toolist.app.ui.theme.ButtonRadius
import com.toolist.app.ui.theme.IconLg
import com.toolist.app.ui.theme.RadiusLg
import com.toolist.app.ui.theme.RadiusMd
import com.toolist.app.ui.theme.SpacingLg
import com.toolist.app.ui.theme.SpacingMd
import com.toolist.app.ui.theme.SpacingSm
import com.toolist.app.ui.theme.SpacingXl
import com.toolist.app.ui.theme.SpacingXs
import com.toolist.app.ui.theme.SpacingXxs
import com.toolist.app.ui.theme.ToolistTheme

// ---------------------------------------------------------------------------
// Pantalla
// ---------------------------------------------------------------------------

@Composable
fun MisListasScreen(
    uiState: MisListasUiState,
    onNavigateToNewList: () -> Unit,
    onNavigateToListDetail: (String) -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToCredits: () -> Unit,
    onDeleteList: (String) -> Unit,
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
            MisListasTopBar(
                userName = uiState.userName,
            )
        },
        bottomBar = {
            MisListasBottomNav(
                onNavigateToSearch = onNavigateToSearch,
                onNavigateToSettings = onNavigateToSettings,
                onNavigateToCredits = onNavigateToCredits,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        when {
            uiState.isLoading -> SkeletonContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = SpacingMd),
            )

            uiState.lists.isEmpty() -> EmptyContent(
                onCreateList = onNavigateToNewList,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )

            else -> ListsContent(
                uiState = uiState,
                onNavigateToNewList = onNavigateToNewList,
                onNavigateToListDetail = onNavigateToListDetail,
                onNavigateToCategories = onNavigateToCategories,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
        }
    }
}

// ---------------------------------------------------------------------------
// TopAppBar
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MisListasTopBar(userName: String) {
    TopAppBar(
        title = {
            if (userName.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.my_lists_greeting, userName),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            } else {
                Text(
                    text = stringResource(R.string.my_lists_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        },
        actions = {
            if (userName.isNotEmpty()) {
                UserAvatar(
                    initials = userName.take(2).uppercase(),
                    modifier = Modifier.padding(end = SpacingSm),
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
    )
}

// ---------------------------------------------------------------------------
// Avatar de usuario
// ---------------------------------------------------------------------------

@Composable
private fun UserAvatar(initials: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.size(AvatarMd),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = initials,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Bottom Navigation
// ---------------------------------------------------------------------------

@Composable
private fun MisListasBottomNav(
    onNavigateToSearch: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToCredits: () -> Unit,
) {
    ToolistBottomNavBar(
        activeTab = ToolistTab.HOME,
        onNavigateToHome = { /* ya estamos aquí */ },
        onNavigateToSearch = onNavigateToSearch,
        onNavigateToSettings = onNavigateToSettings,
        onNavigateToCredits = onNavigateToCredits,
    )
}

// ---------------------------------------------------------------------------
// Contenido: listas
// ---------------------------------------------------------------------------

@Composable
private fun ListsContent(
    uiState: MisListasUiState,
    onNavigateToNewList: () -> Unit,
    onNavigateToListDetail: (String) -> Unit,
    onNavigateToCategories: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = SpacingMd),
        verticalArrangement = Arrangement.spacedBy(SpacingXs),
    ) {
        // ── Card total ────────────────────────────────────────────────────
        item {
            Spacer(modifier = Modifier.height(SpacingXs))
            TotalCard(
                totalEstimated = uiState.totalEstimated,
                listCount = uiState.lists.size,
                productCount = uiState.totalProducts,
            )
            Spacer(modifier = Modifier.height(SpacingLg))
        }

        // ── Encabezado sección ────────────────────────────────────────────
        item {
            Text(
                text = stringResource(R.string.my_lists_section_active),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(SpacingXs))
        }

        // ── Tarjetas de lista ─────────────────────────────────────────────
        items(uiState.lists, key = { it.id }) { list ->
            ListCard(
                list = list,
                onClick = { onNavigateToListDetail(list.id) },
            )
        }

        // ── Acciones ──────────────────────────────────────────────────────
        item {
            Spacer(modifier = Modifier.height(SpacingMd))
            Button(
                onClick = onNavigateToNewList,
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
                    text = stringResource(R.string.my_lists_btn_new_list),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
            Spacer(modifier = Modifier.height(SpacingXs))
            OutlinedButton(
                onClick = onNavigateToCategories,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ButtonHeight),
                shape = RoundedCornerShape(ButtonRadius),
            ) {
                Text(
                    text = stringResource(R.string.my_lists_btn_manage_categories),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(modifier = Modifier.height(SpacingLg))
        }
    }
}

// ---------------------------------------------------------------------------
// Card de total
// ---------------------------------------------------------------------------

@Composable
private fun TotalCard(
    totalEstimated: Double,
    listCount: Int,
    productCount: Int,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(RadiusMd),
        color = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Column(
            modifier = Modifier.padding(SpacingMd),
            verticalArrangement = Arrangement.spacedBy(SpacingXxs),
        ) {
            Text(
                text = stringResource(R.string.my_lists_total_label),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
            Text(
                text = totalEstimated.formatAsCurrency(),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
            Text(
                text = stringResource(R.string.my_lists_lists_count, listCount, productCount),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Estado vacío
// ---------------------------------------------------------------------------

@Composable
private fun EmptyContent(
    onCreateList: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(SpacingXl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Rounded.ShoppingCart,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(IconLg),
        )
        Spacer(modifier = Modifier.height(SpacingMd))
        Text(
            text = stringResource(R.string.my_lists_empty_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(SpacingXs))
        Text(
            text = stringResource(R.string.my_lists_empty_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(SpacingXl))
        Button(
            onClick = onCreateList,
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
                text = stringResource(R.string.my_lists_empty_btn),
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Skeleton loader
// ---------------------------------------------------------------------------

@Composable
private fun SkeletonContent(modifier: Modifier = Modifier) {
    val shimmerAlpha by rememberInfiniteTransition(label = "shimmer").animateFloat(
        initialValue = 0.25f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "shimmerAlpha",
    )

    val skeletonColor = MaterialTheme.colorScheme.outline

    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(SpacingXs))

        // Card total skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clip(RoundedCornerShape(RadiusMd))
                .alpha(shimmerAlpha)
                .background(skeletonColor),
        )

        Spacer(modifier = Modifier.height(SpacingLg))

        // Label skeleton
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(12.dp)
                .clip(RoundedCornerShape(4.dp))
                .alpha(shimmerAlpha)
                .background(skeletonColor),
        )

        Spacer(modifier = Modifier.height(SpacingXs))

        // List card skeletons
        repeat(3) {
            Spacer(modifier = Modifier.height(SpacingXs))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(RadiusLg))
                    .alpha(shimmerAlpha)
                    .background(skeletonColor),
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@Preview(showSystemUi = true)
@Composable
private fun MisListasScreenPreview() {
    ToolistTheme {
        MisListasScreen(
            uiState = MisListasUiState(
                isLoading = false,
                userName = "Thony",
                totalEstimated = 87300.0,
                totalProducts = 22,
                lists = listOf(
                    ShoppingList("1", "Mercado semanal", "", "#F97316", 42500.0, 3, 8, 0L),
                    ShoppingList("2", "Aseo del hogar", "", "#3B82F6", 20800.0, 7, 7, 0L),
                    ShoppingList("3", "Cumpleaños", "", "#8B5CF6", 18000.0, 0, 5, 0L),
                ),
            ),
            onNavigateToNewList = {},
            onNavigateToListDetail = {},
            onNavigateToCategories = {},
            onNavigateToSearch = {},
            onNavigateToSettings = {},
            onNavigateToCredits = {},
            onDeleteList = {},
            onErrorShown = {},
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun MisListasScreenEmptyPreview() {
    ToolistTheme {
        MisListasScreen(
            uiState = MisListasUiState(isLoading = false),
            onNavigateToNewList = {},
            onNavigateToListDetail = {},
            onNavigateToCategories = {},
            onNavigateToSearch = {},
            onNavigateToSettings = {},
            onNavigateToCredits = {},
            onDeleteList = {},
            onErrorShown = {},
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun MisListasScreenLoadingPreview() {
    ToolistTheme {
        MisListasScreen(
            uiState = MisListasUiState(isLoading = true),
            onNavigateToNewList = {},
            onNavigateToListDetail = {},
            onNavigateToCategories = {},
            onNavigateToSearch = {},
            onNavigateToSettings = {},
            onNavigateToCredits = {},
            onDeleteList = {},
            onErrorShown = {},
        )
    }
}
