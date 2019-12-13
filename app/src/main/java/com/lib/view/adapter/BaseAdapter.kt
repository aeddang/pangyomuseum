package com.lib.view.adapter

import android.os.Handler
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import com.lib.model.InfinityPaginationData
import com.lib.util.Log
import com.skeleton.view.item.ListItem


abstract class BaseAdapter<T>(private val isViewMore:Boolean = false, pageSize:Int = -1) : RecyclerView.Adapter<BaseAdapter.ViewHolder>() {
    class ViewHolder(val item: ListItem) : RecyclerView.ViewHolder(item)

    var delegate: Delegate? = null
    private var viewEndHandler: Handler = Handler()
    private var viewEndRunnable: Runnable = Runnable {
        onViewEnd(paginationData.currentPage, paginationData.pageSize)
        delegate?.viewEnd(paginationData.currentPage, paginationData.pageSize)
    }

    private var viewStartHandler: Handler = Handler()
    private var viewStartRunnable: Runnable = Runnable {
        onViewStart(paginationData.currentPage, paginationData.pageSize)
        delegate?.viewStart(0, paginationData.pageSize)
    }
    private var total = 0
    private var isBusy = false
    private var isInit = false
    private var paginationData:InfinityPaginationData<T> = InfinityPaginationData(pageSize)

    @CallSuper
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        delegate = null
        viewEndHandler.removeCallbacks(viewEndRunnable)
        viewStartHandler.removeCallbacks(viewStartRunnable)
    }

    fun addData(data:T) {
        paginationData.add(data)
        notifyDataSetChanged()
    }

    fun setDataArray(data:Array<T>): RecyclerView.Adapter<ViewHolder> {
        isInit = true
        paginationData.reset()
        paginationData.addAll(data)
        notifyDataSetChanged()
        return this
    }

    fun addDataArray(data:Array<T>) {
        val idx = paginationData.data.size
        paginationData.addAll(data)
        notifyItemRangeInserted(idx, data.size)
    }

    fun insertData(data:T, idx:Int = -1) {
        val position = if (idx == -1) paginationData.data.size else idx
        if(position == -1 || position >= total) return
        paginationData.data.add(position, data)
        notifyItemInserted(position)
    }

    fun insertDataArray(data:Array<T>, idx:Int = -1) {
        val position = if (idx == -1) paginationData.data.size else idx
        if(position == -1 || position >= total) return
        Log.d("ADT", "insertDataArray $position")
        paginationData.data.addAll(position, data.toList())
        notifyItemRangeInserted(position, data.size)
    }

    fun updateData(data:T, idx:Int) {
        if(idx == -1 || idx >= total) return
        paginationData.data[idx] = data
        notifyItemChanged(idx)
    }

    fun removeData(data:T) {
        val position = paginationData.data.indexOf(data)
        removeData(position)
    }

    fun removeData(idx:Int) {
        if(idx == -1 || idx >= total) return
        paginationData.data.removeAt(idx)
        notifyItemRemoved(idx)
    }

    fun removeAllData() {
        paginationData.reset()
        notifyDataSetChanged()
    }

    fun getData(position: Int) = paginationData.data[position]

    open protected fun onViewStart(page:Int, size:Int){isBusy = false}
    open protected fun onViewEnd(page:Int, size:Int){isBusy = false}
    @CallSuper
    open fun viewEndComplete(dataArray:Array<T>) {
        addDataArray(dataArray)
        isBusy = false
    }

    @CallSuper
    open fun viewStartComplete(dataArray:Array<T>) {
        insertDataArray(dataArray, 0)
        isBusy = false
    }

    override fun getItemCount():Int {
        total = paginationData.data.size
        return total
    }


    @CallSuper
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item.setData(paginationData.data[position], position)
        Log.d("ADT", "position $position")
        if(position == total-1 && isViewMore && paginationData.isPageable && !isBusy) {
            isBusy = true
            paginationData.next()
            viewEndHandler.post(viewEndRunnable)

        }else if(position == 0 && isViewMore && paginationData.isPageable && !isBusy) {
            //if(isInit){
               // isInit = false
            //}else{
                isBusy = true
                paginationData.next()
                viewStartHandler.post(viewStartRunnable)
            //}

        }
    }

    interface Delegate {
        fun viewEnd(page:Int, size:Int){}
        fun viewStart(page:Int, size:Int){}
    }
}