package tv.huan.bilibili.ui.main.general;

import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.ObjectAdapter;
import androidx.leanback.widget.VerticalGridView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import java.util.Collection;
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
import lib.kalu.frame.mvp.transformer.ComposeSchedulers;
import tv.huan.bilibili.BuildConfig;
import tv.huan.bilibili.R;
import tv.huan.bilibili.base.BasePresenterImpl;
import tv.huan.bilibili.bean.FavBean;
import tv.huan.bilibili.bean.GetSubChannelsByChannelBean;
import tv.huan.bilibili.bean.base.BaseResponsedBean;
import tv.huan.bilibili.bean.format.CallGeneralBean;
import tv.huan.bilibili.http.HttpClient;
import tv.huan.bilibili.ui.main.general.template.GeneralTemplate1;
import tv.huan.bilibili.ui.main.general.template.GeneralTemplate10;
import tv.huan.bilibili.ui.main.general.template.GeneralTemplate11;
import tv.huan.bilibili.ui.main.general.template.GeneralTemplate12;
import tv.huan.bilibili.ui.main.general.template.GeneralTemplate13;
import tv.huan.bilibili.ui.main.general.template.GeneralTemplate14;
import tv.huan.bilibili.ui.main.general.template.GeneralTemplate15;
import tv.huan.bilibili.ui.main.general.template.GeneralTemplate16;
import tv.huan.bilibili.ui.main.general.template.GeneralTemplate17;
import tv.huan.bilibili.ui.main.general.template.GeneralTemplate18;
import tv.huan.bilibili.ui.main.general.template.GeneralTemplate19;
import tv.huan.bilibili.ui.main.general.template.GeneralTemplate2;
import tv.huan.bilibili.ui.main.general.template.GeneralTemplate20;
import tv.huan.bilibili.ui.main.general.template.GeneralTemplate21;
import tv.huan.bilibili.ui.main.general.template.GeneralTemplate22;
import tv.huan.bilibili.ui.main.general.template.GeneralTemplate3;
import tv.huan.bilibili.ui.main.general.template.GeneralTemplate4;
import tv.huan.bilibili.ui.main.general.template.GeneralTemplate5;
import tv.huan.bilibili.ui.main.general.template.GeneralTemplate6;
import tv.huan.bilibili.ui.main.general.template.GeneralTemplate7;
import tv.huan.bilibili.ui.main.general.template.GeneralTemplate8;
import tv.huan.bilibili.ui.main.general.template.GeneralTemplate9;
import tv.huan.bilibili.ui.main.general.template.GeneralTemplateBottom;
import tv.huan.bilibili.ui.main.general.template.GeneralTemplateClass;
import tv.huan.bilibili.widget.GeneralGridView;

@Keep
public class GeneralPresenter extends BasePresenterImpl<GeneralView> {
    public GeneralPresenter(@NonNull GeneralView commonView) {
        super(commonView);
    }

    protected final void setAdapter() {
        VerticalGridView verticalGridView = getView().findViewById(R.id.general_list);
        GeneralSelectorPresenter selectorPresenter = new GeneralSelectorPresenter();
        ArrayObjectAdapter objectAdapter = new ArrayObjectAdapter(selectorPresenter);
        ItemBridgeAdapter bridgeAdapter = new ItemBridgeAdapter(objectAdapter);
        verticalGridView.setAdapter(bridgeAdapter);
    }

