package tv.huan.bilibili.bean;

import java.io.Serializable;

/**
 * Create by Mdw
 * 2023-04-03 10:42
 *  服务器配置
 */
public class ServerSettingData implements Serializable {

    private String authType; // "1",
    private String adType; // "0", 控制是否开启广告sdk的, 0 不开启   1开启
    private String agentUrl;  // ServerIp
    private String skin;  // 换肤包的下载地址
    private boolean debugFlag = false;   //是否是debug状态

    private int isHomeToRetain = 1; //app 退出挽留是否展示 0 不显示，1：显示
    private int showNav = 1; //app 导航是否要显示 0 不显示，1 显示

    private InfoBarBean infoBar;  //导航信息数据
    private UpgradeBean upgrade;  //app 更新
    private PlayerConfigBean playerConfig; //播放器的一些基础配置
    private MineInfo mineInfo; //会员信息页面展示数据 <暂时无用>

    //安徽移动局方探针的地址
    private String besTVIP;
    private String newTVIP;

    private float clarity = 1.0f; // 调整图片的清晰度，0.0~1.0
    private int clarityCode; // 清晰度的code，对应SDK的版本code


    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public String getAdType() {
        return adType;
    }

    public void setAdType(String adType) {
        this.adType = adType;
    }

    public boolean isShowNav() {
        return showNav == 1;
    }

    public void setShowNav(int showNav) {
        this.showNav = showNav;
    }

    public boolean isHomeToRetain() {
        return isHomeToRetain == 1;
    }

    public void setHomeToRetain(int homeToRetain) {
        isHomeToRetain = homeToRetain;
    }

    public float getClarity() {
        return clarity;
    }

    public void setClarity(float clarity) {
        this.clarity = clarity;
    }

    public int getClarityCode() {
        return clarityCode;
    }

    public void setClarityCode(int clarityCode) {
        this.clarityCode = clarityCode;
    }

    public MineInfo getMineInfo() {
        return mineInfo;
    }

    public void setMineInfo(MineInfo mineInfo) {
        this.mineInfo = mineInfo;
    }

    public String getBesTVIP() {
        return besTVIP;
    }

    public void setBesTVIP(String besTVIP) {
        this.besTVIP = besTVIP;
    }

    public String getNewTVIP() {
        return newTVIP;
    }

    public void setNewTVIP(String newTVIP) {
        this.newTVIP = newTVIP;
    }

    public UpgradeBean getUpgrade() {
        return upgrade;
    }

    public void setUpgrade(UpgradeBean upgrade) {
        this.upgrade = upgrade;
    }

    public String getAgentUrl() {
        return agentUrl;
    }

    public void setAgentUrl(String agentUrl) {
        this.agentUrl = agentUrl;
    }

    public boolean isDebugFlag() {
        return debugFlag;
    }

