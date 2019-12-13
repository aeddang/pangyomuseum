package com.skeleton.view.graph

import android.annotation.SuppressLint
import android.graphics.Point
import androidx.annotation.CheckResult
import com.jakewharton.rxbinding3.internal.checkMainThread
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable

@CheckResult
fun Graph.draw(): Observable<ArrayList<Pair<Double,Point>>> {
    return DrawGraphEventObservable(this)
}


private class DrawGraphEventObservable( private val view: Graph) : Observable<ArrayList<Pair<Double,Point>>>() {
    @SuppressLint("RestrictedApi")

    override fun subscribeActual(observer: Observer<in ArrayList<Pair<Double,Point>>>) {
        if ( !checkMainThread(observer))  return
        val listener = Listener(view, observer)
        observer.onSubscribe(listener)
        view.setOnDrawGraphListener(listener)
    }
    private class Listener(
        private val view: Graph,
        private val observer: Observer<in ArrayList<Pair<Double,Point>>>
    ) : MainThreadDisposable(), Graph.Delegate {

        override fun drawGraph(graph: Graph, datas:ArrayList<Pair<Double,Point>>) {
            if (isDisposed) return
            observer.onNext(datas)
        }
        override fun onDispose() {
            view.setOnDrawGraphListener(null)
        }
    }
}