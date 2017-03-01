package cn.net.cc.weibo.ui.login;

import cn.net.cc.weibo.bean.UserBean;

/**
 * Class Note:登陆View的接口，实现类也就是登陆的activity
 */
public interface LoginView {

    public void setError(String error);

    public void setUser(UserBean user);

}
