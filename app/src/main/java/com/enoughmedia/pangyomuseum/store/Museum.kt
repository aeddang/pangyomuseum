package com.enoughmedia.pangyomuseum.store

import android.content.Context
import com.enoughmedia.pangyomuseum.model.Antiquity
import com.enoughmedia.pangyomuseum.model.Mounds
import com.enoughmedia.pangyomuseum.model.MoundsID


class Museum(val ctx: Context, val setting:SettingPreference){

    val mounds = arrayListOf<Mounds>(
        Mounds(ctx, setting,"0", 0, 0),
        Mounds(ctx, setting,"1", 1, 1),
        Mounds(ctx, setting,"2", 2, 2),
        Mounds(ctx, setting,"3", 3, 3),
        Mounds(ctx, setting,"4", 4, 4),
        Mounds(ctx, setting,"5", 5, 5)
    )

    val keys =  arrayOf(
        arrayOf("01","02","03","04","05","06"),
        arrayOf("11","12","13","14","15","16"),
        arrayOf("21","22","23","24","25","26"),
        arrayOf("31","32","33","34","35","36"),
        arrayOf("41","42","43","44","45","46"),
        arrayOf("51","52","53","54","55","56")
    )

    fun setup(){
        mounds.forEachIndexed { index, mound ->
            keys[index].forEach {
                mound.addAntiquity(
                    Antiquity(
                        ctx,
                        setting,
                        it
                    )
                )
            }
        }
    }

    val findQRCodes:List<String>
    get() {
        return mounds.map { it.findCode }
    }

    fun getMoundByCode(code:String):Mounds?{
        return mounds.find { it.findCode == code }
    }

    fun getMoundByBeacon(id:String?):Mounds?{
        return mounds.find { it.findBeaconID == id }
    }

    fun getMound(id:MoundsID?):Mounds?{
        if(id == null) return null
        return mounds.find { it.id == id }
    }

    fun getMound(idx:Int):Mounds?{
        return mounds[idx]
    }

    fun getFindAntiquities():List<Antiquity>{
        return mounds.flatMap { m -> m.antiquitise.filter { it.isFind }.map{ it } }
    }

    fun allAntiquities():List<Antiquity>{
        return mounds.flatMap { it.antiquitise }
    }

}