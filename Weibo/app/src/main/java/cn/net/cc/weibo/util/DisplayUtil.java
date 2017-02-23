package cn.net.cc.weibo.util;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by chengkai on 2016/7/30.
 */
public class DisplayUtil {

    public static DisplayMetrics getDisplayMetrics(Context context) {
        // 通过Resources获取
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm;
    }

    public static int getWidthPix(Context context) {
        // 通过Resources获取
        DisplayMetrics dm = getDisplayMetrics(context);
        return dm.widthPixels;
    }

    public static int getHeightPix(Context context) {
        // 通过Resources获取
        DisplayMetrics dm = getDisplayMetrics(context);
        return dm.heightPixels;
    }

}
