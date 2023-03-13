package tv.huan.bilibili.ui.detail;

import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.ObjectAdapter;
import androidx.leanback.widget.VerticalGridView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import lib.kalu.frame.mvp.transformer.ComposeSchedulers;
import tv.huan.bilibili.R;
import tv.huan.bilibili.base.BasePresenterImpl;
import tv.huan.bilibili.bean.Auth2BeanBase;
import tv.huan.bilibili.bean.FavorBean;
import tv.huan.bilibili.bean.GetMediasByCid2Bean;
import tv.huan.bilibili.bean.MediaBean;
import tv.huan.bilibili.bean.MediaDetailBean;
import tv.huan.bilibili.bean.RecMediaBean;
import tv.huan.bilibili.bean.base.BaseResponsedBean;
import tv.huan.bilibili.bean.format.DetailBean;
import tv.huan.bilibili.bean.format.OptBean;
import tv.huan.bilibili.http.HttpClient;
import tv.huan.bilibili.ui.detail.template.DetailTemplateFavor;
import tv.huan.bilibili.ui.detail.template.DetailTemplatePlayer;
import tv.huan.bilibili.ui.detail.template.DetailTemplateXuanJi;
import tv.huan.bilibili.ui.detail.template.DetailTemplateXuanQi;
import tv.huan.bilibili.utils.LogUtil;
import tv.huan.bilibili.widget.DetailGridView;
import tv.huan.bilibili.widget.player.PlayerView;

