package cn.net.cc.weibo.ui.weiboItem;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;

import cn.net.cc.weibo.info.Constants;
import cn.net.cc.weibo.util.AccessTokenManager;
import openapi.legacy.StatusesAPI;
import openapi.models.ErrorInfo;

/**
 * Created by chengkai on 2016/8/25.
 */
public class WeiboItemDetailModel {

    private static final String TAG = "WeiboItemDetailModel";

    /**转发字段*/
    private static final String REPOSTS = "reposts";
    /**评论字段*/
    private static final String COMMENTS = "comments";
    /**点赞字段*/
    private static final String ATTITUDES = "attitudes";

    /** 当前 Token 信息 */
    private Oauth2AccessToken mAccessToken;
    /** 用于获取微博信息流等操作的API */
    private StatusesAPI mStatusesAPI;

    Context mContext;

    IWeiboItemDetailView mWeiboItemDetailView;

    public WeiboItemDetailModel(Context context, IWeiboItemDetailView weiboItemDetailView){
        this.mContext = context;
        this.mWeiboItemDetailView = weiboItemDetailView;
        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenManager.getAccessToken(context);
        // 对statusAPI实例化
        mStatusesAPI = new StatusesAPI(context, Constants.APP_KEY, mAccessToken);

    }

    public void loadComment(String[] ids) {
        mStatusesAPI.count(ids, mWeiboListenerComment);
    }

    /**
     * 微博 OpenAPI 回调接口。更多
     */
    private RequestListener mWeiboListenerComment = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                Log.d(TAG, response);
                if (response.startsWith("[{\"id\"")) {
                    // 调用 StatusList#parse 解析字符串成微博列表对象
                    try {
                        JSONArray jsonArray = new JSONArray(response);//new JSONObject(response).getJSONArray()
                        if (jsonArray.length()>0&& jsonArray.getJSONObject(0) != null) {
                            int reposts = jsonArray.getJSONObject(0).optInt(REPOSTS);
                            int comments = jsonArray.getJSONObject(0).optInt(COMMENTS);
                            int attitudes = jsonArray.getJSONObject(0).optInt(ATTITUDES);
                            mWeiboItemDetailView.setComment(reposts,comments,attitudes);
                        }else {
                            showError(response);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        showError(response);
                    }
                } else {
                    showError(response);
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            LogUtil.e(TAG, e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            showError(info.toString());
        }
    };

    private void showError(String response) {
        mWeiboItemDetailView.showError(response);
    }
}
