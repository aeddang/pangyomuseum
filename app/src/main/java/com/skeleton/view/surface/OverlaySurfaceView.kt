package com.skeleton.view.surface

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PixelFormat
import android.util.AttributeSet
import android.view.SurfaceView
import android.graphics.PorterDuff



open class OverlaySurfaceView: SurfaceView{
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context,attrs)

    private var delegate: Delegate? = null
    interface Delegate{
        fun drawCanvas(canvas: Canvas, v:OverlaySurfaceView){}
    }
    fun setOnDrawListener( _delegate: Delegate? ){ delegate = _delegate }

    fun clearCanvas(){
        try {
            val canvas = holder.lockCanvas()
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            holder.unlockCanvasAndPost(canvas)
        } catch (e:Exception){

        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {  delegate?.drawCanvas(it, this) }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        this.setZOrderOnTop(true)
        setBackgroundColor(Color.TRANSPARENT)
        holder.setFormat(PixelFormat.TRANSLUCENT)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        delegate = null
    }

}