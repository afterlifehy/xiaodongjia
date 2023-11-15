package com.wbb.base.dialog

import android.app.ActionBar
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.chouyou.base.base.ShareItemData
import com.wbb.base.R
import com.wbb.base.adapter.ShareItemAdapter
import com.wbb.base.emuns.SHARE_EMUN
import com.wbb.base.util.StatusBarUtils
import kotlinx.android.synthetic.main.view_shar_layout.*

/**
 * Created by zj on 2020/11/27.
 * 分享弹框
 */
class ShareDialog(context: Context) : Dialog(context, R.style.DaoxilaDialog_Alert),
    OnItemClickListener, View.OnClickListener {
    var mOnShareClickLisener: OnShareClickLisener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_shar_layout)
        init()
        initView()
        initAdapter()
    }

    /**
     * 设置分享监听
     */
    fun setOnShareClickLinener(mOnShareClickLisener: OnShareClickLisener) {
        this.mOnShareClickLisener = mOnShareClickLisener
    }

    fun init() {
        //        //宽度全屏显示
        val layoutParams = window?.getAttributes()
        layoutParams?.width = ActionBar.LayoutParams.MATCH_PARENT
        layoutParams?.height = ActionBar.LayoutParams.WRAP_CONTENT
        layoutParams?.gravity = Gravity.BOTTOM
        window?.setAttributes(layoutParams)
//        StatusBarUtils.setImmersiveStatus(window, false)
    }

    fun initView() {
        rl_cancel.setOnClickListener(this)
        iv_delete.setOnClickListener(this)
    }

    fun initAdapter() {
        val list = ArrayList<ShareItemData>()
        list.add(
            ShareItemData(
                SHARE_EMUN.WEIXIN.id,
                SHARE_EMUN.WEIXIN.iconId,
                SHARE_EMUN.WEIXIN.shareNam
            )
        )
        list.add(
            ShareItemData(
                SHARE_EMUN.CIRCLE_OF_FRIENDS.id,
                SHARE_EMUN.CIRCLE_OF_FRIENDS.iconId,
                SHARE_EMUN.CIRCLE_OF_FRIENDS.shareNam
            )
        )
        list.add(
            ShareItemData(
                SHARE_EMUN.COPY_URL.id,
                SHARE_EMUN.COPY_URL.iconId,
                SHARE_EMUN.COPY_URL.shareNam
            )
        )
        val mShareItemAdapter = ShareItemAdapter(list)
        mShareItemAdapter.setOnItemClickListener(this)
        item_list.layoutManager = GridLayoutManager(context, 3)
        item_list.adapter = mShareItemAdapter
        //StatusBarUtils.setNavigationBarColor(context as Activity, R.color.white)
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        val mShareItemData = adapter.data.get(position) as ShareItemData
        mOnShareClickLisener?.onShareClickLinsener(mShareItemData)
        dismiss()
    }

    interface OnShareClickLisener {
        fun onShareClickLinsener(mShareItemData: ShareItemData)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rl_cancel -> {
                dismiss()
            }
            R.id.iv_delete -> {
                dismiss()
            }
        }
    }
}