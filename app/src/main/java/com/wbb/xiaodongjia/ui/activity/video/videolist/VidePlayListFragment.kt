package com.wbb.xiaodongjia.ui.activity.video.videolist

import android.view.View
import androidx.viewbinding.ViewBinding
import com.wbb.base.bean.VideoPlayInfo
import com.wbb.base.help.OnItemClickLinsener
import com.wbb.base.util.ARouterMap.COURSE_ID
import com.wbb.base.util.ARouterMap.ViDEO_COURSE_DETAIL
import com.wbb.base.util.ToastUtils
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.adapter.LivePlayItemAdapter
import com.wbb.xiaodongjia.base.VbBaseFragment
import com.wbb.xiaodongjia.databinding.VideoPlayListLayoutBinding
import com.wbb.xiaodongjia.dialog.CourseListDialog
import com.wbb.xiaodongjia.mvvm.viewmodel.VideoViewmodel
import com.wbb.xiaodongjia.ui.activity.video.VideoPlayActivity

/**
 * Created by zj on 2021/3/10.
 * 课程视频播放列表
 */
class VidePlayListFragment : VbBaseFragment<VideoViewmodel, VideoPlayListLayoutBinding>(),
    View.OnClickListener, OnItemClickLinsener<VideoPlayInfo> {
    private var mVideoPlayItemAdapter: LivePlayItemAdapter? = null
    val mAllRecomLiveInfo = ArrayList<VideoPlayInfo>()
    var mType = VideoPlayActivity.COURSE_LIST
    override fun onReloadData() {
    }

    override fun providerVMClass(): Class<VideoViewmodel>? {
        return VideoViewmodel::class.java
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            mDongClassLiveData.observe(this@VidePlayListFragment, {
                hideLoadData()
                mAllRecomLiveInfo.addAll(it.list)
                showAllButton()
                bindData(it.list)
            })
            mException.observe(this@VidePlayListFragment, {
                //ToastUtils.showErrorToast(it.message)
                hideLoadData()
                showAllButton()
            })
            mCourselDetailInfo.observe(this@VidePlayListFragment, {
                hideLoadData()
                val list = ArrayList<VideoPlayInfo>()
                list.add(it)
                mAllRecomLiveInfo.addAll(list)
                showAllButton()
                bindData(list)

            })
            errMsg.observe(this@VidePlayListFragment, {
                ToastUtils.showErrorToast(it.msg)
                hideLoadData()
                showAllButton()
            })
        }
    }

    /**
     * 控制是否显示全部课程按钮
     */
    fun showAllButton() {
        if (mAllRecomLiveInfo == null || mAllRecomLiveInfo.size <= 1) {
            binding.llBom.visibility = View.GONE
        } else {
            binding.llBom.visibility = View.VISIBLE
        }

    }

    fun bindData(list: MutableList<VideoPlayInfo>) {

        if (list.size <= 0) {
            showNotDataView()
            return
        }
        if (mVideoPlayItemAdapter == null) {
            mVideoPlayItemAdapter =
                LivePlayItemAdapter(activity!!.supportFragmentManager, list)
            binding.playListVp.adapter = mVideoPlayItemAdapter
        } else {
            mVideoPlayItemAdapter?.setmList(list)
            mVideoPlayItemAdapter?.notifyDataSetChanged()
        }
    }

    override fun getData() {
        super.getData()
        showLoadData()
        activity?.intent?.getStringExtra(ViDEO_COURSE_DETAIL)?.let {
            this.mType = it
        }
        val courseId = activity?.intent?.getStringExtra(COURSE_ID)

        courseId?.let {
            if (mType == VideoPlayActivity.COURSE_LIST) {//去获取课程列表
                mViewModel.courseList(it)
            } else {//去获取单个课程详情
                mViewModel.getCourseDetail(it)
            }

        }

    }

    override val isLoadNotData: Boolean
        get() = false
    override val isShowTitle: Boolean
        get() = false

    override fun initView() {
        binding.allCourse.setOnClickListener(this)
        binding.ivBack.setOnClickListener(this)
    }

    override fun initData() {
        getData()
    }

    override fun onGetClassTypeNam(): Any {
        return "视频播放列表"
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.all_course -> {
                if (mAllRecomLiveInfo.size <= 0) {
                    ToastUtils.showToash("暂无数据")
                    return
                }
                val mCourseListDialog = CourseListDialog(context!!, mAllRecomLiveInfo, this)
                mCourseListDialog.show()
            }
            R.id.iv_back -> {
                activity?.onBackPressed()
            }
        }
    }

    override fun onItemClick(postion: Int, mInfo: VideoPlayInfo?) {
        binding.playListVp.setCurrentItem(postion, true)
    }

    override fun getVbBindingView(): ViewBinding {
        return VideoPlayListLayoutBinding.inflate(layoutInflater)
    }
}