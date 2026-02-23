package com.samoba.collagekit.collage

import com.samoba.collagekit.model.CollageTemplate
import com.samoba.collagekit.model.ImageSlot

/**
 * Provides predefined collage templates
 */
object TemplateProvider {
    
    fun getAllTemplates(): List<CollageTemplate> = 
        get2ImageTemplates() + get3ImageTemplates() + get4ImageTemplates() + 
        get5ImageTemplates() + get6ImageTemplates()
    
    fun getTemplatesByImageCount(count: Int): List<CollageTemplate> = when (count) {
        2 -> get2ImageTemplates()
        3 -> get3ImageTemplates()
        4 -> get4ImageTemplates()
        5 -> get5ImageTemplates()
        6 -> get6ImageTemplates()
        else -> emptyList()
    }
    
    fun get2ImageTemplates(): List<CollageTemplate> = listOf(
        CollageTemplate(
            id = "2_side_by_side",
            name = "Side by Side",
            imageCount = 2,
            slots = listOf(
                ImageSlot(0, 0f, 0f, 0.5f, 1f),
                ImageSlot(1, 0.5f, 0f, 1f, 1f)
            )
        ),
        CollageTemplate(
            id = "2_top_bottom",
            name = "Top & Bottom",
            imageCount = 2,
            slots = listOf(
                ImageSlot(0, 0f, 0f, 1f, 0.5f),
                ImageSlot(1, 0f, 0.5f, 1f, 1f)
            )
        ),
        CollageTemplate(
            id = "2_large_left",
            name = "Focus Left",
            imageCount = 2,
            slots = listOf(
                ImageSlot(0, 0f, 0f, 0.65f, 1f),
                ImageSlot(1, 0.65f, 0f, 1f, 1f)
            )
        ),
        CollageTemplate(
            id = "2_large_right",
            name = "Focus Right",
            imageCount = 2,
            slots = listOf(
                ImageSlot(0, 0f, 0f, 0.35f, 1f),
                ImageSlot(1, 0.35f, 0f, 1f, 1f)
            )
        )
    )
    
    fun get3ImageTemplates(): List<CollageTemplate> = listOf(
        CollageTemplate(
            id = "3_top_large",
            name = "Featured Top",
            imageCount = 3,
            slots = listOf(
                ImageSlot(0, 0f, 0f, 1f, 0.55f),
                ImageSlot(1, 0f, 0.55f, 0.5f, 1f),
                ImageSlot(2, 0.5f, 0.55f, 1f, 1f)
            )
        ),
        CollageTemplate(
            id = "3_bottom_large",
            name = "Featured Bottom",
            imageCount = 3,
            slots = listOf(
                ImageSlot(0, 0f, 0f, 0.5f, 0.45f),
                ImageSlot(1, 0.5f, 0f, 1f, 0.45f),
                ImageSlot(2, 0f, 0.45f, 1f, 1f)
            )
        ),
        CollageTemplate(
            id = "3_left_large",
            name = "Featured Left",
            imageCount = 3,
            slots = listOf(
                ImageSlot(0, 0f, 0f, 0.55f, 1f),
                ImageSlot(1, 0.55f, 0f, 1f, 0.5f),
                ImageSlot(2, 0.55f, 0.5f, 1f, 1f)
            )
        ),
        CollageTemplate(
            id = "3_columns",
            name = "Triptych",
            imageCount = 3,
            slots = listOf(
                ImageSlot(0, 0f, 0f, 0.33f, 1f),
                ImageSlot(1, 0.33f, 0f, 0.66f, 1f),
                ImageSlot(2, 0.66f, 0f, 1f, 1f)
            )
        )
    )
    
    fun get4ImageTemplates(): List<CollageTemplate> = listOf(
        CollageTemplate(
            id = "4_grid",
            name = "Classic Grid",
            imageCount = 4,
            slots = listOf(
                ImageSlot(0, 0f, 0f, 0.5f, 0.5f),
                ImageSlot(1, 0.5f, 0f, 1f, 0.5f),
                ImageSlot(2, 0f, 0.5f, 0.5f, 1f),
                ImageSlot(3, 0.5f, 0.5f, 1f, 1f)
            )
        ),
        CollageTemplate(
            id = "4_left_stack",
            name = "Magazine Left",
            imageCount = 4,
            slots = listOf(
                ImageSlot(0, 0f, 0f, 0.5f, 1f),
                ImageSlot(1, 0.5f, 0f, 1f, 0.33f),
                ImageSlot(2, 0.5f, 0.33f, 1f, 0.66f),
                ImageSlot(3, 0.5f, 0.66f, 1f, 1f)
            )
        ),
        CollageTemplate(
            id = "4_top_feature",
            name = "Hero Top",
            imageCount = 4,
            slots = listOf(
                ImageSlot(0, 0f, 0f, 1f, 0.5f),
                ImageSlot(1, 0f, 0.5f, 0.33f, 1f),
                ImageSlot(2, 0.33f, 0.5f, 0.66f, 1f),
                ImageSlot(3, 0.66f, 0.5f, 1f, 1f)
            )
        ),
        CollageTemplate(
            id = "4_mosaic",
            name = "Mosaic",
            imageCount = 4,
            slots = listOf(
                ImageSlot(0, 0f, 0f, 0.6f, 0.6f),
                ImageSlot(1, 0.6f, 0f, 1f, 0.4f),
                ImageSlot(2, 0.6f, 0.4f, 1f, 1f),
                ImageSlot(3, 0f, 0.6f, 0.6f, 1f)
            )
        )
    )
    
