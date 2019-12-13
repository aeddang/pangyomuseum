package com.skeleton.view.graph

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Size
import androidx.annotation.CallSuper
import com.lib.view.animate.AnimatedDrawView

abstract class Graph@kotlin.jvm.JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    :  AnimatedDrawView(context, attrs, defStyleAttr) {

    private val appTag = "Graph"

    enum class Type {
        HolizentalBar,
        VerticalBar,
        Circle,
        Ring,
        HalfCircle,
        Polygon
    }

    enum class AnimationType {
        EaseInSine,
        EaseOutSine,
        EaseInElastic,
        EaseOutElastic,
        Linear
    }

    var type:Type = Type.HolizentalBar; protected set
    var aniType:AnimationType = AnimationType.EaseOutSine
    protected var initValue:Double = 0.0
    protected var endValue:Double = 0.0
    protected var targetValue:Double = 1.0
    protected var currentValue:Double = 0.0
    protected var startValue:Double = 0.0
    protected var kind = 0
    protected var isContinuous = true
    protected var camera = Camera()

    protected var delegate:Delegate? = null
    interface Delegate {
        fun drawGraph(graph: Graph, datas:ArrayList<Pair<Double, Point>>){}
    }

    fun setOnDrawGraphListener( _delegate:Delegate? ) {
        delegate = _delegate
    }

    var size = Size(0,0)
    var zeroPaint:Paint? = null
    var paints:ArrayList<Paint> = arrayListOf(Paint())
    internal var delay:Long = 0L

    var values:List<Double> = ArrayList()
        set(value) {
            if(value.isEmpty()) return
            field = getModifyValues(value)
            startAnimation(duration, delay)
        }
    abstract fun getModifyValues(value:List<Double>): List<Double>
    abstract fun setRange(endValue:Double)
    abstract fun setColor(colors:Array<Int>)

    open fun setStroke(stroke:Float, style:Paint.Style = Paint.Style.STROKE, cap:Paint.Cap = Paint.Cap.ROUND) {
        paints.forEach {
            it.style = style
            it.strokeWidth = stroke
            it.strokeCap = cap
            it.isAntiAlias = true
        }
    }

    open fun setFill(shader:Shader? = null) {
        paints.forEach {p->
            p.style = Paint.Style.FILL
            shader?.let {  p.setShader(shader) }
        }
    }

    @CallSuper
    override fun onStart() {
        kind = paints.size
        isContinuous = (values.size == 1)
        if(size.width == 0 || size.height == 0) size = Size(width, height)
    }

    fun setAnimationDuratiuon(d:Long) {
        duration = d
    }

    override fun onCompute(f: Int) {
        val delta = targetValue-startValue
        currentValue = when(aniType){
            AnimationType.EaseInSine -> GraphUtil.easeInSine(currentTime.toDouble(), startValue, delta, duration.toDouble())
            AnimationType.EaseOutSine -> GraphUtil.easeOutSine(currentTime.toDouble(), startValue, delta, duration.toDouble())
            AnimationType.EaseInElastic -> GraphUtil.easeOutElastic(currentTime.toDouble(), startValue, delta, duration.toDouble())
            AnimationType.EaseOutElastic -> GraphUtil.easeInElastic(currentTime.toDouble(), startValue, delta, duration.toDouble())
            AnimationType.Linear -> GraphUtil.linear(currentTime.toDouble(), startValue, delta, duration.toDouble())
        }
    }

    override fun onCompleted(f: Int) {
        currentValue = targetValue
    }
}