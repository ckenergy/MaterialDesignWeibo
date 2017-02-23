package cn.net.cc.weibo.util;

import java.util.Locale;

/**
 * Created by chengkai on 2016/11/1.
 */
public class NumberChangeUtil {

    public static String sizeChange(int size) {
        float result = size / 10000f;
        if(result < 1 ) {
            return String.valueOf(size);
        }
        if (result > 10) {
            return String.format(Locale.CHINA,"%d万", (int)result);
        }
        return String.format(Locale.CHINA,"%.1f万", result);
    }

}
