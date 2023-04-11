package tv.huan.bilibili.bean.base;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Keep
public class BaseImageBean extends BaseDataBean implements Serializable {

    private String poster;
    private String newPicVt;
    private String newPicHz;

    private String picHzPath;
    private String picVtPath;


    @SerializedName("pic")
    private String vipUrl;


    public BaseImageBean() {
    }

    public BaseImageBean(String poster, String newPicVt, String newPicHz) {
        this.poster = poster;
        this.newPicVt = newPicVt;
        this.newPicHz = newPicHz;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public void setNewPicVt(String newPicVt) {
        this.newPicVt = newPicVt;
    }

    public void setNewPicHz(String newPicHz) {
        this.newPicHz = newPicHz;
    }

    public String getPicture(boolean hz) {
        if (null != poster && poster.length() > 0) {
            return poster;
        } else if (hz && null != picHzPath && picHzPath.length() > 0) {
            return picHzPath;
        } else {
            return picVtPath;
        }
//        return "http://123.59.196.222/hljbili/public/productCodePic/series91030010010000000000001819pic27875.jpg";
    }

    public void copyPic(BaseImageBean data) {
        if (null == data)
            return;
        this.poster = data.poster;
        this.picHzPath = data.picHzPath;
        this.picVtPath = data.picVtPath;
    }

    public String getVipUrl() {
        return vipUrl;
    }

    public void setVipUrl(String vipUrl) {
        this.vipUrl = vipUrl;
    }
}