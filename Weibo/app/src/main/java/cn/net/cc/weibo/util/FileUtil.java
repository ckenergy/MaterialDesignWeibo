package cn.net.cc.weibo.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by chengkai on 2016/9/7.
 */
public class FileUtil {

    private static final String TAG = "FileUtil";

    public static void saveFile(String source,String fileName) {
        String filePath = null;
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) {
            filePath = Environment.getExternalStorageDirectory().toString() + File.separator + fileName;
        } else{
            Log.e(TAG,"sdCard is not exit");
            return;
        }

        try {
            File file = new File(filePath);
            if (!file.exists()) {
//                File dir = new File(file.getParent());
//                dir.mkdirs();
                file.createNewFile();
            }
            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(source.getBytes());
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
