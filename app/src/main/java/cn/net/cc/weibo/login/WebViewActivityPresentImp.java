package cn.net.cc.weibo.login;

import android.content.Context;
import android.util.Log;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import cn.net.cc.weibo.util.AccessTokenManager;

/**
 * Created by wenmingvs on 16/5/18.
 */
public class WebViewActivityPresentImp {

    private static final String TAG = "WebViewPresentImp" ;

    private WebViewActivityView mWebViewActivityView;
//    private TokenListModel mTokenListModel;

    public WebViewActivityPresentImp(WebViewActivityView webViewActivityView) {
        this.mWebViewActivityView = webViewActivityView;
//        mTokenListModel = new TokenListModelImp();
    }

    public void handleRedirectedUrl(Context context, String url) {
        if (!url.contains("error")) {
            int tokenIndex = url.indexOf("access_token=");
            int expiresIndex = url.indexOf("expires_in=");
            int refresh_token_Index = url.indexOf("refresh_token=");
            int uid_Index = url.indexOf("uid=");

            String token = url.substring(tokenIndex + 13, url.indexOf("&", tokenIndex));
            String expiresIn = url.substring(expiresIndex + 11, url.indexOf("&", expiresIndex));
            String refresh_token = url.substring(refresh_token_Index + 14, url.indexOf("&", refresh_token_Index));
            String uid = new String();
            if (url.contains("scope=")) {
                uid = url.substring(uid_Index + 4, url.indexOf("&", uid_Index));
            } else {
                uid = url.substring(uid_Index + 4);
            }

            Log.d(TAG, "uid= " + uid);
            updateAccessToken(context, token, expiresIn, refresh_token, uid);
            mWebViewActivityView.startMainActivity();
        }
    }

    public void updateAccessToken(Context context, String token, String expiresIn, String refresh_token, String uid) {
        Oauth2AccessToken mAccessToken = new Oauth2AccessToken();
        mAccessToken.setToken(token);
        mAccessToken.setExpiresIn(expiresIn);
        mAccessToken.setRefreshToken(refresh_token);
        mAccessToken.setUid(uid);
        AccessTokenManager.setAccessToken(context,mAccessToken);
    }

}
