package com.samoba.collagekit.navigation

/**
 * Internal navigation routes for the collage kit
 */
internal sealed class Screen(val route: String) {
    data object Gallery : Screen("collage_gallery/{templateId}/{imageCount}") {
        fun createRoute(templateId: String, imageCount: Int) = 
            "collage_gallery/$templateId/$imageCount"
    }
    data object Editor : Screen("collage_editor")
    
    data object ReplaceImage : Screen("collage_replace_image/{slotIndex}") {
        fun createRoute(slotIndex: Int) = "collage_replace_image/$slotIndex"
    }
}
