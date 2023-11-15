package com.wbb.base.util

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 * Created by zj on 2021/1/27.
 */
class RxTimerUtil {
    private var mDisposable: Disposable? = null

    /**
     * 每隔milliseconds毫秒后执行next操作
     *
     * @param milliseconds
     * @param next
     */
    fun interval(milliseconds: Long, next: IRxNext?, endTime: Int = -1) {
        Observable.interval(milliseconds, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Long> {
                override fun onSubscribe(disposable: Disposable) {
                    mDisposable = disposable
                }

                override fun onNext(number: Long) {
                    if (endTime > 0 && number >= endTime) {
                        cancel()
                    }
                    next?.doNext(number)
                }

                override fun onError(e: Throwable) {}
                override fun onComplete() {}
            })
    }

    /**
     * milliseconds毫秒后执行next操作
     *
     * @param milliseconds
     * @param next
     */
    fun timer(milliseconds: Long, next: IRxNext?) {
        Observable.timer(milliseconds, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Long> {
                override fun onSubscribe(disposable: Disposable) {
                    mDisposable = disposable
                }

                override fun onNext(number: Long) {
                    next?.doNext(number)
                }

                override fun onError(e: Throwable) {
                    //取消订阅
                    cancel()
                }

                override fun onComplete() {
                    //取消订阅
                    cancel()
                }
            })
    }

    /**
     * 取消订阅
     */
    fun cancel() {
        if (mDisposable != null && !mDisposable!!.isDisposed) {
            mDisposable!!.dispose()
        }
    }

    interface IRxNext {
        fun doNext(number: Long)
    }
}