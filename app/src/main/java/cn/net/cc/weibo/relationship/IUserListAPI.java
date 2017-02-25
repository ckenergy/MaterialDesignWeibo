package cn.net.cc.weibo.relationship;

import com.sina.weibo.sdk.net.RequestListener;

/**
 * Created by chengkai on 2016/10/31.
 */
public interface IUserListAPI {

    void loadList(int count, int cursor, RequestListener listener);

}
