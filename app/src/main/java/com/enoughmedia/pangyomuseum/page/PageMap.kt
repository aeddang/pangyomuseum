package com.enoughmedia.pangyomuseum.page

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.enoughmedia.pangyomuseum.MainActivity
import com.enoughmedia.pangyomuseum.PageID
import com.enoughmedia.pangyomuseum.R
import com.enoughmedia.pangyomuseum.component.InfoMessage
import com.enoughmedia.pangyomuseum.page.viewmodel.PageViewModel
import com.enoughmedia.pangyomuseum.store.BeaconController
import com.jakewharton.rxbinding3.view.clicks
import com.lib.page.PagePresenter
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

    @Inject
    lateinit var beaconController: BeaconController

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
        if( !viewModel.repo.setting.getViewGuide() ) PagePresenter.getInstance<PageID>().openPopup(PageID.POPUP_GUIDE)
        if( !viewModel.repo.setting.getViewMapGuide() ) {
            infoMessage.viewMessage(R.string.page_map_camera_guide)
            viewModel.repo.setting.putViewMapGuide(true)
        }

    }

    override fun onSubscribe() {
        super.onSubscribe()
        btnGuide.clicks().subscribe {
            PagePresenter.getInstance<PageID>().openPopup(PageID.POPUP_GUIDE)
        }.apply { disposables.add(this) }

        btnBook.clicks().subscribe {
            PagePresenter.getInstance<PageID>().pageChange(PageID.BOOK)
        }.apply { disposables.add(this) }

        btnCamera.btn.clicks().subscribe {
            PagePresenter.getInstance<PageID>().openPopup(PageID.POPUP_SCAN)
        }.apply { disposables.add(this) }

        beaconController.observable.subscribe {
            when(it.type){
                BeaconController.Event.DetermineStateForRegion ->{ btnCamera.effectOn(false) }
                BeaconController.Event.ExitRegion -> {
                    infoMessage.hideMessage()
                    btnCamera.effectOn(false)
                }
                BeaconController.Event.EnterRegion ->{
                    val mounds = viewModel.repo.museum.getMoundByBeacon(it.value)
                    mounds ?: return@subscribe
                    infoMessage.viewMessage(R.string.page_map_camera_guide, InfoMessage.Type.Marker)

                    val main = PagePresenter.getInstance<PageID>().activity as?  MainActivity?
                    val msg = mounds.title + (context?.getString(R.string.popup_scan_find_info) ?: "")
                    main?.viewMessage(msg, InfoMessage.Type.Find, 2000L)
                    btnCamera.effectOn(true)
                }
            }



        }.apply { disposables.add(this) }
    }

    override fun onPause() {
        super.onPause()
        btnCamera.onPause()
        beaconController.destroyManager()

    }

    override fun onResume() {
        super.onResume()
        btnCamera.onResume()
        beaconController.initManager()
    }



}