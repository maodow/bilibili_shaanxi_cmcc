package tv.huan.bilibili.upgrade

class DownloadFileInfo {
    var isContinue: Boolean? = false //是否开启断点续传，后台接口需要对range 做处理
    var loadUri: String? = null
    var filePath: String? = null
    var filename: String? = null

    constructor(loadUri: String?, filePath: String?, filename: String?) {
        this.loadUri = loadUri
        this.filePath = filePath
        this.filename = filename
    }

}