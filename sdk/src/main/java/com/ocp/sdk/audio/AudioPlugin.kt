package com.ocp.sdk.audio

interface AudioPlugin {
    fun getName(): String
    fun process(audioData: ShortArray, size: Int): ShortArray
}
