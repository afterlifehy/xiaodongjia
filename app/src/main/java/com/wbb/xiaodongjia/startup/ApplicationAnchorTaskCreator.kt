package com.wbb.xiaodongjia.startup

import com.wbb.base.start.StartUpKey
import com.xj.anchortask.library.AnchorTask
import com.xj.anchortask.library.IAnchorTaskCreator

/**
 * Created by zj on 2021/3/5.
 */
class ApplicationAnchorTaskCreator : IAnchorTaskCreator {
    override fun createTask(taskName: String): AnchorTask? {
        when (taskName) {
            StartUpKey.MUST_BE_NITIALIZED -> {
                return AnchorTaskZero()
            }
            StartUpKey.MUST_BE_NOE -> {
                return AnchorTaskOne()

            }
        }
        return null
    }
}