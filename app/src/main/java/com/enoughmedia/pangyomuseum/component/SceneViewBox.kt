package com.enoughmedia.pangyomuseum.component

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PointF
import android.media.AudioAttributes
import android.media.SoundPool
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
import kotlin.math.*


class SceneViewBox : RxFrameLayout , Gesture.Delegate {
    enum class ViewType{
        World,
        Node
    }

    enum class NodeType{
        Child,
        Background,
        Parent,
        Effect
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context,attrs)
    override fun getLayoutResId(): Int { return R.layout.cp_scene_view_box }
    private val appTag = javaClass.simpleName

    private var trigger:Boolean = true
    private lateinit var gesture: Gesture
    var viewType:ViewType = ViewType.Node




    private val audioAttributes = AudioAttributes.Builder()
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .setUsage(AudioAttributes.USAGE_GAME)
        .build()

    private var soundId = 0
    private val staticSoundPool: SoundPool = SoundPool.Builder().setAudioAttributes(audioAttributes).setMaxStreams(20).build()
    override fun onCreatedView() {
        soundId = staticSoundPool.load(context, R.raw.effect_sound, 1)
    }


    fun addRenderModel(@RawRes id:Int, scale:Float, rotate:Float){
        finalScale = 1.5f * scale
        localRotate = rotate
        renderModel(id)
    }