    public void setDebugFlag(boolean debugFlag) {
        this.debugFlag = debugFlag;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public InfoBarBean getInfoBar() {
        return infoBar;
    }

    public void setInfoBar(InfoBarBean infoBar) {
        this.infoBar = infoBar;
    }


    public static class InfoBarBean implements Serializable {
        /**
         * content : 首月9.9
         * linkType : 0
         * linkParam :
         */
        private String content;
        private int linkType;
        private String linkParam;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getLinkType() {
            return linkType;
        }

        public void setLinkType(int linkType) {
            this.linkType = linkType;
        }

        public String getLinkParam() {
            return linkParam;
        }

        public void setLinkParam(String linkParam) {
            this.linkParam = linkParam;
        }
    }

    public static class UpgradeBean implements Serializable {

        private String version_name;  // 最新版本号
        private int version_code;  // 最新版本code
        private boolean update;  //是否更新 false 本次版本，将不再更新
        private boolean forcedUpdating;  //是否强制更新
        private String updateContent; // 本次更新的内容
        private String url;    // apk的下载地址（完整下载地址）

        public boolean getUpdate() {
            return update;
        }

        public String getVersionName() {
            return version_name;
        }

        public void setVersionName(String versionName) {
            this.version_name = versionName;
        }

        public int getVersionCode() {
            return version_code;
        }

        public void setVersionCode(int versionCode) {
            this.version_code = versionCode;
        }

        public boolean isUpdate() {
            return update;
        }

        public boolean isForcedUpdating() {
            return forcedUpdating;
        }

        public void setForcedUpdating(boolean forcedUpdating) {
            this.forcedUpdating = forcedUpdating;
        }

        public String getUpdateContent() {
            return updateContent;
        }

        public void setUpdateContent(String updateContent) {
            this.updateContent = updateContent;
        }

        public void setUpdate(boolean update) {
            this.update = update;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public PlayerConfigBean getPlayerConfig() {
        return playerConfig;
    }

    public void setPlayerConfig(PlayerConfigBean playerConfig) {
        this.playerConfig = playerConfig;
    }


    public static class PlayerConfigBean implements Serializable {
        //是否隐藏播放器logo，true 隐藏， false 不隐藏
        private boolean logoHide;
        private String logoUrl;

        private long logDurationClose; //日志多长时间关闭,单位毫秒
        //是否显示播放器退出挽留
        private boolean playDetainmentShow = true;

        private String vipBgImgUrl;  //广告弹框跳转支付图片
        private boolean vipDialogShow = true; //广告弹框跳转支付是否要展示

        // 播放器进度条，预览图是否需要禁用，true 使用，false 禁用
        private boolean previewValid;


        public boolean isPreviewValid() {
            return previewValid;
        }

        public void setPreviewValid(boolean previewValid) {
            this.previewValid = previewValid;
        }

        public boolean isVipDialogShow() {
            return vipDialogShow;
        }

        public void setVipDialogShow(boolean vipDialogShow) {
            this.vipDialogShow = vipDialogShow;
        }

        public String getVipBgImgUrl() {
            return vipBgImgUrl;
        }

        public void setVipBgImgUrl(String vipBgImgUrl) {
            this.vipBgImgUrl = vipBgImgUrl;
        }

        public boolean isLogoHide() {
            return logoHide;
        }

        public void setLogoHide(boolean logoHide) {
            this.logoHide = logoHide;
        }

        public String getLogoUrl() {
            return logoUrl;
        }

        public void setLogoUrl(String logoUrl) {
            this.logoUrl = logoUrl;
        }

        public long getLogDurationClose() {
            return logDurationClose;
        }

        public void setLogDurationClose(long logDurationClose) {
            this.logDurationClose = logDurationClose;
        }

        public boolean isPlayDetainmentShow() {
            return playDetainmentShow;
        }

        public void setPlayDetainmentShow(boolean playDetainmentShow) {
            this.playDetainmentShow = playDetainmentShow;
        }
    }

    public static class MineInfo implements Serializable {
        private String lotteryIconUrl;
        private String lotteryUrl;
        private boolean lotteryShow;

        private String vipBriefIconUrl;
        private String vipBgUrl;
        private String vipButtonUrl;
        private String vipButtonUrlFocus;
//       private String lotteryIconUrl="http://192.168.13.33:8080/tomcat.png";
//        private String lotteryUrl="http://192.168.13.33:3344/untitled/js/vendor/anhui_cmcc/huodong/mineActivity.html";
//        private boolean lotteryShow=false;
//
//        private String vipBriefIconUrl="http://192.168.13.33:8080/tomcat.png";
//        private String vipBgUrl="http://192.168.13.33:8080/tomcat.png";
//        private String vipButtonUrl="http://192.168.13.33:8080/tomcat.png";

        private String loginUrl;

        public String getLoginUrl() {
            return loginUrl;
        }

        public void setLoginUrl(String loginUrl) {
            this.loginUrl = loginUrl;
        }

        public String getVipBgUrl() {
            return vipBgUrl;
        }

        public void setVipBgUrl(String vipBgUrl) {
            this.vipBgUrl = vipBgUrl;
        }

        public String getVipButtonUrl() {
            return vipButtonUrl;
        }

        public void setVipButtonUrl(String vipButtonUrl) {
            this.vipButtonUrl = vipButtonUrl;
        }

        public String getVipButtonUrlFocus() {
            return vipButtonUrlFocus;
        }

        public void setVipButtonUrlFocus(String vipButtonUrlFocus) {
            this.vipButtonUrlFocus = vipButtonUrlFocus;
        }

        public String getLotteryIconUrl() {
            return lotteryIconUrl;
        }

        public void setLotteryIconUrl(String lotteryIconUrl) {
            this.lotteryIconUrl = lotteryIconUrl;
        }

        public String getLotteryUrl() {
            return lotteryUrl;
        }

        public void setLotteryUrl(String lotteryUrl) {
            this.lotteryUrl = lotteryUrl;
        }

        public boolean isLotteryShow() {
            return lotteryShow;
        }

        public void setLotteryShow(boolean lotteryShow) {
            this.lotteryShow = lotteryShow;
        }

        public String getVipBriefIconUrl() {
            return vipBriefIconUrl;
        }

        public void setVipBriefIconUrl(String vipBriefIconUrl) {
            this.vipBriefIconUrl = vipBriefIconUrl;
        }
    }


    @Override
    public String toString() {
        return "ServerSettingData{" +
                "authType='" + authType + '\'' +
                ", adType='" + adType + '\'' +
                ", agentUrl='" + agentUrl + '\'' +
                ", skin='" + skin + '\'' +
                ", debugFlag=" + debugFlag +
                ", isHomeToRetain=" + isHomeToRetain +
                ", showNav=" + showNav +
                ", infoBar=" + infoBar +
                ", upgrade=" + upgrade +
                ", playerConfig=" + playerConfig +
                ", mineInfo=" + mineInfo +
                ", besTVIP='" + besTVIP + '\'' +
                ", newTVIP='" + newTVIP + '\'' +
                ", clarity=" + clarity +
                ", clarityCode=" + clarityCode +
                '}';
    }

}
