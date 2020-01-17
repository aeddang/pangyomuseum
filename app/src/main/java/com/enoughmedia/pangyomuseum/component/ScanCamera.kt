package com.enoughmedia.pangyomuseum.component

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.media.Image
import android.util.AttributeSet
import androidx.annotation.CheckResult
import androidx.fragment.app.FragmentActivity
import com.enoughmedia.pangyomuseum.R
import com.jakewharton.rxbinding3.internal.checkMainThread
import com.lib.page.PagePresenter
import com.skeleton.view.camera.Camera
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable


@CheckResult
fun ScanCamera.scan(): Observable<Bitmap> {
    return ScanCameraEventObservable(this)
}

open class ScanCamera: Camera {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context,attrs)
    override fun getLayoutResId(): Int { return R.layout.cp_scan_camera }
    private var delegate:Delegate? = null
    private val appTag = javaClass.simpleName
    override fun getActivity(): FragmentActivity? = PagePresenter.getInstance<Any>().activity as? FragmentActivity
    override fun getNeededPermissions():Array<String> = arrayOf(Manifest.permission.CAMERA )


    interface Delegate{
        fun extractionData(camera: ScanCamera, data: Bitmap){}
    }
    fun setOnExtractionCameraListener( _delegate:Delegate? ){ delegate = _delegate }


    override fun onCreatedView() {
        super.onCreatedView()
        this.isFront = false
        cameraRatioType =  CameraRatioType.Smallest
        this.captureMode = CaptureMode.ExtractionBitmap
        this.extractionFps = 5
    }

    override fun onExtract(image: Bitmap) {
        if(isExtraction) delegate?.extractionData(this, image)
    }


}


private class ScanCameraEventObservable( private val view: ScanCamera) : Observable<Bitmap>() {
    @SuppressLint("RestrictedApi")
    override fun subscribeActual(observer: Observer<in Bitmap>) {
        if ( !checkMainThread(observer))  return
        val listener = Listener(view, observer)
        observer.onSubscribe(listener)
        view.setOnExtractionCameraListener(listener)
    }
    private class Listener(
        private val view: ScanCamera,
        private val observer: Observer<in Bitmap>
    ) : MainThreadDisposable(), ScanCamera.Delegate {

        override  fun extractionData(camera: ScanCamera, data: Bitmap){
            if (isDisposed) return
            observer.onNext(data)
        }

        override fun onDispose() {
            view.setOnExtractionCameraListener(null)
        }
    }
}
