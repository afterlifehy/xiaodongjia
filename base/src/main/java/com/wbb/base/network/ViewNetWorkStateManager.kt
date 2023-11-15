package com.wbb.base.network

import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

/**
 * Created by zj on 2021/2/19.
 */
class ViewNetWorkStateManager(
    mOnNetWorkViewShowLinsener: OnNetWorkViewShowLinsener,
    isActivity: Boolean
) :
    LifecycleObserver {
    private var mOnNetWorkViewShowLinsener: OnNetWorkViewShowLinsener? = null

    /**
     * 当前界面是activity还是fragmetn, true为activity ,false为fragment
     */
    private var isActivity = true
    private var mHandler: Handler? = null

    init {
        this.isActivity = isActivity
        this.mOnNetWorkViewShowLinsener = mOnNetWorkViewShowLinsener
        mHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                mOnNetWorkViewShowLinsener.onCurrentNewWorkState(msg.obj as Boolean)
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCraete() {
        NetWorkMonitorManager.getInstance().register(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        NetWorkMonitorManager.getInstance().unregister(this)
        mHandler = null
    }

    fun onNetWorkStateChange(netWorkState: NetWorkState) {
        val mMsg = mHandler?.obtainMessage()
        if (netWorkState == NetWorkState.NONE) {
            mMsg?.obj = false
        } else {
            mMsg?.obj = true
        }
        mHandler?.sendMessage(mMsg!!)
    }
}