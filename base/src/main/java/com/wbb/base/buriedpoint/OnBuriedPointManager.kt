package com.wbb.base.buriedpoint

/**
 * Created by zj on 2020/12/9.
 * 神策埋点的代理类
 */
class OnBuriedPointManager private constructor() {
    private var mOnBuriedPointLinsener: OnBuriedPointLinsener? = null

    companion object {
        private var instance: OnBuriedPointManager? = null
            get() {
                if (field == null) {
                    field = OnBuriedPointManager()
                }
                return field
            }

        fun get(): OnBuriedPointManager {
            return instance!!
        }
    }

    /**
     *初始化值
     */
    fun initProxy(mOnBuriedPointLinsener: OnBuriedPointLinsener?) {
        this.mOnBuriedPointLinsener = mOnBuriedPointLinsener
    }

    fun getOnBuriedPointManager(): OnBuriedPointLinsener? {
        return mOnBuriedPointLinsener
    }
}