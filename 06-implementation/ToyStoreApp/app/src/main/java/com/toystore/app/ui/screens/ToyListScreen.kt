package com.toystore.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.toystore.app.R
import com.toystore.app.data.model.Toy
import com.toystore.app.ui.components.EmptyState
import com.toystore.app.ui.viewmodel.AuthViewModel
import com.toystore.app.ui.viewmodel.ToyUiState
import com.toystore.app.ui.viewmodel.ToyViewModel
import java.text.NumberFormat
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToyListScreen(
    viewModel: ToyViewModel,
    authViewModel: AuthViewModel,
    onToyClick: (Toy) -> Unit,
    onCartClick: () -> Unit,
    onSettingsClick: () -> Unit,
    isAdmin: Boolean,
    onAddToyClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(searchQuery) {
        delay(300)
        if (searchQuery.trim().isEmpty()) {
            viewModel.loadToys()
        } else {
            viewModel.searchToys(searchQuery.trim())
        }
    }

    Scaffold(

        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                            contentDescription = "Toy Store Logo",
                            modifier = Modifier.size(32.dp)
                        )
                        Text("Toy Store", style = MaterialTheme.typography.titleLarge)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }

                    IconButton(onClick = { viewModel.loadToys() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }

                    if (!isAdmin) {
                        IconButton(onClick = onCartClick) {
                            Text("🛒", fontSize = 20.sp)
                        }
                    }


                    IconButton(onClick = { authViewModel.logout() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(
                    onClick = onAddToyClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add toy")
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search toys...") },
                placeholder = { Text("e.g. Robot, Doll, Puzzle") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            viewModel.loadToys()
                        }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                shape = RoundedCornerShape(12.dp)
            )


            when (val uiState = state) {
                is ToyUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is ToyUiState.Success -> {
                    val toys = uiState.toys

                    if (toys.isEmpty()) {
                        EmptyState(
                            emoji = if (searchQuery.isNotEmpty()) "🔍" else "🧸",
                            title = if (searchQuery.isNotEmpty()) "No toys found" else "Welcome to Toy Store!",
                            description = if (searchQuery.isNotEmpty())
                                "Try searching with different keywords"
                            else
                                "Add your first toy or browse our collection",
                            actionText = if (searchQuery.isNotEmpty()) "Clear search" else null,
                            onActionClick = {
                                if (searchQuery.isNotEmpty()) {
                                    searchQuery = ""
                                    viewModel.loadToys()
                                }
                            }
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(
                                start = 12.dp,
                                end = 12.dp,
                                top = 8.dp,
                                bottom = 16.dp
                            ),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(toys) { toy ->
                                ToyCard(
                                    toy = toy,
                                    isAdmin = isAdmin,
                                    onDeleteClick = { viewModel.deleteToy(it) },
                                    onClick = { onToyClick(toy) }
                                )
                            }
                        }
                    }
                }
                is ToyUiState.Error -> {
                    EmptyState(
                        emoji = "⚠️",
                        title = "Something went wrong",
                        description = uiState.message ?: "Failed to load toys",
                        actionText = "Try again",
                        onActionClick = { viewModel.loadToys() }
                    )
                }
            }
        }
    }
}

@Composable
fun ToyCard(
    toy: Toy,
    isAdmin: Boolean,
    onDeleteClick: (Long) -> Unit,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val baseUrl = "http://10.0.2.2:8080"

    val fullImageUrl = if (!toy.imageUrl.isNullOrEmpty() && toy.imageUrl != "null") {
        if (toy.imageUrl.startsWith("http")) toy.imageUrl
        else "$baseUrl/${toy.imageUrl.trimStart('/')}"
    } else null

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                AsyncImage(
                    model = fullImageUrl,
                    contentDescription = toy.name,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(android.R.drawable.ic_menu_gallery),
                    error = painterResource(android.R.drawable.ic_menu_report_image),
                    modifier = Modifier.fillMaxSize()
                )

                toy.category?.let { category ->
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.tertiary
                    ) {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = toy.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2
            )

            Text(
                text = NumberFormat.getCurrencyInstance().format(toy.price),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = if (toy.stock > 0) "✓ In stock" else "✗ Out of stock",
                style = MaterialTheme.typography.bodySmall,
                color = if (toy.stock > 0)
                    MaterialTheme.colorScheme.onSurfaceVariant
                else
                    MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp)
            )

            if (isAdmin) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = { onDeleteClick(toy.id) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete toy",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}