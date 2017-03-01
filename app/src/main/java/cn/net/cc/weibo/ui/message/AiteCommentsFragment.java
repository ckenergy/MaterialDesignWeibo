package cn.net.cc.weibo.ui.message;

import java.util.ArrayList;

import cn.net.cc.weibo.ui.base.BaseListFragment;
import cn.net.cc.weibo.ui.friends.ILoadListModel;
import cn.net.cc.weibo.model.CommentListModelImp;
import openapi.models.Comment;

/**
 * Created by chengkai on 2016/10/19.
 */
public class AiteCommentsFragment extends BaseListFragment<Comment> {

    @Override
    protected void init() {
        ILoadListModel aiteCommentModel = new CommentListModelImp(getContext(), this, new AiteCommentRequest(getContext()));
        setAdapter(new MsgCommentAdapter(getContext(),new ArrayList<Comment>()));
        setModel(aiteCommentModel);
    }
}
