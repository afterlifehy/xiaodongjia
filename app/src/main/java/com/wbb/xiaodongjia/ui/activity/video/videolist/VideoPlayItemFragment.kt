package com.wbb.xiaodongjia.ui.activity.video.videolist

import android.os.Bundle
import android.util.Pair
import android.view.View
import com.chouyou.waterbridge.base.BaseDataFragmentKt
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.MediaItem.AdsConfiguration
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer.DecoderInitializationException
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil.DecoderQueryException
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory.AdsLoaderProvider
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.ads.AdsLoader
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector.ParametersBuilder
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.StyledPlayerControlView
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.util.ErrorMessageProvider
import com.google.android.exoplayer2.util.EventLogger
import com.google.android.exoplayer2.util.Log
import com.google.android.exoplayer2.util.Util
import com.google.gson.Gson
import com.wbb.base.bean.VideoPlayInfo
import com.wbb.base.buriedpoint.OnBuriedPointManager
import com.wbb.base.emuns.SHARE_EMUN
import com.wbb.base.ext.i18n
import com.wbb.base.help.WxSharManager
import com.wbb.base.util.ToastUtils
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.emuns.ATTENTION_CLASS
import com.wbb.xiaodongjia.mvvm.viewmodel.AttentionViewModel
import com.wbb.xiaodongjia.ui.activity.video.FactoryUtil
import kotlinx.android.synthetic.main.fragment_play_new_layout.*


/**
 * Created by zj on 2021/3/9.
 * 视频播放单个item
 */
