package cn.net.cc.weibo.model;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

import cn.net.cc.weibo.bean.UserBean;
import cn.net.cc.weibo.imageLoad.BitmapRequest;
import cn.net.cc.weibo.imageLoad.ImageLoader;
import cn.net.cc.weibo.info.Constants;
import cn.net.cc.weibo.login.LoginView;
import cn.net.cc.weibo.util.AccessTokenManager;
import openapi.models.User;


/**
 * 
* @Description: 登录模型
* @author chengkai  
* @date 2016年6月21日 下午5:02:00 
*
 */
public class LoginModelImpl implements LoginModel, WeiboAuthListener {

	private static final String TAG = "LoginModelImpl";

	private AuthInfo mAuthInfo;
    
    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;

    /** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
    private SsoHandler mSsoHandler;
    
    private Context mContext;
    
    private LoginView mLoginView;
    
    private UserModel userModel;
	
    public LoginModelImpl(Context context, LoginView loginView) {
    	this.mContext = context;
    	this.mLoginView = loginView;
    	init();
    }
    
    public void init() {
    	// 快速授权时，请不要传入 SCOPE，否则可能会授权不成功
        mAuthInfo = new AuthInfo(mContext, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        mSsoHandler = new SsoHandler((Activity) mContext, mAuthInfo);
        userModel = new UserModel(mContext,userRequestListener);
//        HttpManager.getInstance().init(mContext);
    }
    
    public void authorizeCallBack(int requestCode, int resultCode, Intent data) {
    	mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
    }

	@Override
    public void doLogin() {
    	mSsoHandler.authorize(this);
    }
    
	@Override
	public void onComplete(Bundle values) {
		// 从 Bundle 中解析 Token
		mAccessToken = Oauth2AccessToken.parseAccessToken(values);
		if (mAccessToken.isSessionValid()) {

			// 保存 Token 到 SharedPreferences
			AccessTokenManager.setAccessToken(mContext, mAccessToken);
			long uid = Long.parseLong(mAccessToken.getUid());
			userModel.show(uid, true);
			
		} else {
			// 以下几种情况，您会收到 Code：
			// 1. 当您未在平台上注册的应用程序的包名与签名时；
			// 2. 当您注册的应用程序包名与签名不正确时；
			// 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
			String code = String.format("error code:%s", values.getString("code"));
			setViewError(code);
		}
	}

	@Override
	public void onCancel() {
		
	}

	@Override
	public void onWeiboException(WeiboException e) {
		setViewError(e.getMessage());
	}
	
	private void setViewError(String error) {
		mLoginView.setError(error);
	}
	
	@Override
	public boolean checkUser() {
		mAccessToken = AccessTokenManager.getAccessToken(mContext);
		if(mAccessToken != null) {
			if(TextUtils.isEmpty(mAccessToken.getUid())){
				return false;
			}
			Log.d(TAG,"uid:"+mAccessToken.getUid());
			long uid = Long.parseLong(mAccessToken.getUid());
			userModel.show(uid, false);
			return true;
		}
		return false;
	}

	private void loadImage(User user) {
		String imgurl = user.avatar_large;

		final UserBean userbean = new UserBean();
		userbean.name = user.screen_name;

		BitmapRequest request = new BitmapRequest(mContext, imgurl, new BitmapRequest.Listener(){

			@Override
			public void loadBitmap(Bitmap bitmap) {
				userbean.icon = bitmap;
				mLoginView.setUser(userbean);
			}
		});

		ImageLoader.getBitmap(request);
	}

	UserModel.UserRequestListener userRequestListener = new UserModel.UserRequestListener() {
		
		@Override
		public void setUser(User user) {
			loadImage(user);
		}
		
		@Override
		public void setError(String error) {
			setViewError(error);
		}
	};
	
}
