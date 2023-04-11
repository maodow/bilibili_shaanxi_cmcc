package tv.huan.bilibili.ui.main.general.template;


import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import lib.kalu.frame.mvp.util.WrapperUtil;
import lib.kalu.leanback.presenter.ListTvRowHeadPresenter;
import lib.kalu.mediaplayer.config.start.StartBuilder;
import tv.huan.bilibili.BuildConfig;
import tv.huan.bilibili.R;
import tv.huan.bilibili.bean.GetSubChannelsByChannelBean;
import tv.huan.bilibili.ui.main.MainActivity;
import tv.huan.bilibili.utils.BoxUtil;
import tv.huan.bilibili.utils.DevicesUtils;
import tv.huan.bilibili.utils.GlideUtils;
import tv.huan.bilibili.utils.JumpUtil;
import tv.huan.bilibili.utils.LogUtil;
import tv.huan.bilibili.widget.player.PlayerComponentInitTemplate;
import tv.huan.bilibili.widget.player.PlayerView;
import tv.huan.bilibili.widget.player.PlayerViewTemplate;

public class GeneralTemplate21 extends ListTvRowHeadPresenter<GetSubChannelsByChannelBean.ListBean.TemplateBean> {

    @Override
    public String initRowTitle(Context context) {
        if (BuildConfig.HUAN_TEST_TEMPLATE_ENABLE) {
            return "模板20";
        } else {
            return super.initRowTitle(context);
        }
    }

