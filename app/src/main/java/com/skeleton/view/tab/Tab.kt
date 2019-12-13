package com.skeleton.view.tab

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.CallSuper
import com.jakewharton.rxbinding3.view.clicks
import com.skeleton.rx.RxLinearLayout

abstract class Tab<T> : RxLinearLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context,attrs)
    private val appTag = javaClass.simpleName

    private var delegate: Delegate<T>? = null
    interface Delegate <T> {
        fun onSelected(view: Tab<T>, idx:Int){}
        fun onSelected(view: Tab<T>, id:T){}
    }
    fun setOnSelectListener( _delegate:Delegate<T>? ){ delegate = _delegate }

    lateinit var tab:Array<View>
    abstract fun getTabMenu(): Array<View>
    protected lateinit var data:Array<T>
    abstract fun getIDData(): Array<T>

    var selectedIdx:Int = -1 ; private set
    var selectedTab:View? = null
    set(value) {
        if(selectedIdx != -1) onWillChangeSelected(selectedIdx)
        selectedTab?.isSelected = false
        field = value
        value?.let {
            it.isSelected = true
            selectedIdx = it.tag as? Int ?: 0
            onChangeSelected(selectedIdx)
        }
    }

    fun setSelect(id:T) {
        val idx = data.indexOf(id)
        setSelect(idx)
    }

    fun setSelect(idx:Int) {
        if(idx < 0) return
        if(idx >= tab.size) return
        selectedTab = tab[idx]
    }
    open protected fun onWillChangeSelected(idx:Int){}
    open protected fun onChangeSelected(idx:Int){}

    @CallSuper
    override fun init(context: Context) {
        super.init(context)
        data = getIDData()
        tab = getTabMenu()
    }

    override fun onCreatedView() {}
    override fun onSubscribe() {
        super.onSubscribe()
        tab.forEachIndexed { index, view ->
            view.tag = index
            view.clicks().subscribe {
                selectedTab = view
                delegate?.onSelected(this, selectedIdx)
                delegate?.onSelected(this, data[selectedIdx])
            }.apply { disposables?.add(this) }
        }
    }

    override fun onDestroyedView() {
        delegate = null
    }


}

