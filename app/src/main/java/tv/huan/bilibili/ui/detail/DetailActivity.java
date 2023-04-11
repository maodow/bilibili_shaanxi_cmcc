package tv.huan.bilibili.ui.detail;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;
import androidx.annotation.NonNull;
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
import lib.kalu.frame.mvp.BaseActivity;
import tv.huan.bilibili.R;
import tv.huan.bilibili.bean.MediaBean;
import tv.huan.bilibili.dialog.InfoDialog;
import tv.huan.bilibili.utils.DevicesUtils;
import tv.huan.bilibili.utils.LogUtil;
import tv.huan.bilibili.utils.TimeUtils;
import tv.huan.bilibili.widget.DetailGridView;

public class DetailActivity extends BaseActivity<DetailView, DetailPresenter> implements DetailView {

    public static final String INTENT_SEEK = "intent_seek";
    public static final String INTENT_POSITION = "intent_position";
    public static final String INTENT_CID = "intent_cid";
    public static final String INTENT_FROM_SEARCH = "intent_from_search";
    public static final String INTENT_FROM_SEARCH_KEY = "intent_from_search_key";
    public static final String INTENT_FROM_WANLIU = "intent_from_wanliu";
    public static final String INTENT_FROM_WANLIU_KEY = "intent_from_wanliu_key";
    public static final String INTENT_FROM_SPECIAL = "intent_from_special";
    public static final String INTENT_FROM_SPECIAL_SCENEID = "intent_from_special_sceneid";
    public static final String INTENT_FROM_SPECIAL_TOPID = "intent_from_special_topid";
    public static final String INTENT_FROM_SPECIAL_TOPNAME = "intent_from_special_topname";
    //    private static final String INTENT_UPDATE = "intent_update";
    protected static final String INTENT_VID = "intent_vid";
    protected static final String INTENT_INDEX = "intent_index";
    protected static final String INTENT_REC_CLASSID = "intent_rec_classid";
    protected static final String INTENT_START_TIME = "intent_start_time";
    protected static final String INTENT_CUR_POSITION = "intent_cur_position";
    protected static final String INTENT_CUR_DURATION = "intent_cur_duration";

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return getPresenter().dispatchEvent(event) || super.dispatchKeyEvent(event);
    }

    @Override
    public void onBackPressed() {
        getPresenter().uploadBackupPress();
    }

    @Override
    public void finish() {
        stopPlayer();
        super.finish();
    }

    @Override
    public int initLayout() {
        putLongExtra(INTENT_START_TIME, System.currentTimeMillis());
        return R.layout.activity_detail;
    }

    @Override
    public void initData() {
        getPresenter().setAdapter();
        getPresenter().request();
    }

    @Override
    public void showDialog(@NonNull String title, @NonNull String data1, @NonNull String data2) {
        Bundle bundle = new Bundle();
        bundle.putString(InfoDialog.BUNDLE_NAME, title);
        bundle.putString(InfoDialog.BUNDLE_DATA1, data1);
        bundle.putString(InfoDialog.BUNDLE_DATA2, data2);
        InfoDialog dialog = new InfoDialog();
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void cancleFavor(@NonNull String cid, @NonNull String recClassId) {
        getPresenter().cancleFavor(cid);
    }

    @Override
    public void addFavor(@NonNull String cid, @NonNull String recClassId) {
        getPresenter().addFavor(cid, recClassId);
    }

    @Override
    public void updateFavor(boolean status) {
        DetailGridView gridView = findViewById(R.id.detail_list);
        gridView.updateFavor(status);
    }

    @Override
    public void updateVidAndClassId(@NonNull MediaBean data) {
        putStringExtra(INTENT_VID, data.getVid());
        putStringExtra(INTENT_REC_CLASSID, data.getTempRecClassId());
        putIntExtra(INTENT_INDEX, data.getEpisodeIndex() + 1);
    }

    @Override
    public void updatePlayerInfo(@NonNull MediaBean data, boolean isFromUser) {
        DetailGridView gridView = findViewById(R.id.detail_list);
        gridView.showData(data);
    }

    @Override
    public void delayStartPlayer(@NonNull MediaBean data, boolean isFromUser) {
        getPresenter().delayStartPlayer(data, isFromUser);
    }

    @Override
    public void checkPlayer(@NonNull MediaBean data) {
        DetailGridView gridView = findViewById(R.id.detail_list);
        gridView.checkPlayer(data);
    }

    @Override
    public void checkedPlayerPosition(@NonNull MediaBean data) {
        DetailGridView gridView = findViewById(R.id.detail_list);
        gridView.checkedPlayerPosition(data);
    }

    @Override
    public void checkedPlayerPositionNext() {
        DetailGridView gridView = findViewById(R.id.detail_list);
        gridView.checkedPlayerPositionNext();
    }

    @Override
    public void stopPlayer() {
        DetailGridView gridView = findViewById(R.id.detail_list);
        gridView.stopPlayer();
    }

    @Override
    public void completePlayer() {
        getPresenter().checkPlayerNext();
    }

    @Override
    public void jumpVip() {
        Toast.makeText(getApplicationContext(), "需要开通会员", Toast.LENGTH_SHORT).show();
        //跳转局方支付页面
        doPay("", "");
    }

    @Override
    public void stopFull() {
        DetailGridView gridView = findViewById(R.id.detail_list);
        gridView.stopFull();
    }

    @Override
    public void getMediaUrl(MediaBean media, long seek) {
//        getPresenter().getMediaUrl(media, seek);


        //test
        String plaUrl1 = "http://dbiptv.sn.chinamobile.com/21/16/20230206/278978609/278978609.ts/index.m3u8?fmt=ts2hls&zoneoffset=0&servicetype=0&icpid=&limitflux=-1&limitdur=-1&tenantId=8601&accountinfo=%2C3266052%2C111.20.40.65%2C20230215150806%2Cprogram27c3202302061533170007%2C3266052%2C-1%2C1%2C0%2C-1%2C1%2C1%2C100000204%2C%2C416973955%2C1%2C%2C416973882%2CEND&GuardEncType=2&it=H4sIAAAAAAAAAE2OQQqDMBREb5NliDYQs8jKUigUW9B2W37ibypGYxMVevuquOhyhveGGQMYPB-VMFpyxo3IzIFLLaQ0CRNaZppLxrkmET-FVykx4FzT28LXq_Yo82fCaMoEZVSSal07ObCKbWAxdRrDHharxDA3BlUdX3SGSMHagBbGxvf05uB7D25HCFb7s35yjoxrqCC2S0HeEHPfDRCwvni7ceoFLiIZwLRgsYAO_7xrqJcTP9pLmAjqAAAA";

        String plaUrl2 = "http://zteres.sn.chinamobile.com:6060/huanshishaoer/32/movie24bb202302061533170010?AuthInfo=5MfDFjp9D9h%2F0lrqkysKiHPx5ZEnxLMjUQtr6vabE6cXkunNwmblyHwEuDZfcQQFlkNHQLmBSl9COP2D3sYpAg%3D%3D&version=v1.0&BreakPoint=0&virtualDomain=huanshishaoer.vod_hpd.zte.com&mescid=00000050280009372185&programid=&contentid=movie24bb202302061533170010&videoid=00000050280009372185&recommendtype=0&userid=A089E4CA0921&boid=&stbid=&terminalflag=1&profilecode=&usersessionid=529753020";

        onGetPlayUrlSuccess(plaUrl1, 0L);

    }

    @Override
    public void onGetPlayUrlSuccess(String s, long seek) {
        DetailGridView gridView = findViewById(R.id.detail_list);
        gridView.startPlayer(s, seek);
    }


    /**
     * 跳转支付页面
     */
    private void doPay(String appSource, String productId) {
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
            PayApi.toPay(this, request, new PayListener() {
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
