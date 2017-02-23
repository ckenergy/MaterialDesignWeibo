package cn.net.cc.weibo.http.okhttp;

import java.io.File;
import java.io.IOException;

import cn.net.cc.weibo.util.MD5Sum;
import cn.net.cc.weibo.util.MemoryUtil;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by chengkai on 2016/8/18.
 */
public class OkHttpClientFactory {

//    static private OkHttpClient client;

    static public synchronized OkHttpClient.Builder getBuilder() {
        OkHttpClient.Builder builder;
        File cacheDir;
        String cacheFile = getPicasspImageCache();
        if (cacheFile == null) {
            builder = new OkHttpClient.Builder();
        }else {
            cacheDir = new File(cacheFile);
            builder = new OkHttpClient.Builder()
                    //Interceptor -> cache -> NetworkInterceptor
                    //.addNetworkInterceptor(getLogger())
                    .cache(new Cache(cacheDir, 60 * 1024 * 1024));
                    //.dispatcher(getDispatcher())
                    //.dns(HTTP_DNS)
        }
        return builder;
    }

    public static String getPicasspImageCache() {
        return MemoryUtil.getImageCache();
    }

    public static String getPicassoImageName(String url) {
        String file = MD5Sum.md5Sum(url);
        return file+".1";
    }

    public static String getPicassoImageCacheFull(String url) {
        return getPicasspImageCache()+File.separator+getPicassoImageName(url);
    }

    /**
     * Not singleton
     */
    public static OkHttpClient getProgressBarClient(final ProgressListener listener) {
        return getBuilder().addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder()
                        .body(new ProgressResponseBody(originalResponse.body(), listener))
                        .build();
            }
        }).build();
    }
}
