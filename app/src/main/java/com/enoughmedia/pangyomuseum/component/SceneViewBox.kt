package com.enoughmedia.pangyomuseum.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.view.*
import androidx.annotation.RawRes
import com.enoughmedia.pangyomuseum.PageID
import com.enoughmedia.pangyomuseum.PageParam
import com.enoughmedia.pangyomuseum.R
import com.enoughmedia.pangyomuseum.model.Antiquity
import com.enoughmedia.pangyomuseum.model.Mounds
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ExternalTexture
import com.google.ar.sceneform.rendering.ModelRenderable
import com.lib.model.Gesture
import com.lib.page.PagePresenter
import com.lib.util.AnimationDuration
import com.lib.util.Log
import com.skeleton.rx.RxFrameLayout
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.cp_scene_view_box.view.*
import java.util.concurrent.TimeUnit
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
    private lateinit var texture:ExternalTexture
    var viewType:ViewType = ViewType.Node

    override fun onCreatedView() {

        texture = ExternalTexture()

    }


    fun addRenderModel(@RawRes id:Int){
        renderModel(id)
    }


    private var antiquities:ArrayList<Antiquity>? = null
    fun addMounds(mounds: Mounds){
        antiquities = mounds.antiquitise
        renderModel(mounds.modelResource, mounds.id)
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


    val scaleSensitivity = 25.0f
    val roteSensitivity = 05.0f
    var finalScale  = 1.0f
    var deltaScale   = 1.0f
    var finalRotation  = PointF(0.0f,0.0f)
    var deltaRotation  = PointF(0.0f,0.0f)

    private fun touchReset() {
        finalRotation.x = deltaRotation.x
        finalRotation.y = deltaRotation.y
        finalScale = deltaScale
    }

    override fun pinchChange(g: Gesture, dist: Float) {
        super.pinchChange(g, dist)
        deltaScale = finalScale + (dist/scaleSensitivity)
        val m = Vector3(deltaScale, deltaScale, deltaScale)
        when(viewType){
            ViewType.Node -> node?.localScale = m
            ViewType.World -> sceneView.scene.camera.localPosition = Vector3(0f,0f, - (deltaScale - 1.0f) )
        }
    }

    private fun touchMove(deltaX:Int, deltaY:Int) {
       //Log.i(appTag, "delta X ${deltaX} Y ${deltaY}")
        val mx = finalRotation.x + (deltaX.toFloat()/roteSensitivity)
        val my = finalRotation.y + (deltaY.toFloat()/roteSensitivity)
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
    }

    fun onPause(){
        sceneView.resume()
    }



    private fun renderModel(@RawRes res:Int, id:String? = "", isChild:Boolean = false) {
        ModelRenderable.builder()
            .setSource(context, res)
            .build()
            .thenAccept(
                Consumer {
                    if(isChild) addChildNode(it, id) else addNode(it, id)
                })
            .exceptionally {
                Log.e(appTag, "${it.message}")
                return@exceptionally null
            }
    }

    private var node:AnchorNode? = null
    private fun addNode(model: ModelRenderable?, id:String? = "") {
        model?.let {

            val z = when(viewType){
                ViewType.Node ->  -0.1f
                ViewType.World ->  -2.5f
            }
            node = AnchorNode().apply {
                setParent(sceneView.scene)
                localPosition = Vector3(0f, 0f, z)
                localScale = Vector3(finalScale, finalScale, finalScale)
                name = id
                renderable = it
                isSmoothed = true
                //anchor = Anchor(0.5,0.5)
                antiquities?.forEach {
                    renderModel(it.modelResource, it.id, true)
                }
            }
            sceneView.scene.addChild(node)

            sceneView.scene.addOnPeekTouchListener(
                Scene.OnPeekTouchListener { result, event ->
                    val node = result.node
                    node ?: return@OnPeekTouchListener
                    val anti = antiquities?.find { it.id == node.name }
                    anti?.let {findAntiquity(it)}
                }
            )
        }
    }

    private fun addChildNode(parent: ModelRenderable?, id:String? = "") {
        parent?.let {
            val child = AnchorNode().apply {
                setParent(sceneView.scene)
                val tx = Math.random().toFloat() * 3.0f - 1.5f
                val ty = Math.random().toFloat() * 3.0f - 1.5f
                val tz = - Math.random().toFloat()
                localPosition = Vector3(tx, ty, tz)
                localScale = Vector3(finalScale, finalScale, finalScale)
                name = id
                renderable = it
                isSmoothed = true
                //anchor = Anchor(0.5,0.5)

            }
            sceneView.scene.addChild(child)
        }
    }


    private fun findAntiquity(anti:Antiquity){

        if(anti.isFind){
            openAntiquity(anti)
        }else{
            anti.isFind = true
            val duration = 3000L
            infoMessage.viewMessage(R.string.page_mounds_find_info, InfoMessage.Type.Book, duration)
            Observable.interval(duration, TimeUnit.MILLISECONDS)
                .take(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe {
                    openAntiquity(anti)
                }.apply { disposables?.add(this) }
        }

    }

    private fun openAntiquity(anti:Antiquity){
        val param = HashMap<String, Any>()
        param[PageParam.ANTIQUITY] = anti
        PagePresenter.getInstance<PageID>().openPopup(PageID.POPUP_AR, param)
    }
}