package com.skeleton.view.alert

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast

object CustomToast {

    @SuppressLint("InflateParams")

    fun makeToast(context: Context, body: Int, duration: Int): Toast {
        return makeToast(context, context.getString(body), duration)
    }

    @SuppressLint("ShowToast")
    fun makeToast(context: Context, body: String, duration: Int): Toast {

        val toast = Toast.makeText(context, body, duration)
        val v = toast.view.findViewById(android.R.id.message) as TextView
        v.gravity = Gravity.CENTER

        return toast
        /*
        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater.inflate(R.layout.ui_toast, null)
        v.message.text = body
        val toast = Toast(context)
        toast.setGravity(Gravity.BOTTOM, 0, 0)
        toast.view = v
        toast.duration = duration
        return toast
        */
    }
}