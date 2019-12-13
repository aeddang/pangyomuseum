package com.skeleton.view.graph


import android.content.Context
import android.util.AttributeSet



class GraphRing@kotlin.jvm.JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    :  GraphCircle(context, attrs, defStyleAttr) {
    private val TAG = javaClass.simpleName

    init {
        this.isRing = true
        this.duration = GraphUtil.ANIMATION_DURATION
        this.type = Type.Ring
    }


}