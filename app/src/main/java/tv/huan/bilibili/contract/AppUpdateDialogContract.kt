package tv.huan.bilibili.contract

import androidx.annotation.Keep

@Keep
interface AppUpdateDialogContract {

    fun showDetectionUpdate()

    fun hideWindown()

    fun isUpdate(): Boolean

}