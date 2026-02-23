package com.samoba.collagekit.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.samoba.collagekit.GalleryBottomBarScope
import com.samoba.collagekit.GalleryTopBarScope
import com.samoba.collagekit.model.GalleryImage
import com.samoba.collagekit.ui.theme.*
import com.samoba.collagekit.viewmodel.GalleryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GalleryScreen(
    templateId: String,
    imageCount: Int,
    topBar: (@Composable GalleryTopBarScope.() -> Unit)? = null,
    bottomBar: (@Composable GalleryBottomBarScope.() -> Unit)? = null,
    onImagesSelected: (List<GalleryImage>) -> Unit,
    onBack: () -> Unit,
    viewModel: GalleryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var permissionGranted by remember { mutableStateOf(false) }
    
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionGranted = granted
        if (granted) {
            viewModel.initialize(templateId, imageCount)
        }
    }
    
    LaunchedEffect(Unit) {
        permissionLauncher.launch(permission)
    }

    val isComplete = uiState.selectedImages.size == uiState.requiredCount
    
    val topBarScope = remember(uiState.selectedImages.size, uiState.requiredCount) {
        GalleryTopBarScope(
            selectedCount = uiState.selectedImages.size,
            requiredCount = uiState.requiredCount,
            onBack = onBack
        )
    }
    
    val bottomBarScope = remember(uiState.selectedImages.size, uiState.requiredCount, isComplete) {
        GalleryBottomBarScope(
            selectedCount = uiState.selectedImages.size,
            requiredCount = uiState.requiredCount,
            isComplete = isComplete,
            onConfirm = { onImagesSelected(uiState.selectedImages) }
        )
    }
    
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (topBar != null) {
                topBarScope.topBar()
            } else {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                "Select Photos",
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "${uiState.selectedImages.size} of ${uiState.requiredCount} selected",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (uiState.selectedImages.size == uiState.requiredCount) 
                                    AccentGreen 
                                else 
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
                )
            }
        },
        bottomBar = {
            if (bottomBar != null) {
                bottomBarScope.bottomBar()
            } else {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    Button(
                        onClick = { onImagesSelected(uiState.selectedImages) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = isComplete,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GradientStart,
                            disabledContainerColor = GradientStart.copy(alpha = 0.3f),
                            disabledContentColor = Color.White.copy(alpha = 0.5f)
                        )
                    ) {
                        if (isComplete) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            if (isComplete) 
                                "Create Collage" 
                            else 
                                "Select ${uiState.requiredCount - uiState.selectedImages.size} more photo(s)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        when {
            !permissionGranted -> {
                PermissionRequestContent(
                    onRequestPermission = { permissionLauncher.launch(permission) },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GradientStart)
                }
            }
            uiState.images.isEmpty() -> {
                EmptyGalleryContent(modifier = Modifier.padding(paddingValues))
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(
                        start = 8.dp,
                        end = 8.dp,
                        top = paddingValues.calculateTopPadding() + 8.dp,
                        bottom = paddingValues.calculateBottomPadding() + 8.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.images, key = { it.id }) { image ->
                        GalleryImageItem(
                            image = image,
                            isSelected = uiState.selectedImages.contains(image),
                            selectionIndex = uiState.selectedImages.indexOf(image) + 1,
                            canSelect = uiState.selectedImages.size < uiState.requiredCount,
                            onClick = { viewModel.toggleImageSelection(image) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GalleryImageItem(
    image: GalleryImage,
    isSelected: Boolean,
    selectionIndex: Int,
    canSelect: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 0.9f else 1f,
        animationSpec = spring()
    )
    
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) GradientStart else Color.Transparent
    )
    
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .scale(scale)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(enabled = canSelect || isSelected) { onClick() }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(image.uri)
                .crossfade(true)
                .build(),
            contentDescription = image.displayName,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GradientStart.copy(alpha = 0.3f))
            )
            
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(28.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(GradientStart, GradientEnd)
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = selectionIndex.toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
        
        if (!canSelect && !isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            )
        }
    }
}

@Composable
private fun PermissionRequestContent(
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Permission Required",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "We need access to your photos to create collages.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRequestPermission,
            colors = ButtonDefaults.buttonColors(containerColor = GradientStart)
        ) {
            Text("Grant Permission")
        }
    }
}

@Composable
private fun EmptyGalleryContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "No Photos Found",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Add some photos to your device to get started.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
