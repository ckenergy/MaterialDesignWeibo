package cn.net.cc.weibo.db;

import android.util.Log;

import java.util.List;

import cn.net.cc.weibo.info.Constants;
import cn.net.cc.weibo.ui.main.MyApplication;
import openapi.models.User;
import openapi.models.UserDao;

/**
 * Created by chengkai on 2016/9/1.
 */
public class UserDatabase implements IDatabase{

    private static final String TAG = "UserDataBase";

    public UserDao userDao;

    private static class LaztHolder{
        private static final UserDatabase INSTANCE = new UserDatabase();
    }

    public static UserDatabase getInstance() {
        return LaztHolder.INSTANCE;
    }

    public UserDao getDao() {
        if (userDao == null) {
            userDao = MyApplication.getDaoSession().getUserDao();
        }
        return userDao;
    }

    @Override
    public  User getEntity(String id) {
        return getDao().load(id);
    }

    @Override
    public  void saveEntity(Object entity) {
        User user = (User) entity;
        getDao().insertOrReplace(user);
        Log.d(TAG,"user id:"+user.id);
    }

    @Override
    public List<User> getList(long id) {
        if (id == 0) {
            return getDao().queryBuilder().orderDesc(UserDao.Properties.Id).limit(Constants.COUNT).list();
        }
        String ids = String.valueOf(id);
        return getDao().queryBuilder().where(UserDao.Properties.Id.lt(ids)).orderDesc(UserDao.Properties.Id).limit(Constants.COUNT).list();
    }

    @Override
    public  void saveList(List list) {
        List<User> list1 = list;
        getDao().insertOrReplaceInTx(list1);
    }
}
