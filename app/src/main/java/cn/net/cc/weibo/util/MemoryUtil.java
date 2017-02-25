package cn.net.cc.weibo.util;

import android.os.Environment;

import java.io.File;

/**
 * Created by chengkai on 2016/8/12.
 */
public class MemoryUtil {

    static final String IMAGE_CACHE = "weiwei"+ File.separator+"image";

    static final String FULL_IMAGE_CACHE = Environment.getExternalStorageDirectory()+File.separator+IMAGE_CACHE;

    /**
     * end with no {@link File#separator}
     * @return
     */
    public static synchronized String getImageCache() {
        if (isSdCardExist()) {
            File file = new File(FULL_IMAGE_CACHE);
            if (file.exists()) {
                if (!file.isDirectory() && !file.delete()) {
                    return null;
                }
            }
            if (!file.exists() && !file.mkdirs()) {
                return null;
            }
            return FULL_IMAGE_CACHE;
        }
        return null;
    }

    public static String url2FileName(String url) {
        return MD5Sum.md5Sum(url);
    }

    public static boolean isSdCardExist() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }



}
