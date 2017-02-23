package cn.net.cc.weibo.model;

import com.sina.weibo.sdk.net.RequestListener;

import java.util.List;

/**
 * Created by chengkai on 2016/9/20.
 */
public interface IStatusesAPI<T> {

    public void loadList(long sinceId, long maxId, int count, int type, RequestListener listener);

    public void loadIds(long sinceId, long maxId, int type, RequestListener listener);

    public List<T> getList(long id);

    void saveList(List<T> list);

}
