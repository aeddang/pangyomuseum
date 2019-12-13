package com.lib.page

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import com.lib.model.Gesture

const val DURATION_DIV = 5
open class PageGestureView: FrameLayout, Gesture.Delegate {

    private val appTag = javaClass.simpleName

    var closeType = Gesture.Type.PAN_DOWN
    var delegate: Delegate? = null
    lateinit var contentsView:View
    var isVertical = false; private set
    var isHorizontal = false; private set
    var useGesture = true
    private var isClosed = false
    private var animation: ViewPropertyAnimator? = null

    private lateinit var gesture: Gesture
    private var trigger:Boolean = true
    private var startPosition = 0f
    private var finalGesture = Gesture.Type.NONE
    private var animationCloseRunnable: Runnable = Runnable { didCloseAnimation() }
    private var animationReturnRunnable: Runnable = Runnable { didReturnAnimation() }

    private var _contentSize = 0f
    val contentSize:Float
        get() {
        if (_contentSize != 0f) return _contentSize
        _contentSize = if (isVertical) contentsView.height.toFloat() else contentsView.width.toFloat()
        return _contentSize
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs:AttributeSet) : super(context,attrs)

    open fun setGestureStart(startPos:Float) {
        if(isVertical) contentsView.translationY = startPos else contentsView.translationX = startPos
    }

    open fun setGestureClose() {
        val closePosX = getClosePos().first
        val closePosY = getClosePos().second
        contentsView.translationX = closePosX
        contentsView.translationY = closePosY
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isVertical = closeType == Gesture.Type.PAN_UP || closeType == Gesture.Type.PAN_DOWN
        isHorizontal = closeType == Gesture.Type.PAN_LEFT || closeType == Gesture.Type.PAN_RIGHT
        gesture = Gesture(this, isVertical, isHorizontal)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animation?.cancel()
        animation = null
        delegate = null
        gesture.onDestroy()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if(!useGesture) return super.onInterceptTouchEvent(ev)
        trigger =  gesture.adjustEvent(ev)
        return !trigger
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if(!useGesture) return super.onTouchEvent(ev)
        trigger =  gesture.adjustEvent(ev)
        return true
    }

    override fun stateChange(g: Gesture, e: Gesture.Type) {
        when (e) {
            Gesture.Type.START -> touchStart()
            Gesture.Type.MOVE_V -> if(isVertical) touchMove(g.deltaY)
            Gesture.Type.MOVE_H -> if(isHorizontal) touchMove(g.deltaX)
            Gesture.Type.END, Gesture.Type.CANCEL -> touchEnd()
            else -> { }
        }
    }

    private fun touchStart() {

        finalGesture = Gesture.Type.NONE
        delegate?.onMoveStart(this)
        contentsView.let {startPosition = if(isVertical) it.translationY else it.translationX }
    }

    private fun getMoveAmount(pos:Float) :Float {
        var p = pos
        var max = 0
        when(closeType) {
            Gesture.Type.PAN_DOWN -> {
                max = contentsView.height
                if (p > max) p = max.toFloat() else if (p < 0f) p = 0f
                contentsView.translationY = Math.floor(p.toDouble()).toFloat()
            }
            Gesture.Type.PAN_UP -> {
                max = -contentsView.height
                if (p < max) p = max.toFloat() else if (p > 0f) p = 0f
                contentsView.translationY = Math.floor(p.toDouble()).toFloat()
            }
            Gesture.Type.PAN_RIGHT -> {
                max = contentsView.width
                if (p > max) p = max.toFloat() else if (p < 0f) p = 0f
                contentsView.translationX = Math.floor(p.toDouble()).toFloat()
            }
            Gesture.Type.PAN_LEFT -> {
                max = -contentsView.width
                if (p < max) p = max.toFloat() else if (p > 0f) p = 0f
                contentsView.translationX = Math.floor(p.toDouble()).toFloat()
            }
            else -> { }
        }
        return (max - p) / max
    }

    private fun touchMove(delta:Int) {
        val p = delta + startPosition
        delegate?.onMove(this,getMoveAmount(p))
    }

    private fun touchEnd() {
        if (finalGesture == closeType) onGestureClose() else onGestureReturn()
    }

    override fun gestureComplete(g: Gesture, e: Gesture.Type) {
        this.finalGesture = e
    }

    private fun getClosePos():Pair<Float,Float> {
        var closePosX = 0f
        var closePosY = 0f
        when(closeType) {
            Gesture.Type.PAN_DOWN -> closePosY = contentsView.height.toFloat()
            Gesture.Type.PAN_UP -> closePosY = -contentsView.height.toFloat()
            Gesture.Type.PAN_RIGHT -> closePosX = contentsView.width.toFloat()
            Gesture.Type.PAN_LEFT -> closePosX = -contentsView.width.toFloat()
            else -> { }
        }
        return Pair(closePosX + 10,closePosY + 10)
    }

    @SuppressLint("NewApi")
    open fun onGestureClose(isClosure:Boolean = true):Long {
        delegate?.onMoveStart(this)
        isClosed = true
        val closePosX = getClosePos().first
        val closePosY = getClosePos().second

        val start = if (isVertical) contentsView.translationY else contentsView.translationX
        val end = if (isVertical) closePosY else closePosX
        var duration = if (isVertical) Math.abs(closePosY - contentsView.translationY).toLong()
                        else Math.abs(closePosX - contentsView.translationX).toLong()
        duration /= DURATION_DIV

        animation?.cancel()
        animation = contentsView.animate()
                .translationX(closePosX)
                .translationY(closePosY)
                .setInterpolator(DecelerateInterpolator())
                .setUpdateListener { this.onUpdateAnimation(it, start, end) }
                .setDuration(duration)

        if(isClosure) animation?.withEndAction(animationCloseRunnable)
        animation?.start()
        return duration
    }
    protected open fun didCloseAnimation() {
        delegate?.onClose(this)
    }

    @SuppressLint("NewApi")
    open fun onGestureReturn(isClosure:Boolean = true):Long {
        isClosed = false
        val start = if (isVertical) contentsView.translationY else contentsView.translationX
        var duration = if (isVertical) Math.abs(contentsView.translationY).toLong() else Math.abs(contentsView.translationX).toLong()
        duration /= DURATION_DIV

        animation?.cancel()
        animation = contentsView.animate()
            .translationX(0f)
            .translationY(0f)
            .setInterpolator(AccelerateInterpolator())
            .setUpdateListener{this.onUpdateAnimation(it, start, 0f)}
            .setDuration(duration)
        if (isClosure) animation?.withEndAction(animationReturnRunnable)
        animation?.start()
        return duration
    }

    protected open fun onUpdateAnimation(animation: ValueAnimator, start: Float, end: Float) {
        val dr = if (end > start) 1f else -1f
        val range = Math.abs(end - start)
        val pct = animation.animatedValue as Float
        val pos = start + (dr*range*pct)

        delegate?.onAnimate(this, getMoveAmount(pos))
    }

    protected open fun didReturnAnimation() {
        delegate?.onReturn(this)
        delegate?.onMoveCompleted(this)
    }

    interface Delegate {
        fun onMoveStart(view: PageGestureView){}
        fun onMoveCompleted(view: PageGestureView){}
        fun onMove(view: PageGestureView, pct:Float){}
        fun onAnimate(view: PageGestureView, pct:Float){}
        fun onClose(view: PageGestureView){}
        fun onReturn(view: PageGestureView){}
    }
}