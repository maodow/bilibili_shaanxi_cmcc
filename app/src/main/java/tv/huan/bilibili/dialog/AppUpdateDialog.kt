package tv.huan.bilibili.dialog

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import lib.kalu.mediaplayer.util.ToastUtils
import tv.huan.bilibili.BuildConfig
import tv.huan.bilibili.R
import tv.huan.bilibili.contract.AppUpdateDialogContract
import tv.huan.bilibili.help.ConfigHelp
import tv.huan.bilibili.upgrade.AppSystem
import tv.huan.bilibili.upgrade.AppUpdateHelp
import tv.huan.bilibili.upgrade.DownloadFileInfo
import tv.huan.bilibili.upgrade.DownloadFileManager
import tv.huan.bilibili.utils.LogUtil
import tv.huan.bilibili.utils.PathUtils
import tv.huan.bilibili.utils.StringUtils
import java.io.File

class AppUpdateDialog(context: Context) : BaseDialog(context, BaseDialog.POP_UP_THEME), AppUpdateDialogContract {

    private val TAG = AppUpdateDialog::class.java.name
    private val FILE_NAME = "TvTencentVideo.apk"
    private var updateVersion: TextView? = null
    private var updateContent: TextView? = null
    private var btnParent: LinearLayout? = null
    private var btnSure: Button? = null
    private var btnCancel: Button? = null
    private var progress: ProgressBar? = null
    private var progressHint: TextView? = null
    private var downloadManager: DownloadFileManager? = null


    override fun onLayoutId(): Int = R.layout.dialog_app_update

    init {
        updateVersion = findViewById<TextView>(R.id.update_version)
        updateContent = findViewById<TextView>(R.id.update_content)

        btnParent = findViewById<LinearLayout>(R.id.button_parent)
        btnSure = findViewById<Button>(R.id.btn_sure)
        btnCancel = findViewById<Button>(R.id.btn_cancel)

        progress = findViewById<ProgressBar>(R.id.updata_progress)
        progressHint = findViewById<TextView>(R.id.updata_progress_hint)

        btnSure?.setOnClickListener {
            requestUpdate()
        }
        btnCancel?.setOnClickListener {
            dismiss()
//            RxBus.getInstance().post(RxBusTag.UPDATE_CODE) //全屏播放页面开始播放
        }
    }

    private fun getCheckForUpdates() {
        val upgrade = ConfigHelp.INSTANCE.getSettings()?.upgrade
        if (upgrade != null && upgrade?.update
            && upgrade.versionCode > BuildConfig.VERSION_CODE
            && !StringUtils.isTrimEmpty(
                upgrade?.url
            )
        ) {
            updateVersion?.setText("V${upgrade.versionName}")
            if (!StringUtils.isTrimEmpty(upgrade.updateContent))
                updateContent?.setText(upgrade.updateContent.replace("\\n", "\n"))
//            if (upgrade?.isForcedUpdating) {
//                btnCancel?.visibility = View.GONE
//            }
            btnCancel?.visibility = View.VISIBLE
            show()
        } else {
            AppUpdateHelp.isUpdate = false
            AppUpdateHelp.INSTANCE.onDestroy()
        }
    }

    override fun show() {
        super.show()
        if (btnParent != null && !btnParent!!.isShown()) {
            btnParent?.visibility = View.VISIBLE
            progress?.visibility = View.GONE
            progressHint?.visibility = View.GONE
        }
    }

    private fun requestUpdate() {
        btnParent?.visibility = View.GONE
        progress?.visibility = View.VISIBLE
        progressHint?.visibility = View.VISIBLE
        val upgrade = ConfigHelp.INSTANCE.getSettings()?.upgrade
        if (upgrade?.url == null) return
        var filePath = PathUtils.getCachePathExternalFirst()
        LogUtil.log(TAG, ">>>>>>>>>>uri: ${upgrade?.url}")

        var fileInfo =
            DownloadFileInfo(upgrade?.url, filePath + "${File.separator}download", FILE_NAME)
        downloadManager = DownloadFileManager(fileInfo)
        downloadManager?.setOnDowloadListener(object : DownloadFileManager.OnDowloadListener {
            override fun onError(mag: String?) {
                LogUtil.log(">>>>>>error update: $mag")
                btnParent?.post {
                    btnParent?.requestFocusFromTouch()
                    btnSure?.setText("重新下载")
                    btnParent?.visibility = View.VISIBLE
                    progress?.visibility = View.GONE
                    progressHint?.visibility = View.GONE
                }
            }

            override fun onPosition(progress: Int) {
                progressHint?.post {
                    this@AppUpdateDialog.progress?.progress = progress
                    progressHint?.setText("$progress%")
                }
            }

            override fun onComplete(file: File?) {
                dismiss()
                LogUtil.log(">>>>>>update: ${file?.absolutePath}")
                AppSystem.installApp(file)
            }
        })
        downloadManager?.start()
    }

    override fun dismiss() {
        super.dismiss()
        downloadManager?.stop()
        AppUpdateHelp.INSTANCE.onDestroy()
    }

    fun showCoerceUpdate() {
        val upgrade = ConfigHelp.INSTANCE.getSettings()?.upgrade
        if (upgrade != null
            && upgrade.versionCode > BuildConfig.VERSION_CODE
            && !StringUtils.isTrimEmpty(
                upgrade?.url
            )
        ) {
            updateVersion?.setText("V${upgrade.versionName}")
            if (!StringUtils.isTrimEmpty(upgrade.updateContent))
                updateContent?.setText(upgrade.updateContent.replace("\\n", "\n"))
            show()
        } else {
            ToastUtils.showToast(context, "没有要升级的版本")
        }
    }

    override fun showDetectionUpdate() {
        if (mContext == null || (mContext as Activity).isFinishing) return
        getCheckForUpdates()
    }

    override fun hideWindown() {
        dismiss()
    }

    override fun isUpdate(): Boolean {
        val upgrade = ConfigHelp.INSTANCE.getSettings()?.upgrade
        if (upgrade != null
            && upgrade.update
            && upgrade.versionCode > BuildConfig.VERSION_CODE
            && !StringUtils.isTrimEmpty(
                upgrade?.url
            )
        )
            return true
        return false
    }

}