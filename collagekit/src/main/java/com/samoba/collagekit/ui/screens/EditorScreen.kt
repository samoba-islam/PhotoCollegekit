package com.samoba.collagekit.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.samoba.collagekit.CollageTopBarScope
import com.samoba.collagekit.CollageBottomBarScope
import com.samoba.collagekit.collage.TemplateProvider
import com.samoba.collagekit.model.CollageState
import com.samoba.collagekit.model.CollageTemplate
import com.samoba.collagekit.ui.theme.*
import com.samoba.collagekit.viewmodel.EditorUiState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
internal fun EditorScreen(
    uiState: EditorUiState,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onBorderWidthChange: (Float) -> Unit,
    onBorderColorChange: (Int) -> Unit,
    onCornerRadiusChange: (Float) -> Unit,
    onSwapImages: (Int, Int) -> Unit,
    onTemplateChange: (CollageTemplate) -> Unit,
    onNavigateToReplaceImage: (Int) -> Unit
) {
    var selectedTool by remember { mutableStateOf<EditorTool>(EditorTool.Frame) }
    var showColorPicker by remember { mutableStateOf(false) }
    
    val collageState = uiState.collageState
    
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Edit Collage", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = onSave,
                        enabled = !uiState.isGenerating
                    ) {
                        if (uiState.isGenerating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                "Save",
                                color = GradientStart,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (collageState != null) {
                    CollagePreview(
                        collageState = collageState,
                        onSwapImages = onSwapImages,
                        onSlotClick = { slotIndex ->
                            val hasImage = collageState.images.any { it.slotIndex == slotIndex }
                            if (!hasImage) {
                                onNavigateToReplaceImage(slotIndex)
                            }
                        },
                        onLongPressSlot = { slotIndex ->
                            onNavigateToReplaceImage(slotIndex)
                        }
                    )
                }
            }
            
            ToolSelector(
                selectedTool = selectedTool,
                onToolSelected = { selectedTool = it }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (collageState != null) {
                ToolOptions(
                    tool = selectedTool,
                    collageState = collageState,
                    onBorderWidthChange = onBorderWidthChange,
                    onBorderColorClick = { showColorPicker = true },
                    onCornerRadiusChange = onCornerRadiusChange,
                    onTemplateSelected = { template ->
                        onTemplateChange(template)
                    },
                    currentTemplate = collageState.template
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
    
    if (showColorPicker) {
        ColorPickerDialog(
            currentColor = collageState?.borderColor ?: 0xFFFFFFFF.toInt(),
            onColorSelected = { color ->
                onBorderColorChange(color)
                showColorPicker = false
            },
            onDismiss = { showColorPicker = false }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CollagePreview(
    collageState: CollageState,
    onSwapImages: (Int, Int) -> Unit,
    onSlotClick: (Int) -> Unit,
    onLongPressSlot: (Int) -> Unit
) {
    var selectedSlot by remember { mutableStateOf<Int?>(null) }
    
    val padding = collageState.borderWidth.dp
    val halfPadding = padding / 2
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(collageState.cornerRadius.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(collageState.borderColor))
                .padding(halfPadding)
        ) {
            val totalWidth = maxWidth
            val totalHeight = maxHeight
            
            collageState.template.slots.forEachIndexed { index, slot ->
                val slotImage = collageState.images.find { it.slotIndex == index }
                val isSelected = selectedSlot == index
                val hasImage = slotImage != null
                
                val slotLeft = totalWidth * slot.left + halfPadding
                val slotTop = totalHeight * slot.top + halfPadding
                val slotWidth = totalWidth * slot.width - padding
                val slotHeight = totalHeight * slot.height - padding
                
                Box(
                    modifier = Modifier
                        .offset(x = slotLeft, y = slotTop)
                        .size(width = slotWidth, height = slotHeight)
                        .clip(RoundedCornerShape((collageState.cornerRadius / 2).dp))
                        .background(Color(collageState.backgroundColor))
                        .border(
                            width = if (isSelected) 3.dp else 0.dp,
                            color = if (isSelected) GradientStart else Color.Transparent,
                            shape = RoundedCornerShape((collageState.cornerRadius / 2).dp)
                        )
                        .combinedClickable(
                            onClick = {
                                if (!hasImage) {
                                    onSlotClick(index)
                                } else if (selectedSlot == null) {
                                    selectedSlot = index
                                } else if (selectedSlot != index) {
                                    onSwapImages(selectedSlot!!, index)
                                    selectedSlot = null
                                } else {
                                    selectedSlot = null
                                }
                            },
                            onLongClick = {
                                onLongPressSlot(index)
                            }
                        )
                ) {
                    if (slotImage != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(slotImage.uri)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    scaleX = slotImage.scale
                                    scaleY = slotImage.scale
                                    translationX = slotImage.offsetX
                                    translationY = slotImage.offsetY
                                }
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add photo",
                                tint = Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(GradientStart.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "Swap",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private enum class EditorTool {
    Frame, Border, Corners
}

@Composable
private fun ToolSelector(
    selectedTool: EditorTool,
    onToolSelected: (EditorTool) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ToolButton(
            icon = Icons.Default.GridView,
            label = "Frame",
            isSelected = selectedTool == EditorTool.Frame,
            onClick = { onToolSelected(EditorTool.Frame) }
        )
        ToolButton(
            icon = Icons.Default.BorderStyle,
            label = "Border",
            isSelected = selectedTool == EditorTool.Border,
            onClick = { onToolSelected(EditorTool.Border) }
        )
        ToolButton(
            icon = Icons.Default.RoundedCorner,
            label = "Corners",
            isSelected = selectedTool == EditorTool.Corners,
            onClick = { onToolSelected(EditorTool.Corners) }
        )
    }
}

@Composable
private fun ToolButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) GradientStart.copy(alpha = 0.2f)
                else Color.Transparent
            )
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) GradientStart else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) GradientStart else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ToolOptions(
    tool: EditorTool,
    collageState: CollageState,
    onBorderWidthChange: (Float) -> Unit,
    onBorderColorClick: () -> Unit,
    onCornerRadiusChange: (Float) -> Unit,
    onTemplateSelected: (CollageTemplate) -> Unit,
    currentTemplate: CollageTemplate
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurfaceVariant
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        when (tool) {
            EditorTool.Frame -> {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Choose Frame",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val allTemplates = TemplateProvider.getAllTemplates()
                        items(allTemplates) { template ->
                            FrameTemplateItem(
                                template = template,
                                isSelected = template.id == currentTemplate.id,
                                onClick = { onTemplateSelected(template) }
                            )
                        }
                    }
                }
            }
            EditorTool.Border -> {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Width", color = TextPrimary)
                        Text(
                            "${collageState.borderWidth.toInt()}px",
                            color = TextSecondary
                        )
                    }
                    Slider(
                        value = collageState.borderWidth,
                        onValueChange = onBorderWidthChange,
                        valueRange = 0f..30f,
                        colors = SliderDefaults.colors(
                            thumbColor = GradientStart,
                            activeTrackColor = GradientStart
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Color", color = TextPrimary)
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(collageState.borderColor))
                                .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                                .clickable { onBorderColorClick() }
                        )
                    }
                }
            }
            EditorTool.Corners -> {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Radius", color = TextPrimary)
                        Text(
                            "${collageState.cornerRadius.toInt()}dp",
                            color = TextSecondary
                        )
                    }
                    Slider(
                        value = collageState.cornerRadius,
                        onValueChange = onCornerRadiusChange,
                        valueRange = 0f..48f,
                        colors = SliderDefaults.colors(
                            thumbColor = GradientStart,
                            activeTrackColor = GradientStart
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun FrameTemplateItem(
    template: CollageTemplate,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val slotColors = listOf(
        GradientStart.copy(alpha = 0.6f),
        AccentPurple.copy(alpha = 0.6f),
        AccentPink.copy(alpha = 0.6f),
        AccentCyan.copy(alpha = 0.6f),
        AccentGreen.copy(alpha = 0.6f),
        GradientEnd.copy(alpha = 0.6f)
    )
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .size(70.dp)
                .clickable { onClick() },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isSelected) GradientStart.copy(alpha = 0.2f) else DarkCard
            ),
            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, GradientStart) else null
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp)
            ) {
                val width = maxWidth
                val height = maxHeight
                
                template.slots.forEachIndexed { index, slot ->
                    val slotColor = slotColors[index % slotColors.size]
                    
                    Box(
                        modifier = Modifier
                            .offset(
                                x = width * slot.left,
                                y = height * slot.top
                            )
                            .size(
                                width = width * slot.width - 2.dp,
                                height = height * slot.height - 2.dp
                            )
                            .clip(RoundedCornerShape(4.dp))
                            .background(slotColor)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            "${template.imageCount}",
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) GradientStart else TextSecondary
        )
    }
}

