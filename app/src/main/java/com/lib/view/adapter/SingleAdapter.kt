package com.lib.view.adapter

import android.view.ViewGroup
import androidx.annotation.CallSuper
import com.skeleton.view.item.ListItem

abstract class SingleAdapter<T>(isViewMore:Boolean = false, pageSize:Int = -1): BaseAdapter<T>(isViewMore, pageSize) {
    abstract fun getListCell(parent: ViewGroup): ListItem
    @CallSuper
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(getListCell(parent))
    }
}