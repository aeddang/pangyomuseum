package com.lib.page

import android.view.View
import androidx.annotation.CallSuper

abstract class PageDividedGestureFragment: PageGestureFragment(){

    private lateinit var dividedView:View
    private var positionOffset = 0f
    abstract fun getDividedView(): View

    @CallSuper
    override fun onCreatedView() {
        super.onCreatedView()
        dividedView = getDividedView()
        dividedView.viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    @CallSuper
    override fun onDestroyedView() {
        super.onDestroyedView()
        dividedView.viewTreeObserver.removeOnGlobalLayoutListener(this)
    }

    @CallSuper
    override fun onGlobalLayout() {
        super.onGlobalLayout()
        onDividedView()
    }

    protected fun onDividedView() {
        positionOffset = dividedView.translationY
    }

    override fun onMove(view: PageGestureView, pct:Float) {
        super.onMove(view, pct)
        moveGestureArea(pct)
    }
    override fun onAnimate(view: PageGestureView, pct: Float) {
        super.onAnimate(view, pct)
        moveGestureArea(pct)
    }

    protected open fun moveGestureArea(pct:Float) {
        dividedView.alpha = pct
        dividedView.translationY = getContentsView().translationY + positionOffset
    }

}