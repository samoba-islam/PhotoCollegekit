package com.samoba.collagekit.model

import android.net.Uri

/**
 * Represents an image from the device gallery
 */
data class GalleryImage(
    val id: Long,
    val uri: Uri,
    val displayName: String,
    val dateAdded: Long,
    val size: Long
)

/**
 * Represents the current state of a collage being edited
 */
data class CollageState(
    val template: CollageTemplate,
    val images: List<SlotImage> = emptyList(),
    val borderWidth: Float = 8f,
    val borderColor: Int = 0xFFFFFFFF.toInt(),
    val backgroundColor: Int = 0xFF1A1A2E.toInt(),
    val cornerRadius: Float = 0f
)

/**
 * Represents an image placed in a specific slot
 */
data class SlotImage(
    val slotIndex: Int,
    val uri: Uri,
    val scale: Float = 1f,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f
)
