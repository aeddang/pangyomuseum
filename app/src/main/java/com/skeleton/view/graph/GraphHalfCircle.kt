package com.skeleton.view.graph


import android.content.Context
import android.util.AttributeSet



class GraphHalfCircle@kotlin.jvm.JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    :  GraphCircle(context, attrs, defStyleAttr) {
    private val TAG = javaClass.simpleName

    init {
        this.duration = GraphUtil.ANIMATION_DURATION
        this.type = Type.HalfCircle
        this.startDegree = -180.0f
        this.totalDegree = 180.0
    }

    override fun onStart() {
        super.onStart()
        centerY = height.toFloat()
        val marginY = (height.toFloat() - size.height)
        rectF.set(rectF.left, marginY, rectF.right, size.height.toFloat()*2.0f)
    }

}