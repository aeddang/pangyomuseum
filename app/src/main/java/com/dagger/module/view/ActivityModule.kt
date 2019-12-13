package com.dagger.module.view

import com.dagger.ActivityScope
import com.enoughmedia.pangyomuseum.store.Repository
import com.skeleton.module.ViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class ActivityModule {
    @Provides
    @ActivityScope
    fun provideViewModelFactory(repository: Repository): ViewModelFactory = ViewModelFactory(repository)

}
