package com.dagger.module.app

import android.app.Application
import com.enoughmedia.pangyomuseum.store.SettingPreference
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PreferenceModule{

    @Provides
    @Singleton
    fun provideSettingPreference(application: Application): SettingPreference = SettingPreference(application)


}