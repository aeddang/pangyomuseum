package com.enoughmedia.pangyomuseum.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.annotation.RawRes
import com.enoughmedia.pangyomuseum.MainActivity
import com.enoughmedia.pangyomuseum.PageID
import com.enoughmedia.pangyomuseum.PageParam
import com.enoughmedia.pangyomuseum.R
import com.enoughmedia.pangyomuseum.model.Antiquity
import com.enoughmedia.pangyomuseum.model.Mounds
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.animation.ModelAnimator
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.lib.model.Gesture
import com.lib.page.PagePresenter
import com.lib.util.Log
import com.skeleton.rx.RxFrameLayout
import kotlinx.android.synthetic.main.cp_scene_view_box.view.*
import java.util.function.Consumer


class SceneViewBox : RxFrameLayout , Gesture.Delegate {
    enum class ViewType{
        World,
        Node
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context,attrs)
    override fun getLayoutResId(): Int { return R.layout.cp_scene_view_box }
    private val appTag = javaClass.simpleName

    private var trigger:Boolean = true
    private lateinit var gesture: Gesture
    var viewType:ViewType = ViewType.Node

    override fun onCreatedView() {

    }


    fun addRenderModel(@RawRes id:Int){
        renderModel(id)
    }

    private var worldVec3:Vector3? = null
    private var antiquities:ArrayList<Antiquity>? = null
    fun addMounds(mounds: Mounds){
        antiquities = mounds.antiquitise
        worldVec3 = mounds.worldVec3
        renderModel(mounds.modelResource, mounds.id)
        antiquities?.forEach {ant->
            renderModel(ant.modelResource, ant.id, true, ant)
        }
    }

    override fun onDestroyedView() {
        gesture.onDestroy()
        antiquities = null
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        gesture = Gesture(this, isVertical = true, isHorizontal = true)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        gesture.onDestroy()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        trigger =  gesture.adjustEvent(ev)
        return !trigger
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        trigger =  gesture.adjustEvent(ev)
        return true
    }

    override fun stateChange(g: Gesture, e: Gesture.Type) {
        when (e) {
            Gesture.Type.MOVE_H, Gesture.Type.MOVE_V -> touchMove(g.deltaX, g.deltaY)
            Gesture.Type.END -> touchReset()
            else -> { }
        }
    }

    override fun gestureComplete(g: Gesture, e: Gesture.Type) {
        when (e) {
            Gesture.Type.TOUCH ->{
                if(viewType != ViewType.World) return
                val ray = sceneView.scene.camera.screenPointToRay(g.movePosA[0].x.toFloat(), g.movePosA[0].y.toFloat())
                val finds = sceneView.scene.hitTestAll(ray)
                Log.i(appTag, "finds ${finds.size} ")
                val child = finds.find { if( it.node == null ) false else node?.name != it.node?.name }?.node
                child ?: return
                val anti = antiquities?.find { it.id ==  child.name }
                anti?.let {findAntiquity(it)}
            }
            else -> { }
        }

    }


    val scaleSensitivity = 25.0f
    val roteSensitivity = 05.0f
    var finalScale  = 1.0f
    var deltaScale = finalScale
    var finalRotation  = PointF(0.0f,0.0f)
    var deltaRotation  = PointF(0.0f,0.0f)

    private fun touchReset() {
        finalRotation.x = deltaRotation.x
        finalRotation.y = deltaRotation.y
        finalScale = deltaScale
    }

    private fun setCameraStart(){
        deltaScale = 2f
        deltaRotation  = PointF(180.0f,-20f)
        touchReset()
        sceneView.scene.camera.localPosition = Vector3(0f,0f, - (finalScale - 1.0f) )
        val h = Quaternion.axisAngle(Vector3.up(), finalRotation .x)
        val v = Quaternion.axisAngle(Vector3.right(), finalRotation .y)
        val m = Quaternion.multiply(h,v)
        sceneView.scene.camera.localRotation = m
    }

