package com.wbb.base.ext

import com.wbb.base.BaseApplication


fun Any.i18n(res: Int): String {
    return BaseApplication.instance().resources.getString(res)
}
