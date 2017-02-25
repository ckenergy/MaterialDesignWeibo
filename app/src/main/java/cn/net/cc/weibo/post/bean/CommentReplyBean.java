package cn.net.cc.weibo.post.bean;

import java.io.Serializable;

import openapi.models.Comment;


/**
 * Created by wenmingvs on 2016/6/27.
 */
public class CommentReplyBean implements Serializable {
    private static final long serialVersionUID = -7613781506785731376L;
    /**
     * 要发送的文本
     */
    public String content;
    /**
     * 要评论的微博或者
     */
    public Comment comment;



    public CommentReplyBean(String content, Comment comment) {
        this.comment = comment;
        this.content = content;
    }

}
