package cn.net.cc.weibo.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import openapi.models.Status;

/**
 * Created by chengkai on 2016/8/27.
 */
public class UrlUtil {

    public static String userBg2whole(String url) {
        String matchSize = "/crop[0-9\\.]+/";
        String replace = "/crop.0.0.0.0/";
        return url.replaceFirst(matchSize,replace);
    }

    public static ArrayList<String> UrlBean2String(ArrayList<Status.UrlBean> urlbeans) {
        if (urlbeans != null && urlbeans.size() >0) {
            ArrayList<String> urls= new ArrayList<>(urlbeans.size());
            for (Status.UrlBean urlBean : urlbeans) {
                urls.add(replace2middle(urlBean.thumbnail_pic));
            }
            return urls;
        }
        return null;
    }

    private static void changeUrlList(List<Status.UrlBean> urls) {
        if (urls == null) {
            return;
        }
        int length = urls.size();
        for (int i = 0;i<length;i++) {
            Status.UrlBean urlBean = urls.get(i);
            replace2middle(urlBean.thumbnail_pic);
        }
    }

    private static String replace2middle(String url) {
        return url.replace("thumbnail","bmiddle");
    }

    public static String encodeUrl (String url) {
        try {
            return URLEncoder.encode(url,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

}
