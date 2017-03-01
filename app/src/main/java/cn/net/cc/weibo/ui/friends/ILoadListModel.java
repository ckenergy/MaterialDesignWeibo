package cn.net.cc.weibo.ui.friends;

/**
 * Created by chengkai on 2016/8/2.
 */
public interface ILoadListModel {

    public void loadListNew(long sinceId, long maxId, int type);

    public void loadListMore(long sinceId, long maxId, int type);

    public void showError(String error);

}
