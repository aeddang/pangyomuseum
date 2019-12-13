package com.dagger.module.app

import android.content.Context
import com.enoughmedia.pangyomuseum.store.Museum
import com.enoughmedia.pangyomuseum.store.Repository
import com.enoughmedia.pangyomuseum.store.SettingPreference
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideRepository(@Named("appContext") ctx: Context,
                          setting:SettingPreference,
                          museum:Museum

    ): Repository = Repository(ctx, setting, museum)
}