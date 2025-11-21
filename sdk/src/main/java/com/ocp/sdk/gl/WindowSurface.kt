package com.ocp.sdk.gl

import android.view.Surface

class WindowSurface(eglCore: EglCore, surface: Surface, releaseSurface: Boolean) : EglSurfaceBase(eglCore) {
    private val releaseSurface: Boolean

    init {
        createWindowSurface(surface)
        this.releaseSurface = releaseSurface
    }

    fun release() {
        releaseEglSurface()
        if (releaseSurface) {
            // surface.release() // If we owned the surface, we would release it here.
        }
    }
}
