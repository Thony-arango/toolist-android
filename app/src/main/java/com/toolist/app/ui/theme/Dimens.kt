package com.toolist.app.ui.theme

import androidx.compose.ui.unit.dp

// ---------------------------------------------------------------------------
// Espaciado y padding
// ---------------------------------------------------------------------------
val SpacingXxs  = 4.dp
val SpacingXs   = 8.dp
val SpacingSm   = 12.dp
val SpacingMd   = 16.dp
val SpacingLg   = 24.dp
val SpacingXl   = 32.dp
val SpacingXxl  = 48.dp

// ---------------------------------------------------------------------------
// Corner radius
// ---------------------------------------------------------------------------
val RadiusSm     = 8.dp   // Chips, badges, campos de texto
val RadiusMd     = 12.dp  // Cards de lista, bottom sheet handle
val RadiusLg     = 16.dp  // Cards de total, cards de producto
val RadiusXl     = 24.dp  // Botones primarios y secundarios
val RadiusFull   = 50.dp  // Elementos completamente redondeados (FAB, avatar)

// ---------------------------------------------------------------------------
// Iconos
// ---------------------------------------------------------------------------
val IconSm  = 20.dp  // Iconos en campos de texto y chips
val IconMd  = 24.dp  // Iconos estándar de UI (nav, botones)
val IconLg  = 32.dp  // Iconos de estados vacíos y categorías
val IconXl  = 48.dp  // Iconos grandes de ilustración / estado vacío

// ---------------------------------------------------------------------------
// Componentes de navegación
// ---------------------------------------------------------------------------
val BottomNavHeight     = 64.dp   // Altura de la bottom navigation bar
val BottomNavIconSize   = 24.dp   // Tamaño de iconos en la bottom nav
val TopAppBarHeight     = 56.dp   // Altura del top app bar estándar

// ---------------------------------------------------------------------------
// Avatar / iniciales de usuario
// ---------------------------------------------------------------------------
val AvatarSm  = 32.dp  // Avatar pequeño en listas
val AvatarMd  = 40.dp  // Avatar estándar en top app bar (Mis Listas)
val AvatarLg  = 56.dp  // Avatar en Configuración

// ---------------------------------------------------------------------------
// Cards
// ---------------------------------------------------------------------------
val CardElevation    = 0.dp   // Sin sombra — bordes sutiles en su lugar
val CardMinHeight    = 72.dp  // Altura mínima de ListCard y ProductItem
val ListCardHeight   = 80.dp  // Altura fija de las tarjetas de lista

// ---------------------------------------------------------------------------
// Campos de texto
// ---------------------------------------------------------------------------
val TextFieldHeight  = 56.dp  // Altura estándar de OutlinedTextField
val TextFieldRadius  = RadiusMd

// ---------------------------------------------------------------------------
// Botones
// ---------------------------------------------------------------------------
val ButtonHeight     = 52.dp  // Altura de botones primarios/secundarios
val ButtonRadius     = RadiusXl

// ---------------------------------------------------------------------------
// Bottom sheet
// ---------------------------------------------------------------------------
val BottomSheetHandleWidth  = 40.dp
val BottomSheetHandleHeight = 4.dp
val BottomSheetCornerRadius = RadiusLg
val BottomSheetItemHeight   = 56.dp  // Altura de cada opción en el sheet

// ---------------------------------------------------------------------------
// Indicador de color de lista (dot selector en Nueva Lista)
// ---------------------------------------------------------------------------
val ColorDotSize         = 32.dp
val ColorDotSizeSelected = 36.dp
val ColorDotBorderWidth  = 2.5.dp

// ---------------------------------------------------------------------------
// Barra de progreso
// ---------------------------------------------------------------------------
val ProgressBarHeight    = 6.dp
val ProgressBarRadius    = 3.dp

// ---------------------------------------------------------------------------
// Imagen de producto
// ---------------------------------------------------------------------------
val ProductImageSize     = 56.dp   // Thumbnail en ProductItem
val ProductImageSizeLg   = 200.dp  // Imagen grande en DetalleProducto
val ProductImageRadius   = RadiusMd
