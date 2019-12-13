package com.enoughmedia.pangyomuseum.page.popup


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import com.enoughmedia.pangyomuseum.PageID
import com.enoughmedia.pangyomuseum.R
import com.enoughmedia.pangyomuseum.store.SettingPreference
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.checkedChanges
import com.lib.page.PagePresenter
import com.skeleton.rx.RxPageFragment
import com.skeleton.view.item.ListItem
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.item_gesture.view.*
import kotlinx.android.synthetic.main.popup_gesture.*
import javax.inject.Inject


class PopupGesture  : RxPageFragment() {

    private val appTag = javaClass.simpleName
    override fun getLayoutResId() = R.layout.popup_gesture


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
        btnCheck.isChecked = true
        setting.putViewGesture(true)
    }

    override fun onDestroyedView() {
        super.onDestroyedView()
    }

    override fun onSubscribe() {
        super.onSubscribe()
        btnClose.clicks().subscribe {
            PagePresenter.getInstance<PageID>().closePopup(PageID.POPUP_GESTURE)
        }.apply { disposables.add(this) }

        btnCheck.checkedChanges().subscribe {
            setting.putViewGesture(it)
        }.apply { disposables.add(this) }

    }

}

class ItemGesture : ListItem {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context,attrs)

    override fun getLayoutResId(): Int  = R.layout.item_gesture

    @SuppressLint("SetTextI18n")
    override fun setData(data: Any?, idx:Int){

        val packageID = context.packageName
        val resImgID = context.resources.getIdentifier("${packageID}:drawable/icon_gesture${(idx)}", null, null)
        val resTextID = context.resources.getIdentifier("${packageID}:string/popup_gesture_text${(idx)}", null, null)
        image.setImageResource(resImgID)
        text.setText(resTextID)
    }


}