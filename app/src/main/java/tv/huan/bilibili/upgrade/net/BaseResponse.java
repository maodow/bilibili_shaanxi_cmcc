package tv.huan.bilibili.upgrade.net;

/**
 * description:retrofit的base响应对象
 * Created by CharlesYao on 2017/6/5.
 * email: ychdevelop@dingtalk.com
 */

public class BaseResponse {

    private int code;
    private String status;
    private String description;

    public BaseResponse() {

    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "code=" + code +
                ", status='" + status + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

}
