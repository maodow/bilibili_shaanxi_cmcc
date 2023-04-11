package tv.huan.bilibili.http;

import lib.kalu.frame.mvp.http.BaseClient;
import okhttp3.Interceptor;
import tv.huan.bilibili.BuildConfig;

/**
 * description: Http
 * create by kalu on 2018/10/23 10:57
 */
public final class HttpClient extends BaseClient {

    private HttpClient() {
        super();
    }

    @Override
    protected int initReadTimeout() {
        return 10;
    }

    @Override
    protected int initWriteTimeout() {
        return 10;
    }

    @Override
    protected Interceptor addInterceptor() {
        return new HttpInterceptor();
    }

    @Override
    protected String initApi() {
        return BuildConfig.HUAN_API;
    }

    public static HttpClient getHttpClient() {
        return HttpHolder.single;
    }

    public HttpApi getHttpApi() {
        return getApiService(HttpApi.class);
    }

    private final static class HttpHolder {
        private final static HttpClient single = new HttpClient();
    }
}