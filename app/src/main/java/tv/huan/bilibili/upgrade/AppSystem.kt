package tv.huan.bilibili.upgrade

import android.app.Activity
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.Application
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Looper
import androidx.core.content.FileProvider
import tv.huan.bilibili.utils.PathUtils
import tv.huan.bilibili.utils.StringUtils
//import tv.huan.bilibili.utils.Thread2Utils
import tv.huan.bilibili.utils.Utils
import java.io.File

object AppSystem {

    fun getApp(): Application {
        return Utils.getApp()
    }


    /**
     * Get activity from context object
     *
     * @param context context
     * @return object of Activity or null if it is not Activity
     */
    fun scanForActivity(context: Context?): Activity? {
        if (context == null) return null
        if (context is Activity) {
            return context
        } else if (context is ContextWrapper) {
            return scanForActivity(
                context.baseContext
            )
        }
        return null
    }


//    abstract class Task<Result>(consumer: Consumer<Result>?) :
//        Thread2Utils.SimpleTask<Result>() {
//        private val mConsumer: Consumer<Result>?
//        override fun onSuccess(result: Result) {
//            if (mConsumer != null) {
//                mConsumer.accept(result)
//            }
//        }
//
//        init {
//            mConsumer = consumer
//        }
//    }

    interface Consumer<T> {
        fun accept(t: T)
    }


    /**
     * Return the application's version name.
     *
     * @return the application's version name
     */
    fun getAppVersionName(): String? {
        return getAppVersionName(getApp().getPackageName())
    }


    /**
     * Return the application's version name.
     *
     * @param packageName The name of the package.
     * @return the application's version name
     */
    fun getAppVersionName(packageName: String?): String? {
        return if (isSpace(packageName)) "" else try {
            val pm: PackageManager = getApp().getPackageManager()
            val pi = pm.getPackageInfo(packageName!!, 0)
            pi?.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            ""
        }
    }

    fun getAppVersionName(context: Context?, packageName: String?): String? {
        return if (context == null || isSpace(packageName)) "" else try {
            val pm: PackageManager = context!!.getPackageManager()
            val pi = pm.getPackageInfo(packageName!!, 0)
            pi?.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * Return the application's version code.
     *
     * @return the application's version code
     */
    fun getAppVersionCode(): Int {
        return getAppVersionCode(getApp().getPackageName())
    }

    /**
     * Return the application's version code.
     *
     * @param packageName The name of the package.
     * @return the application's version code
     */
    fun getAppVersionCode(packageName: String?): Int {
        return if (isSpace(packageName)) -1 else try {
            val pm: PackageManager = getApp().getPackageManager()
            val pi = pm.getPackageInfo(packageName!!, 0)
            pi?.versionCode ?: -1
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            -1
        }
    }


    private fun isSpace(s: String?): Boolean {
        if (s == null) return true
        var i = 0
        val len = s.length
        while (i < len) {
            if (!Character.isWhitespace(s[i])) {
                return false
            }
            ++i
        }
        return true
    }

    /**
     * 判断App是否是Debug版本
     *
     * @return `true`: 是<br></br>`false`: 否
     */
    fun isAppDebug(): Boolean {
        return if (StringUtils.isTrimEmpty(
                AppSystem.getApp().getPackageName()
            )
        ) false else try {
            val pm: PackageManager = AppSystem.getApp().getPackageManager()
            val ai = pm.getApplicationInfo(AppSystem.getApp().getPackageName(), 0)
            ai != null && ai.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Install the app.
     *
     * Target APIs greater than 25 must hold
     * `<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />`
     *
     * @param file The file.
     */
    fun installApp(file: File?) {
        val installAppIntent: Intent = getInstallAppIntent(file) ?: return
        getApp().startActivity(installAppIntent)
    }

    private fun getInstallAppIntent(file: File?): Intent? {
        if (!PathUtils.isFileExists(file)) return null
        val uri: Uri
        uri = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Uri.fromFile(file)
        } else {
            val authority: String =
                getApp().getPackageName().toString() + ".utilcode.provider"
            FileProvider.getUriForFile(getApp(), authority, file!!)
        }
        return getInstallAppIntent(uri)
    }

    /**
     * Return the intent of install app.
     *
     * Target APIs greater than 25 must hold
     * `<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />`
     *
     * @param uri The uri.
     * @return the intent of install app
     */
    private fun getInstallAppIntent(uri: Uri?): Intent? {
        if (uri == null) return null
        val intent = Intent(Intent.ACTION_VIEW)
        val type = "application/vnd.android.package-archive"
        intent.setDataAndType(uri, type)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        return intent
    }


    /**
     * 应用是否在前台运行
     * @return true 前台 false 后台
     */
    fun isAppForeground(): Boolean {
        val am = getApp()
            .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            ?: return true
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            val info = am.runningAppProcesses
            if (info == null || info.size == 0) return true
            for (aInfo in info) {
                if (aInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    if (aInfo.pkgList == null) return true
                    for (pkg in aInfo.pkgList) {
                        if (getApp()
                                .getPackageName() == pkg
                        ) return true
                    }
                }
            }
        } else {
            try {
                val componentName = am.getRunningTasks(1)[0].topActivity
                if (componentName!!.packageName == getApp()
                        .getPackageName()
                ) {
                    return true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return false
    }

    //判断app是否安装
    fun isInstall(intent: Intent): Boolean {
        return getApp().getPackageManager()
            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size > 0;
    }

    /**
     * 判断app是否安装
     * @param 包名
     */
    fun isInstall(packageName: String): Boolean {
        val pm = getApp().getPackageManager()
        return try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun getAppPackageName(): String? {
        return getApp().getPackageName()
    }

    fun getAppName(): String? {
        var packageManager: PackageManager? = null
        var applicationInfo: ApplicationInfo? = null
        try {
            packageManager = getApp().getPackageManager()
            applicationInfo = getAppPackageName()?.let { packageManager.getApplicationInfo(it, 0) }
        } catch (e: PackageManager.NameNotFoundException) {
            applicationInfo = null
        }
        return packageManager!!.getApplicationLabel(applicationInfo!!) as String
    }

    fun isTopActivity(activityName: String): Boolean {
        val am =
            getApp().getSystemService(ACTIVITY_SERVICE) as ActivityManager?
        val cn = am!!.getRunningTasks(1)[0].topActivity
        return cn!!.className.contains(activityName)
    }

    /**
     * 是否在主线程
     */
    fun isMainThread(): Boolean {
        return Looper.myLooper() == Looper.getMainLooper()
    }

    /**
     * 需要添加卸载权限
     */
    fun unInstallApp(context: Context, pkg: String) {
        val intent = Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + pkg));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}