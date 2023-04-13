package tv.huan.bilibili.utils;

import android.content.Context;
import com.aspire.hdc.pay.sdk.PayApi;
import com.aspire.hdc.pay.sdk.PayListener;
import com.aspire.hdc.pay.sdk.PayRequest;
import org.apache.commons.codec.binary.Base16;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Random;
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


    /**
     * 跳转支付页面
     */
    public void doPay(Context context, String appSource, String productId) {
        try {
            PayRequest request = new PayRequest();
            String appId = "ye_service001"; //业务Id
            String appKey = "2499215r576ss8s95ii6s2ei7e3paa09"; //不重复随机数, 由增值业务平台在应用注册时生成, 业务应用调用增值业务平台接口或增值业务平台主动通知业务应用时鉴权

            request.addRequestHeader("Content-Type", "application/json; charset=UTF-8");

            JSONObject body = new JSONObject();
            String snCode = getSnCode("huanwang", 7);
            body.put("productCode", "ye_by_001"); //增值业务平台侧的商品标识，多产品时，以逗号分隔
            body.put("stbId", DevicesUtils.INSTANCE.getSn()); //机顶盒串号
            body.put("transId", "T".concat(snCode)); //交易流水, 字母T+厂商标识+YYYYMMDDhhmmss+序列  保证不重复 不超过32位，eg: Tyinhe202105261514260000001
            body.put("orderNo", "O".concat(snCode)); //订单号, 字母O+厂商标识+YYYYMMDDhhmmss+序列  保证不重复 不超过32位
            body.put("account", DevicesUtils.INSTANCE.getUserId()); //用户登录账号
            body.put("notifyUrl", "http://10.12.5.61:8090/notifyurl/notify.htm"); //支付结果后台服务器通知地址
            body.put("backUrl", "http://aspire.hdc.payback"); //支付完成页面回调地址。默认：http://aspire.hdc.payback
            body.put("payMode", "9"); //支付方式：1支付宝， 2微信， 9由产品所支持的支付方式决定。 默认填9
            request.setRequestBody(body.toString());
            request.addRequestHeader("Authorization", "HDCAUTH appid=\"" + appId + "\",token=\"" + makeToken(appKey, request.getRequestBody()) + "\"");

            LogUtil.log("===请求header==="+request.getRequestHeaders());
            LogUtil.log("===请求reqBody==="+request.getRequestBody());

            // 发起支付请求
            PayApi.toPay(context, request, new PayListener() {
                @Override
                public void onPaySuccess() {
                    // 支付成功响应（此方法是在backurl使用默认地址时，才会回调）
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getSnCode(String label, int length){
        String snCode = TimeUtils.getNowString(TimeUtils.getSafeDateFormat("yyyyMMddHHmmss"));
        StringBuilder sb = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(rand.nextInt(9));
        }
        return label + snCode + sb;
    }


    /**
     *  局方接口安全机制：跳转支付，发起支付申请时，
     *   请求Header中的Authorization的token字段
     *
     *   附：token的计算过程
     *     1、使用请求报文源文进行MD5的散列运算得到请求报文源文的散列值(byte[]数组，带不可见字符)；
     2、使用HMAC-SHA-256对第一步计算出来的散列值进行单项加密，得到加密的结果（byte[]数组，带不可见字符）；
     3、使用Base16对第二步的加密结果进行转换成可见字符。
     */
    private String makeToken(String appKey, String body) {
        byte[] md5 = DigestUtils.md5(body);
        HmacUtils hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, appKey.getBytes());
        byte[] hmaByte = hmacUtils.hmac(md5);
        Base16 base16 = new Base16();
        String token = base16.encodeAsString(hmaByte);
        LogUtil.log("===TOKEN==="+token);
        return token;
    }


}