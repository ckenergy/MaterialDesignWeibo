package cn.net.cc.weibo.imageLoad;

import android.content.Context;
import android.widget.ImageView;

import cn.net.cc.weibo.http.okhttp.ProgressListener;
import cn.net.cc.weibo.imageLoad.picasso.PicassoProvider;
import cn.net.cc.weibo.ui.imageview.IImageView;
import cn.net.cc.weibo.model.IGifModel;

/**
 *
 */
public class ImageLoader {

    public static ImageProvider provider = PicassoProvider.getInstance();//GlideProvider.getInstance();

    public static void with(Context context, String url, ImageView imageView) {
        provider.withView(context, url, imageView);
    }

    public static void getGif(IGifModel model, String url, IImageView imageView, ProgressListener progressListener) {
        model.getGif(url,imageView,progressListener);
    }

    /*public static void withNeedFit(Context context, String url, ImageView imageView) {
        PicassoProvider.getInstance().withViewNeedFit(context, url, imageView);
    }

    public static void withFitSize(Context context, String url, ImageView imageView, int size) {
        PicassoProvider.getInstance().withViewFitSize(context, url, imageView, size);
    }*/

    public static void getBitmap(final BitmapRequest request) {
        provider.getBitmap(request);
    }

}
