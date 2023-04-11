package tv.huan.bilibili.bean;

import androidx.annotation.Keep;

import java.io.Serializable;

import lib.kalu.leanback.presenter.bean.TvPresenterRowBean;
import tv.huan.bilibili.bean.base.BaseImageBean;

@Keep
public class MediaBean extends BaseImageBean implements Serializable, TvPresenterRowBean {

    @Override
    public MediaBean clone() {
        try {
            return (MediaBean) super.clone();
        } catch (Exception e) {
            return null;
        }
    }

    private String tempVideoUrl;
    private String tempImageUrl;
    private int tempPlayType;
    private boolean tempFavor; //收藏状态
    private String tempTag;
    private String tempTitle;
    private String temoInfo;
    private String tempRecClassId;
    private String[] tempPicList;
    private long tempSeek;

    private String movieCode; //中兴平台
    private String hwMovieCode; //华为平台


    public String getTempVideoUrl() {
        return tempVideoUrl;
    }

    public void setTempVideoUrl(String tempVideoUrl) {
        this.tempVideoUrl = tempVideoUrl;
    }

    public String getTempImageUrl() {
        return tempImageUrl;
    }

    public void setTempImageUrl(String tempImageUrl) {
        this.tempImageUrl = tempImageUrl;
    }

    public int getTempPlayType() {
        return tempPlayType;
    }

    public void setTempPlayType(int tempPlayType) {
        this.tempPlayType = tempPlayType;
    }

    public boolean isTempFavor() {
        return tempFavor;
    }

    public void setTempFavor(boolean tempFavor) {
        this.tempFavor = tempFavor;
    }

    public String getTempTag() {
        return tempTag;
    }

    public void setTempTag(String tempTag) {
        this.tempTag = tempTag;
    }

    public String getTempTitle() {
        return tempTitle;
    }

    public void setTempTitle(String tempTitle) {
        this.tempTitle = tempTitle;
    }

    public String getTemoInfo() {
        return temoInfo;
    }

    public void setTemoInfo(String temoInfo) {
        this.temoInfo = temoInfo;
    }

    public String getTempRecClassId() {
        return tempRecClassId;
    }

    public void setTempRecClassId(String tempRecClassId) {
        this.tempRecClassId = tempRecClassId;
    }

    public String[] getTempPicList() {
        return tempPicList;
    }

    public void setTempPicList(String[] tempPicList) {
        this.tempPicList = tempPicList;
    }

    public long getTempSeek() {
        return tempSeek;
    }

    public void setTempSeek(long tempSeek) {
        this.tempSeek = tempSeek;
    }

    public String getMovieCode() {
        return movieCode;
    }

    public void setMovieCode(String movieCode) {
        this.movieCode = movieCode;
    }

    public String getHwMovieCode() {
        return hwMovieCode;
    }

    public void setHwMovieCode(String hwMovieCode) {
        this.hwMovieCode = hwMovieCode;
    }

    @Override
    public String getRowTitle() {
        return null;
    }
}
