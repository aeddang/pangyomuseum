package com.skeleton.view.player

import android.annotation.SuppressLint
import androidx.annotation.CheckResult
import com.jakewharton.rxbinding3.internal.checkMainThread
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable

typealias  PlayerEventType = String

data class PlayerEvent(val type:  PlayerEventType, val data:Any? = null) {
    companion object {
        const val PLAY:PlayerEventType = "play"
        const val STOP:PlayerEventType = "stop"
        const val LOAD:PlayerEventType = "load"
        const val BUFFERING:PlayerEventType = "buffering"
        const val READY:PlayerEventType = "ready"
        const val ERROR:PlayerEventType = "error"
        const val COMPLETED:PlayerEventType = "completed"
    }
}

@CheckResult
fun PlayBack.init(): Observable<PlayerEvent> {
    return PlayerEventObservable(this)
}

@CheckResult
fun PlayBack.timeChange(): Observable<Long> {
    return PlayerTimeObservable(this)
}


private class PlayerEventObservable( private val view: PlayBack) : Observable<PlayerEvent>() {
    @SuppressLint("RestrictedApi")
    override fun subscribeActual(observer: Observer<in PlayerEvent>) {
        if ( !checkMainThread(observer))  return
        val listener = Listener(view, observer)
        observer.onSubscribe(listener)
        view.setOnPlayerListener(listener)
    }
    private class Listener(private val view: PlayBack, private val observer: Observer<in PlayerEvent>) : MainThreadDisposable(), PlayBackDelegate {

        override fun onLoad(player: PlayBack, duration: Long) {
            if (isDisposed) return
            observer.onNext(PlayerEvent(PlayerEvent.LOAD, duration))
        }

        override fun onPlay(player: PlayBack) {
            if (isDisposed) return
            observer.onNext(PlayerEvent(PlayerEvent.PLAY))
        }

        override fun onStop(player: PlayBack) {
            if (isDisposed) return
            observer.onNext(PlayerEvent(PlayerEvent.STOP))
        }

        override fun onError(player: PlayBack, error: Any){
            if (isDisposed) return
            observer.onNext(PlayerEvent(PlayerEvent.ERROR, error))
        }

        override fun onBuffering(player: PlayBack) {
            if (isDisposed) return
            observer.onNext(PlayerEvent(PlayerEvent.BUFFERING))
        }

        override fun onReady(player: PlayBack) {
            if (isDisposed) return
            observer.onNext(PlayerEvent(PlayerEvent.READY))
        }

        override fun onCompleted(player: PlayBack) {
            if (isDisposed) return
            observer.onNext(PlayerEvent(PlayerEvent.COMPLETED))
        }

        override fun onDispose() {
            view.setOnPlayerListener(null)
        }
    }
}

private class PlayerTimeObservable( private val view: PlayBack) : Observable<Long>() {
    @SuppressLint("RestrictedApi")
    override fun subscribeActual(observer: Observer<in Long>) {
        if ( !checkMainThread(observer))  return
        val listener = Listener(view, observer)
        observer.onSubscribe(listener)
        view.setOnPlayTimeListener(listener)
    }
    private class Listener(private val view: PlayBack, private val observer: Observer<in Long>) : MainThreadDisposable(), PlayBackTimeDelegate{
        override fun onTimeChanged(player: PlayBack, t: Long) {
            if (isDisposed) return
            observer.onNext(t)
        }
        override fun onDispose() {
            view.setOnPlayTimeListener(null)
        }
    }
}