    fun get5ImageTemplates(): List<CollageTemplate> = listOf(
        CollageTemplate(
            id = "5_center_focus",
            name = "Center Stage",
            imageCount = 5,
            slots = listOf(
                ImageSlot(0, 0.2f, 0.2f, 0.8f, 0.8f),
                ImageSlot(1, 0f, 0f, 0.3f, 0.3f),
                ImageSlot(2, 0.7f, 0f, 1f, 0.3f),
                ImageSlot(3, 0f, 0.7f, 0.3f, 1f),
                ImageSlot(4, 0.7f, 0.7f, 1f, 1f)
            )
        ),
        CollageTemplate(
            id = "5_cross",
            name = "Cross",
            imageCount = 5,
            slots = listOf(
                ImageSlot(0, 0.33f, 0f, 0.66f, 0.33f),
                ImageSlot(1, 0f, 0.33f, 0.33f, 0.66f),
                ImageSlot(2, 0.33f, 0.33f, 0.66f, 0.66f),
                ImageSlot(3, 0.66f, 0.33f, 1f, 0.66f),
                ImageSlot(4, 0.33f, 0.66f, 0.66f, 1f)
            )
        ),
        CollageTemplate(
            id = "5_pyramid",
            name = "Pyramid",
            imageCount = 5,
            slots = listOf(
                ImageSlot(0, 0f, 0f, 0.5f, 0.5f),
                ImageSlot(1, 0.5f, 0f, 1f, 0.5f),
                ImageSlot(2, 0f, 0.5f, 0.33f, 1f),
                ImageSlot(3, 0.33f, 0.5f, 0.66f, 1f),
                ImageSlot(4, 0.66f, 0.5f, 1f, 1f)
            )
        )
    )
    
    fun get6ImageTemplates(): List<CollageTemplate> = listOf(
        CollageTemplate(
            id = "6_grid_2x3",
            name = "Grid 2×3",
            imageCount = 6,
            slots = listOf(
                ImageSlot(0, 0f, 0f, 0.5f, 0.33f),
                ImageSlot(1, 0.5f, 0f, 1f, 0.33f),
                ImageSlot(2, 0f, 0.33f, 0.5f, 0.66f),
                ImageSlot(3, 0.5f, 0.33f, 1f, 0.66f),
                ImageSlot(4, 0f, 0.66f, 0.5f, 1f),
                ImageSlot(5, 0.5f, 0.66f, 1f, 1f)
            )
        ),
        CollageTemplate(
            id = "6_grid_3x2",
            name = "Grid 3×2",
            imageCount = 6,
            slots = listOf(
                ImageSlot(0, 0f, 0f, 0.33f, 0.5f),
                ImageSlot(1, 0.33f, 0f, 0.66f, 0.5f),
                ImageSlot(2, 0.66f, 0f, 1f, 0.5f),
                ImageSlot(3, 0f, 0.5f, 0.33f, 1f),
                ImageSlot(4, 0.33f, 0.5f, 0.66f, 1f),
                ImageSlot(5, 0.66f, 0.5f, 1f, 1f)
            )
        ),
        CollageTemplate(
            id = "6_magazine",
            name = "Magazine",
            imageCount = 6,
            slots = listOf(
                ImageSlot(0, 0f, 0f, 0.5f, 0.6f),
                ImageSlot(1, 0.5f, 0f, 1f, 0.3f),
                ImageSlot(2, 0.5f, 0.3f, 1f, 0.6f),
                ImageSlot(3, 0f, 0.6f, 0.33f, 1f),
                ImageSlot(4, 0.33f, 0.6f, 0.66f, 1f),
                ImageSlot(5, 0.66f, 0.6f, 1f, 1f)
            )
        )
    )
}
