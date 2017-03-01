package cn.net.cc.weibo.ui.hot;

import java.util.ArrayList;

import cn.net.cc.weibo.ui.base.BaseListFragment;
import cn.net.cc.weibo.ui.friends.ILoadListModel;
import cn.net.cc.weibo.ui.message.MsgCommentAdapter;
import cn.net.cc.weibo.model.CommentListModelImp;
import openapi.models.Comment;

/**
 * Created by chengkai on 2016/10/20.
 */
public class HotRepostWeeklyFragment extends BaseListFragment<Comment> {
    @Override
    protected void init() {
        ILoadListModel aiteCommentModel = new CommentListModelImp(getContext(), this, new HotRepostWeeklyRequest(getContext()));
        setAdapter(new MsgCommentAdapter(getContext(),new ArrayList<Comment>()));
        setModel(aiteCommentModel);
    }
}
