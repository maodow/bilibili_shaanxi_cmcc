package tv.huan.bilibili.utils;

import android.app.Application;
import android.content.Context;
import java.util.LinkedList;
import tv.huan.bilibili.R;
import tv.scene.ad.opensdk.AdSlot;
import tv.scene.ad.opensdk.INormAd;
import tv.scene.ad.opensdk.SceneAdConfig;
import tv.scene.ad.opensdk.SceneAdSDK;
import tv.scene.ad.opensdk.basecallback.AbsAdListener;

public final class ADUtil {

    private static final LinkedList<INormAd> mINormAd = new LinkedList<>();

    public static void adRelease() {
        try {
            while (mINormAd.size() > 0) {
                INormAd iNormAd = mINormAd.removeFirst();
                if (null != iNormAd) {
                    iNormAd.release();
                }
            }
            LogUtil.log("ADUtil => adRelease => succ");
        } catch (Exception e) {
            LogUtil.log("ADUtil => adRelease => " + e.getMessage());
        }
    }

    public static void adInit(Application application) {
        try {
            SceneAdConfig config = new SceneAdConfig.Builder ()
                    //appkey到后台申请，每个应用保证唯一性
                    .setAppKey(Constants.APP_KEY)
                    //应用名称，对接应用添加
                    .setAppName("云视听小电视")
                    //测试阶段打开，可以通过日志排查问题，上线时去除该调用
                    .setOpenLog(true)
                    .setDebugUrl(false) //广告位是正式环境,使用正式环境需要把初始化时的setDebugUrl去掉或者设为false , 然后清一下APP缓存
                    //厂商设置
                    .setManufacture("shaanxi-yd-bilibili")
                    //机型设置
                    .setDeviceModel(DevicesUtils.INSTANCE.getModel())
                    .setRequestTimeOutSeconds(10)
                    .builder();
            SceneAdSDK.init(application, config);
            LogUtil.log("ADUtil => adInit => succ");
        } catch (Exception e) {
            LogUtil.log("ADUtil => adInit => " + e.getMessage());
        }
    }

    public static void adSplash(Context context) {

        try {
            SceneAdSDK.getAdManager().createAdFactory(context).loadAd(new AdSlot.Builder()
                    .setCodeId(Constants.OPEN_SCREEN_AD)
                    .setDisplayCountDown(false)
                    .setMediaView(null)
                    .build(), new AbsAdListener() {
                @Override
                public void onError(int code, String message) {
                    LogUtil.log("ADUtil => adSplash => onError => code = " + code + ", message = " + message);
                }

                @Override
                public void onLoadAd(INormAd normAd) {
                    mINormAd.add(normAd);
                    LogUtil.log("ADUtil => adSplash => onLoadAd");
                }
            });
        } catch (Exception e) {
            LogUtil.log("ADUtil => adSplash => " + e.getMessage());
        }
    }

    public static void adPlaying(Context context) {

        try {
            SceneAdSDK.getAdManager().createAdFactory(context).loadAd(new AdSlot.Builder()
                    .setCodeId(Constants.PRE_ROLL_AD)
                    .setDisplayCountDown(false)
                    .setMediaView(null)
                    .build(), new AbsAdListener() {
                @Override
                public void onError(int code, String message) {
                    LogUtil.log("ADUtil => adPlaying => onError => code = " + code + ", message = " + message);
                }

                @Override
                public void onLoadAd(INormAd normAd) {
                    mINormAd.add(normAd);
                    LogUtil.log("ADUtil => adPlaying => onLoadAd");
                }
            });
        } catch (Exception e) {
            LogUtil.log("ADUtil => adPlaying => " + e.getMessage());
        }
    }

    public static void adPause(Context context) {

        try {
            SceneAdSDK.getAdManager().createAdFactory(context).loadAd(new AdSlot.Builder()
                    .setCodeId(Constants.PAUSE_AD)
                    .setDisplayCountDown(false)
                    .setMediaView(null)
                    .build(), new AbsAdListener() {
                @Override
                public void onError(int code, String message) {
                    LogUtil.log("ADUtil => adPause => onError => code = " + code + ", message = " + message);
                }

                @Override
                public void onLoadAd(INormAd normAd) {
                    mINormAd.add(normAd);
                    LogUtil.log("ADUtil => adPause => onLoadAd");
                }
            });
        } catch (Exception e) {
            LogUtil.log("ADUtil => adPause => " + e.getMessage());
        }
    }

    public static void adExit(Context context) {

        try {
            SceneAdSDK.getAdManager().createAdFactory(context).loadAd(new AdSlot.Builder()
                    .setCodeId(Constants.EXIT_AD)
                    .setDisplayCountDown(false)
                    .setMediaView(null)
                    .build(), new AbsAdListener() {
                @Override
                public void onError(int code, String message) {
                    LogUtil.log("ADUtil => adExit => onError => code = " + code + ", message = " + message);
                }

                @Override
                public void onLoadAd(INormAd normAd) {
                    mINormAd.add(normAd);
                    LogUtil.log("ADUtil => adExit => onLoadAd");
                }
            });
        } catch (Exception e) {
            LogUtil.log("ADUtil => adExit => " + e.getMessage());
        }
    }
}