    private var worldVec3:Vector3? = null
    private var antiquities:ArrayList<Antiquity>? = null
    fun addMounds(mounds: Mounds){
        finalScale = 1.0f
        antiquities = mounds.antiquitise
        worldVec3 = mounds.worldVec3
        renderModel(mounds.modelResource, mounds.id)
        renderModel(R.raw.skyandground, "sky", NodeType.Background)
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
            Gesture.Type.PINCH_MOVE -> pinchMove(g.deltaPinchX, g.deltaPinchY, g.deltaDist)
            Gesture.Type.MOVE_H, Gesture.Type.MOVE_V -> touchMove(g.deltaX, g.deltaY)
            Gesture.Type.END -> touchReset()
            else -> { }
        }
    }

    override fun gestureComplete(g: Gesture, e: Gesture.Type) {
        when (e) {
            Gesture.Type.TOUCH ->{
                if(viewType != ViewType.World) return
                if(g.movePosA.size < 1) return
                val ray = sceneView.scene.camera.screenPointToRay(g.movePosA[0].x.toFloat(), g.movePosA[0].y.toFloat())
                val finds = sceneView.scene.hitTestAll(ray)
                Log.i(appTag, "finds ${finds.size} ")
                val child = finds.find { if( it.node == null || it.node?.name == "sky") false else node?.name != it.node?.name }?.node
                child ?: return
                Log.i(appTag, "child ${child.name} ")
                val anti = antiquities?.find { it.id ==  child.name }

                Log.i(appTag, "anti ${anti?.id} ")
                anti?.let {
                    renderModel(it.effectResource, it.id, NodeType.Effect, it)
                }
            }
            else -> { }
        }

    }


    val scaleSensitivity = 25.0f
    val moveSensitivity = 600.0f
    val roteSensitivity = 5.0f
    var cameraZ  = 0.1f
    var limitedY  = -0.3f
    var finalScale  = 1.5f
    var localRotate  = 0.0f
    var deltaScale = finalScale
    var finalRotation  = PointF(0.0f,0.0f)
    var deltaRotation  = PointF(0.0f,0.0f)
    var modifyRotation  = PointF(0.0f,0.0f)
    var finalCameraPosition  = Vector3(0.0f,0.0f,0.0f)
    var finalNodeRotation  = Quaternion(0.0f,0.0f,0.0f,0.0f)
    private fun touchReset() {
        finalRotation.x = deltaRotation.x
        finalRotation.y = deltaRotation.y
        finalScale = deltaScale
        node?.let{
            finalNodeRotation = it.localRotation
        }

        cameraMoveReset()
    }
    private fun cameraMoveReset(){
        finalCameraPosition.x = sceneView.scene.camera.localPosition.x
        finalCameraPosition.y = sceneView.scene.camera.localPosition.y
        finalCameraPosition.z = sceneView.scene.camera.localPosition.z
    }

    private fun setCameraStart(){
        deltaScale = -1.5f
        deltaRotation  = PointF(180.0f,-40f)
        //sceneView.scene.camera.
        rotateCamera(deltaRotation.x, deltaRotation.y)
        zoomCamera(deltaScale)
        touchReset()

    }
    private fun zoomCamera(delta:Float){
        val forword:Vector3  = sceneView.scene.camera.forward
        val x = forword.x * delta + finalCameraPosition.x
        var y = forword.y * delta + finalCameraPosition.y
        val z = forword.z * delta + finalCameraPosition.z
        y = max(y, limitedY)
        sceneView.scene.camera.localPosition = Vector3(x,y,z)

    }

    private fun moveCamera(deltaX:Float, deltaY:Float, deltaZ:Float){
        val left:Vector3  = sceneView.scene.camera.left
        val up:Vector3  = sceneView.scene.camera.up
        val forword:Vector3  = sceneView.scene.camera.forward
        val x = (left.x * deltaX) + (up.x * deltaY) + (forword.x * deltaZ) +finalCameraPosition.x
        var y = (left.y * deltaX) + (up.y * deltaY) + (forword.y * deltaZ) +finalCameraPosition.y
        val z = (left.z * deltaX) + (up.z * deltaY) + (forword.z * deltaZ) +finalCameraPosition.z
        y = max(y, limitedY)
        sceneView.scene.camera.localPosition = Vector3(x,y,z)
    }
    private fun rotateNode(deltaX:Float, deltaY:Float){
        node ?: return
        val center = Vector3(0.0f, 0.0f, 0.0f)
        val dy = -deltaY - modifyRotation.y
        val rX = (dy/1000.0) * Math.PI * 2.0
        val rY= Math.PI
        val div = Math.PI/2.0
        val idx = (floor(rX/div)%6)
        val dr = when(idx){
            -4.0 -> Vector3.up()
            -3.0 -> Vector3.down()
            -1.0, -2.0 -> Vector3.right()
            2.0, 3.0 -> Vector3.right()
            4.0, 5.0 -> Vector3.up()
            else -> Vector3.left()
        }
        if(abs(rX) >= Math.PI * 2.0){
            modifyRotation.y = deltaRotation.y
        }
        val x = center.x + (cameraZ * cos(rX) * cos(rY))
        val y = center.y + (cameraZ * sin(rX) * cos(rY))
        val z = center.z + (cameraZ * sin(rY))
        val cameraPos = Vector3(x.toFloat(), y.toFloat(), z.toFloat())
        val direction = Vector3.subtract(center, cameraPos)
        val rt = Quaternion.lookRotation( direction , dr)

        sceneView.scene.camera.localRotation = rt
        sceneView.scene.camera.localPosition = cameraPos

        val h = Quaternion.axisAngle(Vector3.up(),deltaX)
        val v = Quaternion.axisAngle(Vector3.right(),0.0f)
        val m = Quaternion.multiply(h,v)
        node?.let { it.worldRotation = m }


    }
    private fun rotateCamera(deltaX:Float, deltaY:Float){
        val h = Quaternion.axisAngle(Vector3.up(),deltaX)
        val v = Quaternion.axisAngle(Vector3.right(),deltaY)
        val m = Quaternion.multiply(h,v)
        sceneView.scene.camera.localRotation = m

    }

    override fun pinchChange(g: Gesture, dist: Float) {
        super.pinchChange(g, dist)
        deltaScale = (dist/scaleSensitivity)
        when(viewType){
            ViewType.Node -> {
                deltaScale += finalScale
                val m = Vector3(deltaScale, deltaScale, deltaScale)
                node?.localScale = m
            }
            ViewType.World -> {}// zoomCamera(deltaScale)
        }
    }
    private fun pinchMove(deltaX:Int, deltaY:Int, deltaZ:Float) {
        if(viewType == ViewType.Node) return
        val dx = deltaX.toFloat()/moveSensitivity
        val dy = deltaY.toFloat()/moveSensitivity
        moveCamera(dx,dy, deltaZ/scaleSensitivity)
    }

    private fun touchMove(deltaX:Int, deltaY:Int) {
        var mx = finalRotation.x
        var my = finalRotation.y
        when(viewType){
            ViewType.Node -> {
                mx += deltaX.toFloat()
                my += deltaY.toFloat()
                rotateNode(mx,my)
            }
            ViewType.World -> {
                mx += (deltaX.toFloat()/roteSensitivity)
                my += (deltaY.toFloat()/roteSensitivity)
                rotateCamera(mx, my)
            }
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



    private fun renderModel(@RawRes res:Int, id:String? = "", type:NodeType = NodeType.Parent, anti:Antiquity?=null) {
        ModelRenderable.builder()
            .setSource(context, res)
            .build()
            .thenAccept {
                when(type){
                    NodeType.Parent -> addNode(it, id)
                    NodeType.Child -> addChildNode(it, id, anti)
                    NodeType.Background -> addBackgroundNode(it, id)
                    NodeType.Effect -> addEffectNode(it, id, anti)
                }

            }
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
                ViewType.Node ->  0.00f
                ViewType.World ->  worldVec3?.x ?: 0.0f
            }
            val z = when(viewType){
                ViewType.Node ->  0.00f
                ViewType.World ->  worldVec3?.z ?: 0.0f
            }
            val y = when(viewType){
                ViewType.Node ->  -0.02f
                ViewType.World -> worldVec3?.y ?: -0.5f
            }
            /*
            val pos = floatArrayOf(0.0f,0.0f,0.0f)
            val rotation = floatArrayOf(0.0f,0.0f,0.0f,1.0f)
            val anchor = Session(context).createAnchor(Pose(pos,rotation))
            */
            node = AnchorNode().apply {

                setParent(sceneView.scene)
                localPosition = Vector3(x, y, z)
                when(viewType) {
                    ViewType.World -> {

                        localScale = Vector3(finalScale, finalScale, finalScale)
                        setCameraStart()
                    }
                    ViewType.Node -> {

                        localScale = Vector3(finalScale, finalScale, finalScale)
                        finalNodeRotation = localRotation

                    }
                }
                name = id
                renderable = rm
                isSmoothed = true

            }

            if(viewType == ViewType.Node) rotateNode(0.0f, 0.0f)
            val aniData = rm.getAnimationData(0)
            animator = ModelAnimator(aniData, rm)
            animator?.start()
            animator?.addListener(object :Animator.AnimatorListener{
                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {
                    sceneView.scene.addChild(node)
                }
                override fun onAnimationEnd(animation: Animator?) {
                    antiquities?.forEach {ant->
                        renderModel(ant.modelResource, ant.id, NodeType.Child, ant)

                    }
                }

            })
        }
    }

    private fun addBackgroundNode(model: ModelRenderable?, id:String? = "") {
        model?.let { rm->
            node = AnchorNode().apply {
                setParent(sceneView.scene)
                localScale = Vector3(25.0f, 25.0f, 25.0f)
                localPosition = Vector3(0.0f, -0.48f, 0.0f)
                name = id
                renderable = rm
                isSmoothed = true
            }

        }
    }

    private fun addChildNode(parent: ModelRenderable?, id:String? = "", anti:Antiquity?=null) {
        parent?.let { rm->
            val child = AnchorNode().apply {
                setParent(sceneView.scene)
                val pos = anti?.posVec3 ?: Vector3()
                localPosition = pos
                val sc = 3.0f * (anti?.scale ?: 1.0f)
                localRotation = Quaternion.axisAngle(Vector3.up(),anti?.rotate ?: 0.0f)
                localScale = Vector3(sc,sc,sc)
                name = id
                renderable = rm
                isSmoothed = true

            }

            sceneView.scene.addChild(child)
        }
    }

    private fun addEffectNode(parent: ModelRenderable?, id:String? = "", anti:Antiquity?=null) {
        parent?.let { rm->
            val ani = ValueAnimator.ofFloat(0f, 1f)
            val child = AnchorNode().apply {
                setParent(sceneView.scene)
                val pos = anti?.posVec3 ?: Vector3()
                localPosition = Vector3(pos.x, pos.y + 0.02f, pos.z)
                //name = id
                renderable = rm
                isSmoothed = true
                val targetSc = 6.0f  * (anti?.scale ?: 1.0f)
                ani.addUpdateListener {
                    val ratio = it.animatedValue as Float
                    val sc = targetSc * ratio
                    localScale = Vector3(sc,sc,sc)
                }
            }
            sceneView.scene.addChild(child)
            staticSoundPool.play( soundId, 1.0f,1.0f,1,0,1.0f)

            ani.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) { // done
                    anti?.let { findAntiquity(it) }
                    sceneView.scene.removeChild(child)
                }
            })
            ani.duration = 300
            ani.repeatCount = 1
            ani.start()

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