@Composable
private fun ColorPickerDialog(
    currentColor: Int,
    onColorSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val presetColors = listOf(
        0xFFFFFFFF.toInt(),
        0xFF000000.toInt(),
        0xFF1A1A2E.toInt(),
        0xFF16213E.toInt(),
        0xFF667EEA.toInt(),
        0xFF764BA2.toInt(),
        0xFFFF6B9D.toInt(),
        0xFF00D4FF.toInt(),
        0xFF00D9A5.toInt(),
        0xFFFFD93D.toInt(),
        0xFFFF6B35.toInt(),
        0xFFE63946.toInt(),
        0xFF8B5CF6.toInt(),
        0xFF06B6D4.toInt(),
        0xFF84CC16.toInt(),
        0xFFF97316.toInt(),
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose Color") },
        text = {
            Column {
                presetColors.chunked(4).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        row.forEach { color ->
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(4.dp)
                                    .clip(CircleShape)
                                    .background(Color(color))
                                    .border(
                                        width = if (color == currentColor) 3.dp else 1.dp,
                                        color = if (color == currentColor) GradientStart else Color.White.copy(alpha = 0.3f),
                                        shape = CircleShape
                                    )
                                    .clickable { onColorSelected(color) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Editor screen with support for custom TopBar and BottomBar
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
internal fun EditorScreenWithCustomBars(
    uiState: EditorUiState,
    topBar: (@Composable CollageTopBarScope.() -> Unit)?,
    bottomBar: (@Composable CollageBottomBarScope.() -> Unit)?,
    onBack: () -> Unit,
    onDone: () -> Unit,
    onBorderWidthChange: (Float) -> Unit,
    onBorderColorChange: (Int) -> Unit,
    onCornerRadiusChange: (Float) -> Unit,
    onSwapImages: (Int, Int) -> Unit,
    onTemplateChange: (CollageTemplate) -> Unit,
    onNavigateToReplaceImage: (Int) -> Unit
) {
    var selectedTool by remember { mutableStateOf<EditorTool>(EditorTool.Frame) }
    var showColorPicker by remember { mutableStateOf(false) }
    
    val collageState = uiState.collageState
    
    // Create scopes for custom bars
    val topBarScope = remember(collageState, uiState.isGenerating) {
        CollageTopBarScope(
            collageState = collageState,
            onBack = onBack,
            onDone = onDone,
            isGenerating = uiState.isGenerating
        )
    }
    
    val bottomBarScope = remember(collageState, uiState.isGenerating) {
        CollageBottomBarScope(
            collageState = collageState,
            onDone = onDone,
            isGenerating = uiState.isGenerating
        )
    }
    
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (topBar != null) {
                topBarScope.topBar()
            } else {
                // Default top bar
                TopAppBar(
                    title = { Text("Edit Collage", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    },
                    actions = {
                        TextButton(
                            onClick = onDone,
                            enabled = !uiState.isGenerating
                        ) {
                            if (uiState.isGenerating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    "Done",
                                    color = GradientStart,
                                    fontWeight = FontWeight.Bold
                                )
                            }
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
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (collageState != null) {
                    CollagePreviewEditable(
                        collageState = collageState,
                        onSwapImages = onSwapImages,
                        onSlotClick = { slotIndex ->
                            val hasImage = collageState.images.any { it.slotIndex == slotIndex }
                            if (!hasImage) {
                                onNavigateToReplaceImage(slotIndex)
                            }
                        },
                        onLongPressSlot = { slotIndex ->
                            onNavigateToReplaceImage(slotIndex)
                        }
                    )
                }
            }
            
            ToolSelector(
                selectedTool = selectedTool,
                onToolSelected = { selectedTool = it }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (collageState != null) {
                ToolOptions(
                    tool = selectedTool,
                    collageState = collageState,
                    onBorderWidthChange = onBorderWidthChange,
                    onBorderColorClick = { showColorPicker = true },
                    onCornerRadiusChange = onCornerRadiusChange,
                    onTemplateSelected = { template ->
                        onTemplateChange(template)
                    },
                    currentTemplate = collageState.template
                )
            }
            
            Spacer(modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars))
        }
    }
    
    if (showColorPicker) {
        ColorPickerDialog(
            currentColor = collageState?.borderColor ?: 0xFFFFFFFF.toInt(),
            onColorSelected = { color ->
                onBorderColorChange(color)
                showColorPicker = false
            },
            onDismiss = { showColorPicker = false }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CollagePreviewEditable(
    collageState: CollageState,
    onSwapImages: (Int, Int) -> Unit,
    onSlotClick: (Int) -> Unit,
    onLongPressSlot: (Int) -> Unit
) {
    var selectedSlot by remember { mutableStateOf<Int?>(null) }
    
    val padding = collageState.borderWidth.dp
    val halfPadding = padding / 2
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(collageState.cornerRadius.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(collageState.borderColor))
                .padding(halfPadding)
        ) {
            val totalWidth = maxWidth
            val totalHeight = maxHeight
            
            collageState.template.slots.forEachIndexed { index, slot ->
                val slotImage = collageState.images.find { it.slotIndex == index }
                val isSelected = selectedSlot == index
                val hasImage = slotImage != null
                
                val slotLeft = totalWidth * slot.left + halfPadding
                val slotTop = totalHeight * slot.top + halfPadding
                val slotWidth = totalWidth * slot.width - padding
                val slotHeight = totalHeight * slot.height - padding
                
                Box(
                    modifier = Modifier
                        .offset(x = slotLeft, y = slotTop)
                        .size(width = slotWidth, height = slotHeight)
                        .clip(RoundedCornerShape((collageState.cornerRadius / 2).dp))
                        .background(Color(collageState.backgroundColor))
                        .border(
                            width = if (isSelected) 3.dp else 0.dp,
                            color = if (isSelected) GradientStart else Color.Transparent,
                            shape = RoundedCornerShape((collageState.cornerRadius / 2).dp)
                        )
                        .combinedClickable(
                            onClick = {
                                if (!hasImage) {
                                    onSlotClick(index)
                                } else if (selectedSlot == null) {
                                    selectedSlot = index
                                } else if (selectedSlot != index) {
                                    onSwapImages(selectedSlot!!, index)
                                    selectedSlot = null
                                } else {
                                    selectedSlot = null
                                }
                            },
                            onLongClick = {
                                onLongPressSlot(index)
                            }
                        )
                ) {
                    if (slotImage != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(slotImage.uri)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    scaleX = slotImage.scale
                                    scaleY = slotImage.scale
                                    translationX = slotImage.offsetX
                                    translationY = slotImage.offsetY
                                }
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add photo",
                                tint = Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(GradientStart.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "Swap",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