    protected final void request() {

        addDisposable(Observable.create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(ObservableEmitter<Boolean> observableEmitter) {
                        observableEmitter.onNext(true);
                    }
                })
                // 瀑布流
                .flatMap(new Function<Boolean, Observable<BaseResponsedBean<GetSubChannelsByChannelBean>>>() {
                    @Override
                    public Observable<BaseResponsedBean<GetSubChannelsByChannelBean>> apply(Boolean b) {
                        int channelId = getView().getIntExtra(GeneralFragment.BUNDLE_CHANNELID, 0);
                        return HttpClient.getHttpClient().getHttpApi().getSubChannelsByChannel(channelId, 0, Integer.MAX_VALUE);
                    }
                })
                // 瀑布流 => 数据处理
                .map(new Function<BaseResponsedBean<GetSubChannelsByChannelBean>, CallGeneralBean>() {
                    @Override
                    public CallGeneralBean apply(BaseResponsedBean<GetSubChannelsByChannelBean> data) {

                        CallGeneralBean generalBean = new CallGeneralBean();

                        // classId
                        int classId = getView().getIntExtra(GeneralFragment.BUNDLE_CLASSID, 0);
                        generalBean.setClassId(classId);

                        // 瀑布流
                        try {
                            List<GetSubChannelsByChannelBean.ListBean> list = data.getData().getList();
                            if (null == list || list.size() <= 0)
                                throw new Exception();
                            LinkedList<GetSubChannelsByChannelBean.ListBean> listBeans = new LinkedList<>();
                            for (GetSubChannelsByChannelBean.ListBean t : list) {
                                if (null == t)
                                    continue;
                                List<GetSubChannelsByChannelBean.ListBean.TemplateBean> templateData = t.getTemplateData();
                                if (null == templateData || templateData.size() <= 0)
                                    continue;
                                for (GetSubChannelsByChannelBean.ListBean.TemplateBean t2 : templateData) {
                                    if (null == t2)
                                        continue;
                                    t2.setClassId(classId);
                                }
                                listBeans.add(t);
                            }
                            if (null == listBeans || listBeans.size() <= 0)
                                throw new Exception();
                            generalBean.setTemplateDatas(listBeans);
                        } catch (Exception e) {
                            generalBean.setTemplateDatas(null);
                        }

                        // 上报
//                        try {
//                            int channelId = getView().getIntExtra(GeneralFragment.BUNDLE_CHANNELID, 0);
//                            String name = getView().getStringExtra(GeneralFragment.BUNDLE_NAME);
//                            reportChannelLoadFinished(channelId, name);
//                        } catch (Exception e) {
//                        }

                        return generalBean;
                    }
                })
                // 二级菜单
                .flatMap(new Function<CallGeneralBean, ObservableSource<BaseResponsedBean<GetSubChannelsByChannelBean>>>() {
                    @Override
                    public ObservableSource<BaseResponsedBean<GetSubChannelsByChannelBean>> apply(CallGeneralBean data) {

                        if (null == data.getTemplateDatas()) {
                            return Observable.create(new ObservableOnSubscribe<BaseResponsedBean<GetSubChannelsByChannelBean>>() {
                                @Override
                                public void subscribe(ObservableEmitter<BaseResponsedBean<GetSubChannelsByChannelBean>> emitter) {
                                    BaseResponsedBean responsedBean = new BaseResponsedBean();
                                    responsedBean.setExtra(new Gson().toJson(data));
                                    emitter.onNext(responsedBean);
                                }
                            });
                        } else {
                            int classId = data.getClassId();
                            String s = new Gson().toJson(data);
                            return HttpClient.getHttpClient().getHttpApi().getClassByPrentId(classId, s);
                        }
                    }
                })
                // 二级菜单 => 数据处理
                .map(new Function<BaseResponsedBean<GetSubChannelsByChannelBean>, CallGeneralBean>() {
                    @Override
                    public CallGeneralBean apply(BaseResponsedBean<GetSubChannelsByChannelBean> resp) {
                        CallGeneralBean generalBean;
                        try {
                            generalBean = new Gson().fromJson(resp.getExtra(), CallGeneralBean.class);
                        } catch (Exception e) {
                            generalBean = new CallGeneralBean();
                        }

                        try {
                            List<GetSubChannelsByChannelBean.ClassesBean> classes = resp.getData().getClasses();
                            if (classes.size() > 0) {
                                LinkedList<GetSubChannelsByChannelBean.ListBean.TemplateBean> templateCalsss = new LinkedList<>();
                                for (int n = 0; n < /*5*/classes.size(); n++) {
                                    GetSubChannelsByChannelBean.ClassesBean temp = classes.get(n);
                                    if (null == temp)
                                        continue;
                                    String name = temp.getName();
                                    GetSubChannelsByChannelBean.ListBean.TemplateBean bean = new GetSubChannelsByChannelBean.ListBean.TemplateBean();
                                    bean.setName(name);
                                    bean.setClassId(resp.getData().getParent().getId());
                                    bean.setToType(2);
                                    templateCalsss.add(bean);
                                }
                                // 1
                                GetSubChannelsByChannelBean.ListBean.TemplateBean bean = new GetSubChannelsByChannelBean.ListBean.TemplateBean();
                                bean.setName("更多分类");
                                bean.setClassId(resp.getData().getParent().getId());
                                bean.setToType(2);
                                templateCalsss.add(bean);
                                // 2
                                GetSubChannelsByChannelBean.ListBean classDatas = new GetSubChannelsByChannelBean.ListBean();
                                classDatas.setPreTemplate(-1);
                                classDatas.setTemplateData(templateCalsss);
                                // 3
                                List<GetSubChannelsByChannelBean.ListBean> templateDatas = generalBean.getTemplateDatas();
                                templateDatas.add(1, classDatas);
                                // 4
                                generalBean.setTemplateDatas(templateDatas);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return generalBean;
                    }
                })
                // 观看记录
                .flatMap(new Function<CallGeneralBean, Observable<BaseResponsedBean<FavBean>>>() {
                    @Override
                    public Observable<BaseResponsedBean<FavBean>> apply(CallGeneralBean data) {

                        try {
                            List<GetSubChannelsByChannelBean.ListBean> templateDatas = data.getTemplateDatas();
                            for (GetSubChannelsByChannelBean.ListBean t : templateDatas) {
                                if (null == t)
                                    continue;
                                int preTemplate = t.getPreTemplate();
                                // 模板1, 模板16, 模板17, 模板20
                                if (1 == preTemplate || 16 == preTemplate || 17 == preTemplate || 19 == preTemplate) {
                                    String s = new Gson().toJson(data);
                                    return HttpClient.getHttpClient().getHttpApi().getBookmark(0, 5, s);
                                }
                            }
                            throw new Exception();
                        } catch (Exception e) {
                            return Observable.create(new ObservableOnSubscribe<BaseResponsedBean<FavBean>>() {
                                @Override
                                public void subscribe(ObservableEmitter<BaseResponsedBean<FavBean>> emitter) {
                                    BaseResponsedBean responsedBean = new BaseResponsedBean();
                                    responsedBean.setExtra(new Gson().toJson(data));
                                    emitter.onNext(responsedBean);
                                }
                            });
                        }
                    }
                })
                // 观看记录 => 数据处理
                .map(new Function<BaseResponsedBean<FavBean>, CallGeneralBean>() {
                    @Override
                    public CallGeneralBean apply(BaseResponsedBean<FavBean> resp) {

                        CallGeneralBean data;
                        try {
                            data = new Gson().fromJson(resp.getExtra(), CallGeneralBean.class);
                        } catch (Exception e) {
                            data = new CallGeneralBean();
                        }

                        try {
                            List<FavBean.ItemBean> rows = resp.getData().getRows();
                            if (null == rows || rows.size() <= 0)
                                throw new Exception();
                            List<GetSubChannelsByChannelBean.ListBean> templateDatas = data.getTemplateDatas();
                            for (GetSubChannelsByChannelBean.ListBean t : templateDatas) {
                                if (null == t)
                                    continue;
                                int preTemplate = t.getPreTemplate();
                                LinkedList<GetSubChannelsByChannelBean.ListBean.TemplateBean> templateInserts;
                                // 模板1
                                if (1 == preTemplate) {
                                    List<GetSubChannelsByChannelBean.ListBean.TemplateBean> templateData = t.getTemplateData();
                                    if (null != templateData && templateData.size() > 0) {
                                        // 1
                                        GetSubChannelsByChannelBean.ListBean.TemplateBean bean = templateData.get(0);
                                        templateData.add(0, bean);
                                    }
                                }
                                // 模板16
                                else if (16 == preTemplate) {
                                    List<GetSubChannelsByChannelBean.ListBean.TemplateBean> templateData = t.getTemplateData();
                                    if (null != templateData && templateData.size() > 0) {
                                        // 1
                                        GetSubChannelsByChannelBean.ListBean.TemplateBean bean = templateData.get(0);
                                        templateData.add(0, bean);
                                        // 2
                                        GetSubChannelsByChannelBean.ListBean.TemplateBean recBean = new GetSubChannelsByChannelBean.ListBean.TemplateBean();
                                        recBean.setTempFav(resp.getData());
                                        recBean.setClassId(bean.getClassId());
                                        // 3
                                        templateData.add(0, recBean);
                                    }
                                }
                                // 模板17
                                else if (17 == preTemplate) {
                                    List<GetSubChannelsByChannelBean.ListBean.TemplateBean> templateData = t.getTemplateData();
                                    if (null != templateData && templateData.size() > 0) {
                                        GetSubChannelsByChannelBean.ListBean.TemplateBean bean = templateData.get(0);
                                        bean.setTempFav(resp.getData());
                                    }
                                    if (null != templateData && templateData.size() > 1) {
                                        GetSubChannelsByChannelBean.ListBean.TemplateBean bean = templateData.get(1);
                                        templateData.add(1, bean);
                                    }
                                }
                                // 模板20
                                else if (19 == preTemplate) {
                                    List<GetSubChannelsByChannelBean.ListBean.TemplateBean> templateData = t.getTemplateData();
                                    if (null != templateData && templateData.size() > 2) {
                                        GetSubChannelsByChannelBean.ListBean.TemplateBean recBean = new GetSubChannelsByChannelBean.ListBean.TemplateBean();
                                        recBean.setTempFav(resp.getData());
                                        recBean.setClassId(data.getClassId());
                                        templateData.add(2, recBean);
                                    }
                                }
                            }
                        } catch (Exception e) {
                        }
                        return data;
                    }
                })
                // 瀑布流 => 填充数据
                .map(new Function<CallGeneralBean, Boolean>() {
                    @Override
                    public Boolean apply(CallGeneralBean generalBean) {

                        try {
                            List<GetSubChannelsByChannelBean.ListBean> templateDatas = generalBean.getTemplateDatas();
                            if (null == templateDatas || templateDatas.size() <= 0)
                                throw new Exception();
                            VerticalGridView verticalGridView = getView().findViewById(R.id.general_list);
                            if (null == verticalGridView)
                                throw new Exception();
                            ObjectAdapter objectAdapter = ((ItemBridgeAdapter) verticalGridView.getAdapter()).getAdapter();
                            if (null == objectAdapter)
                                throw new Exception();
                            for (GetSubChannelsByChannelBean.ListBean t : templateDatas) {
                                if (null == t)
                                    continue;
                                List<GetSubChannelsByChannelBean.ListBean.TemplateBean> templateData = t.getTemplateData();
                                if (null == templateData || templateData.size() <= 0)
                                    continue;
                                // 测试模板
                                if (BuildConfig.HUAN_TEST_TEMPLATE_ENABLE) {
                                    for (int n = 22; n >= 1; n--) {
                                        addPresenter((ArrayObjectAdapter) objectAdapter, n, templateData);
                                    }
                                }
                                // 正常显示
                                else {
                                    int preTemplate = t.getPreTemplate();
                                    if (preTemplate <= 18) {
                                        addPresenter((ArrayObjectAdapter) objectAdapter, preTemplate, templateData);
                                    } else if (preTemplate == 19) {
                                        addPresenter((ArrayObjectAdapter) objectAdapter, 20, templateData);
                                    } else if (preTemplate == 20) {
                                        addPresenter((ArrayObjectAdapter) objectAdapter, 21, templateData);
                                    } else if (preTemplate == 21) {
                                        addPresenter((ArrayObjectAdapter) objectAdapter, 22, templateData);
                                    }
                                }
                            }
                            // 返回顶部
                            if (null != templateDatas && templateDatas.size() > 1) {
                                addPresenter((ArrayObjectAdapter) objectAdapter, -2, null);
                            }
                            return true;
                        } catch (Exception e) {
                            String poster = getView().getStringExtra(GeneralFragment.BUNDLE_POSTER);
                            return null != poster && poster.length() > 0;
                        }
                    }
                })
                .delay(40, TimeUnit.MILLISECONDS)
                .compose(ComposeSchedulers.io_main())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) {
                        getView().setVisibility(R.id.general_nodata, View.GONE);
                        getView().showLoading();
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        getView().hideLoading();
                        getView().setVisibility(R.id.general_nodata, View.VISIBLE);
                    }
                })
                .doOnNext(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean data) {
                        getView().hideLoading();
                        getView().notifyDataSetChanged(R.id.general_list);
                        getView().setVisibility(R.id.general_nodata, data ? View.GONE : View.VISIBLE);
                    }
                })
                .subscribe());
    }

    private final void addPresenter(@NonNull ArrayObjectAdapter arrayObjectAdapter,
                                    @NonNull int templateCode,
                                    @NonNull List<GetSubChannelsByChannelBean.ListBean.TemplateBean> datas) {
        Collection collection = null;
        Object object = null;
        // 返回顶部
        if (-2 == templateCode) {
            object = new GeneralTemplateBottom();
        }
        // 二级分类
        if (-1 == templateCode) {
            GeneralTemplateClass.GeneralTemplateClassList list = new GeneralTemplateClass.GeneralTemplateClassList();
            list.addAll(datas);
            object = list;
        }
        // 模板1
        else if (1 == templateCode) {
            GeneralTemplate1.GeneralTemplate1List list = new GeneralTemplate1.GeneralTemplate1List();
            list.addAll(datas);
            object = list;
        }
        // 模板2
        else if (2 == templateCode) {
            GeneralTemplate2.GeneralTemplate2List list = new GeneralTemplate2.GeneralTemplate2List();
            list.addAll(datas);
            object = list;
        }
        // 模板3
        else if (3 == templateCode) {
            GeneralTemplate3.GeneralTemplate3List list = new GeneralTemplate3.GeneralTemplate3List();
            list.addAll(datas);
            object = list;
        }
        // 模板4
        else if (4 == templateCode) {
            GeneralTemplate4.GeneralTemplate4List list = new GeneralTemplate4.GeneralTemplate4List();
            list.addAll(datas);
            object = list;
        }
        // 模板5
        else if (5 == templateCode) {
            GeneralTemplate5.GeneralTemplate5List list = new GeneralTemplate5.GeneralTemplate5List();
            list.addAll(datas);
            object = list;
        }
        // 模板6
        else if (6 == templateCode) {
            GeneralTemplate6.GeneralTemplate6List list = new GeneralTemplate6.GeneralTemplate6List();
            list.addAll(datas);
            object = list;
        }
        // 模板7
        else if (7 == templateCode) {
            GeneralTemplate7.GeneralTemplate7List list = new GeneralTemplate7.GeneralTemplate7List();
            list.addAll(datas);
            object = list;
        }
        // 模板8
        else if (8 == templateCode) {
            GeneralTemplate8.GeneralTemplate8List list = new GeneralTemplate8.GeneralTemplate8List();
            list.addAll(datas);
            object = list;
        }
        // 模板9
        else if (9 == templateCode) {
            GeneralTemplate9.GeneralTemplate9List list = new GeneralTemplate9.GeneralTemplate9List();
            list.addAll(datas);
            object = list;
        }
        // 模板10
        else if (10 == templateCode) {
            GeneralTemplate10.GeneralTemplate10List list = new GeneralTemplate10.GeneralTemplate10List();
            list.addAll(datas);
            object = list;
        }
        // 模板11
        else if (11 == templateCode) {
            GeneralTemplate11.GeneralTemplate11List list = new GeneralTemplate11.GeneralTemplate11List();
            list.addAll(datas);
            object = list;
        }
        // 模板12
        else if (12 == templateCode) {
            GeneralTemplate12.GeneralTemplate12List list = new GeneralTemplate12.GeneralTemplate12List();
            list.addAll(datas);
            object = list;
        }
        // 模板13
        else if (13 == templateCode) {
            GeneralTemplate13.GeneralTemplate13List list = new GeneralTemplate13.GeneralTemplate13List();
            list.addAll(datas);
            object = list;
        }
        // 模板14
        else if (14 == templateCode) {
            GeneralTemplate14.GeneralTemplate14List list = new GeneralTemplate14.GeneralTemplate14List();
            list.addAll(datas);
            object = list;
        }
        // 模板15
        else if (15 == templateCode) {
            GeneralTemplate15.GeneralTemplate15List list = new GeneralTemplate15.GeneralTemplate15List();
            list.addAll(datas);
            object = list;
        }
        // 模板16
        else if (16 == templateCode) {
            GeneralTemplate16.GeneralTemplate16List list = new GeneralTemplate16.GeneralTemplate16List();
            list.addAll(datas);
            object = list;
        }
        // 模板17
        else if (17 == templateCode) {
            GeneralTemplate17.GeneralTemplate17List list = new GeneralTemplate17.GeneralTemplate17List();
            list.addAll(datas);
            object = list;
        }
        // 模板18
        else if (18 == templateCode) {
            GeneralTemplate18.GeneralTemplate18List list = new GeneralTemplate18.GeneralTemplate18List();
            list.addAll(datas);
            object = list;
        }
        // 模板19
        else if (19 == templateCode) {
            GeneralTemplate19.GeneralTemplate19List list = new GeneralTemplate19.GeneralTemplate19List();
            list.addAll(datas);
            object = list;
        }
        // 模板20
        else if (20 == templateCode) {
            GeneralTemplate20.GeneralTemplate20List list = new GeneralTemplate20.GeneralTemplate20List();
            list.addAll(datas);
            object = list;
        }
        // 模板21
        else if (21 == templateCode) {
            GeneralTemplate21.GeneralTemplate21List list = new GeneralTemplate21.GeneralTemplate21List();
            list.addAll(datas);
            object = list;
        }
        // 模板22
        else if (22 == templateCode) {
            GeneralTemplate22.GeneralTemplate22List list = new GeneralTemplate22.GeneralTemplate22List();
            list.addAll(datas);
            object = list;
        }

        if (null != object) {
            arrayObjectAdapter.add(object);
        }
        if (null != collection) {
            int size = arrayObjectAdapter.size();
            arrayObjectAdapter.addAll(size, collection, false);
        }
    }

    protected void requestBookmark() {

        addDisposable(Observable.create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                        boolean extra = getView().getBooleanExtra(GeneralFragment.BUNDLE_RESEAT, false);
                        if (extra) {
                            boolean visible = getView().isFragmentVisible();
                            if (visible) {
                                GeneralGridView gridView = getView().findViewById(R.id.general_list);
                                boolean containsTemplateHistory = gridView.containsTemplateHistory();
                                if (containsTemplateHistory) {
                                    emitter.onNext(true);
                                } else {
                                    throw new Exception("not contains");
                                }
                            } else {
                                throw new Exception("not visable");
                            }
                        } else {
                            getView().putBooleanExtra(GeneralFragment.BUNDLE_RESEAT, true);
                            throw new Exception("not init");
                        }
                    }
                })
                // 观看历史
                .flatMap(new Function<Boolean, Observable<BaseResponsedBean<FavBean>>>() {
                    @Override
                    public Observable<BaseResponsedBean<FavBean>> apply(Boolean v) {
                        return HttpClient.getHttpClient().getHttpApi().getBookmark(0, 5, null);
                    }
                })
                // 数据处理
                .map(new Function<BaseResponsedBean<FavBean>, FavBean>() {
                    @Override
                    public FavBean apply(BaseResponsedBean<FavBean> resp) {
                        try {
                            return resp.getData();
                        } catch (Exception e) {
                            throw e;
                        }
                    }
                })
                .delay(40, TimeUnit.MILLISECONDS)
                .compose(ComposeSchedulers.io_main())
                .doOnNext(new Consumer<FavBean>() {
                    @Override
                    public void accept(FavBean data) {
                        getView().updateTemplateHistory(data);
                    }
                })
                .subscribe());
    }

    protected void showBackground() {
        addDisposable(Observable.create(new ObservableOnSubscribe<Drawable>() {
            @Override
            public void subscribe(ObservableEmitter<Drawable> emitter) throws Exception {
                try {
                    String poster = getView().getStringExtra(GeneralFragment.BUNDLE_POSTER);
                    if (null == poster || poster.length() <= 0)
                        throw new Exception("url is null");
                    RequestOptions options = new RequestOptions().dontAnimate().dontTransform().dontAnimate().dontTransform().encodeQuality(40).format(DecodeFormat.PREFER_RGB_565).priority(Priority.LOW).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false);
                    Drawable drawable = Glide.with(getView().getContext()).load(poster).apply(options).submit().get();
                    if (null == drawable)
                        throw new Exception("drawable is null");
                    emitter.onNext(drawable);
                } catch (Exception e) {
                    throw new Exception();
                }
            }
        }).compose(ComposeSchedulers.io_main()).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                getView().setWindowBackgroundColorRes(R.color.color_171819);
            }
        }).doOnNext(new Consumer<Drawable>() {
            @Override
            public void accept(Drawable drawable) {
                getView().setWindowBackgroundDrawable(drawable);
            }
        }).subscribe());
    }
}
