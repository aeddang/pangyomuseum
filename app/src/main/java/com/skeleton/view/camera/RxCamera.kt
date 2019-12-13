package com.skeleton.view.camera

import android.annotation.SuppressLint
import android.util.Size
import androidx.annotation.CheckResult
import com.jakewharton.rxbinding3.internal.checkMainThread
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable
import java.io.File


typealias  CameraEventType = String

data class CameraEvent(val type: CameraEventType, val data:Any? = null) {
    companion object {
        const val CAPTURE_START:CameraEventType = "captureStart"
        const val CAPTURE_COMPLETED:CameraEventType = "captureCompleted"
        const val EXTRACT_START:CameraEventType = "extractStart"
        const val EXTRACT_END:CameraEventType = "extractEnd"
        const val ERROR:CameraEventType = "error"
    }
}

@CheckResult
fun Camera.init(): Observable<CameraEvent> {
    return CameraEventObservable(this)
}


private class CameraEventObservable( private val view: Camera) : Observable<CameraEvent>() {
    @SuppressLint("RestrictedApi")
    override fun subscribeActual(observer: Observer<in CameraEvent>) {
        if ( !checkMainThread(observer))  return
        val listener = Listener(view, observer)
        observer.onSubscribe(listener)
        view.setOnCameraListener(listener)
    }
    private class Listener( private val view: Camera, private val observer: Observer<in CameraEvent>) : MainThreadDisposable(), Camera.Delegate {

        override fun onCaptureStart(camera: Camera){
            if (isDisposed) return
            observer.onNext(CameraEvent(CameraEvent.CAPTURE_START))
        }
        override fun onCaptureCompleted(camera: Camera, file: File?){
            if (isDisposed) return
            observer.onNext(CameraEvent(CameraEvent.CAPTURE_COMPLETED, file))
        }
        override fun onExtractStart(camera: Camera, size: Size){
            if (isDisposed) return
            observer.onNext(CameraEvent(CameraEvent.EXTRACT_START, size))
        }
        override fun onExtractEnd(camera: Camera){
            if (isDisposed) return
            observer.onNext(CameraEvent(CameraEvent.EXTRACT_END))
        }
        override fun onError(camera: Camera, type: Camera.Error, data:Any?){
            if (isDisposed) return
            observer.onNext(CameraEvent(CameraEvent.ERROR, type))
        }
        override fun onDispose() {
            view.setOnCameraListener(null)
        }
    }
}