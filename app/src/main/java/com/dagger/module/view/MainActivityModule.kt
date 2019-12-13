package com.dagger.module.view

import android.app.Activity
import com.dagger.ActivityScope
import com.enoughmedia.pangyomuseum.MainActivity
import dagger.Binds
import dagger.Module

@Module
abstract class MainActivityModule {
    @Binds
    @ActivityScope
    internal abstract fun activity(mainActivity: MainActivity): Activity
}