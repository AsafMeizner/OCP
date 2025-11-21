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
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        try {
            encoder?.stop()
            encoder?.release()
            encoder = null
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
