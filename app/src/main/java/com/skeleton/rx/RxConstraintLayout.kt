package com.skeleton.rx

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.CallSuper
import androidx.constraintlayout.widget.ConstraintLayout
import com.lib.page.Page
import com.skeleton.module.ImageFactory
import io.reactivex.disposables.CompositeDisposable


abstract class RxConstraintLayout : ConstraintLayout, Rx, Page ,DiChild{
    protected var disposables: CompositeDisposable? = null
    protected var imageFactory: ImageFactory? = null
    constructor(context: Context): super(context) { init(context) }
    constructor(context: Context, attrs: AttributeSet): super(context, attrs) { init(context) }

    protected open fun init(context: Context) {
        val resId = getLayoutResId()
        if(resId != -1) LayoutInflater.from(context).inflate(resId, this, true)
    }

    @CallSuper
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        disposables = CompositeDisposable()
        onCreatedView()
        onAttached()
        onSubscribe()
    }

    @CallSuper
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        disposables?.clear()
        disposables = null
        imageFactory = null
        onDetached()
        onDestroyedView()
    }

    @CallSuper
    override fun injectImageFactory(imageFactory: ImageFactory?) {this.imageFactory = imageFactory}


}