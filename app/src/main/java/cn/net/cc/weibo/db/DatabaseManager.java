package cn.net.cc.weibo.db;

import java.util.HashMap;
import java.util.List;

/**
 * Created by chengkai on 2016/9/1.
 */
public class DatabaseManager {

    private static final String TAG = "DatabaseManager";

    public static final String USER_DB = "user_db";

    public static final String STATUS_DB = "status_db";

    public static final String COMMENT_DB = "comment_db";

    private static final HashMap<String,IDatabase> databases = new HashMap<String,IDatabase>();

    static {
        databases.put(USER_DB,UserDatabase.getInstance());
        databases.put(STATUS_DB,StatuDatabase.getInstance());
        databases.put(COMMENT_DB, CommentDatabase.getInstance());
    }

    public static <T> T getEntity(String id, String type) {
        return databases.get(type).getEntity(id);
    }

    public static <T> void saveEntity(T entity, String type) {
        databases.get(type).saveEntity(entity);
    }

    public static <T> void saveList(List<T> list, String type) {
        databases.get(type).saveList(list);
    }

    public static <T> List<T> getList(long id, String type) {
        return databases.get(type).getList(id);
    }

}
