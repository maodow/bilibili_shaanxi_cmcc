package tv.huan.bilibili.ui.welcome;

import androidx.annotation.NonNull;
import tv.huan.bilibili.base.BaseViewImpl;
import tv.huan.bilibili.bean.ServerSettingData.UpgradeBean;

public interface WelcomeView extends BaseViewImpl {

    void updateBackground(@NonNull String url);

    void next();

    void next(@NonNull UpgradeBean upgradeBean, @NonNull String data, @NonNull int select, @NonNull int type, @NonNull String cid, @NonNull int classId, @NonNull String secondTag);
}
