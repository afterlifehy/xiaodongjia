package com.wbb.base.util

import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

/**
 * Created by zj on 2021/1/27.
 */
class ExecutorsUtils private constructor() {
    val customerDispatcher = Executors.newFixedThreadPool(5).asCoroutineDispatcher()

    companion object {
        private var instance: ExecutorsUtils? = null
            get() {
                if (field == null) {
                    field = ExecutorsUtils()
                }
                return field
            }

        fun get(): ExecutorsUtils {
            return instance!!
        }
    }

    fun getExcutorsPool(): ExecutorCoroutineDispatcher {
        return customerDispatcher
    }
}
