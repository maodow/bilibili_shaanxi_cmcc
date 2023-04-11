package tv.huan.bilibili.bean;

import java.io.Serializable;

public class PlayUrlBean implements Serializable {

    private String playurl;

    public String getPlayurl() {
        return playurl;
    }

    public void setPlayurl(String playurl) {
        this.playurl = playurl;
    }

    @Override
    public String toString() {
        return "PlayUrlBean{" +
                "playurl='" + playurl + '\'' +
                '}';
    }
}
