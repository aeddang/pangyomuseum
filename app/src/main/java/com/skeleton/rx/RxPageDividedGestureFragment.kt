package com.skeleton.rx

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import com.lib.page.PageConst
import com.lib.page.PageDividedGestureFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

abstract class RxPageDividedGestureFragment : PageDividedGestureFragment(), Rx {

    protected val disposables by lazy { CompositeDisposable() }
    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onSubscribe()
        Observable.interval(PageConst.TRANSACTION_DELAY, TimeUnit.MILLISECONDS)
            .take(1)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { transactionCompleted() }.apply { disposables.add(this) }
    }

    @CallSuper
    override fun onDestroyedView() {
        super.onDestroyedView()
        disposables.clear()
    }
}