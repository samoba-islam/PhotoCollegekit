package com.samoba.photocollege

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samoba.collagekit.CollageKitEditor
import com.samoba.collagekit.CollageKitConfig
import com.samoba.photocollege.ui.theme.PhotoCollegeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhotoCollegeTheme {
                var showCollageEditor by remember { mutableStateOf(false) }
                
                if (showCollageEditor) {
                    // Show the CollageKit editor
                    CollageKitEditor(
                        config = CollageKitConfig(
                            defaultImageCount = 2
                        ),
                        onResult = { result ->
                            // Handle the result - collage data with bitmap
                            Toast.makeText(
                                this@MainActivity,
                                "Collage created with ${result.images.size} images!",
                                Toast.LENGTH_SHORT
                            ).show()
                            showCollageEditor = false
                        },
                        onCancel = { 
                            showCollageEditor = false
                        }
                    )
                } else {
                    // Show the main app screen with button
                    MainScreen(
                        onOpenCollage = { showCollageEditor = true }
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    onOpenCollage: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E)
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Icon/Logo placeholder
            Icon(
                imageVector = Icons.Default.PhotoLibrary,
                contentDescription = null,
                tint = Color(0xFF667EEA),
                modifier = Modifier.size(80.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Photo College",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Create beautiful photo collages",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Create Collage Button
            Button(
                onClick = onOpenCollage,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF667EEA)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Create Collage",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}