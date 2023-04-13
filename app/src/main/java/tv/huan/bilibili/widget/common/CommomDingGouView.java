package tv.huan.bilibili.widget.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.leanback.plus.TextViewPlus;
import tv.huan.bilibili.utils.AuthUtils;
import tv.huan.heilongjiang.OnStatusChangeListener;

public final class CommomDingGouView extends TextViewPlus {
    public CommomDingGouView(@NonNull Context context) {
        super(context);
        init();
    }

    public CommomDingGouView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CommomDingGouView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

    private void init() {
        setVisibility(View.GONE);
    }

    private void checkVip() {
        AuthUtils.getInstance().doAuth(new OnStatusChangeListener() {
            @Override
            public void onPass() {
                updateVisibility(false);
            }

            @Override
            public void onFail() {
                updateVisibility(true);
            }
        });
    }

    private void updateVisibility(boolean succ) {
        setVisibility(succ ? View.VISIBLE : View.GONE);
    }
}
