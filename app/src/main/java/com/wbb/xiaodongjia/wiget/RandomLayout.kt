package com.wbb.xiaodongjia.wiget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.wbb.base.util.AppUtil
import com.wbb.base.util.StatusBarUtils
import com.wbb.xiaodongjia.bean.TagViewBean
import com.wbb.xiaodongjia.bean.WaterDropletsInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by zj on 2021/1/26.
 */
class RandomLayout : FrameLayout, OnWaterDropletsClickLinsener {
    private var randomViewWidth = AppUtil.dip2px(83f + 20f)
    private var randomViewHeight = AppUtil.dip2px(67f + 10f)
    private var mRandomXYList: ArrayList<TagViewBean>? = null
    private var mScreanWidth = 0
    private var mScreanHeight = 0
    private var mStatusBarHeigh = 0
    private var mRandom: Random? = null
    private var mTitleHeight = AppUtil.dip2px(100f)
    private var mOnActiivtyClickLinsener: OnActiivtyClickLinsener? = null

    constructor(
        conext: Context
    ) : super(conext)

    constructor(conext: Context, attrs: AttributeSet) : super(conext, attrs)
    constructor(conext: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        conext,
        attrs,
        defStyleAttr
    )

    init {
        mRandomXYList = ArrayList()
        mRandom = Random()
        mScreanWidth = AppUtil.getScreanWidth()
        mScreanHeight = AppUtil.getScreanHeight()
        mStatusBarHeigh = StatusBarUtils.getStatusBarHeight()
        initView()

    }

    fun initView() {


    }

    /**
     * 添加水滴
     */
    fun addRandomView(
        list: List<WaterDropletsInfo>?,
        mOnActiivtyClickLinsener: OnActiivtyClickLinsener? = null
    ) {
        this.mOnActiivtyClickLinsener = mOnActiivtyClickLinsener
        if (list == null || list.size <= 0) {
            return
        }
        val mEmpList = list
        GlobalScope.launch(Dispatchers.Main) {
            val empList = ArrayList<TagViewBean>()
            withContext(Dispatchers.IO) {

                for (mInof in mEmpList) {
                    val mTagViewBean = createRandomViewData()
                    empList.add(mTagViewBean)
                    mRandomXYList?.add(mTagViewBean)
                }
            }
            withContext(Dispatchers.Main) {
                empList.forEach {
                    val mRandomView = RandomView(context, it)
                    mRandomView.setOnActiivtyClickLinsener(this@RandomLayout)
                    val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                    params.leftMargin = it.x
                    params.topMargin = it.y
                    addView(mRandomView, params)
                }
            }
        }
    }

    suspend fun createRandomViewData(): TagViewBean {
        synchronized(mRandomXYList!!) {
            var x = mRandom?.nextInt(mScreanWidth - randomViewWidth)
            var y = 0
            if (mScreanHeight - mTitleHeight - mStatusBarHeigh - randomViewHeight > 0) {
                y =
                    mRandom?.nextInt(mScreanHeight - mTitleHeight - mStatusBarHeigh - randomViewHeight)!! + mStatusBarHeigh
            } else {
                y = 100
            }
            while (!isContains(x, y)) {
                x = mRandom?.nextInt(mScreanWidth - randomViewWidth)
                if (mScreanHeight - mTitleHeight - mStatusBarHeigh - randomViewHeight > 0) {
                    y =
                        mRandom?.nextInt(mScreanHeight - mTitleHeight - mStatusBarHeigh - randomViewHeight)!! + mStatusBarHeigh
                } else {
                    y = 100
                }
            }
            return TagViewBean(x!!, y)
        }

    }

    private fun isContains(x: Int? = 0, y: Int? = 0): Boolean {
        mRandomXYList?.forEach {
            if (it.x < x!! + randomViewWidth && it.x > x - randomViewWidth) {
                if (it.y < y!! + randomViewHeight && it.y > y - randomViewHeight) {
                    return false
                }
            }
        }
        return true
    }

    override fun onClickLinsener(view: View, mTagViewBean: TagViewBean?) {

        removeView(view)
        if (mTagViewBean != null) {
            var meTagViewBean: TagViewBean? = null
            mRandomXYList?.forEach {
                if (mTagViewBean.x == it.x && mTagViewBean.y == it.y) {
                    meTagViewBean = mTagViewBean
                }
            }
            meTagViewBean?.let {
                mRandomXYList?.remove(it)
            }
        }
        mOnActiivtyClickLinsener?.onActivityClickLinsener(WaterDropletsInfo())
    }
}