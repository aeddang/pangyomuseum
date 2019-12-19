package com.skeleton.view.player
import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.metadata.MetadataOutput
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.AssetDataSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.VideoListener
import com.google.android.exoplayer2.upstream.DataSource.Factory
import com.lib.page.PagePresenter
import com.lib.page.PageRequestPermission
import com.lib.util.Log
import com.skeleton.rx.RxFrameLayout

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

abstract class ExoVideoPlayer :  RxFrameLayout,PlayBack, Player.EventListener, VideoListener, MetadataOutput, PageRequestPermission {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context,attrs)
    private val appTag = javaClass.simpleName
    companion object {
        private  var viewModelInstance: ExoPlayerViewModel? = null
        fun getViewmodel(): ExoPlayerViewModel {
            return if(viewModelInstance == null) ExoPlayerViewModel() else viewModelInstance!!
        }
    }
    val playWhenReady:Boolean
    get() {
        player ?: return false
        return player!!.playWhenReady
    }

    protected var delegate: PlayBackDelegate? = null
    final override fun setOnPlayerListener( _delegate:PlayBackDelegate? ){ delegate = _delegate }


    abstract fun getNeededPermissions():Array<String>
    protected open fun requestPlayerPermission(){ PagePresenter.getInstance<Any>().requestPermission(getNeededPermissions(), this) }
    protected open fun hasPlayerPermission():Boolean{ return PagePresenter.getInstance<Any>().hasPermissions(getNeededPermissions() ) }


    private var timeDisposable: Disposable? = null
    protected var timeDelegate: PlayBackTimeDelegate? = null
    final override fun setOnPlayTimeListener( _delegate:PlayBackTimeDelegate? ){
        timeDelegate = _delegate
        if(timeDelegate == null){
            stopTimeSearch()
            return
        }
        if( sharedModel.playWhenReady )  startTimeSearch()
    }
    private fun startTimeSearch() {
        timeDisposable?.dispose()
        timeDisposable = Observable.interval(10, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                player?.let {
                    onTimeChange(it.currentPosition)

                }
            }
    }
    private fun stopTimeSearch() {
        timeDisposable?.dispose()
        timeDisposable = null
    }

    protected  var sharedModel:ExoPlayerViewModel = getViewmodel()
    protected  var player: SimpleExoPlayer? = null
    private var playerView:PlayerView? = null
    var initImage:ImageView? = null; private set
    protected open fun getUserAgent(): String { return "AwesomePlayer" }
    @StringRes abstract fun getAppName():Int
    abstract fun getPlayerView(): PlayerView
    open protected fun getInitImageView(): ImageView? { return null }

    @CallSuper
    override fun onCreatedView() {
        initImage = getInitImageView()
    }
    override fun onDestroyedView() {
        releasePlayer()
        sharedModel.reset()
        stopTimeSearch()
        delegate = null
        timeDelegate = null
    }

    @CallSuper
    override fun onPause(){
        releasePlayer()
    }

    @CallSuper
    override fun onResume() {
        initPlayer()
    }

    @CallSuper
    open fun initPlayer() {
        if ( !hasPlayerPermission() ) {
            requestPlayerPermission()
            return
        }
        if( player != null ) return

        val audioAttributes = AudioAttributes.Builder()
        .setUsage(C.USAGE_MEDIA)
        .setContentType(C.CONTENT_TYPE_MOVIE)
        .build()

        playerView = getPlayerView()
        player = ExoPlayerFactory.newSimpleInstance(context)
        player?.addListener( this )
        player?.addVideoListener( this )
        player?.addMetadataOutput(this)
        player?.setAudioAttributes(audioAttributes, true)
        playerView?.player = player
        sharedModel.source?.let {  player?.prepare(it) }
        setVolume(sharedModel.currentVolume)
        player?.seekTo(sharedModel.currentWindow, sharedModel.playbackPosition)
        if( sharedModel.playWhenReady )  resume()
        else pause()
    }

    @CallSuper
    open fun releasePlayer() {
        player?.let {
            sharedModel.playbackPosition = it.currentPosition
            sharedModel.currentWindow = it.currentWindowIndex
            sharedModel.playWhenReady = it.playWhenReady
            it.removeListener( this )
            it.removeVideoListener( this )
            it.release()
            player = null
            playerView = null
            stopTimeSearch()
        }
    }

    private fun bildAssetDataSource(uri: Uri): MediaSource {
        val dataSourceFactory: Factory = Factory { AssetDataSource(context) }
        return ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri)

    }

    private fun buildDataSource(uri: Uri): MediaSource {
        val dataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context, context?.getString( getAppName())))
        return ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        val userAgent = this.getUserAgent()
        val extension = uri.lastPathSegment
        extension?.let {
            return if (it.contains("m3u8")) HlsMediaSource.Factory(DefaultHttpDataSourceFactory(userAgent)).createMediaSource(uri)
            else if (it.contains("mp3") || it.contains("mp4")) ExtractorMediaSource.Factory(DefaultHttpDataSourceFactory(userAgent)).createMediaSource(uri)
            else {
                HlsMediaSource.Factory(DefaultHttpDataSourceFactory(userAgent)).createMediaSource(uri)
                /*
                val dashChunkSourceFactory = DefaultDashChunkSource.Factory(DefaultHttpDataSourceFactory("ua", DefaultBandwidthMeter()))
                val manifestDataSourceFactory = DefaultHttpDataSourceFactory(userAgent)
                DashMediaSource.Factory(dashChunkSourceFactory, manifestDataSourceFactory).createMediaSource(uri)
                */
            }
        }
        return ExtractorMediaSource.Factory(DefaultHttpDataSourceFactory(userAgent)).createMediaSource(uri)
    }

    private var isCompleted = false
    private var isInit = false
    protected var initTime = 0L
    @CallSuper
    override fun load(videoPath:String, initTime:Long, isDataSorce:Boolean) {

        Log.d(appTag, "videoPath $videoPath initTime $initTime")
        sharedModel.videoPath = videoPath
        val uri = Uri.parse(videoPath)
        val source:MediaSource = if( isDataSorce ) {
            if(videoPath.indexOf("assets:///") == -1) buildDataSource(uri) else bildAssetDataSource(uri)
        } else buildMediaSource(uri)
        isInit = false
        isCompleted = false
        sharedModel.source = source
        player?.prepare(source)
        initImage?.visibility = View.VISIBLE
        this.initTime = initTime

    }

    override fun reload(){
        isCompleted = false
        sharedModel.source?.let {
            player?.prepare(it)
            resume()
            return
        }
        onError(-1)
    }



    override fun pause(){
        sharedModel.playWhenReady = false
        player?.playWhenReady = false
        delegate?.onStop(this)
        stopTimeSearch()
    }


    override fun resume(){
        if(isCompleted){
            reload()
            return
        }
        initImage?.visibility = View.GONE
        sharedModel.playWhenReady = true
        player?.playWhenReady = true
        delegate?.onPlay(this)
        if( timeDelegate != null ) startTimeSearch()
    }

    @CallSuper
    override fun seek(t:Long){
        Log.d(appTag, "seek $t")
        player?.seekTo(sharedModel.currentWindow, t)
    }

    @CallSuper
    override fun setVolume(v: Float) {
        sharedModel.currentVolume = v
        player?.volume = v
    }

    @CallSuper
    override fun onCompleted(){
        isCompleted = true
        pause()
        initImage?.visibility = View.VISIBLE


    }
    @CallSuper
    override fun onError(e:Any?){
        pause()
        delegate?.onError(this, e ?: -1)
    }
    @CallSuper
    override fun onBuffering(){
        delegate?.onBuffering(this)
    }

    @CallSuper
    override fun onReady(){
        delegate?.onReady(this)
    }

    override fun onTimeChange(t:Long){
        timeDelegate?.onTimeChanged(this, t)
    }

    override fun onMetadata(metadata: Metadata){}

    @CallSuper
    override fun onLoadingChanged(isLoading: Boolean) {
        Log.d(appTag, "onLoadingChanged $isLoading")
        if( isLoading && !isInit ){
            isInit = true
            onInit()
            val d = player?.duration ?: 0L
            delegate?.onLoad(this, d)
            Log.d(appTag, "onInit $initTime")
            if(initTime != 0L) seek(initTime)
            initTime = 0L
        }
    }
    @CallSuper
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        Log.d(appTag, "onPlayerStateChanged $playbackState")
        when(playbackState){
            Player.STATE_ENDED -> {
                onCompleted()
                stopTimeSearch()
                delegate?.onCompleted(this)
            }
            Player.STATE_BUFFERING -> onBuffering()
            Player.STATE_READY -> onReady()
        }
    }

    @CallSuper
    override fun onPlayerError(error: ExoPlaybackException) {
        Log.d(appTag, "onPlayerError ${error.message}")
        onError(error)
    }

    override fun onTimelineChanged( timeline: Timeline, manifest: Any? ,@Player.TimelineChangeReason reason: Int){
        Log.d(appTag, "onTimelineChanged $timeline")
    }
    override fun onTracksChanged( trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray) {
        Log.d(appTag, "onTracksChanged $trackGroups")
    }
    override fun onSeekProcessed() {
        Log.d(appTag, "onSeekProcessed")
    }

    override fun onRepeatModeChanged(@Player.RepeatMode repeatMode: Int) {}
    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {}
    override fun onPositionDiscontinuity(@Player.DiscontinuityReason reason: Int) {}
    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {}
    override fun onVideoSizeChanged( width: Int, height: Int, unappliedRotationDegrees: Int, pixelWidthHeightRatio: Float) { }
    override fun onSurfaceSizeChanged(width: Int, height: Int) {}
    override fun onRenderedFirstFrame() {}

}