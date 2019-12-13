package com.enoughmedia.pangyomuseum.store

import com.enoughmedia.pangyomuseum.model.Antiquity
import com.enoughmedia.pangyomuseum.model.Mounds

class Museum(){

    val mounds = arrayListOf<Mounds>(
        Mounds("0", 0, 0),
        Mounds("1", 1, 1),
        Mounds("2", 2, 2),
        Mounds("3", 3, 3),
        Mounds("4", 4, 4),
        Mounds("5", 5, 5)
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

    fun getMound(code:String):Mounds?{
        return mounds.find { it.findCode == code }
    }




}