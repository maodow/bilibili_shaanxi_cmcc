package tv.huan.bilibili.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ItemBridgeAdapter;

import com.google.gson.Gson;

import lib.kalu.leanback.list.LeanBackVerticalGridView;
import tv.huan.bilibili.R;
import tv.huan.bilibili.bean.MediaBean;
import tv.huan.bilibili.ui.detail.template.DetailTemplatePlayer;
import tv.huan.bilibili.ui.detail.template.DetailTemplateXuanJi;
import tv.huan.bilibili.ui.detail.template.DetailTemplateXuanQi;
import tv.huan.bilibili.utils.LogUtil;
import tv.huan.bilibili.widget.player.PlayerView;

public final class DetailGridView extends LeanBackVerticalGridView {
    public DetailGridView(@NonNull Context context) {
        super(context);
        init();
    }

    public DetailGridView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DetailGridView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
    }

    /*********/

    public void stopPlayer() {
        try {
            ItemBridgeAdapter itemBridgeAdapter = (ItemBridgeAdapter) getAdapter();
            ArrayObjectAdapter objectAdapter = (ArrayObjectAdapter) itemBridgeAdapter.getAdapter();
            DetailTemplatePlayer.DetailTemplatePlayerObject playerObject = (DetailTemplatePlayer.DetailTemplatePlayerObject) objectAdapter.get(0);
            playerObject.setUpdateOnlyVideoStop(true);
            itemBridgeAdapter.notifyItemChanged(0);
        } catch (Exception e) {
        }
    }

    public void nextPlayer(@NonNull int position) {
        try {
            ItemBridgeAdapter itemBridgeAdapter = (ItemBridgeAdapter) getAdapter();
            ArrayObjectAdapter objectAdapter = (ArrayObjectAdapter) itemBridgeAdapter.getAdapter();
            DetailTemplatePlayer.DetailTemplatePlayerObject playerObject = (DetailTemplatePlayer.DetailTemplatePlayerObject) objectAdapter.get(0);
            playerObject.setPlayingIndex(position);
            playerObject.setUpdateOnlyVideoStop(true);
            itemBridgeAdapter.notifyItemChanged(0);
        } catch (Exception e) {
        }
    }

    public boolean containsXuanJi() {
        boolean contains = false;
        try {
            ItemBridgeAdapter itemBridgeAdapter = (ItemBridgeAdapter) getAdapter();
            ArrayObjectAdapter objectAdapter = (ArrayObjectAdapter) itemBridgeAdapter.getAdapter();
            int size = objectAdapter.size();
            for (int i = 0; i < size; i++) {
                Object o = objectAdapter.get(i);
                if (null == o)
                    continue;
                if (o instanceof DetailTemplateXuanJi.DetailTemplateXuanJiList) {
                    contains = true;
                    break;
                }
            }
        } catch (Exception e) {
        }
        return contains;
    }

    public boolean containsXuanQi() {
        boolean contains = false;
        try {
            ItemBridgeAdapter itemBridgeAdapter = (ItemBridgeAdapter) getAdapter();
            ArrayObjectAdapter objectAdapter = (ArrayObjectAdapter) itemBridgeAdapter.getAdapter();
            int size = objectAdapter.size();
            for (int i = 0; i < size; i++) {
                Object o = objectAdapter.get(i);
                if (null == o)
                    continue;
                if (o instanceof DetailTemplateXuanQi.DetailTemplateXuanQiList) {
                    contains = true;
                    break;
                }
            }
        } catch (Exception e) {
        }
        return contains;
    }

    public long getPlayerPosition() {
        try {
            PlayerView playerView = findViewById(R.id.detail_player_item_video);
            return playerView.getPosition();
        } catch (Exception e) {
            return 0;
        }
    }

    public long getPlayerDuraion() {
        try {
            PlayerView playerView = findViewById(R.id.detail_player_item_video);
            return playerView.getDuration();
        } catch (Exception e) {
            return 0;
        }
    }

    public void updateFavor(boolean status) {
        try {
            ItemBridgeAdapter itemBridgeAdapter = (ItemBridgeAdapter) getAdapter();
            ArrayObjectAdapter objectAdapter = (ArrayObjectAdapter) itemBridgeAdapter.getAdapter();
            DetailTemplatePlayer.DetailTemplatePlayerObject playerObject = (DetailTemplatePlayer.DetailTemplatePlayerObject) objectAdapter.get(0);
            playerObject.setUpdateOnlyFavor(true);
            playerObject.setFavor(status);
            itemBridgeAdapter.notifyItemChanged(0);
        } catch (Exception e) {
        }
    }

    public void stopFull() {
        try {
            ItemBridgeAdapter itemBridgeAdapter = (ItemBridgeAdapter) getAdapter();
            ArrayObjectAdapter objectAdapter = (ArrayObjectAdapter) itemBridgeAdapter.getAdapter();
            DetailTemplatePlayer.DetailTemplatePlayerObject playerObject = (DetailTemplatePlayer.DetailTemplatePlayerObject) objectAdapter.get(0);
            playerObject.setUpdateOnlyStopFull(true);
            itemBridgeAdapter.notifyItemChanged(0);
        } catch (Exception e) {
        }
    }

    public void startPlayer(@NonNull MediaBean data) {
        LogUtil.log("DetailGridView", "startPlayer => ");
        try {
            ItemBridgeAdapter itemBridgeAdapter = (ItemBridgeAdapter) getAdapter();
            ArrayObjectAdapter objectAdapter = (ArrayObjectAdapter) itemBridgeAdapter.getAdapter();
            DetailTemplatePlayer.DetailTemplatePlayerObject playerData = (DetailTemplatePlayer.DetailTemplatePlayerObject) objectAdapter.get(0);
            playerData.setUpdateOnlyVideoPlaying(true);
            playerData.setPlayingIndex(data.getTempIndex());
            playerData.setVideoUrl(data.getTempVideoUrl());
            playerData.setSeek(data.getTempSeek());
            itemBridgeAdapter.notifyItemChanged(0);
        } catch (Exception e) {
        }
    }

    public boolean dispatchKeyEvent(int direction) {

        try {
            ItemBridgeAdapter itemBridgeAdapter = (ItemBridgeAdapter) getAdapter();
            ArrayObjectAdapter objectAdapter = (ArrayObjectAdapter) itemBridgeAdapter.getAdapter();
            Object o = objectAdapter.get(1);
            if (null == o)
                throw new Exception("objectAdapter null");
            // 选集列表
            if (o instanceof DetailTemplateXuanJi.DetailTemplateXuanJiList) {
                DetailTemplateXuanJi presenter = getPresenter(DetailTemplateXuanJi.class);
                if (null != presenter && direction == View.FOCUS_DOWN) {
                    return presenter.dispatchKeyEventCheckedPositionEpisode(this);
                } else if (null != presenter && direction == View.FOCUS_UP) {
                    return presenter.dispatchKeyEventCheckedPositionRange(this);
                }
            }
            // 选期列表
            else if (o instanceof DetailTemplateXuanQi.DetailTemplateXuanQiList) {
                DetailTemplateXuanQi presenter = getPresenter(DetailTemplateXuanQi.class);
                if (null != presenter) {
                    return presenter.dispatchKeyEventCheckedPosition(this);
                }
            }
            throw new Exception("not find");
        } catch (Exception e) {
            LogUtil.log("DetailGridView", "dispatchKeyEvent => " + e.getMessage());
            return false;
        }
    }

    public void checkedPlayerPosition(@NonNull MediaBean data) {
        LogUtil.log("DetailGridView => checkedPlayerPosition => " + new Gson().toJson(data));
        try {
            ItemBridgeAdapter itemBridgeAdapter = (ItemBridgeAdapter) getAdapter();
            ArrayObjectAdapter objectAdapter = (ArrayObjectAdapter) itemBridgeAdapter.getAdapter();
            Object o = objectAdapter.get(1);
            if (null == o)
                throw new Exception("objectAdapter null");
            // 选集列表
            if (o instanceof DetailTemplateXuanJi.DetailTemplateXuanJiList) {
                int index = data.getTempIndex();
                DetailTemplateXuanJi presenter = getPresenter(DetailTemplateXuanJi.class);
                if (index > 0 && null != presenter) {
                    int position = --index;
                    presenter.checkedPositionEpisode(this, position);
                }
            }
            // 选期列表
            else if (o instanceof DetailTemplateXuanQi.DetailTemplateXuanQiList) {
                int index = data.getTempIndex();
                DetailTemplateXuanQi presenter = getPresenter(DetailTemplateXuanQi.class);
                if (index > 0 && null != presenter) {
                    int position = --index;
                    presenter.checkedPosition(this, position);
                }
            }
            // 电影
            else {
                startPlayer(data);
            }
        } catch (Exception e) {
            LogUtil.log("DetailGridView", "checkedPlayerPosition => " + e.getMessage());
        }
    }

    public void checkedPlayerPositionNext() {
        LogUtil.log("DetailGridView => checkedPlayerPositionNext =>");
        try {
            ItemBridgeAdapter itemBridgeAdapter = (ItemBridgeAdapter) getAdapter();
            ArrayObjectAdapter objectAdapter = (ArrayObjectAdapter) itemBridgeAdapter.getAdapter();
            Object o = objectAdapter.get(1);
            if (null == o)
                throw new Exception("objectAdapter null");
            // 选集列表
            if (o instanceof DetailTemplateXuanJi.DetailTemplateXuanJiList) {
                DetailTemplateXuanJi presenter = getPresenter(DetailTemplateXuanJi.class);
                if (null != presenter) {
                    presenter.checkedPositionEpisodeNext(this);
                }
            }
            // 选期列表
            else if (o instanceof DetailTemplateXuanQi.DetailTemplateXuanQiList) {
                DetailTemplateXuanQi presenter = getPresenter(DetailTemplateXuanQi.class);
                if (null != presenter) {
                    presenter.checkedPositionNext(this);
                }
            }
        } catch (Exception e) {
            LogUtil.log("DetailGridView", "checkedPlayerPositionNext => " + e.getMessage());
        }
    }

    public void refreshPositionRange(@NonNull MediaBean data) {
        DetailTemplateXuanJi presenter = getPresenter(DetailTemplateXuanJi.class);
        LogUtil.log("DetailGridView", "updatePlayerPosition => presenter = " + presenter);
        if (null == presenter)
            return;
        int index = data.getTempIndex();
        LogUtil.log("DetailGridView", "updatePlayerPosition => index = " + index);
        if (index <= 0)
            return;
        int position = --index;
        presenter.refreshPositionRange(this, position);
    }

    public void updatePlayerInfo(@NonNull MediaBean data) {
        LogUtil.log("DetailGridView => updatePlayerInfo => " + new Gson().toJson(data));
        try {
            ItemBridgeAdapter itemBridgeAdapter = (ItemBridgeAdapter) getAdapter();
            ArrayObjectAdapter objectAdapter = (ArrayObjectAdapter) itemBridgeAdapter.getAdapter();
            DetailTemplatePlayer.DetailTemplatePlayerObject playerObject = (DetailTemplatePlayer.DetailTemplatePlayerObject) objectAdapter.get(0);
            playerObject.setFavor(data.isTempFavor());
            playerObject.setVip(data.isTempVip());
            playerObject.setPlayingIndex(data.getTempIndex());
            playerObject.setImageUrl(data.getTempImageUrl());
            playerObject.setTag(data.getTempTag());
            playerObject.setTitle(data.getTempTitle());
            playerObject.setCid(data.getCid());
            playerObject.setInfo(data.getTemoInfo());
            playerObject.setRecClassId(data.getTempRecClassId());
            playerObject.setPicList(data.getTempPicList());
            itemBridgeAdapter.notifyItemChanged(0);
        } catch (Exception e) {
        }
    }

    public int getPlayerNextPosition() {
        LogUtil.log("DetailGridView => getPlayerNextPosition =>");
        try {
            ItemBridgeAdapter itemBridgeAdapter = (ItemBridgeAdapter) getAdapter();
            ArrayObjectAdapter objectAdapter = (ArrayObjectAdapter) itemBridgeAdapter.getAdapter();
            Object o = objectAdapter.get(1);
            if (null == o)
                throw new Exception("objectAdapter null");
            // 选集列表
            if (o instanceof DetailTemplateXuanJi.DetailTemplateXuanJiList) {
                DetailTemplateXuanJi presenter = getPresenter(DetailTemplateXuanJi.class);
                if (null != presenter) {
                    return presenter.getEpisodeNextPosition();
                }
            }
            // 选期列表
            else if (o instanceof DetailTemplateXuanQi.DetailTemplateXuanQiList) {
                DetailTemplateXuanQi presenter = getPresenter(DetailTemplateXuanQi.class);
                if (null != presenter) {
                    return presenter.getEpisodeNextPosition();
                }
            }
            throw new Exception("not find");
        } catch (Exception e) {
            LogUtil.log("DetailGridView", "checkedPlayerPositionNext => " + e.getMessage());
            return -1;
        }
    }

    public boolean isPlayingEnd() {
        LogUtil.log("DetailGridView => isPlayingEnd =>");
        try {
            ItemBridgeAdapter itemBridgeAdapter = (ItemBridgeAdapter) getAdapter();
            ArrayObjectAdapter objectAdapter = (ArrayObjectAdapter) itemBridgeAdapter.getAdapter();
            Object o = objectAdapter.get(1);
            if (null == o)
                throw new Exception("objectAdapter null");
            // 选集列表
            if (o instanceof DetailTemplateXuanJi.DetailTemplateXuanJiList) {
                DetailTemplateXuanJi presenter = getPresenter(DetailTemplateXuanJi.class);
                if (null != presenter) {
                    return presenter.isEpisodeEnd();
                }
            }
            // 选期列表
            else if (o instanceof DetailTemplateXuanQi.DetailTemplateXuanQiList) {
                DetailTemplateXuanQi presenter = getPresenter(DetailTemplateXuanQi.class);
                if (null != presenter) {
                    return presenter.isEpisodeEnd();
                }
            }
            throw new Exception("not find");
        } catch (Exception e) {
            LogUtil.log("DetailGridView", "isPlayingEnd => " + e.getMessage());
            return false;
        }
    }
}
