package tv.huan.bilibili.utils

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.text.TextUtils
import android.util.Log
import tv.huan.bilibili.HuanApp
import tv.huan.bilibili.utils.StringUtils.isTrimEmpty
import tv.huan.bilibili.utils.Utils.getApp
import java.io.*
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*

object DevicesUtils {

    /**
     * 获取IP
     */
    fun getEtherNetIP(): String? {
        try {
            val en = NetworkInterface
                .getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf
                    .inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress
                        && inetAddress is Inet4Address
                    ) {
                        return inetAddress.getHostAddress()
                    }
                }
            }
        } catch (ex: SocketException) {
            Log.e("IpAddress", ex.toString())
        }
        return "0.0.0.0"
    }


    /**
     * 获取eth的mac地址
     */
    fun getMacAddress(context: Context): String? {
        var mac = getEthMacAddress("eth")
        if (TextUtils.isEmpty(mac)) {
            mac = getWifiMacAddress(context)
        }
        return mac
    }


    /**
     * 获取eth的mac地址
     *
     * @param name
     * @return
     */
    @SuppressLint("NewApi", "DefaultLocale")
    private fun getEthMacAddress(name: String): String? {
        try {
            val en = NetworkInterface
                .getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                if (intf.name.toLowerCase(Locale.getDefault())
                        .contains(name)
                ) {
                    val ha = intf.hardwareAddress
                    if (ha != null) {
                        val sb = StringBuilder()
                        for (i in ha.indices) {
                            sb.append(String.format("%1$02x", ha[i]))
                            sb.append(":")
                        }
                        return sb.toString().substring(0, sb.toString().length - 1)
                            .toLowerCase(Locale.getDefault())
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            return ""
        }
        return ""
    }


    /**
     * 获取Wifi的mac地址
     */
    private fun getWifiMacAddress(context: Context): String? {
        try {
            val wifi = context
                .getSystemService(Context.WIFI_SERVICE) as WifiManager
            val info = wifi.connectionInfo
            return info.macAddress
                .toLowerCase(Locale.getDefault())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * 计算已使用内存的百分比，并返回。
     */
    fun getUsedPercentValue(): String? {
        val dir = "/proc/meminfo"
        try {
            val fr = FileReader(dir)
            val br = BufferedReader(fr, 2048)
            val memoryLine = br.readLine()
            val subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"))
            br.close()
            val totalMemorySize =
                subMemoryLine.replace("\\D+".toRegex(), "").toInt().toLong()
            val availableSize: Long = getAvailableMemory() / 1024
            val percent =
                ((totalMemorySize - availableSize) / totalMemorySize.toFloat() * 100).toInt()
            //return percent + "%";
            return percent.toString()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
    }


    /**
     * 获取手机内部空间的使用率
     *
     * @return
     */
    fun getTotalInternalMemorySize(): String? {
        val path =
            Environment.getDataDirectory() //Gets the Android data directory
        val stat = StatFs(path.path)
        val blockSize = stat.blockSize.toLong() //每个block 占字节数
        val totalBlocks = stat.blockCount.toLong() //block总数
        val sum = totalBlocks * blockSize
        val availableSize: Long =
            getAvailableInternalMemorySize()
        val percent = ((sum - availableSize) / sum.toFloat() * 100).toInt()
//        Log.d(
//            "TAGTAG",
//            "手机内部空间剩余:$availableSize--使用率：$percent----SD-总：$sum"
//        )
        return percent.toString()
    }


    /**
     * 获取手机内部可用空间大小
     *
     * @return
     */
    private fun getAvailableInternalMemorySize(): Long {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSize.toLong()
        val availableBlocks = stat.availableBlocks.toLong()
        return availableBlocks * blockSize
    }

    /**
     * 获取当前可用内存，返回数据以字节为单位。
     */
    private fun getAvailableMemory(): Long {
        val am = HuanApp.getContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mi = ActivityManager.MemoryInfo()
        am.getMemoryInfo(mi)
        return mi.availMem
    }


    /**
     * 返回手机所支持的cpu类型
     */
    fun getCpuType(): String {
        val abis = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Build.SUPPORTED_ABIS
        } else {
            arrayOf(Build.CPU_ABI, Build.CPU_ABI2)
        }
        return abis.toList().toString()
    }


    /**
     * 获取设备的SN号
     */
    fun getDeviceSN(): String? {
        return Build.SERIAL
    }


    var userId = ""
    private var mac = ""
    private var sn = ""
    private var platform = ""
    var userToken = ""
    private var epgServer = ""


    fun getIP(): String? {
        return getEtherNetIP()
    }


    fun getMAC(): String {
        if (StringUtils.isTrimEmpty(mac)) {
            mac = NullUtis.null2Length0(getMacAddress(HuanApp.getContext()))
        }
        return mac
    }


    /**
     * 盒子厂商
     *  Build.MANUFACTURER
     */
    fun getManufacture(): String? {
        return Build.MANUFACTURER
    }


    /**
     * 盒子机型
     *  Build.MODEL
     */
    fun getModel(): String? {
        return Build.MODEL
    }


    //软终端序列号
    fun getSn(): String {
        if (StringUtils.isTrimEmpty(sn)) {
            sn = NullUtis.null2Length0(getDeviceSN())
        }
        return sn
    }


    /**
     * 获取用户账户
     */
    fun getAccount(): String {
        if (StringUtils.isTrimEmpty(userId)) {
            userId = NullUtis.null2Length0(getDeviceInfo(getApp(), "username"))
        }
        return userId
    }


    /**
     * 获取平台信息 -- 0华为， 1中兴
     * @return hw zx
     */
    fun getPlatform(): String {
        if (StringUtils.isTrimEmpty(platform)) {
            platform = NullUtis.null2Length0(getDeviceInfo(getApp(), "platform"))
        }
        return platform
    }


    /**
     * 获取Token, 请求局方接口Header
     */
    fun getToken(): String {
        if (StringUtils.isTrimEmpty(userToken)) {
            userToken = NullUtis.null2Length0(getDeviceInfo(getApp(), "user_token"))
        }
        return userToken
    }


    /**
     * 获取EpgServer --局方接口ip
     */
    fun getEpgServer(): String {
        if (StringUtils.isTrimEmpty(epgServer)) {
            epgServer = NullUtis.null2Length0(getDeviceInfo(getApp(), "epg_server"))
        }
        return epgServer
    }


    /**
     * 获取盒子的相关参数
     * @param context
     */
    @SuppressLint("Range")
    private fun getDeviceInfo(context: Context, paramsKey: String): String? {
        val contentResolver = context.contentResolver
        val uri = Uri.parse("content://stbauthinfo/authentication/$paramsKey")
        var cursor: Cursor? = null
        try {
            cursor = contentResolver.query(uri, null, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                cursor.moveToPosition(0)
                val paramsValue = cursor.getString(cursor.getColumnIndex("value"))
                Log.i("@@@DevicesUtils", " @@ == $paramsKey ---> $paramsValue")
                return paramsValue
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }

        return ""
    }

}