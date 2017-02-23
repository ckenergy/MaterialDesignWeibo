package cn.net.cc.weibo.imageLoad.picasso;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import cn.net.cc.weibo.R;
import cn.net.cc.weibo.imageLoad.BitmapRequest;
import cn.net.cc.weibo.imageLoad.ImageProvider;
import cn.net.cc.weibo.util.DisplayUtil;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by chengkai on 2016/7/13.
 */
public class PicassoProvider implements ImageProvider {

    private static final String TAG = "PicassoProvider";

//    ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);

    /**
     * 静态内部类式懒汉式单例
     */
    private static class LazyHolder{
        private static final PicassoProvider INSTANCE = new PicassoProvider();
    }

    private PicassoProvider(){}

    public static PicassoProvider getInstance() {
        return LazyHolder.INSTANCE;
    }

    @Override
    public void withView(Context context, String url, ImageView imageView) {
        withViewNeedFit(context,url,imageView);
    }

    public void withViewNeedFit(Context context, String url, ImageView imageView) {
        Picasso.with(context).load(url).fit().centerCrop().error(R.drawable.pic_error).into(imageView);
    }

    public void withViewFitSize(Context context, String url, ImageView imageView, int size) {
        Picasso.with(context).load(url).resize(size, size).centerCrop().into(imageView);
    }

    @Override
    public void getBitmap(final BitmapRequest request) {
        Log.d(TAG, "getBitmap");
        Observable.just(request.getImageUrl()).map(new Func1<String, Bitmap>() {
            @Override
            public Bitmap call(String s) {
                try {
                    if (request.isBigImage()){
                        int width = DisplayUtil.getWidthPix(request.getContext());
                        int height = DisplayUtil.getHeightPix(request.getContext());
                        return PicassoFactory.getPicasso(request.getContext(), request.getProgressListener()).load(s)
                                .memoryPolicy(MemoryPolicy.NO_CACHE).centerInside().resize(width, height).get();
                    }else {
                        return Picasso.with(request.getContext()).load(s).get();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) {
                        request.doBitmap(bitmap);
                    }
                });
        /*fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap = Picasso.with(request.getContext()).load(request.getImageUrl()).get();
                    request.doBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });*/
    }

    @Override
    public void clear(Context context, boolean cleanDisk) {

    }

}
