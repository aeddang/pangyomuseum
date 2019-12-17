package com.enoughmedia.pangyomuseum.page.popup

import android.content.res.AssetManager
import android.graphics.BitmapFactory
import android.os.Bundle
import com.enoughmedia.pangyomuseum.PageID
import com.enoughmedia.pangyomuseum.PageParam
import com.enoughmedia.pangyomuseum.R
import com.enoughmedia.pangyomuseum.model.Mounds

import com.google.vr.sdk.widgets.common.VrWidgetView
import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener
import com.google.vr.sdk.widgets.pano.VrPanoramaView
import com.jakewharton.rxbinding3.view.clicks
import com.lib.page.PageFragment
import com.lib.page.PagePresenter
import com.skeleton.module.ImageFactory
import com.skeleton.rx.RxPageFragment
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.popup_vr.*
import java.io.InputStream
import javax.inject.Inject


class PopupVR  : RxPageFragment() {
    enum class ViewType{
        Mono,
        Stereo
    }
    private val appTag = javaClass.simpleName

    @Inject
    lateinit var imageFactory: ImageFactory
    override fun getLayoutResId() = R.layout.popup_vr

    private var mounds:Mounds? = null
    private var viewType:ViewType = ViewType.Mono
    override fun setParam(param: Map<String, Any?>): PageFragment {
        mounds = param[PageParam.MOUNDS] as? Mounds
        viewType = param[PageParam.VIEW_TYPE] as? ViewType ?: viewType
        return super.setParam(param)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)

    }

    private fun loadPhotoSphere() {

        mounds?.let {
            val path = when(viewType){
                ViewType.Mono -> it.panoViewPath
                ViewType.Stereo -> it.cardViewPath
            }
            val options = VrPanoramaView.Options()
            var inputStream: InputStream? = null
            val assetManager: AssetManager = context!!.assets
            try {
                inputStream = assetManager.open(path)
                options.inputType = VrPanoramaView.Options.TYPE_STEREO_OVER_UNDER
                vrPanoramaView.loadImageFromBitmap(BitmapFactory.decodeStream(inputStream), options)
                inputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    override fun onCreatedView() {
        super.onCreatedView()

        vrPanoramaView.setFullscreenButtonEnabled(false)
        vrPanoramaView.setStereoModeButtonEnabled(false)
        vrPanoramaView.setInfoButtonEnabled(false)
        if(viewType == ViewType.Mono){
            vrPanoramaView.displayMode = VrWidgetView.DisplayMode.EMBEDDED
            loadPhotoSphere()
        }
    }

    override fun onTransactionCompleted() {
        super.onTransactionCompleted()
        if(viewType == ViewType.Stereo) {
            vrPanoramaView.displayMode = VrWidgetView.DisplayMode.FULLSCREEN_STEREO
            loadPhotoSphere()
            vrPanoramaView.setEventListener(object : VrPanoramaEventListener() {
                override fun onDisplayModeChanged(newDisplayMode: Int) {
                    super.onDisplayModeChanged(newDisplayMode)
                    if(newDisplayMode != VrWidgetView.DisplayMode.FULLSCREEN_STEREO) PagePresenter.getInstance<PageID>().closePopup(PageID.POPUP_VR)
                }
            })
        }
    }


    override fun onDestroyedView() {
        super.onDestroyedView()
    }

    override fun onSubscribe() {
        super.onSubscribe()
        btnClose.clicks().subscribe {
            PagePresenter.getInstance<PageID>().goBack()
        }.apply { disposables.add(this) }
    }

}