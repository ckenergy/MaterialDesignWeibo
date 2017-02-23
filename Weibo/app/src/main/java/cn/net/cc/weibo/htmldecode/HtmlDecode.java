package cn.net.cc.weibo.htmldecode;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by chengkai on 2016/9/29.
 */
public class HtmlDecode {

    private static final String TAG = "HtmlDecode";

    private static final String baseUrl = "http://www.flvcd.com/parse.php?format=&kw=";

    public static void DecodeUrl(String urlStr, final decodeListener listener) {
        String encodeUrl = null;
        if (TextUtils.isEmpty(urlStr)) {
            Log.e(TAG,"url is empty");
            return;
        }
        try {
            encodeUrl = URLEncoder.encode(urlStr,"UTF-8");
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null) {
                        listener.fail(e.getMessage());
                    }
                }
            });

            return;
        }

        final String fullUrl = baseUrl+encodeUrl;
        Log.d(TAG,encodeUrl);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 从URL直接加载 HTML 文档
                Handler handler = new Handler(Looper.getMainLooper());
                try {
                    long before = System.currentTimeMillis();
                    Log.d(TAG,"time1:"+before);

                    Document doc = Jsoup.connect(fullUrl).get();
                    Elements elements = doc.getElementsByTag("input");
                    String sourceUrl = null;
                    Log.d(TAG,"size:"+elements.size());
                    for (Element link : elements) {
                        if (link.attr("name").equals("inf")) {
                            final String linkHref = link.attr("value");
                            if (TextUtils.isEmpty(linkHref)) {
                                break;
                            }
                            Log.d(TAG,"linkHref:"+linkHref);
//                            int end = linkHref.indexOf("|");
//                            Log.d(TAG,""+end);
                            final String[] strings = linkHref.split("\\|");
//                            sourceUrl = linkHref.substring(0,end);
                            sourceUrl = strings[0];
                            final String  videoUrl = sourceUrl;
                            Log.d(TAG,"url:"+sourceUrl);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (listener != null) {
                                        listener.succed(videoUrl);
                                    }
                                }
                            });
                            break;
                        }
                    }
                    if (TextUtils.isEmpty(sourceUrl)) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null) {
                                    listener.fail("decode fail");
                                }
                            }
                        });
                    }
                    long now = System.currentTimeMillis();
                    Log.d(TAG,"time2:"+now);
                    Log.d(TAG,"totle:"+(now-before));

                } catch (final IOException e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null) {
                                listener.fail(e.getMessage());
                            }
                        }
                    });

                }
            }
        }).start();

    }

    public interface decodeListener {
        public void succed(String urlStr) ;
        public void fail(String msg);
    }

    public static void dispatchListener(decodeListener listener, String msg) {
        if (listener != null) {

        }
    }

    public static void message(String msg) {
        Log.d(TAG,msg);
    }
}
