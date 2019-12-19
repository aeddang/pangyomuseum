package com.enoughmedia.pangyomuseum.component

import android.content.Context
import android.util.AttributeSet
import com.enoughmedia.pangyomuseum.R
import com.google.android.exoplayer2.ui.PlayerView
import com.skeleton.view.player.ExoVideoPlayer
import kotlinx.android.synthetic.main.cp_intro_player.view.*

class IntroPlayer: ExoVideoPlayer {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    private val appTag = javaClass.simpleName
    override fun getLayoutResId(): Int  = R.layout.cp_intro_player
    override fun getAppName(): Int { return R.string.app_name }
    override fun getNeededPermissions():Array<String>{
        return arrayOf(
            android.Manifest.permission.WAKE_LOCK)
    }

    override fun getPlayerView(): PlayerView {
        return playerView
    }
}