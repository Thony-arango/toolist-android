package com.toolist.app.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.toolist.app.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
import com.toolist.app.ui.screens.product.DetalleProductoScreen
import com.toolist.app.ui.screens.product.DetalleProductoViewModel
import com.toolist.app.ui.screens.product.EditarProductoScreen
import com.toolist.app.ui.screens.settings.CreditsScreen
import com.toolist.app.ui.screens.settings.SettingsScreen
import com.toolist.app.ui.screens.settings.SettingsViewModel
import com.toolist.app.ui.screens.categories.CategoriesScreen
import com.toolist.app.ui.screens.categories.CategoriesViewModel
import com.toolist.app.ui.screens.search.SearchScreen
import com.toolist.app.ui.screens.search.SearchViewModel

// ---------------------------------------------------------------------------
// Rutas selladas
// ---------------------------------------------------------------------------

sealed class Screen(val route: String) {

    // Splash
    object Splash : Screen("splash")

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
    isSessionActive: Boolean = false,
) {
    // AuthViewModel compartido entre todas las pantallas de auth
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
    ) {

        // ── Splash ────────────────────────────────────────────────────────
        composable(Screen.Splash.route) {
            LaunchedEffect(Unit) {
                val destination = if (isSessionActive) Screen.MyLists.route else Screen.Welcome.route
                navController.navigate(destination) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
            SplashContent()
        }

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
                onNavigateToSearch = { navController.navigate(Screen.Search.route) { launchSingleTop = true } },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) { launchSingleTop = true } },
                onNavigateToCredits = { navController.navigate(Screen.Credits.route) { launchSingleTop = true } },
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
        ) { backStackEntry ->
            val viewModel: DetalleProductoViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            DetalleProductoScreen(
                uiState = uiState,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = {
                    navController.navigate(
                        Screen.EditProduct.createRoute(viewModel.listId, viewModel.productId)
                    )
                },
                onToggleStatus = { viewModel.toggleStatus() },
            )
        }
        composable(
            route = Screen.EditProduct.route,
            arguments = listOf(
                navArgument(Screen.EditProduct.ARG_LIST_ID) { type = NavType.StringType },
                navArgument(Screen.EditProduct.ARG_PRODUCT_ID) { type = NavType.StringType },
            ),
        ) {
            val viewModel: AgregarProductoViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(uiState.isSuccess) {
                if (uiState.isSuccess) navController.popBackStack()
            }

            EditarProductoScreen(
                uiState = uiState,
                onSaveClick = { name, targetListId, quantity, unit, categoryName, price, status, notes ->
                    viewModel.updateProduct(name, targetListId, quantity, unit, categoryName, price, status, notes)
                },
                onNavigateBack = { navController.popBackStack() },
            )
        }

        // ── Otras ─────────────────────────────────────────────────────────
        composable(Screen.Categories.route) {
            val viewModel: CategoriesViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            CategoriesScreen(
                uiState = uiState,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.route) { launchSingleTop = true }
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route) { launchSingleTop = true }
                },
                onNavigateToCredits = {
                    navController.navigate(Screen.Credits.route) { launchSingleTop = true }
                },
                onShowCreateDialog = { viewModel.showCreateDialog() },
                onDismissCreateDialog = { viewModel.dismissCreateDialog() },
                onCreateCategory = { name -> viewModel.createCategory(name) },
                onRequestDelete = { category -> viewModel.requestDelete(category) },
                onDismissDeleteDialog = { viewModel.dismissDeleteDialog() },
                onConfirmDelete = { viewModel.confirmDelete() },
                onErrorShown = { viewModel.clearError() },
                onSnackShown = { viewModel.clearSnack() },
            )
        }
        composable(Screen.Search.route) {
            val viewModel: SearchViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            SearchScreen(
                uiState = uiState,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(Screen.MyLists.route) {
                        launchSingleTop = true
                        popUpTo(Screen.MyLists.route) { inclusive = false }
                    }
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route) { launchSingleTop = true }
                },
                onNavigateToCredits = {
                    navController.navigate(Screen.Credits.route) { launchSingleTop = true }
                },
                onQueryChange = { query -> viewModel.onQueryChange(query) },
                onSearch = { query -> viewModel.onSearch(query) },
                onSelectRecentSearch = { query -> viewModel.selectRecentSearch(query) },
                onRemoveRecentSearch = { query -> viewModel.removeRecentSearch(query) },
                onClearAllRecent = { viewModel.clearAllRecentSearches() },
                onSelectCategory = { category -> viewModel.selectCategoryFilter(category) },
                onProductClick = { listId, productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(listId, productId))
                },
                onToggleProductStatus = { /* Fase 5: via ViewModel */ },
                onErrorShown = { viewModel.clearError() },
            )
        }
        composable(Screen.Settings.route) {
            val viewModel: SettingsViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(uiState.isLoggedOut) {
                if (uiState.isLoggedOut) {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }

            SettingsScreen(
                uiState = uiState,
                onNavigateToHome = {
                    navController.navigate(Screen.MyLists.route) {
                        launchSingleTop = true
                        popUpTo(Screen.MyLists.route) { inclusive = false }
                    }
                },
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.route) { launchSingleTop = true }
                },
                onNavigateToCredits = {
                    navController.navigate(Screen.Credits.route) { launchSingleTop = true }
                },
                onShowLogoutDialog = { viewModel.showLogoutDialog() },
                onDismissLogoutDialog = { viewModel.dismissLogoutDialog() },
                onConfirmLogout = { viewModel.logout() },
            )
        }
        composable(Screen.Credits.route) {
            CreditsScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.MyLists.route) {
                        launchSingleTop = true
                        popUpTo(Screen.MyLists.route) { inclusive = false }
                    }
                },
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.route) { launchSingleTop = true }
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route) { launchSingleTop = true }
                },
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Splash
// ---------------------------------------------------------------------------

@Composable
private fun SplashContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_toolist_logo),
            contentDescription = null,
            modifier = Modifier.width(120.dp),
        )
    }
}
