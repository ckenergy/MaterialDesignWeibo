package cn.net.cc.weibo.model;

import android.content.Intent;

/**
 * Class Note:模拟登陆的操作的接口，实现类为LoginModelImpl.相当于MVP模式中的Model层
 */
public interface LoginModel {
    public void doLogin();
    public boolean checkUser();
    public void authorizeCallBack(int requestCode, int resultCode, Intent data);
    
}
