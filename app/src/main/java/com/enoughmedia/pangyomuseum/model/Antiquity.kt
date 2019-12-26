package com.enoughmedia.pangyomuseum.model

import android.content.Context
import com.enoughmedia.pangyomuseum.AppConst
import com.enoughmedia.pangyomuseum.store.SettingPreference
import com.google.ar.sceneform.math.Vector3
import java.util.*
import kotlin.collections.ArrayList


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
    val findBeaconID:String
        get() {
            return "beacon_${id}"
        }
    val findCode:String
        get() {
            return "qrcode_${id}"
        }

    val panoViewPath:String
        get() {
            return "panoramas/pano${id}.jpg"
        }
    val cardViewPath:String
        get() {
            return "cardboards/cardboard${id}.jpg"
        }

    val modelResource:Int
        get() {
            return ctx.resources.getIdentifier("${AppConst.PACKAGE_NAME}:raw/mounds${id}", null, null)
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

    val worldVec3: Vector3
        get() {
            val x= 0.0f
            val y= -0.5f
            val z = 0.5f
            return Vector3(x, y, z)
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
            return ctx.resources.getIdentifier("${AppConst.PACKAGE_NAME}:string/data_antiquity${moundsID}_${id}_title", null, null)
        }
    val infoResource:Int
        get() {
            return ctx.resources.getIdentifier("${AppConst.PACKAGE_NAME}:string/data_antiquity${moundsID}_${id}_info", null, null)
        }
    val descResource:Int
        get() {
            return ctx.resources.getIdentifier("${AppConst.PACKAGE_NAME}:string/data_antiquity${moundsID}_${id}_desc", null, null)
        }

    val imageResource:Int
        get() {
            return ctx.resources.getIdentifier("${AppConst.PACKAGE_NAME}:drawable/z_antiquity${moundsID}_${id}", null, null)
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

    val posVec3: Vector3
        get() {
            val x= Math.random().toFloat() * 1.5f - 0.75f
            val y= -0.25f
            val z= Math.random().toFloat() * 1.5f - 0.75f
            return Vector3(x, y, z)
        }
}