package com.wbb.base.dialog

import android.content.Context
import com.wbb.base.R
import com.wbb.base.ext.i18n

/**
 * Created by zj on 2021/1/6.
 */
class DialogHelp {
    var title: String = ""
    var contentMsg: String = ""
    var leftMsg: String = ""
    var rightMsg: String = ""
    var mOnButtonClickLinsener: OnButtonClickLinsener? = null
    var cancelable = true
    var mGlobalDialog: GlobalDialog? = null
    private var context: Context? = null
    var isAloneButton = false
    fun initDailog() {
        mGlobalDialog = GlobalDialog(context!!, this)
    }

    /**
     * 开始弹窗
     */
    fun showDailog(): GlobalDialog? {
        mGlobalDialog?.show()
        return mGlobalDialog
    }

    private constructor(mBuilder: Builder, context: Context?) {
        this.title = Builder.getTitle()
        this.contentMsg = Builder.getContentMsg()
        this.leftMsg = Builder.getLeftMsg()
        this.rightMsg = Builder.getRightMsg()
        this.mOnButtonClickLinsener = Builder.getOnButtonClickLinsener()
        this.cancelable = Builder.getCancelable()
        this.context = context
        this.isAloneButton = Builder.getisAloneButton()
        initDailog()
    }

    object Builder {
        private var title: String = ""
        private var contentMsg: String = ""
        private var leftMsg: String = i18n(R.string.取消)!!
        private var rightMsg: String = i18n(R.string.确定)!!
        private var isAloneButton = false
        private var mOnButtonClickLinsener: OnButtonClickLinsener = object : OnButtonClickLinsener {
            override fun onLeftClickLinsener(msg: String) {
            }

            override fun onRightClickLinsener(msg: String) {
            }
        }
        private var cancelable = true

        fun getTitle(): String {
            return title
        }

        fun getContentMsg(): String {
            return contentMsg
        }

        fun getLeftMsg(): String {
            return leftMsg
        }

        fun getOnButtonClickLinsener(): OnButtonClickLinsener {
            return mOnButtonClickLinsener
        }

        fun getisAloneButton(): Boolean {
            return isAloneButton
        }

        fun isAloneButton(isAloneButton: Boolean): Builder {
            Builder.isAloneButton = isAloneButton
            return this
        }

        fun setOnButtonClickLinsener(mOnButtonClickLinsener: OnButtonClickLinsener): Builder {
            Builder.mOnButtonClickLinsener = mOnButtonClickLinsener
            return this
        }

        fun getRightMsg(): String {
            return rightMsg
        }

        fun getCancelable(): Boolean {
            return cancelable
        }

        fun setTitle(title: String): Builder {
            Builder.title = title
            return this
        }

        fun setContentMsg(contentMsg: String): Builder {
            Builder.contentMsg = contentMsg
            return this
        }

        fun setLeftMsg(leftMsg: String): Builder {
            Builder.leftMsg = leftMsg
            return this
        }

        fun setRightMsg(rightMsg: String): Builder {
            Builder.rightMsg = rightMsg
            return this
        }

        fun setCancelable(cancelable: Boolean): Builder {
            Builder.cancelable = cancelable
            return this
        }

        fun build(context: Context?): DialogHelp {
            return DialogHelp(this, context)
        }

    }

    interface OnButtonClickLinsener {
        fun onLeftClickLinsener(msg: String = "")
        fun onRightClickLinsener(msg: String = "")
    }
}