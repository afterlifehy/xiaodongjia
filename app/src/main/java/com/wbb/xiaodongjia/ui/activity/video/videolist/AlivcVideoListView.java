package com.wbb.xiaodongjia.ui.activity.video.videolist;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.aliyun.player.AliListPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorCode;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoBean;
import com.aliyun.player.bean.InfoCode;
import com.aliyun.player.nativeclass.MediaInfo;
import com.aliyun.player.nativeclass.PlayerConfig;
import com.aliyun.player.nativeclass.TrackInfo;
import com.aliyun.player.source.StsInfo;
import com.aliyun.player.source.UrlSource;
import com.aliyun.player.source.VidAuth;
import com.aliyun.player.source.VidSts;
import com.wbb.xiaodongjia.listener.PlayAuthListener;
import com.wbb.base.bean.User;
import com.wbb.base.util.AppUtil;
import com.wbb.base.util.ToastUtils;
import com.wbb.xiaodongjia.R;
import com.wbb.xiaodongjia.bean.Video;
import com.wbb.xiaodongjia.ui.activity.video.adapter.VideoListAdapter;
import com.wbb.xiaodongjia.ui.activity.video.adapter.holder.BaseHolder;
import com.wbb.xiaodongjia.wiget.Love;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 视频列表view
 *
 * @author xlx
 */
public class AlivcVideoListView extends FrameLayout implements SeekBar.OnSeekBarChangeListener, IPlayer.OnInfoListener, IPlayer.OnCompletionListener {
    private static String TAG = "video-tag";
    private Context mContext;
    private RecyclerViewEmptySupport recycler;
    private SwipeRefreshLayout refreshView;
    private PagerLayoutManager pagerLayoutManager;
    private BaseVideoListAdapter adapter;
    private VideoListAdapter mLittleVideoListAdapte;

    /**
     * 播放凭证回调
     */
    public PlayAuthListener playAuthListener;
    private View mPlayerViewContainer;
    /**
     * 数据是否到达最后一页
     */
    private boolean isEnd;
    /**
     * 播放器的封装，可以提前准备视频
     */
    private AliListPlayer mListPlayer;
    private TextureView mTextureView;
    private List<Video> list;

    /**
     * 刷新数据listener
     */
    private OnRefreshDataListener onRefreshDataListener;
    /**
     * 判断是否处于加载数据的状态中
     */
    private boolean isLoadingData;
    /**
     * 预加载位置, 默认离底部还有5条数据时请求下一页视频列表
     */
    private static final int DEFAULT_PRELOAD_NUMBER = 2;
    /**
     * 是否点击暂停
     */
    private boolean isPause = false;
    /**
     * 当前页面是否处于可见状态
     */
    private boolean isOnBackground = false;
    /**
     * 当前选中位置
     */
    public int mCurrentPosition;
    /**
     * 正常滑动，上一个被暂停的位置
     */
    private int mLastStopPosition = -1;
    private RecyclerView.RecycledViewPool mRecycledViewPool;
    private ImageView mPlayIcon;
    private VideoTenCallBack callBack;
    private Boolean isVideoFirst = false;
    private Love mLove;
    /**
     * 当前界面的current,防止凭证拿的太快空指针
     */
    private int mCurrentViewPosition = 0;
    //用户按下的时间
    private long downTime = 0L;
    private boolean isRepeatedlyClickLinke = false;
    //当前播放视频的长度
    private Long mCurrPlayTimeSize = 0L;
    private SeekBar mCurrSeekBar;//当前播放界面的进度条

    public AlivcVideoListView(@NonNull Context context, VideoTenCallBack callBack) {
        super(context);
        this.mContext = context;
        this.callBack = callBack;
        initPlayer();
        init();
    }

