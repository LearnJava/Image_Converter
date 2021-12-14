package com.konstantin.image_converter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.util.concurrent.Callable

class ConvertToPng(
    private val drawableImage: Bitmap,
    private val format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
    private val quality: Int = 100
): Callable<Bitmap> {

    override fun call(): Bitmap {
        println(Thread.currentThread().name)
        val stream = ByteArrayOutputStream()
        drawableImage.compress(format, quality, stream)
        val byteArray = stream.toByteArray()
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}