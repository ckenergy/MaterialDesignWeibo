package cn.net.cc.weibo.model;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.utils.LogUtil;

import cn.net.cc.weibo.ui.base.IListView;
import cn.net.cc.weibo.ui.friends.ILoadListModel;
import cn.net.cc.weibo.info.Constants;
import openapi.models.Comment;
import openapi.models.CommentList;
import openapi.models.ErrorInfo;

/**
 * Created by chengkai on 2016/8/25.
 */
public class CommentListModelImp implements ILoadListModel{

    private static final String TAG = "CommentListModelImp";

    /**获取微博条数*/
    private static final int COUNT = Constants.COUNT;

    private Context mContext;

    private IListView<Comment> mCommentListView;

    private INoIdListAPI commentAPI;

    public CommentListModelImp(Context context, IListView<Comment> commentListView, INoIdListAPI commentAPI) {
        this.mContext = context;
        this.mCommentListView = commentListView;
        this.commentAPI = commentAPI;

    }

    /**
     * count+1刷新新数据，删掉旧数据 {@link android.widget.BaseAdapter} addListNew(List) 中的算法
     * @param sinceId
     * @param maxId
     * @param type
     */
    @Override
    public void loadListNew(long sinceId, long maxId,  int type) {
        commentAPI.loadList(sinceId, maxId, COUNT+1, type, mWeiboListenerNew);
    }

    @Override
    public void loadListMore(long sinceId, long maxId,  int type) {
        commentAPI.loadList(sinceId, Math.max(0,maxId-1), COUNT, type, mWeiboListenerMore);
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
                if (response.startsWith("{\"comments\"")) {
                    // 调用 StatusList#parse 解析字符串成微博列表对象
                    CommentList commentList = CommentList.parse(response);
                    if (commentList != null) {
                        Log.d(TAG, "commentList"+commentList.comments);
                        if (commentList.total_number > 0 && commentList.comments !=null && commentList.comments.size() > 0) {
                            mCommentListView.setListNew(commentList.comments);
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
                if (response.startsWith("{\"comments\"")) {
                    CommentList commentList = CommentList.parse(response);
                    if (commentList != null) {
                        if (commentList.total_number > 0 && commentList.comments !=null && commentList.comments.size() > 0) {
                            mCommentListView.setListEnd(commentList.comments);
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
