package com.enoughmedia.pangyomuseum.page

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.enoughmedia.pangyomuseum.AppConst
import com.enoughmedia.pangyomuseum.PageID
import com.enoughmedia.pangyomuseum.PageParam
import com.enoughmedia.pangyomuseum.R
import com.enoughmedia.pangyomuseum.component.SceneViewBox
import com.enoughmedia.pangyomuseum.model.Mounds
import com.enoughmedia.pangyomuseum.model.MoundsID
import com.enoughmedia.pangyomuseum.page.popup.PopupVR

import com.enoughmedia.pangyomuseum.page.viewmodel.PageViewModel
import com.jakewharton.rxbinding3.view.clicks
import com.lib.page.PageFragment
import com.lib.page.PagePresenter
import com.skeleton.module.ViewModelFactory
import com.skeleton.rx.RxPageFragment
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.page_mounds.*
import javax.inject.Inject


class PageMounds  : RxPageFragment() {

    private val appTag = javaClass.simpleName
    override fun getLayoutResId() = R.layout.page_mounds
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel:PageViewModel
    private var moundsId:MoundsID? = null
    private var mounds:Mounds? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(PageViewModel::class.java)
    }

    override fun setParam(param: Map<String, Any?>): PageFragment {
        moundsId = param[PageParam.MOUNDS_ID] as? String ?: "0"

        return super.setParam(param)
    }

    override fun onCreatedView() {
        super.onCreatedView()
        sceneViewBox.viewType = SceneViewBox.ViewType.World
        mounds = viewModel.repo.museum.getMound(moundsId)
        mounds?.let {
            infoBox.moundsTitle =  it.title
            infoBox.moundsDesc = it.desc
            sceneViewBox.addMounds(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        moundsId = null
        mounds = null
    }



    override fun onTransactionCompleted() {
        super.onTransactionCompleted()
        if( !viewModel.repo.setting.getViewGesture() ){
            PagePresenter.getInstance<PageID>().openPopup(PageID.POPUP_GESTURE)
        }
        //infoMessage.viewMessage(R.string.page_map_camera_guide)
    }

    override fun onSubscribe() {
        super.onSubscribe()
        btnClose.clicks().subscribe {
            PagePresenter.getInstance<PageID>().goBack()
        }.apply { disposables.add(this) }

        btnPano.clicks().subscribe {
            val param = HashMap<String, Any?>()
            param[PageParam.MOUNDS] = mounds
            param[PageParam.VIEW_TYPE] = PopupVR.ViewType.Mono
            PagePresenter.getInstance<PageID>().openPopup(PageID.POPUP_VR, param)
        }.apply { disposables.add(this) }

        btnCardboard.clicks().subscribe {
            val param = HashMap<String, Any?>()
            param[PageParam.MOUNDS] = mounds
            param[PageParam.VIEW_TYPE] = PopupVR.ViewType.Stereo
            PagePresenter.getInstance<PageID>().openPopup(PageID.POPUP_VR, param)
        }.apply { disposables.add(this) }
    }

    override fun onPause() {
        super.onPause()
        sceneViewBox.onPause()
    }

    override fun onResume() {
        super.onResume()
        sceneViewBox.onPause()
    }



}