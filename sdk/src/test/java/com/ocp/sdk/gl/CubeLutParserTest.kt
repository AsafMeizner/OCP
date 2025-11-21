package com.ocp.sdk.gl

import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets
import kotlin.test.Test
import kotlin.test.assertEquals

class CubeLutParserTest {

    @Test
    fun testParseValidLut() {
        val lutContent = """
            # Test LUT
            LUT_3D_SIZE 2
            0.0 0.0 0.0
            1.0 0.0 0.0
            0.0 1.0 0.0
            1.0 1.0 0.0
            0.0 0.0 1.0
            1.0 0.0 1.0
            0.0 1.0 1.0
            1.0 1.0 1.0
        """.trimIndent()

        val inputStream = ByteArrayInputStream(lutContent.toByteArray(StandardCharsets.UTF_8))
        val lutData = CubeLutParser.parse(inputStream)

        assertEquals(2, lutData.size)
        // 2^3 * 3 floats = 8 * 3 = 24 floats
        assertEquals(24, lutData.data.capacity())
        
        // Check first value
        assertEquals(0.0f, lutData.data.get(0))
    }
}
