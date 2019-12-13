package com.dagger


import com.dagger.module.view.ActivityModule
import com.dagger.module.view.MainActivityModule
import com.dagger.module.view.PageModule
import com.enoughmedia.pangyomuseum.MainActivity
import com.enoughmedia.pangyomuseum.page.PageMain
import com.enoughmedia.pangyomuseum.page.PageMap
import com.enoughmedia.pangyomuseum.page.PageMounds
import com.enoughmedia.pangyomuseum.page.popup.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class AndroidBindingModule {


    /**
     * Main Activity
     */

    @ActivityScope
    @ContributesAndroidInjector(modules = [MainActivityModule::class, ActivityModule::class])
    internal abstract fun bindMainActivity(): MainActivity


    @PageScope
    @ContributesAndroidInjector(modules = [PageModule::class])
    internal abstract fun bindPageMain(): PageMain

    @PageScope
    @ContributesAndroidInjector(modules = [PageModule::class])
    internal abstract fun bindPageMap(): PageMap

    @PageScope
    @ContributesAndroidInjector(modules = [PageModule::class])
    internal abstract fun bindPageMounds(): PageMounds

    @PageScope
    @ContributesAndroidInjector(modules = [PageModule::class])
    internal abstract fun bindPopupAR(): PopupAR

    @PageScope
    @ContributesAndroidInjector(modules = [PageModule::class])
    internal abstract fun bindPopupVR(): PopupVR

    @PageScope
    @ContributesAndroidInjector(modules = [PageModule::class])
    internal abstract fun bindPopupScan(): PopupScan

    @PageScope
    @ContributesAndroidInjector(modules = [PageModule::class])
    internal abstract fun bindPopupGuide(): PopupGuide

    @PageScope
    @ContributesAndroidInjector(modules = [PageModule::class])
    internal abstract fun bindPopupGesture(): PopupGesture

}
