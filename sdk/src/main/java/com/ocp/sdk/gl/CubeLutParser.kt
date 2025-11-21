package com.ocp.sdk.gl

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder

object CubeLutParser {
    fun parse(inputStream: InputStream): LutData {
        val reader = BufferedReader(InputStreamReader(inputStream))
        var size = 0
        val data = mutableListOf<Float>()
        
        reader.forEachLine { line ->
            val trimmed = line.trim()
            if (trimmed.startsWith("#") || trimmed.isEmpty()) return@forEachLine
            
            if (trimmed.startsWith("LUT_3D_SIZE")) {
                size = trimmed.split(" ").last().toInt()
            } else if (trimmed.startsWith("TITLE") || trimmed.startsWith("DOMAIN")) {
                // Ignore
            } else {
                // Data line
                val parts = trimmed.split(" ")
                if (parts.size >= 3) {
                    data.add(parts[0].toFloat())
                    data.add(parts[1].toFloat())
                    data.add(parts[2].toFloat())
                }
            }
        }
        
        val buffer = ByteBuffer.allocateDirect(data.size * 4)
        buffer.order(ByteOrder.nativeOrder())
        val floatBuffer = buffer.asFloatBuffer()
        data.forEach { floatBuffer.put(it) }
        floatBuffer.position(0)
        
        return LutData(size, floatBuffer)
    }
    
    data class LutData(val size: Int, val data: java.nio.FloatBuffer)
}
