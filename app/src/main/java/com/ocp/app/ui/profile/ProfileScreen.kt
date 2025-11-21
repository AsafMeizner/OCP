package com.ocp.app.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ocp.app.data.PipelineRepository
import com.ocp.shared.PipelineDefinition

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onBack: () -> Unit) {
    var pipelines by remember { mutableStateOf<List<PipelineDefinition>>(emptyList()) }

    LaunchedEffect(Unit) {
        pipelines = PipelineRepository.getMyPipelines()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("User", style = MaterialTheme.typography.headlineMedium)
                    Text("user@example.com", style = MaterialTheme.typography.bodyMedium)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text("My Pipelines", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(pipelines) { pipeline ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(pipeline.name, style = MaterialTheme.typography.titleMedium)
                            Text("ID: ${pipeline.id}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
