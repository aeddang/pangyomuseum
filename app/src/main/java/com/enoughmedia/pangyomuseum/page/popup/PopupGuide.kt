package com.enoughmedia.pangyomuseum.page.popup


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import com.enoughmedia.pangyomuseum.AppConst
import com.enoughmedia.pangyomuseum.PageID
import com.enoughmedia.pangyomuseum.R
import com.enoughmedia.pangyomuseum.store.SettingPreference
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.checkedChanges
import com.lib.page.PagePresenter
import com.skeleton.rx.RxPageFragment
import com.skeleton.view.item.ListItem
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.item_quide.view.*
import kotlinx.android.synthetic.main.popup_quide.*
import javax.inject.Inject


class PopupGuide  : RxPageFragment() {

    private val appTag = javaClass.simpleName
    override fun getLayoutResId() = R.layout.popup_quide


    @Inject
    lateinit var setting: SettingPreference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreatedView() {
        super.onCreatedView()
        step0.setData(null, 0)
        step1.setData(null, 1)
        step2.setData(null, 2)
        step3.setData(null, 3)
        btnCheck.isChecked = true
        setting.putViewGuide(true)
    }

    override fun onDestroyedView() {
        super.onDestroyedView()
    }

    override fun onSubscribe() {
        super.onSubscribe()
        btnClose.clicks().subscribe {
            PagePresenter.getInstance<PageID>().closePopup(PageID.POPUP_GUIDE)
        }.apply { disposables.add(this) }

        btnCheck.checkedChanges().subscribe {
            setting.putViewGuide(it)
        }.apply { disposables.add(this) }

    }

}

class ItemGuide : ListItem {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context,attrs)

    override fun getLayoutResId(): Int  = R.layout.item_quide

    @SuppressLint("SetTextI18n")
    override fun setData(data: Any?, idx:Int){

        num.text = "0${(idx+1)}"
        val packageID = AppConst.PACKAGE_NAME
        val resImgID = context.resources.getIdentifier("${packageID}:drawable/icon_step${(idx)}", null, null)
        val resTextID = context.resources.getIdentifier("${packageID}:string/popup_guide_text${(idx)}", null, null)
        image.setImageResource(resImgID)
        text.setText(resTextID)
    }


}