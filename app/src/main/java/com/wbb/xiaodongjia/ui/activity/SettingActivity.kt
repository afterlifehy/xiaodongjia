package com.wbb.xiaodongjia.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.wbb.base.dialog.DialogHelp
import com.wbb.base.ext.i18N
import com.wbb.base.mvvm.base.BaseViewModel
import com.wbb.base.util.ARouterMap
import com.wbb.base.util.CleanDataUtil
import com.wbb.base.util.ToastUtils
import com.wbb.base.viewbase.BaseDataActivityKt
import com.wbb.xiaodongjia.R
import com.wbb.base.util.UserInfoManager
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.layout_comment_toolbar.*

/**
 * Created by hy on 2021/3/1.
 */
@Route(path = ARouterMap.SETTING)
class SettingActivity : BaseDataActivityKt<BaseViewModel>(), View.OnClickListener {
    override fun onReloadData() {

    }

    override val isLoadNotData: Boolean
        get() = false
    override val isShowTitle: Boolean
        get() = false

    override fun getLayoutResId(): Int {
        return R.layout.activity_setting
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        tv_title.text = i18N(R.string.设置)
        tv_cache.text = CleanDataUtil.getTotalCacheSize(this)
        startListener()
    }

    private fun startListener() {
        iv_back.setOnClickListener(this)
        rl_clear.setOnClickListener(this)
        tv_logOut.setOnClickListener(this)
    }

    override fun initData() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                onBackPressedSupport()
            }
            R.id.rl_clear -> {
                CleanDataUtil.clearAllCache(this)
                tv_cache.text = CleanDataUtil.getTotalCacheSize(this)
                ToastUtils.showSucessToast(i18N(R.string.清除缓存成功))
            }
            R.id.tv_logOut -> {
                DialogHelp.Builder.setTitle(i18N(R.string.是否退出登录)).setContentMsg("").setLeftMsg(i18N(R.string.取消)).setRightMsg(i18N(R.string.确定))
                    .setOnButtonClickLinsener(object : DialogHelp.OnButtonClickLinsener {
                        override fun onLeftClickLinsener(msg: String) {

                        }

                        override fun onRightClickLinsener(msg: String) {
                            UserInfoManager.instance().clearUserInfo()
                            ARouter.getInstance().build(ARouterMap.LOGIN).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                            finish()
                        }

                    }).build(this@SettingActivity).showDailog()
            }
        }
    }

    override fun onGetClassTypeNam(): Any {
        return "设置"
    }

}