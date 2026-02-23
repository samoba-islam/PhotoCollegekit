# PhotoCollegekit 📸

[![JitPack](https://jitpack.io/v/samoba-islam/PhotoCollegekit.svg)](https://jitpack.io/#samoba-islam/PhotoCollegekit)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg)](https://android-arsenal.com/api?level=24)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-Ready-green)](https://developer.android.com/jetpack/compose)

A modern, customizable photo collage editor library for Android built with **Jetpack Compose**. Create stunning collages with minimal code while maintaining full control over the UI.

---

## ✨ Features

| Feature | Description |
|---------|-------------|
| 🎨 **15+ Templates** | Beautiful layouts for 2-6 images |
| 🖼️ **Built-in Gallery** | Permission handling & image selection |
| ⚙️ **Customizable Styling** | Border width, color, corner radius |
| 🔧 **Custom Bars** | Full control over TopBar/BottomBar in Gallery & Editor |
| 📏 **System Bar Support** | Proper status & navigation bar padding |
| 📤 **Data Return** | Returns bitmap + collage data — you handle saving |
| 🔄 **Gesture Support** | Tap to swap, long-press to replace |

---

## 📦 Installation

### Step 1: Add JitPack Repository

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

### Step 2: Add Dependency

```kotlin
// build.gradle.kts (app module)
dependencies {
    implementation("com.github.samoba-islam:PhotoCollegekit:1.0.0")
}
```

---

## 🚀 Quick Start

```kotlin
import com.samoba.collagekit.CollageKitEditor
import com.samoba.collagekit.CollageKitConfig

@Composable
fun MyScreen() {
    var showEditor by remember { mutableStateOf(false) }
    
    if (showEditor) {
        CollageKitEditor(
            config = CollageKitConfig(defaultImageCount = 4),
            onResult = { result ->
                // result.outputBitmap → Generated 1080×1080 bitmap
                // result.images → List of SlotImageOutput with transforms
                saveBitmapToGallery(result.outputBitmap)
                showEditor = false
            },
            onCancel = { showEditor = false }
        )
    } else {
        Button(onClick = { showEditor = true }) {
            Text("Create Collage")
        }
    }
}
```

---

## ⚙️ Configuration

### CollageKitConfig

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `initialImages` | `List<SlotImageInput>` | `[]` | Pre-loaded images with positions |
| `defaultTemplateId` | `String?` | `null` | Template ID to use |
| `defaultImageCount` | `Int` | `2` | Number of images for picker |
| `showPickerInitially` | `Boolean` | `true` | Show gallery picker first |
| `presets` | `CollagePresets` | Default | Styling presets |

### CollagePresets

| Parameter | Type | Default |
|-----------|------|---------|
| `borderWidth` | `Float` | `8f` |
| `borderColor` | `Int` | `0xFFFFFFFF` (White) |
| `cornerRadius` | `Float` | `0f` |
| `backgroundColor` | `Int` | `0xFF1A1A2E` (Dark) |

### Pre-configured Images Example

```kotlin
CollageKitEditor(
    config = CollageKitConfig(
        initialImages = listOf(
            SlotImageInput(slotIndex = 0, uri = imageUri1),
            SlotImageInput(slotIndex = 1, uri = imageUri2, scale = 1.5f)
        ),
        defaultTemplateId = "2_side_by_side",
        showPickerInitially = false,
        presets = CollagePresets(
            borderWidth = 12f,
            borderColor = 0xFF000000.toInt(),
            cornerRadius = 16f
        )
    ),
    onResult = { /* Handle result */ }
)
```

---

## 🎨 Custom TopBar / BottomBar

CollageKit provides full customization of both **Gallery** (image picker) and **Editor** screens.

### Scope Properties

| Scope | Properties |
|-------|------------|
| **`GalleryTopBarScope`** | `selectedCount`, `requiredCount`, `onBack` |
| **`GalleryBottomBarScope`** | `selectedCount`, `requiredCount`, `isComplete`, `onConfirm` |
| **`CollageTopBarScope`** | `collageState`, `onBack`, `onDone`, `isGenerating` |
| **`CollageBottomBarScope`** | `collageState`, `onDone`, `isGenerating` |

### Example: Custom Gallery Bars

```kotlin
CollageKitEditor(
    config = CollageKitConfig(defaultImageCount = 3),
    galleryTopBar = {
        TopAppBar(
            title = { Text("Select $requiredCount Photos") },
            subtitle = { Text("$selectedCount selected") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.Close, null)
                }
            }
        )
    },
    galleryBottomBar = {
        Surface(modifier = Modifier.navigationBarsPadding()) {
            Button(
                onClick = onConfirm,
                enabled = isComplete,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text(if (isComplete) "Continue" else "Select ${requiredCount - selectedCount} more")
            }
        }
    },
    onResult = { /* ... */ }
)
```

### Example: Custom Editor Bars

```kotlin
CollageKitEditor(
    config = CollageKitConfig(),
    editorTopBar = {
        TopAppBar(
            title = { Text("Edit Collage") },
            navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
            },
            actions = {
                if (isGenerating) {
                    CircularProgressIndicator(Modifier.size(24.dp))
                } else {
                    IconButton(onClick = onDone) { Icon(Icons.Default.Check, null) }
                }
            }
        )
    },
    onResult = { /* ... */ }
)
```

---

## 🖼️ Templates

| Images | Available Templates |
|--------|---------------------|
| **2** | Side by Side, Top & Bottom, Focus Left, Focus Right |
| **3** | Featured Top, Featured Bottom, Featured Left, Triptych |
| **4** | Classic Grid, Magazine Left, Hero Top, Mosaic |
| **5** | Center Stage, Cross, Pyramid |
| **6** | Grid 2×3, Grid 3×2, Magazine |

---

## 📋 CollageResult

When the user completes editing, `onResult` receives a `CollageResult`:

```kotlin
data class CollageResult(
    val template: CollageTemplate,      // Template used
    val images: List<SlotImageOutput>,  // Images with transforms
    val borderWidth: Float,
    val borderColor: Int,
    val cornerRadius: Float,
    val backgroundColor: Int,
    val outputBitmap: Bitmap            // 1080×1080 generated image
)
```

---

## 📄 License

```
Copyright 2026 Samoba

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
```

See [LICENSE](LICENSE) for the full text.

---

## 👤 Author

**Shawon Hossain**

- GitHub: [@samoba-islam](https://github.com/samoba-islam)
- Website: [samoba.pages.dev](https://samoba.pages.dev)

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

<p align="center">
  Made with ❤️ for the Android community
</p>
