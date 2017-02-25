package cn.net.cc.weibo.hot;

import java.util.ArrayList;

import cn.net.cc.weibo.base.BaseListFragment;
import cn.net.cc.weibo.friends.ILoadListModel;
import cn.net.cc.weibo.message.MsgCommentAdapter;
import cn.net.cc.weibo.model.CommentListModelImp;
import openapi.models.Comment;

/**
 * Created by chengkai on 2016/10/20.
 */
public class HotRepostDailyFragment extends BaseListFragment<Comment> {
    @Override
    protected void init() {
        ILoadListModel aiteCommentModel = new CommentListModelImp(getContext(), this, new HotRepostDailyRequest(getContext()));
        setAdapter(new MsgCommentAdapter(getContext(),new ArrayList<Comment>()));
        setModel(aiteCommentModel);
    }
}
