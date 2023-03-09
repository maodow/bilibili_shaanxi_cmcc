package tv.huan.bilibili.ui.main.general.template;


import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import lib.kalu.leanback.presenter.ListTvGridPresenter;
import tv.huan.bilibili.R;
import tv.huan.bilibili.bean.GetSubChannelsByChannelBean;
import tv.huan.bilibili.utils.GlideUtils;
import tv.huan.bilibili.utils.JumpUtil;

public class GeneralTemplate6 extends ListTvGridPresenter<GetSubChannelsByChannelBean.ListBean.TemplateBean> {

    @Override
    protected void onCreateHolder(@NonNull Context context, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull View view, @NonNull List<GetSubChannelsByChannelBean.ListBean.TemplateBean> list, @NonNull int i) {
        try {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = viewHolder.getAbsoluteAdapterPosition();
                    GetSubChannelsByChannelBean.ListBean.TemplateBean bean = list.get(position);
                    JumpUtil.next(v.getContext(), bean);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onBindHolder(@NonNull View view, @NonNull GetSubChannelsByChannelBean.ListBean.TemplateBean templateBean, @NonNull int i, @NonNull int i1) {
        try {
            TextView textView = view.findViewById(R.id.common_poster_name);
            textView.setText(templateBean.getName());
        } catch (Exception e) {
        }
        try {
            ImageView imageView = view.findViewById(R.id.common_poster_img);
            if (i <= 3) {
                GlideUtils.loadHz(imageView.getContext(), templateBean.getPicture(true), imageView);
            } else {
                GlideUtils.loadVt(imageView.getContext(), templateBean.getPicture(false), imageView);
            }
        } catch (Exception e) {
        }
        try {
            ImageView imageView = view.findViewById(R.id.common_poster_vip);
            GlideUtils.loadVt(imageView.getContext(), templateBean.getVipUrl(), imageView);
        } catch (Exception e) {
        }
    }

    @Override
    protected int initSpan() {
        return 12;
    }

    @Override
    protected int initSpanSize(int position) {
        return position <= 3 ? 3 : 2;
    }

    @Override
    protected int initMax() {
        return 10;
    }

    @Override
    protected int initItemViewType(int position, GetSubChannelsByChannelBean.ListBean.TemplateBean templateBean) {
        return position <= 3 ? 1 : 2;
    }

    @Override
    protected int initLayout(int viewType) {
        return viewType == 1 ? R.layout.fragment_general_item_template06a : R.layout.fragment_general_item_template06b;
    }

    @Override
    public int initMagrinTop(@NonNull Context context) {
        return context.getResources().getDimensionPixelOffset(R.dimen.dp_10);
    }

    @Override
    public int initHeadPadding(@NonNull Context context) {
        return context.getResources().getDimensionPixelOffset(R.dimen.dp_10);
    }

    @Override
    public int initHeadTextSize(@NonNull Context context) {
        return context.getResources().getDimensionPixelOffset(R.dimen.sp_24);
    }

    @Override
    public String initHeadAssetTTF(@NonNull Context context) {
        return null;
    }

    @Override
    protected RecyclerView.ItemDecoration initItemDecoration() {

        return new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);

                int position = parent.getChildAdapterPosition(view);

                if (position <= 3) {
                    int offset = view.getResources().getDimensionPixelOffset(R.dimen.dp_72) / 8;

                    if (position == 0) {
                        outRect.set(0, 0, offset * 2, 0);
                    } else if (position == 3) {
                        outRect.set(offset * 2, 0, 0, 0);
                    } else {
                        outRect.set(offset, 0, offset, 0);
                    }

                    int transX = offset * 2 / 6;
                    if (position == 1) {
                        view.setTranslationX(-transX);
                    } else if (position == 2) {
                        view.setTranslationX(transX);
                    }
                } else if (position <= 9) {
                    int offset = view.getResources().getDimensionPixelOffset(R.dimen.dp_120) / 12;

                    if (position == 4) {
                        outRect.set(0, 0, offset * 2, 0);
                    } else if (position == 9) {
                        outRect.set(offset * 2, 0, 0, 0);
                    } else {
                        outRect.set(offset, 0, offset, 0);
                    }

                    int transX = offset * 2 / (5810);
                    if (position == 5) {
                        view.setTranslationX(-transX * 2);
                    } else if (position == 6) {
                        view.setTranslationX(-transX);
                    } else if (position == 7) {
                        view.setTranslationX(transX);
                    } else if (position == 8) {
                        view.setTranslationX(transX * 2);
                    }
                }
            }
        };
    }

    public static class GeneralTemplate6List extends ArrayList {
    }
}
