package tv.huan.bilibili.ui.detail;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;
import androidx.annotation.NonNull;
import lib.kalu.frame.mvp.BaseActivity;
import tv.huan.bilibili.R;
import tv.huan.bilibili.bean.MediaBean;
import tv.huan.bilibili.dialog.InfoDialog;
import tv.huan.bilibili.utils.AuthUtils;;
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
        AuthUtils.getInstance().doPay(DetailActivity.this, "","");
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

}
