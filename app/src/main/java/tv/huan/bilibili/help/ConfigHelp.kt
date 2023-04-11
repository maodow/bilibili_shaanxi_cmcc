package tv.huan.bilibili.help

import tv.huan.bilibili.bean.ServerSettingData
import tv.huan.bilibili.upgrade.AppUpdateHelp
import tv.huan.bilibili.utils.CustomSpUtils

/**
 *  保存Server配置信息
 * @see ServerSettingData
 */
class ConfigHelp private constructor() {

    private val CONFIG_HELP = "config_help"
    private val SERVER_SETTINGS = "settings_from_server"

    private var mSettings: ServerSettingData? = null
    private var isDebug: Boolean = false


    companion object {
        val INSTANCE: ConfigHelp by lazy {
            ConfigHelp()
        }
    }

    fun init(mSettings: ServerSettingData) {
        this.mSettings = mSettings
        isDebug = mSettings.isDebugFlag
        saveConfig(mSettings)
        AppUpdateHelp.INSTANCE.setConfigInfo(mSettings)
    }
    

    /**
     * 保存服务端的配置
     */
    fun saveConfig(mSettings: ServerSettingData) {
        getShareObject().putObject(SERVER_SETTINGS, mSettings)
    }

    /**
     * 获取服务端的配置
     */
    fun getSettings(): ServerSettingData? {
        if (mSettings == null)
            mSettings = getShareObject().getObject<ServerSettingData>(SERVER_SETTINGS)
        return mSettings
    }
    
    
    private fun getShareObject(): CustomSpUtils {
        return CustomSpUtils.getInstance().setSharedPreferences(CONFIG_HELP)
    }
    
}