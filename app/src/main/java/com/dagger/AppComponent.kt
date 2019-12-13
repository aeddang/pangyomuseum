package com.dagger


import com.dagger.module.app.*
import com.enoughmedia.pangyomuseum.App
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton


@Singleton
@Component(
    modules = [
        AppModule::class,
        BaseModule::class,
        StoreModule::class,
        RepositoryModule::class,
        PreferenceModule::class,
        NetworkModule::class,
        AndroidBindingModule::class,
        AndroidSupportInjectionModule::class
    ]
)
interface AppComponent : AndroidInjector<App> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<App>()
}