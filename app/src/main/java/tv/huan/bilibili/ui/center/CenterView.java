package tv.huan.bilibili.ui.center;

import java.util.ArrayList;

import lib.kalu.leanback.clazz.ClassBean;
import tv.huan.bilibili.base.BaseViewImpl;

public interface CenterView extends BaseViewImpl {

    void updateTab(ArrayList<ClassBean> data, int select);
}
