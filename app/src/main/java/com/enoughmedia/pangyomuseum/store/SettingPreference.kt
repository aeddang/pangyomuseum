package com.enoughmedia.pangyomuseum.store

import android.content.Context
import com.enoughmedia.pangyomuseum.PreferenceName
import com.lib.module.CachedPreference

class SettingPreference(context: Context) : CachedPreference(context, PreferenceName.SETTING) {
    companion object {
        private const val VIEW_GUIDE = "viewGuide"
        private const val VIEW_GESTURE = "viewGesture"
        private const val VIEW_SCAN_GUIDE = "viewScanGuide"
    }

    fun putViewGuide(bool: Boolean) = put(VIEW_GUIDE, bool)
    fun getViewGuide(): Boolean = get(VIEW_GUIDE, false) as Boolean

    fun putViewGesture(bool: Boolean) = put(VIEW_GESTURE, bool)
    fun getViewGesture(): Boolean = get(VIEW_GESTURE, false) as Boolean

    fun putViewScanGuide(bool: Boolean) = put(VIEW_SCAN_GUIDE, bool)
    fun getViewScanGuide(): Boolean = get(VIEW_SCAN_GUIDE, false) as Boolean
}