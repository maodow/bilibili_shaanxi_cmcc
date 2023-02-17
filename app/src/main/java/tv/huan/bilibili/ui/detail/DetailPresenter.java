package tv.huan.bilibili.ui.detail;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.ObjectAdapter;
import androidx.leanback.widget.VerticalGridView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import lib.kalu.frame.mvp.BasePresenter;
import lib.kalu.frame.mvp.transformer.ComposeSchedulers;
import lib.kalu.frame.mvp.util.CacheUtil;
import tv.huan.bilibili.BuildConfig;
import tv.huan.bilibili.utils.LogUtil;
import tv.huan.bilibili.widget.player.PlayerView;
import tv.huan.bilibili.R;
import tv.huan.bilibili.bean.Album;
import tv.huan.bilibili.bean.Auth2Bean;
import tv.huan.bilibili.bean.BaseBean;
import tv.huan.bilibili.bean.LookBean;
import tv.huan.bilibili.bean.Media;
import tv.huan.bilibili.bean.ProgramDetail;
import tv.huan.bilibili.bean.ProgramInfoDetail;
import tv.huan.bilibili.http.HttpClient;
import tv.huan.bilibili.ui.detail.template.DetailTemplateFav;
import tv.huan.bilibili.ui.detail.template.DetailTemplatePlayer;
import tv.huan.bilibili.ui.detail.template.DetailTemplateXuanJi;
import tv.huan.bilibili.ui.detail.template.DetailTemplateXuanQi;
import tv.huan.bilibili.utils.Constants;
import tv.huan.common.starcor.StarcorUtil;

public class DetailPresenter extends BasePresenter<DetailView> {
    public DetailPresenter(@NonNull DetailView detailView) {
        super(detailView);
    }

    protected boolean checkPlayer() {
        try {
            PlayerView playerView = getView().findViewById(R.id.detail_player_item_video);
            LogUtil.log("DetailPresenter => checkPlayer => playerView = " + playerView);
            if (null == playerView) {
                VerticalGridView gridView = getView().findViewById(R.id.detail_list);
                LogUtil.log("DetailPresenter => checkPlayer => gridView = " + gridView);
                RecyclerView.Adapter adapter = gridView.getAdapter();
                if (null == adapter) {
                    return true;
                } else {
                    int itemCount = adapter.getItemCount();
                    if (itemCount <= 0) {
                        return true;
                    } else {
                        gridView.smoothScrollToPosition(0);
                        return false;
                    }
                }
            } else {
                boolean full = playerView.isFull();
                boolean floats = playerView.isFloat();
                LogUtil.log("DetailPresenter => checkPlayer => full = " + full);
                LogUtil.log("DetailPresenter => checkPlayer => floats = " + floats);
                if (full) {
                    playerView.stopFull();
                    return false;
                } else if (floats) {
                    playerView.stopFloat();
                    return false;
                } else {
                    playerView.release();
                    return true;
                }
            }
        } catch (Exception e) {
            LogUtil.log("DetailPresenter => checkPlayer => " + e.getMessage());
            return false;
        }
    }

    protected void refreshLook() {

        String cid = getView().getStringExtra(DetailActivity.INTENT_CID);
        if (null == cid || cid.length() <= 0)
            return;

        Context context = getView().getContext();
        String s = CacheUtil.getCache(context, "user_look");
        if (null == s || s.length() <= 0) {
            s = "[]";
        }
        Type type = new TypeToken<List<LookBean>>() {
        }.getType();
        List<LookBean> recs = new Gson().fromJson(s, type);
        int position = -1;
        int size = recs.size();
        for (int i = 0; i < size; i++) {
            LookBean temp = recs.get(i);
            if (null == temp)
                continue;
            String str = temp.getCid();
            if (cid.equals(str)) {
                position = i;
                break;
            }
        }
        LinkedList<LookBean> list = new LinkedList<>();
        list.addAll(recs);
        // exist
        if (position != -1) {
            LookBean bean = list.remove(position);
            list.addFirst(bean);
        } else {
            LookBean bean = new LookBean();
            bean.setCid(cid);
            list.addFirst(bean);
        }
        String s1 = new Gson().toJson(list);
        CacheUtil.setCache(context, "user_look", s1);
    }

