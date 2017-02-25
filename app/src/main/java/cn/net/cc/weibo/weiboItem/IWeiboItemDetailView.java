package cn.net.cc.weibo.weiboItem;

/**
 * Created by chengkai on 2016/8/25.
 *
 */
public interface IWeiboItemDetailView {

    public void setComment(int retweet,int comment, int attitude);

    public void showError(String error);
}
