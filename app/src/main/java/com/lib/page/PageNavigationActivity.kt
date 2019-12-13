package com.lib.page

import android.animation.Animator

import android.view.View
import android.view.ViewPropertyAnimator
import android.view.ViewTreeObserver
import androidx.annotation.CallSuper
import com.lib.util.AnimationDuration
import com.lib.model.Gesture



abstract class PageNavigationActivity<T>: PageActivity<T>(), PageGestureView.Delegate, Animator.AnimatorListener {
    private lateinit var navigationView: PageGestureView
    private lateinit var contentsView: View
    private lateinit var navigationViewBgView: View

    abstract fun getNavigationView(): PageGestureView
    abstract fun getContentsView(): View
    abstract fun getNavigationViewBgView(): View
    abstract fun getCloseType(): Gesture.Type

    private lateinit var closeType: Gesture.Type
    private var animation: ViewPropertyAnimator? = null

    @CallSuper
    override fun onCreatedView() {
        closeType = getCloseType()
        navigationView = getNavigationView()
        contentsView = getContentsView()
        navigationViewBgView = getNavigationViewBgView()
        navigationViewBgView.alpha = 0f
        navigationView.delegate = this
        navigationView.contentsView = contentsView
        navigationView.closeType = closeType

        contentsView.viewTreeObserver.addOnGlobalLayoutListener {
            this.contentsView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            navigationView.setGestureClose()
            didHideAnimation()
        }
    }

    @CallSuper
    override fun onDestroyedView() {
        removeAnimation()
    }

    override fun onShowNavigation() {
        navigationViewBgView.visibility = View.VISIBLE
        navigationView.visibility = View.VISIBLE
        removeAnimation()
        navigationView.onGestureReturn()
    }

    override fun onHideNavigation() {
        removeAnimation()
        navigationView.onGestureClose()
    }

    override fun onAnimate(view: PageGestureView, pct: Float) {
        movePageArea(pct)
    }

    override fun onMove(view: PageGestureView, pct:Float) {
        navigationViewBgView.alpha = pct
        movePageArea(pct)
    }

    protected open fun movePageArea(pct:Float) {
        var pos = navigationView.contentSize * pct
        pos = if (closeType == Gesture.Type.PAN_RIGHT || closeType == Gesture.Type.PAN_DOWN) -pos else pos
        if (navigationView.isHorizontal) pageArea.translationX = pos else pageArea.translationY = pos
    }

    override fun onClose(view: PageGestureView) {
        removeAnimation()
        pagePresenter.isNavigationShow = false
        animation = navigationViewBgView.animate()
        animation?.setListener(this)
        animation?.alpha(0f)?.setDuration(AnimationDuration.SHORT)?.start()
    }

    override fun onReturn(view: PageGestureView) {
        removeAnimation()
        pagePresenter.isNavigationShow = true
        animation = navigationViewBgView.animate()
        animation?.setListener(this)
        animation?.alpha(1f)?.setDuration(AnimationDuration.SHORT)?.start()
    }

    private fun removeAnimation() {
        animation?.cancel()
        animation?.setListener(null)
        animation = null
    }

    override fun onAnimationStart(animation: Animator) {}
    override fun onAnimationRepeat(animation: Animator) {}
    override fun onAnimationCancel(animation: Animator) {}
    override fun onAnimationEnd(animation: Animator) {
        if (this.pagePresenter.isNavigationShow) didShowAnimation() else didHideAnimation()
    }

    protected open fun didShowAnimation() {}
    protected open fun didHideAnimation() {
        navigationViewBgView.visibility = View.GONE
        navigationView.visibility = View.GONE
    }
}

private fun <T> ViewTreeObserver.removeOnGlobalLayoutListener(pageNavigationActivity: PageNavigationActivity<T>) {

}
