package cn.net.cc.weibo.model;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.utils.LogUtil;

import cn.net.cc.weibo.ui.base.IListView;
import cn.net.cc.weibo.ui.friends.ILoadListModel;
import cn.net.cc.weibo.info.Constants;
import cn.net.cc.weibo.util.AccessTokenManager;
import openapi.RetweetList;
import openapi.legacy.StatusesAPI;
import openapi.models.ErrorInfo;
import openapi.models.Status;

/**
 * Created by chengkai on 2016/8/26.
 */
public class RetweetListModelImp implements ILoadListModel{

    private static final String TAG = "RetweetListModel";

    /**获取评论，转发条数*/
    private static final int COUNT = Constants.COUNT;

    /** 当前 Token 信息 */
    private Oauth2AccessToken mAccessToken;
    /** 用于获取微博信息流等操作的API */
    private StatusesAPI mStatusesAPI;

    private Context mContext;

    private IListView<Status> mRetweetListView;
    private long id;

    public RetweetListModelImp(Context context, IListView<Status> retweetListView, long id){
        this.mContext = context;
        this.mRetweetListView = retweetListView;
        this.id = id;
        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenManager.getAccessToken(context);
        // 对statusAPI实例化
        mStatusesAPI = new StatusesAPI(context, Constants.APP_KEY, mAccessToken);
    }

    @Override
    public void loadListNew(long sinceId, long maxId, int type) {
        mStatusesAPI.repostTimeline(id,sinceId, maxId, COUNT+1, 1, type, mWeiboListenerNew);
    }

    /**
     * 获取指定微博的转发微博列表
     *
     * id            需要查询的微博ID。
     * @param sinceId      若指定此参数，则返回ID比since_id大的微博（即比since_id时间晚的微博），默认为0
     * @param maxId        若指定此参数，则返回ID小于或等于max_id的微博，默认为0
     * count               单页返回的记录条数，默认为50
     * page                返回结果的页码，默认为1
     * @param type    作者筛选类型，0：全部、1：我关注的人、2：陌生人，默认为0。可为以下几种：
     *                      <li> {@link StatusesAPI#AUTHOR_FILTER_ALL}
     *                      <li> {@link StatusesAPI#AUTHOR_FILTER_ATTENTIONS}
     *                      <li> {@link StatusesAPI#AUTHOR_FILTER_STRANGER}
     */
    @Override
    public void loadListMore(long sinceId, long maxId,  int type) {
        mStatusesAPI.repostTimeline(id,sinceId, Math.max(maxId-1,0), COUNT, 1, type, mWeiboListenerMore);
    }

    /**
     * 微博 OpenAPI 回调接口。刷新
     */
    private RequestListener mWeiboListenerNew = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                Log.d(TAG, response);
                if (response.startsWith("{\"reposts\"")) {
                    // 调用 StatusList#parse 解析字符串成微博列表对象
                    RetweetList retweetList = RetweetList.parse(response);
                    if (retweetList != null) {
                        if (retweetList.total_number > 0 && retweetList.reposts !=null && retweetList.reposts.size() > 0) {
                            mRetweetListView.setListNew(retweetList.reposts);
                        }else {
                            showError("并没有最新微博！");
                        }
                    }else{
                        showError("获取更多失败！");
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

    /**
     * 微博 OpenAPI 回调接口。更多
     */
    private RequestListener mWeiboListenerMore = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                Log.d(TAG, response);
                if (response.startsWith("{\"reposts\"")) {
                    // 调用 StatusList#parse 解析字符串成微博列表对象
                    RetweetList retweetList = RetweetList.parse(response);
                    if (retweetList != null) {
                        if (retweetList.total_number > 0 && retweetList.reposts !=null && retweetList.reposts.size() > 0) {
                            mRetweetListView.setListEnd(retweetList.reposts);
                        }else {
                            mRetweetListView.setNoMore();
                        }
                    }else{
                        showError("获取更多失败！");
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


    public void showError(String msg) {
        mRetweetListView.setError(msg);
    }
}
