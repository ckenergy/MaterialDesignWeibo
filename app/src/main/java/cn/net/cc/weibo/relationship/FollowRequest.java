package cn.net.cc.weibo.relationship;

import android.content.Context;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.net.RequestListener;

import cn.net.cc.weibo.info.Constants;
import cn.net.cc.weibo.util.AccessTokenManager;
import openapi.legacy.FriendshipsAPI;

/**
 * Created by chengkai on 2016/10/29.
 */
public class FollowRequest implements IUserListAPI {

    FriendshipsAPI friendshipsAPI;
    long uid;

    public FollowRequest(Context context, long uid) {
        this.uid = uid;
        // 获取当前已保存过的 Token
        Oauth2AccessToken mAccessToken = AccessTokenManager.getAccessToken(context);
        friendshipsAPI = new FriendshipsAPI(context, Constants.APP_KEY, mAccessToken);
    }

    @Override
    public void loadList(int count, int cursor, RequestListener listener) {
        friendshipsAPI.followers(uid, count, cursor, false, listener);
    }
}
