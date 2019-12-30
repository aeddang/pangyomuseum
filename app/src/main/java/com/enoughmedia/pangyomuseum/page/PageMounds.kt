package com.enoughmedia.pangyomuseum.page

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.enoughmedia.pangyomuseum.PageID
import com.enoughmedia.pangyomuseum.PageParam
import com.enoughmedia.pangyomuseum.R
import com.enoughmedia.pangyomuseum.component.SceneViewBox
import com.enoughmedia.pangyomuseum.component.SceneViewWorld
import com.enoughmedia.pangyomuseum.model.Mounds
import com.enoughmedia.pangyomuseum.model.MoundsID
import com.enoughmedia.pangyomuseum.page.popup.PopupVR
import com.enoughmedia.pangyomuseum.page.viewmodel.PageViewModel
import com.jakewharton.rxbinding3.view.clicks
import com.lib.page.PageFragment
import com.lib.page.PagePresenter
import com.lib.util.animateAlpha
import com.skeleton.module.ViewModelFactory
import com.skeleton.rx.RxPageFragment
import com.skeleton.view.player.PlayerEvent
import com.skeleton.view.player.init
import dagger.android.support.AndroidSupportInjection
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.page_mounds.*
import java.util.concurrent.TimeUnit
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
        //sceneViewBox.viewType = SceneViewWorld.ViewType.World
        mounds = viewModel.repo.museum.getMound(moundsId)
        mounds?.let {
            infoBox.moundsTitle =  it.title
            infoBox.moundsDesc = it.desc
        }
        sceneViewBox.alpha = 0f
        introStart()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        moundsId = null
        mounds = null
    }

    override fun onTransactionCompleted() {
        super.onTransactionCompleted()
        mounds?.let {
            sceneViewBox.addMounds(it)
            sceneViewBox.animateAlpha(1.0f)
        }
        Observable.interval(10000, TimeUnit.MILLISECONDS)
            .take(1)
            .observeOn(AndroidSchedulers.mainThread()).subscribe {
               introCompleted()
            }.apply { disposables.add(this) }
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
    private fun introStart(){
        infoBox.alpha =0.0f
        btnCardboard.alpha =0.0f
        btnPano.alpha =0.0f
        infoBox.visibility = View.GONE
        btnCardboard.visibility = View.GONE
        btnPano.visibility = View.GONE
    }

    private fun introCompleted(){
        this.view?.invalidate()
        infoBox.animateAlpha(1.0f)
        btnCardboard.animateAlpha(1.0f)
        btnPano.animateAlpha(1.0f)
        if( !viewModel.repo.setting.getViewGesture() ){
            PagePresenter.getInstance<PageID>().openPopup(PageID.POPUP_GESTURE)
        }
    }

    override fun onPause() {
        super.onPause()
        sceneViewBox.onPause()
    }

    override fun onResume() {
        super.onResume()
        sceneViewBox.onResume()
    }



}