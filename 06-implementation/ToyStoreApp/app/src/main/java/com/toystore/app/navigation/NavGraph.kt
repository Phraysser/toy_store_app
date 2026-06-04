package com.toystore.app.navigation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.toystore.app.data.model.Toy
import com.toystore.app.ui.screens.*
import com.toystore.app.ui.viewmodel.AuthState
import com.toystore.app.ui.viewmodel.AuthViewModel
import com.toystore.app.ui.viewmodel.CartUiState
import com.toystore.app.ui.viewmodel.CartViewModel
import com.toystore.app.ui.viewmodel.SettingsViewModel
import com.toystore.app.ui.viewmodel.ToyViewModel
import java.io.File

@Composable
fun NavGraph(
    authViewModel: AuthViewModel,
    toyViewModel: ToyViewModel,
    cartViewModel: CartViewModel,
    settingsViewModel: SettingsViewModel
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    var authToken by remember { mutableStateOf("") }
    var userRole by remember { mutableStateOf("") }
    var showAddToyDialog by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showSplash by remember { mutableStateOf(true) }

    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val cartState by cartViewModel.state.collectAsStateWithLifecycle()
    val isDarkTheme by settingsViewModel.isDarkTheme.collectAsStateWithLifecycle()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            val success = authState as AuthState.Success
            if (authToken.isEmpty()) {
                authToken = success.token
                userRole = success.role
                toyViewModel.setToken(success.token)
                cartViewModel.setToken(success.token)

                if (navController.currentDestination?.route == "login") {
                    navController.navigate("toys") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
        }
    }

    LaunchedEffect(authState) {
        if (authState is AuthState.Initial && authToken.isNotEmpty()) {
            authToken = ""
            userRole = ""
            if (navController.currentDestination?.route == "toys") {
                navController.navigate("login") {
                    popUpTo("toys") { inclusive = true }
                }
            }
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }


    if (showSplash) {
        SplashScreen(onTimeout = { showSplash = false })
        return
    }

    NavHost(
        navController = navController,
        startDestination = if (authToken.isEmpty()) "login" else "toys",

        enterTransition = {
            slideInHorizontally(initialOffsetX = { it }) + fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { -it / 2 }) + fadeOut(animationSpec = tween(200))
        },
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { -it / 2 }) + fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { it }) + fadeOut(animationSpec = tween(200))
        }
    ) {
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { token, role ->
                    authToken = token
                    userRole = role
                    toyViewModel.setToken(token)
                    cartViewModel.setToken(token)
                    navController.navigate("toys") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = {
                    navController.navigate("login") { popUpTo("register") { inclusive = true } }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("toys") {
            ToyListScreen(
                viewModel = toyViewModel,
                authViewModel = authViewModel,
                onToyClick = { toy ->
                    navController.navigate("toyDetail/${toy.id}/${authViewModel.isAdmin}")
                },
                onCartClick = { navController.navigate("cart") },
                onSettingsClick = { navController.navigate("settings") },
                isAdmin = authViewModel.isAdmin,
                onAddToyClick = {
                    selectedImageUri = null
                    showAddToyDialog = true
                }
            )
        }

        composable("cart") {
            LaunchedEffect(Unit) {
                if (authToken.isNotEmpty()) {
                    cartViewModel.loadCart()
                }
            }

            when (val state = cartState) {
                is CartUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is CartUiState.Success -> {
                    CartScreen(
                        cartItems = state.items,
                        onRemoveItem = { cartId -> cartViewModel.removeFromCart(cartId) },
                        onCheckout = { /* TODO: реализовать чекаут */ },
                        onBackClick = { navController.popBackStack() },
                        onClearCart = { cartViewModel.clearCart() }
                    )
                }
                is CartUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        composable("toyDetail/{toyId}/{isAdmin}") { backStackEntry ->
            val toyId = backStackEntry.arguments?.getString("toyId")?.toLongOrNull() ?: return@composable
            val isAdmin = backStackEntry.arguments?.getString("isAdmin")?.toBoolean() ?: false

            ToyDetailScreen(
                viewModel = toyViewModel,
                cartViewModel = cartViewModel,
                toyId = toyId,
                isAdmin = isAdmin,
                onBackClick = { navController.popBackStack() },
                onNavigateToCart = { navController.navigate("cart") }
            )
        }

        composable("settings") {
            SettingsScreen(
                isDarkTheme = isDarkTheme,
                onThemeChange = { settingsViewModel.setDarkTheme(it) },
                onBackClick = { navController.popBackStack() }
            )
        }
    }

    if (showAddToyDialog) {
        AddToyDialog(
            selectedImageUri = selectedImageUri,
            onImagePickClick = { imagePickerLauncher.launch("image/*") },
            onDismiss = { showAddToyDialog = false },
            onConfirm = { name, price, description, stock, category ->
                val newToy = Toy(
                    id = 0,
                    name = name,
                    description = description,
                    price = price,
                    imageUrl = "",
                    stock = stock,
                    category = category,
                    createdAt = null,
                    updatedAt = null
                )

                val imageFile = selectedImageUri?.let { uriToFile(it, context) }
                toyViewModel.createToyWithImage(newToy, imageFile)
                showAddToyDialog = false
            }
        )
    }
}

fun uriToFile(uri: Uri, context: android.content.Context): File {
    val inputStream = context.contentResolver.openInputStream(uri)
        ?: throw IllegalArgumentException("Cannot open input stream for $uri")

    val tempFile = File.createTempFile(
        "upload_${System.currentTimeMillis()}",
        ".jpg",
        context.cacheDir
    )

    tempFile.outputStream().use { fileOutput ->
        inputStream.copyTo(fileOutput)
    }

    return tempFile
}