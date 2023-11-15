package com.wbb.xiaodongjia.ui.activity

import android.Manifest
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Route
import com.aries.ui.util.StatusBarUtil
import com.github.dfqin.grantor.PermissionListener
import com.github.dfqin.grantor.PermissionsUtil
import com.tmall.ultraviewpager.UltraViewPager
import com.tmall.ultraviewpager.transformer.UltraScaleTransformer
import com.uuzuche.lib_zxing.activity.CodeUtils
import com.wbb.base.BaseApplication
import com.wbb.base.buriedpoint.OnBuriedPointManager
import com.wbb.base.emuns.SHARE_EMUN
import com.wbb.base.ext.i18N
import com.wbb.base.help.WxSharManager
import com.wbb.base.mvvm.base.BaseViewModel
import com.wbb.base.pf.PreferencesHelp
import com.wbb.base.util.*
import com.wbb.base.viewbase.BaseDataActivityKt
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.adapter.PosterAdapter
import com.wbb.xiaodongjia.databinding.ItemPosterShotBinding
import com.wbb.xiaodongjia.dialog.SelectPicDialog
import kotlinx.android.synthetic.main.activity_poster.*


/**
 * Created by hy on 2021/3/12.
 */
@Route(path = ARouterMap.POSTER)
class PosterActivity : BaseDataActivityKt<BaseViewModel>(), View.OnClickListener,
    WxSharManager.OnShareResultLinsener {
    var posterAdapter: PosterAdapter? = null
    var posterList: MutableList<Int> = ArrayList()
    var currentPostion = 0

    override fun onReloadData() {

    }

    override val isLoadNotData: Boolean
        get() = false
    override val isShowTitle: Boolean
        get() = false

    override fun getLayoutResId(): Int {
        return R.layout.activity_poster
    }

    override fun initView() {
//        posterList = intent.getStringArrayListExtra(ARouterMap.POSTER_LIST)!!
        StatusBarUtils.setTranslucentStatus(this)
        val lp = abl_toolbar.layoutParams as RelativeLayout.LayoutParams
        lp.topMargin = StatusBarUtil.getStatusBarHeight()
        abl_toolbar.layoutParams = lp

        posterList.add(R.mipmap.ic_poster_1)
        uvp_poster.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL)
        uvp_poster.setMultiScreen(0.78f)
        uvp_poster.setItemRatio(0.6)
        uvp_poster.setAutoMeasureHeight(true)
        uvp_poster?.setInfiniteLoop(false)
        uvp_poster?.setOffscreenPageLimit(3)
        uvp_poster?.setPageTransformer(false, UltraScaleTransformer())
        posterAdapter = PosterAdapter(posterList)
        uvp_poster?.adapter = posterAdapter
        uvp_poster?.setCurrentItem(0, true)

        uvp_poster.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                currentPostion = position
            }

        })
        startListener()
    }

    private fun startListener() {
        iv_right.setOnClickListener(this)
        iv_share.setOnClickListener(this)
        iv_download.setOnClickListener(this)
    }

    override fun initData() {
    }

    override fun onGetClassTypeNam(): Any {
        return "海报"
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_right -> {
                onBackPressedSupport()
            }
            R.id.iv_share -> {
                PermissionsUtil.requestPermission(
                    BaseApplication.instance(), object : PermissionListener {
                        override fun permissionGranted(permission: Array<out String>) {
                            val mItemPosterShotBinding = ItemPosterShotBinding.inflate(layoutInflater)
                            val item_poster: View = mItemPosterShotBinding.root
                            layoutView(item_poster, AppUtil.getScreanWidth(), AppUtil.getScreanWidth() * 5 / 3)
                            val poster = posterList[currentPostion]
                            val bitmap = BitmapFactory.decodeResource(resources, poster, null)
                            mItemPosterShotBinding.rivPoster.setImageBitmap(bitmap)
                            val qrBitmap = CodeUtils.createImage(Constant.POST_SHARE + "?code=${PreferencesHelp.getINVITE_CODE()}", AppUtil.dip2px(72f), AppUtil.dip2px(72f), null)
                            mItemPosterShotBinding.ivScan.setImageBitmap(qrBitmap)
                            val selectedBitmap = AppUtil.getViewBp(item_poster)
                            selectedBitmap?.let {
                                WxSharManager.instance().shareBitmapToWx(this@PosterActivity, it, Constant.POST_SHARE + "?code=${PreferencesHelp.getINVITE_CODE()}", this@PosterActivity)
                            }
                        }

                        override fun permissionDenied(permission: Array<out String>) {
                            ToastUtils.showToash("没有读写权限")
                        }
                    },
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                )
            }
            R.id.iv_download -> {
                PermissionsUtil.requestPermission(
                    BaseApplication.instance(), object : PermissionListener {
                        override fun permissionGranted(permission: Array<out String>) {
                            val mItemPosterShotBinding = ItemPosterShotBinding.inflate(layoutInflater)
                            val item_poster: View = mItemPosterShotBinding.root
                            layoutView(item_poster, AppUtil.getScreanWidth(), AppUtil.getScreanWidth() * 5 / 3)
                            val poster = posterList[currentPostion]
                            val bitmap = BitmapFactory.decodeResource(resources, poster, null)
                            mItemPosterShotBinding.rivPoster.setImageBitmap(bitmap)
                            val qrBitmap = CodeUtils.createImage(Constant.POST_SHARE + "?code=${PreferencesHelp.getINVITE_CODE()}", AppUtil.dip2px(72f), AppUtil.dip2px(72f), null)
                            mItemPosterShotBinding.ivScan.setImageBitmap(qrBitmap)
                            val selectedBitmap = AppUtil.getViewBp(item_poster)
                            FileAccessor.saveBitmap(selectedBitmap, FileAccessor.DCIM)
                            ToastUtils.showSucessToast(i18N(R.string.下载成功))
                        }

                        override fun permissionDenied(permission: Array<out String>) {
                            ToastUtils.showToash("没有读写权限，不能保存图片")
                        }
                    },
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                )
            }
        }
    }

    private fun layoutView(view: View, width: Int, height: Int) {
        // 指定整个View的大小 参数是左上角 和右下角的坐标
        view.layout(0, 0, width, height)
        val measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
        val measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST)
        /** 当然，measure完后，并不会实际改变View的尺寸，需要调用View.layout方法去进行布局。
         * 按示例调用layout函数后，View的大小将会变成你想要设置成的大小。
         */
        view.measure(measuredWidth, measuredHeight)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }

    override fun onShareSucess(mType: SHARE_EMUN) {
        OnBuriedPointManager.get().getOnBuriedPointManager()
            ?.SavePoster(mType.shareNam, "1", PreferencesHelp.getINVITE_CODE())
    }

    override fun onShareError(mType: SHARE_EMUN) {
        OnBuriedPointManager.get().getOnBuriedPointManager()
            ?.SavePoster(mType.shareNam, "0", PreferencesHelp.getINVITE_CODE())
    }

    override fun onShareCancer(mType: SHARE_EMUN) {
        OnBuriedPointManager.get().getOnBuriedPointManager()
            ?.SavePoster(mType.shareNam, "0", PreferencesHelp.getINVITE_CODE())
    }
}