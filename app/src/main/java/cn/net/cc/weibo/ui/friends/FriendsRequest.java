package cn.net.cc.weibo.ui.friends;

import android.content.Context;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.net.RequestListener;

import java.util.ArrayList;
import java.util.List;

import cn.net.cc.weibo.db.DatabaseManager;
import cn.net.cc.weibo.info.Constants;
import cn.net.cc.weibo.model.IStatusesAPI;
import cn.net.cc.weibo.util.AccessTokenManager;
import openapi.legacy.StatusesAPI;
import openapi.models.Status;
import openapi.models.User;

/**
 * Created by chengkai on 2016/9/20.
 */
public class FriendsRequest implements IStatusesAPI<Status> {

    /**获取微博条数*/
    private static final int COUNT = Constants.COUNT;

    /** 用于获取微博信息流等操作的API */
    private StatusesAPI mStatusesAPI;

    public FriendsRequest(Context context) {
        // 获取当前已保存过的 Token
        Oauth2AccessToken mAccessToken = AccessTokenManager.getAccessToken(context);
        // 对statusAPI实例化
        mStatusesAPI = new StatusesAPI(context, Constants.APP_KEY, mAccessToken);
    }

    @Override
    public void loadList(long sinceId, long maxId, int count, int type, RequestListener listener) {
        mStatusesAPI.friendsTimeline(sinceId, maxId, count, 1, false, type, false, listener);
    }

    @Override
    public void loadIds(long sinceId, long maxId, int type, RequestListener listener) {
        mStatusesAPI.friendsTimelineIds(sinceId, maxId, COUNT, 1, false, type, listener);
    }

    @Override
    public List<Status> getList(long id) {
        return DatabaseManager.getList(id, DatabaseManager.STATUS_DB);
    }

    @Override
    public void saveList(List<Status> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        final List<Status> statusList = list;
        final ArrayList<Status> retweetStatus = new ArrayList<>();
        ArrayList<User> users = new ArrayList<>(statusList.size());
        for (Status status:statusList) {
            status.weibo_type = Status.FOLLOW;
            if (status.retweeted_status != null) {
//                Log.d(TAG,"re type:"+status.retweeted_status.weibo_type);
                status.retweetedID = status.retweeted_status.id;
                if (DatabaseManager.getEntity(status.retweeted_status.id,DatabaseManager.STATUS_DB) == null) {
                    retweetStatus.add(status.retweeted_status);
                }
            }
            if (status.user != null) {
                status.uid = status.user.id;
                users.add(status.user);
            }
        }
        DatabaseManager.saveList(users, DatabaseManager.USER_DB);
        if (retweetStatus.size() > 0) {
            saveList(retweetStatus);
        }
        DatabaseManager.saveList(statusList, DatabaseManager.STATUS_DB);
    }
}
