package com.wbb.xiaodongjia.ui.activity

import android.Manifest
import android.os.Environment
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.github.dfqin.grantor.PermissionListener
import com.github.dfqin.grantor.PermissionsUtil
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.wbb.base.BaseApplication
import com.wbb.base.dialog.LoadingDialog
import com.wbb.base.ext.i18N
import com.wbb.base.mvvm.base.BaseViewModel
import com.wbb.base.util.*
import com.wbb.base.viewbase.BaseDataActivityKt
import com.wbb.xiaodongjia.R
import kotlinx.android.synthetic.main.activity_pdf.*
import kotlinx.android.synthetic.main.layout_comment_toolbar.*
import java.io.File

/**
 * Created by hy on 2021/3/12.
 */
@Route(path = ARouterMap.PDF)
class PdfActivity : BaseDataActivityKt<BaseViewModel>(), View.OnClickListener {
    var downloadUrl = ""
    private var file: File? = null
    var loadingDialog: LoadingDialog? = null

    override fun onReloadData() {

    }

    override val isLoadNotData: Boolean
        get() = false
    override val isShowTitle: Boolean
        get() = false

    override fun getLayoutResId(): Int {
        return R.layout.activity_pdf
    }

    override fun initView() {
        downloadUrl = intent.getStringExtra(ARouterMap.PDF_URL).toString()
        PermissionsUtil.requestPermission(
            BaseApplication.instance(), object : PermissionListener {
                override fun permissionGranted(permission: Array<out String>) {
                    val fileDir = File(Environment.getExternalStorageDirectory().toString() + FileAccessor.XDJ_PATH, "/file")
                    if (!fileDir.exists()) {
                        fileDir.mkdir()
                    }
                    download(downloadUrl)
                }

                override fun permissionDenied(permission: Array<out String>) {
                    ToastUtils.showToash(i18N(R.string.暂无权限))
                }
            },
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        iv_back.setOnClickListener(this)
    }

    override fun initData() {
    }

    private fun download(url: String) {
        file = File(FileAccessor.getDownLoadPathName(), FileAccessor.getFileName(url))
        FileDownloader.getImpl()
            .create(url)
            .setPath(file!!.absolutePath)
            .setAutoRetryTimes(Integer.MAX_VALUE)
            .setListener(fileDownloadListener).start()
        loadingDialog = null
        loadingDialog = LoadingDialog(this, i18N(R.string.下载进度) + "0%")
        loadingDialog?.showDialog()
    }

    var fileDownloadListener: FileDownloadListener = object : FileDownloadListener() {
        override fun pending(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
        }

        override fun progress(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
            loadingDialog?.updateContent(i18N(R.string.下载进度) + "${soFarBytes * 100 / totalBytes}%")
        }

        override fun completed(task: BaseDownloadTask) {
            loadingDialog?.closeDialog()
            pdf_view.fromFile(file)
//                        .pages(0, 2, 1, 3, 3, 3) // all pages are displayed by default
                .enableSwipe(true) // allows to block changing pages using swipe
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)
                // allows to draw something on the current page, usually visible in the middle of the screen
                .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
                .password(null)
                .scrollHandle(null)
                .enableAntialiasing(true) // improve rendering a little bit on low-res screens
                // spacing between pages in dp. To define spacing color, set view background
                .spacing(0)
//                    .autoSpacing(false) // add dynamic spacing to fit each page on its own on the screen
//                    .linkHandler(DefaultLinkHandler(pdf_whitePaper))
//                    .pageFitPolicy(FitPolicy.WIDTH)
//                    .pageSnap(true) // snap pages to screen boundaries
//                    .pageFling(false) // make a fling change only a single page like ViewPager
                .load()
        }

        override fun paused(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
        }

        override fun error(task: BaseDownloadTask, e: Throwable) {
            LogUtil.v(e.toString())
        }

        override fun warn(task: BaseDownloadTask) {
        }
    }

    override fun onGetClassTypeNam(): Any {
        return "pdf"
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                onBackPressedSupport()
            }
        }
    }
}