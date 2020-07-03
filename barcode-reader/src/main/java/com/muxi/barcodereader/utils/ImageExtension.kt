package com.muxi.barcodereader.utils

import androidx.camera.core.ImageProxy
import java.nio.ByteBuffer

fun ImageProxy.fromYUVToNv21(): ByteArray? {
    val nv21: ByteArray

    val yBuffer: ByteBuffer = this.planes[0].buffer
    val uBuffer: ByteBuffer = this.planes[1].buffer
    val vBuffer: ByteBuffer = this.planes[2].buffer

    yBuffer.rewind()
    uBuffer.rewind()
    vBuffer.rewind()

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()
    nv21 = ByteArray(ySize + uSize + vSize)

    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)
    return nv21
}