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

    fun setPipeline(pipeline: com.ocp.shared.PipelineDefinition) {
        val plugins = pluginLoader.instantiatePipeline(pipeline)
        pipelineEngine.setPlugins(plugins)
        
        // Audio Plugins
        // For now, we just check if Reverb is in the list for the AudioEngine
        // In a real engine, we'd have separate audio/video chains in the definition
        val audioPlugins = plugins.filterIsInstance<com.ocp.sdk.audio.AudioPlugin>()
        audioEngine.setPlugins(audioPlugins)
    }

    fun stop() {
        cameraInput.stop()
        audioEngine.stop()
        engine.stopRecording()
        engine.stop()
    }
}
