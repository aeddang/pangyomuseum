package com.skeleton.rx
import com.skeleton.module.ImageFactory


interface Rx {
    fun onSubscribe(){}
}

interface DiChild {
    fun injectImageFactory(imageFactory: ImageFactory?) {}
}