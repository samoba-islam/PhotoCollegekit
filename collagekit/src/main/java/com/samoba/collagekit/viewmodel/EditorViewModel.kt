package com.samoba.collagekit.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.samoba.collagekit.CollageKitConfig
import com.samoba.collagekit.SlotImageInput
import com.samoba.collagekit.collage.TemplateProvider
import com.samoba.collagekit.model.CollageState
import com.samoba.collagekit.model.CollageTemplate
import com.samoba.collagekit.model.SlotImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class EditorUiState(
    val collageState: CollageState? = null,
    val isLoading: Boolean = false,
    val isGenerating: Boolean = false,
    val errorMessage: String? = null,
    val slotToReplace: Int? = null
)

class EditorViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()
    
    /**
     * Initialize collage from config with pre-set images and styling
     */
    fun initializeFromConfig(config: CollageKitConfig) {
        val template = if (config.defaultTemplateId != null) {
            TemplateProvider.getAllTemplates().find { it.id == config.defaultTemplateId }
                ?: TemplateProvider.get2ImageTemplates().first()
        } else if (config.initialImages.isNotEmpty()) {
            // Find a template that fits the number of images
            val imageCount = config.initialImages.size
            TemplateProvider.getTemplatesByImageCount(imageCount).firstOrNull()
                ?: TemplateProvider.get2ImageTemplates().first()
        } else {
            TemplateProvider.get2ImageTemplates().first()
        }
        
        val slotImages = config.initialImages.map { input ->
            SlotImage(
                slotIndex = input.slotIndex,
                uri = input.uri,
                scale = input.scale,
                offsetX = input.offsetX,
                offsetY = input.offsetY
            )
        }
        
        val collageState = CollageState(
            template = template,
            images = slotImages,
            borderWidth = config.presets.borderWidth,
            borderColor = config.presets.borderColor,
            cornerRadius = config.presets.cornerRadius,
            backgroundColor = config.presets.backgroundColor
        )
        
        _uiState.value = _uiState.value.copy(collageState = collageState)
    }
    
    /**
     * Initialize collage from template ID and image URIs (legacy method)
     */
    fun initializeCollage(templateId: String, imageUris: List<Uri>) {
        val template = TemplateProvider.getAllTemplates().find { it.id == templateId }
            ?: TemplateProvider.get2ImageTemplates().first()
        
        val slotImages = imageUris.mapIndexed { index, uri ->
            SlotImage(slotIndex = index, uri = uri)
        }
        
        val collageState = CollageState(
            template = template,
            images = slotImages
        )
        
        _uiState.value = _uiState.value.copy(collageState = collageState)
    }
    
    fun changeTemplate(newTemplate: CollageTemplate) {
        val currentState = _uiState.value.collageState ?: return
        val currentImages = currentState.images
        val requiredCount = newTemplate.imageCount
        
        val newImages = currentImages.take(requiredCount).mapIndexed { index, img ->
            img.copy(slotIndex = index)
        }
        
        _uiState.value = _uiState.value.copy(
            collageState = currentState.copy(
                template = newTemplate,
                images = newImages
            )
        )
    }
    
    fun replaceImage(slotIndex: Int, newUri: Uri) {
        val currentState = _uiState.value.collageState ?: return
        val existingImage = currentState.images.find { it.slotIndex == slotIndex }
        
        val newImages = if (existingImage != null) {
            currentState.images.map { img ->
                if (img.slotIndex == slotIndex) {
                    img.copy(uri = newUri, scale = 1f, offsetX = 0f, offsetY = 0f)
                } else img
            }
        } else {
            currentState.images + SlotImage(slotIndex = slotIndex, uri = newUri)
        }
        
        _uiState.value = _uiState.value.copy(
            collageState = currentState.copy(images = newImages),
            slotToReplace = null
        )
    }
    
    fun swapImages(fromIndex: Int, toIndex: Int) {
        val currentState = _uiState.value.collageState ?: return
        val images = currentState.images.toMutableList()
        
        val fromImg = images.find { it.slotIndex == fromIndex }
        val toImg = images.find { it.slotIndex == toIndex }
        
        if (fromImg != null && toImg != null) {
            val newImages = images.map { img ->
                when (img.slotIndex) {
                    fromIndex -> toImg.copy(slotIndex = fromIndex)
                    toIndex -> fromImg.copy(slotIndex = toIndex)
                    else -> img
                }
            }
            _uiState.value = _uiState.value.copy(
                collageState = currentState.copy(images = newImages)
            )
        }
    }
    
    fun updateBorderWidth(width: Float) {
        val currentState = _uiState.value.collageState ?: return
        _uiState.value = _uiState.value.copy(
            collageState = currentState.copy(borderWidth = width)
        )
    }
    
    fun updateBorderColor(color: Int) {
        val currentState = _uiState.value.collageState ?: return
        _uiState.value = _uiState.value.copy(
            collageState = currentState.copy(borderColor = color)
        )
    }
    
    fun updateCornerRadius(radius: Float) {
        val currentState = _uiState.value.collageState ?: return
        _uiState.value = _uiState.value.copy(
            collageState = currentState.copy(cornerRadius = radius)
        )
    }
    
    fun setGenerating(generating: Boolean) {
        _uiState.value = _uiState.value.copy(isGenerating = generating)
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
