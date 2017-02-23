/*
 * Copyright (C) 2010-2013 The SINA WEIBO Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package openapi.models;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.net.cc.weibo.db.StatuUrlsConverter;

/**
 * 微博结构体。
 * 
 * @author SINA
 * @since 2013-11-22
 */
@Entity
public class Status implements Serializable{

    private static final long serialVersionUID = -8457931340015953051L;

    public static final int NOT_FOLLOW = 0;

    public static final int FOLLOW = 1;

    /** 微博创建时间 */
    public String created_at;
    /** 微博ID */
    @Id
    public String id;
    /** 微博MID */
    public String mid;
    /** 字符串型的微博ID */
    public String idstr;
    /** 微博信息内容 */
    public String text;
    /** 微博来源 */
    public String source;
    /** 是否已收藏，true：是，false：否  */
    public boolean favorited;
    /** 是否被截断，true：是，false：否 */
    public boolean truncated;

    /**来判断转发微博是否可以显示，即是否是被关注用户，默认0为显示*/
    public int weibo_type = NOT_FOLLOW;
    /**（暂未支持）回复ID */
//    public String in_reply_to_status_id;
    /**（暂未支持）回复人UID */
//    public String in_reply_to_user_id;
    /**（暂未支持）回复人昵称 */
//    public String in_reply_to_screen_name;
    /** 缩略图片地址（小图），没有时不返回此字段 */
//    public String thumbnail_pic;
    /** 中等尺寸图片地址（中图），没有时不返回此字段 */
//    public String bmiddle_pic;
    /** 原始图片地址（原图），没有时不返回此字段 */
//    public String original_pic;

    /** 地理信息字段 */
    @Transient
    public Geo geo;
    /** 微博作者的用户信息字段 */

    public String uid;

    /**
     * 微博文本内容长度
     */
    public int textLength;
    /**
     * 是否是超过140个字的长微博
     */
    public boolean isLongText;

    @ToOne(joinProperty = "uid")
    public User user;

    public String retweetedID;
    /** 被转发的原微博信息字段，当该微博为转发微博时返回 */
    @ToOne(joinProperty = "retweetedID")
    public Status retweeted_status;
    /** 转发数 */
    public int reposts_count;
    /** 评论数 */
    public int comments_count;
    /** 表态数 */
    public int attitudes_count;
    /** 暂未支持 */
//    @Transient
//    public int mlevel;
    @ToMany(referencedJoinProperty = "statusID")
    private List<Comment> comments;

    @ToMany(referencedJoinProperty = "retweetedID")
    private List<Status> retweets;
    /**
     * 微博的可见性及指定可见分组信息。该 object 中 type 取值，
     * 0：普通微博，1：私密微博，3：指定分组微博，4：密友微博；
     * list_id为分组的组号
     */
    @Transient
    public Visible visible;
    /** 微博配图地址。多图时返回多图链接。无配图返回"[]" */
    @Transient
    public ArrayList<UrlBean> pic_urls;

    @Convert(converter = StatuUrlsConverter.class, columnType = String.class)
    public ArrayList<String> pic_urlStr;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1443046080)
    private transient StatusDao myDao;

    @Generated(hash = 1026906284)
    public Status(String created_at, String id, String mid, String idstr, String text, String source,
            boolean favorited, boolean truncated, int weibo_type, String uid, int textLength,
            boolean isLongText, String retweetedID, int reposts_count, int comments_count,
            int attitudes_count, ArrayList<String> pic_urlStr) {
        this.created_at = created_at;
        this.id = id;
        this.mid = mid;
        this.idstr = idstr;
        this.text = text;
        this.source = source;
        this.favorited = favorited;
        this.truncated = truncated;
        this.weibo_type = weibo_type;
        this.uid = uid;
        this.textLength = textLength;
        this.isLongText = isLongText;
        this.retweetedID = retweetedID;
        this.reposts_count = reposts_count;
        this.comments_count = comments_count;
        this.attitudes_count = attitudes_count;
        this.pic_urlStr = pic_urlStr;
    }

    @Generated(hash = 1855832530)
    public Status() {
    }

    @Generated(hash = 1867105156)
    private transient String user__resolvedKey;
    @Generated(hash = 591556362)
    private transient String retweeted_status__resolvedKey;

    public static class UrlBean implements Serializable{
        private static final long serialVersionUID = 5240128708811394110L;
        public String thumbnail_pic;
    }
    /** 微博流内的推广微博ID */
    //public Ad ad;

