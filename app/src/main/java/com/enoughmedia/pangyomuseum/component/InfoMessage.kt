package com.enoughmedia.pangyomuseum.component

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.StringRes
import com.enoughmedia.pangyomuseum.R
import com.jakewharton.rxbinding3.view.clicks
import com.lib.util.AnimationDuration
import com.lib.util.animateAlpha
import com.skeleton.rx.RxFrameLayout
import com.skeleton.rx.RxLinearLayout
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.cp_info_message.view.*
import kotlinx.android.synthetic.main.cp_info_message.view.text

import java.util.concurrent.TimeUnit


class InfoMessage: RxLinearLayout {
    enum class Type {
        Default,
        Marker,
        Find,
        Book
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context,attrs)
    override fun getLayoutResId(): Int { return R.layout.cp_info_message }
    private val appTag = javaClass.simpleName

    override fun onCreatedView() {
        alpha = 0.0f
        visibility = View.GONE

    }
    override fun onDestroyedView() {
        closeDisposable?.dispose()
        closeDisposable = null
    }

    override fun onSubscribe() {
        super.onSubscribe()
        btnOff.clicks().subscribe {
            this.animateAlpha(0.0f)
        }.apply { disposables?.add(this) }
    }

    fun hideMessage(){
        closeDisposable?.dispose()
        animateAlpha(0.0f)
    }

    fun viewMessage(@StringRes resId: Int, type:Type = Type.Default, duration:Long = -1L){
        viewMessage(context.getString(resId), type, duration)
    }
    fun viewMessage(msg:String, type:Type = Type.Default, duration:Long = -1L){
        closeDisposable?.dispose()
        closeDisposable = null
        animateAlpha(1.0f)
        when(type){
            Type.Default ->{
                bg.setImageResource(R.drawable.bg_blue)
                icon.setImageResource(R.drawable.icon_marker)
            }
            Type.Marker ->{
                bg.setImageResource(R.drawable.bg_green)
                icon.setImageResource(R.drawable.icon_marker)
            }
            Type.Find ->{
                bg.setImageResource(R.drawable.bg_red)
                icon.setImageResource(R.drawable.icon_brick)
            }
            Type.Book ->{
                bg.setImageResource(R.drawable.bg_black)
                icon.setImageResource(R.drawable.icon_book)
            }
        }
        text.text = msg
        btnOff.visibility = View.VISIBLE
        if(duration > -1L) autoClose(duration)


    }

    private var closeDisposable: Disposable? = null
    private fun autoClose(duration:Long){
        btnOff.visibility = View.GONE
        Observable.interval(duration, TimeUnit.MILLISECONDS)
            .take(1)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe {
                animateAlpha(0.0f)
            }.apply { closeDisposable = this }
    }











}


