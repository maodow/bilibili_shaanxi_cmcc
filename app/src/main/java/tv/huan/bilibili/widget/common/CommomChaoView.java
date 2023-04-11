package tv.huan.bilibili.widget.common;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import lib.kalu.leanback.plus.TextViewPlus;
import tv.huan.bilibili.R;
import tv.huan.bilibili.utils.AuthUtils;
import tv.huan.heilongjiang.HeilongjiangApi;
import tv.huan.heilongjiang.OnStatusChangeListener;

public final class CommomChaoView extends TextViewPlus {
    public CommomChaoView(@NonNull Context context) {
        super(context);
        init();
    }

    public CommomChaoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CommomChaoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == View.VISIBLE) {
            checkVip();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        checkVip();
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        ViewCompat.animate(this).scaleX(focused ? 1.1f : 1.0f).scaleY(focused ? 1.1f : 1.0f).setDuration(200).start();
    }

    private void init() {
        String packageName = getContext().getPackageName();
        if (null != packageName && packageName.endsWith(".yanshi")) {
            updateText(false);
        }
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                HeilongjiangApi.jumpVip(getContext(), null);
            }
        });
    }

    private void checkVip() {
        AuthUtils.getInstance().doAuth(new OnStatusChangeListener() {
            @Override
            public void onPass() {
                updateText(true);
            }

            @Override
            public void onFail() {
                updateText(false);
            }
        });
    }

    private void updateText(boolean succ) {
        setText(getResources().getString(succ ? R.string.menu_chao_yes : R.string.menu_chao_no));
    }
}
