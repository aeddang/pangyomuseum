package com.enoughmedia.pangyomuseum.page.popup

import android.os.Bundle
import android.transition.ChangeBounds

import com.enoughmedia.pangyomuseum.PageID
import com.enoughmedia.pangyomuseum.PageParam
import com.enoughmedia.pangyomuseum.R
import com.enoughmedia.pangyomuseum.model.Antiquity
import com.jakewharton.rxbinding3.view.clicks
import com.lib.page.PageFragment
import com.lib.page.PagePresenter
import com.skeleton.rx.RxPageFragment
import dagger.android.support.AndroidSupportInjection

import kotlinx.android.synthetic.main.popup_ar.*
import kotlinx.android.synthetic.main.popup_ar.btnClose
import kotlinx.android.synthetic.main.popup_ar.sceneViewBox


class PopupAR  : RxPageFragment() {

    private val appTag = javaClass.simpleName
    override fun getLayoutResId() = R.layout.popup_ar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)

    }


    private var antiquity:Antiquity? = null
    private var shareImageID:String? = null

    override fun setParam(param: Map<String, Any?>): PageFragment {
        antiquity = param[PageParam.ANTIQUITY] as? Antiquity?
        shareImageID = param[PageParam.SHARE_IMAGE_ID] as? String?
        return super.setParam(param)
    }
    override fun onCreatedView() {
        super.onCreatedView()

        antiquity?.let {
            title.text = it.title
            info.text = it.info
            desc.text = it.desc
            sceneViewBox.addRenderModel(it.modelResource)
        }
        shareImageID?.let {
            sharedElementEnterTransition = ChangeBounds()
            sceneViewBox.transitionName = it
        }


    }

    override fun onSubscribe() {
        super.onSubscribe()
        btnClose.clicks().subscribe {
            PagePresenter.getInstance<PageID>().goBack()
        }.apply { disposables.add(this) }

    }

    override fun onResume() {
        super.onResume()
        sceneViewBox.onResume()

    }

    override fun onPause() {
        super.onPause()
        sceneViewBox.onPause()
    }



}