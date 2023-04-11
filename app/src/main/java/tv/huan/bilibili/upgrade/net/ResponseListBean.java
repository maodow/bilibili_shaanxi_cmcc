package tv.huan.bilibili.upgrade.net;


import java.util.List;

/**
 * description: retrofit基本的响应对象:  适用于result为集合泛型为object时
 * Created by CharlesYao on 2017/6/5.
 * email: ychdevelop@dingtalk.com
 */

public class ResponseListBean<T> extends BaseResponse {
    List<T> data;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseListBean{" +
                "data=" + data +
                '}';
    }
}
