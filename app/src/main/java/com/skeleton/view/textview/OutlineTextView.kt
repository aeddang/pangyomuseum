package com.skeleton.view.textview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.TextView
import com.enoughmedia.pangyomuseum.R

class OutlineTextView : TextView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet):  super(context, attrs) {
        initView(attrs)
    }

    private var hasStroke = false
    private var mStrokeWidth = 0.0f
    private var mStrokeColor = 0


   private fun initView(attrs: AttributeSet) {
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.OutlineTextView)
       hasStroke = typeArray.getBoolean(R.styleable.OutlineTextView_textStroke, false)
       mStrokeWidth = typeArray.getDimension(R.styleable.OutlineTextView_textStrokeWidth, 0.0f)
       mStrokeColor = typeArray.getColor(R.styleable.OutlineTextView_textStrokeColor, 0xffffff)
   }


    override fun onDraw(canvas:Canvas) {
        if (hasStroke) {
            val states = textColors
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = mStrokeWidth
            setTextColor(mStrokeColor)
            super.onDraw(canvas)

            paint.style = Paint.Style.FILL
            setTextColor(states)
        }
        super.onDraw(canvas)
    }


}

