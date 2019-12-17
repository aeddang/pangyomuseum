package com.lib.page

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.annotation.LayoutRes
import androidx.lifecycle.ViewModel
object PageConst{
    const val TRANSACTION_DELAY = 500L
}


interface PagePosition {
    var position: Int
}

interface Page {
    @LayoutRes
    fun getLayoutResId(): Int{ return -1 }
    fun onCreatedView()
    fun onTransactionCompleted(){}
    fun onAttached(){}
    fun onDetached(){}
    fun onDestroyedView()
    fun onPageEvent(id:Any?, type:String, data:Any? = null){}
    fun onPageAdded(id:Any?){}
    fun onPageRemoved(id:Any?){}
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){}
    fun onCategoryChanged(prevPageId:Any?){}
    fun onPageReload(){}
}

interface PageChildView {
    fun setParentViewModel(vm: ViewModel?){}
    fun onGlobalLayout(){}
}

interface PageDelegate<T> {
    fun onAddedPage(id:T?)
    fun onTransactionCompleted(id:T?)
    fun onRemovedPage(id:T?)
    fun onEvent(id:T?, type:String, data:Any? = null)
}

interface PageRequestPermission {
    fun onRequestPermissionResult(resultAll:Boolean ,  permissions: List<Boolean>?){}
}

interface Presenter<T> {
    var activity: Activity<T>?
    fun goHome(idx:Int = 0)
    fun goBack(id:T)
    fun goBack()
    fun toggleNavigation()
    fun showNavigation()
    fun hideNavigation()
    fun clearPageHistory(id:T?=null): Presenter<T>
    fun closePopup(id:T,isAni:Boolean = true): Presenter<T>
    fun closeAllPopup(isAni:Boolean = true): Presenter<T>
    fun openPopup(id:T,param:Map<String, Any?>? = null, sharedElement:View? = null, transitionName:String? = null): Presenter<T>
    fun pageStart(id:T): Presenter<T>
    fun pageChange(id:T,param:Map<String, Any?>? = null, sharedElement:View? = null, transitionName:String? = null): Presenter<T>
    fun hasPermissions( permissions: Array<String> ): Boolean
    fun requestPermission( permissions: Array<out String>, requester: PageRequestPermission)
    fun loading(isRock:Boolean = false): Presenter<T>
    fun loaded(): Presenter<T>
    fun finish()
}

interface Activity<T> {
    fun getPrevPage():Pair< T, Map< String, Any? >? >?
    fun getCurrentPageFragment(): PageFragment?
    fun getCurrentFragment(): PageFragment?
    fun getCurrentContext(): Context
    fun getCurrentActivity(): android.app.Activity
    fun getPageAreaSize():Pair<Float,Float>

}

interface View<T> {
    fun onClearPageHistory(id:T?)
    fun onPageStart(id:T)
    fun onBack(id:T)
    fun onBack()
    fun onFinish()
    fun onPageChange(id:T,param:Map<String, Any?>? = null, sharedElement: View? = null, transitionName:String? = null)
    fun onOpenPopup(id:T, param:Map<String, Any?>? = null, sharedElement: View? = null, transitionName:String? = null)
    fun onClosePopup(id:T, isAni:Boolean = true)
    fun onCloseAllPopup(isAni:Boolean = true)
    fun onShowNavigation(){}
    fun onHideNavigation(){}
    fun hasPermissions( permissions: Array<out String> ):  Pair< Boolean, List<Boolean>>?
    fun requestPermission( permissions: Array<out String> , requester: PageRequestPermission)
    fun loading(isRock:Boolean){}
    fun loaded(){}

}

interface Model<T> {
    fun getHome(idx:Int = 0):T
    fun isHome(id:T):Boolean
    fun isBackStack(id:T):Boolean
    fun isHistory(id:T):Boolean
    fun onDestroy()
}