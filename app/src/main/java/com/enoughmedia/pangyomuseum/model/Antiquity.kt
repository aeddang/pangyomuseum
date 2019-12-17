package com.enoughmedia.pangyomuseum.model

import android.content.Context
import com.enoughmedia.pangyomuseum.AppConst
import com.enoughmedia.pangyomuseum.store.SettingPreference


typealias MoundsID = String

data class Mounds(
    val ctx: Context,
    val setting: SettingPreference,
    val id:MoundsID,
    val group:Int,
    val idx:Int
    ){

    var antiquitise = ArrayList<Antiquity>(); private set
    fun addAntiquity(antiquity:Antiquity){
        antiquity.idx = antiquitise.size
        antiquity.group = group
        antiquity.moundsID = id
        antiquitise.add(antiquity)
    }

    val findCode:String
    get() {
        return "qrcode_${id}"
    }
    val panoViewPath:String
        get() {
            return "panoramas/testRoom1_2kStereo.jpg"
        }
    val cardViewPath:String
        get() {
            return "panoramas/testRoom1_2kStereo.jpg"
        }

    val modelResource:Int
        get() {
            return ctx.resources.getIdentifier("${AppConst.PACKAGE_NAME}:raw/mounds${0}", null, null)
        }

    val titleResource:Int
        get() {
            return ctx.resources.getIdentifier("${AppConst.PACKAGE_NAME}:string/data_mounds${id}_title", null, null)
        }
    val descResource:Int
        get() {
            return ctx.resources.getIdentifier("${AppConst.PACKAGE_NAME}:string/data_mounds${id}_desc", null, null)
        }

    val title:String
        get() {
            return ctx.getString(titleResource)
        }
    val desc:String
        get() {
            return ctx.getString(descResource)
        }
}

data class Antiquity(
    val ctx: Context,
    val setting:SettingPreference,
    val id:String
){
    var moundsID:MoundsID = ""; internal set
    var group:Int = 0; internal set
    var idx:Int = 0; internal set
    var isFind:Boolean = false
    get() {
        return setting.getIsFind(id)
    }
    set(value) {
        field = value
        setting.putIsFind(field, id)
    }

    val titleResource:Int
        get() {
            return ctx.resources.getIdentifier("${AppConst.PACKAGE_NAME}:string/data_antiquity${moundsID}_${0}_title", null, null)
        }
    val infoResource:Int
        get() {
            return ctx.resources.getIdentifier("${AppConst.PACKAGE_NAME}:string/data_antiquity${moundsID}_${0}_info", null, null)
        }
    val descResource:Int
        get() {
            return ctx.resources.getIdentifier("${AppConst.PACKAGE_NAME}:string/data_antiquity${moundsID}_${0}_desc", null, null)
        }

    val imageResource:Int
        get() {
            return ctx.resources.getIdentifier("${AppConst.PACKAGE_NAME}:drawable/z_antiquity${0}_${0}", null, null)
        }

    val modelResource:Int
        get() {
            return ctx.resources.getIdentifier("${AppConst.PACKAGE_NAME}:raw/antiquity${0}_${0}", null, null)
        }

    val title:String
        get() {
            return ctx.getString(titleResource)
        }
    val info:String
        get() {
            return ctx.getString(infoResource)
        }
    val desc:String
        get() {
            return ctx.getString(descResource)
        }
}