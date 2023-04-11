package tv.huan.bilibili.utils

import tv.huan.bilibili.utils.NullUtis.null2Length0
import tv.huan.bilibili.utils.StringUtils

object NullUtis {

    fun nullToFloat(value: Float?): Float {
        return value ?: 0f
    }

    fun nullToInt(value: Int?): Int {
        return value ?: 0
    }

    fun nullToInt(value: Int?, defValue: Int): Int {
        return value ?: defValue
    }

    fun nullToInt(value: String?): Int {
        return StringUtils.stringToInt(value)
    }

    fun nullToLong(value: Long?): Long {
        return value ?: 0
    }

    fun nullToLong(value: String?): Long {
        return StringUtils.stringToLong(value)
    }

    fun null2Length0(value: String?): String {
        return StringUtils.null2Length0(value)
    }

    fun null2String(value: String?, defValue: String): String {
        if (StringUtils.isTrimEmpty(value)) return defValue
        return value!!
    }

    fun null2False(value: Boolean?): Boolean {
        return value ?: false
    }

    fun null2False(value: Boolean?, defValue: Boolean): Boolean {
        return value ?: defValue
    }

    fun null2False(value: String?): Boolean {
        if (StringUtils.isTrimEmpty(value)) return false
        return value!!.toBoolean()
    }

    fun null2False(value: String?, defValue: Boolean): Boolean {
        if (StringUtils.isTrimEmpty(value)) return defValue
        return value!!.toBoolean()
    }

    fun null2Double(value: Double?): Double {
        return value ?:0.0
    }
}