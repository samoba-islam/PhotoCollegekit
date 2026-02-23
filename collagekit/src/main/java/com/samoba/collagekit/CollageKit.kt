package com.samoba.collagekit

import android.graphics.Bitmap
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.samoba.collagekit.model.CollageState
import com.samoba.collagekit.navigation.Screen
import com.samoba.collagekit.ui.screens.*
import com.samoba.collagekit.util.BitmapGenerator
import com.samoba.collagekit.viewmodel.EditorViewModel
import kotlinx.coroutines.launch

/**
 * Scope for custom Editor TopBar composables
 */
data class CollageTopBarScope(
    val collageState: CollageState?,
    val onBack: () -> Unit,
    val onDone: () -> Unit,
    val isGenerating: Boolean
)

/**
 * Scope for custom Editor BottomBar composables
 */
data class CollageBottomBarScope(
    val collageState: CollageState?,
    val onDone: () -> Unit,
    val isGenerating: Boolean
)

/**
 * Scope for custom Gallery TopBar composables
 */
data class GalleryTopBarScope(
    val selectedCount: Int,
    val requiredCount: Int,
    val onBack: () -> Unit
)

/**
 * Scope for custom Gallery BottomBar composables
 */
data class GalleryBottomBarScope(
    val selectedCount: Int,
    val requiredCount: Int,
    val isComplete: Boolean,
    val onConfirm: () -> Unit
)

/**
 * CollageKit - Photo Collage Library
 * 
 * A flexible photo collage creation library for Android.
 */
object CollageKit {
    
