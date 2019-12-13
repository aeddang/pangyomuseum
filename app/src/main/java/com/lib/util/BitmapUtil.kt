package com.lib.util

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Rect
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


fun Bitmap.rotate(degree: Int): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(degree.toFloat())
    val scaledBitmap = Bitmap.createScaledBitmap(this, width, height, true)
    return Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)
}

fun Bitmap.crop(rectF: Rect): Bitmap {
    return Bitmap.createBitmap(this, rectF.left, rectF.top, rectF.width(), rectF.height(), null, false)
}

fun Bitmap.swapHolizental(): Bitmap {
    val matrix = Matrix()
    matrix.postScale(- 1F, 1F)
    val scaledBitmap = Bitmap.createScaledBitmap(this, width, height, true)
    return Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)
}

fun Bitmap.swapVertical(): Bitmap {
    val matrix = Matrix()
    matrix.postScale(1F, - 1F)
    val scaledBitmap = Bitmap.createScaledBitmap(this, width, height, true)
    return Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)
}



//bitmap -> File
fun Bitmap.toFile(context: Context): File {

    val wrapper = ContextWrapper(context)
    var file = wrapper.getDir("homeT", Context.MODE_PRIVATE)
    file = File(file, "profile.jpg")

    try {
        val stream: OutputStream = FileOutputStream(file)
        this.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.flush()
        stream.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return file
}