//    public static Status parse(String jsonString) {
//        /*try {
//            JSONObject jsonObject = new JSONObject(jsonString);
//            return Status.parse(jsonObject);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }*/
//        Status status = JsonProvider.json2pojo(jsonString,Status.class);
//        return status;
//    }

    public ArrayList<String> getPic_urlStr() {
        if (pic_urlStr == null) {
            pic_urlStr = UrlBean2String(pic_urls);
        }
        return pic_urlStr;
    }

    public static ArrayList<String> UrlBean2String(ArrayList<Status.UrlBean> urlbeans) {
        if (urlbeans != null && urlbeans.size() >0) {
            ArrayList<String> urls= new ArrayList<>(urlbeans.size());
            for (Status.UrlBean urlBean : urlbeans) {
                urls.add(replace2middle(urlBean.thumbnail_pic));
            }
            return urls;
        }
        return null;
    }

    private static String replace2middle(String url) {
        return url.replace("thumbnail","bmiddle");
    }

    public String getCreated_at() {
        return this.created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMid() {
        return this.mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getIdstr() {
        return this.idstr;
    }

    public void setIdstr(String idstr) {
        this.idstr = idstr;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean getFavorited() {
        return this.favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public boolean getTruncated() {
        return this.truncated;
    }

    public void setTruncated(boolean truncated) {
        this.truncated = truncated;
    }

    public String getRetweetedID() {
        return this.retweetedID;
    }

    public void setRetweetedID(String retweetedID) {
        this.retweetedID = retweetedID;
    }

    public int getReposts_count() {
        return this.reposts_count;
    }

    public void setReposts_count(int reposts_count) {
        this.reposts_count = reposts_count;
    }

    public int getComments_count() {
        return this.comments_count;
    }

    public void setComments_count(int comments_count) {
        this.comments_count = comments_count;
    }

    public int getAttitudes_count() {
        return this.attitudes_count;
    }

    public void setAttitudes_count(int attitudes_count) {
        this.attitudes_count = attitudes_count;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1749355717)
    public User getUser() {
        String __key = this.uid;
        if (user__resolvedKey == null || user__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserDao targetDao = daoSession.getUserDao();
            User userNew = targetDao.load(__key);
            synchronized (this) {
                user = userNew;
                user__resolvedKey = __key;
            }
        }
        return user;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 515172768)
    public void setUser(User user) {
        synchronized (this) {
            this.user = user;
            uid = user == null ? null : user.getId();
            user__resolvedKey = uid;
        }
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 280787075)
    public Status getRetweeted_status() {
        String __key = this.retweetedID;
        if (retweeted_status__resolvedKey == null || retweeted_status__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            StatusDao targetDao = daoSession.getStatusDao();
            Status retweeted_statusNew = targetDao.load(__key);
            synchronized (this) {
                retweeted_status = retweeted_statusNew;
                retweeted_status__resolvedKey = __key;
            }
        }
        return retweeted_status;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 351434235)
    public void setRetweeted_status(Status retweeted_status) {
        synchronized (this) {
            this.retweeted_status = retweeted_status;
            retweetedID = retweeted_status == null ? null : retweeted_status.getId();
            retweeted_status__resolvedKey = retweetedID;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    public void setPic_urlStr(ArrayList<String> pic_urlStr) {
        this.pic_urlStr = pic_urlStr;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1991699694)
    public List<Comment> getComments() {
        if (comments == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CommentDao targetDao = daoSession.getCommentDao();
            List<Comment> commentsNew = targetDao._queryStatus_Comments(id);
            synchronized (this) {
                if(comments == null) {
                    comments = commentsNew;
                }
            }
        }
        return comments;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 249603048)
    public synchronized void resetComments() {
        comments = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 315841502)
    public List<Status> getRetweets() {
        if (retweets == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            StatusDao targetDao = daoSession.getStatusDao();
            List<Status> retweetsNew = targetDao._queryStatus_Retweets(id);
            synchronized (this) {
                if(retweets == null) {
                    retweets = retweetsNew;
                }
            }
        }
        return retweets;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1464822139)
    public synchronized void resetRetweets() {
        retweets = null;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getTextLength() {
        return this.textLength;
    }

    public void setTextLength(int textLength) {
        this.textLength = textLength;
    }

    public boolean getIsLongText() {
        return this.isLongText;
    }

    public void setIsLongText(boolean isLongText) {
        this.isLongText = isLongText;
    }

    public int getWeibo_type() {
        return this.weibo_type;
    }

    public void setWeibo_type(int weibo_type) {
        this.weibo_type = weibo_type;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1377533788)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getStatusDao() : null;
    }

    /*private void bean2Str() {
        if (pic_urls !=null && pic_urls.size()>0 ) {
            int length = pic_urls.size();
            pic_urlStr = new ArrayList<>(length);
            for (UrlBean urlBean : pic_urls) {
                pic_urlStr.add(urlBean.thumbnail_pic);
                Log.d("bean2Str",urlBean.thumbnail_pic);
            }
        }
    }*/

    /* public static Status parse(JSONObject jsonObject) {
        if (null == jsonObject) {
            return null;
        }
        
        Status status = new Status();
        status.created_at       = jsonObject.optString("created_at");
        status.id               = jsonObject.optString("id");
        status.mid              = jsonObject.optString("mid");
        status.idstr            = jsonObject.optString("idstr");
        status.text             = jsonObject.optString("text");
        status.source           = jsonObject.optString("source");
        status.favorited        = jsonObject.optBoolean("favorited", false);
        status.truncated        = jsonObject.optBoolean("truncated", false);
        
        // Have NOT supported
        status.in_reply_to_status_id   = jsonObject.optString("in_reply_to_status_id");
        status.in_reply_to_user_id     = jsonObject.optString("in_reply_to_user_id");
        status.in_reply_to_screen_name = jsonObject.optString("in_reply_to_screen_name");
        
        status.thumbnail_pic    = jsonObject.optString("thumbnail_pic");
        status.bmiddle_pic      = jsonObject.optString("bmiddle_pic");
        status.original_pic     = jsonObject.optString("original_pic");
        status.geo              = Geo.parse(jsonObject.optJSONObject("geo"));
        status.user             = User.parse(jsonObject.optJSONObject("user"));
        status.retweeted_status = Status.parse(jsonObject.optJSONObject("retweeted_status"));
        status.reposts_count    = jsonObject.optInt("reposts_count");
        status.comments_count   = jsonObject.optInt("comments_count");
        status.attitudes_count  = jsonObject.optInt("attitudes_count");
        status.mlevel           = jsonObject.optInt("mlevel", -1);    // Have NOT supported
        status.visible          = Visible.parse(jsonObject.optJSONObject("visible"));
        
        JSONArray picUrlsArray = jsonObject.optJSONArray("pic_urls");
        if (picUrlsArray != null && picUrlsArray.length() > 0) {
            int length = picUrlsArray.length();
            status.pic_urls = new ArrayList<String>(length);
            JSONObject tmpObject = null;
            for (int ix = 0; ix < length; ix++) {
                tmpObject = picUrlsArray.optJSONObject(ix);
                if (tmpObject != null) {
                    status.pic_urls.add(tmpObject.optString("thumbnail_pic"));
                }
            }
        }
        
        //status.ad = jsonObject.optString("ad", "");
        
        return status;
    }*/

}
