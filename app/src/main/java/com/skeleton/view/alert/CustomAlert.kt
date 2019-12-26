package com.skeleton.view.alert

import android.app.AlertDialog
import android.content.Context
import com.enoughmedia.pangyomuseum.R


object CustomAlert {
    fun makeAlert(context: Context, title: Int, message: Int): AlertDialog.Builder {
        return makeAlert(
            context,
            context.getString(title),
            context.getString(message)
        )
    }

    fun makeAlert(context: Context, title: Int, message: String?): AlertDialog.Builder {
        return makeAlert(
            context,
            context.getString(title),
            message
        )
    }

    fun makeAlert(context: Context, title:String?, message: Int): AlertDialog.Builder {
        return makeAlert(
            context,
            title,
            context.getString(message)
        )
    }

    fun makeAlert(context: Context,  message: Int, positiveButton:String, delegate: AlertDelegate): AlertDialog.Builder {
        return makeAlert(
            context,
            null,
            context.getString(message),positiveButton,null,null, delegate
        )
    }

    fun makeAlert(context: Context,  message: Int, delegate: AlertDelegate): AlertDialog.Builder {
        return makeAlert(
            context, message , R.string.btn_confirm, R.string.btn_cancel, delegate
        )
    }

    fun makeAlert(context: Context,  message: Int, positiveButton:Int, negativeButton: Int, delegate: AlertDelegate): AlertDialog.Builder {
        return makeAlert(
            context,
            null,
            context.getString(message),context.getString(positiveButton),context.getString(negativeButton),null, delegate
        )
    }

    fun makeAlert(context: Context, title:String?, message: String?, positiveButton:String?=null, negativeButton: String?=null, neutralButton: String?=null, delegate: AlertDelegate?=null): AlertDialog.Builder{
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        positiveButton?.let{
            builder.setPositiveButton(it) { d, _ ->
                d.dismiss()
                delegate?.onPositiveClicked()
            }
        }
        negativeButton?.let{
            builder.setNegativeButton(it) { d, _ ->
                d.dismiss()
                delegate?.onNegativeClicked()
            }
        }
        neutralButton?.let{
            builder.setNeutralButton(it) { d, _ ->
                d.dismiss()
                delegate?.onNeutralClicked()
            }
        }
        return builder
    }
}

interface AlertDelegate {
    fun onPositiveClicked() {}
    fun onNeutralClicked() {}
    fun onNegativeClicked() {}
}