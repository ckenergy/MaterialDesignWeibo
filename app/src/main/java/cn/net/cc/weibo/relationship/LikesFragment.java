package cn.net.cc.weibo.relationship;

import android.os.Bundle;

import java.util.ArrayList;

import cn.net.cc.weibo.base.BaseListFragment;
import cn.net.cc.weibo.friends.ILoadListModel;
import cn.net.cc.weibo.model.UserListModelImp;
import openapi.models.User;

public class LikesFragment extends BaseListFragment<User> {

    public static final String PARAM_UID = "uid";

    public static LikesFragment newInstance(long uid) {
        LikesFragment followsFragment = new LikesFragment();
        Bundle data = new Bundle();
        data.putLong(PARAM_UID, uid);
        followsFragment.setArguments(data);
        return followsFragment;
    }

    @Override
    protected void init() {
        if (getArguments() == null) {
            return;
        }
        long uid = getArguments().getLong(PARAM_UID);
        ILoadListModel listModel = new UserListModelImp(getContext(), this, new LikeRequest(getContext(), uid));
        setAdapter(new RelationshipAdapter(getContext(),new ArrayList<User>()));
        setModel(listModel);
    }
}