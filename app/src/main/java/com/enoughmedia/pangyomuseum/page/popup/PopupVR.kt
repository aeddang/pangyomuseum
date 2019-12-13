package com.enoughmedia.pangyomuseum.page.popup

import android.content.res.AssetManager
import android.graphics.BitmapFactory
import android.os.Bundle
import com.enoughmedia.pangyomuseum.R
import com.google.vr.sdk.widgets.common.VrWidgetView
import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener
import com.google.vr.sdk.widgets.pano.VrPanoramaView
import com.skeleton.module.ImageFactory
import com.skeleton.rx.RxPageFragment
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.popup_vr.*
import java.io.InputStream
import javax.inject.Inject


class PopupVR  : RxPageFragment() {

    private val appTag = javaClass.simpleName

    @Inject
    lateinit var imageFactory: ImageFactory
    override fun getLayoutResId() = R.layout.popup_vr


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)

    }

    private fun loadPhotoSphere() {
        val options = VrPanoramaView.Options()
        var inputStream: InputStream? = null
        val assetManager: AssetManager = context!!.assets
        try {
            inputStream = assetManager.open("panoramas/testRoom1_2kStereo.jpg")
            options.inputType = VrPanoramaView.Options.TYPE_STEREO_OVER_UNDER
            vrPanoramaView.loadImageFromBitmap(BitmapFactory.decodeStream(inputStream), options)
            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreatedView() {
        super.onCreatedView()
        loadPhotoSphere()
        vrPanoramaView.displayMode = VrWidgetView.DisplayMode.FULLSCREEN_STEREO
        vrPanoramaView.setFullscreenButtonEnabled(false)
        vrPanoramaView.setStereoModeButtonEnabled(false)
        vrPanoramaView.setInfoButtonEnabled(false)
        vrPanoramaView.setEventListener(object : VrPanoramaEventListener(){
            override fun onDisplayModeChanged(newDisplayMode: Int) {
                super.onDisplayModeChanged(newDisplayMode)
            }

        })
    }

    override fun onDestroyedView() {
        super.onDestroyedView()
    }

    override fun onSubscribe() {
        super.onSubscribe()
    }

}