package com.enoughmedia.pangyomuseum.component

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RawRes
import com.enoughmedia.pangyomuseum.MainActivity
import com.enoughmedia.pangyomuseum.PageID
import com.enoughmedia.pangyomuseum.PageParam
import com.enoughmedia.pangyomuseum.R
import com.enoughmedia.pangyomuseum.model.Antiquity
import com.enoughmedia.pangyomuseum.model.Mounds
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.animation.ModelAnimator
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.lib.model.Gesture
import com.lib.page.PagePresenter
import com.lib.util.Log
import com.skeleton.rx.RxFrameLayout
import java.util.*
import java.util.function.Consumer
import kotlin.collections.HashMap


class SceneViewWorld : RxFrameLayout , Gesture.Delegate {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context,attrs)
    override fun getLayoutResId(): Int { return R.layout.cp_scene_view_world }
    private val appTag = javaClass.simpleName

    private var trigger:Boolean = true
    private lateinit var gesture: Gesture
    private lateinit var sceneFragment:ArFragment
    private lateinit var sceneView:ArSceneView
    override fun onCreatedView() {


    }

    private var isArInit = false
    private var currentMounds: Mounds? = null
    private var worldVec3:Vector3? = null
    private var antiquities:ArrayList<Antiquity>? = null
    fun addMounds(mounds: Mounds){
        val main = PagePresenter.getInstance<PageID>().activity as?  MainActivity?
        sceneFragment = main?.supportFragmentManager?.findFragmentById(R.id.sceneFragment) as? ArFragment? ?: ArFragment()
        sceneView = sceneFragment.arSceneView
        currentMounds = mounds
        antiquities = mounds.antiquitise
        worldVec3 = mounds.worldVec3
        arCheck()
    }
    private fun arCheck(){
        val avi = ArCoreApk.getInstance().checkAvailability(context)
        if(avi == ArCoreApk.Availability.SUPPORTED_INSTALLED) arInit()
        else {
            val ac = PagePresenter.getInstance<PageID>().activity
            ac ?: return
            val main = PagePresenter.getInstance<PageID>().activity as?  MainActivity?
            ac.getCurrentActivity().let{
                try {
                    ArCoreApk.getInstance().requestInstall(it, true)
                } catch (e: UnavailableDeviceNotCompatibleException){
                    main?.viewMessage(R.string.notice_disable_arcore, InfoMessage.Type.Default, 2000L)
                    main?.pagePresenter?.goBack()
                } catch (e:UnavailableUserDeclinedInstallationException){
                    main?.viewMessage(R.string.notice_need_arcore, InfoMessage.Type.Default, 2000L)
                    main?.pagePresenter?.goBack()
                }
            }
        }
    }



    private fun setupPlaneFinding() {
        val session = Session(context, EnumSet.of(Session.Feature.SHARED_CAMERA))
        val sharedCamera = session.sharedCamera
        val cameraId = session.cameraConfig.cameraId
        val arConfig = Config(session)
        try { sceneView.pause() } catch (e:Exception){}
        arConfig.planeFindingMode = Config.PlaneFindingMode.VERTICAL
        try { sceneView.resume() } catch (e:Exception){}
        arConfig.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
        session.configure(arConfig)
        sceneView.setupSession(session)
    }

    override fun onDestroyedView() {
        sceneFragment.view?.visibility = View.GONE
        gesture.onDestroy()
        antiquities = null
        val main = PagePresenter.getInstance<PageID>().activity as?  MainActivity?
        main?.supportFragmentManager?.beginTransaction()?.remove( sceneFragment )?.commit()
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
                val ray = sceneView.scene.camera.screenPointToRay(g.movePosA[0].x.toFloat(), g.movePosA[0].y.toFloat())
                val finds = sceneView.scene.hitTestAll(ray)
                Log.i(appTag, "finds ${finds?.size} ")
                val child = finds?.find { if( it.node == null ) false else transformableNode?.name != it.node?.name }?.node
                child ?: return
                val anti = antiquities?.find { it.id ==  child.name }
                anti?.let {findAntiquity(it)}
            }
            else -> { }
        }

    }


    val roteSensitivity = 05.0f
    var finalRotation  = PointF(0.0f,0.0f)
    var deltaRotation  = PointF(0.0f,0.0f)

    private fun touchReset() {
        finalRotation.x = deltaRotation.x
        finalRotation.y = deltaRotation.y
    }

    private fun rotateCamera(deltaX:Float, deltaY:Float){
        transformableNode ?: return
        Log.i(appTag, "deltaX $deltaX $deltaY")
        val h = Quaternion.axisAngle(Vector3.up(),deltaX)
        transformableNode?.localRotation = h
        deltaRotation.x = deltaX
        deltaRotation.y = deltaY
    }



    private fun touchMove(deltaX:Int, deltaY:Int) {
        val mx = finalRotation.x + (deltaX.toFloat()/roteSensitivity)
        val my = finalRotation.y + (deltaY.toFloat()/roteSensitivity)
        rotateCamera(mx,my)
    }


    fun onResume(){
        if(!isArInit){
            arCheck()
        }else{
            sceneFragment.onResume()
            animator?.resume()
        }

    }

    fun onPause(){
        sceneFragment.onPause()
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
    private var nodeRenderable:ModelRenderable? = null
    private var transformableNode:TransformableNode? = null

    private fun arInit(){
        currentMounds?.let {
            isArInit = true
            renderModel(it.modelResource, it.id)
            sceneFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
                nodeRenderable?.let {rm->
                    val anchor = hitResult.createAnchor()
                    val anchorNode =
                        AnchorNode(anchor)
                    anchorNode.setParent(sceneView.scene)
                    transformableNode = TransformableNode(sceneFragment.transformationSystem)
                    transformableNode?.setParent(anchorNode)
                    transformableNode?.renderable = rm
                    transformableNode?.select()
                    rotateCamera(167.0f, 0.0f)
                    touchReset()
                    val aniData = rm.getAnimationData(0)
                    animator = ModelAnimator(aniData, rm)
                    animator?.start()
                    animator?.addListener(object :Animator.AnimatorListener{
                        override fun onAnimationRepeat(animation: Animator?) {}
                        override fun onAnimationCancel(animation: Animator?) {}
                        override fun onAnimationStart(animation: Animator?) {}
                        override fun onAnimationEnd(animation: Animator?) {
                            antiquities?.forEach {ant->
                                renderModel(ant.modelResource, ant.id, true, ant)
                            }
                        }
                    })
                }
            }
        }
    }
    private fun addNode(model: ModelRenderable?, id:String? = "") {
        nodeRenderable = model
    }

    private fun addChildNode(parent: ModelRenderable?, id:String? = "", anti:Antiquity?=null) {
        parent?.let { rm->
            val child = AnchorNode().apply {
                setParent(transformableNode)
                val pos = anti?.posVec3 ?: Vector3()
                pos.y = pos.y - currentMounds!!.worldVec3.y
                localPosition = pos
                localScale = Vector3(3.0f,3.0f,3.0f)
                name = id
                renderable = rm
                isSmoothed = true

            }
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