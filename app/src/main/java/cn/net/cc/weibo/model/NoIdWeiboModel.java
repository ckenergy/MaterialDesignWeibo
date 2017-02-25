package cn.net.cc.weibo.model;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.utils.LogUtil;

import cn.net.cc.weibo.base.IListView;
import cn.net.cc.weibo.friends.ILoadListModel;
import cn.net.cc.weibo.info.Constants;
import openapi.models.ErrorInfo;
import openapi.models.Status;
import openapi.models.StatusList;

/**
 * Created by chengkai on 2016/10/19.
 */
public class NoIdWeiboModel implements ILoadListModel {
    private static final String TAG = "AiteWeiboModel";

    /**获取评论，转发条数*/
    private static final int COUNT = Constants.COUNT;

    Context mContext;

    IListView<Status> mListView;
    INoIdListAPI iNoIdListAPI;

    public NoIdWeiboModel(Context context, IListView<Status> commentListView, INoIdListAPI iNoIdListAPI) {
        this.mContext = context;
        this.mListView = commentListView;
        this.iNoIdListAPI = iNoIdListAPI;
    }

    @Override
    public void loadListNew(long sinceId, long maxId,  int type) {
        iNoIdListAPI.loadList(sinceId, maxId, COUNT+1, type, mWeiboListenerNew);
    }

    @Override
    public void loadListMore(long sinceId, long maxId,  int type) {
        iNoIdListAPI.loadList(sinceId, Math.max(0,maxId-1), COUNT, type, mWeiboListenerNew);
    }

    /**
     * 微博 OpenAPI 回调接口。刷新
     */
    private RequestListener mWeiboListenerNew = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                Log.d(TAG, response);
//                FileUtil.saveFile(response, "comment");
                if (response.startsWith("{\"statuses\"")) {
                    // 调用 StatusList#parse 解析字符串成微博列表对象
                    StatusList statuses = StatusList.parse(response);
                    if (statuses != null) {
                        Log.d(TAG, "commentList"+statuses.statuses);
                        if (statuses.total_number > 0 && statuses.statuses !=null && statuses.statuses.size() > 0) {
                            mListView.setListNew(statuses.statuses);
                        }else {
                            showError("并没有最新微博！");
                        }
                    }else {
                        showError("获取更多失败！");
                    }
                }else {
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
                if (response.startsWith("{\"statuses\"")) {
                    // 调用 StatusList#parse 解析字符串成微博列表对象
                    StatusList statuses = StatusList.parse(response);
                    if (statuses != null && statuses.total_number > 0 && statuses.statuses !=null && statuses.statuses.size() > 0) {
                        mListView.setListEnd(statuses.statuses);
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

    @Override
    public void showError(String s) {
        mListView.setError(s);
    }

}
