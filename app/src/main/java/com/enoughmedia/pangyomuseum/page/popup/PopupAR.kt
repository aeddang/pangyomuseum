package com.enoughmedia.pangyomuseum.page.popup

import android.os.Bundle
import androidx.annotation.RawRes
import com.enoughmedia.pangyomuseum.R
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.lib.util.Log
import com.skeleton.module.ImageFactory
import com.skeleton.rx.RxPageFragment
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.page_main.*
import java.util.function.Consumer
import javax.inject.Inject

class PopupAR  : RxPageFragment() {

    private val appTag = javaClass.simpleName

    @Inject
    lateinit var imageFactory: ImageFactory
    override fun getLayoutResId() = R.layout.popup_ar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)

    }

    override fun onCreatedView() {
        super.onCreatedView()
        renderModel(R.raw.andy)
        moveAngle()
    }

    override fun onResume() {
        super.onResume()
        sceneView.resume()
    }

    override fun onPause() {
        super.onPause()
        sceneView.pause()
    }

    private fun moveAngle() {
        val camera = sceneView.scene.camera
        camera.localRotation = Quaternion.axisAngle(Vector3.right(), -30.0f)
    }

    private fun renderModel(@RawRes res:Int) {
        ModelRenderable.builder()
            .setSource(context, res)
            .build()
            .thenAccept(Consumer { addNode(it) })
            .exceptionally {
                Log.e(appTag, "${it.message}")
                return@exceptionally null
            }
    }

    private fun addNode(model: ModelRenderable?, id:String? = "") {
        model?.let {
            val node = Node().apply {
                setParent(sceneView.scene)
                localPosition = Vector3(0f, 0f, -1f)
                localScale = Vector3(3f, 3f, 3f)
                name = id
                renderable = it
            }

            sceneView.scene.addChild(node)
        }
    }

}