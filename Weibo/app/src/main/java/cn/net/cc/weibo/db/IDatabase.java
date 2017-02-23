package cn.net.cc.weibo.db;

import java.util.List;

/**
 * Created by chengkai on 2016/9/2.
 */
public interface IDatabase {

    public <T> T getEntity(String id);

    public  void saveEntity(Object entity);

    public <T> List<T> getList(long id) ;

    public void saveList(List list);
}
