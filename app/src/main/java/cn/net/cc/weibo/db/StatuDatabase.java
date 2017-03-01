package cn.net.cc.weibo.db;

import org.greenrobot.greendao.query.Query;

import java.util.List;

import cn.net.cc.weibo.info.Constants;
import cn.net.cc.weibo.ui.main.MyApplication;
import openapi.models.Status;
import openapi.models.StatusDao;

/**
 * Created by chengkai on 2016/9/1.
 */
public class StatuDatabase implements IDatabase{

    private static final String TAG = "StatuDatabase";

    public StatusDao statusDao;

    private static class LaztHolder{
        private static final StatuDatabase INSTANCE = new StatuDatabase();
    }

    public static StatuDatabase getInstance() {
        return LaztHolder.INSTANCE;
    }

    public StatusDao getDao() {
        if (statusDao == null) {
            statusDao = MyApplication.getDaoSession().getStatusDao();
        }
        return statusDao;
    }

    @Override
    public void saveEntity(Object entity) {
        if (entity instanceof Status) {
            Status status = (Status) entity;
            getDao().insertOrReplace(status);
//        Log.d(TAG,"user id:"+status.uid);
        }
    }

    public Status getEntity(String id) {
        return getDao().load(id);
    }

    @Override
    public void saveList(List list) {//如果要使用多线程来储存信息，注意status.pic_urlStr同步
        if (list.get(0) instanceof Status) {
            List<Status> statusList = list;
            getDao().insertOrReplaceInTx(statusList);
        }
    }

    public List<Status> getList(long id) {
        if (id == 0) {
            return getDao().queryBuilder().where(StatusDao.Properties.Weibo_type.eq(Status.FOLLOW)).orderDesc(StatusDao.Properties.Id).limit(Constants.COUNT).list();
        }
        String ids = String.valueOf(id);
        return getDao().queryBuilder().where(StatusDao.Properties.Id.lt(ids)).where(StatusDao.Properties.Weibo_type.eq(Status.FOLLOW))
                .orderDesc(StatusDao.Properties.Id).limit(Constants.COUNT).list();
    }

    public List<Status> getListWithUser(String uid, long id) {
        Query<Status> queryBuilder = getDao().queryBuilder().where(StatusDao.Properties.Uid.eq(null)).where(StatusDao.Properties.Id.lt(id))
                .limit(Constants.COUNT).build();
        return queryBuilder.forCurrentThread().setParameter(0, uid).list();
    }

}
