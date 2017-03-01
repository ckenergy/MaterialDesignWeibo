package cn.net.cc.weibo.model;

import android.os.Handler;

import java.io.File;

import cn.net.cc.weibo.http.HttpManager;
import cn.net.cc.weibo.http.okhttp.OkHttpClientFactory;
import cn.net.cc.weibo.http.okhttp.ProgressListener;
import cn.net.cc.weibo.ui.imageview.IImageView;

/**
 * Created by chengkai on 2016/8/22.
 */
public class GifModel implements IGifModel{

    private Handler handler = new Handler();

    @Override
    public void getGif(String url, final IImageView imageView, ProgressListener progressListener) {
        String fileName = OkHttpClientFactory.getPicassoImageCacheFull(url);
        File file = new File(fileName);
        if (file.exists()){
            imageView.setGif(fileName);
            return;
        }
        HttpManager.downFile(url, new HttpManager.OnDownListener() {
            @Override
            public void onSuccess(final String file) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setGif(file);//uithred
                    }
                });
            }

            @Override
            public void onFail(final String erro) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setFail(erro);
                    }
                });
            }
        },progressListener);
    }



}
