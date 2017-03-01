package cn.net.cc.weibo.ui.hot;

import android.content.Context;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.net.RequestListener;

import cn.net.cc.weibo.info.Constants;
import cn.net.cc.weibo.model.INoIdListAPI;
import cn.net.cc.weibo.util.AccessTokenManager;
import openapi.legacy.StatusesAPI;

/**
 * Created by chengkai on 2016/10/24.
 */
public class HotRepostWeeklyRequest implements INoIdListAPI{

    private StatusesAPI statusesAPI;

    public HotRepostWeeklyRequest(Context context) {
        // 获取当前已保存过的 Token
        Oauth2AccessToken mAccessToken = AccessTokenManager.getAccessToken(context);
        // 对statusAPI实例化
        statusesAPI = new StatusesAPI(context, Constants.APP_KEY, mAccessToken);
    }

    @Override
    public void loadList(long sinceId, long maxId, int count, int type, RequestListener listener) {
        statusesAPI.hotRepostWeekly(20, false, listener);
    }
}
