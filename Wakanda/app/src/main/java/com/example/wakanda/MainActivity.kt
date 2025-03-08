package com.example.wakanda

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PhotoScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoScreen() {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var analysisResult by remember { mutableStateOf<String?>(null) }

    // Launchers
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                capturedImageUri = imageUri
                Toast.makeText(context, "Photo captured!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Capture failed", Toast.LENGTH_LONG).show()
                imageUri = null
            }
        }
    )

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            capturedImageUri = uri
            if (uri == null) {
                val toastView = android.widget.TextView(context).apply {
                    text = "No image selected"
                    textSize = 20f
                    setTextColor(android.graphics.Color.WHITE)
                    setBackgroundColor(android.graphics.Color.DKGRAY)
                    gravity = android.view.Gravity.CENTER
                    setPadding(16, 16, 16, 16)
                }
                Toast(context).apply {
                    duration = Toast.LENGTH_SHORT
                    view = toastView
                    show()
                }
            }
        }
    )

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                val picturesDir = context.getExternalFilesDir("Pictures") ?: return@rememberLauncherForActivityResult
                if (!picturesDir.exists()) picturesDir.mkdirs()
                val photoFile = File(picturesDir, "photo_${System.currentTimeMillis()}.jpg")
                if (!photoFile.exists()) photoFile.createNewFile()
                val uri = FileProvider.getUriForFile(context, "com.example.wakanda.provider", photoFile)
                imageUri = uri
                try {
                    takePictureLauncher.launch(uri)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, "Camera app not found", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    )

    // Mock analysis
    LaunchedEffect(capturedImageUri) {
        capturedImageUri?.let {
            analysisResult = mockAnalyzeImage()
        }
    }

    // Bottom Sheet
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Choose an Option", style = MaterialTheme.typography.titleMedium)
                Button(
                    onClick = {
                        showBottomSheet = false
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            val picturesDir = context.getExternalFilesDir("Pictures") ?: return@Button
                            if (!picturesDir.exists()) picturesDir.mkdirs()
                            val photoFile = File(picturesDir, "photo_${System.currentTimeMillis()}.jpg")
                            if (!photoFile.exists()) photoFile.createNewFile()
                            val uri = FileProvider.getUriForFile(context, "com.example.wakanda.provider", photoFile)
                            imageUri = uri
                            takePictureLauncher.launch(uri)
                        } else {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, Modifier.padding(end = 8.dp))
                        Text("Take Photo")
                    }
                }
                Button(
                    onClick = {
                        showBottomSheet = false
                        pickImageLauncher.launch("image/*")
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Image, contentDescription = null, Modifier.padding(end = 8.dp))
                        Text("Pick from Gallery")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "MediCare",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        AnimatedVisibility(visible = capturedImageUri != null) {
            capturedImageUri?.let { uri ->
                Card(
                    modifier = Modifier
                        .size(300.dp)
                        .padding(8.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(model = uri),
                        contentDescription = "Selected or Captured Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        analysisResult?.let {
            Text(
                text = "Analysis: $it",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(16.dp)
            )
            Button(
                onClick = {
                    val intent = Intent(context, ARActivity::class.java).apply {
                        putExtra("analysisResult", it)
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("View in AR")
            }
        }

        if (capturedImageUri == null) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "No photo selected yet",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize=20.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        Button(
            onClick = { showBottomSheet = true },
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(48.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Select or Take Photo", style = MaterialTheme.typography.labelLarge)
        }
    }
}

private fun mockAnalyzeImage(): String {
    return "Detected: Placeholder Object (Confidence: 0.95)"
}