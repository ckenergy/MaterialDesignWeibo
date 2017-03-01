package cn.net.cc.weibo.ui.user;

import android.content.Context;
import android.util.Log;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.net.RequestListener;

import java.util.ArrayList;
import java.util.List;

import cn.net.cc.weibo.db.DatabaseManager;
import cn.net.cc.weibo.db.StatuDatabase;
import cn.net.cc.weibo.info.Constants;
import cn.net.cc.weibo.model.IStatusesAPI;
import cn.net.cc.weibo.util.AccessTokenManager;
import openapi.legacy.StatusesAPI;
import openapi.models.Status;
import openapi.models.User;

/**
 * Created by chengkai on 2016/9/20.
 */
public class UserWeiListModelRequest implements IStatusesAPI<Status> {

    private static final String TAG = "UserWeiListModelRequest";

    /**获取微博条数*/
    private static final int COUNT = Constants.COUNT;

    /** 用于获取微博信息流等操作的API */
    private StatusesAPI mStatusesAPI;

    long uid;

    public UserWeiListModelRequest(Context context, long uid) {
        this.uid = uid;
        // 获取当前已保存过的 Token
        Oauth2AccessToken mAccessToken = AccessTokenManager.getAccessToken(context);
        // 对statusAPI实例化
        mStatusesAPI = new StatusesAPI(context, Constants.APP_KEY, mAccessToken);
    }

    @Override
    public void loadList(long sinceId, long maxId, int count, int type, RequestListener listener) {
        mStatusesAPI.userTimeline(uid, sinceId, maxId, count, 1, false, type, false, listener);
    }

    @Override
    public void loadIds(long sinceId, long maxId, int type, RequestListener listener) {
        mStatusesAPI.userTimelineIds(uid, sinceId, maxId, COUNT, 1, false, type, listener);
    }

    @Override
    public List<Status> getList(long id) {
        String uids = String.valueOf(uid);
        return StatuDatabase.getInstance().getListWithUser(uids,id);//第一次id=0，获取的list为0，正好是我们需要的，因为第一次我希望从网络获取最新的
    }

    @Override
    public void saveList(List<Status> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        final List<Status> statusList = list;
        final ArrayList<Status> statusArrayList = new ArrayList<>();
        ArrayList<User> users = new ArrayList<>(statusList.size());
        List<Status> retweetStatus = new ArrayList<>();
        for (Status status:statusList) {//判断是否存在
            if (DatabaseManager.getEntity(status.id, DatabaseManager.STATUS_DB) == null) {
                statusArrayList.add(status);
                if (status.retweeted_status != null) {
                    status.retweetedID = status.retweeted_status.id;
                    if (DatabaseManager.getEntity(status.retweeted_status.id,DatabaseManager.STATUS_DB) == null) {
                        retweetStatus.add(status.retweeted_status);
                    }
                }
                Log.d(TAG,"user:"+status.user+",uid:"+status.uid);
                if (status.user != null) {
                    status.uid = status.user.id;
                    users.add(status.user);
                }

            }
        }
        DatabaseManager.saveList(users,DatabaseManager.USER_DB);
        if (retweetStatus.size() > 0) {
            saveList(retweetStatus);
        }
        if (statusArrayList.size() > 0) {
            DatabaseManager.saveList(statusArrayList,DatabaseManager.STATUS_DB);
        }
    }

}
