package com.skeleton.module
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.enoughmedia.pangyomuseum.store.Repository


class ViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(Repository::class.java).newInstance(repository)
    }
}