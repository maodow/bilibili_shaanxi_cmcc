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
import java.util.List;

import lib.kalu.frame.mvp.util.WrapperUtil;
import lib.kalu.leanback.presenter.ListTvGridPresenter;
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

public final class GeneralTemplate22 extends ListTvGridPresenter<GetSubChannelsByChannelBean.ListBean.TemplateBean> {

    @Override
    public String initRowTitle(Context context) {
        if (BuildConfig.HUAN_TEST_TEMPLATE_ENABLE) {
            return "模板21";
        } else {
            return super.initRowTitle(context);
        }
    }

    @Override
    public void initItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);

        int offset = view.getResources().getDimensionPixelOffset(R.dimen.dp_72) / 8;
        if (position == 0) {
            int margin = view.getResources().getDimensionPixelOffset(R.dimen.dp_24);
            outRect.set(0, 0, 0, margin);
        } else if (position == 1) {
            outRect.set(0, 0, offset * 2, 0);
        } else if (position == 4) {
            outRect.set(offset * 2, 0, 0, 0);
        } else {
            outRect.set(offset, 0, offset, 0);
        }

        int transX = offset * 2 / (3 * 2);
        if (position == 2) {
            view.setTranslationX(-transX);
        } else if (position == 3) {
            view.setTranslationX(transX);
        }
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
    protected void onCreateHolder(@NonNull Context context, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull View view, @NonNull List<GetSubChannelsByChannelBean.ListBean.TemplateBean> list, @NonNull int viewType) {
        // img
        if (viewType == 22_2) {
            try {
                view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        TextView textView = view.findViewById(R.id.common_poster_name);
                        textView.setEllipsize(b ? TextUtils.TruncateAt.MARQUEE : TextUtils.TruncateAt.END);
                    }
                });
            } catch (Exception e) {
            }
            try {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = viewHolder.getAbsoluteAdapterPosition();
                        if (position >= 0) {
                            GetSubChannelsByChannelBean.ListBean.TemplateBean templateBean = list.get(position);
                            JumpUtil.next(v.getContext(), templateBean);
                        }
                    }
                });
            } catch (Exception e) {
            }
        }
        // video
        else if (viewType == 22_1) {
            try {
                view.findViewById(R.id.general_template22_root).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = viewHolder.getAbsoluteAdapterPosition();
                        if (position >= 0) {
                            GetSubChannelsByChannelBean.ListBean.TemplateBean templateBean = list.get(position);
                            JumpUtil.next(v.getContext(), templateBean);
                        }
                    }
                });
            } catch (Exception e) {
            }
        }
    }

    @Override
    protected void onBindHolder(@NonNull View view, @NonNull GetSubChannelsByChannelBean.ListBean.TemplateBean templateBean, @NonNull int position, @NonNull int viewType) {

        // player
        if (viewType == 22_1) {
            try {
                PlayerViewTemplate playerView = view.findViewById(R.id.general_template22_player);
                PlayerComponentInitTemplate component = playerView.findComponent(PlayerComponentInitTemplate.class);
                if (null != component) {
                    component.showImage(templateBean.getPicture(true));
                }
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
        // img
        else if (viewType == 22_2) {
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
    }

    @Override
    protected int initSpanSize(int position) {
        return position == 0 ? 4 : 1;
    }

    @Override
    protected int initItemViewType(int position, GetSubChannelsByChannelBean.ListBean.TemplateBean data) {
        if (position == 0) {
            return 22_1;
        } else {
            return 22_2;
        }
    }

    @Override
    protected int initLayout(int viewType) {
        if (viewType == 22_1) {
            return R.layout.fragment_general_item_template22a;
        } else {
            return R.layout.fragment_general_item_template22b;
        }
    }

    @Override
    protected int initSpan() {
        return 4;
    }

    @Override
    protected int initMax() {
        return 5;
    }

    public void pausePlayer(ViewGroup viewGroup) {
        LogUtil.log("GeneralTemplate22 => pausePlayer =>");
        try {
            PlayerView playerView = viewGroup.findViewById(R.id.general_template22_player);
            playerView.setPlayWhenReady(false);
            playerView.pause();
        } catch (Exception e) {
            LogUtil.log("GeneralTemplate22 => pausePlayer => " + e.getMessage());
        }
    }

    public void resumePlayer(ViewGroup viewGroup) {
        LogUtil.log("GeneralTemplate22 => resumePlayer =>");
        try {
            PlayerView playerView = viewGroup.findViewById(R.id.general_template22_player);
            playerView.setPlayWhenReady(true);
            playerView.resume();
        } catch (Exception e) {
            LogUtil.log("GeneralTemplate22 => resumePlayer => " + e.getMessage());
        }
    }

    public void startPlayer(View inflate, String s) {
        try {
            if (null == s || s.length() <= 0)
                throw new Exception("url error: null");
            PlayerView playerView = inflate.findViewById(R.id.general_template22_player);
            StartBuilder.Builder builder = new StartBuilder.Builder();
            builder.setLoop(true);
            builder.setDelay(3000);
            playerView.start(builder.build(), s);
        } catch (Exception e) {
            LogUtil.log("GeneralTemplate22 => startPlayer => " + e.getMessage());
        }
    }

    public static class GeneralTemplate22List extends ArrayList {
    }
}