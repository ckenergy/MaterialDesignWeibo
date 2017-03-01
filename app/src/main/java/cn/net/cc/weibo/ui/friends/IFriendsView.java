package cn.net.cc.weibo.ui.friends;

import java.util.List;

import openapi.models.Status;

/**
 * Created by chengkai on 2016/8/2.
 */
public interface IFriendsView {

    public void setStatusListEnd(List<Status> datas);

    public void setStatusListStart(List<Status> datas);

    public void setError(String msg);

}
