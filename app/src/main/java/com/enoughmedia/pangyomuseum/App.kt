package com.enoughmedia.pangyomuseum

import androidx.fragment.app.Fragment
import com.dagger.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import org.altbeacon.beacon.powersave.BackgroundPowerSaver
import javax.inject.Inject


class App : DaggerApplication(), HasSupportFragmentInjector {

    private val appTag = javaClass.simpleName

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    override fun applicationInjector(): AndroidInjector<out App> {
        return DaggerAppComponent.builder().create(this)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }


    private var backgroundPowerSaver: BackgroundPowerSaver? = null
    override fun onCreate() {
        super.onCreate()
        backgroundPowerSaver = BackgroundPowerSaver(this)
    }
}