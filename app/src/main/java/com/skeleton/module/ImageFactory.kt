package com.skeleton.module

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder


class ImageFactory(val context: Context) {

    fun getBitmapLoader(): RequestBuilder<Bitmap> {
        return Glide.with(context).asBitmap()
    }

    fun getItemDrawableLoader (): RequestBuilder<Drawable> {
        return Glide.with(context).asDrawable()
    }

    fun getBackgroundDrawableLoader(): RequestBuilder<Drawable> {
        return Glide.with(context).asDrawable()
    }

}