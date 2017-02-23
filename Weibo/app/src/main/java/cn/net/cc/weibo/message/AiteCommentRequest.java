package cn.net.cc.weibo.message;

import android.content.Context;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.net.RequestListener;

import cn.net.cc.weibo.info.Constants;
import cn.net.cc.weibo.model.INoIdListAPI;
import cn.net.cc.weibo.util.AccessTokenManager;
import openapi.CommentsAPI;

/**
 * Created by chengkai on 2016/10/20.
 */
public class AiteCommentRequest implements INoIdListAPI {

    CommentsAPI commentsAPI;

    public AiteCommentRequest(Context context) {
        Oauth2AccessToken mAccessToken = AccessTokenManager.getAccessToken(context);
        // 对statusAPI实例化
        commentsAPI = new CommentsAPI(context, Constants.APP_KEY, mAccessToken);
    }

    @Override
    public void loadList(long sinceId, long maxId, int count, int type, RequestListener listener) {
        commentsAPI.mentions(sinceId, maxId, count, 1, type,0, listener);
    }
}
