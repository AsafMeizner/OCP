package com.ocp.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.ocp.app.ui.CameraScreen
import com.ocp.app.ui.theme.OCPTheme
import com.ocp.sdk.CaptureController
import java.io.File

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ocp.app.ui.editor.PipelineEditorScreen

class MainActivity : ComponentActivity() {

    private lateinit var captureController: CaptureController

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.all { it.value }) {
                // Permissions granted
            } else {
                Toast.makeText(this, "Permissions required", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        captureController = CaptureController(this)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
                                onToggleRecording = { isRecording ->
                                    if (isRecording) {
                                        val file = File(getExternalFilesDir(null), "video_${System.currentTimeMillis()}.mp4")
                                        captureController.startRecording(file.absolutePath)
                                    } else {
                                        captureController.stop()
                                        captureController.start("0", "default")
                                    }
                                },
                                onEditPipeline = {
                                    navController.navigate("editor")
                                },
                                onOpenMarketplace = {
                                    navController.navigate("marketplace")
                                }
                            )
                        }
                        composable("editor") {
                            PipelineEditorScreen(
                                captureController = captureController,
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("marketplace") {
                            MarketplaceScreen(
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    }
}
