package cn.net.cc.weibo.model;

import android.content.Context;
import android.util.Log;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

import java.util.List;

import cn.net.cc.weibo.info.Constants;
import cn.net.cc.weibo.util.AccessTokenManager;
import openapi.models.UrlAPI;
import openapi.models.UrlList;

/**
 * Created by chengkai on 2016/10/8.
 */
public class UrlModel {

    private static final String TAG = "UrlModel";

    UrlAPI urlAPI;

    public UrlModel(Context context) {
        Oauth2AccessToken mAccessToken = AccessTokenManager.getAccessToken(context);
        urlAPI = new UrlAPI(context, Constants.APP_KEY, mAccessToken);
    }

    public void short2long(String url, Listener listener) {
        urlAPI.short2long(url, new UrlRequestListener(listener));
    }

    public void short2long(List<String> urls, Listener listener) {
        urlAPI.short2long(urls, new UrlRequestListener(listener));
    }

    class UrlRequestListener implements RequestListener {

        Listener listener;

        UrlRequestListener(Listener listener) {
            this.listener = listener;
        }

        @Override
        public void onComplete(String s) {
            Log.d(TAG, "s");
            UrlList urlList = UrlList.parse(s);
            if (urlList.urls == null || urlList.urls.size() ==0 ) {
                listener.error("解析url失败");
            }
            listener.succed(urlList);
        }

        @Override
        public void onWeiboException(WeiboException e) {
            listener.error(e.getMessage());
        }
    };

    public interface Listener {
        public void succed(UrlList urlList) ;
        public void error(String error) ;
    }

}
