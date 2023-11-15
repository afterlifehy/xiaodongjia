package com.wbb.xiaodongjia.dialog

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.chouyou.waterbridge.mvvm.base.ErrorMessage
import com.chouyou.waterbridge.mvvm.base.SafeMutableLiveData
import com.wbb.base.buriedpoint.OnBuriedPointManager
import com.wbb.base.buriedpoint.ShenCeManagerKt
import com.wbb.base.ext.i18n
import com.wbb.base.util.StatusBarUtils
import com.wbb.base.util.ToastUtils
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.mvvm.viewmodel.UserViewModel
import com.wbb.xiaodongjia.ui.activity.help.UserInfoInputHelp
import kotlinx.android.synthetic.main.dialog_user_info_input_layout.*

/**
 * Created by zj on 2021/2/19.
 * 登录成功后，如果没有用户信息需要录入
 * 在哪个activity使用，必须把当前 activity设置为
 * android:windowSoftInputMode="adjustResize" 否则无法滑动上去
 */
class UserInfoInputDialog(mActivity: FragmentActivity, mUserViewModel: UserViewModel?) :
    Dialog(mActivity, R.style.DaoxilaDialog_Alert),
    View.OnClickListener, LifecycleOwner {
    var mUserInfoInputHelp: UserInfoInputHelp? = null
    var mActivity: FragmentActivity? = null
    private var mUserViewModel: UserViewModel? = null

    init {
        this.mActivity = mActivity
        this.mUserViewModel = mUserViewModel

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mView = View.inflate(mActivity, R.layout.dialog_user_info_input_layout, null)
        setContentView(mView)
        init()
        mUserInfoInputHelp = UserInfoInputHelp(mActivity!!, mView)
        findById()
        regViewModel()
    }

    fun regViewModel() {
        mUserViewModel?.apply {
            val mError = SafeMutableLiveData<ErrorMessage>()
            registerToListen(mError)
            mError.observe(this@UserInfoInputDialog, Observer {
                ToastUtils.showErrorToast(it.msg)
                OnBuriedPointManager.get().getOnBuriedPointManager()
                    ?.FillInfo_FillInfo(
                        "",
                        et_user_name.text.toString().trim(),
                        et_recommend_code.text.toString().trim(),
                        "1"
                    )
            })
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {//不能反悔取消
            return true
        }
        return super.onKeyDown(keyCode, event)
    }


    fun findById() {
        tv_carry_out.setOnClickListener(this)
    }

    fun init() {
        //        //宽度全屏显示
        val layoutParams = window?.getAttributes()
        layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
        window?.setAttributes(layoutParams)
        StatusBarUtils.setImmersiveStatus(window, false)
        val mSafeMutableLiveData = SafeMutableLiveData<ErrorMessage>()
        mUserViewModel?.registerToListen(mSafeMutableLiveData)
        mSafeMutableLiveData.observe(this, Observer {
            ToastUtils.showErrorToast(it.msg)
        })
    }

    override fun dismiss() {
        super.dismiss()
        mUserInfoInputHelp?.onDestroy()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_carry_out -> {
                val user_name = et_user_name.text.toString().trim()
                if (TextUtils.isEmpty(user_name)) {
                    ToastUtils.showToash(i18n(R.string.input_name))
                    return
                }
                val code = et_recommend_code.text.toString().trim()
                if (TextUtils.isEmpty(code)) {
                    ToastUtils.showToash(i18n(R.string.input_yq_code))
                    return
                }
                mUserViewModel?.saveNameRecommend(user_name, code)
                    ?.observe(this@UserInfoInputDialog, Observer {
                        ToastUtils.showSucessToast("设置成功")
                        OnBuriedPointManager.get().getOnBuriedPointManager()
                            ?.FillInfo_FillInfo("", user_name, code, "1")
                        dismiss()
                    })
                // dismiss()
            }
        }
    }

    interface OnClickCrrayOutLinsener {
        fun onClickCrralyOut(name: String, code: String)
    }

    override fun getLifecycle(): Lifecycle {
        return mActivity!!.lifecycle
    }

}