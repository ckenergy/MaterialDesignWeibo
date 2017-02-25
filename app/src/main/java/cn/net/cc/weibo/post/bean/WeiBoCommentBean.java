package cn.net.cc.weibo.post.bean;

import java.io.Serializable;

import openapi.models.Status;

/**
 * Created by wenmingvs on 2016/6/27.
 */
public class WeiBoCommentBean implements Serializable {
    private static final long serialVersionUID = -1141184845877692196L;
    /**
     * 要发送的文本
     */
    public String content;
    /**
     * 要评论的微博或者
     */
    public Status status;

    public WeiBoCommentBean(String content, Status status) {
        this.content = content;
        this.status = status;
    }

}
