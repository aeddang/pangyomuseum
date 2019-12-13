package com.dagger.module.app

import android.app.Application
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import retrofit2.CallAdapter
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import javax.inject.Singleton


@Module
open class NetworkModule {

    private val appTag = javaClass.simpleName

    private val CONNECT_TIMEOUT: Long = 15
    private val WRITE_TIMEOUT: Long = 15
    private val READ_TIMEOUT: Long = 15

    @Provides
    @Singleton
    fun provideCache(application: Application): Cache {
        val cacheSize = 10 * 1024 * 1024 // 10MB
        return Cache(application.cacheDir, cacheSize.toLong())
    }

    @Provides
    @Singleton
    fun provideCallAdapterFactory(): CallAdapter.Factory = RxJava2CallAdapterFactory.createAsync()


    /*
    @Provides
    @Named(AppConst.HOMET_KAKAO_VX_API)
    fun provideOkHttpClientVX(cache: Cache, interceptor: VXInterceptor): OkHttpClient {
        val logger = HttpLoggingInterceptor(
            HttpLoggingInterceptor.Logger { message ->
                var parseMessage = message
                Log.d(appTag, parseMessage)
                if (parseMessage.contains("END")) {
                    Log.d(appTag, "\n")
                    parseMessage += "\n"
                }
            })
        logger.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .cache(cache)
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .cookieJar(JavaNetCookieJar(CookieManager(null, CookiePolicy.ACCEPT_ALL)))
            .addInterceptor(interceptor)
            .addInterceptor(logger)

            .build()
    }

    @Provides
    fun provideAccountApi(@Named(AppConst.HOMET_KAKAO_VX_API) retrofit: Retrofit): AccountApi {
        return retrofit.create(AccountApi::class.java)
    }
    */

}