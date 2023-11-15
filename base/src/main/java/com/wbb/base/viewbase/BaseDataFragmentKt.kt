package com.chouyou.waterbridge.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.chouyou.base.base.BaseCommentLinsener
import com.chouyou.base.base.BaseViewAddManagers
import com.chouyou.base.base.BaseViewAddManagersImpl
import com.chouyou.base.base.NewBaseFragment
import com.wbb.base.mvvm.base.BaseViewModel
import com.wbb.base.R
import com.wbb.base.bean.NetWorkRequestData
import com.wbb.base.ext.i18N
import com.wbb.base.wiget.PagerStatesView
import kotlinx.android.synthetic.main.base_title_layout.*
import java.util.*

/**
 * Created by zj on 2019/12/23.
 */
abstract class BaseDataFragmentKt<VM : BaseViewModel> : NewBaseFragment<VM>(), View.OnTouchListener,
    BaseCommentLinsener, PagerStatesView.OnPagerClickLinsener {
    protected var mViewAddManager: BaseViewAddManagers? = null

    init {
        mViewAddManager = BaseViewAddManagersImpl()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (mRoot != null && mRoot?.parent != null) {
            val parent = mRoot?.parent as ViewGroup
            parent.removeView(mRoot)
        } else { //加一层布局
            val mBinView = getBindingView()
            if (mBinView == null) {
                mRoot = mViewAddManager?.getRootViewId(requireContext(), getLayoutResId())!!
            } else {
                mRoot = mViewAddManager?.getRootView(requireContext(), mBinView)!!

            }
            mViewAddManager?.setIsShowTitle(isShowTitle)
            mViewAddManager?.setIsLoadNotData(isLoadNotData)
            savedInstanceState?.let { }
            // initPlayInfo(mRootView)
            mInflater = inflater
            // Get savedInstanceState
            savedInstanceState?.let { onRestartInstance(it) }
        }
        return mRoot
    }

    open fun getBindingView(): View? {
        return null
    }

    protected abstract fun onReloadData()

    /**
     * 是否需要添加暂无数据
     *
     * @return
     */
    abstract val isLoadNotData: Boolean

    /**
     * 是否需要显示title
     */
    abstract val isShowTitle: Boolean
    override fun currentNewWorkState(isNetWork: Boolean) {
        super.currentNewWorkState(isNetWork)
        if (isNetWork) {
            // mViewAddManager?.setNetWorkStatesView(false)

        } else {
            //   mViewAddManager?.setNetWorkStatesView(true)
        }
    }

    /**
     * 适配齐刘海
     */
    fun setSerHeight() {
        if (isShowTitle) {
            val resourceId =
                context?.resources?.getIdentifier("status_bar_height", "dimen", "android")
            val viewHeight = context?.resources?.getDimensionPixelSize(resourceId!!)
            val layoutParams: ViewGroup.LayoutParams = view_title_top!!.getLayoutParams()
            layoutParams.height = viewHeight!!
            view_title_top.setLayoutParams(layoutParams)
        }
    }

    /**
     * 如果有分页，统一关闭一些加载效果
     */
    open fun hindLoadStatus() {
        hideLoadData()
    }

    /**
     * 设置左边控件
     */
    override fun setRightIcon(icon: Int, onCLickLinsenr: View.OnClickListener) {
        mViewAddManager?.setRightIcon(icon, onCLickLinsenr)
    }

    /**
     * 判断当前是否暂无数据了
     *
     * @return
     */
    override fun notDataIsVisib(): Boolean {
        return mViewAddManager?.notDataIsVisib()!!
    }


    /**
     * @param textName
     */
    override fun setTitleName(title: String) {
        mViewAddManager?.setTitleName(title)
    }

    /**
     * 默认图标
     */
    override fun showPromptView(
        text: String,
        errorIcont: Int,
        mOnNotNetWorkClickLinsener: PagerStatesView.OnPagerClickLinsener?
    ) {
        mViewAddManager?.showPromptView(text, errorIcont, mOnNotNetWorkClickLinsener)

    }

    fun showNotDataView() {
        showPromptView(i18N(R.string.not_data), R.mipmap.not_new_work)
    }

    /**
     * 请求数据
     */
    open fun getData() {

    }

    /**
     * 显示加载失败，点击屏幕重试
     */
    fun showLoadError() {
        showPromptView(
            i18N(R.string.load_error_re),
            R.mipmap.not_new_work, this
        )
    }

    /**
     * 隐藏我的
     */
    override fun hidePromptView() {
        mViewAddManager?.hidePromptView()
    }

    /**
     * 暂无网络
     */
    override fun showNewWorkError(mOnNotNetWorkClickLinsener: PagerStatesView.OnPagerClickLinsener?) {
        mViewAddManager?.showNewWorkError(this)
    }

    /**
     * 隐藏掉暂无网络
     */
    override fun goneNewWorkError() {
        mViewAddManager?.goneNewWorkError()
    }


    /**
     * 加载动画
     */
    override fun showLoadData() {
        mViewAddManager?.showLoadData()
    }


    override fun onPause() {
        super.onPause()
        onMyPause()
    }

    /**
     * 隐藏加载框A
     */
    override fun hideLoadData() {
        mViewAddManager?.hideLoadData()
    }

    override fun onMyPause() {
        mViewAddManager?.onMyPause()
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        onMyTouch()
        if (null != activity?.currentFocus) {
            /**
             * 点击空白位置 隐藏软键盘
             */
            val mInputMethodManager =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            return mInputMethodManager.hideSoftInputFromWindow(
                activity?.currentFocus!!.windowToken,
                0
            )
        }
        return false
    }

    /**
     * 点击了布局
     */
    fun onMyTouch() { //点击其他地方的时候，EditText失去焦点
        mRoot?.isFocusable = true
        mRoot?.isFocusableInTouchMode = true
        mRoot?.requestFocus()
    }

    protected open fun getFragment(tag: String?): Fragment? {
        val fragmentManager = Objects.requireNonNull(requireActivity()).supportFragmentManager
            ?: return null
        val fragment = fragmentManager.findFragmentByTag(tag)
        return if (fragment is Fragment) fragment else null
    }

    override fun onNetWorkRequestError(errror: NetWorkRequestData) {
        showLoadError()
//        showNotDataView()
    }

    override fun onNotNetWorkErrror(errror: NetWorkRequestData) {
        showNewWorkError()
    }

    override fun onPagerClick() {
        onReloadData()
        getData()
        hidePromptView()
    }
}