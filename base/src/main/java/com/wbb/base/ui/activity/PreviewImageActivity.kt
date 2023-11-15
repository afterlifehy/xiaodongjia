package com.wbb.base.ui.activity

import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Route
import com.wbb.base.viewbase.BaseDataActivityKt
import com.wbb.base.mvvm.base.BaseViewModel
import com.github.chrisbanes.photoview.PhotoView
import com.wbb.base.BaseApplication
import com.wbb.base.R
import com.wbb.base.ext.i18n
import com.wbb.base.help.ActivityCacheManager
import com.wbb.base.util.*
import kotlinx.android.synthetic.main.activity_preview_image.*
import java.util.ArrayList

/**
 * Created by hy on 2021/1/29.
 */
@Route(path = ARouterMap.base.PREVIEW_IMAGE)
class PreviewImageActivity : BaseDataActivityKt<BaseViewModel>(), View.OnClickListener {
    var index = 0
    var imageList: ArrayList<String> = ArrayList()
    var samplePagerAdapter: SamplePagerAdapter? = null

    override fun onReloadData() {
    }

    override fun onGetClassTypeNam(): Any {
        return "图片预览"
    }

    override val isLoadNotData: Boolean
        get() = false
    override val isShowTitle: Boolean
        get() = false

    override fun getLayoutResId(): Int {
        return R.layout.activity_preview_image
    }

    override fun initView() {
        index = intent.getIntExtra("index", 0)
        imageList = intent.getSerializableExtra("data") as ArrayList<String>
//        //设置当前窗体为全屏显示
        StatusBarUtils.setTranslucentStatus(this)

        samplePagerAdapter = SamplePagerAdapter(this, imageList)
        hvp_viewpager.adapter = samplePagerAdapter
        hvp_viewpager.currentItem = index
        hvp_viewpager.offscreenPageLimit = imageList.size
        rtv_indicator.text = (index + 1).toString() + " / " + imageList!!.size

        hvp_viewpager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                rtv_indicator.text = (position + 1).toString() + " / " + imageList!!.size
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        hvp_viewpager!!.setOnClickListener(this)
    }

    override fun initData() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.pv_img -> {
                onBackPressedSupport()
            }
            R.id.hvp_viewpager -> {
                onBackPressedSupport()
            }
        }
    }

    class SamplePagerAdapter(
        var onClickListener: View.OnClickListener?,
        var list: ArrayList<String>?
    ) : PagerAdapter() {

        var saveBuilder: AlertDialog.Builder? = null
        var bitmap: Bitmap? = null
        var drawable: Drawable? = null

        private val onDismissListener = DialogInterface.OnDismissListener {
            if (null != cacheView) {
                cacheView.isEnabled = true
            }
        }

        private val cacheView: View? = null
        var onLongClickListener: View.OnLongClickListener? = View.OnLongClickListener { v ->
            drawable = (v as PhotoView).drawable
            if (drawable is BitmapDrawable) {
                bitmap = (drawable as BitmapDrawable).bitmap
                saveBuilder!!.show()
            }
            true
        }

        override fun getCount(): Int {
            return if (list == null) 1 else list!!.size
        }

        init {
            saveBuilder =
                ActivityCacheManager.instance().getLastActivity()?.let { AlertDialog.Builder(it) }
            saveBuilder!!.setMessage(i18n(R.string.保存图片))
            saveBuilder!!.setPositiveButton(i18n(R.string.确定)) { dialog, _ ->
                dialog.dismiss()
                FileAccessor.saveImageToGallery(bitmap)
                i18n(R.string.保存成功)?.let { ToastUtils.showSucessToast(it) }
            }
            saveBuilder!!.setOnDismissListener(onDismissListener)
        }

        override fun instantiateItem(container: ViewGroup, position: Int): View {
            val convertView =
                View.inflate(BaseApplication.instance(), R.layout.item_preview_img, null)
            if (list != null) {
                var url = ""
                url = list!![position]
                GlideUtil.loadImagePreview(url, convertView.findViewById(R.id.pv_img))
            }
            container.addView(
                convertView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            convertView.findViewById<PhotoView>(R.id.pv_img)
                .setOnLongClickListener(onLongClickListener)
            convertView.findViewById<PhotoView>(R.id.pv_img).setOnClickListener(onClickListener)
            return convertView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val contentView = `object` as View
            container.removeView(contentView)
            if (`object` is PhotoView) {
                val s = `object`
                val bitmapDrawable = s.drawable as BitmapDrawable
                val bm = bitmapDrawable.bitmap
                if (bm != null && !bm.isRecycled) {
                    s.setImageResource(0)
                    bm.recycle()
                }
            }
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }
    }

    override fun onBackPressedSupport() {
        super.onBackPressedSupport()
    }

    override fun onDestroy() {
        super.onDestroy()
        imageList.clear()
        samplePagerAdapter!!.list!!.clear()
        hvp_viewpager!!.setOnClickListener(null)
        hvp_viewpager!!.setOnLongClickListener(null)
        samplePagerAdapter!!.saveBuilder = null
        if (samplePagerAdapter!!.bitmap != null) {
            samplePagerAdapter!!.bitmap!!.recycle()
            samplePagerAdapter!!.bitmap = null
        }
        samplePagerAdapter!!.drawable = null
        samplePagerAdapter!!.list = null
        samplePagerAdapter!!.onClickListener = null
        samplePagerAdapter!!.onLongClickListener = null
        samplePagerAdapter = null
        hvp_viewpager!!.destroyDrawingCache()
        System.gc()
    }
}