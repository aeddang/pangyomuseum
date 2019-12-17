package com.lib.page

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.lib.util.Log


abstract class PageFragment: Fragment(), Page,  ViewTreeObserver.OnGlobalLayoutListener{

    private val appTag = javaClass.simpleName

    protected open var isRestoredPage:Boolean = false
    private var isInit:Boolean = true
    private var restoredView:View? = null
    var pageID:Any? = null; internal set
    var isPopup:Boolean = false; internal set
    protected var transactionRunnable: Runnable = Runnable { onTransactionCompleted() }
    protected var delegate: PageDelegate<Any>? = null
    fun setOnPageEvent(delegate: PageDelegate<Any>){ this.delegate = delegate }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.restoredView?.let { return it }
        return inflater.inflate(getLayoutResId(), container, false)
    }

    override fun onCreatedView() {}

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if( isInit ) {
            onCreatedView()
            view.viewTreeObserver?.addOnGlobalLayoutListener ( this )
            if(isPopup) delegate?.onAddedPage(pageID)
        }
    }

    @CallSuper
    override fun onGlobalLayout(){
        view?.viewTreeObserver?.removeOnGlobalLayoutListener( this )
    }

    protected fun transactionCompleted(){
        val handler = Handler()
        handler.post(transactionRunnable)
    }



    @CallSuper
    override fun onAttach(context: Context) {
        super.onAttach(context)
        onAttached()
    }

    @CallSuper
    override fun onDetach() {
        super.onDetach()
        onDetached()
    }

    @CallSuper
    override fun onDestroyView() {
        if( isPopup ) delegate?.onRemovedPage(pageID)
        delegate = null
        if( isRestoredPage ){
            isInit = false
            this.restoredView = this.view
            super.onDestroyView()
            return
        } else {
            super.onDestroyView()
            view?.viewTreeObserver?.removeOnGlobalLayoutListener( this )
            onDestroyedView()
        }
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        if( isRestoredPage ) {
            onDestroyedView()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){}

    override fun onPageReload() {
        Log.d(appTag, "onPageReload")
    }
    override fun onCategoryChanged(prevPageId: Any?) {
        Log.d(appTag, "onCategoryChanged")
    }

    override fun onPageEvent(id:Any?, type:String, data:Any? ){}
    override fun onPageAdded(id:Any?){}
    override fun onPageRemoved(id:Any?){}
    open fun setParam(param:Map<String,Any?>):PageFragment { return this }
    open fun isBackAble():Boolean { return true }

    private var shadowUi:View? = null
    protected var isAutoClearShadow = true
    @LayoutRes
    open protected fun getShadowUi():Int? { return null }


}