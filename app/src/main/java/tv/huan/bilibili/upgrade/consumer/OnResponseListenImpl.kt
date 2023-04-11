package tv.huan.bilibili.upgrade.consumer

import com.google.gson.JsonParseException
import io.reactivex.functions.Consumer
import org.json.JSONException
import retrofit2.HttpException
import tv.huan.bilibili.upgrade.net.ResponseBean
import tv.huan.bilibili.upgrade.net.ResponseListBean
import tv.huan.bilibili.utils.LogUtil
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException

abstract class OnResponseListenImpl<T> : Consumer<Any> {

    abstract fun onNoxt(accept: T)
    abstract fun onError(code: Int, msg: String?)
    abstract fun onComplete()


    override fun accept(t: Any) {
        onComplete()
        if (t is Throwable) {
            getMessage(t)
        } else {
            getSucceed(t)
        }
    }

    private fun getSucceed(t: Any) {
        if (t is ResponseBean<*>) {
            val responseBean = t
            if (responseBean.code == 200) {
                onNoxt(t as T)
            } else {
                onError(responseBean.code, "-${responseBean.status}")
            }
        } else if (t is ResponseListBean<*>) {
            val responseListBean = t
            if (responseListBean.code == 200) {
                onNoxt(t as T);
            } else {
                onError(responseListBean.code, "-${responseListBean.status}")
            }
        } else {
            onNoxt(t as T)
        }
    }

    private fun getMessage(e: Throwable?) {
        var msg: String? = e!!.message
        var code: Int = 0
        if (e is UnknownHostException ||
            e is ConnectException
        ) {
            code = 504
            msg = "(╯▽╰)网络开小差了..."
        } else if (e is SocketTimeoutException) {
            code = 408
            msg = "网络连接超时"
        } else if (e is JsonParseException
            || e is JSONException
            || e is ParseException
        ) {
            code = 400
            msg = "解析错误"
        } else if (e is HttpException) {
            val httpException = e
            code = httpException.code()
            msg = httpException.message()
            if (code == 504) {
                msg = "网络不给力"
            }
            if (code == 500) {
                msg = "请求失败"
            }
            if (code == 502 || code == 404) {
                msg = "服务器异常，请稍后再试"
            }
        }
        LogUtil.log("-Response-------" + e.localizedMessage);
        onError(code, msg)
    }

}