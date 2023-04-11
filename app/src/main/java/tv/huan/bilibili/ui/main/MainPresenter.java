package tv.huan.bilibili.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.leanback.widget.Presenter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;
import java.lang.reflect.Type;
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
import lib.kalu.leanback.tab.TabLayout;
import lib.kalu.leanback.tab.model.TabModel;
import lib.kalu.leanback.tab.model.TabModelImage;
import lib.kalu.leanback.tab.model.TabModelText;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import tv.huan.bilibili.BuildConfig;
import tv.huan.bilibili.R;
import tv.huan.bilibili.base.BasePresenterImpl;
import tv.huan.bilibili.bean.ExitBean;
import tv.huan.bilibili.bean.GetChannelsBean;
import tv.huan.bilibili.bean.base.BaseDataBean;
import tv.huan.bilibili.bean.base.BaseResponsedBean;
import tv.huan.bilibili.bean.base.BaseThirdResponse;
import tv.huan.bilibili.bean.format.CallMainBean;
import tv.huan.bilibili.dialog.WarningDialog;
import tv.huan.bilibili.http.HttpClient;
import tv.huan.bilibili.ui.main.general.GeneralFragment;
import tv.huan.bilibili.ui.main.mine.MineFragment;
import tv.huan.bilibili.utils.ADUtil;
import tv.huan.bilibili.utils.DevicesUtils;
import tv.huan.bilibili.utils.JumpUtil;
import tv.huan.bilibili.utils.LogUtil;
import tv.huan.bilibili.utils.StringUtils;
import tv.huan.bilibili.bean.ServerSettingData.UpgradeBean;

public class MainPresenter extends BasePresenterImpl<MainView> {

    public MainPresenter(@NonNull MainView mainView) {
        super(mainView);
    }

    protected void checkIntent() {
        // userId
        boolean enable = getView().getBooleanExtra(MainActivity.INTENT_ENABLE, true);
        if (enable) {
            int type = getView().getIntExtra(MainActivity.INTENT_TYPE, -1);
            // 专辑
            if (type == MainActivity.INTENT_TYPE_DETAIL) {
                String cid = getView().getStringExtra(MainActivity.INTENT_CID);
                BaseDataBean bean = new BaseDataBean();
                bean.setToType(1);
                bean.setCid(cid);
                JumpUtil.next((Context) getView(), bean);
            }
            // 筛选
            else if (type == MainActivity.INTENT_TYPE_FILTER) {
                String secondTag = getView().getStringExtra(MainActivity.INTENT_SECOND_TAG);
                int classId = getView().getIntExtra(MainActivity.INTENT_CLASSID, -1);
                BaseDataBean bean = new BaseDataBean();
                bean.setToType(2);
                bean.setClassId(classId);
                bean.setName(secondTag);
                JumpUtil.next(getView().getContext(), bean);
            }
        } else {
            FragmentManager fragmentManager = getView().getSupportFragmentManager();
            WarningDialog dialog = new WarningDialog();
            dialog.show(fragmentManager, null);
        }
    }

