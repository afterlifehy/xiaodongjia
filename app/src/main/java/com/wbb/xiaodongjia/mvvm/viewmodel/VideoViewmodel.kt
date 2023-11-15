package com.wbb.xiaodongjia.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.chouyou.waterbridge.mvvm.base.ErrorMessage
import com.wbb.base.bean.PaginationInfo
import com.wbb.base.bean.VideoPlayInfo
import com.wbb.base.mvvm.base.BaseViewModel
import com.wbb.base.util.AssetsUtils
import com.wbb.base.util.GsonUtils
import com.wbb.xiaodongjia.bean.Video
import com.wbb.xiaodongjia.mvvm.repository.DongClassReposity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * Created by zj on 2021/2/1.
 */
class VideoViewmodel : BaseViewModel() {
    val mDongClassReposity by lazy {
        DongClassReposity()
    }
    var mDongClassLiveData = MutableLiveData<PaginationInfo<VideoPlayInfo>>()
    var mCourselDetailInfo = MutableLiveData<VideoPlayInfo>()

    val mVideList = MutableLiveData<List<Video>>()
    fun getVideoList() {
        launch {
            withContext(Dispatchers.IO) {
                delay(1000)
            }
            val vides_list = AssetsUtils.getAssetsString("video_list.txt")
            val list = GsonUtils.getObjectList(vides_list, Video::class.java)
            mVideList.value = list
        }
    }

    /**
     * 获取课程列表
     */
    fun courseList(courseId: String) {
        val par = HashMap<String, String>()
        par["courseTypeId"] = courseId
        launch {
            val response = withContext(Dispatchers.IO) {
                mDongClassReposity.courseList(par)
            }
            executeResponse(response, {
                mDongClassLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = 1000))
            })
        }
    }

    /**
     * 获取课程详情
     */
    fun getCourseDetail(courseId: String) {
        val par = HashMap<String, String>()
        par["id"] = courseId
        launch {
            val response = withContext(Dispatchers.IO) {
                mDongClassReposity.getCourseDetail(par)
            }
            executeResponse(response, {
                mCourselDetailInfo.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = 1000))
            })
        }
    }
}   