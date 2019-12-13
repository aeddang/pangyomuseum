package com.skeleton.view.player

import com.google.android.exoplayer2.source.MediaSource
import com.skeleton.view.camera.Camera


class ExoPlayerViewModel{

    var playbackPosition:Long = 0
    var currentWindow:Int = 0
    var videoPath:String? = null
    var playWhenReady:Boolean = false
    var source: MediaSource? = null
    var currentVolume:Float = 1.0f
    constructor(
    )


    fun reset(){
        source = null
        videoPath = null
        playbackPosition = 0
        currentWindow = 0
    }

    fun destroy(){
        source = null
        videoPath = null
    }

}