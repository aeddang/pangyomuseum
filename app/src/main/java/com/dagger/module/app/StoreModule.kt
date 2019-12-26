package com.dagger.module.app

import android.content.Context
import com.enoughmedia.pangyomuseum.store.BeaconController
import com.enoughmedia.pangyomuseum.store.Museum
import com.enoughmedia.pangyomuseum.store.SettingPreference

import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton


@Module
class StoreModule {

    @Provides
    @Singleton
    fun provideMuseum(@Named("appContext") ctx: Context, setting: SettingPreference): Museum = Museum(ctx,setting)

    @Provides
    @Singleton
    fun provideBeaconController(@Named("appContext") ctx: Context, setting: SettingPreference): BeaconController = BeaconController(ctx, setting)

}