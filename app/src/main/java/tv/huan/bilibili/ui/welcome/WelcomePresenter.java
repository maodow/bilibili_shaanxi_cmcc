package tv.huan.bilibili.ui.welcome;

import android.content.Context;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import lib.kalu.frame.mvp.transformer.ComposeSchedulers;
import tv.huan.bilibili.BuildConfig;
import tv.huan.bilibili.R;
import tv.huan.bilibili.base.BasePresenterImpl;
import tv.huan.bilibili.bean.GetChannelsBean;
import tv.huan.bilibili.bean.GetPopupInfoBeanBase;
import tv.huan.bilibili.bean.ServerSettingData;
import tv.huan.bilibili.bean.ServerSettingData.UpgradeBean;
import tv.huan.bilibili.bean.base.BaseResponsedBean;
import tv.huan.bilibili.bean.format.CallWelcomeBean;
import tv.huan.bilibili.help.ConfigHelp;
import tv.huan.bilibili.http.HttpClient;
import tv.huan.bilibili.ui.main.MainActivity;
import tv.huan.bilibili.utils.ADUtil;
import tv.huan.bilibili.utils.DevicesUtils;
import tv.huan.bilibili.utils.LogUtil;


public class WelcomePresenter extends BasePresenterImpl<WelcomeView> {

    public WelcomePresenter(@NonNull WelcomeView welcomeView) {
        super(welcomeView);
    }

