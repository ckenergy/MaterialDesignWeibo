package cn.net.cc.weibo.model;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import cn.net.cc.weibo.base.IListView;
import cn.net.cc.weibo.db.DatabaseManager;
import cn.net.cc.weibo.friends.ILoadListModel;
import cn.net.cc.weibo.info.Constants;
import openapi.legacy.StatusesAPI;
import openapi.models.ErrorInfo;
import openapi.models.Status;
import openapi.models.StatusIdList;
import openapi.models.StatusList;

/**
 * Created by chengkai on 2016/8/2.
 */
public class FriendsModelImpl implements ILoadListModel {

    private static final String TAG = "FriendsModelImpl";

    /**获取微博条数*/
    private static final int COUNT = Constants.COUNT;

    boolean isLoadingmore = false;
    String firstId;
    Context mContext;

    IStatusesAPI<Status> mStatusesAPI;
    IListView<Status> mFriendsView;

    public FriendsModelImpl(Context context, IListView<Status> friendsView, IStatusesAPI<Status> statusesAPI){
        this.mContext = context;
        this.mFriendsView = friendsView;
        this.mStatusesAPI = statusesAPI;
    }

    /**
     * 获取新微博 默认获取count条微博
     * @param sinceId 若指定此参数，则返回ID比since_id大的微博（即比since_id时间晚的微博），默认为0
     * @param maxId 若指定此参数，则返回ID小于或等于max_id的微博，默认为0
     * @param type 过滤类型ID，0：全部、1：原创、2：图片、3：视频、4：音乐，默认为0。
     *                    <li>{@link StatusesAPI#FEATURE_ALL}
     *                    <li>{@link StatusesAPI#FEATURE_ORIGINAL}
     *                    <li>{@link StatusesAPI#FEATURE_PICTURE}
     *                    <li>{@link StatusesAPI#FEATURE_VIDEO}
     *                    <li>{@link StatusesAPI#FEATURE_MUSICE}
     */
    @Override
    public void loadListNew(long sinceId, long maxId, int type) {
        mStatusesAPI.loadList(Math.max(sinceId-1,0), maxId, COUNT+1, type, new ListNewRequestListener(sinceId));
    }

    /**
     * 获取更多微博 默认获取25条微博
     * @param sinceId 若指定此参数，则返回ID比since_id大的微博（即比since_id时间晚的微博），默认为0
     * @param maxId 若指定此参数，则返回ID小于或等于max_id的微博，默认为0
     * @param type 过滤类型ID，0：全部、1：原创、2：图片、3：视频、4：音乐，默认为0。
     *                    <li>{@link StatusesAPI#FEATURE_ALL}
     *                    <li>{@link StatusesAPI#FEATURE_ORIGINAL}
     *                    <li>{@link StatusesAPI#FEATURE_PICTURE}
     *                    <li>{@link StatusesAPI#FEATURE_VIDEO}
     *                    <li>{@link StatusesAPI#FEATURE_MUSICE}
     */
    @Override
    public void loadListMore(long sinceId, long maxId, int type) {
        if (isLoadingmore) {
            return;
        }
        if (maxId == 0) {
            List<Status> statusList = mStatusesAPI.getList(maxId);
            if (statusList != null) {
                Log.d(TAG,"size:"+statusList.size());
            }
            if (statusList != null && statusList.size() > 0) {
                long id = Long.parseLong(statusList.get(0).id);
                mFriendsView.setListEnd(statusList);
            }else {
                isLoadingmore = true;
                mStatusesAPI.loadList(sinceId, Math.max(0 , maxId-1), COUNT, type, mWeiboListenerMore);
            }
            return;
        }
        mStatusesAPI.loadIds(sinceId, Math.max(0 , maxId-1), type, new WeiboIdListener());
    }

    /**
     * 微博 OpenAPI 回调接口。id
     */
//    mWeiboListenerId
    class WeiboIdListener implements RequestListener {

        WeiboIdListener() {
//            this.statusList = statusList;
        }

        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                Log.d(TAG, response);
                if (response.startsWith("{\"statuses\"")) {
                    // 调用 StatusList#parse 解析字符串成微博列表对象
                    StatusIdList statuses = StatusIdList.parse(response);
                    if (statuses != null && statuses.total_number > 0 && statuses.statuses !=null && statuses.statuses.size() > 0) {
                        List<Status> list = new ArrayList<>();
                        //删除顶置
                        if (firstId != null && statuses.statuses.get(0).equals(firstId)) {
                            statuses.statuses.remove(0);
                            if (statuses.statuses.size() <= 0) {
                                showError("获取更多失败！");
                                return;
                            }
                        }else if (firstId == null) {
                            firstId = statuses.statuses.get(0);
                        }
                        for(int i=0;i<statuses.statuses.size();i++) {
                            Status status =DatabaseManager.getEntity(statuses.statuses.get(i),DatabaseManager.STATUS_DB);
                            if (status != null) {
                                list.add(status);
                            }else {
                                break;
                            }
                        }
                        int length = list.size();
                        if (length > 0) {
                            mFriendsView.setListEnd(list);
                        }
                        if (length == statuses.statuses.size()) {  //没有获取到全部，说明有些信息没有，从这联网获取
                            return;
                        }
                        long maxId = Long.parseLong(statuses.statuses.get(length));
                        isLoadingmore = true;
                        mStatusesAPI.loadList(0, Math.max(0 , maxId-1), COUNT, 0, mWeiboListenerMore);
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
                isLoadingmore = false;
                if (response.startsWith("{\"statuses\"")) {
                    // 调用 StatusList#parse 解析字符串成微博列表对象
                    StatusList statuses = StatusList.parse(response);
                    if (statuses != null && statuses.total_number > 0 && statuses.statuses !=null && statuses.statuses.size() > 0) {
                        //删除顶置
                        if (firstId != null && statuses.statuses.get(0).id.equals(firstId)) {
                            statuses.statuses.remove(0);
                            if (statuses.statuses.size() <= 0) {
                                showError("获取更多失败！");
                                return;
                            }
                        }else if (firstId == null) {
                            firstId = statuses.statuses.get(0).id;
                        }
                        saveStatusList(statuses.statuses);
                        mFriendsView.setListEnd(statuses.statuses);
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
    public void showError(String msg) {
        mFriendsView.setError(msg);
    }

    private void saveStatusList(List<Status> list) {
        mStatusesAPI.saveList(list);
    }

    /**
     * 微博 OpenAPI 回调接口。刷新
     */
    private class ListNewRequestListener implements RequestListener {

        long sinceId;

        ListNewRequestListener(long sinceId) {
            this.sinceId = sinceId;
        }

        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                Log.d(TAG, response);
                if (response.startsWith("{\"statuses\"")) {
                    // 调用 StatusList#parse 解析字符串成微博列表对象
                    StatusList statuses = StatusList.parse(response);
                    if (statuses != null && statuses.total_number > 0 && statuses.statuses != null && statuses.statuses.size() > 1) {
                        int last = statuses.statuses.size() - 1;
                        if (sinceId == Long.parseLong(statuses.statuses.get(last).id)) {
                            statuses.statuses.remove(last);
                        }
                        saveStatusList(statuses.statuses);
                        mFriendsView.setListNew(statuses.statuses);
                    }else {
                        showError("并没有最新微博");
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
}