    protected void showFragment(int position) {

        addDisposable(Observable.create(new ObservableOnSubscribe<List<GetChannelsBean.ItemBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<GetChannelsBean.ItemBean>> emitter) throws Exception {
                try {
                    String extra = getView().getStringExtra(MainActivity.INTENT_TABS);
                    Type type = new TypeToken<List<GetChannelsBean.ItemBean>>() {
                    }.getType();
                    List<GetChannelsBean.ItemBean> datas = new Gson().fromJson(extra, type);
                    if (null == datas || datas.size() <= 0) throw new Exception("datas is null");
                    emitter.onNext(datas);
                } catch (Exception e) {
                    throw e;
                }
            }
        }).map(new Function<List<GetChannelsBean.ItemBean>, Fragment>() {
            @Override
            public Fragment apply(List<GetChannelsBean.ItemBean> data) {
                try {
                    String tag = "fragment" + position;
                    Fragment fragment = getView().findFragmentByTag(tag);
                    if (null == fragment) {
                        GetChannelsBean.ItemBean bean = data.get(position);
                        Bundle bundle = new Bundle();
                        bundle.putInt(GeneralFragment.BUNDLE_INDEX, position);
                        bundle.putString(GeneralFragment.BUNDLE_NAME, bean.getName());
                        bundle.putInt(GeneralFragment.BUNDLE_CHANNELID, bean.getId());
                        bundle.putInt(GeneralFragment.BUNDLE_CLASSID, bean.getClassId());
                        if (null != bean.getImgs() && null != bean.getImgs().getPoster() && bean.getImgs().getPoster().length() > 0) {
                            bundle.putString(GeneralFragment.BUNDLE_POSTER, bean.getImgs().getPoster());
                        } else {
                            bundle.putString(GeneralFragment.BUNDLE_POSTER, "");
                        }
                        if (position == 0) {
                            fragment = new MineFragment();
                        } else {
                            fragment = new GeneralFragment();
                        }
                        fragment.setArguments(bundle);
                        getView().addFragment(fragment, R.id.main_content, tag);
                    }
                    return fragment;
                } catch (Exception e) {
                    throw e;
                }
            }
        }).compose(ComposeSchedulers.io_main()).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) {
                try {
                    TabLayout tabLayout = getView().findViewById(R.id.main_tabs);
                    int count = tabLayout.getItemCount();
                    Fragment[] fragments = new Fragment[count];
                    if (count > 0) {
                        for (int i = 0; i < count; i++) {
                            String tag = "fragment" + i;
                            Fragment fragment = getView().findFragmentByTag(tag);
                            if (null == fragment || fragment.isHidden()) continue;
                            if (fragment instanceof GeneralFragment) {
                                ((GeneralFragment) fragment).onHide();
                            } else if (fragment instanceof MineFragment) {
                                ((MineFragment) fragment).onHide();
                            }
                            fragments[i] = fragment;
                        }
                    }
                    getView().hideFragment(fragments);
                } catch (Exception e) {
                    LogUtil.log("MainPresenter => showFragment => " + e.getMessage());
                }
            }
        }).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                LogUtil.log("MainPresenter => showFragment => fail");
                LogUtil.log("MainPresenter => showFragment => " + throwable.getMessage());
            }
        }).doOnNext(new Consumer<Fragment>() {
            @Override
            public void accept(Fragment fragment) {
                LogUtil.log("MainPresenter => showFragment => succ");
                LogUtil.log("MainPresenter => showFragment => fragment = " + fragment);
                getView().showFragment(fragment);
                if (fragment instanceof GeneralFragment) {
                    ((GeneralFragment) fragment).onShow();
                } else if (fragment instanceof MineFragment) {
                    ((MineFragment) fragment).onShow();
                }
            }
        }).subscribe());
    }

    protected void showTabs() {

        addDisposable(Observable.create(new ObservableOnSubscribe<CallMainBean>() {
                    @Override
                    public void subscribe(ObservableEmitter<CallMainBean> observableEmitter) {

                        // 0
                        int select = getView().getIntExtra(MainActivity.INTENT_SELECT, 0);
                        String extra = getView().getStringExtra(MainActivity.INTENT_TABS);
                        Type type = new TypeToken<List<GetChannelsBean.ItemBean>>() {
                        }.getType();
                        List<GetChannelsBean.ItemBean> datas = new Gson().fromJson(extra, type);

                        ArrayList<TabModel> list = new ArrayList<>();
                        int size = datas.size();
                        for (int i = 0; i < size; i++) {

                            GetChannelsBean.ItemBean bean = datas.get(i);
                            if (null == bean) continue;

                            TabModel tabModel;

                            // 图片
                            if (null != bean.getImgs() && null != bean.getImgs().getIcon() && null != bean.getImgs().getFocus() && null != bean.getImgs().getStay()) {
                                tabModel = new TabModelImage();
                                tabModel.setImagePlaceholderResource(R.mipmap.ic_launcher);
                                tabModel.setImageUrlFocus(bean.getImgs().getFocus());
                                tabModel.setImageUrlChecked(bean.getImgs().getStay());
                                tabModel.setImageUrlNormal(bean.getImgs().getIcon());
                            }
                            // 文字
                            else {
                                tabModel = new TabModelText();
                                tabModel.setText(bean.getName());
                                tabModel.setTextColorResourceNormal(R.color.color_bfbfbf);
                                tabModel.setTextColorResourceChecked(R.color.color_bfbfbf);
                                tabModel.setTextColorResourceFocus(R.color.color_black);
                                tabModel.setBackgroundResourceFocus(R.drawable.bg_shape_tab_focus);
                                tabModel.setBackgroundResourceChecked(R.drawable.bg_shape_tab_selected);
                                tabModel.setBackgroundResourceNormal(R.drawable.bg_shape_tab);
                            }
                            list.add(tabModel);
                        }

                        CallMainBean mainBean = new CallMainBean();
                        mainBean.setPosition(select);
                        mainBean.setTabModels(list);
                        observableEmitter.onNext(mainBean);
                    }
                })
                // 上报-进入APP首页
                .map(new Function<CallMainBean, CallMainBean>() {
                    @Override
                    public CallMainBean apply(CallMainBean mainBean) {
                        try {
                            int select = getView().getIntExtra(MainActivity.INTENT_SELECT, 0);
                            String extra = getView().getStringExtra(MainActivity.INTENT_TABS);
                            Type type = new TypeToken<List<GetChannelsBean.ItemBean>>() {
                            }.getType();
                            List<GetChannelsBean.ItemBean> datas = new Gson().fromJson(extra, type);
                            GetChannelsBean.ItemBean itemBean = datas.get(select);
                            int classId = itemBean.getClassId();
                            reportHomePageEnter(classId);
                        } catch (Exception e) {
                        }
                        return mainBean;
                    }
                }).delay(40, TimeUnit.MILLISECONDS).compose(ComposeSchedulers.io_main()).doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) {
                        getView().showLoading();
                    }
                }).doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        getView().hideLoading();
                    }
                }).doOnNext(new Consumer<CallMainBean>() {
                    @Override
                    public void accept(CallMainBean data) {
                        getView().hideLoading();
                        getView().refreshTabs(data.getTabModels(), data.getPosition());
                    }
                }).subscribe());
    }

    protected boolean dispatchKey(KeyEvent event) {
        // menu
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
//            TabLayout tabLayout = getView().findViewById(R.id.main_tabs);
//            int checkedIndex = tabLayout.getCheckedIndex();
//            int itemCount = tabLayout.getItemCount();
//            if (itemCount > 0 && checkedIndex != 1) {
//                // 1
//                getView().showTitle();
//                // 2
//                getView().tabScroll(1);
//                // 3
//                getView().contentScrollTop();
//                return true;
//            }
        }
        // back
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            boolean visibility = getView().isVisibility(R.id.main_search);
            LogUtil.log("MainPresenter", "back => visibility = " + visibility);
            if (visibility) {
                TabLayout tabLayout = getView().findViewById(R.id.main_tabs);
                int checkedIndex = tabLayout.getCheckedIndex();
                int itemCount = tabLayout.getItemCount();
                if (itemCount > 1 && checkedIndex != 1) {
                    tabLayout.scrollToPosition(1);
                    getView().contentScrollTop();
                } else {
                    requestExit();
                }
            } else {
                getView().contentScrollTop();
            }
            return true;
        }
        // up
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
            int focusId = getView().getCurrentFocusId();
            LogUtil.log("MainPresenter => dispatchKeyEvent => up_action_down => ");
            if (focusId == R.id.main_tabs) {
                TabLayout tabLayout = (TabLayout) getView().getCurrentFocus();
                LogUtil.log("MainPresenter => dispatchKeyEvent => up_action_down => tabLayout = " + tabLayout);
                tabLayout.checkedCurrentItem();
                int index = tabLayout.getCheckedIndex();
                getView().setFocusable(R.id.main_vip, index > 0);
                getView().setFocusable(R.id.main_search, index <= 0);
            } else if (focusId == R.id.main_search || focusId == R.id.main_vip) {
                return true;
            }
        }
        // down
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
            int focusId = getView().getCurrentFocusId();
            if (focusId == R.id.main_search || focusId == R.id.main_vip) {
                LogUtil.log("MainPresenter => dispatchKeyEvent => down_action_down => ");
                getView().setFocusable(R.id.main_vip, false);
                getView().setFocusable(R.id.main_search, false);
                TabLayout tabLayout = getView().findViewById(R.id.main_tabs);
                tabLayout.focusCurrentItem();
                return true;
            }
        }
        // right
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            int focusId = getView().getCurrentFocusId();
            LogUtil.log("MainPresenter => dispatchKeyEvent => right_action_down => " + getView().getCurrentFocus());
            if (focusId == R.id.main_search) {
                getView().setFocusable(R.id.main_vip, true);
                getView().setFocusable(R.id.main_search, false);
                return true;
            } else if (focusId == R.id.main_vip) {
                return true;
            }
        }
        // left
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
            int focusId = getView().getCurrentFocusId();
            LogUtil.log("MainPresenter => dispatchKeyEvent => left_action_down => " + getView().getCurrentFocus());
            if (focusId == R.id.main_vip) {
                getView().setFocusable(R.id.main_search, true);
                getView().setFocusable(R.id.main_vip, false);
                return true;
            } else if (focusId == R.id.main_search) {
                return true;
            }
        }
        return false;
    }

    private void requestExit() {
        addDisposable(Observable.create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(ObservableEmitter<Boolean> observableEmitter) {
                        ADUtil.adExit(getView().getContext().getApplicationContext());
                        observableEmitter.onNext(true);
                    }
                }).flatMap(new Function<Boolean, Observable<BaseResponsedBean<List<ExitBean>>>>() {
                    @Override
                    public Observable<BaseResponsedBean<List<ExitBean>>> apply(Boolean aBoolean) {
                        return HttpClient.getHttpClient().getHttpApi().getExit(2);
                    }
                })
                // 上报
                .map(new Function<BaseResponsedBean<List<ExitBean>>, String>() {
                    @Override
                    public String apply(BaseResponsedBean<List<ExitBean>> listBaseResponsedBean) {
                        ArrayList<ExitBean> list = new ArrayList<>();
                        try {
                            List<ExitBean> data = listBaseResponsedBean.getData();
                            for (int i = 0; i < 2; i++) {
                                ExitBean bean = data.get(i);
                                if (null == bean) continue;
                                reportExitRetentionExposure(bean.getCid());
                                list.add(bean);
                            }
                        } catch (Exception e) {
                        }
                        String s = new Gson().toJson(list);
                        return s;
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
                        getView().showDialog("[]");
                    }
                }).doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        getView().hideLoading();
                        getView().showDialog(s);
                    }
                }).subscribe());
    }

    protected <T extends Presenter> void getMediaUrl(Class<T> cls, Class<?> obj, String cid) {
        addDisposable(Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                        emitter.onNext(cid);
                    }
                })
                .flatMap(new Function<String, Observable<BaseThirdResponse>>() {
                    @Override
                    public Observable<BaseThirdResponse> apply(String s) throws Exception {
                        JSONObject object = new JSONObject();
                        object.put("cid", s);
                        object.put("tid", "-1");
                        object.put("supercid", "-1");
                        object.put("playType", "1");
                        object.put("contentType", "0");
                        object.put("businessType", "1");
                        object.put("idflag", "1");
                        String json = String.valueOf(object);
                        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), json);
//                        String url = HeilongjiangApi.getEpgServer(getView().getContext());
                        String epgServer = DevicesUtils.INSTANCE.getEpgServer();
                        String reqUrl = "";
                        if(!TextUtils.isEmpty(epgServer)){
                            reqUrl = epgServer.concat(BuildConfig.PLAY_API);
                        }
                        return HttpClient.getHttpClient().getHttpApi().getPlayUrl(reqUrl, requestBody);
                    }
                })
                // 获取播放地址 => 数据处理
                .map(new Function<BaseThirdResponse, String>() {
                    @Override
                    public String apply(BaseThirdResponse resp) throws Exception {
                        try {
                            String url = resp.getUrls().get(0).getPlayurl();
                            if (null == url || url.length() <= 0)
                                throw new Exception();
                            return url;
                        } catch (Exception e) {
                            throw new Exception("获取播放链接失败！: " + resp.getReturncode());
                        }
                    }
                })
                .delay(40, TimeUnit.MILLISECONDS)
                .compose(ComposeSchedulers.io_main())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        getView().showToast(throwable);

                    }
                })
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        getView().onGetPlayUrlSuccess(cls, obj, s);
                    }
                })
                .subscribe());
    }


    protected <T extends Presenter> void startPlayerFromFragment(Class<T> cls, Class<?> obj, String s) {
        addDisposable(Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> emitter) {
                        TabLayout tabLayout = getView().findViewById(R.id.main_tabs);
                        int checkedIndex = tabLayout.getCheckedIndex();
                        emitter.onNext(checkedIndex);
                    }
                }).map(new Function<Integer, Fragment>() {
                    @Override
                    public Fragment apply(Integer i) throws Exception {
                        try {
                            String tag = "fragment" + i;
                            Fragment fragment = getView().findFragmentByTag(tag);
                            if (null == fragment)
                                throw new Exception("not find");
                            return fragment;
                        } catch (Exception e) {
                            throw e;
                        }
                    }
                }).compose(ComposeSchedulers.io_main())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        LogUtil.log("MainPresenter => startPlayerFromFragment => fail");
                    }
                })
                .doOnNext(new Consumer<Fragment>() {
                    @Override
                    public void accept(Fragment fragment) {
                        LogUtil.log("MainPresenter => startPlayerFromFragment => succ");
                        if (fragment instanceof GeneralFragment) {
                            ((GeneralFragment) fragment).startPlayerFromHuawei(cls, obj, s);
                        }
                    }
                }).subscribe());
    }

}
