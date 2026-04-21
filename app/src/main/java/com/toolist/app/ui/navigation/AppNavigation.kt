package com.toolist.app.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.toolist.app.ui.screens.auth.AuthViewModel
import com.toolist.app.ui.screens.auth.ForgotPasswordScreen
import com.toolist.app.ui.screens.lists.DetalleListaScreen
import com.toolist.app.ui.screens.lists.DetalleListaViewModel
import com.toolist.app.ui.screens.lists.MisListasScreen
import com.toolist.app.ui.screens.lists.MisListasViewModel
import com.toolist.app.ui.screens.lists.NuevaListaScreen
import com.toolist.app.ui.screens.lists.NuevaListaViewModel
import com.toolist.app.ui.screens.auth.ForgotPasswordUiState
import com.toolist.app.ui.screens.auth.LoginScreen
import com.toolist.app.ui.screens.auth.LoginUiState
import com.toolist.app.ui.screens.auth.RegisterScreen
import com.toolist.app.ui.screens.auth.RegisterUiState
import com.toolist.app.ui.screens.auth.WelcomeScreen
import com.toolist.app.ui.screens.product.AgregarProductoScreen
import com.toolist.app.ui.screens.product.AgregarProductoViewModel

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
    // AuthViewModel compartido entre todas las pantallas de auth
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route,
    ) {

        // ── Auth ──────────────────────────────────────────────────────────
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onNavigateToLogin = {
                    authViewModel.resetState()
                    navController.navigate(Screen.Login.route)
                },
                onNavigateToRegister = {
                    authViewModel.resetState()
                    navController.navigate(Screen.Register.route)
                },
                onContinueWithoutAccount = { navController.navigate(Screen.MyLists.route) },
            )
        }

        composable(Screen.Login.route) {
            // Navegar a MyLists tras login exitoso
            LaunchedEffect(authState.isSuccess) {
                if (authState.isSuccess) {
                    navController.navigate(Screen.MyLists.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            }

            LoginScreen(
                onLoginClick = { email, password -> authViewModel.login(email, password) },
                onNavigateToRegister = {
                    authViewModel.resetState()
                    navController.navigate(Screen.Register.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToForgotPassword = {
                    authViewModel.resetState()
                    navController.navigate(Screen.ForgotPassword.route)
                },
                onNavigateBack = {
                    authViewModel.resetState()
                    navController.popBackStack()
                },
                uiState = LoginUiState(
                    isLoading = authState.isLoading,
                    emailError = authState.emailError,
                    passwordError = authState.passwordError,
                    generalError = authState.error,
                ),
            )
        }

        composable(Screen.Register.route) {
            // Navegar a MyLists tras registro exitoso
            LaunchedEffect(authState.isSuccess) {
                if (authState.isSuccess) {
                    navController.navigate(Screen.MyLists.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            }

            RegisterScreen(
                onRegisterClick = { name, email, password, confirmPassword ->
                    authViewModel.register(name, email, password, confirmPassword)
                },
                onNavigateToLogin = {
                    authViewModel.resetState()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    authViewModel.resetState()
                    navController.popBackStack()
                },
                uiState = RegisterUiState(
                    isLoading = authState.isLoading,
                    nameError = authState.nameError,
                    emailError = authState.emailError,
                    passwordError = authState.passwordError,
                    confirmPasswordError = authState.confirmPasswordError,
                    generalError = authState.error,
                ),
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onSendClick = { email -> authViewModel.sendPasswordReset(email) },
                onNavigateBack = {
                    authViewModel.resetState()
                    navController.popBackStack()
                },
                uiState = ForgotPasswordUiState(
                    isLoading = authState.isLoading,
                    emailError = authState.emailError,
                    isSuccess = authState.isSuccess,
                    generalError = authState.error,
                ),
            )
        }

        // ── Listas ────────────────────────────────────────────────────────
        composable(Screen.MyLists.route) {
            val viewModel: MisListasViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            MisListasScreen(
                uiState = uiState,
                onNavigateToNewList = { navController.navigate(Screen.NewList.route) },
                onNavigateToListDetail = { listId -> navController.navigate(Screen.ListDetail.createRoute(listId)) },
                onNavigateToCategories = { navController.navigate(Screen.Categories.route) },
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToCredits = { navController.navigate(Screen.Credits.route) },
                onDeleteList = { listId -> viewModel.deleteList(listId) },
                onErrorShown = { viewModel.clearError() },
            )
        }
        composable(Screen.NewList.route) {
            val viewModel: NuevaListaViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(uiState.isSuccess) {
                if (uiState.isSuccess) navController.popBackStack()
            }

            NuevaListaScreen(
                onCreateClick = { name, colorHex, description ->
                    viewModel.createList(name, colorHex, description)
                },
                onCancel = { navController.popBackStack() },
                uiState = uiState,
            )
        }
        composable(
            route = Screen.ListDetail.route,
            arguments = listOf(navArgument(Screen.ListDetail.ARG_LIST_ID) {
                type = NavType.StringType
            }),
        ) {
            val viewModel: DetalleListaViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(uiState.isDeleted) {
                if (uiState.isDeleted) navController.popBackStack()
            }

            DetalleListaScreen(
                uiState = uiState,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddProduct = {
                    navController.navigate(Screen.AddProduct.createRoute(uiState.list?.id ?: ""))
                },
                onToggleProductStatus = { product -> viewModel.toggleProductStatus(product) },
                onDeleteProduct = { product -> viewModel.deleteProduct(product) },
                onDuplicateProduct = { product -> viewModel.duplicateProduct(product) },
                onDeleteList = { viewModel.deleteList() },
                onDuplicateList = { /* Fase 5 */ },
                onResetList = { viewModel.resetList() },
                onTabSelected = { tab -> viewModel.selectTab(tab) },
                onErrorShown = { viewModel.clearError() },
            )
        }

        // ── Productos ─────────────────────────────────────────────────────
        composable(
            route = Screen.AddProduct.route,
            arguments = listOf(navArgument(Screen.AddProduct.ARG_LIST_ID) {
                type = NavType.StringType
            }),
        ) {
            val viewModel: AgregarProductoViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(uiState.isSuccess) {
                if (uiState.isSuccess) navController.popBackStack()
            }

            AgregarProductoScreen(
                initialListId = viewModel.listId,
                uiState = uiState,
                onAddClick = { name, targetListId, quantity, unit, categoryName, price, status, notes ->
                    viewModel.addProduct(name, targetListId, quantity, unit, categoryName, price, status, notes)
                },
                onNavigateBack = { navController.popBackStack() },
            )
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
