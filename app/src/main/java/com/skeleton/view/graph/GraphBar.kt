package com.skeleton.view.graph

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet

class GraphBar@kotlin.jvm.JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    :  Graph(context, attrs, defStyleAttr) {
    private val TAG = javaClass.simpleName

    var isVertical:Boolean = true
    set(value) {
        type = if(isVertical) Type.VerticalBar else Type.HolizentalBar
        field = value
    }

    private var center = 0.0f
    private var max = 0.0f
    private var strokeMargin = 0.0f
    init {
        this.duration = GraphUtil.ANIMATION_DURATION
        zeroPaint = Paint()
        zeroPaint?.strokeCap = Paint.Cap.ROUND
        zeroPaint?.color = Color.LTGRAY
    }

    override fun getModifyValues(value: List<Double>): List<Double> {
        return value
    }

    override fun setRange(endValue: Double) {
        this.endValue = endValue
    }

    override fun setColor(colors: Array<Int>) {
        paints = ArrayList()
        colors.forEach {
            val paint = Paint()
            paint.color = it
            paints.add( paint )
        }
    }


    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if(kind == 0) return
        var sum = 0.0
        var start = 0.0
        values.forEachIndexed { idx , value ->
            var end = currentValue
            if(!isContinuous){
                val v = sum + value
                end = if ( v >  currentValue ) currentValue else v
            }
            val sPos = start.toFloat() / endValue.toFloat() * max
            var ePos = end.toFloat() / endValue.toFloat() * max

            if(zeroPaint != null && ePos == 0f) ePos = -( zeroPaint!!.strokeWidth )
            val paint = if(ePos > 0.0f) paints[ idx % kind] else if(zeroPaint != null) zeroPaint else null

            if(paint != null){
                if( isVertical ) canvas?.drawLine(center,max - sPos,center,max - ePos + strokeMargin,paint)
                else canvas?.drawLine(sPos,center,ePos,center,paint)
            }
            start = end
            sum += value
        }

        delegate?.let {
            val data = ArrayList<Pair<Double, Point>>()
            val pos = Math.round(currentValue/ endValue.toFloat() * max).toInt()
            val point =  if(isVertical) Point( 0 , pos) else Point( pos , 0 )
            data.add(Pair(currentValue, point))
            it.drawGraph(this, data)
        }
    }

    override fun onStart() {
        super.onStart()

        val strokeWidth = if(isVertical) size.width.toFloat() else size.height.toFloat()
        paints.forEach {
            it.strokeWidth = strokeWidth
        }
        zeroPaint?.strokeWidth = strokeWidth
        center = if( isVertical) size.width.toFloat()/2.0f else size.height.toFloat()/2.0f
        max = if( isVertical) size.height.toFloat() else size.width.toFloat()
        //max += strokeWidth/2f
        targetValue = values.sum()
        if(isContinuous){
            startValue = currentValue
        }else{
            currentValue = 0.0
            startValue = 0.0
        }
        strokeMargin = strokeWidth/4
    }


}