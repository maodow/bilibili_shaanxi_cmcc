package tv.huan.bilibili.widget.player;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.RequiresApi;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.component.ComponentLoading;
import lib.kalu.mediaplayer.core.component.ComponentSpeed;

public final class PlayerViewTemplate21 extends PlayerView {

    public PlayerViewTemplate21(Context context) {
        super(context);
        init();
    }

    public PlayerViewTemplate21(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlayerViewTemplate21(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PlayerViewTemplate21(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected void addListeren() {
    }

    @Override
    protected void init() {

        // 填充模式
//        if (getId() == R.id.qvoice_video || getId() == R.id.qsquare_video || getId() == R.id.qreport_video) {
        setScaleType(PlayerType.ScaleType.SCREEN_SCALE_16_9);

        // loading
        ComponentLoading loading = new ComponentLoading(getContext());
        addComponent(loading);

        // speed
        ComponentSpeed speed = new ComponentSpeed(getContext());
        addComponent(speed);

        // init
        PlayerComponentInitTemplate21 initTemplate21 = new PlayerComponentInitTemplate21(getContext());
        addComponent(initTemplate21);
    }
}