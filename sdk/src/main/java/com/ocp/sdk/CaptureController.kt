                engine.setPreviewSurface(holder.surface)
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                // Handle resize
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                engine.setPreviewSurface(null!!) // Simplified for now
            }
        })
    }

    fun startRecording(outputPath: String) {
        val encoder = VideoEncoder(1920, 1080, 10000000, outputPath)
        engine.startRecording(encoder)
        println("Recording started to: $outputPath")
    }

    fun stop() {
        cameraInput.stop()
        audioEngine.stop()
        engine.stopRecording()
        engine.stop()
    }
}
