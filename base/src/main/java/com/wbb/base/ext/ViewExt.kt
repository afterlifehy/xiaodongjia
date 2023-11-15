package com.chouyou.base.ext

import android.view.View
import android.widget.Checkable

/**
 * Copyright (C), 2019-2019, 里德软件
 * FileName: ViewExt
 * Author: OnionMac by 张琦
 * Date: 2019/4/23 5:49 PM
 * Description: View扩展
 */

fun View.gone(): View{
    this.visibility = View.GONE
    return this
}

fun View.show(): View{
    this.visibility = View.VISIBLE
    return this
}

fun View.hide(): View{
    this.visibility = View.INVISIBLE
    return this
}

fun Checkable.check(): Checkable{
    this.isChecked = true
    return this
}

fun Checkable.unCheck(): Checkable{
    this.isChecked = false
    return this
}