    private AlivcVideoListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        throw new IllegalArgumentException("this view isn't allow create by xml");
    }

    private GestureDetector gestureDetector;

    /**
     * 是否显示暂停按钮
     *
     * @param isShow
     */
    private void isShowVidePashView(boolean isShow) {
        if (mPlayIcon != null) {
            if (isShow) {
                mPlayIcon.setVisibility(View.VISIBLE);
            } else {
                mPlayIcon.setVisibility(View.GONE);
            }

        }
    }

    /**
     * 初始化播放器相关
     */
    private void initPlayer() {
        mPlayerViewContainer = View.inflate(getContext(), R.layout.layout_player_view, null);
        mTextureView = mPlayerViewContainer.findViewById(R.id.video_textureview);
        mLove = mPlayerViewContainer.findViewById(R.id.ll_linke);
        gestureDetector = new GestureDetector(mContext,
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        //判断当前view是否可见，防止退后台、切换页面和单击同时操作导致，退后台时视频又重新开始播放
                        if (AlivcVideoListView.this.isShown()) {
                            if (!isRepeatedlyClickLinke) {//如果正在多次点击播放点赞动画，就不去处理暂停播放视频
                                onPauseClick();
                            }

                        }
                        return true;
                    }

                    @Override
                    public boolean onDown(MotionEvent e) {
                        return true;
                    }
                });
        mPlayerViewContainer.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {

            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                Surface mSurface = new Surface(surface);
                if (mListPlayer != null) {
                    mListPlayer.setSurface(mSurface);
                    mListPlayer.redraw();
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                if (mListPlayer != null) {
                    mListPlayer.redraw();
                }
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                if (mListPlayer != null) {
                    mListPlayer.setSurface(null);
                }
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
        mListPlayer = AliPlayerFactory.createAliListPlayer(mContext);
        mListPlayer.setOnInfoListener(this);
        mListPlayer.setOnCompletionListener(this);
        //打开播放器
        mListPlayer.enableLog(true);
        PlayerConfig config = mListPlayer.getConfig();
        config.mClearFrameWhenStop = true;
        mListPlayer.setConfig(config);
        mListPlayer.enableLog(true);
        mListPlayer.setLoop(true);
        mListPlayer.setAutoPlay(false);
        mListPlayer.setScaleMode(IPlayer.ScaleMode.SCALE_TO_FILL);
        //mListPlayer.set
        //指定播放清晰度
        mListPlayer.setDefinition(VideoQuality.PLAY.getValue());
        mListPlayer.setOnPreparedListener(new IPlayer.OnPreparedListener() {
            @Override
            public void onPrepared() {
                MediaInfo mediaInfo = mListPlayer.getMediaInfo();
                List<TrackInfo> trackInfos = mediaInfo.getTrackInfos();
                int size = trackInfos.size();
                for (TrackInfo trackInfo : trackInfos) {
                    if (trackInfo.getVodDefinition().equals(VideoQuality.VIDEO.getValue())) {
                        float aspectRatio = (float) trackInfo.getVideoWidth() / trackInfo.getVideoHeight();
                        if (aspectRatio < 9f / 15f) {
                            mListPlayer.setScaleMode(IPlayer.ScaleMode.SCALE_ASPECT_FILL);
                        } else {
                            mListPlayer.setScaleMode(IPlayer.ScaleMode.SCALE_ASPECT_FIT);
                        }
                        break;
                    }
                }
                if (!isVideoFirst) {
                    if (!isPause && !isOnBackground) {
                        mListPlayer.start();
                    }
                    isVideoFirst = false;
                }
            }
        });
        mListPlayer.setOnRenderingStartListener(new IPlayer.OnRenderingStartListener() {
            @Override
            public void onRenderingStart() {
                //  2018/11/21 隐藏封面
                BaseHolder holder = (BaseHolder) recycler.findViewHolderForLayoutPosition(mCurrentPosition);
                if (holder != null) {
                    holder.getCoverView().setVisibility(GONE);
                }
                if (mLoadingListener != null) {
                    mLoadingListener.onLoadingEnd();
                }
            }
        });

        mListPlayer.setOnLoadingStatusListener(new IPlayer.OnLoadingStatusListener() {
            @Override
            public void onLoadingBegin() {
                if (mLoadingListener != null) {
                    mLoadingListener.onLoadingBegin();
                }
            }

            @Override
            public void onLoadingEnd() {
                if (mLoadingListener != null) {
                    mLoadingListener.onLoadingEnd();
                }
            }

            @Override
            public void onLoadingProgress(int percent,
                                          float netSpeed) {
                if (mLoadingListener != null) {
                    mLoadingListener.onLoadingProgress(percent, netSpeed);
                }
            }
        });
        mListPlayer.setOnErrorListener(new IPlayer.OnErrorListener() {
            @Override
            public void onError(ErrorInfo errorInfo) {
                if (errorInfo.getCode() == ErrorCode.ERROR_SERVER_POP_TOKEN_EXPIRED) {
                    //鉴权过期
                    if (mTimeExpiredErrorListener != null) {
                        mTimeExpiredErrorListener.onTimeExpiredError();
                        Log.i(TAG, "刷新鉴权");
                    }
                }
                ToastUtils.INSTANCE.showToash(errorInfo.getMsg());
            }
        });

    }

    /**
     * 鉴权过期时发生
     */
    private OnTimeExpiredErrorListener mTimeExpiredErrorListener;

    public void setTimeExpiredErrorListener(
            OnTimeExpiredErrorListener mTimeExpiredErrorListener) {
        this.mTimeExpiredErrorListener = mTimeExpiredErrorListener;
    }

    private void init() {
        list = new ArrayList<>();
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_video_list, this, true);
        recycler = view.findViewById(R.id.recycler);
        /**
         * 去掉默认动画
         */
        recycler.getItemAnimator().setAddDuration(0);
        recycler.getItemAnimator().setChangeDuration(0);
        recycler.getItemAnimator().setMoveDuration(0);
        recycler.getItemAnimator().setRemoveDuration(0);
        ((SimpleItemAnimator) recycler.getItemAnimator()).setSupportsChangeAnimations(false);

        refreshView = view.findViewById(R.id.refresh_view);
        refreshView.setProgressViewEndTarget(true, 300);
        refreshView.setColorSchemeColors(getResources().getColor(R.color.color_ff1b1b27));
        refreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (onRefreshDataListener != null) {
                    isLoadingData = true;
                    onRefreshDataListener.onRefresh();
                }
            }
        });
        recycler.setHasFixedSize(true);
        mRecycledViewPool = new RecyclerView.RecycledViewPool();
        mRecycledViewPool.setMaxRecycledViews(0, 50);
        recycler.setRecycledViewPool(mRecycledViewPool);
        pagerLayoutManager = new PagerLayoutManager(mContext);
        pagerLayoutManager.setItemPrefetchEnabled(true);
        recycler.setLayoutManager(pagerLayoutManager);
