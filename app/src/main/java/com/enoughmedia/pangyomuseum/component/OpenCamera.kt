package com.enoughmedia.pangyomuseum.component

import android.content.Context
import android.util.AttributeSet
import com.enoughmedia.pangyomuseum.R
import com.lib.util.AnimationDuration
import com.lib.util.animateAlpha
import com.skeleton.rx.RxFrameLayout
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.cp_open_camera.view.*
import java.util.concurrent.TimeUnit


class OpenCamera: RxFrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context,attrs)
    override fun getLayoutResId(): Int { return R.layout.cp_open_camera }
    private val appTag = javaClass.simpleName

    override fun onCreatedView() {
        effectDefault.alpha = 0.0f
        effectFind.alpha = 0.0f
    }

    override fun onDestroyedView() {
        effectDisposable?.dispose()
    }

    fun onResume(){

    }

    fun onPause(){
        effectOff()
    }

    private var isFind = false
    private var effectDisposable: Disposable? = null

    fun effectOff() {
        effectDisposable?.dispose()
        effectDefault.animateAlpha(0.0f, false, AnimationDuration.SHORT)
        effectFind.animateAlpha(0.0f, false, AnimationDuration.SHORT)
    }

    fun effectOn(isFind:Boolean = this.isFind) {
        this.isFind = isFind
        effectOn()
    }

    private fun effectOn(){
        effectDisposable?.dispose()
        val target = if(isFind) effectFind else effectDefault
        effectDefault.alpha = 0.0f
        effectFind.alpha = 0.0f
        var targetAlpha = 1.0f
        var duration = AnimationDuration.LONG
        Observable.interval(1000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe {
                target.animateAlpha(targetAlpha , false, duration)
                if(targetAlpha == 1.0f) {
                    targetAlpha = 0.0f
                    duration = AnimationDuration.SHORT
                }else{
                    targetAlpha = 1.0f
                    duration = AnimationDuration.LONG
                }
            }.apply { effectDisposable = this }
    }




}


