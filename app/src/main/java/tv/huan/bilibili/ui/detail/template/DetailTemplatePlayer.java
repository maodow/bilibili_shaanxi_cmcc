package tv.huan.bilibili.ui.detail.template;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.leanback.widget.Presenter;

import org.json.JSONObject;

import lib.kalu.frame.mvp.util.WrapperUtil;
import lib.kalu.mediaplayer.config.start.StartBuilder;
import tv.huan.bilibili.BuildConfig;
import tv.huan.bilibili.R;
import tv.huan.bilibili.bean.MediaBean;
import tv.huan.bilibili.bean.base.BaseDataBean;
import tv.huan.bilibili.ui.detail.DetailActivity;
import tv.huan.bilibili.utils.AuthUtils;
import tv.huan.bilibili.utils.BoxUtil;
import tv.huan.bilibili.utils.GlideUtils;
import tv.huan.bilibili.utils.LogUtil;
import tv.huan.bilibili.widget.common.CommonPicView;
import tv.huan.bilibili.widget.player.PlayerComponentInit;
import tv.huan.bilibili.widget.player.PlayerView;
import tv.huan.heilongjiang.OnStatusChangeListener;

public final class DetailTemplatePlayer extends Presenter {

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
//        LogUtil.log("DetailTemplatePlayer => onViewAttachedToWindow");
        stopFloat(holder.view);
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
//        LogUtil.log("DetailTemplatePlayer => onViewDetachedFromWindow");
        startFloat(holder.view);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
//        LogUtil.log("DetailTemplatePlayer => onCreateViewHolder");
        return createViewHolder(viewGroup);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
//        LogUtil.log("DetailTemplatePlayer => onUnbindViewHolder");
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object o) {
//        LogUtil.log("DetailTemplatePlayer => onBindViewHolder");
    }

    public void updateFavor(View view, boolean status) {
        try {
            TextView textView = view.findViewById(R.id.detail_player_item_favor);
            textView.setText(view.getResources().getString(status ? R.string.detail_favor_yes : R.string.detail_favor_no));
        } catch (Exception e) {
        }
    }

    public void showData(View view, MediaBean data) {
        try {
            PlayerView playerView = view.findViewById(R.id.detail_player_item_video);
            PlayerComponentInit componentInit = playerView.findComponent(PlayerComponentInit.class);
            componentInit.setData(data);
            componentInit.show();
        } catch (Exception e) {
        }
        try {
            TextView textView = view.findViewById(R.id.detail_player_item_favor);
            textView.setText(view.getResources().getString(data.isTempFavor() ? R.string.detail_favor_yes : R.string.detail_favor_no));
            JSONObject object = new JSONObject();
            object.put("cid", data.getCid());
            object.put("recClassId", data.getRecClassId());
            textView.setTag(R.id.detail_player_item_favor, object);

        } catch (Exception e) {
        }
        try {
            TextView textView = view.findViewById(R.id.detail_player_item_tag);
            textView.setText(data.getTempTag());
        } catch (Exception e) {
        }
        try {
            TextView textView = view.findViewById(R.id.detail_player_item_title);
            textView.setText(data.getTempTitle());
        } catch (Exception e) {
        }
        try {
            TextView textView = view.findViewById(R.id.detail_player_item_info);
            textView.setText(data.getTemoInfo());
        } catch (Exception e) {
        }
        try {
            LinearLayout linearLayout = view.findViewById(R.id.detail_player_item_pic);
            int childCount = linearLayout.getChildCount();
            for (int i = 0; i < childCount - 1; i++) {
                linearLayout.removeViewAt(i);
            }
            int length;
            try {
                length = data.getTempPicList().length;
            } catch (Exception e) {
                length = 0;
            }
            Context context = linearLayout.getContext();
            int height = context.getResources().getDimensionPixelOffset(R.dimen.dp_16);
            for (int i = 0; i < length; i++) {
                CommonPicView picView = new CommonPicView(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);
                int margin = linearLayout.getContext().getResources().getDimensionPixelOffset(i == length - 1 ? R.dimen.dp_8 : R.dimen.dp_4);
                params.setMargins(0, 0, margin, 0);
                picView.setLayoutParams(params);
                GlideUtils.loadHz(picView.getContext(), data.getTempPicList()[i], picView);
                linearLayout.addView(picView, i);
            }
        } catch (Exception e) {
        }
        try {
            if (BuildConfig.HUAN_ALWAYS_SHOW_DETAIL_VIP) {
                TextView textView = view.findViewById(R.id.detail_player_item_vip);
                textView.setVisibility(View.VISIBLE);
            } else {
                AuthUtils.getInstance().doAuth(new OnStatusChangeListener() {
                    @Override
                    public void onPass() {
                        TextView textView = view.findViewById(R.id.detail_player_item_vip);
                        textView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFail() {
                        TextView textView = view.findViewById(R.id.detail_player_item_vip);
                        textView.setVisibility(View.VISIBLE);
                    }
                });
            }
        } catch (Exception e) {
        }
    }

    private void hideWarning(View viewGroup) {
        LogUtil.log("DetailTemplatePlayer => hideWarning");
        try {
            PlayerView playerView = viewGroup.findViewById(R.id.detail_player_item_video);
            PlayerComponentInit componentInit = playerView.findComponent(PlayerComponentInit.class);
            componentInit.gone();
        } catch (Exception e) {
            LogUtil.log("DetailTemplatePlayer => hideWarning => " + e.getMessage());
        }
    }

    public void checkVip(View view, MediaBean data) {
        try {
            LogUtil.log("DetailTemplatePlayer => checkAccount");
            AuthUtils.getInstance().doAuth(new OnStatusChangeListener() {
                @Override
                public void onPass() {
                    getPlayUrl(view, data);
                }

                @Override
                public void onFail() {
                    goPay(view);
                }
            });
        } catch (Exception e) {
            LogUtil.log("DetailTemplatePlayer => checkAccount => " + e.getMessage());
        }
    }


    /**
     * 跳转支付
     * @param view
     */
    private void goPay(View view) {
        try {
            Activity activity = WrapperUtil.getWrapperActivity(view.getContext());
            if (null == activity)
                throw new Exception("activity error: null");
            if (!(activity instanceof DetailActivity))
                throw new Exception("activity error: " + activity);
            ((DetailActivity) activity).jumpVip();
        } catch (Exception e) {
            LogUtil.log("DetailTemplatePlayer => goPay() => " + e.getMessage());
        }
    }


    /**
     * 获取播放链接
     * @param view
     * @param data
     */
    public void getPlayUrl(View view, MediaBean data) {
        if (BuildConfig.HUAN_HUAWEI_AUTH) {
            try {
                Activity activity = WrapperUtil.getWrapperActivity(view.getContext());
                if (null == activity)
                    throw new Exception("activity error: null");
                if (!(activity instanceof DetailActivity))
                    throw new Exception("activity error: " + activity);
                ((DetailActivity) activity).getMediaUrl(data, data.getTempSeek());
            } catch (Exception e) {
                LogUtil.log("DetailTemplatePlayer => getPlayUrl() => " + e.getMessage());
            }
        } else {
            try {
                String url = BoxUtil.getTestVideoUrl();
                startPlayer(view, url, 0);
            } catch (Exception e) {
                LogUtil.log("DetailTemplatePlayer => startHuawei => " + e.getMessage());
            }
        }
    }

    public long getPosition(View view) {
        try {
            PlayerView playerView = view.findViewById(R.id.detail_player_item_video);
            return playerView.getPosition();
        } catch (Exception e) {
            LogUtil.log("DetailTemplatePlayer => getPosition => " + e.getMessage());
            return 0;
        }
    }

    public long getDuration(View view) {
        try {
            PlayerView playerView = view.findViewById(R.id.detail_player_item_video);
            return playerView.getDuration();
        } catch (Exception e) {
            LogUtil.log("DetailTemplatePlayer => getDuration => " + e.getMessage());
            return 0;
        }
    }

    public void stopPlayer(View view) {
        try {
            PlayerView playerView = view.findViewById(R.id.detail_player_item_video);
            playerView.stop();
            playerView.release();
        } catch (Exception e) {
            LogUtil.log("DetailTemplatePlayer => startPlayer => " + e.getMessage());
        }
    }

    public void startPlayer(View view, String s, long seek) {
        LogUtil.log("DetailTemplatePlayer => startPlayer");
        try {
            stopPlayer(view);
            StartBuilder.Builder builder = new StartBuilder.Builder();
            builder.setLoop(false);
            builder.setDelay(4000);
            builder.setSeek(seek);
            PlayerView playerView = view.findViewById(R.id.detail_player_item_video);
            playerView.start(builder.build(), s);
        } catch (Exception e) {
            LogUtil.log("DetailTemplatePlayer => startPlayer => " + e.getMessage());
        }
    }

    public void stopFull(View view) {
        try {
            ViewGroup viewGroup = (ViewGroup) view.getParent().getParent().getParent();
            PlayerView playerView = viewGroup.findViewById(R.id.detail_player_item_video);
            playerView.stopFull();
        } catch (Exception e) {
        }
    }

    private void stopFloat(View view) {
        try {
            ViewGroup viewGroup = (ViewGroup) view.getParent().getParent().getParent();
            PlayerView playerView = viewGroup.findViewById(R.id.detail_player_item_video);
            playerView.stopFloat();
        } catch (Exception e) {
        }
    }

    private void startFloat(View view) {
        try {
            ViewGroup viewGroup = (ViewGroup) view.getParent().getParent().getParent();
            PlayerView playerView = viewGroup.findViewById(R.id.detail_player_item_video);
            playerView.startFloat();
        } catch (Exception e) {
        }
    }

    private ViewHolder createViewHolder(ViewGroup viewGroup) {
        try {
            Context context = viewGroup.getContext();
            View view = LayoutInflater.from(context).inflate(R.layout.activity_detail_item_player, viewGroup, false);
            // 简介
            view.findViewById(R.id.detail_player_item_info).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity activity = WrapperUtil.getWrapperActivity(v.getContext());
                    LogUtil.log("DetailTemplatePlayer => createViewHolder => onClick => activity = " + activity);
                    if (null != activity && activity instanceof DetailActivity) {
                        ViewGroup group = (ViewGroup) v.getParent();
                        TextView v1 = group.findViewById(R.id.detail_player_item_title);
                        String s1 = v1.getText().toString();
                        TextView v2 = group.findViewById(R.id.detail_player_item_tag);
                        String s2 = v2.getText().toString();
                        TextView v3 = group.findViewById(R.id.detail_player_item_info);
                        String s3 = v3.getText().toString();
                        ((DetailActivity) activity).showDialog(s1, s2, s3);
                    }
                }
            });
            // 会员
            view.findViewById(R.id.detail_player_item_vip).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity activity = WrapperUtil.getWrapperActivity(v.getContext());
                    LogUtil.log("DetailTemplatePlayer => createViewHolder => onClick => activity = " + activity);
                    if (null != activity && activity instanceof DetailActivity) {
                        ((DetailActivity) activity).jumpVip();
                    }
                }
            });
            // 收藏
            view.findViewById(R.id.detail_player_item_favor).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity activity = WrapperUtil.getWrapperActivity(v.getContext());
                    LogUtil.log("DetailTemplatePlayer => createViewHolder => onClick => activity = " + activity);
                    if (null != activity && activity instanceof DetailActivity) {
                        try {
                            JSONObject object = (JSONObject) v.getTag(R.id.detail_player_item_favor);
                            String cid = object.optString("cid", "");
                            String recClassId = object.optString("recClassId", "");
                            String text = ((TextView) v).getText().toString();
                            String s = v.getResources().getString(R.string.detail_favor_no);
                            if (s.equalsIgnoreCase(text)) {
                                ((DetailActivity) activity).addFavor(cid, recClassId);
                            } else {
                                ((DetailActivity) activity).cancleFavor(cid, recClassId);
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            });
            // 全屏
            view.findViewById(R.id.detail_player_item_full).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewGroup viewGroup = (ViewGroup) v.getParent().getParent().getParent();
                    PlayerView playerView = viewGroup.findViewById(R.id.detail_player_item_video);
                    playerView.startFull();
                }
            });
            return new ViewHolder(view);
        } catch (Exception e) {
            return null;
        }
    }

    public static class DetailTemplatePlayerObject extends BaseDataBean {
    }
}