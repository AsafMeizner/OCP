package com.ocp.desktop

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.ocp.shared.PipelineDefinition
import com.ocp.shared.PipelineSerializer
import com.ocp.shared.PluginDefinition

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "OCP Pipeline Designer") {
        MaterialTheme {
            PipelineDesigner()
        }
    }
}

@Composable
fun PipelineDesigner() {
    var pipelineName by remember { mutableStateOf("My Pipeline") }
    var jsonOutput by remember { mutableStateOf("") }
    
    val plugins = remember { mutableStateListOf<PluginDefinition>() }

    Row(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Sidebar: Available Plugins
        Column(modifier = Modifier.width(250.dp).fillMaxHeight()) {
            Text("Available Plugins", style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(8.dp))
            
            val availablePlugins = listOf("BrightnessContrast", "Lut", "FaceMask", "Reverb")
            
            LazyColumn {
                items(availablePlugins) { pluginType ->
                    Button(
                        onClick = {
                            plugins.add(PluginDefinition(
                                id = java.util.UUID.randomUUID().toString(),
                                type = pluginType,
                                parameters = emptyMap()
                            ))
                        },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Text(pluginType)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Main Area: Pipeline Graph (List for now)
        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
            Text("Current Pipeline", style = MaterialTheme.typography.h6)
            TextField(
                value = pipelineName,
                onValueChange = { pipelineName = it },
                label = { Text("Pipeline Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(plugins) { plugin ->
                    Text(
                        text = "${plugin.type} (${plugin.id.take(8)})",
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row {
                Button(onClick = {
                    val pipeline = PipelineDefinition(
                        id = java.util.UUID.randomUUID().toString(),
                        name = pipelineName,
                        plugins = plugins.toList()
                    )
                    jsonOutput = PipelineSerializer.toJson(pipeline)
                }) {
                    Text("Generate JSON")
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Button(onClick = {
                    val fileDialog = java.awt.FileDialog(null as java.awt.Frame?, "Save Pipeline", java.awt.FileDialog.SAVE)
                    fileDialog.file = "$pipelineName.json"
                    fileDialog.isVisible = true
                    if (fileDialog.file != null) {
                        val file = java.io.File(fileDialog.directory, fileDialog.file)
                        file.writeText(jsonOutput)
                    }
                }) {
                    Text("Save to File")
                }
                
                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = {
                    val fileDialog = java.awt.FileDialog(null as java.awt.Frame?, "Open Pipeline", java.awt.FileDialog.LOAD)
                    fileDialog.isVisible = true
                    if (fileDialog.file != null) {
                        val file = java.io.File(fileDialog.directory, fileDialog.file)
                        val json = file.readText()
                        try {
                            val pipeline = PipelineSerializer.fromJson(json)
                            pipelineName = pipeline.name
                            plugins.clear()
                            plugins.addAll(pipeline.plugins)
                            jsonOutput = json
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }) {
                    Text("Open File")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            TextField(
                value = jsonOutput,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth().height(150.dp),
                readOnly = true,
                label = { Text("JSON Output") }
            )
        }
    }
}
