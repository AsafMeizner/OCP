package com.ocp.app.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ocp.sdk.CaptureController
import com.ocp.sdk.Plugin
import kotlinx.coroutines.launch
import com.ocp.app.data.PipelineRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PipelineEditorScreen(
    captureController: CaptureController,
    onBack: () -> Unit
) {
    // Mock data for now, eventually this comes from the Engine/Controller
    var plugins by remember { mutableStateOf(listOf<PluginUiModel>(
        PluginUiModel("Brightness/Contrast", "Adjusts brightness and contrast"),
        PluginUiModel("LUT Color Grade", "Applies cinematic color grading")
    )) }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("List", "Graph")

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Pipeline Editor") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* TODO: Add Plugin Dialog */ }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Plugin")
                        }
                        IconButton(onClick = { 
                            // Simple "Save Current" logic
                            val pipeline = com.ocp.shared.PipelineDefinition(
                                id = java.util.UUID.randomUUID().toString(),
                                name = "New Pipeline",
                                plugins = emptyList() // In real app, map 'plugins' state to PluginDefinition
                            )
                            kotlinx.coroutines.GlobalScope.launch {
                                PipelineRepository.uploadPipeline(pipeline)
                            }
                        }) {
                            Icon(Icons.Default.CloudUpload, contentDescription = "Save to Cloud")
                        }
                    }
                )
                TabRow(selectedTabIndex = selectedTab) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (selectedTab == 0) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text("Active Chain", style = MaterialTheme.typography.titleMedium)
                    }
                    
                    items(plugins) { plugin ->
                        PluginCard(plugin)
                    }
                }
            } else {
                NodeGraphEditor()
            }
        }
    }
}

@Composable
fun PluginCard(plugin: PluginUiModel) {
    var intensity by remember { mutableStateOf(0.5f) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = plugin.name, style = MaterialTheme.typography.titleMedium)
            Text(text = plugin.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Intensity: ${String.format("%.2f", intensity)}")
            Slider(
                value = intensity,
                onValueChange = { intensity = it },
                valueRange = 0f..1f
            )
        }
    }
}

data class PluginUiModel(val name: String, val description: String)
