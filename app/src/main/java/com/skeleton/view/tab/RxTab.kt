package com.skeleton.view.tab

import android.annotation.SuppressLint
import androidx.annotation.CheckResult
import com.jakewharton.rxbinding3.internal.checkMainThread
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable


@CheckResult
fun <T> Tab<T>.selected(): Observable<T> {
    return TabObservable(this)
}

class TabObservable<T>(private val module: Tab<T>) : Observable<T>() {
    @SuppressLint("RestrictedApi")
    private val appTag = javaClass.simpleName
    override fun subscribeActual(observer: Observer<in T>) {
        if ( ! checkMainThread(observer))  return
        val listener = Listener( module, observer)
        observer.onSubscribe(listener)
        module.setOnSelectListener(listener)
    }
    inner class Listener(
        private val module: Tab<T>,
        private val observer: Observer<in T>
    ) : MainThreadDisposable(), Tab.Delegate<T> {

        override fun onSelected(view: Tab<T>, id: T) {
            if (isDisposed) return
            observer.onNext(id)
        }

        override fun onDispose() {
            module.setOnSelectListener(null)
        }
    }
}