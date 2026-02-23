package com.samoba.collagekit.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.samoba.collagekit.model.CollageState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Utility for generating collage bitmaps
 */
object BitmapGenerator {
    
    /**
     * Generate a bitmap from the collage state
     * @param context Android context for image loading
     * @param collageState Current collage state
     * @param outputSize Output bitmap size (square)
     * @return Generated bitmap
     */
    suspend fun generateBitmap(
        context: Context,
        collageState: CollageState,
        outputSize: Int = 1080
    ): Bitmap = withContext(Dispatchers.IO) {
        val bitmap = Bitmap.createBitmap(outputSize, outputSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        // Draw border color as background
        val borderPaint = Paint().apply {
            color = collageState.borderColor
        }
        canvas.drawRect(0f, 0f, outputSize.toFloat(), outputSize.toFloat(), borderPaint)
        
        // Calculate padding
        val padding = collageState.borderWidth * outputSize / 360f
        val halfPadding = padding / 2f
        val contentSize = outputSize - padding
        
        val imageLoader = ImageLoader(context)
        
        // Draw each image slot
        for (slot in collageState.template.slots) {
            val slotImage = collageState.images.find { it.slotIndex == slot.index }
            if (slotImage != null) {
                val slotLeft = (halfPadding + contentSize * slot.left + halfPadding).toInt()
                val slotTop = (halfPadding + contentSize * slot.top + halfPadding).toInt()
                val slotWidth = (contentSize * slot.width - padding).toInt().coerceAtLeast(1)
                val slotHeight = (contentSize * slot.height - padding).toInt().coerceAtLeast(1)
                
                val request = ImageRequest.Builder(context)
                    .data(slotImage.uri)
                    .size(slotWidth, slotHeight)
                    .allowHardware(false) // Required for software Canvas rendering
                    .build()
                
                val result = imageLoader.execute(request)
                if (result is SuccessResult) {
                    val imageBitmap = (result.drawable as android.graphics.drawable.BitmapDrawable).bitmap
                    
                    val scaledBitmap = Bitmap.createScaledBitmap(
                        imageBitmap,
                        slotWidth,
                        slotHeight,
                        true
                    )
                    
                    canvas.drawBitmap(scaledBitmap, slotLeft.toFloat(), slotTop.toFloat(), null)
                    scaledBitmap.recycle()
                }
            }
        }
        
        bitmap
    }
}
