package cn.net.cc.weibo.base;

import java.util.List;

/**
 * Created by chengkai on 2016/9/19.
 */
public interface IAdapterModel<T> {

    public void addListNew(List<T> datas) ;

    public void addListMore(List<T> datas) ;

    public long getSinceId() ;

    public long getMaxId() ;

}
