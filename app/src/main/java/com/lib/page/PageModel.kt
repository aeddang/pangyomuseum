package com.lib.page


class PageModel<T> : Model<T> {
    internal lateinit var homes: Array<T>
    internal var backStacks: Array<T>? = null
    internal var disableHistorys: Array<T>? = null
    override fun onDestroy() {
    }

    override fun isHome(id:T):Boolean {
        val idx  = homes.indexOf(id)
        return idx != -1
    }

    override fun getHome(idx:Int):T {
        return homes[idx]
    }


    override fun isBackStack(id:T):Boolean {
        if( backStacks == null) return false
        val idx  = backStacks!!.indexOf(id)
        return idx != -1
    }

    override fun isHistory(id:T):Boolean {
        if( disableHistorys == null) return true
        val idx  = disableHistorys!!.indexOf(id)
        return idx == -1
    }

}