    /**
     * Main composable for the CollageKit library with full customization.
     * 
     * @param config Configuration including initial images, presets, and picker settings
     * @param editorTopBar Optional custom editor top bar composable
     * @param editorBottomBar Optional custom editor bottom bar composable
     * @param galleryTopBar Optional custom gallery top bar composable
     * @param galleryBottomBar Optional custom gallery bottom bar composable
     * @param onResult Callback when user finishes editing - returns CollageResult with bitmap
     * @param onCancel Callback when user cancels
     */
    @Composable
    fun CollageEditor(
        config: CollageKitConfig = CollageKitConfig(),
        editorTopBar: (@Composable CollageTopBarScope.() -> Unit)? = null,
        editorBottomBar: (@Composable CollageBottomBarScope.() -> Unit)? = null,
        galleryTopBar: (@Composable GalleryTopBarScope.() -> Unit)? = null,
        galleryBottomBar: (@Composable GalleryBottomBarScope.() -> Unit)? = null,
        onResult: (CollageResult) -> Unit,
        onCancel: () -> Unit = {}
    ) {
        val navController = rememberNavController()
        val editorViewModel: EditorViewModel = viewModel()
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        
        // Determine start destination based on config
        val startDestination = remember(config) {
            if (config.initialImages.isNotEmpty()) {
                // Has initial images - go straight to editor
                Screen.Editor.route
            } else if (config.showPickerInitially) {
                // Show picker first - go to Gallery with default template
                Screen.Gallery.createRoute(
                    config.defaultTemplateId ?: "2_side_by_side",
                    config.defaultImageCount
                )
            } else {
                // Go to editor with empty state (user will add images via slots)
                Screen.Editor.route
            }
        }
        
        // Initialize if we have initial images
        LaunchedEffect(config) {
            if (config.initialImages.isNotEmpty()) {
                editorViewModel.initializeFromConfig(config)
            }
        }
        
        // Function to generate result and call callback
        val generateResult: () -> Unit = {
            scope.launch {
                val collageState = editorViewModel.uiState.value.collageState
                if (collageState != null) {
                    editorViewModel.setGenerating(true)
                    
                    val bitmap = BitmapGenerator.generateBitmap(context, collageState)
                    
                    val result = CollageResult(
                        template = collageState.template,
                        images = collageState.images.map { img ->
                            SlotImageOutput(
                                slotIndex = img.slotIndex,
                                uri = img.uri,
                                scale = img.scale,
                                offsetX = img.offsetX,
                                offsetY = img.offsetY
                            )
                        },
                        borderWidth = collageState.borderWidth,
                        borderColor = collageState.borderColor,
                        cornerRadius = collageState.cornerRadius,
                        backgroundColor = collageState.backgroundColor,
                        outputBitmap = bitmap
                    )
                    
                    editorViewModel.setGenerating(false)
                    onResult(result)
                }
            }
        }
        
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            
            composable(
                route = Screen.Gallery.route,
                arguments = listOf(
                    navArgument("templateId") { type = NavType.StringType },
                    navArgument("imageCount") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val templateId = backStackEntry.arguments?.getString("templateId") ?: ""
                val imageCount = backStackEntry.arguments?.getInt("imageCount") ?: 2
                
                GalleryScreen(
                    templateId = templateId,
                    imageCount = imageCount,
                    topBar = galleryTopBar,
                    bottomBar = galleryBottomBar,
                    onImagesSelected = { images ->
                        // Create config with selected images and apply presets
                        val newConfig = config.copy(
                            initialImages = images.mapIndexed { index, img ->
                                SlotImageInput(slotIndex = index, uri = img.uri)
                            },
                            defaultTemplateId = templateId
                        )
                        editorViewModel.initializeFromConfig(newConfig)
                        navController.navigate(Screen.Editor.route) {
                            popUpTo(Screen.Gallery.route) { inclusive = true }
                        }
                    },
                    onBack = { 
                        if (!navController.popBackStack()) {
                            onCancel()
                        }
                    }
                )
            }
            
            composable(Screen.Editor.route) {
                val uiState by editorViewModel.uiState.collectAsState()
                
                EditorScreenWithCustomBars(
                    uiState = uiState,
                    topBar = editorTopBar,
                    bottomBar = editorBottomBar,
                    onBack = { 
                        if (!navController.popBackStack()) {
                            onCancel()
                        }
                    },
                    onDone = generateResult,
                    onBorderWidthChange = { editorViewModel.updateBorderWidth(it) },
                    onBorderColorChange = { editorViewModel.updateBorderColor(it) },
                    onCornerRadiusChange = { editorViewModel.updateCornerRadius(it) },
                    onSwapImages = { from, to -> editorViewModel.swapImages(from, to) },
                    onTemplateChange = { template -> editorViewModel.changeTemplate(template) },
                    onNavigateToReplaceImage = { slotIndex ->
                        navController.navigate(
                            Screen.ReplaceImage.createRoute(slotIndex)
                        )
                    }
                )
            }
            
            composable(
                route = Screen.ReplaceImage.route,
                arguments = listOf(
                    navArgument("slotIndex") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val slotIndex = backStackEntry.arguments?.getInt("slotIndex") ?: 0
                
                GalleryScreen(
                    templateId = "",
                    imageCount = 1,
                    onImagesSelected = { images ->
                        if (images.isNotEmpty()) {
                            editorViewModel.replaceImage(slotIndex, images.first().uri)
                        }
                        navController.popBackStack()
                    },
                    onBack = { 
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

/**
 * Convenient top-level composable for using CollageKit
 */
@Composable
fun CollageKitEditor(
    config: CollageKitConfig = CollageKitConfig(),
    editorTopBar: (@Composable CollageTopBarScope.() -> Unit)? = null,
    editorBottomBar: (@Composable CollageBottomBarScope.() -> Unit)? = null,
    galleryTopBar: (@Composable GalleryTopBarScope.() -> Unit)? = null,
    galleryBottomBar: (@Composable GalleryBottomBarScope.() -> Unit)? = null,
    onResult: (CollageResult) -> Unit,
    onCancel: () -> Unit = {}
) {
    CollageKit.CollageEditor(
        config = config,
        editorTopBar = editorTopBar,
        editorBottomBar = editorBottomBar,
        galleryTopBar = galleryTopBar,
        galleryBottomBar = galleryBottomBar,
        onResult = onResult,
        onCancel = onCancel
    )
}
