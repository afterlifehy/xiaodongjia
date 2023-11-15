package com.wbb.base.util

import android.os.Handler
import android.os.Looper
import android.os.Message


/**
 * Created by zj on 2021/3/16.
 * 依次提示所有toast信息
 */
class ToastManager private constructor() {

    val queue = ToastShowQueue<String>(100)

    var mHandler: Handler? = null
    var mCarriedOutToast: CarriedOutToast? = null

    init {
        mHandler = ToashHandler()
        mCarriedOutToast = CarriedOutToast(queue, mHandler)
        mCarriedOutToast?.start()

    }

    companion object {
        private var instance: ToastManager? = null
            get() {
                if (field == null) {
                    field = ToastManager()
                }
                return field
            }

        fun get(): ToastManager {
            return instance!!
        }
    }

    fun showToast(msg: String) {
        queue.add(msg)
        mCarriedOutToast?.let {
            if (it.isLock()) {
                it.upNotify()
            }
        }
    }

    class CarriedOutToast(
        mToastShowQueue: ToastShowQueue<String>?,
        mHandler: Handler?
    ) : Thread() {
        private val lock = Object()
        var queue: ToastShowQueue<String>? = null
        var isLocck = false
        var mHandler: Handler? = null

        init {
            this.queue = mToastShowQueue
            this.mHandler = mHandler
        }

        override fun run() {
            super.run()
            while (true) {
                checkShowWoast()
            }
        }

        fun checkShowWoast() = synchronized(lock) {
            val msg = queue?.poll()
            if (msg == null) {
                isLocck = true
                lock.wait()
            } else {
                val msgHd = mHandler?.obtainMessage()
                msgHd?.obj = msg
                mHandler?.sendMessage(msgHd!!)
                sleep(2500)
            }

        }

        fun upNotify() = synchronized(lock) {
            if (isLocck) {
                lock.notify()
                isLocck = false
            }
        }

        /**
         * 是否睡眠当中
         */
        fun isLock(): Boolean {
            return isLocck
        }
    }


    class ToashHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            ToastUtils.showErrorToast(msg.obj.toString())
        }

    }
}