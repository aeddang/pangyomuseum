package com.dagger.module.app

import com.enoughmedia.pangyomuseum.store.Museum

import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class StoreModule {

    @Provides
    @Singleton
    fun provideMuseum(): Museum = Museum()



}