package tv.huan.bilibili.upgrade

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import tv.huan.bilibili.http.HttpClient
import tv.huan.bilibili.upgrade.consumer.OnResponseListenImpl
import tv.huan.bilibili.utils.LogUtil
import java.io.BufferedInputStream
import java.io.File
import java.io.RandomAccessFile

/**
 * 断点续传的工具类
 */
class DownloadFileManager {

    private val TAG = DownloadFileManager::class.java.name
    private var fileInfo: DownloadFileInfo?
    private var compositeDisposable: CompositeDisposable?
    private var filePosition: Long = 0L
    private val MAX_PROGRESS_BAR: Int = 100 //进度条最大值
    private var onDowloadListener: OnDowloadListener? = null
    private var file: File? = null
    private var isDownloading = false

    constructor(fileInfo: DownloadFileInfo) {
        this.fileInfo = fileInfo
        compositeDisposable = CompositeDisposable()
    }

    private var consumer = object : OnResponseListenImpl<ResponseBody>() {
        override fun onNoxt(accept: ResponseBody) {
            val contentLength = accept.contentLength()
            LogUtil.log(TAG, ">>>>>file length: $contentLength")
            val createFile = createFile(contentLength)
            if (createFile == null) return
            var buffered: BufferedInputStream? = null
            try {
                createFile.seek(filePosition) //从指定的位置写入
                val byteStream = accept.byteStream()
                buffered = BufferedInputStream(byteStream)
                val butt = ByteArray(1024 * 4)
                var length = 0
                var currentPercent = 0L
                while (buffered.read(butt, 0, butt.size).also({ length = it }) != -1) {
                    isDownloading = true
                    createFile.write(butt, 0, length)
                    // 下载的进度
                    filePosition += length
                    var percent = MAX_PROGRESS_BAR * filePosition / contentLength
                    if (currentPercent != percent && percent <= 100) // 过滤重复的进度
                        onDowloadListener?.onPosition(percent.toInt())
                    currentPercent = percent
                }
                filePosition = 0
                isDownloading = false
                onDowloadListener?.onComplete(file)
            } catch (e: Exception) {
                isDownloading = false
                onDowloadListener?.onError(e.localizedMessage)
                e.printStackTrace()
            } finally {
                try {
                    buffered?.close()
                    createFile?.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        override fun onError(code: Int, mag: String?) {
            isDownloading = false
            onDowloadListener?.onError(mag)
        }

        override fun onComplete() {
        }
    }

    private fun download() {
        if (fileInfo != null && fileInfo?.isContinue!!) {
            compositeDisposable?.add(
                //设置下载位置  "Range"-下载的范围  "bytes="+start+"-"+End 设置文件的开始位置和结束位置
                HttpClient.getHttpClient().httpApi
                    .download("bytes=$filePosition-", fileInfo?.loadUri)
                    ?.subscribeOn(Schedulers.io())
                    ?.unsubscribeOn(Schedulers.io())
                    ?.observeOn(Schedulers.io())
                !!.subscribe(consumer, consumer)
            )
        } else {
            filePosition = 0
            compositeDisposable?.add(
                HttpClient.getHttpClient().httpApi.download(fileInfo?.loadUri)
                    ?.subscribeOn(Schedulers.io())
                    ?.unsubscribeOn(Schedulers.io())
                    ?.observeOn(Schedulers.io())
                !!.subscribe(consumer, consumer)
            )
        }
    }


    /**
     * 创建储存文件
     */
    private fun createFile(fileLeng: Long): RandomAccessFile? {
        var rand: RandomAccessFile? = null
        try {
            // 创建目录
            val fiel = File(fileInfo?.filePath)
            if (!fiel.exists()) {
                fiel.mkdir()
            }
            // 创建文件
            file = File(fiel.absolutePath, fileInfo?.filename)
            //   打开文件的类似与IO流 “rwd”模式可读可写可删
            rand = RandomAccessFile(file, "rwd")
            //  设置文件的长度
            rand?.setLength(fileLeng)
        } catch (e: Exception) {
            isDownloading = false
            onDowloadListener?.onError(e.localizedMessage)
            e.printStackTrace()
        }
        return rand
    }

    fun setOnDowloadListener(onDowloadListener: OnDowloadListener) {
        this.onDowloadListener = onDowloadListener
    }

    fun isDownloading(): Boolean {
        return isDownloading
    }

    fun start() {
        isDownloading = true
        download()
    }

    fun stop() {
        isDownloading = false
        compositeDisposable?.clear()
    }

    interface OnDowloadListener {
        fun onError(mag: String?)
        fun onPosition(progress: Int)
        fun onComplete(file: File?)
    }

}