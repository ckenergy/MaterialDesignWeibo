package cn.net.cc.weibo.db;

import java.util.ArrayList;
import java.util.List;

import cn.net.cc.weibo.info.Constants;
import cn.net.cc.weibo.main.MyApplication;
import openapi.models.Comment;
import openapi.models.CommentDao;
import openapi.models.StatusDao;
import openapi.models.User;

/**
 * Created by chengkai on 2016/9/7.
 */
public class CommentDatabase implements IDatabase {

    private CommentDao CommentDatabase;

    private static class LaztHolder{
        private static final CommentDatabase INSTANCE = new CommentDatabase();
    }

    public static CommentDatabase getInstance() {
        return LaztHolder.INSTANCE;
    }

    public CommentDao getDao() {
        if (CommentDatabase == null) {
            CommentDatabase = MyApplication.getDaoSession().getCommentDao();
        }
        return CommentDatabase;
    }
    @Override
    public Comment getEntity(String id) {
        return getDao().load(id);
    }

    @Override
    public void saveEntity(Object entity) {
        Comment status = (Comment) entity;
        getDao().insert(status);
    }

    @Override
    public List<Comment> getList(long id) {
        if (id == 0) {
            return getDao().queryBuilder().orderDesc(StatusDao.Properties.Id).limit(Constants.COUNT).list();
        }
        String ids = String.valueOf(id);
        return getDao().queryBuilder().where(StatusDao.Properties.Id.lt(ids)).orderDesc(StatusDao.Properties.Id).limit(Constants.COUNT).list();
    }

    @Override
    public void saveList(List list) {
        List<Comment> commentsList = list;
        ArrayList<Comment> retweetStatus = new ArrayList<>();
        ArrayList<User> users = new ArrayList<>();
        for (Comment comment : commentsList) {
            users.add(comment.user);
            comment.userId = comment.user.id;
        }
        if (retweetStatus.size() >0) {
            saveList(retweetStatus);
        }
        getDao().insertOrReplaceInTx(commentsList);
        DatabaseManager.saveList(users,DatabaseManager.USER_DB);
    }
}
