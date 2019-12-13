package com.dagger.module.app

import android.app.Application
import android.content.Context

import dagger.Module
import dagger.Provides
import javax.inject.Named


@Module
class BaseModule {

    @Provides
    @Named("appContext")
    fun provideContext(app: Application): Context
            = app.applicationContext



}