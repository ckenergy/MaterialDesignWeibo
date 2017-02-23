package cn.net.cc.weibo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by chengkai on 2016/8/4.
 */
public class DateUtil {

    static Calendar calendar = Calendar.getInstance();
    static int dayOfYear;
    static SimpleDateFormat sdf = new SimpleDateFormat( "E MMM dd HH:mm:ss Z yyyy" , Locale.ENGLISH);
    static SimpleDateFormat sdfMonth = new SimpleDateFormat( "MM月dd HH:mm", Locale.getDefault());
    static SimpleDateFormat sdfHour = new SimpleDateFormat( "HH:mm", Locale.getDefault());

    static {
        calendar.setTime(new Date());
        dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
    }

    public static String dateFormat(String created_at) {
        try {
            Date date = sdf.parse(created_at);
            calendar.setTime(date);
            if (dayOfYear != calendar.get(Calendar.DAY_OF_YEAR)) {
                return sdfMonth.format(date);
            }else {
                return sdfHour.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