    protected final void setAdapter() {
        VerticalGridView gridView = getView().findViewById(R.id.detail_list);
        DetailSelectorPresenter selectorPresenter = new DetailSelectorPresenter();
        ArrayObjectAdapter objectAdapter = new ArrayObjectAdapter(selectorPresenter);
        ItemBridgeAdapter bridgeAdapter = new ItemBridgeAdapter(objectAdapter);
        gridView.setAdapter(bridgeAdapter);
    }

    protected void request() {
        addDisposable(Observable.create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(ObservableEmitter<Boolean> emitter) {
                        emitter.onNext(true);
                    }
                })
                // 媒资数据
                .flatMap(new Function<Boolean, ObservableSource<BaseBean<ProgramInfoDetail>>>() {
                    @Override
                    public ObservableSource<BaseBean<ProgramInfoDetail>> apply(Boolean o) {
                        String cid = getView().getStringExtra(DetailActivity.INTENT_CID);
                        if (null == cid) {
                            cid = "";
                        }
                        return HttpClient.getHttpClient().getHttpApi().getMediasByCid2(cid);
                    }
                })
                // 媒资鉴权
                .flatMap(new Function<BaseBean<ProgramInfoDetail>, ObservableSource<Auth2Bean>>() {
                    @Override
                    public ObservableSource<Auth2Bean> apply(BaseBean<ProgramInfoDetail> programInfoDetailBaseBean) {

                        ProgramInfoDetail data = programInfoDetailBaseBean.getData();
                        boolean request;
                        try {
                            ProgramDetail album = data.getAlbum();
                            // 付费
                            if (0 != album.getProductType()) {
                                request = true;
                            } else {
                                request = false;
                            }
                        } catch (Exception e) {
                            request = false;
                        }

                        if (request) { //专区请求周期返回专区购买的列表
                            String cid = getView().getStringExtra(DetailActivity.INTENT_CID);
                            if (null == cid) {
                                cid = "";
                            }
                            String s = new Gson().toJson(data);
                            LogUtil.log("DetailPresenter => s = " + s);
                            return HttpClient.getHttpClient().getHttpApi().auth2(cid, s);
                        } else {
                            return Observable.create(new ObservableOnSubscribe<Auth2Bean>() {
                                @Override
                                public void subscribe(ObservableEmitter<Auth2Bean> emitter) {
                                    Auth2Bean bean = new Auth2Bean();
                                    bean.setFree(true);
                                    String s = new Gson().toJson(data);
                                    bean.setExtra(s);
                                    emitter.onNext(bean);
                                }
                            });
                        }
                    }
                })
                // 数据处理
                .map(new Function<Auth2Bean, ProgramInfoDetail>() {
                    @Override
                    public ProgramInfoDetail apply(Auth2Bean auth2Bean) {

                        String s;
                        try {
                            String extra = auth2Bean.getExtra();
                            if (null != extra && extra.length() > 0) {
                                s = extra;
                            } else {
                                s = "{}";
                            }
                        } catch (Exception e) {
                            s = "{}";
                        }
                        LogUtil.log("DetailPresenter => s = " + s);
                        ProgramInfoDetail data = new Gson().fromJson(s, ProgramInfoDetail.class);
                        String whiteListVip = auth2Bean.getWhiteListVip();

                        // 免费 || 白名单
                        if (auth2Bean.isFree() || "1".equals(whiteListVip)) {
                            data.setVipFree(true);
                        }
                        // 付费
                        else {

                            ProgramDetail album = data.getAlbum();
                            // 关联产品编码计费包
                            List<ProgramDetail.Item> productCodes = album.getProductCodes();
                            int length;
                            try {
                                length = productCodes.size();
                            } catch (Exception e) {
                                length = 0;
                            }

                            // 单点会员
                            try {
                                for (int i = 0; i < length; i++) {
                                    ProgramDetail.Item item = productCodes.get(i);
                                    if (null == item)
                                        continue;
                                    int productType = item.getProductType();
                                    if (1 != productType)
                                        continue;
                                    String code = item.getCode();
                                    if (null == code || code.length() <= 0)
                                        continue;
                                    data.setVipLinkDanDian(true);
                                    String singleAuth = auth2Bean.getSingleAuth();
                                    if ("1".equals(singleAuth)) {
                                        data.setVipPassDanDian(true);
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                            }

                            // 周期会员
                            try {
                                for (int i = 0; i < length; i++) {
                                    ProgramDetail.Item item = productCodes.get(i);
                                    if (null == item)
                                        continue;
                                    int productType = item.getProductType();
                                    if (3 != productType)
                                        continue;
                                    String code = item.getCode();
                                    if (null == code || code.length() <= 0)
                                        continue;
                                    data.setVipLinkZhouQi(true);
                                    String vip = auth2Bean.getVip();
                                    if ("1".equals(vip)) {
                                        data.setVipPassZhouQi(true);
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                            }

                            // 专区会员
                            try {
                                for (int i = 0; i < length; i++) {
                                    ProgramDetail.Item item = productCodes.get(i);
                                    if (null == item)
                                        continue;
                                    int productType = item.getProductType();
                                    if (4 != productType)
                                        continue;
                                    String code = item.getCode();
                                    if (null == code || code.length() <= 0)
                                        continue;
                                    data.setVipLinkZhuanQu(true);
                                    List<Auth2Bean.VipBean> specialList = auth2Bean.getVipSpecialList();
                                    int size;
                                    try {
                                        size = specialList.size();
                                    } catch (Exception e) {
                                        size = 0;
                                    }
                                    for (int j = 0; j < size; j++) {
                                        Auth2Bean.VipBean vipBean = specialList.get(j);
                                        String v = vipBean.getCode();
                                        if (null == v || v.length() <= 0)
                                            continue;
                                        if (v.equals(code)) {
                                            data.setVipPassZhuanQu(true);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                            }
                        }
                        return data;
                    }
                })
                // 播放器
                .map(new Function<ProgramInfoDetail, ProgramInfoDetail>() {
                    @Override
                    public ProgramInfoDetail apply(ProgramInfoDetail data) {
                        try {
                            DetailTemplateXuanJi.DetailTemplateXuanJiList list = new DetailTemplateXuanJi.DetailTemplateXuanJiList();
                            String cndUrl = null;
                            List<Media> medias = data.getMedias();
                            int size = medias.size();
                            for (int i = 0; i < size; i++) {
                                Media media = medias.get(i);
                                if (null == media) {
                                    continue;
                                }
                                if (i == 0) {
                                    cndUrl = media.getCdnUrl();
                                }
                                media.setIndex(i);
                                media.setTop(true);
                                media.setName(String.valueOf(i + 1));
                                media.setHead("选集列表");
                                list.add(media);
                            }
                            for (int i = 0; i < size; i += 10) {
                                Media media1 = new Media();
                                int i1 = (i / 10) * 10 + 1;
                                int i2 = i1 + 9;
                                if (i2 >= size) {
                                    i2 = size;
                                }
                                media1.setTop(false);
                                media1.setIndex(i1);
                                media1.setName(i1 + "-" + i2);
                                media1.setHead("选集列表");
                                list.add(media1);
                            }

                            VerticalGridView verticalGridView = getView().findViewById(R.id.detail_list);
                            RecyclerView.Adapter adapter = verticalGridView.getAdapter();
                            ObjectAdapter objectAdapter = ((ItemBridgeAdapter) adapter).getAdapter();

                            DetailTemplatePlayer.DetailTemplatePlayerObject object = new DetailTemplatePlayer.DetailTemplatePlayerObject();

                            // aaaMediaAuth
                            boolean aaaPass = false;
                            if (BuildConfig.HUAN_AAA) {
                                try {
                                    boolean vipFree = data.isVipFree();
                                    if (vipFree) {
                                        aaaPass = true;
                                    } else {
                                        // 关联产品编码计费包
                                        ProgramDetail album = data.getAlbum();
                                        List<ProgramDetail.Item> productCodes = album.getProductCodes();
                                        int num = productCodes.size();
                                        for (int i = 0; i < num; i++) {
                                            if (aaaPass)
                                                break;
                                            ProgramDetail.Item item = productCodes.get(i);
                                            if (null == item)
                                                continue;
                                            String code = item.getCode();
                                            if (null == code || code.length() <= 0)
                                                continue;
                                            try {
                                                aaaPass = StarcorUtil.mediaAuth(code);
                                            } catch (Exception e) {
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    aaaPass = false;
                                }
                            } else {
                                aaaPass = true;
                            }
                            if (aaaPass) {
                                object.setCdnUrl(cndUrl);
                            } else {
                                object.setCdnUrl(null);
                            }
                            ((ArrayObjectAdapter) objectAdapter).add(object);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return data;
                    }
                })
                // 选期列表
                .map(new Function<ProgramInfoDetail, ProgramInfoDetail>() {
                    @Override
                    public ProgramInfoDetail apply(ProgramInfoDetail data) {


                        boolean show;
                        try {
                            List<Media> medias = data.getMedias();
                            ProgramDetail detail = data.getAlbum();

                            // 容错1
                            if (detail.getType() != Constants.AlbumType.FILM && medias == null) {
                                show = false;
                            }
                            // 容错2
                            else if (detail.getType() != Constants.AlbumType.FILM && medias.size() == 0) {
                                show = false;
                            }
                            // 电影
                            else if (detail.getType() == Constants.AlbumType.FILM) {
                                show = false;
                            }
                            //  选期 => 教育、体育、综艺
                            else if (detail.getType() == Constants.AlbumType.EDUCATION || detail.getType() == Constants.AlbumType.SPORTS || detail.getType() == Constants.AlbumType.VARIETY) {
                                show = true;
                            }
                            // 选集
                            else {
                                show = false;
                            }
                        } catch (Exception e) {
                            show = false;
                        }

                        if (show) {
                            try {
                                VerticalGridView verticalGridView = getView().findViewById(R.id.detail_list);
                                RecyclerView.Adapter adapter = verticalGridView.getAdapter();
                                ObjectAdapter objectAdapter = ((ItemBridgeAdapter) adapter).getAdapter();
                                DetailTemplateXuanQi.DetailTemplateXuanQiList list = new DetailTemplateXuanQi.DetailTemplateXuanQiList();
                                List<Media> medias = data.getMedias();
                                int size = medias.size();
                                for (int i = 0; i < size; i++) {
                                    Media media = medias.get(i);
                                    if (null == media) {
                                        continue;
                                    }
                                    media.setIndex(i);
                                    media.setHead("选期列表");
                                    list.add(media);
                                }
                                ((ArrayObjectAdapter) objectAdapter).add(list);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        return data;
                    }
                })
                // 选集列表
                .map(new Function<ProgramInfoDetail, ProgramInfoDetail>() {
                    @Override
                    public ProgramInfoDetail apply(ProgramInfoDetail data) {

                        boolean show;
                        try {
                            List<Media> medias = data.getMedias();
                            ProgramDetail detail = data.getAlbum();

                            // 容错1
                            if (detail.getType() != Constants.AlbumType.FILM && medias == null) {
                                show = false;
                            }
                            // 容错2
                            else if (detail.getType() != Constants.AlbumType.FILM && medias.size() == 0) {
                                show = false;
                            }
                            // 电影
                            else if (detail.getType() == Constants.AlbumType.FILM) {
                                show = false;
                            }
                            //  选期 => 教育、体育、综艺
                            else if (detail.getType() == Constants.AlbumType.EDUCATION || detail.getType() == Constants.AlbumType.SPORTS || detail.getType() == Constants.AlbumType.VARIETY) {
                                show = false;
                            }
                            // 选集
                            else {
                                show = true;
                            }
                        } catch (Exception e) {
                            show = false;
                        }

                        if (show) {
                            try {
                                VerticalGridView verticalGridView = getView().findViewById(R.id.detail_list);
                                RecyclerView.Adapter adapter = verticalGridView.getAdapter();
                                ObjectAdapter objectAdapter = ((ItemBridgeAdapter) adapter).getAdapter();
                                DetailTemplateXuanJi.DetailTemplateXuanJiList list = new DetailTemplateXuanJi.DetailTemplateXuanJiList();
                                List<Media> medias = data.getMedias();
                                int size = medias.size();
                                for (int i = 0; i < size; i++) {
                                    Media media = medias.get(i);
                                    if (null == media) {
                                        continue;
                                    }
                                    media.setIndex(i);
                                    media.setTop(true);
                                    media.setName(String.valueOf(i + 1));
                                    media.setHead("选集列表");
                                    list.add(media);
                                }
                                for (int i = 0; i < size; i += 10) {
                                    Media media1 = new Media();
                                    int i1 = (i / 10) * 10 + 1;
                                    int i2 = i1 + 9;
                                    if (i2 >= size) {
                                        i2 = size;
                                    }
                                    media1.setTop(false);
                                    media1.setIndex(i1);
                                    media1.setName(i1 + "-" + i2);
                                    media1.setHead("选集列表");
                                    list.add(media1);
                                }
                                ((ArrayObjectAdapter) objectAdapter).add(list);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        return data;
                    }
                })
                // 猜你喜欢
                .map(new Function<ProgramInfoDetail, ProgramInfoDetail>() {
                    @Override
                    public ProgramInfoDetail apply(ProgramInfoDetail data) {
                        try {
                            VerticalGridView verticalGridView = getView().findViewById(R.id.detail_list);
                            RecyclerView.Adapter adapter = verticalGridView.getAdapter();
                            ObjectAdapter objectAdapter = ((ItemBridgeAdapter) adapter).getAdapter();
                            DetailTemplateFav.DetailTemplateFavList list = new DetailTemplateFav.DetailTemplateFavList();
                            List<Album> recAlbums = data.getRecAlbums();
                            int size = recAlbums.size();
                            for (int i = 0; i < size; i++) {
                                Album album = recAlbums.get(i);
                                if (null == album)
                                    continue;
                                album.setHead("猜你喜欢");
                                list.add(album);
                            }
                            ((ArrayObjectAdapter) objectAdapter).add(list);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return data;
                    }
                })
                // 是否显示vip按钮
                .map(new Function<ProgramInfoDetail, Boolean>() {
                    @Override
                    public Boolean apply(ProgramInfoDetail programInfoDetail) {

                        boolean show;
                        try {

                            boolean passZhouQi = programInfoDetail.isVipPassZhouQi();
                            boolean linkZhouQi = programInfoDetail.isVipLinkZhouQi();

                            boolean linkZhuanQu = programInfoDetail.isVipLinkZhuanQu();
                            boolean passZhuanQu = programInfoDetail.isVipPassZhuanQu();

                            boolean linkDanDian = programInfoDetail.isVipLinkDanDian();
                            boolean passDanDian = programInfoDetail.isVipPassDanDian();

                            // 周期会员
                            if (linkZhouQi && passZhouQi) {
                                show = false;
                            }
                            // 专区会员
                            else if (linkZhuanQu && passZhuanQu) {
                                show = false;
                            }
                            // 单点会员
                            else if (linkDanDian && passDanDian) {
                                show = false;
                            }
                            // 存在关联, 未购买
                            else if (linkZhouQi || linkZhuanQu || linkDanDian) {
                                show = true;
                            }
                            // 其他情况, 隐藏
                            else {
                                show = false;
                            }
                        } catch (Exception e) {
                            show = false;
                        }
                        return show;
                    }
                })
                .delay(40, TimeUnit.MILLISECONDS)
                .compose(ComposeSchedulers.io_main())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) {
                        getView().showLoading();
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        getView().hideLoading();
                    }
                })
                .doOnNext(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) {
                        getView().hideLoading();
                        // 1
                        getView().refreshContent();
                        // 2
                        getView().updateVip(aBoolean);
                    }
                })
                .subscribe());
    }
}