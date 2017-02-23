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
import cn.net.cc.weibo.relationship.IUserListAPI;
import openapi.models.ErrorInfo;
import openapi.models.User;
import openapi.models.UserList;

/**
 * Created by chengkai on 2016/8/25.
 */
public class UserListModelImp implements ILoadListModel{

    private static final String TAG = "UserListModelImp";

    /**获取微博条数*/
    private static final int COUNT = 2*Constants.COUNT;

    Context mContext;

    IListView<User> mCommentListView;
    IUserListAPI userListAPI;

    int next_curcor = 0;
    int pre_curcor = 0;

    boolean noMore = false;

    public UserListModelImp(Context context, IListView<User> commentListView, IUserListAPI userListAPI) {
        this.mContext = context;
        this.mCommentListView = commentListView;
        this.userListAPI = userListAPI;

    }

    /**
     * count+1刷新新数据，删掉旧数据 {@link android.widget.BaseAdapter} addListNew(List) 中的算法
     * @param sinceId
     * @param maxId
     * @param type
     */
    @Override
    public void loadListNew(long sinceId, long maxId, int type) {
        int pre_curcor = this.pre_curcor;
        userListAPI.loadList(COUNT, pre_curcor, mWeiboListenerNew);
    }

    @Override
    public void loadListMore(long sinceId, long maxId, int type) {

        int next_curcor = this.next_curcor;

        userListAPI.loadList(COUNT, next_curcor, mWeiboListenerMore);
    }

    /**
     * 微博 OpenAPI 回调接口。刷新
     */
    private RequestListener mWeiboListenerNew = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                Log.d(TAG, response);
                if (response.startsWith("{\"users\"")) {
                    UserList userList = UserList.parse(response);
                    if (userList != null) {
                        if (userList.total_number > 0 && userList.users !=null && userList.users.size() > 0) {
                            mCommentListView.setListNew(userList.users);
                            Log.d(TAG, "pre_curcor:"+userList.previous_cursor);
                            pre_curcor = Integer.parseInt(userList.previous_cursor);
                            if (next_curcor <= 0) {
                                next_curcor = Integer.parseInt(userList.next_cursor);
                            }
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
                if (response.startsWith("{\"users\"")) {
                    UserList userList = UserList.parse(response);
                    if (userList != null) {
                        if (userList.total_number > 0 && userList.users !=null && userList.users.size() > 0) {
                            mCommentListView.setListEnd(userList.users);
                            Log.d(TAG, "next_curcor:"+userList.next_cursor);
                            int next_curcor1 = Integer.parseInt(userList.next_cursor);
                            next_curcor = next_curcor1 == 0 ? next_curcor+userList.users.size() : next_curcor1;
                            if (pre_curcor <= 0) {
                                pre_curcor = Integer.parseInt(userList.previous_cursor);
                            }
                        }else {
                            mCommentListView.setNoMore();
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

    @Override
    public void showError(String s) {
        mCommentListView.setError(s);
    }

}
