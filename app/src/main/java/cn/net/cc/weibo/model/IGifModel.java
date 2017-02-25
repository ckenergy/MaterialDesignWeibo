package cn.net.cc.weibo.model;

import cn.net.cc.weibo.http.okhttp.ProgressListener;
import cn.net.cc.weibo.imageview.IImageView;

/**
 * Created by chengkai on 2016/8/22.
 */
public interface IGifModel {
    public void getGif(String url, IImageView imageView, ProgressListener progressListener);
}
