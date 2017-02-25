package cn.net.cc.weibo.model;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

import java.util.List;

import cn.net.cc.weibo.db.DatabaseManager;
import cn.net.cc.weibo.db.UserDatabase;
import cn.net.cc.weibo.info.Constants;
import cn.net.cc.weibo.json.JsonProvider;
import cn.net.cc.weibo.util.AccessTokenManager;
import openapi.UsersAPI;
import openapi.models.ErrorInfo;
import openapi.models.User;
import openapi.models.UserDao;


/**
 * 
* @Description: weibo查询用户
* @author chengkai  
* @date 2016年6月23日 下午2:53:12 
*
 */
public class UserModel {

    private static final String TAG = "UserModel";

	private UserRequestListener mUserListener;
	
	/** 当前 Token 信息 */
    private Oauth2AccessToken mAccessToken;
    /** 用户信息接口 */
    private UsersAPI mUsersAPI;
    
    public UserModel(Context context){
		this(context, null);
	}
	
	public UserModel(Context context, UserRequestListener userListener){
		this.mUserListener = userListener;
		// 获取当前已保存过的 Token
        mAccessToken = AccessTokenManager.getAccessToken(context);
        // 获取用户信息接口
        mUsersAPI = new UsersAPI(context, Constants.APP_KEY, mAccessToken);
	}

	/**
	 * @param mUserListener the {@link #mUserListener} to set
	 */
	public void setmUserListener(UserRequestListener mUserListener) {
		this.mUserListener = mUserListener;
	}

	public void show(long uid ,boolean isForce) {
        if (!isForce) {
            User user = loadUserWithId(uid);
            if (user != null) {
                mUserListener.setUser(user);
                return;
            }
        }
		mUsersAPI.show(uid, new UserListener(uid));
	}

    private User loadUserWithId(long id) {
        String ids = String.valueOf(id);
        User user = DatabaseManager.getEntity(ids, DatabaseManager.USER_DB);
        return user;
    }

    public void show(String name, boolean isForce) {
        if(!isForce) {
            User user = loadUserWithName(name);
            if (user != null) {
                mUserListener.setUser(user);
                return;
            }
            Log.d(TAG,name +"is null");
        }
        mUsersAPI.show(name, new UserListener(name));
    }

    private User loadUserWithName(String name) {
        List<User> list = UserDatabase.getInstance().getDao().queryBuilder().where(UserDao.Properties.Name.eq(name))
                .orderDesc(UserDao.Properties.Id).limit(1).list();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

	/**
     * 微博 OpenAPI 回调接口。
     */
    class UserListener implements RequestListener {

        String name;
        long id ;
        UserListener(String name) {
            this.name = name;
        }
        UserListener(long id) {
            this.id = id;
        }
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                // 调用 User#parse 将JSON串解析成User对象
            	Log.d(TAG, response);
                User user = JsonProvider.json2pojo(response,User.class);//User.parse(response);
                if (user != null) {
                    DatabaseManager.saveEntity(user, DatabaseManager.USER_DB);
                	mUserListener.setUser(user);
                } else {
                    if (name != null) {
                        user = loadUserWithName(name);
                    }else {
                        user = loadUserWithId(id);
                    }
                    if (user != null) {
                        mUserListener.setUser(user);
                    }else {
                        mUserListener.setError("parse error");
                    }
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            User user;
            if (name != null) {
                user = loadUserWithName(name);
            }else {
                user = loadUserWithId(id);
            }
            if (user != null) {
                mUserListener.setUser(user);
            }else {
                ErrorInfo info = ErrorInfo.parse(e.getMessage());
                mUserListener.setError(info.toString());
            }
        }
    }
    
    public interface UserRequestListener{
    	
    	public void setUser(User user);
    	
    	public void setError(String error);
    }
}
