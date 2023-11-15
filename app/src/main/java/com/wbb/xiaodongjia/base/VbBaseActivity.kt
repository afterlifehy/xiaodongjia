package com.wbb.xiaodongjia.base

import android.view.View
import androidx.viewbinding.ViewBinding
import com.wbb.base.mvvm.base.BaseViewModel
import com.wbb.base.viewbase.BaseDataActivityKt
import com.wbb.xiaodongjia.R

/**
 * Created by zj on 2021/3/12.
 */
abstract class VbBaseActivity<VM : BaseViewModel, vb : ViewBinding> : BaseDataActivityKt<VM>() {
    lateinit var binding: vb

    override fun getBindingView(): View? {
        val mBindind = getVbBindingView()
        binding = mBindind as vb
        return mBindind.root
    }


    abstract fun getVbBindingView(): ViewBinding

    override fun getLayoutResId(): Int {
        return R.layout.activity_vb_default_layout
    }
}