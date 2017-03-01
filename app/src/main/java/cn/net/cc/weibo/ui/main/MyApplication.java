package cn.net.cc.weibo.ui.main;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

import org.greenrobot.greendao.database.Database;

import openapi.models.DaoMaster;
import openapi.models.DaoSession;

/**
 * Created by chengkai on 2016/8/17.
 */
public class MyApplication extends Application{

    /** A flag to show how easily you can switch from standard SQLite to the encrypted SQLCipher. */
    public static final boolean ENCRYPTED = false;

    private static DaoSession daoSession;

    @Override public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, ENCRYPTED ? "notes-db-encrypted" : "notes-db");
        Database db = ENCRYPTED ? helper.getEncryptedWritableDb("super-secret") : helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public static DaoSession getDaoSession() {
        return daoSession;
    }

}
