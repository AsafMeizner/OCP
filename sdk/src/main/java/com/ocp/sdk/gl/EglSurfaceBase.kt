package com.ocp.sdk.gl

import android.opengl.EGL14
import android.opengl.EGLSurface

/**
 * Common base class for EGL surfaces.
 */
open class EglSurfaceBase(protected val eglCore: EglCore) {

    protected var eglSurface: EGLSurface = EGL14.EGL_NO_SURFACE
    private var width = -1
    private var height = -1

    fun createWindowSurface(surface: Any) {
        if (eglSurface !== EGL14.EGL_NO_SURFACE) {
            throw IllegalStateException("surface already created")
        }
        eglSurface = eglCore.createWindowSurface(surface)
    }

    fun createOffscreenSurface(width: Int, height: Int) {
        if (eglSurface !== EGL14.EGL_NO_SURFACE) {
            throw IllegalStateException("surface already created")
        }
        eglSurface = eglCore.createOffscreenSurface(width, height)
        this.width = width
        this.height = height
    }

    fun getWidth(): Int {
        if (width < 0) {
            val value = IntArray(1)
            EGL14.eglQuerySurface(EGL14.eglGetCurrentDisplay(), eglSurface, EGL14.EGL_WIDTH, value, 0)
            width = value[0]
        }
        return width
    }

    fun getHeight(): Int {
        if (height < 0) {
            val value = IntArray(1)
            EGL14.eglQuerySurface(EGL14.eglGetCurrentDisplay(), eglSurface, EGL14.EGL_HEIGHT, value, 0)
            height = value[0]
        }
        return height
    }

    fun releaseEglSurface() {
        eglCore.releaseSurface(eglSurface)
        eglSurface = EGL14.EGL_NO_SURFACE
        width = -1
        height = -1
    }

    fun makeCurrent() {
        eglCore.makeCurrent(eglSurface)
    }

    fun swapBuffers(): Boolean {
        val result = eglCore.swapBuffers(eglSurface)
        if (!result) {
            println("WARNING: swapBuffers() failed")
        }
        return result
    }
}