    protected void request() {

        addDisposable(Observable.create(new ObservableOnSubscribe<CallWelcomeBean>() {
                    @Override
                    public void subscribe(ObservableEmitter<CallWelcomeBean> emitter) {

                        // ad
                        ADUtil.adSplash(getView().getContext());

                        CallWelcomeBean welcomeBean = new CallWelcomeBean();
                        try {
                            // 3
                            int select = getView().getIntExtra(WelcomeActivity.INTENT_SELECT, 1);
                            welcomeBean.setSelect(select);

                            int type;
                            try {
                                String extra = getView().getStringExtra(WelcomeActivity.INTENT_TYPE);
                                type = Integer.parseInt(extra);
                            } catch (Exception e) {
                                type = getView().getIntExtra(WelcomeActivity.INTENT_TYPE, MainActivity.INTENT_TYPE_DEFAULT);
                            }
                            welcomeBean.setType(type);

                            String cid = null;
                            try {
                                cid = getView().getStringExtra(WelcomeActivity.INTENT_CID);
                            } catch (Exception e) {
                            }
                            welcomeBean.setCid(cid);

                            int classId;
                            try {
                                String extra = getView().getStringExtra(WelcomeActivity.INTENT_CLASSID);
                                classId = Integer.parseInt(extra);
                            } catch (Exception e) {
                                classId = getView().getIntExtra(WelcomeActivity.INTENT_CLASSID, -1);
                            }
                            welcomeBean.setClassId(classId);

                            String secondTag = null;
                            try {
                                secondTag = getView().getStringExtra(WelcomeActivity.INTENT_SECOND_TAG);
                            } catch (Exception e) {
                            }
                            welcomeBean.setSecondTag(secondTag);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        emitter.onNext(welcomeBean);
                    }
                })

                // 获取userId
                .map(new Function<CallWelcomeBean, CallWelcomeBean>() {
                    @Override
                    public CallWelcomeBean apply(CallWelcomeBean data) throws Exception {
                        LogUtil.log("WelcomePresenter => request => 获取userId");
                        try {
                            if (BuildConfig.HUAN_CHECK_USERID) {
                                Context context = getView().getContext();
                                String userId = DevicesUtils.INSTANCE.getAccount();
                                LogUtil.log("WelcomePresenter => request => userId = " + userId);
                                if (userId.length() <= 0) {
                                    String s = getView().getString(R.string.welcome_warning);
                                    throw new Exception(s);
                                }
                            }
                            return data;
                        } catch (Exception e) {
                            throw e;
                        }
                    }
                })
                // 首次打开app上报
                .map(new Function<CallWelcomeBean, CallWelcomeBean>() {
                    @Override
                    public CallWelcomeBean apply(CallWelcomeBean data) {
                        // 上报
                        try {
                            LogUtil.log("WelcomePresenter => request => 首次打开app上报");
                            reportAppActivation();
                        } catch (Exception e) {
                        }

                        return data;
                    }
                })

                //获取服务器配置
                .flatMap(new Function<CallWelcomeBean, Observable<BaseResponsedBean<ServerSettingData>>>() {
                    @Override
                    public Observable<BaseResponsedBean<ServerSettingData>> apply(CallWelcomeBean data) {
                        LogUtil.log("WelcomePresenter => request => 获取服务器配置接口");
                        String s = new Gson().toJson(data);
                        return HttpClient.getHttpClient().getHttpApi().getSetting(DevicesUtils.INSTANCE.getUserId(), s);
                    }
                })

                //获取服务器配置-数据整理
                .map(new Function<BaseResponsedBean<ServerSettingData>, CallWelcomeBean>() {
                    @Override
                    public CallWelcomeBean apply(BaseResponsedBean<ServerSettingData> response) {
                        LogUtil.log("WelcomePresenter => 获取服务器配置-数据整理");

                        CallWelcomeBean welcomeBean;
                        try {
                            welcomeBean = new Gson().fromJson(response.getExtra(), CallWelcomeBean.class);
                        } catch (Exception e) {
                            welcomeBean = new CallWelcomeBean();
                        }
                        ServerSettingData data = response.getData();
                        welcomeBean.setSettingData(data);
                        ConfigHelp.Companion.getINSTANCE().init(data);
                        return welcomeBean;
                    }
                })

                // 广告接口
                .flatMap(new Function<CallWelcomeBean, Observable<GetPopupInfoBeanBase>>() {
                    @Override
                    public Observable<GetPopupInfoBeanBase> apply(CallWelcomeBean data) {
                        LogUtil.log("WelcomePresenter => request => 广告接口");
                        String s = new Gson().toJson(data);
                        return HttpClient.getHttpClient().getHttpApi().getPopupInfo(s);
                    }
                })
                // 下载广告
                .map(new Function<GetPopupInfoBeanBase, CallWelcomeBean>() {
                    @Override
                    public CallWelcomeBean apply(GetPopupInfoBeanBase loadPageIcon) {
                        LogUtil.log("WelcomePresenter => request => 下载广告");

                        CallWelcomeBean welcomeBean;
                        try {
                            welcomeBean = new Gson().fromJson(loadPageIcon.getExtra(), CallWelcomeBean.class);
                        } catch (Exception e) {
                            welcomeBean = new CallWelcomeBean();
                        }

                        try {
                            GetPopupInfoBeanBase.LoadingPicBean loading_pic = loadPageIcon.getLoading_pic();
                            String display_time = loading_pic.getDisplay_time();
                            if (null != display_time && display_time.length() > 0) {
                                int parseInt = Integer.parseInt(display_time);
                                welcomeBean.setAdTime(parseInt);
                            } else {
                                welcomeBean.setAdTime(0);
                            }
                        } catch (Exception e) {
                            welcomeBean.setAdTime(0);
                        }
                        try {
                            GetPopupInfoBeanBase.LoadingPicBean loading_pic = loadPageIcon.getLoading_pic();
                            String picture_hd = loading_pic.getPicture_HD();
                            welcomeBean.setAdUrl(picture_hd);
                        } catch (Exception e) {
                        }
                        return welcomeBean;
                    }
                })
                // 频道接口
                .flatMap(new Function<CallWelcomeBean, Observable<BaseResponsedBean<GetChannelsBean>>>() {
                    @Override
                    public Observable<BaseResponsedBean<GetChannelsBean>> apply(CallWelcomeBean data) {
                        LogUtil.log("WelcomePresenter => request => 频道接口");
                        String s = new Gson().toJson(data);
                        return HttpClient.getHttpClient().getHttpApi().getChannels(1, 100, s);
                    }
                })
                // 整理数据
                .map(new Function<BaseResponsedBean<GetChannelsBean>, CallWelcomeBean>() {
                    @Override
                    public CallWelcomeBean apply(BaseResponsedBean<GetChannelsBean> response) {
                        LogUtil.log("WelcomePresenter => request => 整理数据");

                        CallWelcomeBean welcomeBean;
                        try {
                            welcomeBean = new Gson().fromJson(response.getExtra(), CallWelcomeBean.class);
                        } catch (Exception e) {
                            welcomeBean = new CallWelcomeBean();
                        }

                        ArrayList<GetChannelsBean.ItemBean> list = new ArrayList<>();
                        GetChannelsBean.ItemBean bean = new GetChannelsBean.ItemBean();
                        bean.setName("我的");
                        list.add(bean);
                        try {
                            List<GetChannelsBean.ItemBean> datas = response.getData().getList();
                            list.addAll(datas);
                        } catch (Exception e) {
                        }
                        String s = new Gson().toJson(list);
                        welcomeBean.setChannel(s);

                        int size = list.size();
                        if (size == 1) {
                            welcomeBean.setSelect(0);
                        } else {
                            int select = welcomeBean.getSelect();
                            if (select + 1 >= size) {
                                welcomeBean.setSelect(1);
                            }
                        }

                        return welcomeBean;
                    }
                })
                .delay(40, TimeUnit.MILLISECONDS)
                .compose(ComposeSchedulers.io_main())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) {
                        //  getView().showLoading();
                        LogUtil.log("WelcomePresenter => request => doOnSubscribe");
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        LogUtil.log("WelcomePresenter => request => doOnError =>");
                        getView().next();
                    }
                })
                .doOnNext(new Consumer<CallWelcomeBean>() {
                    @Override
                    public void accept(CallWelcomeBean data) {
                        LogUtil.log("WelcomePresenter => doOnNext => " + new Gson().toJson(data));
                        if (data.containsAd()) {
                            getView().setVisibility(R.id.welcome_img, View.VISIBLE);
                            getView().updateBackground(data.getAdUrl());
                            intervalTime(data.getSettingData().getUpgrade(), data.getChannel(), data.getSelect(), data.getType(), data.getCid(), data.getClassId(), data.getSecondTag(), data.getAdTime());
                        } else {
                            getView().next(data.getSettingData().getUpgrade(), data.getChannel(), data.getSelect(), data.getType(), data.getCid(), data.getClassId(), data.getSecondTag());
                        }
                    }
                })
                .subscribe());
    }

    private void intervalTime(@NonNull ServerSettingData.UpgradeBean upgradeBean, @NonNull String data, @NonNull int select, @NonNull int type, @NonNull String cid, @NonNull int classId, @NonNull String secondTag, @NonNull int time) {

        // 延时1s ，每间隔1s，时间单位
        addDisposable(Observable.interval(1, 1, TimeUnit.SECONDS)
                .compose(ComposeSchedulers.io_main())
                .doOnNext(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) {
                        Log.e("WelcomePresenter", "intervalTime => " + aLong);
                        // 取消订阅
                        if (aLong >= time) {
                            dispose();
                            getView().next(upgradeBean, data, select, type, cid, classId, secondTag);
                        } else {
                            int num = (int) (time - aLong);
                            getView().setVisibility(R.id.welcome_time, View.VISIBLE);
                            getView().setText(R.id.welcome_time, num + "s");
                        }
                    }
                })
                .subscribe());
    }
}
