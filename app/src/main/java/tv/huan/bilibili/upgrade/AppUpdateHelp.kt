package tv.huan.bilibili.upgrade

import android.app.Activity
import android.content.Context
import androidx.annotation.Keep
import tv.huan.bilibili.bean.ServerSettingData
import tv.huan.bilibili.contract.AppUpdateDialogContract
import tv.huan.bilibili.utils.NullUtis
import tv.huan.bilibili.utils.StringUtils
//import tv.huan.common.constant.hw.ActivityPath
//import tv.huan.common.utils.arouter.ARouterUtils

/**
 * 获取APK更新对象
 */
@Keep
class AppUpdateHelp private constructor() {
    private var newInstance: AppUpdateDialogContract? = null
    private var mSettings: ServerSettingData? = null
    private var popUp = false // 活动已经弹过

    companion object {
        var isUpdate = true
        val INSTANCE: AppUpdateHelp by lazy {
            AppUpdateHelp()
        }
    }

    fun getAppUpdate(context: Context): AppUpdateDialogContract? {
//        if (!isUpdate || newInstance != null || context::class.java.name.contains("WelcomeActivity")
//            || (context!! as Activity).isFinishing
//        ) {
//            getPopUp(context)
//            return newInstance
//        }

        try {
            if (newInstance == null) {
                val aClass = Class.forName("tv.huan.bilibili.dialog.AppUpdateDialog")
                val constructor = aClass.getConstructor(Context::class.java)
                val new = constructor.newInstance(context)
                if (new != null)
                    newInstance = new as AppUpdateDialogContract
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (!isUpdate()) {
            getPopUp(context)
        }
        return newInstance
    }

    fun isUpdate(): Boolean {
        return NullUtis.null2False(newInstance?.isUpdate())
    }

    /**
     * 防止内存泄露
     */
    fun onDestroy() {
        newInstance = null
    }

    fun onAppExit(){
        popUp = false
    }


    fun setConfigInfo(mSettings: ServerSettingData) {
        this.mSettings = mSettings
    }

    
    /**
     * 是否有活动
     */
    private fun getPopUp(context: Context) {
        if (/*StringUtils.isTrimEmpty(mSettings?.popup) || */popUp
            || context::class.java.name.contains("WelcomeActivity")
            || context::class.java.name.contains("FullScreenActivity")
        ) return
        popUp = true
//        ARouterUtils.newIntent(ActivityPath.COMMON_WEB_ACTIVITY)
//            .putExtra("url", mSettings?.popup)
//            .putExtra("from", "hd")
//            .startActivity(context)
    }
}