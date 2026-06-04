package com.toystore.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.toystore.app.data.model.Toy
import com.toystore.app.ui.viewmodel.CartViewModel
import com.toystore.app.ui.viewmodel.ToyUiState
import com.toystore.app.ui.viewmodel.ToyViewModel
import java.io.File
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToyDetailScreen(
    viewModel: ToyViewModel,
    cartViewModel: CartViewModel? = null,
    toyId: Long,
    isAdmin: Boolean,
    onBackClick: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    val detailState by viewModel.detailState.collectAsState()
    val context = LocalContext.current

    var isEditing by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> selectedImageUri = uri }

    LaunchedEffect(toyId) {
        viewModel.loadToyById(toyId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Toy Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }

            )
        }
    ) { padding ->
        when (val state = detailState) {
            is ToyUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ToyUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                }
            }
            is ToyUiState.Success -> {
                val toy = state.toys.firstOrNull() ?: return@Scaffold

                ToyDetailContent(
                    toy = toy,
                    isAdmin = isAdmin,
                    isEditing = isEditing,
                    selectedImageUri = selectedImageUri,
                    onImagePick = { imagePicker.launch("image/*") },
                    onSave = { updatedToy ->
                        val imageFile = selectedImageUri?.let { uri -> uriToFile(uri, context) }
                        viewModel.updateToyWithImage(toyId, updatedToy, imageFile)
                        isEditing = false
                        selectedImageUri = null
                    },
                    onCancelEdit = {
                        isEditing = false
                        selectedImageUri = null
                    },
                    onEditToggle = { isEditing = !isEditing },
                    onAddToCart = {
                        cartViewModel?.addToCart(toy.id, 1)
                        onNavigateToCart()
                    },
                    modifier = Modifier
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                )
            }
        }
    }
}

@Composable
fun ToyDetailContent(
    toy: Toy,
    isAdmin: Boolean,
    isEditing: Boolean,
    selectedImageUri: Uri?,
    onImagePick: () -> Unit,
    onSave: (Toy) -> Unit,
    onCancelEdit: () -> Unit,
    onEditToggle: () -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf(toy.name) }
    var description by remember { mutableStateOf(toy.description ?: "") }
    var price by remember { mutableStateOf(toy.price.toString()) }
    var stock by remember { mutableStateOf(toy.stock.toString()) }
    var category by remember { mutableStateOf(toy.category ?: "") }

    val baseUrl = "http://10.0.2.2:8080"
    val imageUrl = if (toy.imageUrl.isNullOrEmpty() || toy.imageUrl == "null") {
        null
    } else if (toy.imageUrl.startsWith("http")) {
        toy.imageUrl
    } else {
        "$baseUrl/${toy.imageUrl.trimStart('/')}"
    }

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Картинка
        if (isEditing) {
            OutlinedButton(onClick = onImagePick, modifier = Modifier.fillMaxWidth()) {
                Text(if (selectedImageUri != null) "Change Image" else "Select New Image")
            }
            selectedImageUri?.let { uri ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(uri).crossfade(true).build(),
                    contentDescription = "New image preview",
                    modifier = Modifier.fillMaxWidth().height(250.dp).clip(MaterialTheme.shapes.medium).background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop
                )
            }
        } else {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(imageUrl).crossfade(true).build(),
                contentDescription = toy.name,
                placeholder = painterResource(android.R.drawable.ic_menu_gallery),
                error = painterResource(android.R.drawable.ic_menu_report_image),
                modifier = Modifier.fillMaxWidth().height(250.dp).clip(MaterialTheme.shapes.medium).background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )
        }

        // Поля
        if (isEditing) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), maxLines = 4)
            OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        } else {
            Text(text = toy.name, style = MaterialTheme.typography.headlineLarge)
            Text(text = NumberFormat.getCurrencyInstance().format(toy.price), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Stock: ${toy.stock}", style = MaterialTheme.typography.bodyLarge)
            toy.category?.let { cat -> Text(text = "Category: $cat", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = toy.description.ifEmpty { "No description" }, style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.height(24.dp))


        if (isAdmin) {
            if (!isEditing) {

                Button(
                    onClick = onEditToggle,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit Toy")
                }
            } else {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            onSave(
                                toy.copy(
                                    name = name,
                                    description = description,
                                    price = price.toDoubleOrNull() ?: toy.price,
                                    stock = stock.toIntOrNull() ?: toy.stock,
                                    category = category
                                )
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save Changes")
                    }
                    OutlinedButton(onClick = onCancelEdit, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }
                }
            }
        } else {
            Button(onClick = onAddToCart, modifier = Modifier.fillMaxWidth()) {
                Text("Add to Cart")
            }
        }
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