package tv.huan.bilibili.utils;

import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import lib.kalu.frame.mvp.transformer.ComposeSchedulers;
import tv.huan.bilibili.bean.AuthBean;
import tv.huan.bilibili.bean.base.BaseResponsedBean;
import tv.huan.bilibili.http.HttpClient;
import tv.huan.heilongjiang.OnStatusChangeListener;

/**
 * 鉴权工具类
 */
public class AuthUtils {

    private static AuthUtils mInstance;

    private AuthUtils() {
    }

    public static AuthUtils getInstance() {
        if (null == mInstance) {
            synchronized (AuthUtils.class) {
                if (null == mInstance) {
                    mInstance = new AuthUtils();
                }
            }
        }
        return mInstance;
    }


    /**
     * 用户鉴权
     */
    public void doAuth(final OnStatusChangeListener listener) {
        String userId = DevicesUtils.INSTANCE.getAccount();
        String deviceSN = DevicesUtils.INSTANCE.getSn();
        Observable.create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(ObservableEmitter<Boolean> observableEmitter) {
                        observableEmitter.onNext(true);
                    }
                })
                .flatMap(new Function<Boolean, Observable<BaseResponsedBean<AuthBean>>>() {
                    @Override
                    public Observable<BaseResponsedBean<AuthBean>> apply(Boolean aBoolean) {
                        return HttpClient.getHttpClient().getHttpApi().getAuth(userId, deviceSN, "");
                    }
                })
                .map(new Function<BaseResponsedBean<AuthBean>, Boolean>() {
                    @Override
                    public Boolean apply(BaseResponsedBean<AuthBean> authResponse) {
                        return authResponse.getData().getAuth().equals("1");
                    }
                })
                .delay(40, TimeUnit.MILLISECONDS)
                .compose(ComposeSchedulers.io_main())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) {
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                    }
                })
                .doOnNext(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isVip) {
                        if (isVip) {
                            listener.onPass();
                        } else {
                            listener.onFail();
                        }
                    }
                }).subscribe();
    }

}