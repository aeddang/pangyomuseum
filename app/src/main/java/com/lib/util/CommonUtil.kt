package com.lib.util
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.graphics.RectF
import android.net.Uri
import android.provider.MediaStore
import android.util.Size
import android.view.View
import android.view.WindowManager
import androidx.exifinterface.media.ExifInterface

object CommonUtil{

    //사진방향
    fun exifOrientationToDegrees(exifOrientation: Int): Int {
        return when (exifOrientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
    }

    // 로컬 파일경로 Uri 로 content://
    fun getImageContentUri(context : Context, absPath:String) : Uri? {

        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            , arrayOf(MediaStore.Images.Media._ID)
            , MediaStore.Images.Media.DATA + "=? "
            , arrayOf(absPath), null
        )

        if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
            return Uri.withAppendedPath(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                id.toString()
            )

        } else if (!absPath.isEmpty()) {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.DATA, absPath)
            return context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
            )
        } else {
            return null
        }
    }

    fun enterDefaultMode(ac: Activity) {

        ac.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        ac.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_VISIBLE)
    }

    fun enterFullScreenMode(ac: Activity) {
        ac.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        ac.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

}


fun Uri.getRealPathFromUri(context: Context): String {
    var path = ""
    val array = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = context.contentResolver.query(this, array, null, null, null)
    cursor?.let {
        it.moveToFirst()
        path = it.getString(it.getColumnIndex(array[0]))
        cursor.close()
    }
    return path
}

fun Size.getCropRatioSize(crop: Size):RectF{
    val cropRatio = crop.width.toFloat()/crop.height.toFloat()
    val originWidth = width.toFloat()
    val originHeight = height.toFloat()

    var ratioWidth = originWidth
    var ratioHeight = originWidth / cropRatio
    if( ratioHeight > originHeight ){
        ratioHeight = originHeight
        ratioWidth = originHeight * cropRatio
    }
    val marginX = (originWidth - ratioWidth)/2
    val marginY = (originHeight - ratioHeight)/2
    return RectF(marginX,marginY, marginX + ratioWidth ,marginY + ratioHeight)

}