package com.enoughmedia.pangyomuseum.page

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.enoughmedia.pangyomuseum.PageID
import com.enoughmedia.pangyomuseum.R

import com.enoughmedia.pangyomuseum.page.viewmodel.PageViewModel
import com.jakewharton.rxbinding3.view.clicks
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(PageViewModel::class.java)
    }

    override fun onCreatedView() {
        super.onCreatedView()

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
            PagePresenter.getInstance<PageID>().openPopup(PageID.POPUP_AR)
        }.apply { disposables.add(this) }

        btnCardboard.clicks().subscribe {
            PagePresenter.getInstance<PageID>().openPopup(PageID.POPUP_VR)
        }.apply { disposables.add(this) }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()

    }



}