package com.wbb.base.util

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * Created by zj on 2021/1/27.
 */
class DelayUtils {
    fun startCountdown(
        countTime: Int,
        mOnTimeCountdownCallbackLinsener: OnTimeCountdownCallbackLinsener? = null
    ) {
        runBlocking {
            flow {
                for (i in 1..countTime / 1000) {
                    delay(1000)
                    emit(i)
                }

            }.flowOn(Dispatchers.IO)
                .collect {
                    mOnTimeCountdownCallbackLinsener?.OnTimeCallbackLinsener(it)
                }
        }
    }

    interface OnTimeCountdownCallbackLinsener {
        fun OnTimeCallbackLinsener(time: Int)
    }

}