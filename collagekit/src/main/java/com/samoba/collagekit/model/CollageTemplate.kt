package com.samoba.collagekit.model

import android.graphics.RectF
import androidx.annotation.DrawableRes

/**
 * Represents a collage template with predefined image slots
 */
data class CollageTemplate(
    val id: String,
    val name: String,
    val imageCount: Int,
    val slots: List<ImageSlot>,
    @DrawableRes val previewRes: Int? = null
)

/**
 * Represents a single image slot within a collage template
 * Bounds are normalized to 0-1 range for flexible rendering
 */
data class ImageSlot(
    val index: Int,
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
    val rotation: Float = 0f
) {
    val bounds: RectF get() = RectF(left, top, right, bottom)
    val width: Float get() = right - left
    val height: Float get() = bottom - top
}
