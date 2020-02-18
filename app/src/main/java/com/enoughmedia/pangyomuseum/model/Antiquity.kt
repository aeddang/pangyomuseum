package com.enoughmedia.pangyomuseum.model

import android.content.Context
import android.graphics.Point
import android.graphics.PointF
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
    var isFind = false
    var antiquitise = ArrayList<Antiquity>(); private set
    fun addAntiquity(antiquity:Antiquity){
        antiquity.idx = antiquitise.size
        antiquity.group = group
        antiquity.moundsID = id
        antiquitise.add(antiquity)
    }
    val findBeaconID:Int
        get() {
            return when(id){
                "0" -> 32239
                "1" -> 32240
                "2" -> 35997
                "3" -> 35998
                "4" -> 36001
                "5" -> 36002
                "6" -> 36007
                "7" -> 12211
                "8" -> 34327
                "9" -> 34333
                "10" -> 34334
                else -> 0
            }
        }
    val markerPos:PointF?
        get() {
            return when(id){
                "0" -> PointF(825f/1645f,15f/978f)
                "1" -> PointF(1011f/1645f,15f/978f)
                "2" -> PointF(515f/1645f,0f/978f)
                "3" -> PointF(1194f/1645f,247f/978f)
                "4" -> PointF(952f/1645f,283f/978f)
                "5" -> PointF(722f/1645f,362f/978f)
                "8" -> PointF(286f/1645f,471f/978f)
                "9" -> PointF(621f/1645f,563f/978f)
                "10" -> PointF(277f/1645f,733f/978f)
                else -> null
            }
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
            val key= when(id){
                else -> id
            }
            return ctx.resources.getIdentifier("${AppConst.PACKAGE_NAME}:raw/mounds${key}", null, null)
        }

    val titleResource:Int
        get() {
            return ctx.resources.getIdentifier("${AppConst.PACKAGE_NAME}:string/data_mounds${id}_title", null, null)
        }
    val descResource:Int
        get() {
            return ctx.resources.getIdentifier("${AppConst.PACKAGE_NAME}:string/data_mounds${id}_desc", null, null)
        }
    val addressResource:Int
        get() {
            return ctx.resources.getIdentifier("${AppConst.PACKAGE_NAME}:string/data_mounds${id}_address", null, null)
        }
    val title:String
        get() {
            return ctx.getString(titleResource)
        }
    val desc:String
        get() {
            return ctx.getString(descResource)
        }

    val address:String
        get() {
            return ctx.getString(addressResource)
        }

    val worldVec3: Vector3
        get() {
            val x= 0.0f
            val y= -0.5f
            val z = 0.7f
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
            return ctx.resources.getIdentifier("${AppConst.PACKAGE_NAME}:raw/antiquity${moundsID}_${id}", null, null)
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
            return  when(id){
                "21" -> Vector3(0.0f, -0.45f, 0.8f)
                "22" -> Vector3(0.21f, -0.45f, 0.7f)
                "23" -> Vector3(-0.4f, -0.45f, 0.91f)
                "24" -> Vector3(0.2f, -0.45f, 0.51f)
                "25" -> Vector3(-0.22f, -0.45f, 0.63f)
                "26" -> Vector3(0.25f, -0.45f, 1.12f)

                "31" -> Vector3(0.0f, -0.45f, 0.3f)

                "41" -> Vector3(0.0f, -0.41f, 0.5f)
                "42" -> Vector3(0.21f, -0.42f, 0.6f)
                "43" -> Vector3(-0.4f, -0.43f, 0.81f)
                "44" -> Vector3(0.2f, -0.44f, 0.41f)
                "45" -> Vector3(-0.22f, -0.45f, 0.43f)
                "46" -> Vector3(0.25f, -0.46f, 1.02f)
                "47" -> Vector3(0.0f, -0.46f, 0.82f)

                "51" -> Vector3(0.0f, -0.42f, 0.3f)
                "52" -> Vector3(0.21f, -0.42f, 0.6f)

                "61" -> Vector3(0.0f, -0.42f, 0.5f)
                "62" -> Vector3(0.1f, -0.42f, 1.1f)

                "71" -> Vector3(0.0f, -0.41f, 1.3f)
                "72" -> Vector3(0.12f, -0.42f, 0.7f)
                "73" -> Vector3(-0.02f, -0.43f, 0.5f)
                "74" -> Vector3(0.1f, -0.44f, 0.3f)
                "75" -> Vector3(-0.1f, -0.45f, 0.7f)
                "76" -> Vector3(0.0f, -0.42f, 0.9f)

                "81" -> Vector3(-0.1f, -0.41f, 0.5f)
                "82" -> Vector3(-0.05f, -0.42f, 0.9f)

                "91" -> Vector3(-0.2f, -0.41f, 0.3f)

                "101" -> Vector3(0.21f, -0.43f, 0.6f)

                else -> Vector3(0.0f, -0.41f, 0.0f)
            }
        }
}