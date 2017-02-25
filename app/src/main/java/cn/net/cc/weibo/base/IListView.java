package cn.net.cc.weibo.base;

import java.util.List;

/**
 * Created by chengkai on 2016/8/25.
 */
public interface IListView<T> {

    public void setListEnd(List<T> datas);

    public void setListNew(List<T> datas);

    public void setNoMore();

    public void setError(String Error);
}
