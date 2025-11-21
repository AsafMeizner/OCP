package com.ocp.app.ui.marketplace

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(
    onBack: () -> Unit
) {
    // Mock Data
    val plugins = remember {
        listOf(
            MarketplacePlugin("Retro VHS", "Analog tape effects", "Free"),
            MarketplacePlugin("Cyberpunk LUT", "Neon city vibes", "$1.99"),
            MarketplacePlugin("Face Smooth", "Beauty filter", "Free"),
            MarketplacePlugin("Glitch Art", "Digital distortion", "$0.99"),
            MarketplacePlugin("Cinematic Teal", "Blockbuster look", "Free"),
            MarketplacePlugin("B&W Noir", "Classic film style", "Free")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Plugin Marketplace") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(plugins) { plugin ->
                PluginStoreCard(plugin)
            }
        }
    }
}

@Composable
fun PluginStoreCard(plugin: MarketplacePlugin) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(plugin.name.take(1), style = MaterialTheme.typography.displayMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = plugin.name, style = MaterialTheme.typography.titleMedium)
            Text(text = plugin.description, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = plugin.price, style = MaterialTheme.typography.labelLarge)
                IconButton(onClick = { /* TODO: Download */ }) {
                    Icon(Icons.Default.Download, contentDescription = "Download")
                }
            }
        }
    }
}

data class MarketplacePlugin(val name: String, val description: String, val price: String)
