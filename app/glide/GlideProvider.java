package cn.net.cc.weibo.imageLoad.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.UiThread;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;

import java.io.File;

import cn.net.cc.weibo.imageLoad.BitmapRequest;
import cn.net.cc.weibo.imageLoad.ImageProvider;
import cn.net.cc.weibo.http.okhttp.OkHttpClientFactory;
import cn.net.cc.weibo.http.okhttp.ProgressListener;
import cn.net.cc.weibo.util.DisplayUtil;

/**
 * Created by chengkai on 2016/8/13.
 */
public class GlideProvider implements ImageProvider {

    private static final String TAG = "GlideProvider";

    /**
     * 静态内部类式懒汉式单例
     */
    private static class LazyHolder{
        private static final GlideProvider INSTANCE = new GlideProvider();
    }

    private GlideProvider(){}

    public static GlideProvider getInstance() {
        return LazyHolder.INSTANCE;
    }

    @Override
    public void withView(Context context, final String url, ImageView imageView) {
        Glide.with(context).using(new ProgressModelLoader(((ProgressListener)null))).load(url).asBitmap().centerCrop().into(imageView);
    }

    @Override
    public void getBitmap(final BitmapRequest request) {

        SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>(DisplayUtil.getWidthPix(request.getContext())
                , DisplayUtil.getHeightPix(request.getContext())) { //图片除以2倍数，知道大于width，height当中任何一个最小数
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                //这里我们拿到回掉回来的bitmap，可以加载到我们想使用到的地方
                request.doBitmap(bitmap);
            }
        };

        if (request.isBigImage()) {
            if (request.getImageUrl().endsWith("gif")) {
                if (!new File(OkHttpClientFactory.getPicassoImageCacheFull(request.getImageUrl())).exists()) {
                    Log.d(TAG, "down");
                    Glide.with(request.getContext()).using(new ProgressModelLoader(request.getProgressListener())).load(request.getImageUrl())
                            .downloadOnly(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL);
                }else {
                    request.doBitmap(null);
                }

            }else {
                Glide.with(request.getContext()).using(new ProgressModelLoader(request.getProgressListener())).load(request.getImageUrl())
                        .asBitmap().into(target);
            }
        }else {
            Glide.with(request.getContext()).load(request.getImageUrl()).asBitmap().into(target);
        }

    }

    @Override
    @UiThread
    public void clear(final Context context, boolean cleanDisk) {
        Glide.get(context).clearMemory();
        if (!cleanDisk) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(context).clearDiskCache();
            }
        }).start();
    }

}
