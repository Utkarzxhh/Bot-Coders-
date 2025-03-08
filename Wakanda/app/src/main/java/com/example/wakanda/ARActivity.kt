package com.example.wakanda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import android.view.SurfaceView
import android.widget.Toast
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Session
import com.google.ar.core.exceptions.UnavailableException

class ARActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ARScreen()
                }
            }
        }
    }
}

@Composable
fun ARScreen() {
    val context = LocalContext.current
    // Access the Activity from LocalContext and get the intent
    val activity = context as? ComponentActivity
    val analysisResult = activity?.intent?.getStringExtra("analysisResult")

    AndroidView(
        factory = { ctx: android.content.Context ->
            // Create a SurfaceView to render the AR camera feed
            val surfaceView = SurfaceView(ctx).apply {
                // Check if ARCore is supported
                val availability = ArCoreApk.getInstance().checkAvailability(ctx)
                if (availability.isTransient) {
                    Toast.makeText(ctx, "Checking ARCore availability...", Toast.LENGTH_SHORT).show()
                } else if (!availability.isSupported) {
                    Toast.makeText(ctx, "ARCore not supported on this device", Toast.LENGTH_LONG).show()
                    return@apply
                }

                // Create an AR session
                val session: Session? = try {
                    Session(ctx)
                } catch (e: UnavailableException) {
                    Toast.makeText(ctx, "ARCore session failed: ${e.message}", Toast.LENGTH_LONG).show()
                    null
                }

                session?.let {
                    val config = it.config
                    it.configure(config)

                    it.setCameraTextureName(0)
                    it.resume()

                    analysisResult?.let { result ->
                        Toast.makeText(ctx, "AR View: $result", Toast.LENGTH_SHORT).show()
                    }

                    // Update listener for AR session (placeholder for future AR content)
                    it.setDisplayGeometry(0, 0, 0) // Placeholder; requires actual display metrics
                }
            }
            surfaceView
        },
        modifier = Modifier.fillMaxSize()
    )
}