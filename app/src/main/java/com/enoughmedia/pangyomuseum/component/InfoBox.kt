package com.enoughmedia.pangyomuseum.component

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import com.enoughmedia.pangyomuseum.R
import com.jakewharton.rxbinding3.view.clicks
import com.lib.util.AnimationDuration
import com.lib.util.animateAlpha
import com.lib.util.animateFrame
import com.lib.util.animateY
import com.skeleton.rx.RxFrameLayout
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.cp_info_box.view.*
import kotlinx.android.synthetic.main.cp_open_camera.view.*
import kotlinx.android.synthetic.main.item_quide.view.*
import java.util.concurrent.TimeUnit


class InfoBox: RxFrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context,attrs)
    override fun getLayoutResId(): Int { return R.layout.cp_info_box }
    private val appTag = javaClass.simpleName

    override fun onCreatedView() {

    }

    override fun onDestroyedView() {

    }

    override fun onSubscribe() {
        super.onSubscribe()
        btnOpen.clicks().subscribe {
            isOpen = !isOpen
        }.apply { disposables?.add(this) }
    }

    var isOpen = true
    set(value) {
        field = value
        if(value) onOpen() else onClose()
    }

    var moundsTitle:String = ""
    set(value) {
        field = value
        title.text = value
    }

    var moundsDesc:String = ""
        set(value) {
            field = value
            desc.text = value
        }


    private fun onOpen(){
        btnOpen.setImageResource(R.drawable.btn_down)
        /*
        animateY(0, true).apply {
            interpolator = AccelerateInterpolator()
            startAnimation(this)
        }
        */

        val layout = layoutParams as FrameLayout.LayoutParams
        animateFrame(Rect(layout.leftMargin,0,layout.width, layout.height), false, true).start()
    }

    private fun onClose(){
        btnOpen.setImageResource(R.drawable.btn_open)
        /*
        animateY(- desc.height, true).apply {
            interpolator = DecelerateInterpolator()
            startAnimation(this)
        }
        */
        val layout = layoutParams as FrameLayout.LayoutParams
        animateFrame(Rect(layout.leftMargin,- desc.height ,layout.width, layout.height), false, true).start()

    }





}