public class DetailPresenter extends BasePresenterImpl<DetailView> {
    public DetailPresenter(@NonNull DetailView detailView) {
        super(detailView);
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
                .flatMap(new Function<Boolean, ObservableSource<BaseResponsedBean<GetMediasByCid2Bean>>>() {
                    @Override
                    public ObservableSource<BaseResponsedBean<GetMediasByCid2Bean>> apply(Boolean o) {
                        String cid = getView().getStringExtra(DetailActivity.INTENT_CID);
                        if (null == cid) {
                            cid = "";
                        }
                        return HttpClient.getHttpClient().getHttpApi().getMediasByCid2(cid);
                    }
                })
                // 媒资数据 => 数据处理
                .map(new Function<BaseResponsedBean<GetMediasByCid2Bean>, DetailBean>() {
                    @Override
                    public DetailBean apply(BaseResponsedBean<GetMediasByCid2Bean> getMediasByCid2BeanBaseResponsedBean) {
                        // 上报 => 详情页加载完成
                        try {
                            String cid = getView().getStringExtra(DetailActivity.INTENT_CID, null);
                            if (null != cid && cid.length() > 0) {
                                reportDetailLoadFinished(cid);
                            }
                        } catch (Exception e) {
                        }
                        // 上报 => 来自搜索
                        try {
                            boolean booleanExtra = getView().getBooleanExtra(DetailActivity.INTENT_FROM_SEARCH, false);
                            if (booleanExtra) {
                                String keys = getView().getStringExtra(DetailActivity.INTENT_FROM_SEARCH_KEY, null);
                                if (null != keys && keys.length() > 0) {
                                    String cid = getView().getStringExtra(DetailActivity.INTENT_CID, null);
                                    if (null != cid && cid.length() > 0) {
                                        reportSearchResultItemClicked(keys, cid);
                                    }
                                }
                            }
                        } catch (Exception e) {
                        }
                        // 上报 => 来自挽留
                        try {
                            boolean booleanExtra = getView().getBooleanExtra(DetailActivity.INTENT_FROM_WANLIU, false);
                            if (booleanExtra) {
                                String keys = getView().getStringExtra(DetailActivity.INTENT_FROM_WANLIU_KEY, null);
                                if (null != keys && keys.length() > 0) {
                                    String cid = getView().getStringExtra(DetailActivity.INTENT_CID, null);
                                    if (null != cid && cid.length() > 0) {
                                        reportExitRetentionClick(cid, keys);
                                    }
                                }
                            }
                        } catch (Exception e) {
                        }
                        // 上报 => 来自专题
                        try {
                            boolean booleanExtra = getView().getBooleanExtra(DetailActivity.INTENT_FROM_SPECIAL, false);
                            if (booleanExtra) {
                                int sceneId = getView().getIntExtra(DetailActivity.INTENT_FROM_SPECIAL_SCENEID, -1);
                                int topId = getView().getIntExtra(DetailActivity.INTENT_FROM_SPECIAL_TOPID, -1);
                                String topName = getView().getStringExtra(DetailActivity.INTENT_FROM_SPECIAL_TOPNAME, null);
                                if (null != topName && topName.length() > 0 && sceneId != -1 && topId != -1) {
                                    String cid = getView().getStringExtra(DetailActivity.INTENT_CID, null);
                                    if (null != cid && cid.length() > 0) {
                                        reportTopicEnterDetail(cid, sceneId, topId, topName);
                                    }
                                }
                            }
                        } catch (Exception e) {
                        }

                        DetailBean detailBean = new DetailBean();

                        // recClassId
                        try {
                            detailBean.setRecClassId(getMediasByCid2BeanBaseResponsedBean.getData().getRecClassId());
                        } catch (Exception e) {
                            detailBean.setRecClassId("");
                        }

                        // 免费/收费
                        try {
                            GetMediasByCid2Bean data = getMediasByCid2BeanBaseResponsedBean.getData();
                            MediaDetailBean album = data.getAlbum();
                            if (0 == album.getProductType()) {
                                detailBean.setVip(true);
                            } else {
                                detailBean.setVip(false);
                            }
                        } catch (Exception e) {
                            detailBean.setVip(false);
                        }

                        // 播放策略
                        try {
                            GetMediasByCid2Bean data = getMediasByCid2BeanBaseResponsedBean.getData();
                            MediaDetailBean album = data.getAlbum();
                            detailBean.setPlayType(album.getPlayType());
                        } catch (Exception e) {
                            detailBean.setPlayType(0);
                        }

                        // 详情
                        try {
                            GetMediasByCid2Bean data = getMediasByCid2BeanBaseResponsedBean.getData();
                            detailBean.setAlbum(data.getAlbum());
                        } catch (Exception e) {
                            detailBean.setAlbum(null);
                        }

                        // 剧集
                        try {
                            GetMediasByCid2Bean data = getMediasByCid2BeanBaseResponsedBean.getData();
                            detailBean.setMedias(data.getMedias());
                        } catch (Exception e) {
                            detailBean.setMedias(null);
                        }

                        // 猜你喜欢
                        try {
                            GetMediasByCid2Bean data = getMediasByCid2BeanBaseResponsedBean.getData();
                            detailBean.setRecAlbums(data.getRecAlbums());
                        } catch (Exception e) {
                            detailBean.setAlbum(null);
                        }
                        return detailBean;
                    }
                })
                // 白名单
                .flatMap(new Function<DetailBean, ObservableSource<Auth2BeanBase>>() {
                    @Override
                    public ObservableSource<Auth2BeanBase> apply(DetailBean detailBean) {
                        String s = new Gson().toJson(detailBean);
                        String cid = getView().getStringExtra(DetailActivity.INTENT_CID, null);
                        return HttpClient.getHttpClient().getHttpApi().auth2(cid, s);
                    }
                })
                // 白名单 => 数据处理
                .map(new Function<Auth2BeanBase, DetailBean>() {
                    @Override
                    public DetailBean apply(Auth2BeanBase auth2Bean) {

                        DetailBean detailBean;
                        try {
                            detailBean = new Gson().fromJson(auth2Bean.getExtra(), DetailBean.class);
                        } catch (Exception e) {
                            detailBean = new DetailBean();
                        }

                        // 白名单
                        try {
                            boolean whiteUser = auth2Bean.isWhiteUser();
                            if (whiteUser) {
                                detailBean.setPlayType(Integer.MAX_VALUE);
                            }
                        } catch (Exception e) {
                        }

                        return detailBean;
                    }
                })
                // 收藏
                .flatMap(new Function<DetailBean, ObservableSource<BaseResponsedBean<FavorBean>>>() {
                    @Override
                    public ObservableSource<BaseResponsedBean<FavorBean>> apply(DetailBean detailBean) {
                        String s = new Gson().toJson(detailBean);
                        String cid = getView().getStringExtra(DetailActivity.INTENT_CID);
                        return HttpClient.getHttpClient().getHttpApi().checkFavorite(cid, s);
                    }
                })
                // 收藏 => 数据处理
                .map(new Function<BaseResponsedBean<FavorBean>, DetailBean>() {
                    @Override
                    public DetailBean apply(BaseResponsedBean<FavorBean> data) {

                        DetailBean detailBean;
                        try {
                            detailBean = new Gson().fromJson(data.getExtra(), DetailBean.class);
                        } catch (Exception e) {
                            detailBean = new DetailBean();
                        }

                        // 收藏状态
                        try {
                            detailBean.setFavor(data.getData().isFavor());
                        } catch (Exception e) {
                        }
                        return detailBean;
                    }
                })
                // 播放器 => 数据处理
                .map(new Function<DetailBean, DetailBean>() {
                    @Override
                    public DetailBean apply(DetailBean data) {
                        try {
                            DetailTemplatePlayer.DetailTemplatePlayerObject playerData = new DetailTemplatePlayer.DetailTemplatePlayerObject();
                            VerticalGridView verticalGridView = getView().findViewById(R.id.detail_list);
                            RecyclerView.Adapter adapter = verticalGridView.getAdapter();
                            ObjectAdapter objectAdapter = ((ItemBridgeAdapter) adapter).getAdapter();
                            ((ArrayObjectAdapter) objectAdapter).add(playerData);
                        } catch (Exception e) {
                        }
                        return data;
                    }
                })
                // 选期列表 => 数据处理
                .map(new Function<DetailBean, DetailBean>() {
                    @Override
                    public DetailBean apply(DetailBean data) {
                        try {
                            MediaDetailBean detail = data.getAlbum();
                            boolean xuanQi = detail.isXuanQi();
                            if (xuanQi) {
                                List<MediaBean> medias = data.getMedias();
                                if (null != medias) {
                                    int size = medias.size();
                                    if (size > 0) {
                                        // 1 上报 => 选期
                                        String cid = getView().getStringExtra(DetailActivity.INTENT_CID);
                                        reportDetailSelectionsButtonShow(cid);

                                        // 2
                                        DetailTemplateXuanQi.DetailTemplateXuanQiList xuanqiData = new DetailTemplateXuanQi.DetailTemplateXuanQiList();
                                        for (int i = 0; i < size; i++) {
                                            MediaBean bean = medias.get(i);
                                            if (null == bean)
                                                continue;
                                            bean.setTempIndex(i + 1);
                                            bean.setTempVip(data.isVip());
                                            bean.setTempFavor(data.isFavor());
                                            bean.setTempPlayType(data.getPlayType());
                                            bean.setTempTag(detail.getSplitTag());
                                            bean.setTempTitle(detail.getName());
                                            bean.setTemoInfo(detail.getInfo());
                                            bean.setTempRecClassId(data.getRecClassId());
                                            bean.setTempImageUrl(detail.getPicture(true));
                                            xuanqiData.add(bean);
                                        }
                                        VerticalGridView verticalGridView = getView().findViewById(R.id.detail_list);
                                        RecyclerView.Adapter adapter = verticalGridView.getAdapter();
                                        ObjectAdapter objectAdapter = ((ItemBridgeAdapter) adapter).getAdapter();
                                        ((ArrayObjectAdapter) objectAdapter).add(xuanqiData);
                                    }
                                }
                            }
                        } catch (Exception e) {
                        }
                        return data;
                    }
                })
                // 选集列表 => 数据处理
                .map(new Function<DetailBean, DetailBean>() {
                    @Override
                    public DetailBean apply(DetailBean data) {

                        try {
                            MediaDetailBean detail = data.getAlbum();
                            boolean xuanJi = detail.isXuanJi();
                            if (xuanJi) {
                                List<MediaBean> medias = data.getMedias();
                                if (null != medias) {
                                    int size = medias.size();
                                    if (size > 0) {

                                        // 上报
                                        String cid = getView().getStringExtra(DetailActivity.INTENT_CID);
                                        reportDetailSelectionsButtonShow(cid);

                                        // 2
                                        DetailTemplateXuanJi.DetailTemplateXuanJiList xuanjiData = new DetailTemplateXuanJi.DetailTemplateXuanJiList();
                                        for (int i = 0; i < size; i++) {
                                            MediaBean bean = medias.get(i);
                                            if (null == bean)
                                                continue;
                                            bean.setTempIndex(i + 1);
                                            bean.setTempVip(data.isVip());
                                            bean.setTempFavor(data.isFavor());
                                            bean.setTempPlayType(data.getPlayType());
                                            bean.setTempTag(detail.getSplitTag());
                                            bean.setTempTitle(detail.getName());
                                            bean.setTemoInfo(detail.getInfo());
                                            bean.setTempRecClassId(data.getRecClassId());
                                            bean.setTempImageUrl(detail.getPicture(true));
                                            xuanjiData.add(bean);
                                        }
                                        VerticalGridView verticalGridView = getView().findViewById(R.id.detail_list);
                                        RecyclerView.Adapter adapter = verticalGridView.getAdapter();
                                        ObjectAdapter objectAdapter = ((ItemBridgeAdapter) adapter).getAdapter();
                                        ((ArrayObjectAdapter) objectAdapter).add(xuanjiData);
                                    }
                                }
                            }
                        } catch (Exception e) {
                        }
                        return data;
                    }
                })
                // 猜你喜欢 => 数据处理
                .map(new Function<DetailBean, DetailBean>() {
                    @Override
                    public DetailBean apply(DetailBean data) {
                        try {
                            List<RecMediaBean> albums = data.getRecAlbums();
                            if (null != albums) {
                                int size = albums.size();
                                if (size > 0) {
                                    // 上报
                                    String cid = getView().getStringExtra(DetailActivity.INTENT_CID);
                                    reportDetailRecommendShow(cid);

                                    // 2
                                    DetailTemplateFavor.DetailTemplateFavList favorData = new DetailTemplateFavor.DetailTemplateFavList();
                                    for (int i = 0; i < size; i++) {
                                        MediaBean bean = albums.get(i);
                                        if (null == bean)
                                            continue;
                                        favorData.add(bean);
                                    }
                                    VerticalGridView verticalGridView = getView().findViewById(R.id.detail_list);
                                    RecyclerView.Adapter adapter = verticalGridView.getAdapter();
                                    ObjectAdapter objectAdapter = ((ItemBridgeAdapter) adapter).getAdapter();
                                    ((ArrayObjectAdapter) objectAdapter).add(favorData);
                                }
                            }
                        } catch (Exception e) {
                        }
                        return data;
                    }
                })
                // 默认position => 数据处理
                .map(new Function<DetailBean, MediaBean>() {
                    @Override
                    public MediaBean apply(DetailBean data) {
                        try {
                            int position = getView().getIntExtra(DetailActivity.INTENT_POSITION, 0);
                            List<MediaBean> medias = data.getMedias();
                            return medias.get(position);
                        } catch (Exception e) {
                            MediaBean bean = new MediaBean();
                            bean.setTempIndex(-1);
                            bean.setTempVip(data.isVip());
                            bean.setTempFavor(data.isFavor());
                            bean.setTempPlayType(data.getPlayType());
                            bean.setTempRecClassId(data.getRecClassId());
                            bean.setTempTag(data.getAlbum().getSplitTag());
                            bean.setTempTitle(data.getAlbum().getName());
                            bean.setTemoInfo(data.getAlbum().getInfo());
                            bean.setTempImageUrl(data.getAlbum().getPicture(true));
                            bean.setTempVideoUrl(null);
                            return bean;
                        }
                    }
                })
                .delay(40, TimeUnit.MILLISECONDS)
                .compose(ComposeSchedulers.io_main())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) {
                        getView().showLoading();
                    }
                }).doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        getView().hideLoading();
                        getView().refreshContent();
                    }
                }).doOnNext(new Consumer<MediaBean>() {
                    @Override
                    public void accept(MediaBean data) {
                        getView().hideLoading();
                        getView().updateVidAndClassId(data);
                        getView().refreshContent();
                        getView().updateData(data, false);
                        getView().delayPlayer(data, false);
                    }
                }).subscribe());
    }

    protected void addFavor(@NonNull String cid, @NonNull String recClassId) {

        addDisposable(Observable.create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(ObservableEmitter<Boolean> observableEmitter) {
                        observableEmitter.onNext(true);
                    }
                })
                .flatMap(new Function<Boolean, Observable<BaseResponsedBean<FavorBean>>>() {
                    @Override
                    public Observable<BaseResponsedBean<FavorBean>> apply(Boolean aBoolean) {
                        return HttpClient.getHttpClient().getHttpApi().addFavorite(cid, recClassId);
                    }
                })
                .map(new Function<BaseResponsedBean<FavorBean>, Boolean>() {
                    @Override
                    public Boolean apply(BaseResponsedBean<FavorBean> favorBeanBaseResponsedBean) {
                        try {
                            return favorBeanBaseResponsedBean.getData().isFavor();
                        } catch (Exception e) {
                            return false;
                        }
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
                        getView().updateFavor(true);
                    }
                })
                .subscribe());
    }

    protected void cancleFavor(@NonNull String cid) {

        addDisposable(Observable.create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(ObservableEmitter<Boolean> observableEmitter) {
                        observableEmitter.onNext(true);
                    }
                })
                .flatMap(new Function<Boolean, Observable<BaseResponsedBean<OptBean>>>() {
                    @Override
                    public Observable<BaseResponsedBean<OptBean>> apply(Boolean aBoolean) {
                        return HttpClient.getHttpClient().getHttpApi().cancelFavorite(cid);
                    }
                })
                .map(new Function<BaseResponsedBean<OptBean>, Boolean>() {
                    @Override
                    public Boolean apply(BaseResponsedBean<OptBean> resp) {
                        return resp.getData().isSucc();
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
                        getView().updateFavor(false);
                    }
                })
                .subscribe());
    }

    protected boolean dispatchEvent(KeyEvent event) {
        // left right up down
        if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
            PlayerView playerView = getView().findViewById(R.id.detail_player_item_video);
            boolean isFull = playerView.isFull();
            // full
            if (isFull) {
                playerView.dispatchKeyEvent(event);
                return true;
            }
            // down
            else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                int focusId = getView().getCurrentFocusId();
                if (focusId == R.id.detail_player_item_vip || focusId == R.id.detail_player_item_full || focusId == R.id.detail_player_item_favor) {
                    DetailGridView gridView = getView().findViewById(R.id.detail_list);
                    boolean containsXuanJi = gridView.containsXuanJi();
                    if (containsXuanJi) {
                        gridView.requestFocusEpisodePlayingItem();
                        return true;
                    }
                }
            }
        }
        // back
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            try {
                DetailGridView gridView = getView().findViewById(R.id.detail_list);
                long position = gridView.getPlayerPosition();
                long duration = gridView.getPlayerDuraion();
                LogUtil.log("DetailPresenter => onBack => duration = " + duration);
                LogUtil.log("DetailPresenter => onBack => position = " + position);
                getView().putLongExtra(DetailActivity.INTENT_CUR_POSITION, position);
                getView().putLongExtra(DetailActivity.INTENT_CUR_DURATION, duration);
                getView().callOnBackPressed();
                return true;
            } catch (Exception e) {
            }
        }
        return false;
    }

    protected final void delayPlayer(@NonNull MediaBean data, boolean isFromUser) {
        addDisposable(Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {

                        String videoUrl = data.getTempVideoUrl();
                        if (null == videoUrl || videoUrl.length() <= 0) {
                            throw new Exception("视频url错误");
                        } else {
                            boolean isVip = data.isTempVip();
                            int playType = data.getTempPlayType();
                            int index = data.getTempIndex();
                            if (!isVip && playType <= 0) {
                                throw new Exception("需要开通vip");
                            } else if (!isVip && index > playType) {
                                throw new Exception("第" + index + "集, 需要开通vip");
                            } else {
                                if (!isVip && index > playType) {
                                    emitter.onNext(1);
                                } else {
                                    if (isFromUser) {
                                        long seek = getView().getLongExtra(DetailActivity.INTENT_SEEK, 0);
                                        if (seek > 0) {
                                            getView().putLongExtra(DetailActivity.INTENT_SEEK, 0);
                                            data.setTempSeek(seek);
                                        }
                                    }
                                    emitter.onNext(isFromUser ? 21 : 22);
                                }
                            }
                        }
                    }
                })
