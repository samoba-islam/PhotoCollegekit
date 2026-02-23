package com.samoba.collagekit

import android.graphics.Bitmap
import android.net.Uri
import com.samoba.collagekit.model.CollageTemplate

/**
 * Configuration for initializing CollageKit
 */
data class CollageKitConfig(
    /**
     * Pre-configured images with their slot positions and transforms
     * If provided, picker won't open initially
     */
    val initialImages: List<SlotImageInput> = emptyList(),
    
    /**
     * Default template ID to use. If null, uses first 2-image template
     */
    val defaultTemplateId: String? = null,
    
    /**
     * Number of images to select in picker (default: 2)
     */
    val defaultImageCount: Int = 2,
    
    /**
     * Whether to show the image picker initially
     * Only applies when initialImages is empty
     */
    val showPickerInitially: Boolean = true,
    
    /**
     * Preset styling options
     */
    val presets: CollagePresets = CollagePresets()
)

/**
 * Preset styling configuration for the collage
 */
data class CollagePresets(
    val borderWidth: Float = 8f,
    val borderColor: Int = 0xFFFFFFFF.toInt(),
    val cornerRadius: Float = 0f,
    val backgroundColor: Int = 0xFF1A1A2E.toInt()
)

/**
 * Input configuration for a single image slot
 */
data class SlotImageInput(
    val slotIndex: Int,
    val uri: Uri,
    val scale: Float = 1f,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f
)

/**
 * Result returned when collage editing is complete
 */
data class CollageResult(
    /**
     * The template used for the collage
     */
    val template: CollageTemplate,
    
    /**
     * All images with their slot positions and transforms
     */
    val images: List<SlotImageOutput>,
    
    /**
     * Border width in dp
     */
    val borderWidth: Float,
    
    /**
     * Border/gap color as ARGB int
     */
    val borderColor: Int,
    
    /**
     * Corner radius in dp
     */
    val cornerRadius: Float,
    
    /**
     * Background color for empty slots
     */
    val backgroundColor: Int,
    
    /**
     * Generated bitmap of the final collage
     * Size is 1080x1080 by default
     */
    val outputBitmap: Bitmap
)

/**
 * Output data for a single image in the collage
 */
data class SlotImageOutput(
    val slotIndex: Int,
    val uri: Uri,
    val scale: Float,
    val offsetX: Float,
    val offsetY: Float
)
