package com.dagger

import javax.inject.Qualifier
import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ActivityScope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class PageScope

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ComponentScope(
    val value: String = ""
)

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class UserScope

