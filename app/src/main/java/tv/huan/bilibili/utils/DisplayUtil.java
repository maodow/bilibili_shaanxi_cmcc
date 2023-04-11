package tv.huan.bilibili.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.view.View;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;

/**
 * Author: yhw on 2016-10-12.
 */

public class DisplayUtil {
    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue （DisplayMetrics类中属性density）
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue （DisplayMetrics类中属性density）
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    private static final float ZOOM_SCALE = 1.2f;
    private static final float ZOOM_RECYCLER_SCALE = 1.1f;
    private static final float ZOOM_RECYCLER_SCALE_X = 1.1f;
    private static final float ORIGIN_SCALE = 1.0f;
    private static final int SCALE_TIME = 150;

    public static void setViewAnimator(View v, boolean focus, boolean isRecycler) {
        if (!isRecycler) {
            setViewAnimator(v, focus, ZOOM_SCALE);
        } else {
            setViewAnimator(v, focus, ZOOM_RECYCLER_SCALE);
        }

    }

    public static void setViewAnimatorOther(View v, boolean focus, boolean isRecycler) {
        if (!isRecycler) {
            setViewAnimator(v, focus, ZOOM_SCALE);
        } else {
            setViewAnimatorOther(v, focus, ZOOM_RECYCLER_SCALE, ZOOM_RECYCLER_SCALE_X);
        }

    }

    public static void setViewAnimator2(View v, boolean focus, float x, float y) {
        setViewAnimatorOther(v, focus, y, x);
    }

    private static void setViewAnimatorOther(View v, boolean focus, float... params) {
        AnimatorSet animatorSet = new AnimatorSet();//组合动画
        ObjectAnimator scaleX;
        ObjectAnimator scaleY;
        if (focus) {
            scaleX = ObjectAnimator.ofFloat(v, "scaleX", ORIGIN_SCALE, params[1]);
            scaleY = ObjectAnimator.ofFloat(v, "scaleY", ORIGIN_SCALE, params[0]);
        } else {
            scaleX = ObjectAnimator.ofFloat(v, "scaleX", params[1], ORIGIN_SCALE);
            scaleY = ObjectAnimator.ofFloat(v, "scaleY", params[0], ORIGIN_SCALE);
        }
//        if (params.length > 1) {
//            v.setPivotX(params[1]);
//            v.setPivotY(params[2]);
//        }
        animatorSet.setDuration(SCALE_TIME);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.play(scaleX).with(scaleY);//两个动画同时开始
        animatorSet.start();

    }

    public static void setViewAnimatorSmall(View v, float x, float y) {
        AnimatorSet animatorSet = new AnimatorSet();//组合动画
        ObjectAnimator scaleX;
        ObjectAnimator scaleY;

        scaleX = ObjectAnimator.ofFloat(v, "scaleX", ORIGIN_SCALE, x);
        scaleY = ObjectAnimator.ofFloat(v, "scaleY", ORIGIN_SCALE, y);

        animatorSet.setDuration(SCALE_TIME);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.play(scaleX).with(scaleY);
        animatorSet.start();
    }

    public static void setViewAnimatorNormal(View v, float x, float y) {
        AnimatorSet animatorSet = new AnimatorSet();//组合动画
        ObjectAnimator scaleX;
        ObjectAnimator scaleY;

        scaleX = ObjectAnimator.ofFloat(v, "scaleX", x, ORIGIN_SCALE);
        scaleY = ObjectAnimator.ofFloat(v, "scaleY", y, ORIGIN_SCALE);

        animatorSet.setDuration(SCALE_TIME);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.play(scaleX).with(scaleY);
        animatorSet.start();
    }

    private static void setViewAnimator(View v, boolean focus, float... params) {
        AnimatorSet animatorSet = new AnimatorSet();//组合动画
        ObjectAnimator scaleX;
        ObjectAnimator scaleY;
        if (focus) {
            scaleX = ObjectAnimator.ofFloat(v, "scaleX", ORIGIN_SCALE, params[0]);
            scaleY = ObjectAnimator.ofFloat(v, "scaleY", ORIGIN_SCALE, params[0]);
        } else {
            scaleX = ObjectAnimator.ofFloat(v, "scaleX", params[0], ORIGIN_SCALE);
            scaleY = ObjectAnimator.ofFloat(v, "scaleY", params[0], ORIGIN_SCALE);
        }
//        if (params.length > 1) {
//            v.setPivotX(params[1]);
//            v.setPivotY(params[2]);
//        }
        animatorSet.setDuration(SCALE_TIME);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.play(scaleX).with(scaleY);//两个动画同时开始
        animatorSet.start();

    }

    public static void setRecyclerViewAnimator(View v, boolean focus, float... params) {
        AnimatorSet animatorSet = new AnimatorSet();//组合动画
        ObjectAnimator scaleX;
        ObjectAnimator scaleY;
        if (focus) {
            scaleX = ObjectAnimator.ofFloat(v, "scaleX", ORIGIN_SCALE, params[0]);
            scaleY = ObjectAnimator.ofFloat(v, "scaleY", ORIGIN_SCALE, params[0]);
        } else {
            scaleX = ObjectAnimator.ofFloat(v, "scaleX", params[0], ORIGIN_SCALE);
            scaleY = ObjectAnimator.ofFloat(v, "scaleY", params[0], ORIGIN_SCALE);
        }
        if (params.length > 1) {
            v.setPivotX(params[1]);
            v.setPivotY(params[2]);
        }
        animatorSet.setDuration(SCALE_TIME);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.play(scaleX).with(scaleY);//两个动画同时开始
        animatorSet.start();
    }


//    public static void showVipOrSpecial(ImageView imageView, SubChannelsInfo.DataBean.ListBean.TemplateDataBean templateDataBean) {
//
//        try {
//            showVipOrSpecial(imageView, templateDataBean.getPic());
//        } catch (Exception e) {
//        }
//    }


    public static void showVipOrSpecial(@NonNull ImageView imageView, int payStatus, @NonNull String url) {

        try {
            showVipOrSpecial(imageView, url);
        } catch (Exception e) {
        }
    }

    public static void showVipOrSpecial(@NonNull ImageView imageView, @NonNull String url) {

//        if (null != imageView && imageView.getVisibility() != View.VISIBLE) {
//            imageView.setVisibility(View.VISIBLE);
//        }
//
//        try {
//            if (null != url && url.length() > 0) {
//                GlideUtils.loadUrl(imageView.getContext(), url, imageView);
//            } else {
//                imageView.setImageDrawable(null);
//            }
//        } catch (Exception e) {
//        }
    }

    public static void showMediaTag(@NonNull ImageView imageView, int playType, int index, String url) {
//        Log.e("WEWE", "showMediaTag => playType = " + playType + ", index = " + index + ", url = " + url);

//        if (playType == Integer.MAX_VALUE || index < playType) {
//            imageView.setImageDrawable(null);
//        } else {
//            if (null != url && url.length() > 0) {
//                GlideUtils.loadUrl(imageView.getContext(), url, imageView);
//            } else {
//                imageView.setImageDrawable(null);
//            }
//        }
    }

    /**
     * 设置window全局灰
     * @param window x
     */
    public static void setGrayBase(Window window) {
        if (window == null) return;
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0f);
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        window.getDecorView().setLayerType(View.LAYER_TYPE_HARDWARE, paint);
    }

}

