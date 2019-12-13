package com.enoughmedia.pangyomuseum.page

import com.enoughmedia.pangyomuseum.PageID
import com.enoughmedia.pangyomuseum.R
import com.lib.page.PagePresenter
import com.skeleton.rx.RxPageFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit


class PageIntro  : RxPageFragment() {

    private val appTag = javaClass.simpleName
    override fun getLayoutResId() = R.layout.page_intro


    override fun onCreatedView() {
        super.onCreatedView()
        Observable.interval(1500, TimeUnit.MILLISECONDS)
            .take(1)
            .observeOn(AndroidSchedulers.mainThread()).subscribe {
                PagePresenter.getInstance<PageID>().pageChange(PageID.MAP)
            }.apply { disposables.add(this) }

    }

    override fun onDestroy() {
        super.onDestroy()
    }



}