package cn.net.cc.weibo.util;

import android.content.Context;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;

/**
 * Created by chengkai on 2016/7/29.
 */
public class AccessTokenManager {

    /** 当前 Token 信息 */
    private static Oauth2AccessToken mAccessToken;

    public static Oauth2AccessToken getAccessToken(Context context) {
        if (mAccessToken == null) {
            mAccessToken = AccessTokenKeeper.readAccessToken(context);
        }
        return mAccessToken;
    }

    public static void setAccessToken(Context context, Oauth2AccessToken accessToken) {
        AccessTokenKeeper.writeAccessToken(context, accessToken);
        mAccessToken = accessToken;
    }

}