//                .delay(isFromUser ? 4000 : 1, TimeUnit.MILLISECONDS)
                .compose(ComposeSchedulers.io_main())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        getView().showToast(throwable);
                    }
                })
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer v) {
                        if (v == 21) {
                            getView().startPlayer(data);
                        } else if (v == 22) {
                            getView().selectPosition(data);
                        } else if (v == 1) {
                            getView().jumpVip();
                        }
                    }
                })
                .subscribe());
    }

    protected void checkPlayerNext() {
        LogUtil.log("DetailPresenter => checkPlayerNext =>");
        addDisposable(Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                        DetailGridView gridView = getView().findViewById(R.id.detail_list);
                        boolean playingEnd = gridView.isPlayingEnd();
                        if (playingEnd)
                            throw new Exception("播放结束");
                        int nextPosition = gridView.getPlayerNextPosition();
                        if (nextPosition < 0)
                            throw new Exception("播放结束1");
                        emitter.onNext(nextPosition + 1);
                    }
                })
                .compose(ComposeSchedulers.io_main())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        getView().showToast(throwable);
                        getView().stopFull();
                    }
                })
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) {
                        String s = getView().getString(R.string.detail_warning_next, integer);
                        LogUtil.log("DetailPresenter => checkPlayerNext => s = " + s);
                        getView().showToast(s);
                        getView().startPlayerNext1();
                    }
                })
                .subscribe());
    }

    protected void uploadBackupPress() {
        addDisposable(Observable.create(new ObservableOnSubscribe<Boolean>() {
                    // 上报数据
                    @Override
                    public void subscribe(ObservableEmitter<Boolean> emitter) {

                        String cid = getView().getStringExtra(DetailActivity.INTENT_CID, "");
                        String vid = getView().getStringExtra(DetailActivity.INTENT_VID, "");
                        // 1
                        long start = getView().getLongExtra(DetailActivity.INTENT_START_TIME, 0);
                        long end = System.currentTimeMillis();
                        reportPlayVodStop(cid, vid, start, end);
                        // 2
                        long duration = getView().getLongExtra(DetailActivity.INTENT_CUR_DURATION, 0);
                        long position = getView().getLongExtra(DetailActivity.INTENT_CUR_POSITION, 0);
                        boolean isEnd = position > 0 && duration > 0 && position >= duration;
                        String classId = getView().getStringExtra(DetailActivity.INTENT_REC_CLASSID, "");
                        int pos = getView().getIntExtra(DetailActivity.INTENT_INDEX, 1);
                        int endFlag = isEnd ? 0 : 1;
                        uploadPlayHistory(cid, vid, classId, pos, endFlag, duration, position);
                        // 3
                        emitter.onNext(true);
                    }
                })
                .compose(ComposeSchedulers.io_main())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        getView().callFinish();
                    }
                })
                .doOnNext(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) {
                        getView().callFinish();
                    }
                })
                .subscribe());
    }
}
