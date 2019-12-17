package com.lib.page

class PagePresenter<T>(var view: View<T>?, internal val model: Model<T>): Presenter<T> {

    override var activity: Activity<T>? = null
    companion object {
        internal const val TAG = "PagePresenter"
        var currentInstance:Any? = null; private set
        @Suppress("UNCHECKED_CAST")
        fun <T> getInstance(): Presenter<T> {
            if(currentInstance == null) return PagePresenter(null, PageModel())
            return currentInstance as PagePresenter<T>
        }
    }

    fun onDestroy() {
        model.onDestroy()
        view = null
        activity = null
        currentInstance = null
    }

    var isNavigationShow = false
        internal set (newValue) { field = newValue }

    init {
        currentInstance = this
    }

    override fun toggleNavigation() {
        if( isNavigationShow ) hideNavigation() else showNavigation()
    }

    override fun showNavigation() {
        isNavigationShow = true
        view?.onShowNavigation()
    }

    override fun hideNavigation() {
        isNavigationShow = false
        view?.onHideNavigation()
    }
    override fun goHome(idx:Int){
        pageChange(model.getHome(idx))
    }
    override fun goBack(id:T){
        view?.onBack(id)
    }
    override fun goBack(){
        view?.onBack()
    }

    override fun clearPageHistory(id:T?): Presenter<T> {
        view?.onClearPageHistory(id)
        return this
    }

    override fun closeAllPopup(isAni:Boolean): Presenter<T> {
        view?.onCloseAllPopup(isAni)
        return this
    }

    override fun closePopup(id:T, isAni:Boolean): Presenter<T> {
        view?.onClosePopup(id, isAni)
        return this
    }


    override fun openPopup(id:T, param:Map<String, Any?>?, sharedElement:android.view.View?, transitionName:String?): Presenter<T> {
        view?.onOpenPopup(id, param, sharedElement, transitionName)
        return this
    }

    override fun pageStart(id:T): Presenter<T> {
        view?.onPageStart(id)
        return this
    }

    override fun pageChange(id:T, param:Map<String, Any?>?, sharedElement:android.view.View?, transitionName:String?): Presenter<T> {
        view?.onPageChange(id, param, sharedElement, transitionName)
        return this
    }

    override fun hasPermissions( permissions: Array<String> ): Boolean {
        view?.let { v ->
            v.hasPermissions( permissions )?.let { return it.first }
        }
        return false
    }
    override fun requestPermission( permissions: Array<out String>, requester: PageRequestPermission){
        view?.requestPermission(permissions, requester)
    }
    override fun loading(isRock: Boolean): Presenter<T> {
        view?.loading(isRock)
        return this
    }
    override fun loaded(): Presenter<T> {
        view?.loaded()
        return this
    }

    override fun finish() {
        view?.onFinish()
    }

}




