package com.wbb.xiaodongjia.utils

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.widget.TextView
import androidx.core.content.FileProvider
import com.github.dfqin.grantor.PermissionListener
import com.github.dfqin.grantor.PermissionsUtil
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.wbb.base.BaseApplication.Companion.instance
import com.wbb.base.bean.UpdateBean
import com.wbb.base.ext.i18n
import com.wbb.base.help.ActivityCacheManager
import com.wbb.base.util.AppUtil
import com.wbb.base.util.FileAccessor
import com.wbb.base.util.ToastUtils
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.dialog.CheckUpdateDialog
import com.wbb.xiaodongjia.ui.activity.MainActivity
import rx.Observable
import rx.Subscriber
import rx.schedulers.Schedulers
import java.io.File
import java.util.*

/**
 * Created by kxrt_android_03 on 2017/4/20.
 */
class CheckVersionUtil {
    private var downloadFiles: ArrayList<String>? = ArrayList()
    private var isShow = false
    var progressPopX = 0f
    var updateBean: UpdateBean? = null
    var lastUpdataTime = 0L
    private var checkUpdateDialog: CheckUpdateDialog? = null
    private var context: Activity? = null
    fun init(mainActivity: MainActivity?, updateBean: UpdateBean) {
        context = mainActivity
        this.updateBean = updateBean
        checkUp()
    }

    private fun checkUp() {
//        if (System.currentTimeMillis() - lastUpdataTime >= 5 * 60 * 1000) {
//            lastUpdataTime = System.currentTimeMillis();
        checkNewVersion()
//        } else {
//            Log.e("check", "两次间隔必须小5分钟");
//        }
    }

    fun checkNewVersion() {
        downloadFiles?.clear()
        Log.e("check", "start check self")
        checkSelfVersion()
    }

    private fun checkSelfVersion() {
//        SysHttpUtil.INSTANCE.Update(new HttpHandler() {
//            @Override
//            public void requestSuccess(JSONObject json) {
//                if (json.getInteger("code") == 200) {
//                    updateBean = GsonUtils.fromJson(json.getString("result"), UpdateBean.class);
        if (TextUtils.equals(updateBean?.packageName, instance().packageName)) {
            if (updateBean?.id?.let { AppUtil.checkAppViewsionCode(it) }!!) {
                isShow = updateBean?.forceUpdate != 0
                checkUpdateDialog = CheckUpdateDialog(ActivityCacheManager.instance().getLastActivity()!!, updateBean?.content.toString(), isShow, object : CheckUpdateDialog.UpdateCallback {
                    override fun updateNow() {
                        PermissionsUtil.requestPermission(
                            instance(), object : PermissionListener {
                                override fun permissionGranted(permission: Array<out String>) {
                                    download()
                                }

                                override fun permissionDenied(permission: Array<out String>) {
                                    ToastUtils.showToash(i18n(R.string.暂无权限))
                                }
                            },
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    }
                })
                checkUpdateDialog?.setCancelable(!isShow)
                checkUpdateDialog?.show()
            }
        }
    }

    fun download() {
        var file = File(FileAccessor.getDownLoadPathName(), FileAccessor.getFileName(updateBean?.url))
        if (file.exists()) {
            file.delete()
        }
        updateBean?.url?.let { download(it) }
    }

    private var file: File? = null
    private fun download(url: String) {
        file = File(FileAccessor.getDownLoadPathName(), FileAccessor.getFileName(url))
        progressPopX = AppUtil.dp2px(20f).toFloat()
        checkUpdateDialog?.startUpdate()
        FileDownloader.getImpl()
            .create(url)
            .setPath(file!!.absolutePath)
            .setAutoRetryTimes(Int.MAX_VALUE)
            .setListener(fileDownloadListener).start()
    }

    var fileDownloadListener: FileDownloadListener = object : FileDownloadListener() {
        override fun pending(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {}
        override fun progress(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
            checkUpdateDialog?.getSeekBar()?.incrementProgressBy(1)
            checkUpdateDialog?.getSeekBar()?.progress = ((soFarBytes * 100f) / totalBytes).toInt()
            checkUpdateDialog?.getProgressPop()?.x = progressPopX + (checkUpdateDialog?.getSeekBar()?.width!! - progressPopX * 2) * (((soFarBytes * 100f) / totalBytes) / 100f)
            checkUpdateDialog?.getProgressPop()?.invalidate()
            checkUpdateDialog?.getProgressPop()?.findViewById<TextView>(R.id.tv_progress)?.text = ((soFarBytes * 100f) / totalBytes).toInt().toString()
        }

        override fun completed(task: BaseDownloadTask) {
            checkUpdateDialog!!.dismiss()
            downloadFiles!!.add(task.path)
            startInstallAPK(downloadFiles)
        }

        override fun paused(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {}
        override fun error(task: BaseDownloadTask, e: Throwable) {}
        override fun warn(task: BaseDownloadTask) {}
    }

    fun startInstallAPK(files: ArrayList<String>?) {
        Observable.just(files)
            .filter { t -> t != null && t.size > 0 }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(object : Subscriber<ArrayList<String>>() {
                override fun onNext(t: ArrayList<String>?) {
                    if (file == null) {
                        file = File(FileAccessor.getDownLoadPathName(), FileAccessor.getFileName(t?.get(0)))
                    }
                    installApk(file)
                }

                override fun onCompleted() {
                }

                override fun onError(e: Throwable?) {
                    e!!.printStackTrace()
                }

            })
    }

    protected fun installApk(file: File?) {
        val intent = Intent()
        //执行动作
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.action = Intent.ACTION_VIEW
            //执行的数据类型`
            intent.setDataAndType(
                FileProvider.getUriForFile(instance(), "com.wbb.xiaodongjia.fileprovider", file!!),
                "application/vnd.android.package-archive"
            )
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
        }
        instance().startActivity(intent)
    }

    companion object {
        private const val lastUpdataTime: Long = 0
        private var checkVersionUtil: CheckVersionUtil? = null

        //    private UpdateBean updateBean = null;
        val instance: CheckVersionUtil?
            get() {
                if (checkVersionUtil == null) {
                    checkVersionUtil = CheckVersionUtil()
                }
                return checkVersionUtil
            }
    }
}