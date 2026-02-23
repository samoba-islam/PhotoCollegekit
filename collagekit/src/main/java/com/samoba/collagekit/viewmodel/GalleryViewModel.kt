package com.samoba.collagekit.viewmodel

import android.app.Application
import android.content.ContentUris
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.samoba.collagekit.model.GalleryImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GalleryUiState(
    val images: List<GalleryImage> = emptyList(),
    val selectedImages: List<GalleryImage> = emptyList(),
    val isLoading: Boolean = true,
    val requiredCount: Int = 2,
    val templateId: String = ""
)

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    
    private val _uiState = MutableStateFlow(GalleryUiState())
    val uiState: StateFlow<GalleryUiState> = _uiState.asStateFlow()
    
    fun initialize(templateId: String, imageCount: Int) {
        _uiState.value = _uiState.value.copy(
            templateId = templateId,
            requiredCount = imageCount
        )
        loadImages()
    }
    
    private fun loadImages() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val images = mutableListOf<GalleryImage>()
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.SIZE
            )
            
            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
            
            getApplication<Application>().contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val date = cursor.getLong(dateColumn)
                    val size = cursor.getLong(sizeColumn)
                    
                    val uri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    
                    images.add(GalleryImage(id, uri, name, date, size))
                }
            }
            
            _uiState.value = _uiState.value.copy(
                images = images,
                isLoading = false
            )
        }
    }
    
    fun toggleImageSelection(image: GalleryImage) {
        val currentSelected = _uiState.value.selectedImages.toMutableList()
        
        if (currentSelected.contains(image)) {
            currentSelected.remove(image)
        } else if (currentSelected.size < _uiState.value.requiredCount) {
            currentSelected.add(image)
        }
        
        _uiState.value = _uiState.value.copy(selectedImages = currentSelected)
    }
    
    fun isSelectionComplete(): Boolean {
        return _uiState.value.selectedImages.size == _uiState.value.requiredCount
    }
    
    fun clearSelection() {
        _uiState.value = _uiState.value.copy(selectedImages = emptyList())
    }
}
