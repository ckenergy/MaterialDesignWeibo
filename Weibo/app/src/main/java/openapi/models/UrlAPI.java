package openapi.models;

import android.content.Context;
import android.util.Log;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import openapi.AbsOpenAPI;

/**
 * Created by chengkai on 2016/10/8.
 */
public class UrlAPI extends AbsOpenAPI{

    private static final String TAG = "UrlAPI";

    private static final String SHORT_URL_LONG = API_SERVER + "/short_url/expand.json";

    private static final String SHORT_URL = "url_short";


    /**
     * 构造函数，使用各个 API 接口提供的服务前必须先获取 Token。
     *
     * @param context
     * @param appKey
     * @param accessToken 访问令牌
     */
    public UrlAPI(Context context, String appKey, Oauth2AccessToken accessToken) {
        super(context, appKey, accessToken);
    }

    public void short2long(List<String> urls, RequestListener listener) {
        if (urls.size() < 1) {
            Log.e(TAG, "url is empty");
            return;
        }
        StringBuilder builder = new StringBuilder();
        String encodeUrl1 = null;
        try {
            encodeUrl1 = URLEncoder.encode(urls.get(0),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (encodeUrl1 != null)
            builder.append(encodeUrl1);

        for (int i=1; i < urls.size(); i++) {

            String encodeUrl = null;
            try {
                encodeUrl = URLEncoder.encode(urls.get(i),"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (encodeUrl != null) {
                builder.append("&"+SHORT_URL+"=")
                        .append(encodeUrl);
            }

        }
        WeiboParameters params = new WeiboParameters(mAppKey);
        params.put(SHORT_URL,builder.toString());

        requestAsync(SHORT_URL_LONG, params, HTTPMETHOD_GET, listener);
    }

    public void short2long(String url, RequestListener listener) {
        WeiboParameters params = new WeiboParameters(mAppKey);
//        String encodeUrl = UrlUtil.encodeUrl(url);
        params.put(SHORT_URL,url);

        requestAsync(SHORT_URL_LONG, params, HTTPMETHOD_GET, listener);
    }

}
