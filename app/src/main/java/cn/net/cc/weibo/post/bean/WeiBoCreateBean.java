package cn.net.cc.weibo.post.bean;

import java.io.Serializable;
import java.util.ArrayList;

import cn.net.cc.weibo.post.picselect.bean.ImageInfo;
import openapi.models.Status;

/**
 * Created by wenmingvs on 2016/6/27.
 */
public class WeiBoCreateBean implements Serializable {
    private static final long serialVersionUID = -536423553908024540L;
    /**
     * 要发送的图片列表
     */
    public ArrayList<ImageInfo> selectImgList;
    /**
     * 要发送的文本
     */
    public String content;
    /**
     * 要转发的微博
     */
    public Status status;

    public WeiBoCreateBean(String content, ArrayList<ImageInfo> selectImgList, Status status) {
        this.selectImgList = selectImgList;
        this.content = content;
        this.status = status;
    }

    public WeiBoCreateBean(String content, ArrayList<ImageInfo> selectImgList) {
        this.content = content;
        this.selectImgList = selectImgList;
    }

}
