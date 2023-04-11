package tv.huan.bilibili.utils;

public final class Constants {

	/**
	 * 注：1、暂不考虑广告视图渲染方面的, onLoadAd调用了，就算成功了
	 *    2、广告位是正式环境, 使用正式环境需要把初始化时的setDebugUrl去掉或者设为false,
	 *      然后清一下APP缓存 ,上线时要使用正式环境
	 */

	public static boolean IS_SHOW_AD; //广告配置开关(后台接口配置)

	public static final String APP_KEY = "Ylk5hg"/*"7dc15107ec1341ae80bf5f49fee16af2"*/;

	//开屏广告
	public static final String OPEN_SCREEN_AD = "operator-openscreen-shaanxi-cmcc-bl"/*"test-kaiping"*/;

	//贴片广告
	public static final String PRE_ROLL_AD = "operator-preroll-shaanxi-cmcc-bl"/*"test-tiepian"*/;

	//暂停广告
	public static final String PAUSE_AD = "operator-pause-shaanxi-cmcc-bl"/*"test-zanting"*/;

	//APP退出广告
	public static final String EXIT_AD = "operator-quit-shaanxi-cmcc-bl"/*"test-tuichu"*/;


	//上报类型及结果
	public static class REPORT {
		public static final String TYPE_DOWNLOAD = "300";
		public static final String TYPE_INSTALL = "400";
		public static final String TYPE_UNINSTALL = "500";
		public static final String TYPE_UPDATE_NORMAL = "100";
		public static final String TYPE_UPDATE_FORCE = "200";
		public static final String TYPE_ACTIVATE = "ACTIVATE";
		public static final String TYPE_GRID = "GRID";
		public static final String TYPE_APPDETAIL = "APPDETAIL";

        public static final String RESULT_SUCCESS = "1";
        public static final String RESULT_FAIL = "0";

		public static final String CONTENT_CLASS = "CLASS";
		public static final String CONTENT_SEARCHREMD = "SEARCHREMD";
		public static final String CONTENT_SEARCHRESULT = "SEARCHRESULT";
		public static final String CONTENT_DETAILREMD = "DETAILREMD";
		public static final String CONTENT_BOOTREMD = "BOOTREMD";
		public static final String CONTENT_OTHER = "OTHER";
	}


	/**
	 * 操作类型
	 */
	public static final class OpeType {

		//下载应用
		public static final int APP_REP_DOWNLOAD = 1;

		//安装应用
		public static final int APP_REP_INSTALL = 2;

		//升级
		public static final int APP_REP_UPGRADE = 3;

		//卸载应用
		public static final int APP_REP_UNINSTALL = 4;

	}


	public static final class OpeState {

		//下载apk
		public static final int OPERATE_SUCCESS = 0;

		//升级apk
		public static final int OPERATE_FAIL = 1;

	}

}
