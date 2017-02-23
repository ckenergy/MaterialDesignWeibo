package cn.net.cc.weibo.imageLoad;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by chengkai on 2016/7/13.
 */
public interface ImageProvider {

    public void withView(Context context, String url, ImageView imageView) ;

    public void getBitmap(BitmapRequest request);

    public void clear(Context context,boolean cleanDisk);

}
