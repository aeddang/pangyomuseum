package com.skeleton.view.graph

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Shader
import android.util.Size
import android.view.ViewGroup
import androidx.annotation.ColorRes

class GraphBuilder {

    private val appTag = "GraphBuilder"

    private lateinit var graph:Graph
    private var type:Graph.Type

    constructor(type:Graph.Type) {
        this.type = type
    }
    constructor(parent:ViewGroup, params:ViewGroup.LayoutParams, type:Graph.Type) {
        this.type = type
        graph = makeGraph(parent.context)
        parent.addView(graph, params)
    }
    constructor(parent:ViewGroup,  type:Graph.Type) {
        this.type = type
        graph = makeGraph(parent.context)
        parent.addView(graph, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    constructor(_graph: Graph) {
        graph = _graph
        this.type = graph.type
    }

    private fun makeGraph(context:Context):Graph {
        graph = when(type){
            Graph.Type.HolizentalBar -> GraphBar(context)
            Graph.Type.VerticalBar -> GraphBar(context)
            Graph.Type.Circle -> GraphCircle(context)
            Graph.Type.HalfCircle -> GraphHalfCircle(context)
            Graph.Type.Ring -> GraphRing(context)
            Graph.Type.Polygon -> GraphPolygon(context)
        }
        return graph
    }

    fun setAnimationType(type:Graph.AnimationType):GraphBuilder {
        graph.aniType = type
        return this
    }

    fun setRange(endValue:Double):GraphBuilder {
        graph.setRange(endValue)
        return this
    }

    fun setColor(@ColorRes colors:Array<Int>):GraphBuilder {
        val newColors = colors.mapNotNull { graph.context.getColor(it) }
        graph.setColor(newColors.toTypedArray())
        return this
    }

    fun setColor(colors:Array<String>):GraphBuilder {
        val newColors = colors.mapNotNull { Color.parseColor(it) }
        graph.setColor(newColors.toTypedArray())
        return this
    }

    fun setColor(@ColorRes color:Int):GraphBuilder {
        graph.setColor(arrayOf(graph.context.getColor(color)))
        return this
    }

    fun setColor(color:String):GraphBuilder {
        graph.setColor(arrayOf(Color.parseColor(color)))
        return this
    }

    fun setPaint(paints:ArrayList<Paint>):GraphBuilder {
        graph.paints = paints
        return this
    }

    fun setStroke(stroke:Float, style:Paint.Style = Paint.Style.STROKE, cap:Paint.Cap = Paint.Cap.ROUND):GraphBuilder {
        graph.setStroke(stroke, style, cap)
        return this
    }

    fun setFill(shader: Shader? = null):GraphBuilder {
        graph.setFill(shader)
        return this
    }

    fun setSize(size:Size):GraphBuilder {
        graph.size = size
        return this
    }

    fun setDuration(duration:Long):GraphBuilder {
        graph.setAnimationDuratiuon(duration)
        return this
    }

    fun show(value:Double, delay:Long = 0):Graph {
        graph.delay = delay
        graph.values = arrayListOf(value)
        return graph
    }

    fun show(values:List<Double>, delay:Long = 0):Graph {
        graph.delay = delay
        graph.values = values
        return graph
    }



}