package com.enoughmedia.pangyomuseum.page

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.enoughmedia.pangyomuseum.PageID
import com.enoughmedia.pangyomuseum.R
import com.enoughmedia.pangyomuseum.component.InfoMessage
import com.enoughmedia.pangyomuseum.page.viewmodel.PageViewModel
import com.jakewharton.rxbinding3.view.clicks
import com.lib.page.PagePresenter
import com.skeleton.module.ImageFactory
import com.skeleton.module.ViewModelFactory
import com.skeleton.rx.RxPageFragment
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.cp_open_camera.view.*
import kotlinx.android.synthetic.main.page_map.*
import javax.inject.Inject


class PageMap  : RxPageFragment() {

    private val appTag = javaClass.simpleName
    override fun getLayoutResId() = R.layout.page_map
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
        if( !viewModel.repo.setting.getViewGuide() ){
            PagePresenter.getInstance<PageID>().openPopup(PageID.POPUP_GUIDE)
        }
        infoMessage.viewMessage(R.string.page_map_camera_guide)
    }

    override fun onSubscribe() {
        super.onSubscribe()
        btnGuide.clicks().subscribe {
            PagePresenter.getInstance<PageID>().openPopup(PageID.POPUP_GUIDE)
        }.apply { disposables.add(this) }

        btnCamera.btn.clicks().subscribe {
            PagePresenter.getInstance<PageID>().openPopup(PageID.POPUP_SCAN)
        }.apply { disposables.add(this) }
    }

    override fun onPause() {
        super.onPause()
        btnCamera.onPause()
    }

    override fun onResume() {
        super.onResume()
        btnCamera.onResume()
    }



}