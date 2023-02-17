package tv.huan.bilibili.ui.search;

import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import lib.kalu.frame.mvp.BasePresenter;
import lib.kalu.frame.mvp.transformer.ComposeSchedulers;
import tv.huan.bilibili.utils.BoxUtil;
import tv.huan.bilibili.widget.keyboard.KeyboardView;
import tv.huan.bilibili.R;
import tv.huan.bilibili.bean.BaseBean;
import tv.huan.bilibili.http.HttpClient;
import tv.huan.bilibili.bean.SearchBean;
import tv.huan.bilibili.bean.SearchRecommendBean;
import tv.huan.bilibili.utils.GlideUtils;
import tv.huan.bilibili.utils.JumpUtil;

public class SearchPresenter extends BasePresenter<SearchView> {

    private final ArrayList<SearchBean.ItemBean> mData = new ArrayList<>();

    public SearchPresenter(@NonNull SearchView searchView) {
        super(searchView);
    }

    protected void setAdapter() {
        Context context = getView().getContext();
        RecyclerView recyclerView = getView().findViewById(R.id.search_list);
        GridLayoutManager layoutManager = new GridLayoutManager(context, 4);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int offset = view.getResources().getDimensionPixelOffset(R.dimen.dp_24);

                outRect.set(0, 0, offset, offset);

//                int position = parent.getChildAdapterPosition(view);
//                if (position % 4 == 2 || ) {
//                    outRect.set(20, 0, 0, 20);
//                } else {
//                    outRect.set(0, 0, 20, 20);
//                }
//                if (position % 3 == 1) {
//                    view.setTranslationX(10);
//                }
            }
        });
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                Context context = parent.getContext();
                View inflate = LayoutInflater.from(context).inflate(R.layout.activity_search_item, parent, false);
                RecyclerView.ViewHolder holder = new RecyclerView.ViewHolder(inflate) {
                };
                inflate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getAbsoluteAdapterPosition();
                        SearchBean.ItemBean itemBean = mData.get(position);
                        JumpUtil.next(v.getContext(), itemBean);
                    }
                });
                return holder;
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                try {
                    SearchBean.ItemBean itemBean = mData.get(position);
                    TextView textView = holder.itemView.findViewById(R.id.search_item_name);
                    textView.setText(itemBean.getName());
                    ImageView imageView = holder.itemView.findViewById(R.id.search_item_img);
                    GlideUtils.loadVt(imageView.getContext(), itemBean.getNewPicVt(), imageView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public int getItemCount() {
                return mData.size();
            }
        });
    }

    protected void searchBySpell() {

        addDisposable(Observable.create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(ObservableEmitter<Boolean> observableEmitter) {
                        observableEmitter.onNext(true);
                    }
                })
                .flatMap(new Function<Boolean, Observable<BaseBean<SearchBean>>>() {
                    @Override
                    public Observable<BaseBean<SearchBean>> apply(Boolean aBoolean) {
                        KeyboardView keyboardView = getView().findViewById(R.id.search_keyboard);
                        String result = keyboardView.getResult();
                        String lowerCase = result.toLowerCase();
                        return HttpClient.getHttpClient().getHttpApi().searchBySpell(lowerCase, 0, Integer.MAX_VALUE);
                    }
                })
                .map(new Function<BaseBean<SearchBean>, Boolean>() {
                    @Override
                    public Boolean apply(BaseBean<SearchBean> searchBeanBaseBean) {
                        try {
                            List<SearchBean.ItemBean> beans = searchBeanBaseBean.getData().getAlbums();
                            mData.clear();
                            mData.addAll(beans);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                })
                .delay(40, TimeUnit.MILLISECONDS)
                .compose(ComposeSchedulers.io_main())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) {
                        getView().showLoading();
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        getView().hideLoading();
                    }
                })
                .doOnNext(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) {
                        getView().hideLoading();
                        getView().refreshContent();
                    }
                })
                .subscribe());
    }

    protected void getSearchRecommend() {

        addDisposable(Observable.create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(ObservableEmitter<Boolean> observableEmitter) {
                        observableEmitter.onNext(true);
                    }
                })
                .flatMap(new Function<Boolean, Observable<BaseBean<SearchRecommendBean>>>() {
                    @Override
                    public Observable<BaseBean<SearchRecommendBean>> apply(Boolean aBoolean) {
                        return HttpClient.getHttpClient().getHttpApi().getSearchRecommend(BoxUtil.getProdId(), Integer.MAX_VALUE);
                    }
                })
                .map(new Function<BaseBean<SearchRecommendBean>, Boolean>() {
                    @Override
                    public Boolean apply(BaseBean<SearchRecommendBean> searchRecommendBeanBaseBean) throws Exception {
                        try {
                            List<SearchRecommendBean.RecommendsBean> recommends = searchRecommendBeanBaseBean.getData().getRecommends();
                            mData.clear();
                            mData.addAll(recommends);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                })
                .delay(40, TimeUnit.MILLISECONDS)
                .compose(ComposeSchedulers.io_main())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) {
                        getView().showLoading();
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        getView().hideLoading();
                    }
                })
                .doOnNext(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) {
                        getView().hideLoading();
                        getView().refreshContent();
                    }
                })
                .subscribe());
    }

//    /**
//     * 获取热搜推荐
//     *
//     * @param size 个数
//     */
//    public void getHotSearchRecommends(int size) {
//
//        int prod = HuanApp.getProd(null);
//        mRepository.getSearchRecommend(new CallBack<SearchRecommendBean>() {
//            @Override
//            public void onNoNetWork() {
//                loadState.postValue(StateConstants.NET_WORK_STATE);
//            }
//
//            @Override
//            public void onNext(SearchRecommendBean searchRecommendBean) {
//                if (searchRecommendBean == null || searchRecommendBean.getData() == null || searchRecommendBean.getCode() != 200) {
//                    loadState.postValue(StateConstants.ERROR_STATE);
//                    return;
//                }
//                recommendsResult.postValue(searchRecommendBean.getData());
//            }
//
//            @Override
//            public void onError(String e) {
//                loadState.postValue(StateConstants.ERROR_STATE);
//            }
//        }, prod, size);
//    }
}