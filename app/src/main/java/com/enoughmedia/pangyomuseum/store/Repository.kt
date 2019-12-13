package com.enoughmedia.pangyomuseum.store
import android.content.Context

class Repository(
    val ctx: Context,
    val setting:SettingPreference,
    val museum:Museum
) {

    private val appTag = "Repository"

    init {
        museum.setup()
    }


}