package com.ocp.app.ui.editor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Node(
    val id: String,
    val name: String,
    var position: Offset,
    val inputs: List<String> = emptyList(),
    val outputs: List<String> = emptyList()
)

data class Connection(val fromNodeId: String, val toNodeId: String)

@Composable
fun NodeGraphEditor() {
    var nodes by remember { mutableStateOf(listOf(
        Node("1", "Camera Input", Offset(100f, 300f), outputs = listOf("out")),
        Node("2", "Face Detect", Offset(400f, 200f), inputs = listOf("in"), outputs = listOf("out", "face")),
        Node("3", "Filter", Offset(400f, 400f), inputs = listOf("in"), outputs = listOf("out")),
        Node("4", "Screen Output", Offset(700f, 300f), inputs = listOf("in"))
    )) }
    
    var connections by remember { mutableStateOf(listOf(
        Connection("1", "2"),
        Connection("1", "3"),
        Connection("2", "4"),
        Connection("3", "4")
    )) }

    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
                change.consume()
                // Simple hit testing for dragging (very naive)
                val draggedNodeIndex = nodes.indexOfFirst { 
                    (change.position - it.position).getDistance() < 100 // Radius check
                }
                if (draggedNodeIndex != -1) {
                    val newNodes = nodes.toMutableList()
                    newNodes[draggedNodeIndex] = newNodes[draggedNodeIndex].copy(
                        position = newNodes[draggedNodeIndex].position + dragAmount
                    )
                    nodes = newNodes
                }
            }
        }
    ) {
        // Draw Connections
        connections.forEach { connection ->
            val from = nodes.find { it.id == connection.fromNodeId }!!
            val to = nodes.find { it.id == connection.toNodeId }!!
            
            val path = Path().apply {
                moveTo(from.position.x + 50, from.position.y)
                cubicTo(
                    from.position.x + 150, from.position.y,
                    to.position.x - 150, to.position.y,
                    to.position.x - 50, to.position.y
                )
            }
            drawPath(path, Color.Gray, style = Stroke(width = 5f))
        }

        // Draw Nodes
        nodes.forEach { node ->
            drawCircle(
                color = Color.DarkGray,
                radius = 60f,
                center = node.position
            )
            drawText(
                textMeasurer = textMeasurer,
                text = node.name,
                topLeft = node.position - Offset(40f, 10f),
                style = TextStyle(color = Color.White, fontSize = 12.sp)
            )
        }
    }
}
