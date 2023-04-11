package tv.huan.bilibili.bean;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import lib.kalu.leanback.presenter.bean.TvPresenterRowBean;
import tv.huan.bilibili.BuildConfig;
import tv.huan.bilibili.bean.base.BaseDataBean;
import tv.huan.bilibili.bean.base.BaseImageBean;

@Keep
public class GetSubChannelsByChannelBean implements Serializable {

    private ChannelBean channel;
    private List<ListBean> list;
    private List<ClassesBean> classes;
    private ClassParentBean parent;

    public ClassParentBean getParent() {
        return parent;
    }

    public void setParent(ClassParentBean parent) {
        this.parent = parent;
    }

    public List<ClassesBean> getClasses() {
        return classes;
    }

    public void setClasses(List<ClassesBean> classes) {
        this.classes = classes;
    }

    public ChannelBean getChannel() {
        return channel;
    }

    public void setChannel(ChannelBean channel) {
        this.channel = channel;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    @Keep
    public static class ChannelBean implements Serializable {

    }

    @Keep
    public static class ListBean implements Serializable {

        private int preTemplate;  // 模板类型
        private String name; // 模板标题
        private int show; // 模板标题是否显示
        private List<TemplateBean> templateData; // 数据

        public List<TemplateBean> getTemplateData() {
            try {
                for (TemplateBean t : templateData) {
                    t.setTempTitle(name);
                    t.setTempShow(show == 1);
                }
            } catch (Exception e) {
            }
            return templateData;
        }

        public void setTemplateData(List<TemplateBean> templateData) {
            this.templateData = templateData;
        }

        public int getPreTemplate() {
            return preTemplate;
        }

        public void setPreTemplate(int preTemplate) {
            this.preTemplate = preTemplate;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getShow() {
            return show;
        }

        public void setShow(int show) {
            this.show = show;
        }

        @Keep
        public static class TemplateBean extends BaseImageBean implements TvPresenterRowBean, Serializable {

            private boolean tempShow;
            private String tempTitle;
            private FavBean tempFav;
            private boolean tempChecked;


            private String description;
            private String score;
            private String tag;
            private String cname;
            private String extPoster;
            @SerializedName("jumpParam")
            private String huaweiId;

            private String movieCode; //中兴平台
            private String hwMovieCode; //华为平台


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


            public String getHuaweiId() {
                return huaweiId;
            }

            public void setHuaweiId(String huaweiId) {
                this.huaweiId = huaweiId;
            }

            public String getExtPoster() {
                return extPoster;
            }

            public boolean hasExtPoster() {
                return null != extPoster && extPoster.length() > 0;
            }

            public void setExtPoster(String extPoster) {
                this.extPoster = extPoster;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getScore() {
                return score;
            }

            public void setScore(String score) {
                this.score = score;
            }

            public String getTag() {
                return tag;
            }

            public void setTag(String tag) {
                this.tag = tag;
            }

            public String getCname() {
                return cname;
            }

            public void setCname(String cname) {
                this.cname = cname;
            }

            public TemplateBean() {
            }


            public boolean isTempChecked() {
                return tempChecked;
            }

            public void setTempChecked(boolean tempChecked) {
                this.tempChecked = tempChecked;
            }

            public boolean isTempShow() {
                return tempShow;
            }

            public String getTempTitle() {
                return tempTitle;
            }

            public FavBean getTempFav() {
                return tempFav;
            }

            public void setTempFav(FavBean tempFav) {
                this.tempFav = tempFav;
            }

            public void setTempShow(boolean tempShow) {
                this.tempShow = tempShow;
            }

            public void setTempTitle(String tempTitle) {
                this.tempTitle = tempTitle;
            }

            @Override
            public String getRowTitle() {
                if (BuildConfig.HUAN_TEST_TEMPLATE_ENABLE) {
                    return tempTitle;
                } else if (tempShow) {
                    return tempTitle;
                } else {
                    return null;
                }
            }
        }
    }

    public static class ClassesBean extends BaseDataBean implements Serializable {

        private int productId;
        private int parentId;
        private int hasNext;
        private int status;

        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public int getParentId() {
            return parentId;
        }

        public void setParentId(int parentId) {
            this.parentId = parentId;
        }

        public int getHasNext() {
            return hasNext;
        }

        public void setHasNext(int hasNext) {
            this.hasNext = hasNext;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }

    @Keep
    public static class ClassParentBean extends BaseDataBean implements Serializable {
    }
}
