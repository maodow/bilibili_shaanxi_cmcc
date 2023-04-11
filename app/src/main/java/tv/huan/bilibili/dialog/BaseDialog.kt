package tv.huan.bilibili.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.view.WindowManager
import tv.huan.bilibili.R

abstract class BaseDialog : Dialog {

    protected var mContext: Context? = null
    private var theme: Int = 0

    companion object {
        val FULL_THEME = R.style.AppThemeDialog2
        val POP_UP_THEME: Int = R.style.AppThemeDialog
    }

    abstract fun onLayoutId(): Int

    open fun onLayoutView(): View? {
        return null
    }

    constructor(context: Context, theme: Int) : super(context, theme) {
        mContext = context
        this.theme = theme
        if (theme == POP_UP_THEME) {
            setCancelable(false)
        }
        val onLayoutId = onLayoutId()
        if (onLayoutId != 0)
            setContentView(onLayoutId)
        val onLayoutView = onLayoutView()

        if (onLayoutView != null) {
            setContentView(onLayoutView)
        }
//        DisplayUtil.setGrayBase(window)
    }

    override fun show() {
        super.show()
        if (theme == FULL_THEME)
            setFullScreenDialog(window)
    }

    private fun setFullScreenDialog(window: Window?) {
        if (window == null) return
        //设置alterdialog全屏
        val layoutParams: WindowManager.LayoutParams = window.getAttributes()
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        window.attributes = layoutParams
    }

}