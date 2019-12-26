package com.enoughmedia.pangyomuseum.page.popup

import android.os.Bundle
import com.enoughmedia.pangyomuseum.PageID
import com.enoughmedia.pangyomuseum.R
import com.enoughmedia.pangyomuseum.store.Museum
import com.enoughmedia.pangyomuseum.store.SettingPreference
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.checkedChanges
import com.lib.page.PagePresenter
import com.skeleton.rx.RxPageFragment
import com.skeleton.view.alert.AlertDelegate
import com.skeleton.view.alert.CustomAlert
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.popup_setting.*
import javax.inject.Inject


class PopupSetting  : RxPageFragment() {
    private val appTag = javaClass.simpleName

    @Inject
    lateinit var setting: SettingPreference
    @Inject
    lateinit var museum: Museum

    override fun getLayoutResId() = R.layout.popup_setting



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)


    }

    override fun onCreatedView() {
        super.onCreatedView()
        switchBecon.isChecked = setting.getUseBecon()
        switchSound.isChecked = setting.getUseSound()
    }

    override fun onDestroyedView() {
        super.onDestroyedView()
    }

    override fun onSubscribe() {
        super.onSubscribe()
        btnClose.clicks().subscribe {
            PagePresenter.getInstance<PageID>().goBack()
        }.apply { disposables.add(this) }

        switchBecon.checkedChanges().subscribe {
            setting.putUseBecon(it)
        }.apply { disposables.add(this) }
        switchSound.checkedChanges().subscribe {
            setting.putUseSound(it)
        }.apply { disposables.add(this) }

        btnReset.clicks().subscribe {
            context ?: return@subscribe
            CustomAlert.makeAlert(context!!,  R.string.popup_setting_reset_check, object: AlertDelegate {
                override fun onPositiveClicked() { museum.resetMuseum() }
            }).show()
        }.apply { disposables.add(this) }
    }

}