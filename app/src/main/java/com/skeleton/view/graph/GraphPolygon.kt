package com.skeleton.view.graph

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet


class GraphPolygon@kotlin.jvm.JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    :  Graph(context, attrs, defStyleAttr) {

    private val appTag = "GraphPolygon"

    var startDegree:Float = -90.0f
    private var changeAngle:Float = -0.0f
    private var centerX = 0.0f
    private var centerY = 0.0f
    private var max = 0.0f
    private var isFill = false
    init {
        this.duration = GraphUtil.ANIMATION_DURATION
        this.type = Type.Polygon
    }
    override fun getModifyValues(value: List<Double>): List<Double> {
        return value
    }

    override fun setRange(endValue: Double) {
        this.endValue = endValue
        camera = Camera()
        //camera.rotateY(-40.0f)
        //camera.rotateX(20.0f)
    }

    override fun setColor(colors: Array<Int>) {
        paints = ArrayList()
        colors.forEach {
            val paint = Paint()
            val dpi = context.resources.displayMetrics.density
            paint.style = Paint.Style.STROKE
            paint.strokeCap = Paint.Cap.SQUARE
            paint.strokeWidth =  dpi * 2.0f
            paint.color = it
            paints.add( paint )
        }
    }


    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if(kind == 0) return
        var rotate = 0.0f
        camera.save()
        canvas?.save()
        canvas?.translate(centerX, centerY)
        camera.applyToCanvas(canvas)
        canvas?.translate(-centerX, -centerY)
        canvas?.rotate(startDegree , centerX, centerY)
        val positions = ArrayList<PointF>()
        values.forEachIndexed { idx , value ->
            val paint = paints[ idx % kind ]
            val v =  value / endValue * currentValue * max
            val r = rotate * Math.PI/180
            val tx = centerX + (Math.cos(r) *v)
            val ty = centerY + (Math.sin(r) *v)
            rotate += changeAngle
            val position = PointF(tx.toFloat(), ty.toFloat() )
            if(!isFill) canvas?.drawLine(centerX, centerY, position.x, position.y, paint)
            positions.add(position)
        }
        val paintOut = paints[ values.size % kind ]
        val path = Path()
        var initX = 0.0f
        var initY = 0.0f
        positions.forEachIndexed { idx, value ->
            if(idx  == 0) {
                path.moveTo(value.x, value.y)
                initX = value.x
                initY = value.y
            }
            else path.lineTo(value.x, value.y)
        }
        path.lineTo(initX, initY)
        canvas?.drawPath(path, paintOut)
        canvas?.restore()
        camera.restore()
        if( currentValue != 1.0 ) return
        delegate?.let {
            val datas = ArrayList<Pair<Double, Point>>()
            values.forEachIndexed { idx , value ->
                val p = positions[idx]
                val point = Point( Math.round(p.x) , Math.round(p.y) )
                datas.add(Pair(value, point))
            }
            it.drawGraph(this, datas)
        }
    }

    override fun onStart() {
        super.onStart()
        if(values.isEmpty()) return
        max = Math.min(size.width.toFloat(), size.height.toFloat())
        max *= 0.5f
        centerX = width.toFloat()/2.0f
        centerY = height.toFloat()/2.0f
        currentValue = 0.0
        changeAngle = 360.0f / values.size
        targetValue = 1.0
        startValue = 0.0
    }

    override fun setFill(shader:Shader?) {
        isFill = true
        super.setFill(shader)
    }

    override fun setStroke(stroke: Float, style: Paint.Style, cap: Paint.Cap) {
        super.setStroke(stroke, style, cap)
        isFill = false
    }


}