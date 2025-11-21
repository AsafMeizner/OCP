package com.ocp.app.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ocp.sdk.CaptureController
import com.ocp.sdk.Plugin

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

    Scaffold(
        topBar = {
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
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
    }
}

@Composable
fun PluginCard(plugin: PluginUiModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = plugin.name, style = MaterialTheme.typography.titleMedium)
            Text(text = plugin.description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

data class PluginUiModel(val name: String, val description: String)
