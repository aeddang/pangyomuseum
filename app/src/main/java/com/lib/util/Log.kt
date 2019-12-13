package com.lib.util

import com.lib.page.PageActivity
import com.enoughmedia.pangyomuseum.BuildConfig

object Log {

    var enable = 0

    fun i(tag: String, vararg objects: Any) {
        if (enable == 1) android.util.Log.i(PageActivity.TAG + tag, toString(*objects))
        else {
            when (BuildConfig.BUILD_TYPE) {
                "debug" -> android.util.Log.i(PageActivity.TAG + tag, toString(*objects))
            }
        }
    }

    fun d(tag: String, vararg objects: Any) {
        if (enable == 1) android.util.Log.d(PageActivity.TAG + tag, toString(*objects))
        else {
            when (BuildConfig.BUILD_TYPE) {
                "debug" -> android.util.Log.d(PageActivity.TAG + tag, toString(*objects))
            }
        }
    }

    fun w(tag: String, vararg objects: Any) {
        android.util.Log.w(PageActivity.TAG + tag, toString(*objects))
    }

    fun e(tag: String, vararg objects: Any) {
        android.util.Log.e(PageActivity.TAG + tag, toString(*objects))
    }

    fun v(tag: String, vararg objects: Any) {
        android.util.Log.v(PageActivity.TAG + tag, toString(*objects))
    }

    private fun toString(vararg objects: Any): String {
        val sb = StringBuilder()
        for (o in objects) {
            sb.append(o)
        }
        return sb.toString()
    }
}