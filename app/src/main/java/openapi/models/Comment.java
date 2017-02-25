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

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

import java.io.Serializable;

/**
 * 评论结构体。
 * 
 * @author SINA
 * @since 2013-11-24
 */
@Entity
public class Comment implements Serializable{

    private static final long serialVersionUID = -159176937109309928L;
    /** 评论创建时间 */
    public String created_at;
    /** 评论的 ID */
    @Id
    public String id;
    /** 评论的内容 */
    public String text;
    /** 评论的来源 */
    public String source;
    /** 评论作者的用户信息字段 */
    public String userId;

    @ToOne(joinProperty = "userId")
    public User user;
    /** 评论的 MID */
    public String mid;
    /** 字符串型的评论 ID */
    public String idstr;

    /**可能是赞*/
    public int source_allowclick;

    public int floor_number;

    /** 评论的微博信息字段 */
    public String statusID;
    @ToOne(joinProperty = "statusID")
    public Status status;
    /** 评论来源评论，当本评论属于对另一评论的回复时返回此字段 */
    public String replyId;
    @ToOne(joinProperty = "replyId")
    public Comment reply_comment;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1903578761)
    private transient CommentDao myDao;
    @Generated(hash = 1141735085)
    public Comment(String created_at, String id, String text, String source, String userId,
            String mid, String idstr, int source_allowclick, int floor_number,
            String statusID, String replyId) {
        this.created_at = created_at;
        this.id = id;
        this.text = text;
        this.source = source;
        this.userId = userId;
        this.mid = mid;
        this.idstr = idstr;
        this.source_allowclick = source_allowclick;
        this.floor_number = floor_number;
        this.statusID = statusID;
        this.replyId = replyId;
    }
    @Generated(hash = 1669165771)
    public Comment() {
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
    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
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
    public String getStatusID() {
        return this.statusID;
    }
    public void setStatusID(String statusID) {
        this.statusID = statusID;
    }
    public String getReplyId() {
        return this.replyId;
    }
    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }
    @Generated(hash = 1867105156)
    private transient String user__resolvedKey;
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 538271798)
    public User getUser() {
        String __key = this.userId;
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
    @Generated(hash = 1065606912)
    public void setUser(User user) {
        synchronized (this) {
            this.user = user;
            userId = user == null ? null : user.getId();
            user__resolvedKey = userId;
        }
    }
    @Generated(hash = 1655103481)
    private transient String status__resolvedKey;
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1426651286)
    public Status getStatus() {
        String __key = this.statusID;
        if (status__resolvedKey == null || status__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            StatusDao targetDao = daoSession.getStatusDao();
            Status statusNew = targetDao.load(__key);
            synchronized (this) {
                status = statusNew;
                status__resolvedKey = __key;
            }
        }
        return status;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1201871685)
    public void setStatus(Status status) {
        synchronized (this) {
            this.status = status;
            statusID = status == null ? null : status.getId();
            status__resolvedKey = statusID;
        }
    }
    @Generated(hash = 408894020)
    private transient String reply_comment__resolvedKey;
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 147682510)
    public Comment getReply_comment() {
        String __key = this.replyId;
        if (reply_comment__resolvedKey == null || reply_comment__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CommentDao targetDao = daoSession.getCommentDao();
            Comment reply_commentNew = targetDao.load(__key);
            synchronized (this) {
                reply_comment = reply_commentNew;
                reply_comment__resolvedKey = __key;
            }
        }
        return reply_comment;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 504355727)
    public void setReply_comment(Comment reply_comment) {
        synchronized (this) {
            this.reply_comment = reply_comment;
            replyId = reply_comment == null ? null : reply_comment.getId();
            reply_comment__resolvedKey = replyId;
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
    public int getSource_allowclick() {
        return this.source_allowclick;
    }
    public void setSource_allowclick(int source_allowclick) {
        this.source_allowclick = source_allowclick;
    }
    public int getFloor_number() {
        return this.floor_number;
    }
    public void setFloor_number(int floor_number) {
        this.floor_number = floor_number;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 2038262053)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCommentDao() : null;
    }
    
    /*public static Comment parse(String json) {
        if (null == json) {
            return null;
        }

        Comment comment = JsonProvider.json2pojo(json,Comment.class);
        *//*comment.created_at    = jsonObject.optString("created_at");
        comment.id            = jsonObject.optString("id");
        comment.text          = jsonObject.optString("text");
        comment.source        = jsonObject.optString("source");
        comment.user          = User.parse(jsonObject.optJSONObject("user"));
        comment.mid           = jsonObject.optString("mid");
        comment.idstr         = jsonObject.optString("idstr");
        comment.status        = Status.parse(jsonObject.optJSONObject("status"));            
        comment.reply_comment = Comment.parse(jsonObject.optJSONObject("reply_comment"));*//*
        
        return comment;
    }*/
}
