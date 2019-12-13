package com.lib.page

import android.view.View
import androidx.annotation.CallSuper

abstract class PageGestureFragment: PageFragment(), PageGestureView.Delegate {

    private lateinit var gestureView: PageGestureView
    private lateinit var contentsView:View
    private lateinit var backgroundView:View
    protected open var useGesture:Boolean = false

    abstract fun getGestureView(): PageGestureView
    abstract fun getContentsView(): View
    abstract fun getBackgroundView(): View

    @CallSuper
    override fun onCreatedView() {

        contentsView = getContentsView()
        backgroundView = getBackgroundView()

        gestureView = getGestureView()
        gestureView.useGesture = useGesture
        gestureView.contentsView = contentsView
        gestureView.delegate = this
    }

    @CallSuper
    override fun  onDestroyedView() {
    }

    override fun onMove(view: PageGestureView, pct:Float) {
        backgroundView.alpha = pct
    }

    override fun onAnimate(view: PageGestureView, pct:Float){
        backgroundView.alpha = pct
    }

    override fun onClose(view: PageGestureView) {
        pageID?.let { PagePresenter.getInstance<Any>().closePopup(it) }

    }

    override fun onReturn(view: PageGestureView) {

    }
}