package com.toolist.app.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.toolist.app.ui.screens.auth.WelcomeScreen

// ---------------------------------------------------------------------------
// Rutas selladas
// ---------------------------------------------------------------------------

sealed class Screen(val route: String) {

    // Auth
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")

    // Listas
    object MyLists : Screen("my_lists")
    object NewList : Screen("new_list")
    object ListDetail : Screen("list_detail/{listId}") {
        fun createRoute(listId: String) = "list_detail/$listId"
        const val ARG_LIST_ID = "listId"
    }

    // Productos
    object AddProduct : Screen("add_product/{listId}") {
        fun createRoute(listId: String) = "add_product/$listId"
        const val ARG_LIST_ID = "listId"
    }
    object ProductDetail : Screen("product_detail/{listId}/{productId}") {
        fun createRoute(listId: String, productId: String) = "product_detail/$listId/$productId"
        const val ARG_LIST_ID = "listId"
        const val ARG_PRODUCT_ID = "productId"
    }
    object EditProduct : Screen("edit_product/{listId}/{productId}") {
        fun createRoute(listId: String, productId: String) = "edit_product/$listId/$productId"
        const val ARG_LIST_ID = "listId"
        const val ARG_PRODUCT_ID = "productId"
    }

    // Otras pantallas principales
    object Categories : Screen("categories")
    object Search : Screen("search")
    object Settings : Screen("settings")
    object Credits : Screen("credits")
}

// ---------------------------------------------------------------------------
// NavGraph
// ---------------------------------------------------------------------------

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route,
    ) {

        // ── Auth ──────────────────────────────────────────────────────────
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onContinueWithoutAccount = { navController.navigate(Screen.MyLists.route) },
            )
        }
        composable(Screen.Login.route) {
            PlaceholderScreen("Iniciar sesión")
        }
        composable(Screen.Register.route) {
            PlaceholderScreen("Registrarse")
        }
        composable(Screen.ForgotPassword.route) {
            PlaceholderScreen("Recuperar contraseña")
        }

        // ── Listas ────────────────────────────────────────────────────────
        composable(Screen.MyLists.route) {
            PlaceholderScreen("Mis listas")
        }
        composable(Screen.NewList.route) {
            PlaceholderScreen("Nueva lista")
        }
        composable(
            route = Screen.ListDetail.route,
            arguments = listOf(navArgument(Screen.ListDetail.ARG_LIST_ID) {
                type = NavType.StringType
            }),
        ) {
            PlaceholderScreen("Detalle de lista")
        }

        // ── Productos ─────────────────────────────────────────────────────
        composable(
            route = Screen.AddProduct.route,
            arguments = listOf(navArgument(Screen.AddProduct.ARG_LIST_ID) {
                type = NavType.StringType
            }),
        ) {
            PlaceholderScreen("Agregar producto")
        }
        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(
                navArgument(Screen.ProductDetail.ARG_LIST_ID) { type = NavType.StringType },
                navArgument(Screen.ProductDetail.ARG_PRODUCT_ID) { type = NavType.StringType },
            ),
        ) {
            PlaceholderScreen("Detalle del producto")
        }
        composable(
            route = Screen.EditProduct.route,
            arguments = listOf(
                navArgument(Screen.EditProduct.ARG_LIST_ID) { type = NavType.StringType },
                navArgument(Screen.EditProduct.ARG_PRODUCT_ID) { type = NavType.StringType },
            ),
        ) {
            PlaceholderScreen("Editar producto")
        }

        // ── Otras ─────────────────────────────────────────────────────────
        composable(Screen.Categories.route) {
            PlaceholderScreen("Categorías")
        }
        composable(Screen.Search.route) {
            PlaceholderScreen("Búsqueda")
        }
        composable(Screen.Settings.route) {
            PlaceholderScreen("Configuración")
        }
        composable(Screen.Credits.route) {
            PlaceholderScreen("Créditos")
        }
    }
}

// ---------------------------------------------------------------------------
// Placeholder — se reemplaza pantalla a pantalla en Fases 2–5
// ---------------------------------------------------------------------------

@Composable
private fun PlaceholderScreen(name: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = name)
    }
}
