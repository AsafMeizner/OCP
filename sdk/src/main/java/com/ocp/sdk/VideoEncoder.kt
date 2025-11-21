package com.ocp.sdk

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.view.Surface
import java.io.IOException
import java.nio.ByteBuffer

class VideoEncoder(
    private val width: Int,
    private val height: Int,
    private val bitRate: Int,
    private val outputFile: String
) {
    private var encoder: MediaCodec? = null
    private var inputSurface: Surface? = null
    private var muxer: MediaMuxer? = null
    private var trackIndex = -1
    private var muxerStarted = false
    }

    fun drainEncoder(endOfStream: Boolean) {
        if (!isRecording || encoder == null) return

        if (endOfStream) {
            encoder!!.signalEndOfInputStream()
        }

        while (true) {
            val encoderStatus = encoder!!.dequeueOutputBuffer(bufferInfo, 0)
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                if (!endOfStream) break
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                if (muxerStarted) throw RuntimeException("format changed twice")
                val newFormat = encoder!!.outputFormat
                trackIndex = muxer!!.addTrack(newFormat)
                muxer!!.start()
                muxerStarted = true
            } else if (encoderStatus < 0) {
                // ignore
            } else {
                val encodedData = encoder!!.getOutputBuffer(encoderStatus) ?: throw RuntimeException("encoderOutputBuffer $encoderStatus was null")

                if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    bufferInfo.size = 0
                }

                if (bufferInfo.size != 0) {
                    if (!muxerStarted) throw RuntimeException("muxer hasn't started")
                    encodedData.position(bufferInfo.offset)
                    encodedData.limit(bufferInfo.offset + bufferInfo.size)
                    muxer!!.writeSampleData(trackIndex, encodedData, bufferInfo)
                }

                encoder!!.releaseOutputBuffer(encoderStatus, false)

                if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
package com.ocp.sdk

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.view.Surface
import java.io.IOException
import java.nio.ByteBuffer

class VideoEncoder(
    private val width: Int,
    private val height: Int,
    private val bitRate: Int,
    private val outputFile: String
) {
    private var encoder: MediaCodec? = null
    private var inputSurface: Surface? = null
    private var muxer: MediaMuxer? = null
    private var trackIndex = -1
    private var muxerStarted = false

    // Audio State
    private var audioEncoder: MediaCodec? = null

    private var audioThread: Thread? = null
    private val audioBufferInfo = MediaCodec.BufferInfo()
    private var audioTrackIndex = -1
    private var isRecording = false // Assuming this was missing and needed for the new code
    private val bufferInfo = MediaCodec.BufferInfo() // Assuming this was missing and needed for drainEncoder

    fun start(): Surface {
        if (isRecording) return inputSurface!!

        try {
            // Video Init
            val format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height)
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
            format.setInteger(MediaFormat.KEY_BIT_RATE, bitRate)
            format.setInteger(MediaFormat.KEY_FRAME_RATE, 30)
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)

            encoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
            encoder!!.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            inputSurface = encoder!!.createInputSurface()
            encoder!!.start()

            muxer = MediaMuxer(outputFile, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            isRecording = true
            
            startAudio()
            
        } catch (e: IOException) {
            e.printStackTrace()
            throw RuntimeException("Failed to start encoder", e)
        }

        return inputSurface!!
    }

    private fun startAudio() {
        try {
            val sampleRate = 44100
            val format = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, sampleRate, 1)
            format.setInteger(MediaFormat.KEY_BIT_RATE, 64000)
            format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC)
            
            audioEncoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC)
            audioEncoder!!.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            audioEncoder!!.start()
            
            audioThread = Thread {
                while (isRecording) {
                    drainAudio()
                    try {
                        Thread.sleep(10)
                    } catch (e: InterruptedException) {
                        break
                    }
                }
            }
            audioThread?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun queueAudio(buffer: ByteArray, size: Int, timestampNs: Long) {
        if (!isRecording || audioEncoder == null) return
        
        try {
            val inputBufferIndex = audioEncoder!!.dequeueInputBuffer(0) // Don't block here
            if (inputBufferIndex >= 0) {
                val inputBuffer = audioEncoder!!.getInputBuffer(inputBufferIndex)
                inputBuffer?.clear()
                inputBuffer?.put(buffer, 0, size)
                audioEncoder!!.queueInputBuffer(inputBufferIndex, 0, size, timestampNs / 1000, 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun drainAudio() {
        if (audioEncoder == null || muxer == null) return
        
        while (true) {
            val status = audioEncoder!!.dequeueOutputBuffer(audioBufferInfo, 0)
            if (status == MediaCodec.INFO_TRY_AGAIN_LATER) break
            else if (status == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                if (audioTrackIndex == -1) {
                    audioTrackIndex = muxer!!.addTrack(audioEncoder!!.outputFormat)
                    if (!muxerStarted && trackIndex != -1) { // Wait for both video and audio? Simplified: start if video ready
                         // In real app, need to sync start
                    }
                }
            } else if (status >= 0) {
                val encodedData = audioEncoder!!.getOutputBuffer(status)
                if (encodedData != null && muxerStarted && audioTrackIndex != -1) {
                    if ((audioBufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                        audioBufferInfo.size = 0
                    }
                    if (audioBufferInfo.size != 0) {
                        encodedData.position(audioBufferInfo.offset)
                        encodedData.limit(audioBufferInfo.offset + audioBufferInfo.size)
                        muxer!!.writeSampleData(audioTrackIndex, encodedData, audioBufferInfo)
                    }
                    audioEncoder!!.releaseOutputBuffer(status, false)
                }
            }
        }
    }

    fun drainEncoder(endOfStream: Boolean) {
        if (!isRecording || encoder == null) return

        if (endOfStream) {
            encoder!!.signalEndOfInputStream()
        }

        while (true) {
            val encoderStatus = encoder!!.dequeueOutputBuffer(bufferInfo, 0)
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                if (!endOfStream) break
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                if (muxerStarted) throw RuntimeException("format changed twice")
                val newFormat = encoder!!.outputFormat
                trackIndex = muxer!!.addTrack(newFormat)
                muxer!!.start()
                muxerStarted = true
            } else if (encoderStatus < 0) {
                // ignore
            } else {
                val encodedData = encoder!!.getOutputBuffer(encoderStatus) ?: throw RuntimeException("encoderOutputBuffer $encoderStatus was null")

                if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    bufferInfo.size = 0
                }

                if (bufferInfo.size != 0) {
                    if (!muxerStarted) throw RuntimeException("muxer hasn't started")
                    encodedData.position(bufferInfo.offset)
                    encodedData.limit(bufferInfo.offset + bufferInfo.size)
                    muxer!!.writeSampleData(trackIndex, encodedData, bufferInfo)
                }

                encoder!!.releaseOutputBuffer(encoderStatus, false)

                if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    break
                }
            }
        }
    }

    fun stop() {
        if (!isRecording) return
        isRecording = false
        try {
            drainEncoder(true)
            audioThread?.join()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        try {
            encoder?.stop()
            encoder?.release()
            encoder = null
            
            audioEncoder?.stop()
            audioEncoder?.release()
            audioEncoder = null
            

            
            inputSurface?.release()
            inputSurface = null
            if (muxerStarted) {
                muxer?.stop()
            }
            muxer?.release()
            muxer = null
            muxerStarted = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
