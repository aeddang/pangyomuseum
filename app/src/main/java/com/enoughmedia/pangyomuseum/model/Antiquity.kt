package com.enoughmedia.pangyomuseum.model

data class Mounds(
    val id:String,
    val group:Int,
    val idx:Int
    ){

    var antiquitise = ArrayList<Antiquity>(); private set
    fun addAntiquity(antiquity:Antiquity){
        antiquity.idx = antiquitise.size
        antiquity.group = group
        antiquitise.add(antiquity)
    }

    val findCode:String
    get() {
        return "qrcode_${id}"
    }
}

data class Antiquity(
    val id:String
){
    var group:Int = 0; internal set
    var idx:Int = 0; internal set
}