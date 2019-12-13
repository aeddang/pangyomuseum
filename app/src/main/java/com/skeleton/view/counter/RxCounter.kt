package com.skeleton.view.counter

import android.view.View
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.concurrent.TimeUnit


class RxCounter(val text:TextView? = null) {

    private val Tag = javaClass.simpleName
    private var isOnTimer = false
    private var finalCount:Long = 0
    private var finalPeriod:Long = 1
    private var finalProgress:Long = 0
    private var finalIsProgress:Boolean = false
    private var finalObservable:Subject<Long>? = null
    private var finalTimeUnit:TimeUnit =  TimeUnit.SECONDS
    val countObservable = PublishSubject.create<Long>()
    private var disposable:Disposable? = null
    init {
        text?.visibility = View.GONE
    }

    fun pause(){
        isOnTimer = false
        disposable?.dispose()
        disposable = null
    }

    private var resumeCount:Long = 0
    fun resume():Subject<Long>?{
        resumeCount = finalProgress
        val count = finalCount - (finalProgress/finalPeriod)
        if (count == 0L) return null
        return onStart(count)
    }

    fun start(count:Long, isProgress:Boolean = false, period:Long = 1000, timeUnit:TimeUnit = TimeUnit.MILLISECONDS):Subject<Long>{
        finalCount = count
        finalPeriod = period
        finalIsProgress = isProgress
        finalTimeUnit = timeUnit
        resumeCount = 0
        finalObservable = if(finalIsProgress) PublishSubject.create() else countObservable
        return onStart(count)
    }

    private fun onStart(count:Long):Subject<Long>{
        if( isOnTimer ) return finalObservable ?: countObservable
        isOnTimer = true
        disposable?.dispose()
        text?.visibility = View.VISIBLE
        text?.text = ""
        disposable = Observable.interval(finalPeriod, finalTimeUnit)
            .take(count)
            .observeOn(AndroidSchedulers.mainThread()).subscribe (
                {
                    finalProgress = (it*finalPeriod ) + resumeCount
                    val num = finalCount - finalProgress - 1
                    if(num != 0L) text?.text = num.toString()
                    else text?.text = ""
                    if(finalIsProgress) finalObservable?.onNext(finalProgress)

                },
                {
                    stop()
                    if(finalIsProgress) finalObservable?.onError(it)
                },
                {
                    if(finalIsProgress) finalObservable?.onComplete()
                    else finalObservable?.onNext(finalProgress)
                    stop()
                }
            )
        return finalObservable ?: countObservable
    }

    fun stop(){
        if( !isOnTimer ) return
        finalCount = 0
        finalProgress = 0
        isOnTimer = false
        finalObservable = null
        disposable?.dispose()
        disposable = null
        text?.text = ""
        text?.visibility = View.GONE
    }



}