//        TextView tv = new TextView(getContext());
//        tv.setText(R.string.home_no_following);
//        tv.setTextColor(Color.WHITE);
//        tv.setTextSize(70);
        recycler.setEmptyView(view.findViewById(R.id.ll_empty_view));
        String s = "您没有关注任何人";
        Log.d("zhangqi", s);
//        recycler.setEmptyView(tv);
//        recycler.setEmptyView(View.inflate(this.getContext(),R.layout.widget_empty_page,null));
        pagerLayoutManager.setOnViewPagerListener(new PagerLayoutManager.OnViewPagerListener() {
            @Override
            public void onInitComplete() {
                Log.e(TAG, "onInitComplete mCurrentPosition= " + mCurrentPosition);
                int position = pagerLayoutManager.findFirstVisibleItemPosition();
                if (position != -1) {
                    mCurrentPosition = position;
                }
                int itemCount = adapter.getItemCount();
                if (itemCount - position < DEFAULT_PRELOAD_NUMBER && !isLoadingData && !isEnd) {
                    // 正在加载中, 防止网络太慢或其他情况造成重复请求列表
                    isLoadingData = true;
                    loadMore();
                }
                startPlay(mCurrentPosition);
                mLastStopPosition = -1;
                Log.e(TAG, "onInitComplete mCurrentPosition= " + mCurrentPosition);
            }

            @Override
            public void onPageRelease(boolean isNext, int position) {

                if (mCurrentPosition == position) {
                    mLastStopPosition = position;
                    stopPlay();
                    BaseHolder holder = (BaseHolder) recycler.findViewHolderForLayoutPosition(position);
                    if (holder != null) {
                        holder.getCoverView().setVisibility(VISIBLE);
                        if (holder.getPlayIcon() != null) {
                            holder.getPlayIcon().setVisibility(View.GONE);
                        }
                    }
                }

            }

            @Override
            public void onPageSelected(int position, boolean b) {
                //重新选中视频不播放，如果该位置被stop过则会重新播放视频
                if (mCurrentPosition == position && mLastStopPosition != position) {
                    return;
                }

                int itemCount = adapter.getItemCount();
                Log.d(TAG, "onInitComplete: " + itemCount + "-" + position + "-" + DEFAULT_PRELOAD_NUMBER + "-" + isLoadingData + "-" + isEnd);

                if (itemCount - position < DEFAULT_PRELOAD_NUMBER && !isLoadingData && !isEnd) {
                    // 正在加载中, 防止网络太慢或其他情况造成重复请求列表
                    isLoadingData = true;
                    loadMore();
                }
                if (itemCount == position + 1 && isEnd) {
                    Toast.makeText(getContext(), "这是最后一个视频了", Toast.LENGTH_SHORT).show();
                }
                //开始播放选中视频
                startPlay(position);
                mCurrentPosition = position;
            }
        });

    }

    private void clickLinke() {
        if (mLittleVideoListAdapte != null) {
            if (mLittleVideoListAdapte.getDataList().get(mCurrentPosition).isLike() == Video.UNLIKE) {
                // mLittleVideoListAdapte.getOnItemBtnClick().onStar(list.get(mCurrentPosition), mCurrentPosition);
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Long intervalTime = System.currentTimeMillis() - downTime;
                if (intervalTime < 500 && intervalTime > 100) {
                    isRepeatedlyClickLinke = true;
                    clickLinke();
                    if (mLove != null) {
                        mLove.clickLinke(ev);
                    }
                    return true;
                }
                downTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                if (System.currentTimeMillis() - downTime > 1000) {//长按生效
                    showReport();
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isRepeatedlyClickLinke = false;
                if (System.currentTimeMillis() - downTime > 1000) {//长按生效
                    showReport();
                    return true;
                }

                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 举报
     */
    private void showReport() {
    }

    /**
     * 停止视频播放
     */
    private void stopPlay() {
        removePlayView();
        mListPlayer.stop();
        mListPlayer.setSurface(null);
    }

    /**
     * 移除播放器
     */
    private void removePlayView() {
        ViewParent parent = mPlayerViewContainer.getParent();
        if (parent != null && parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(mPlayerViewContainer);
        }
    }

    /**
     * 拿视频播放授权
     *
     * @param position
     */
    private void startPlay(int position) {
        mCurrentViewPosition = position;
        Log.d(TAG, "startPlay: " + mCurrentViewPosition);
        if (position < 0 || position > list.size()) {
            return;
        }
        Video video = list.get(position);

        User user = null;
//        String userInfo = AppCenter.mSpUtil.getString(Constant.USERINFO);
//        if (!TextUtils.isEmpty(userInfo)) {
//            user = AppCenter.mGson.fromJson(userInfo, User.class);
//        }
//        Boolean isExist = false;
//        if (!user.isWater()) {
//            String date = AppUtil.getTimeLongToString(System.currentTimeMillis(), "yyyy-MM-dd");
//            String id = user.getUserId() + date;
//            List<String> ids = new ArrayList();
//            List<DayVideoCountBean> dayVideoCountList = RealmUtil.getInstanceBlock().findDayVideoCountBeanByFactor(DayVideoCountBean.class, "id", id);
//            if (dayVideoCountList != null && dayVideoCountList.size() > 0) {
//                DayVideoCountBean dayVideoCountBean = dayVideoCountList.get(0);
//                if (dayVideoCountBean.getId().contains(date)) {
//                    if (!TextUtils.isEmpty(dayVideoCountBean.getVideoId())) {
//                        ids = Arrays.asList(dayVideoCountBean.getVideoId().split(","));
//                    }
//                    if (ids.size() > 9) {
//                        callBack.videoTen();
//                        stopPlay();
//                        return;
//                    } else {
//                        for (int i = 0; i < ids.size(); i++) {
//                            if (TextUtils.equals(((Video) (adapter.list.get(position))).getVideoId(), ids.get(i))) {
//                                isExist = true;
//                                break;
//                            } else {
//                                isExist = false;
//                            }
//                        }
//                        if (!isExist) {
//                            dayVideoCountBean.setVideoId(dayVideoCountBean.getVideoId() + "," + ((Video) (adapter.list.get(position))).getVideoId());
//                            dayVideoCountBean.setCount(dayVideoCountBean.getCount() + 1);
//                            RealmUtil.getInstance().addRealm(dayVideoCountBean);
//                        }
//                    }
//                } else {
//                    RealmUtil.getInstance().deleteDayVideoCount();
//                    RealmUtil.getInstance().addRealm(new DayVideoCountBean(id, 1, ((Video) (adapter.list.get(position))).getVideoId()));
//                }
//            } else {
//                RealmUtil.getInstance().addRealm(new DayVideoCountBean(id, 1, ((Video) (adapter.list.get(position))).getVideoId()));
//            }
//        }
        /**
         * 如果有凭证直接播放
         */
        if (video.getSourceType() == VideoSourceType.TYPE_AUTH) {
            /**
             * 如果有凭证直接播放
             */
            if (video.getVidAuth().getPlayAuth() == null) {
                playAuthListener.getPlayAuth(video, position);
            } else {
                start(video, position);
            }
        } else if (video.getSourceType() == VideoSourceType.TYPE_URL) {
            start(video, position);
        }
    }

    /**
     * 开始播放
     *
     * @param position 播放位置
     */
    public void start(Video video, int position) {
        Log.d(TAG, "" + mCurrentViewPosition + "-" + position);
        if (mCurrentViewPosition != position) {
            return;
        }

        //恢复界面状态
        isPause = false;
        BaseHolder holder = (BaseHolder) recycler.findViewHolderForLayoutPosition(position);
        /**
         * 视频凭证拿的太快 所以会报错
         */
        Log.d(TAG, "" + mCurrentViewPosition + "-" + position + "-" + holder);
        mPlayIcon = holder.getPlayIcon();

        isShowVidePashView(false);
        removePlayView();
        mLittleVideoListAdapte.setOnSeekBarChangeListener(this);

        if (holder != null) {
            holder.getContainerView().addView(mPlayerViewContainer, 0);
        }
        mCurrPlayTimeSize = 0L;
        mCurrSeekBar = holder.getSeekBarSchedule();
        //防止退出后台之后，再次调用start方法，导致视频播放
        if (video.getSourceType() == VideoSourceType.TYPE_STS) {
            VidSts vidSts = video.getVidStsSource();
            StsInfo stsInfo = new StsInfo();
            stsInfo.setAccessKeyId(vidSts.getAccessKeyId());
            stsInfo.setAccessKeySecret(vidSts.getAccessKeySecret());
            stsInfo.setSecurityToken(vidSts.getSecurityToken());
            stsInfo.setRegion(vidSts.getRegion());
            if (position - mCurrentPosition == 1) {
                mListPlayer.moveToNext(stsInfo);
            } else if (position - mCurrentPosition == -1) {
                mListPlayer.moveToPrev(stsInfo);
            } else {
                mListPlayer.moveTo(video.getUUID(), stsInfo);
            }
        } else if (video.getSourceType() == VideoSourceType.TYPE_URL) {
            if (position - mCurrentPosition == 1) {
                mListPlayer.moveToNext();
            } else if (position - mCurrentPosition == -1) {
                mListPlayer.moveToPrev();
            } else {
                mListPlayer.moveTo(video.getUUID());
            }
            mListPlayer.prepare();
            /**
             * 是否是wifi网络
             */
            mListPlayer.start();
        } else if (video.getSourceType() == VideoSourceType.TYPE_LIVE) {
            UrlSource urlSource = video.getUrlSource();
            mListPlayer.setDataSource(urlSource);
            mListPlayer.prepare();
        } else if (video.getSourceType() == VideoSourceType.TYPE_AUTH) {
            VidAuth auth = video.getVidAuth();
//            if (position - mCurrentPosition == 1) {
//                mListPlayer.moveToNext();
//            } else if (position - mCurrentPosition == -1) {
//                mListPlayer.moveToPrev();
//            } else {
//                mListPlayer.moveTo(video.getUUID());
//            }
            mListPlayer.setDataSource(auth);
            mListPlayer.prepare();
            mListPlayer.start();
            mListPlayer.pause();
        }
    }


    /**
     * 加载更多
     */
    private void loadMore() {
        if (onRefreshDataListener != null) {
            onRefreshDataListener.onLoadMore();
        }
    }

    /**
     * 刷新数据
     *
     * @param list 刷新数据
     */
    public void refreshData(List<IVideoSourceModel> list) {
        clearNotShowVideo(list);
        if (refreshView != null && refreshView.isRefreshing()) {
            refreshView.setRefreshing(false);
            // 加载完毕, 重置加载状态
        }
        if (mListPlayer != null) {
            mListPlayer.clear();
            for (IVideoSourceModel iVideoSourceModel : list) {
                if (iVideoSourceModel.getSourceType() == VideoSourceType.TYPE_AUTH) {
                    mListPlayer.addVid(iVideoSourceModel.getVidAuthSource().getVid(), iVideoSourceModel.getUUID());
                } else if (iVideoSourceModel.getSourceType() == VideoSourceType.TYPE_URL) {
                    String url = iVideoSourceModel.getUrlSource().getUri();
                    String uuid = iVideoSourceModel.getUUID();
                    mListPlayer.addUrl(url, uuid);
                }
            }
        }
        isEnd = false;
        isLoadingData = false;
        adapter.refreshData(list);
        refreshView.setRefreshing(false);
    }

    /**
     * 刷新数据，并播放指定位置的视频
     *
     * @param list     视频列表数据
     * @param position 刷新完成之后播放位置
     */
    public void refreshData(List<IVideoSourceModel> list, int position) {
        int size = list.size();
        if (position < 0) {
            position = 0;
        }
        if (size <= position) {
            position = size - 1;
        }
        //获取不进行显示
        int notShowVideoCount = 0;
        for (int i = 0; i < size; i++) {
            if (i < position && list.get(i).getSourceType() == VideoSourceType.TYPE_ERROR_NOT_SHOW) {
                notShowVideoCount++;
            }
        }
        mCurrentPosition = position - notShowVideoCount;
        refreshData(list);
        recycler.scrollToPosition(mCurrentPosition);

    }

    /**
     * 清除不允许显示的视频
     *
     * @param list
     */
    private void clearNotShowVideo(List<IVideoSourceModel> list) {
        Iterator<IVideoSourceModel> it = list.iterator();
        while (it.hasNext()) {
            if (it.next().getSourceType() == VideoSourceType.TYPE_ERROR_NOT_SHOW) {
                it.remove();
            }
        }
    }

    /**
     * 加载更多数据
     *
     * @param list
     */
    public void addMoreData(List<IVideoSourceModel> list) {
        if (list == null || list.size() < 10) {
            isEnd = true;
        } else {
            isEnd = false;
        }
        clearNotShowVideo(list);
        isLoadingData = false;

        if (adapter != null) {
            adapter.addMoreData(list);
        }
        if (mListPlayer != null) {
            for (IVideoSourceModel iVideoSourceModel : list) {
                if (iVideoSourceModel.getSourceType() == VideoSourceType.TYPE_AUTH) {
                    mListPlayer.addVid(iVideoSourceModel.getVidStsSource().getVid(), iVideoSourceModel.getUUID());
                } else if (iVideoSourceModel.getSourceType() == VideoSourceType.TYPE_URL) {
                    mListPlayer.addUrl(iVideoSourceModel.getUrlSource().getUri(), iVideoSourceModel.getUUID());
                }
            }
        }
        if (refreshView.isRefreshing()) {
            refreshView.setRefreshing(false);
        }

    }

    private IPlayer.OnLoadingStatusListener mLoadingListener;

    public void setLoadingListener(IPlayer.OnLoadingStatusListener mLoadingListener) {
        this.mLoadingListener = mLoadingListener;
    }

    public void setOnRefreshDataListener(OnRefreshDataListener onRefreshDataListener) {
        this.onRefreshDataListener = onRefreshDataListener;
    }

    public void pausePlayFirst() {
        this.isVideoFirst = true;
    }

    @Override
    public void onCompletion() {
        isShowVidePashView(true);
    }


    /**
     * 刷新数据
     */
    public interface OnRefreshDataListener {
        /**
         * 下拉刷新
         */
        void onRefresh();

        /**
         * 上拉加载
         */
        void onLoadMore();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mListPlayer != null) {
            mListPlayer.release();
        }
        if (mRecycledViewPool != null) {
            mRecycledViewPool.clear();
            mRecycledViewPool = null;
        }
    }

    /**
     * 暂停播放
     */
    public void pausePlay() {
        isPause = true;
        isShowVidePashView(true);
        mListPlayer.pause();

    }

    /**
     * 恢复播放
     */
    public void resumePlay() {
        isPause = false;
        isShowVidePashView(false);
        mListPlayer.start();
    }

    /**
     * activity不可见或者播放页面不可见时调用该方法
     */
    public void setOnBackground(boolean isOnBackground) {
        this.isOnBackground = isOnBackground;
        if (isOnBackground) {
            pausePlay();
        } else {
            resumePlay();
        }
    }

    /**
     * 视频暂停/恢复的时候使用，
     */
    public void onPauseClick() {
        if (isPause) {
            resumePlay();
        } else {
            pausePlay();
        }
    }

    /**
     * 设置播放器数量
     *
     * @param playerCount
     */
    public void setPlayerCount(int playerCount) {

        mListPlayer.setPreloadCount(playerCount);
    }

    /**
     * 设置adapter
     *
     * @param adapter
     */
    public void setAdapter(BaseVideoListAdapter adapter) {
        this.adapter = adapter;
        recycler.setAdapter(adapter);
        if (adapter instanceof VideoListAdapter) {
            mLittleVideoListAdapte = (VideoListAdapter) adapter;
        }
        this.list = adapter.getDataList();
    }

    /**
     * 移除当前播放的视频
     */
    public void removeCurrentPlayVideo() {
        stopPlay();
        int tempCurrentPostion = mCurrentPosition;
        //当前视频如果为最后一个视频，则需要播放上一个视频
        if (tempCurrentPostion == list.size() - 1 && list.size() >= 2) {
            recycler.scrollToPosition(tempCurrentPostion - 1);
        }
        list.remove(tempCurrentPostion);
        adapter.notifyDataSetChanged();

    }

    public RecyclerView getRecycler() {
        return recycler;
    }

    public List<Video> getList() {
        return list;
    }

    /**
     * 设置是否能刷新
     *
     * @param isRef
     */
    public void setRecycler(boolean isRef) {
        refreshView.setEnabled(isRef);
    }

    /**
     * 视频列表获取失败
     */
    public void loadFailure() {
        if (refreshView.isRefreshing()) {
            refreshView.setRefreshing(false);
        }
    }

    /**
     * 获取当前播放的视频唯一标志
     */
    public String getCurrentUid() {
        String mCurrentUid = "";
        if (mListPlayer != null) {
            mCurrentUid = mListPlayer.getCurrentUid();
        }
        return mCurrentUid;
    }

    /**
     * 移动到指定的视频
     */
    public void moveTo(String uid, StsInfo stsInfo) {
        if (mListPlayer != null) {
            mListPlayer.moveTo(uid, stsInfo);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int progress = seekBar.getProgress();
        if (mCurrPlayTimeSize != 0L) {
            double pf = AppUtil.INSTANCE.toFloat(progress, 100);
            long playPro = (long) (mCurrPlayTimeSize * pf);
            mListPlayer.seekTo(playPro, IPlayer.SeekMode.Accurate);
            mListPlayer.start();
        }
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {


    }

    @Override
    public void onInfo(InfoBean infoBean) {
        if (mCurrPlayTimeSize == 0) {
            mCurrPlayTimeSize = mListPlayer.getDuration();
        }
        if (infoBean.getCode() == InfoCode.LoopingStart) {//开启了循环播放，但只放一只，在次播放需要用户自己点击从新播放
            pausePlay();

        }
        if (infoBean.getCode() == InfoCode.CurrentPosition && mCurrPlayTimeSize > 0) {
            int valu = (int) (AppUtil.INSTANCE.toFloat(infoBean.getExtraValue(), mCurrPlayTimeSize) * 100);
            if (mCurrSeekBar != null) {
                mCurrSeekBar.setProgress(valu);
            }
        }
    }


    public interface VideoTenCallBack {
        void videoTen();
    }
}
