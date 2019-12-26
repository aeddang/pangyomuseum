package com.enoughmedia.pangyomuseum.store

import android.content.Context
import com.enoughmedia.pangyomuseum.PreferenceName
import com.lib.module.CachedPreference

class SettingPreference(context: Context) : CachedPreference(context, PreferenceName.SETTING) {
    companion object {
        private const val VIEW_GUIDE = "viewGuide"
        private const val VIEW_GESTURE = "viewGesture"
        private const val VIEW_SCAN_GUIDE = "viewScanGuide"
        private const val VIEW_MAP_GUIDE = "viewMapGuide"

        private const val IS_FIND = "isFind"
        private const val USE_BEACON = "useBeacon"
        private const val USE_SOUND = "useSound"
    }

    fun putViewGuide(bool: Boolean) = put(VIEW_GUIDE, bool)
    fun getViewGuide(): Boolean = get(VIEW_GUIDE, false) as Boolean

    fun putViewGesture(bool: Boolean) = put(VIEW_GESTURE, bool)
    fun getViewGesture(): Boolean = get(VIEW_GESTURE, false) as Boolean

    fun putViewScanGuide(bool: Boolean) = put(VIEW_SCAN_GUIDE, bool)
    fun getViewScanGuide(): Boolean = get(VIEW_SCAN_GUIDE, false) as Boolean

    fun putViewMapGuide(bool: Boolean) = put(VIEW_MAP_GUIDE, bool)
    fun getViewMapGuide(): Boolean = get(VIEW_MAP_GUIDE, false) as Boolean

    fun putIsFind(bool: Boolean, id:String) = put("$IS_FIND$id", bool)
    fun getIsFind(id:String): Boolean = get("$IS_FIND$id", false) as Boolean

    fun putUseBecon(bool: Boolean) = put(USE_BEACON, bool)
    fun getUseBecon(): Boolean = get(USE_BEACON, true) as Boolean

    fun putUseSound(bool: Boolean) = put(USE_SOUND, bool)
    fun getUseSound(): Boolean = get(USE_SOUND, true) as Boolean
}