class VideoPlayItemFragment : BaseDataFragmentKt<AttentionViewModel>(),
    PlayerControlView.VisibilityListener,
    Player.EventListener, View.OnClickListener, WxSharManager.OnShareResultLinsener,
    StyledPlayerControlView.ProgressUpdateListener {
    protected var player: SimpleExoPlayer? = null
    private var dataSourceFactory: DataSource.Factory? = null
    private var trackSelector: DefaultTrackSelector? = null
    private var trackSelectorParameters: DefaultTrackSelector.Parameters? = null
    private var lastSeenTrackGroupArray: TrackGroupArray? = null
    private val startAutoPlay = false
    protected var playerView: StyledPlayerView? = null

    // For ad playback only.
    private var adsLoader: AdsLoader? = null
    private var currPagerIsVisible = false
    private var mVideoPlayInfo: VideoPlayInfo? = null

    //当前播放状态
    private var mCurrentPlayStates = Player.STATE_IDLE

    //当前播放进度
    private var playTime = 0L
    override fun onReloadData() {
    }

    override fun providerVMClass(): Class<AttentionViewModel> {
        return AttentionViewModel::class.java
    }

    companion object {
        const val VIDEO_PLAY_INFP = "video_play_info"
        fun getFragmetn(mInfo: VideoPlayInfo?): VideoPlayItemFragment {
            val mVideoPlayItemFragment = VideoPlayItemFragment()
            val mBundle = Bundle()
            mBundle.putSerializable(VIDEO_PLAY_INFP, mInfo)
            mVideoPlayItemFragment.arguments = mBundle
            return mVideoPlayItemFragment
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer()
            playerView?.onResume()
        }
    }

    override fun setUserVisibleHint(hidden: Boolean) {
        super.setUserVisibleHint(hidden)
        this.currPagerIsVisible = hidden
        Log.i("VideoPlayItemFragment", "setUserVisibleHint:${hidden}")
        checkIsPlay()
    }

    /**
     * 开始播放
     */
    private fun checkIsPlay() {
        if (this.currPagerIsVisible) {
            if (mCurrentPlayStates == Player.STATE_ENDED) {//如果是播放结束

            } else {
                player?.play()
                checkPlayButton(false, 1)
            }

        } else {
            onPlayPause()
            checkPlayButton(true, 2)
        }
    }

    /**
     * 设置显示参数
     */
    fun setCouserInfo() {
        mVideoPlayInfo?.let {
            tv_course_name.setText(it.courseName)
            tv_course_description.setText(it.author)
            if (it.follow) {
                iv_flolw.setImageResource(R.mipmap.flolow)
            } else {
                iv_flolw.setImageResource(R.mipmap.not_flolow)
            }
        }

    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            if (playerView != null) {
                playerView!!.onPause()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseAdsLoader()
        releasePlayer()
    }

    private fun releaseAdsLoader() {
        if (adsLoader != null) {
            adsLoader?.release()
            adsLoader = null
            playerView?.overlayFrameLayout?.removeAllViews()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            if (playerView != null) {
                playerView!!.onPause()
            }
            releasePlayer()
        }
    }

    override val isLoadNotData: Boolean
        get() = false
    override val isShowTitle: Boolean
        get() = false

    override fun getLayoutResId(): Int {
        return R.layout.fragment_play_new_layout
    }

    /**
     * 重置播放
     */
    fun releasePlayer() {
        player?.release()
        player = null
    }

    fun onPlayPause() {
        playerView?.onPause()
    }

    override fun initView() {
        player_view.setOnClickListener(this)
        iv_share.setOnClickListener(this)
        iv_flolw.setOnClickListener(this)
        exo_play_pause.setOnClickListener(this)

        playerView = player_view
        playerView?.setErrorMessageProvider(PlayerErrorMessageProvider())
        playerView?.requestFocus()
        playerView?.getControllerView()?.setProgressUpdateListener(this)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        Log.i("VideoPlayItemFragment", "onHiddenChanged")
    }

    override fun initData() {
        mVideoPlayInfo = arguments?.getSerializable(VIDEO_PLAY_INFP) as VideoPlayInfo
        setCouserInfo()
        Log.i(
            "VideoPlayItemFragment",
            "data:${this.currPagerIsVisible}${mVideoPlayInfo!!.courseId}"
        )
        Log.i(
            "VideoPlayItemFragment",
            "data:${this.userVisibleHint}${mVideoPlayInfo!!.courseId}"
        )
        if (mVideoPlayInfo == null) {
            return
        }
        val builder = ParametersBuilder( /* context= */requireContext())
        trackSelectorParameters = builder.build()
        dataSourceFactory = FactoryUtil.getDataSourceFactory( /* context= */requireContext())
        initializePlayer()
    }

    override fun onGetClassTypeNam(): Any {
        return "测试"
    }

    override fun onVisibilityChange(visibility: Int) {
    }

    private fun initializePlayer() {
        if (player == null) {
            val renderersFactory: RenderersFactory =
                FactoryUtil.buildRenderersFactory( /* context= */requireContext(), false)

            val mediaSourceFactory: MediaSourceFactory =
                DefaultMediaSourceFactory(dataSourceFactory!!)
                    .setAdsLoaderProvider(object : AdsLoaderProvider {
                        override fun getAdsLoader(adsConfiguration: AdsConfiguration): AdsLoader? {
                            return getMyAdsLoader(adsConfiguration)
                        }
                    })
                    .setAdViewProvider(playerView!!)
            trackSelector = DefaultTrackSelector( /* context= */requireContext())
            trackSelector?.setParameters(trackSelectorParameters!!)
            lastSeenTrackGroupArray = null


            player = SimpleExoPlayer.Builder( /* context= */requireContext(), renderersFactory)
                .setMediaSourceFactory(mediaSourceFactory)
                .setTrackSelector(trackSelector!!)
                .build()
            player!!.addListener(this)
            player!!.addAnalyticsListener(EventLogger(trackSelector))
            player!!.setAudioAttributes(AudioAttributes.DEFAULT,  /* handleAudioFocus= */false)
            player!!.playWhenReady = startAutoPlay
            playerView?.setPlayer(player)
        }
        player!!.setMediaItems(createMediaItems(), true)
        player!!.prepare()
        if (userVisibleHint) {//检查是否可以播放
            checkIsPlay()
        }

    }

    private fun getMyAdsLoader(adsConfiguration: AdsConfiguration): AdsLoader? {
        // The ads loader is reused for multiple playbacks, so that ad playback can resume.
        if (adsLoader == null) {
            adsLoader = ImaAdsLoader.Builder( /* context= */requireContext()).build()
        }
        adsLoader!!.setPlayer(player)
        return adsLoader
    }

    /**
     *
     */
    private fun createMediaItems(): List<MediaItem> {
        val list = ArrayList<MediaItem>()
        val mediaItem =
            MediaItem.fromUri(mVideoPlayInfo!!.videoPath)
        list.add(mediaItem)
        return list
    }

    private class PlayerErrorMessageProvider :
        ErrorMessageProvider<ExoPlaybackException> {
        override fun getErrorMessage(e: ExoPlaybackException): Pair<Int, String> {
            var errorString: String = i18n(R.string.error_generic)
            if (e.type == ExoPlaybackException.TYPE_RENDERER) {
                val cause = e.rendererException
                if (cause is DecoderInitializationException) {
                    val decoderInitializationException = cause
                    if (decoderInitializationException.codecInfo == null) {
                        if (decoderInitializationException.cause is DecoderQueryException) {
                        } else if (decoderInitializationException.secureDecoderRequired) {
                        } else {
                        }
                    } else {
                    }
                }
            }
            return Pair.create(0, errorString)
        }
    }

    override fun onPlaybackStateChanged(@Player.State playbackState: Int) {
        this.mCurrentPlayStates = playbackState
        when (playbackState) {
            Player.STATE_BUFFERING -> {//开始缓冲
            }
            Player.STATE_READY -> {//开始播放
                //checkPlayButton(false, 3)

            }
            Player.STATE_ENDED -> {//播放结束
                checkPlayButton(!isPlay(), 4)
            }
        }
    }

    override fun onPlayerError(e: ExoPlaybackException) {
    }

    override fun onTracksChanged(
        trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray
    ) {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_share -> {//分享
                if (mVideoPlayInfo == null) {
                    return
                }
                WxSharManager.instance()
                    .shareToGraphis(
                        requireContext(),
                        mVideoPlayInfo!!.courseName,
                        mVideoPlayInfo!!.videoPath,
                        "",
                        this
                    )
            }
            R.id.iv_flolw -> {//收藏
                mVideoPlayInfo?.let {
                    mViewModel.addFollow(
                        it.courseId,
                        ATTENTION_CLASS.CLASSROOM.mType.toString()
                    ).observe(this, {
                        val isFollow = mVideoPlayInfo!!.follow
                        if (!isFollow) {
                            mVideoPlayInfo?.let {
                                OnBuriedPointManager.get().getOnBuriedPointManager()
                                    ?.onCourseCollection("", it.courseName, it.courseName, "1")
                            }
                            ToastUtils.showSucessToast(i18n(R.string.att_sucess))
                        } else {
                            mVideoPlayInfo?.let {
                                OnBuriedPointManager.get().getOnBuriedPointManager()
                                    ?.onCourseCollection("", it.courseName, it.courseName, "0")
                            }
                            // ToastUtils.showSucessToast("取消关注")
                        }
                        mVideoPlayInfo?.follow = !isFollow
                        setCouserInfo()
                    })
                }

            }
            R.id.exo_play_pause,
            R.id.player_view -> {//播放器的点击事件
                toPlayPause()
            }
        }
    }

    /**
     * 请求判断当前是播放还是暂停
     */
    private fun toPlayPause() {
        playerView?.getControllerView()?.let {
            playerView?.player?.let { it1 ->
                it.dispatchPlayPause(it1)
                checkPlayButton(!isPlay(), 5)
            }
        }
    }

    /**
     * 判断当前是否在播放
     */
    fun checkPlayButton(isShow: Boolean, index: Int) {
        if (exo_play_pause == null) {
            return
        }
        if (isShow) {
            exo_play_pause.visibility = View.VISIBLE
            //去上传埋点
            playPauseMd()
        } else {
            exo_play_pause.visibility = View.GONE
        }

    }

    /**
     * 当前是否播放
     */
    fun isPlay(): Boolean {
        if (player == null) {
            return false
        }
        @Player.State val state = player!!.playbackState
        if (state == Player.STATE_IDLE || state == Player.STATE_ENDED || !player!!.playWhenReady) {
            return false
        } else {
            return true
        }

    }

    override fun onShareSucess(mType: SHARE_EMUN) {
        mVideoPlayInfo?.let {
            OnBuriedPointManager.get().getOnBuriedPointManager()
                ?.onCourseShare("", it.courseName, "", "1", mType.shareNam)

        }

    }

    override fun onShareError(mType: SHARE_EMUN) {
        mVideoPlayInfo?.let {
            OnBuriedPointManager.get().getOnBuriedPointManager()
                ?.onCourseShare("", it.courseName, "", "0", mType.shareNam)

        }
    }

    override fun onShareCancer(mType: SHARE_EMUN) {
    }

    override fun onProgressUpdate(position: Long, bufferedPosition: Long) {
        playTime = position / 1000
    }

    /**
     * 暂停的时候需要去调用埋点
     */
    fun playPauseMd() {
        mVideoPlayInfo?.let {
            OnBuriedPointManager.get().getOnBuriedPointManager()
                ?.onCourseView("", it.courseName, "", "${playTime}")

        }
    }

}