    override fun pinchChange(g: Gesture, dist: Float) {
        super.pinchChange(g, dist)
        val d = if(viewType == ViewType.World) -dist else dist
        deltaScale = finalScale + (d/scaleSensitivity)
        Log.i(appTag, "deltaScale $deltaScale ")
        val m = Vector3(deltaScale, deltaScale, deltaScale)
        when(viewType){
            ViewType.Node -> node?.localScale = m
            ViewType.World -> sceneView.scene.camera.localPosition = Vector3(0f,0f, - (deltaScale - 1.0f) )
        }
    }

    private fun touchMove(deltaX:Int, deltaY:Int) {
        val mx = finalRotation.x + (deltaX.toFloat()/roteSensitivity)
        val my = finalRotation.y + (deltaY.toFloat()/roteSensitivity)
        Log.i(appTag, "finalRotation ${finalRotation.x}  ${finalRotation.y} ")
        Log.i(appTag, "deltaX ${deltaX}  ${deltaY} ")
        val h = Quaternion.axisAngle(Vector3.up(), mx)
        val v = Quaternion.axisAngle(Vector3.right(), my)
        val m = Quaternion.multiply(h,v)
        when(viewType){
            ViewType.Node -> node?.localRotation = m
            ViewType.World -> sceneView.scene.camera.localRotation = m
        }
        deltaRotation.x = mx
        deltaRotation.y = my
    }



    fun onResume(){
        sceneView.resume()
        animator?.resume()
    }

    fun onPause(){
        sceneView.pause()
        animator?.pause()
    }



    private fun renderModel(@RawRes res:Int, id:String? = "", isChild:Boolean = false, anti:Antiquity?=null) {
        ModelRenderable.builder()
            .setSource(context, res)
            .build()
            .thenAccept(
                Consumer {
                    if(isChild) addChildNode(it, id, anti) else addNode(it, id)
                })
            .exceptionally {
                Log.e(appTag, "${it.message}")
                return@exceptionally null
            }
    }

    private var animator:ModelAnimator? = null
    private var node:AnchorNode? = null
    private fun addNode(model: ModelRenderable?, id:String? = "") {
        model?.let { rm->
            val x = when(viewType){
                ViewType.Node ->  0.0f
                ViewType.World ->  worldVec3?.x ?: 0.0f
            }
            val z = when(viewType){
                ViewType.Node ->  -0.1f
                ViewType.World ->  worldVec3?.z ?: 0.0f
            }
            val y = when(viewType){
                ViewType.Node ->  -0.03f
                ViewType.World -> worldVec3?.y ?: -0.5f
            }

            node = AnchorNode().apply {
                setParent(sceneView.scene)
                localPosition = Vector3(x, y, z)
                localScale = Vector3(finalScale, finalScale, finalScale)
                if(viewType == ViewType.World) setCameraStart()
                name = id
                renderable = rm
                isSmoothed = true

            }
            sceneView.scene.addChild(node)

            val aniData = rm.getAnimationData(0)
            animator = ModelAnimator(aniData, rm)
            animator?.start()
        }
    }

    private fun addChildNode(parent: ModelRenderable?, id:String? = "", anti:Antiquity?=null) {
        parent?.let { rm->
            val child = AnchorNode().apply {
                setParent(sceneView.scene)
                val pos = anti?.posVec3 ?: Vector3()
                localPosition = pos
                localScale = Vector3(finalScale, finalScale, finalScale)
                name = id
                renderable = rm
                isSmoothed = true

            }

            sceneView.scene.addChild(child)
        }
    }


    private fun findAntiquity(anti:Antiquity){
        openAntiquity(anti)
        if(!anti.isFind){
            anti.isFind = true
            val main = PagePresenter.getInstance<PageID>().activity as?  MainActivity?
            main?.viewMessage(R.string.page_mounds_find_info, InfoMessage.Type.Book, 2000L)
        }


    }

    private fun openAntiquity(anti:Antiquity){
        val param = HashMap<String, Any>()
        param[PageParam.ANTIQUITY] = anti
        PagePresenter.getInstance<PageID>().openPopup(PageID.POPUP_AR, param)
    }
}