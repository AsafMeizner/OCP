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
    }
}

@Composable
fun PluginStoreCard(plugin: PluginDto) {
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
                Text(text = "$${plugin.price}", style = MaterialTheme.typography.labelLarge)
                IconButton(onClick = { /* TODO: Download */ }) {
                    Icon(Icons.Default.Download, contentDescription = "Download")
                }
            }
        }
    }
}
