package tv.huan.bilibili.bean;

/**
 * Create by mdw
 * 2023-03-29 10:10
 *
 * 【用户鉴权】
 *  http://36.133.254.116:81/boss/auth?userId=C0132BA8F7BA&stbId=004003FF111100200001C0132BA8F7BA&contentId=
 *
 *  ===响应： auth ==> 0--未通过;  1--通过;   -1--鉴权异常
 *
 *   {
 *  	"status":"success",
 *
 *  	"code":200,
 *
 *  	"data":{
 *
 *  		"auth":"0",
 *
 *  		"fav":false,
 *
 *  		"desc":"鉴权不通过"
 *
 *        }
 *  }
 */
public class AuthBean {

    private String auth; //鉴权返回: 0-未通过;  1-通过;  -1-鉴权异常
    private boolean fav;
    private String authType;
    private String vtoken;
    private String vuid;

    /**
     * 陕西局方字段
     */
    private String cloudPayUrl;

    public String getVtoken() {
        return vtoken;
    }

    public void setVtoken(String vtoken) {
        this.vtoken = vtoken;
    }

    public String getVuid() {
        return vuid;
    }

    public void setVuid(String vuid) {
        this.vuid = vuid;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getCloudPayUrl() {
        return cloudPayUrl;
    }

    public void setCloudPayUrl(String cloudPayUrl) {
        this.cloudPayUrl = cloudPayUrl;
    }

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

}
