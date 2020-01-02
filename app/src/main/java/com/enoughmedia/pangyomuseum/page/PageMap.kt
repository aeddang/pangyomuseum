package com.enoughmedia.pangyomuseum.page

import android.os.Bundle
import android.util.Size
import androidx.lifecycle.ViewModelProviders
import com.enoughmedia.pangyomuseum.MainActivity
import com.enoughmedia.pangyomuseum.PageID
import com.enoughmedia.pangyomuseum.R
import com.enoughmedia.pangyomuseum.component.InfoMessage
import com.enoughmedia.pangyomuseum.component.Marker
import com.enoughmedia.pangyomuseum.page.viewmodel.PageViewModel
import com.enoughmedia.pangyomuseum.store.BeaconController
import com.jakewharton.rxbinding3.view.clicks
import com.lib.page.PagePresenter
import com.lib.util.getCropRatioSize
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

    private var markers:List<Marker>? = null

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
        createMarkers()
    }

    private fun createMarkers(){
        context ?: return
        var tx= 0
        var ty = 0
        val areaF = Size(areaMaker.width, areaMaker.height).getCropRatioSize(Size(830,499))
        markers = viewModel.repo.museum.mounds.map{
            val m = Marker(context!!)
            m.id = it.id
            it.markerPos?.let {pos->
                m.x = areaF.left + (areaF.width() * pos.x)
                m.y = areaF.top + (areaF.height() * pos.y)
                areaMaker.addView(m)
            }
            m
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

        btnSetup.clicks().subscribe {
            PagePresenter.getInstance<PageID>().openPopup(PageID.POPUP_SETTING)
        }.apply { disposables.add(this) }

        btnCamera.btn.clicks().subscribe {
            PagePresenter.getInstance<PageID>().openPopup(PageID.POPUP_SCAN)
        }.apply { disposables.add(this) }

        beaconController.observable.subscribe { be->
            val marker =markers?.find { be.value == it.id }
            when(be.type){
                BeaconController.Event.DetermineStateForRegion ->{ btnCamera.effectOn(false) }
                BeaconController.Event.ExitRegion -> {
                    infoMessage.hideMessage()
                    btnCamera.effectOn(false)
                    marker?.findOff()
                }
                BeaconController.Event.EnterRegion ->{
                    val mounds = viewModel.repo.museum.getMound(be.value)
                    mounds ?: return@subscribe
                    infoMessage.viewMessage(R.string.page_map_camera_guide, InfoMessage.Type.Marker)

                    val main = PagePresenter.getInstance<PageID>().activity as?  MainActivity?
                    val msg = mounds.title + (context?.getString(R.string.popup_scan_find_info) ?: "")
                    main?.viewMessage(msg, InfoMessage.Type.Find, 2000L)
                    btnCamera.effectOn(true)
                    marker?.findOn()
                }
            }



        }.apply { disposables.add(this) }
    }

    override fun onPageAdded(id: Any?) {
        super.onPageAdded(id)

        if(id == PageID.POPUP_SETTING) {
            btnCamera.onPause()
            beaconController.destroyManager()
        }
    }

    override fun onPageRemoved(id: Any?) {
        super.onPageRemoved(id)
        if(id == PageID.POPUP_SETTING) beaconController.initManager()
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


