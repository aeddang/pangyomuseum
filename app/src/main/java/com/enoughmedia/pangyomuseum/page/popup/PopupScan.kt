package com.enoughmedia.pangyomuseum.page.popup

import android.graphics.Bitmap
import android.os.Bundle
import com.enoughmedia.pangyomuseum.PageID
import com.enoughmedia.pangyomuseum.PageParam
import com.enoughmedia.pangyomuseum.R
import com.enoughmedia.pangyomuseum.component.InfoMessage
import com.enoughmedia.pangyomuseum.component.scan
import com.enoughmedia.pangyomuseum.model.Mounds
import com.enoughmedia.pangyomuseum.store.Museum
import com.enoughmedia.pangyomuseum.store.SettingPreference
import com.google.zxing.BinaryBitmap
import com.google.zxing.LuminanceSource
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import com.lib.page.PagePresenter
import com.lib.util.Log
import com.skeleton.rx.RxPageFragment
import com.skeleton.view.camera.init
import dagger.android.support.AndroidSupportInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.popup_scan.*
import java.lang.Exception
import javax.inject.Inject

class PopupScan  : RxPageFragment() {

    private val appTag = javaClass.simpleName
    override fun getLayoutResId() = R.layout.popup_scan

    @Inject
    lateinit var setting: SettingPreference
    @Inject
    lateinit var museum: Museum
    private val reader = QRCodeReader()
    private lateinit var findCodes:List<String>
    private var findObservable:PublishSubject<Mounds>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)

    }

    override fun onCreatedView() {
        super.onCreatedView()
        findCodes = museum.findQRCodes
        camera.startCamera()
        camera.isExtraction = true

    }

    override fun onDestroyedView() {
        super.onDestroyedView()
        findObservable = null
    }

    override fun onTransactionCompleted() {
        super.onTransactionCompleted()
        if(!setting.getViewScanGuide()) infoMessage.viewMessage(R.string.popup_scan_guide, InfoMessage.Type.Marker)
        setting.putViewScanGuide(true)
    }

    override fun onSubscribe() {
        super.onSubscribe()

        camera.scan().subscribe {
            reading(it)
        }.apply { disposables.add(this) }

        findObservable = PublishSubject.create<Mounds>()
        findObservable!!.observeOn(AndroidSchedulers.mainThread()).subscribe {
            findObservable = null

            val param = HashMap<String, String>()
            param[PageParam.MOUNDS_ID] = it.id
            PagePresenter.getInstance<PageID>().pageChange(PageID.MOUNDS, param)
        }.apply { disposables.add(this) }
    }



    private fun reading(bitmap:Bitmap){
        //Log.i(appTag, "reading ${bitmap.width} ${bitmap.height}")
        val intArray = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(intArray, 0, bitmap.width, 0, 0,bitmap.width, bitmap.height)
        val source: LuminanceSource = RGBLuminanceSource(bitmap.width, bitmap.height, intArray)
        try {
            val result = reader.decode(BinaryBitmap(HybridBinarizer(source)))
            Log.i(appTag, "result ${result.text}")
            val find = findCodes.find { it == result.text }
            find?.let { f->
                val mounds = museum.getMoundByCode(f)
                mounds?.let { findObservable?.onNext(it) }
            }

        } catch (e:Exception){

        }

    }

    override fun onPause() {
        super.onPause()
        camera.onPause()
    }

    override fun onResume() {
        super.onResume()
        camera.onResume()
    }

}