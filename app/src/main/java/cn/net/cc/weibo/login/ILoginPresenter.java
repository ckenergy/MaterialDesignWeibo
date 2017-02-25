package cn.net.cc.weibo.login;

import android.content.Intent;

/**
 * Created by chengkai on 2016/8/2.
 */
public interface ILoginPresenter {

    public void doLogin();
    public boolean checkUser();
    public void authorizeCallBack(int requestCode, int resultCode, Intent data);
}
