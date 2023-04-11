package tv.huan.bilibili.bean.base;

import java.util.List;

import tv.huan.bilibili.bean.PlayUrlBean;

public class BaseThirdResponse {

    private String returncode;
    private String description;
    private List<PlayUrlBean> urls;


    public String getReturncode() {
        return returncode;
    }

    public void setReturncode(String returncode) {
        this.returncode = returncode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PlayUrlBean> getUrls() {
        return urls;
    }

    public void setUrls(List<PlayUrlBean> urls) {
        this.urls = urls;
    }

    @Override
    public String toString() {
        return "BaseThirdResponse{" +
                "returncode='" + returncode + '\'' +
                ", description='" + description + '\'' +
                ", urls=" + urls +
                '}';
    }
}
