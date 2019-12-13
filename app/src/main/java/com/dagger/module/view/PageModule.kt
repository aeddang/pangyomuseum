package com.dagger.module.view

import android.content.Context
import com.dagger.PageScope
import com.skeleton.module.ImageFactory
import com.enoughmedia.pangyomuseum.store.Repository
import com.skeleton.module.ViewModelFactory
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class PageModule {
    @Provides
    @PageScope
    fun provideViewModelFactory(repository: Repository): ViewModelFactory = ViewModelFactory(repository)

    @Provides
    @PageScope
    fun provideImageFactory(@Named("appContext") ctx: Context): ImageFactory = ImageFactory(ctx)
}