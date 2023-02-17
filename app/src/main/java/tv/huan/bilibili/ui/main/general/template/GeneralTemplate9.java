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

import lib.kalu.leanback.presenter.ListGridPresenter;
import tv.huan.bilibili.R;
import tv.huan.bilibili.bean.GetSubChannelsByChannelBean;
import tv.huan.bilibili.utils.GlideUtils;
import tv.huan.bilibili.utils.JumpUtil;

public class GeneralTemplate9 extends ListGridPresenter<GetSubChannelsByChannelBean.ListBean.TemplateBean> {

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
            @IdRes
            int txt = (i == 0 ? -1 : R.id.album_item_name_template92);
            if (txt != -1) {
                TextView textView = view.findViewById(txt);
                textView.setText(templateBean.getName());
            }
            @IdRes
            int img = (i == 0 ? R.id.album_item_img_template91 : R.id.album_item_img_template92);
            ImageView imageView = view.findViewById(img);
            if (i == 0) {
                GlideUtils.loadHz(imageView.getContext(), templateBean.getNewPicHz(), imageView);
            } else {
                GlideUtils.loadVt(imageView.getContext(), templateBean.getNewPicVt(), imageView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected int initSpan() {
        return 6;
    }

    @Override
    protected int initSpanSize(int position) {
        return position == 0 ? 6 : 1;
    }

    @Override
    protected int initMax() {
        return 7;
    }

    @Override
    protected int initItemViewType(int position, GetSubChannelsByChannelBean.ListBean.TemplateBean templateBean) {
        return position == 0 ? 1 : 2;
    }

    @Override
    protected int initLayout(int viewType) {
        return viewType == 1 ? R.layout.fragment_general_item_template09a : R.layout.fragment_general_item_template09b;
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

                Context context = view.getContext();
                int position = parent.getChildAdapterPosition(view);
                int offset = context.getResources().getDimensionPixelOffset(R.dimen.dp_10);

                if (position > 0 && position <= 7) {
                    if (position == 7) {
                        outRect.set(offset, 0, 0, offset);
                    } else {
                        outRect.set(0, 0, offset, offset);
                    }
                }

                if (position != 0 && position != 1 && position != 7) {
                    view.setTranslationX(offset / 2);
                }
            }
        };
    }

    public static class GeneralTemplate9List extends ArrayList {
    }
}