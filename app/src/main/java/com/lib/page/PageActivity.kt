package com.lib.page

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.transition.ChangeBounds
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.AnimRes
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import java.util.*
import kotlin.math.abs


abstract class PageActivity<T> : AppCompatActivity(), View<T>, Page, PageDelegate<Any>,
    Activity<T> {
    companion object {
        val TAG = javaClass.simpleName
    }
    open lateinit var pagePresenter: PagePresenter<T>;  protected set
    protected lateinit var pageArea:ViewGroup

    @IdRes abstract fun getPageAreaId(): Int
    @StringRes abstract fun getPageExitMsg(): Int
    abstract fun getHomes():Array<T>
    protected open fun getBackStacks():Array<T>? { return null }
    protected open fun getDisableHistorys():Array<T>? { return null }
    @AnimRes protected open fun getPageStart(): Int { return android.R.anim.fade_in }
    @AnimRes protected open fun getPageIn(isBack:Boolean): Int { return if(isBack) android.R.anim.fade_in else android.R.anim.slide_in_left}
    @AnimRes protected open fun getPageOut(isBack:Boolean): Int { return if(isBack) android.R.anim.fade_out else android.R.anim.slide_out_right}
    @AnimRes protected open fun getPopupIn(): Int { return android.R.anim.fade_in }
    @AnimRes protected open fun getPopupOut(): Int { return android.R.anim.fade_in }
    protected open fun getSharedChange():Any { return ChangeBounds() }

    protected var currentPage: T? = null
    protected var currentPageParam: Map<String, Any>? = null
    private val historys = Stack< Pair< T, Map< String, Any >? >> ()
    private val popups = ArrayList<T>()

    @SuppressLint("UseSparseArrays")
    private var currentRequestPermissions = HashMap< Int , PageRequestPermission>()


    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val model = PageModel<T>()
        model.homes = getHomes()
        model.backStacks = getBackStacks()
        model.disableHistorys = getDisableHistorys()
        pagePresenter = PagePresenter(this, model)
        pagePresenter.activity = this
        setContentView(getLayoutResId())
        pageArea = findViewById(getPageAreaId())
        onCreatedView()
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        currentPage = null
        pagePresenter.onDestroy()
        popups.clear()
        historys.clear()
        onDestroyedView()
    }

    @CallSuper
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        onDetached()
    }

    @CallSuper
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        onAttached()
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun hasPermissions(permissions: Array<out String> ): Pair< Boolean, List<Boolean>>? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return null
        val permissionResults = ArrayList< Boolean >()
        var resultAll = true
        for (permission in permissions) {
            val grant =  checkSelfPermission( permission ) == PackageManager.PERMISSION_GRANTED
            permissionResults.add ( grant )
            if( !grant ) resultAll = false
        }
        return Pair(resultAll, permissionResults )
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun requestPermission(permissions: Array<out String>, requester: PageRequestPermission)
    {
        val grantResult = currentRequestPermissions.size
        currentRequestPermissions[ grantResult ] = requester
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            requestPermissionResult( grantResult, true )
            return
        }

        hasPermissions(permissions)?.let {

            if ( !it.first ) {

                requestPermissions( permissions, grantResult)
            } else {
                requestPermissionResult(grantResult, true )
            }
        }
    }

    private fun requestPermissionResult(requestCode: Int, resultAll:Boolean , permissions: List<Boolean>? = null )
    {
        currentRequestPermissions[ requestCode ]?.onRequestPermissionResult(resultAll, permissions)
        currentRequestPermissions.remove(requestCode)
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
        hasPermissions(permissions)?.let { requestPermissionResult(requestCode, it.first, it.second) }
    }

    override fun getCurrentContext(): Context{
        return this.applicationContext
    }

    override fun getPrevPage(): Pair<T, Map<String, Any>?>? {
        if(historys.isEmpty()) return null
        return historys.peek()
    }

    override fun getCurrentActivity(): android.app.Activity {
        return this
    }

    private fun getPageFragment(pageID:T): PageFragment?{
        val fragment = supportFragmentManager.findFragmentByTag(pageID.toString())
        return fragment as? PageFragment
    }

    override fun getCurrentPageFragment(): PageFragment? {
        if( currentPage == null ) return null
        return getPageFragment(currentPage!!)
    }

    override fun getCurrentFragment(): PageFragment? {
        if( supportFragmentManager.fragments.isEmpty() ) {

            return null
        }
        val fragment = supportFragmentManager.fragments.last()
        var pageFragment = fragment as? PageFragment
        if(pageFragment == null) pageFragment =  getCurrentPageFragment()
        return pageFragment
    }

    override fun getPageAreaSize():Pair<Float,Float> {
        return Pair(pageArea.width.toFloat(),pageArea.height.toFloat())
    }


    private fun clearHistory(id:T?){
        if(id == null) {
            historys.clear()
            return
        }
        var peek:T? = null
        do {
            if(peek != null) historys.pop()
            peek = try {
                historys.peek().first
            }catch (e:EmptyStackException){
                null
            }
        } while (id != peek  && !historys.isEmpty())

    }

    @CallSuper
    override fun onClearPageHistory(id:T?) {
        clearHistory(id)
    }

    override fun onBack(id:T) {
        clearHistory(id)
        if( historys.isEmpty() ) pagePresenter.goHome()
        else onBackPressed()
    }

    override fun onBack() {
        onBackPressed()
    }
    override fun onBackPressed() {
        if(popups.isNotEmpty()){
            val last = popups.last()
            val lastPopup = supportFragmentManager.findFragmentByTag(last.toString()) as? PageFragment
            lastPopup?.let { if(!it.isBackAble()) return }
            popups.remove(last)
            onClosePopup(last)
            return
        }
        val currentFragment = getCurrentFragment()
        currentFragment?.let{ if(!it.isBackAble()) return }
        currentPage?.let { if( pagePresenter.model.isHome(it) )  onExitAction() else onBackPressedAction() }
    }

    private var finalExitActionTime:Long = 0L
    protected open fun resetBackPressedAction() { finalExitActionTime = 0L }
    protected open fun onExitAction() {
        val cTime =  Date().time
        if( abs(cTime - finalExitActionTime) < 3000L ) { finish() }
        else {
            finalExitActionTime = cTime
            Toast.makeText(this,getPageExitMsg(),Toast.LENGTH_SHORT).show()
        }
    }

    override fun onFinish() {
        finish()
    }

    protected open fun onBackPressedAction() {
        var backPage:Pair< T, Map< String, Any >? >? = null
        if( historys.isEmpty()) {
            if(currentPage == null){
                onExitAction()
                return
            }
            else if( pagePresenter.model.isHome(currentPage!!)) {
                onExitAction()
                return
            }
        }else {
            backPage = historys.pop()
        }
        if(backPage == null) backPage =  Pair( pagePresenter.model.getHome(0), null)
        pageChange( backPage.first , backPage.second  , false, null, null, true )
    }

    protected  fun clearAllBackStackFragment(){
        val len = supportFragmentManager.backStackEntryCount
        for (i in 1..len) supportFragmentManager.popBackStackImmediate()
    }

    private fun getSharedTransitionName(sharedElement: android.view.View,  transitionName:String):String{
        val name = ViewCompat.getTransitionName(sharedElement)
        if(name == null) ViewCompat.setTransitionName(sharedElement, transitionName)
        return transitionName
    }

    private fun getWillChangePageFragment(id:T, param:Map<String, Any>?, isPopup:Boolean): PageFragment {
        onWillChangePageFragment(id, param, isPopup)
        val isBackStack = pagePresenter.model.isBackStack(id)
        if( isBackStack ) {
            val backStackFragment = supportFragmentManager.findFragmentByTag( id.toString() ) as? PageFragment
            backStackFragment?.let {
                param?.let { backStackFragment.setParam(it) }
                return backStackFragment
            }
        }
        val newFragment = if( isPopup ) getPopupByID(id)  else getPageByID(id)
        newFragment.pageID = id
        newFragment.isPopup = isPopup
        param?.let { newFragment.setParam(it) }
        return newFragment
    }

    protected open fun isChangePageAble(id:T, param:Map<String, Any>?, isPopup:Boolean):Boolean { return true }
    protected open fun onWillChangePageFragment(id:T, param:Map<String, Any>?, isPopup:Boolean) {}
    protected open fun isChangedCategory(prevId:T?, currentId:T?):Boolean = false

    abstract fun getPageByID(id:T): PageFragment
    final override fun onPageStart(id:T) { pageChange(id, null, true) }
    final override fun onPageChange(id:T, param:Map<String, Any>?, sharedElement: android.view.View?,  transitionName:String?) {
        pageChange(id, param, false, sharedElement,  transitionName )
    }

    private fun pageChange(id:T, param:Map<String, Any>? , isStart:Boolean = false, sharedElement: android.view.View? = null, transitionName:String? = null, isBack:Boolean = false) {
        if( !isChangePageAble(id, param, false) ) return
        if(currentPage == id) {
            if(param == null){
                getCurrentPageFragment()?.onPageReload()
                return
            }else {
                val currentValues = currentPageParam?.values?.map { it.toString() }
                val values = param.values.map { it.toString() }
                if(currentValues == values){
                    getCurrentPageFragment()?.onPageReload()
                    return
                }
            }
        }
        onCloseAllPopup()
        resetBackPressedAction()
        val willChangePage = getWillChangePageFragment(id, param, false)
        if(isChangedCategory(currentPage, id)) willChangePage.onCategoryChanged(currentPage)
        willChangePage.setOnPageEvent( this )
        val transaction = supportFragmentManager.beginTransaction()
        if(isStart) {
            transaction.setCustomAnimations(getPageStart(),getPageOut(false))
        } else {
            if(sharedElement == null) {
                var isReverse = isBack
                var currentPos = 9999
                val pos = id as? PagePosition
                pos?.let { currentPos = it.position }
                currentPage?.let {
                    val prevPos = it as? PagePosition
                    prevPos?.let { pp -> isReverse = pp.position > currentPos }
                }
                transaction.setCustomAnimations(getPageIn(isReverse),getPageOut(isReverse))
            }else {
                transaction.setReorderingAllowed(true)
                transitionName?.let { transaction.addSharedElement(sharedElement, getSharedTransitionName(sharedElement,it)) }
                willChangePage.sharedElementEnterTransition = getSharedChange()
            }
        }
        currentPage?.let { if( pagePresenter.model.isBackStack(it) ) transaction.addToBackStack(it.toString()) }
        transaction.replace(getPageAreaId(), willChangePage, id.toString())
        transaction.commit()
        if( !isBack ) {
            currentPage?.let {
                if( pagePresenter.model.isHistory(it) ) historys.push(Pair(it, currentPageParam))
            }
        }

        currentPageParam = param
        currentPage = id
    }

    abstract fun getPopupByID(id:T): PageFragment
    private var currentAddedPopup:T? = null
    private var finalOpenPopupTime:Long = 0L
    final override fun onOpenPopup(id:T, param:Map<String, Any>?, sharedElement: android.view.View?, transitionName:String?) {

        if( !isChangePageAble(id, param, true) ) return
        val cTime =  Date().time
        if( currentAddedPopup == id && (abs(cTime - finalOpenPopupTime) < 500 ) ) return
        currentAddedPopup = id
        finalOpenPopupTime = cTime
        resetBackPressedAction()
        val popup = getWillChangePageFragment(id, param, true)
        popup.setOnPageEvent( this )
        val transaction = supportFragmentManager.beginTransaction()
        if(sharedElement == null) {
            transaction.setCustomAnimations(getPopupIn(), getPopupOut())
        }else {
            transaction.setReorderingAllowed(true)
            transitionName?.let { transaction.addSharedElement(sharedElement, getSharedTransitionName(sharedElement,it)) }
            popup.sharedElementEnterTransition = getSharedChange()
            getCurrentPageFragment()?.let { transaction.hide(it) }
        }
        transaction.add(getPageAreaId(), popup, id.toString())
        transaction.commit()
        if(sharedElement != null) {
            getCurrentPageFragment()?.let { supportFragmentManager.beginTransaction().show(it).commit()}
        }
        popups.add(id)
    }

    override fun onTransactionCompleted(id: Any?) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        getCurrentFragment()?.onActivityResult(requestCode, resultCode, data)
        popups.forEach { this.getPageFragment(it)?.onActivityResult(requestCode, resultCode, data) }
    }

    override fun onAddedPage(id:Any?){
        getCurrentPageFragment()?.onPageAdded(id)
        popups.forEach { this.getPageFragment(it)?.onPageAdded(id) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onRemovedPage(id:Any?){
        getCurrentPageFragment()?.onPageRemoved(id)
        popups.forEach { this.getPageFragment(it)?.onPageRemoved(id) }
        val currentFragment = getCurrentFragment()
        currentFragment?.let { cf->
            val pageID = cf.pageID as? T
            pageID?.let { onWillChangePageFragment(it, null , cf.isPopup) }
        }
    }

    override fun onEvent(id:Any?, type:String, data:Any?){
        getCurrentPageFragment()?.onPageEvent(id, type, data)
        popups.forEach { this.getPageFragment(it)?.onPageEvent(id, type, data) }
    }


    final override fun onClosePopup(id:T,isAni:Boolean) {
        val fragment = supportFragmentManager.findFragmentByTag(id.toString())
        fragment?.let { closePopup(it, null, isAni) }
        popups.remove(id)
    }

    @CallSuper
    override fun onCloseAllPopup(isAni:Boolean) {
        val transaction = supportFragmentManager.beginTransaction()
        popups.forEach { p ->
            getPageFragment(p)?.let {f->
                if( pagePresenter.model.isBackStack(p) ) transaction.addToBackStack(p.toString() )
                if(isAni) transaction.setCustomAnimations(getPopupIn(), getPopupOut())
                transaction.remove(f)
            }
        }
        transaction.commitNow()
        popups.clear()
    }

    private fun closePopup(pop: Fragment, id:T? = null, isAni:Boolean = true){
        val transaction = supportFragmentManager.beginTransaction()
        id?.let { if( pagePresenter.model.isBackStack(it) )  transaction.addToBackStack(it.toString()) }
        if(isAni) transaction.setCustomAnimations(getPopupIn(), getPopupOut())
        transaction.remove(pop).commitNow()
    }

    fun goStore(){
        val appPackageName = packageName // getPackageName() from Context or Activity object
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
        } catch (anfe: android.content.ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
        }

    }


}