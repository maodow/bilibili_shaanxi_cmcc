package tv.huan.bilibili.utils

import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.text.TextUtils
import tv.huan.bilibili.utils.Utils.getApp
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

object PathUtils {
    private val SEP = File.separatorChar

    /**
     * Return whether the file exists.
     *
     * @param file The file.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isFileExists(file: File?): Boolean {
        if (file == null) return false
        return if (file.exists()) {
            true
        } else isFileExists(file.absolutePath)
    }

    fun getFileByPath(filePath: String?): File? {
        return if (StringUtils.isSpace(filePath)) null else File(filePath)
    }

    /**
     * Return whether the file exists.
     *
     * @param filePath The path of file.
     * @return `true`: yes<br></br>`false`: no
     */
   private fun isFileExists(filePath: String): Boolean {
        val file: File = getFileByPath(filePath) ?: return false
        return if (file.exists()) {
            true
        } else isFileExistsApi29(filePath)
    }

    private fun isFileExistsApi29(filePath: String): Boolean {
        if (Build.VERSION.SDK_INT >= 29) {
            try {
                val uri: Uri = Uri.parse(filePath)
                val cr: ContentResolver = Utils.getApp().getContentResolver()
                val afd = cr.openAssetFileDescriptor(uri, "r") ?: return false
                try {
                    afd.close()
                } catch (ignore: IOException) {
                }
            } catch (e: FileNotFoundException) {
                return false
            }
            return true
        }
        return false
    }
    /**
     * Return whether sdcard is enabled by environment.
     *
     * @return `true`: enabled<br></br>`false`: disabled
     */
    fun isSDCardEnableByEnvironment(): Boolean {
        return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
    }

    /**
     * Return the path of sdcard by environment.
     *
     * @return the path of sdcard by environment
     */
    fun getSDCardPathByEnvironment(): String? {
        return if (isSDCardEnableByEnvironment()) {
            Environment.getExternalStorageDirectory().absolutePath
        } else ""
    }

    /**
     * Return the path of /data/data/package/cache.
     *
     * @return the path of /data/data/package/cache
     */
    fun getInternalAppCachePath(): String? {
        return getAbsolutePath(getApp().cacheDir)
    }

    /**
     * Return the path of /data/data/package/files.
     *
     * @return the path of /data/data/package/files
     */
    fun getInternalAppFilesPath(): String? {
        return getAbsolutePath(getApp().getFilesDir())
    }

    /**
     * Return the path of /cache.
     *
     * @return the path of /cache
     */
    fun getDownloadCachePath(): String? {
        return getAbsolutePath(Environment.getDownloadCacheDirectory())
    }

    /**
     * Return the path of /storage/emulated/0/Android/data/package/cache.
     *
     * @return the path of /storage/emulated/0/Android/data/package/cache
     */
    fun getExternalAppCachePath(): String? {
        return if (!isSDCardEnableByEnvironment()) "" else getAbsolutePath(getApp().externalCacheDir)
    }

    fun getCachePathExternalFirst(): String? {
        var appPath = getExternalAppCachePath()
        if (TextUtils.isEmpty(appPath)) {
            appPath = getInternalAppCachePath()
        }
        return appPath
    }

    private fun getAbsolutePath(file: File?): String? {
        return if (file == null) "" else file.absolutePath
    }
}