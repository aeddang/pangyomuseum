package com.enoughmedia.pangyomuseum.page

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.enoughmedia.pangyomuseum.AppConst
import com.enoughmedia.pangyomuseum.PageID
import com.enoughmedia.pangyomuseum.PageParam
import com.enoughmedia.pangyomuseum.R
import com.enoughmedia.pangyomuseum.model.Antiquity
import com.enoughmedia.pangyomuseum.model.MoundsID
import com.enoughmedia.pangyomuseum.page.popup.PopupAR
import com.enoughmedia.pangyomuseum.page.viewmodel.PageViewModel
import com.jakewharton.rxbinding3.view.clicks
import com.lib.page.PageFragment
import com.lib.page.PagePresenter
import com.lib.view.adapter.BaseViewPagerAdapter
import com.skeleton.module.ViewModelFactory
import com.skeleton.rx.Rx
import com.skeleton.rx.RxFrameLayout
import com.skeleton.rx.RxPageFragment
import com.skeleton.view.item.ListItem
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposables
import kotlinx.android.synthetic.main.item_antiquity.view.*
import kotlinx.android.synthetic.main.page_book.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.floor
import kotlin.math.roundToInt


class PageBook  : RxPageFragment() {

    private val appTag = javaClass.simpleName
    override fun getLayoutResId() = R.layout.page_book
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel:PageViewModel
    private var moundsId:MoundsID? = null
    private val adapter = PagerAdapter()
    private var pageNum:Int = 8
    private var find:Int = 0
        set(value) {
            field = value
            textFind.text = String.format("%02d", value);
        }

    private var search:Int = 0
        set(value) {
            field = value
            textSearch.text = String.format("%02d", value);
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(PageViewModel::class.java)
    }

    override fun setParam(param: Map<String, Any?>): PageFragment {
        moundsId = param[PageParam.MOUNDS_ID] as? String ?: "0"

        return super.setParam(param)
    }

    override fun onCreatedView() {
        super.onCreatedView()

        val pageSize = PagePresenter.getInstance<PageID>().activity?.getPageAreaSize()
        pageSize?.let { size->
            val density = viewModel.repo.ctx.resources.displayMetrics.density
            val offset = 65.0 * density
            var v:Int = floor((size.second - offset) / (160.0 * density)).toInt()
            var h:Int = floor((size.first - offset) / (176.0 * density)).toInt()
            if(v>2) v = 2
            if(h>4) h = 4
            pageNum = v * h
        }

        val allDatas = viewModel.repo.museum.allAntiquities()
        val size = allDatas.size
        find = viewModel.repo.museum.getFindAntiquities().size
        search = size - find

        val groups = ArrayList<List<Antiquity>>()
        var idx = 0
        val line = pageNum
        var end = 0
        do {
            end = idx+line
            if(end > size) end = size
            groups.add( allDatas.slice(IntRange(idx,end-1)) )
            idx += line

        } while (end < size)

        viewPager.adapter = adapter
        adapter.setData(groups.map { it }.toTypedArray())
    }



    override fun onSubscribe() {
        super.onSubscribe()
        btnClose.clicks().subscribe {
            PagePresenter.getInstance<PageID>().goBack()
        }.apply { disposables.add(this) }

        btnPrev.clicks().subscribe {
            viewPager.currentItem --
        }.apply { disposables.add(this) }

        btnNext.clicks().subscribe {
            viewPager.currentItem ++
        }.apply { disposables.add(this) }
    }


    inner class PagerAdapter : BaseViewPagerAdapter<Item, List<Antiquity>>(){
        override fun getPageView(container: ViewGroup, position: Int): Item {
            val item =  Item(context!!)
            pages?.let {
                item.setData(it[position], position)
            }
            return item
        }

    }

    inner class Item(context: Context) : ListItem(context) {
        override fun getLayoutResId(): Int {
            return R.layout.item_antiquities
        }

        override fun setData(data: Any?, idx: Int) {
            val antis = data as? ArrayList<*>?
            for (index in 0..8) {
                val view = findViewById<View>(
                    context.resources.getIdentifier(
                        "antiquity${index}",
                        "id",
                        AppConst.PACKAGE_NAME
                    )
                ) as? ItemAntiquity
                view?.let { v ->
                    if (antis == null) {
                        v.visibility = View.GONE
                    } else if (index >= antis.size) {
                        v.visibility = View.GONE
                    } else {
                        v.setData(antis[index], index)
                    }

                }
            }
        }
    }

}


class ItemAntiquity : RxFrameLayout, Rx {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context,attrs)

    override fun getLayoutResId(): Int  = R.layout.item_antiquity

    private var antiquity:Antiquity? = null
    fun setData(data: Any?, idx:Int){
        antiquity = data as? Antiquity?
        if(antiquity == null) {
            image.visibility = View.GONE
            return
        }
        if(antiquity!!.isFind) {
            image.setImageResource(antiquity!!.imageResource)

        } else {
            image.visibility = View.GONE
        }
        antiquity ?: return
        if(antiquity!!.isFind){
            this.clicks().subscribe {
                val shareImageID = UUID.randomUUID().toString()
                val param = HashMap<String, Any>()
                param[PageParam.ANTIQUITY] = antiquity!!
                //param[PageParam.SHARE_IMAGE_ID] = shareImageID
                PagePresenter.getInstance<PageID>().openPopup(PageID.POPUP_AR, param)

            }.apply { disposables?.add(this) }

        }
    }

    override fun onCreatedView() {

    }


    override fun onDestroyedView() {
        antiquity = null
    }




}

