package cn.net.cc.weibo.model;

import com.sina.weibo.sdk.net.RequestListener;

/**
 * Created by chengkai on 2016/10/20.
 */
public interface INoIdListAPI {

    public void loadList(long sinceId, long maxId, int count, int type, RequestListener listener);

}