    @Override
    public void initItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int margin = view.getResources().getDimensionPixelOffset(R.dimen.dp_24);
        outRect.set(0, 0, margin, 0);
    }

    @Override
    public int initPaddingBottom(@NonNull Context context) {
        return context.getResources().getDimensionPixelOffset(R.dimen.dp_40);
    }

    @Override
    public int initTitlePaddingBottom(@NonNull Context context) {
        return context.getResources().getDimensionPixelOffset(R.dimen.dp_12);
    }

    @Override
    protected boolean canScrollHorizontally(int count) {
        return count > 4;
    }

    @Override
    protected void onFocusItemHolder(@NonNull Context context, @NonNull View view, @NonNull GetSubChannelsByChannelBean.ListBean.TemplateBean templateBean, @NonNull int position, boolean hasFocus) {
        try {
            TextView textView = view.findViewById(R.id.common_poster_name);
            textView.setEllipsize(hasFocus ? TextUtils.TruncateAt.MARQUEE : TextUtils.TruncateAt.END);
        } catch (Exception e) {
        }
        try {
            if (!hasFocus)
                throw new Exception();
            View parent = (View) view.getParent().getParent();
            stopPlayer(parent);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onClickItemHolder(@NonNull Context context, @NonNull View view, @NonNull GetSubChannelsByChannelBean.ListBean.TemplateBean templateBean, @NonNull int i) {
        try {
            JumpUtil.next(view.getContext(), templateBean);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onBindItemHolder(@NonNull Context context, @NonNull View view, @NonNull GetSubChannelsByChannelBean.ListBean.TemplateBean templateBean, @NonNull int position) {
        try {
            TextView textView = view.findViewById(R.id.common_poster_name);
            textView.setText(templateBean.getName());
        } catch (Exception e) {
        }
        try {
            ImageView imageView = view.findViewById(R.id.common_poster_img);
            GlideUtils.loadHz(imageView.getContext(), templateBean.getPicture(true), imageView);
        } catch (Exception e) {
        }
        try {
            ImageView imageView = view.findViewById(R.id.common_poster_vip);
            GlideUtils.loadVt(imageView.getContext(), templateBean.getVipUrl(), imageView);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onBindHeadHolder(@NonNull Context context, @NonNull View view, @NonNull GetSubChannelsByChannelBean.ListBean.TemplateBean templateBean, @NonNull int position) {
        try {
            ImageView imageView = view.findViewById(R.id.general_template21_poster);
            boolean hasExtPoster = templateBean.hasExtPoster();
            if (hasExtPoster) {
                GlideUtils.loadHz(imageView.getContext(), templateBean.getExtPoster(), imageView);
            } else {
                imageView.setImageDrawable(null);
            }
        } catch (Exception e) {
        }
        try {
            TextView textView = view.findViewById(R.id.general_template21_title);
            textView.setVisibility(templateBean.hasExtPoster() ? View.GONE : View.VISIBLE);
            textView.setText(templateBean.getName());
        } catch (Exception e) {
        }
        try {
            TextView textView = view.findViewById(R.id.general_template21_cname);
            textView.setVisibility(templateBean.hasExtPoster() ? View.GONE : View.VISIBLE);
            textView.setText(templateBean.getCname());
        } catch (Exception e) {
        }
        try {
            TextView textView = view.findViewById(R.id.general_template21_tag);
            textView.setText(templateBean.getTag());
        } catch (Exception e) {
        }
        try {
            TextView textView = view.findViewById(R.id.general_template21_score);
            textView.setText(templateBean.getScore());
        } catch (Exception e) {
        }
        try {
            TextView textView = view.findViewById(R.id.general_template21_info);
            textView.setText(templateBean.getDescription());
        } catch (Exception e) {
        }
        try {
            PlayerViewTemplate playerView = view.findViewById(R.id.general_template21_player);
            PlayerComponentInitTemplate component = playerView.findComponent(PlayerComponentInitTemplate.class);
            if (null == component)
                throw new Exception("component error: null");
            String picture = templateBean.getPicture(true);
            component.showImage(picture);
        } catch (Exception e) {
        }

        if (BuildConfig.HUAN_HUAWEI_AUTH) {
            try {
                Activity activity = WrapperUtil.getWrapperActivity(view.getContext());
                if (activity instanceof MainActivity) {
                    String platformType = DevicesUtils.INSTANCE.getPlatform(); //判断平台
                    String mediaCid = "0".equals(platformType) ? templateBean.getHwMovieCode() : templateBean.getMovieCode();
                    ((MainActivity) activity).getMediaUrl(GeneralTemplate21.class, GeneralTemplate21.GeneralTemplate21List.class, mediaCid);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                String url = BoxUtil.getTestVideoUrl();
                startPlayer(view, url);
            } catch (Exception e) {
            }
        }
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_general_item_template21b;
    }

    @Override
    protected int initContent() {
        return R.layout.fragment_general_item_template21a;
    }

    @Override
    protected int initContentMarginBottom(Context context) {
        return context.getResources().getDimensionPixelOffset(R.dimen.dp_24);
    }

    private void stopPlayer(View inflate) {
        try {
            PlayerView playerView = inflate.findViewById(R.id.general_template21_player);
            playerView.stop();
            playerView.release();
        } catch (Exception e) {
        }
    }

    public void pausePlayer(ViewGroup viewGroup) {
        try {
            PlayerView playerView = viewGroup.findViewById(R.id.general_template21_player);
            String url = playerView.getUrl();
            if (null == url || url.length() <= 0)
                throw new Exception("url error: " + url);
            boolean playing = playerView.isPlaying();
            if (!playing)
                throw new Exception("playing error: false");
            playerView.pause();
        } catch (Exception e) {
            LogUtil.log("GeneralTemplate21 => pausePlayer => " + e.getMessage());
        }
    }

    public void resumePlayer(ViewGroup viewGroup) {
        try {
            PlayerView playerView = viewGroup.findViewById(R.id.general_template21_player);
            String url = playerView.getUrl();
            if (null == url || url.length() <= 0)
                throw new Exception("url error: " + url);
            boolean playing = playerView.isPlaying();
            if (playing)
                throw new Exception("playing error: true");
            playerView.resume();
        } catch (Exception e) {
            LogUtil.log("GeneralTemplate21 => resumePlayer => " + e.getMessage());
        }
    }

    public void startPlayer(View inflate, String s) {
        try {
            if (null == s || s.length() <= 0)
                throw new Exception("url error: null");
            PlayerView playerView = inflate.findViewById(R.id.general_template21_player);
            StartBuilder.Builder builder = new StartBuilder.Builder();
            builder.setLoop(true);
            builder.setDelay(3000);
            playerView.start(builder.build(), s);
        } catch (Exception e) {
            LogUtil.log("GeneralTemplate21 => startPlayer => " + e.getMessage());
        }
    }

    public static class GeneralTemplate21List extends ArrayList {
    }
}