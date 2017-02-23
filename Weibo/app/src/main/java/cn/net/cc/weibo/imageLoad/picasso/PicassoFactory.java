package cn.net.cc.weibo.imageLoad.picasso;

import android.content.Context;

import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import cn.net.cc.weibo.http.okhttp.OkHttpClientFactory;
import cn.net.cc.weibo.http.okhttp.ProgressListener;
import okhttp3.OkHttpClient;

/**
 * Created by axing on 16/7/2.
 */
public class PicassoFactory {

    /**
     * Download Big Image only, Not singleton but shared cache
     */
    static public Picasso getPicasso(Context context, ProgressListener listener) {
        OkHttpClient client = OkHttpClientFactory.getProgressBarClient(listener);
        OkHttp3Downloader downloader = new OkHttp3Downloader(client);
        return new Picasso.Builder(context).downloader(downloader)
                .memoryCache(com.squareup.picasso.Cache.NONE)
                .build();
    }

}
