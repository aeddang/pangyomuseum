package com.enoughmedia.pangyomuseum.component

import android.content.Context
import android.util.AttributeSet
import com.enoughmedia.pangyomuseum.R
import com.lib.util.animateAlpha
import com.skeleton.rx.RxFrameLayout
import kotlinx.android.synthetic.main.cp_marker.view.*

class Marker : RxFrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context,attrs)
    override fun getLayoutResId(): Int { return R.layout.cp_marker}
    private val appTag = javaClass.simpleName

    var id:String = ""

    override fun onCreatedView() {
        this.alpha = 0.0f
        findOff()
        this.animateAlpha(1.0f)

    }
    override fun onDestroyedView() {}

    fun findOff() {
        image.setImageResource(R.drawable.marker)
    }

    fun findOn() {
        image.setImageResource(R.drawable.marker_on)
    }
}