package tv.huan.bilibili.upgrade.net;

/**
 * description:retrofit基本的响应对象:  适用于result为Object
 * Created by CharlesYao on 2017/6/4.
 * email: ychdevelop@dingtalk.com
 */

public class ResponseBean<T> extends BaseResponse {

    public ResponseBean() {
    }

    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseBean{" +
                "data=" + data +
                '}';
    }
}
