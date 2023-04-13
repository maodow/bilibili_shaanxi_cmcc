package tv.huan.bilibili.utils;

import android.content.Context;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import lib.kalu.frame.mvp.context.FrameContext;
import tv.huan.bilibili.BuildConfig;

public final class BoxUtil {

    public static String getTestVideoUrl() {
//        return "http://39.134.19.248:6610/yinhe/2/ch00000090990000001335/index.m3u8?virtualDomain=yinhe.live_hls.zte.com";
//        return "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
        return "http://wxsnsdy.tc.qq.com/105/20210/snsdyvideodownload?filekey=30280201010421301f0201690402534804102ca905ce620b1241b726bc41dcff44e00204012882540400&amp;bizid=1023&amp;hy=SH&amp;fileparam=302c020101042530230204136ffd93020457e3c4ff02024ef202031e8d7f02030f42400204045a320a0201000400";
//        return "";
    }

    public static int getProdId() {
        try {
            return 1;
        } catch (Exception e) {
            return 1;
        }
    }

    public static String getUserId() {
        try {
            Context context = FrameContext.getApplicationContext();
            String userId = DevicesUtils.INSTANCE.getAccount();
            if (null == userId || userId.length() <= 0)
                throw new Exception("userId error: null");
            return userId;
        } catch (Exception e) {
            LogUtil.log("BoxUtil => getUserId => " + e.getMessage());
            return BuildConfig.HUAN_CHECK_USERID ? "" : "00380035890";
        }
    }

//    public static String getHuanId() {
//        Context context = FrameContext.getApplicationContext();
//        return CacheUtil.getCache(context, "huanId");
//    }

    public static String getEtherNetIP() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            LogUtil.log("IpAddress", ex.toString());
        }
        return "0.0.0.0";
    }
}
