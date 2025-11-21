package com.ocp.app.ui

import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.ocp.sdk.CaptureController

@Composable
fun CameraScreen(
    captureController: CaptureController,
    onToggleRecording: (Boolean) -> Unit,
    onEditPipeline: () -> Unit,
    onOpenMarketplace: () -> Unit
) {
    var isRecording by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Camera Preview (SurfaceView)
        AndroidView(
            factory = { context ->
                SurfaceView(context).apply {
                    holder.addCallback(object : SurfaceHolder.Callback {
                        override fun surfaceCreated(holder: SurfaceHolder) {
                            captureController.setPreviewSurface(holder.surface)
                            captureController.start("0", "default")
                        }

                        override fun surfaceChanged(
                            holder: SurfaceHolder,
                            format: Int,
                            width: Int,
                            height: Int
                        ) {}

                        override fun surfaceDestroyed(holder: SurfaceHolder) {
                            captureController.setPreviewSurface(null)
                            captureController.stop()
                        }
                    })
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Controls Overlay
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = onEditPipeline) {
                    Text("Editor")
                }
                Button(onClick = onOpenMarketplace) {
                    Text("Store")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    isRecording = !isRecording
                    onToggleRecording(isRecording)
                }
            ) {
                Text(text = if (isRecording) "Stop Recording" else "Start Recording")
            }
        }
    }
}
