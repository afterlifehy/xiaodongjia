package com.wbb.xiaodongjia.dialog

import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wbb.base.bean.VideoPlayInfo
import com.wbb.base.help.OnItemClickLinsener
import com.wbb.base.util.AppUtil
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.adapter.CourseListAdapter

/**
 * Created by zj on 2021/2/2.
 */
class CourseListDialog(
    context: Context,
    mAllRecomLiveInfo: ArrayList<VideoPlayInfo>?,
    mOnItemClickLinsener: OnItemClickLinsener<VideoPlayInfo>? = null
) :
    Dialog(context, R.style.DaoxilaDialog_Alert), OnItemClickLinsener<VideoPlayInfo> {
    private var cour_list: RecyclerView? = null
    var mAllRecomLiveInfo: ArrayList<VideoPlayInfo>? = null
    private var mOnItemClickLinsener: OnItemClickLinsener<VideoPlayInfo>? = null

    init {
        this.mAllRecomLiveInfo = mAllRecomLiveInfo
        this.mOnItemClickLinsener = mOnItemClickLinsener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_course_list_dialog)
        init()
        findViewById<ImageView>(R.id.iv_delete).setOnClickListener {
            dismiss()
        }
        iniAdapterView()
    }

    fun iniAdapterView() {
        cour_list = findViewById(R.id.cour_list)
        mAllRecomLiveInfo?.let {
            val mCourseListAdapter = CourseListAdapter(it, this)
            cour_list?.layoutManager = LinearLayoutManager(context)
            cour_list?.adapter = mCourseListAdapter
        }

    }

    fun init() {
        //        //宽度全屏显示
        val layoutParams = window?.getAttributes()
        layoutParams?.width = ActionBar.LayoutParams.MATCH_PARENT
        layoutParams?.height = AppUtil.getScreanHeight() / 2
        layoutParams?.gravity = Gravity.BOTTOM
        window?.setAttributes(layoutParams)
        //   StatusBarUtils.setImmersiveStatus(window, false)
    }

    override fun onItemClick(postion: Int, mInfo: VideoPlayInfo?) {
        dismiss()
        mOnItemClickLinsener?.onItemClick(postion, mInfo)
    }
}