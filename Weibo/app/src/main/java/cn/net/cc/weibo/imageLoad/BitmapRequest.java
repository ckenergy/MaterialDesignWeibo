package cn.net.cc.weibo.imageLoad;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import cn.net.cc.weibo.http.okhttp.ProgressListener;
import cn.net.cc.weibo.model.IGifModel;

/**
 * Created by chengkai on 2016/7/13.
 */
public class BitmapRequest {

    private Context context;

    private String imageUrl;

    private ImageView imageView;

    private Listener listener;

    private ProgressListener progressListener;

    private boolean isBigImage = false;

    private boolean isGif = false;

    private IGifModel model;

    public interface Listener {
        void loadBitmap(Bitmap bitmap);
    }

    public BitmapRequest(Context context, String imageUrl, ImageView imageView) {
        this.context = context;
        this.imageUrl = imageUrl;
        this.imageView = imageView;
    }

    public BitmapRequest(Context context, String imageUrl, Listener listener) {
        this.context = context;
        this.imageUrl = imageUrl;
        this.listener = listener;
    }

    public void doWith(ImageProvider provider) {
    }

    public void doBitmap(final Bitmap bitmap) {
        if(Looper.myLooper() == Looper.getMainLooper()) {
            listener.loadBitmap(bitmap);
        }else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    listener.loadBitmap(bitmap);
                }
            });
        }
    }

    public void doGif(final Bitmap bitmap) {

    }

    public ProgressListener getProgressListener() {
        return progressListener;
    }

    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public IGifModel getModel() {
        return model;
    }

    public void setModel(IGifModel model) {
        this.model = model;
    }

    public boolean isGif() {
        return isGif;
    }

    public void setGif(boolean gif) {
        isGif = gif;
    }

    public boolean isBigImage() {
        return isBigImage;
    }

    public void setBigImage(boolean bigImage) {
        isBigImage = bigImage;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

}
