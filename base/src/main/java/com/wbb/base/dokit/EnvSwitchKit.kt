package com.wbb.base.dokit

import android.content.Context
import android.content.Intent
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.wbb.base.R
import com.wbb.base.ui.activity.ApiSwitchAtivity

class EnvSwitchKit: AbstractKit() {
    override val icon: Int
        get() = R.mipmap.ic_app_logo

    override val name: Int
        get() = R.string.环境切换

    override fun onAppInit(context: Context?) {

    }

    override fun onClick(context: Context?) {
        val intent = Intent(context, ApiSwitchAtivity::class.java)
        context?.startActivity(intent)
    }
}