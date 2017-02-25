package cn.net.cc.weibo.post;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

import cn.net.cc.weibo.R;
import cn.net.cc.weibo.imageLoad.BitmapRequest;
import cn.net.cc.weibo.imageLoad.ImageLoader;
import cn.net.cc.weibo.info.Constants;
import cn.net.cc.weibo.main.WeiboMainActivity;
import cn.net.cc.weibo.post.bean.CommentReplyBean;
import cn.net.cc.weibo.post.bean.WeiBoCommentBean;
import cn.net.cc.weibo.post.bean.WeiBoCreateBean;
import cn.net.cc.weibo.util.AccessTokenKeeper;
import openapi.CommentsAPI;
import openapi.legacy.StatusesAPI;


/**
 * Created by wenmingvs on 16/5/8.
 */
public class PostService extends Service {

    private static final String TAG = "PostService";

    private Context mContext;
    private NotificationManager mSendNotifity;
    private StatusesAPI mStatusesAPI;

    public String mPostType;

    public static final String POST_SERVICE_REPOST_STATUS = "转发微博";
    public static final String POST_SERVICE_CREATE_WEIBO = "发微博";
    public static final String POST_SERVICE_COMMENT_STATUS = "评论微博";
    public static final String POST_SERVICE_REPLY_COMMENT = "回复评论";


    /**
     * 微博发送成功
     */
    private static final int SEND_STATUS_SUCCESS = 1;
    /**
     * 微博发送失败
     */
    private static final int SEND_STATUS_ERROR = 2;
    /**
     * 微博发送中
     */
    private static final int SEND_STATUS_SEND = 3;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"time3:"+System.currentTimeMillis());
        mStatusesAPI = new StatusesAPI(this, Constants.APP_KEY, AccessTokenKeeper.readAccessToken(this));
        mPostType = intent.getStringExtra("postType");
        showSendNotifiy();
        switch (mPostType) {
            case POST_SERVICE_CREATE_WEIBO:
                WeiBoCreateBean createWeiBo = (WeiBoCreateBean) intent.getSerializableExtra("weiBoCreateBean");
                if (createWeiBo.selectImgList == null || createWeiBo.selectImgList.size() == 0) {
                    sendTextContent(createWeiBo);
                } else {
                    sendImgTextContent(createWeiBo);
                }
                break;
            case POST_SERVICE_REPOST_STATUS:
                WeiBoCreateBean repostBean = (WeiBoCreateBean) intent.getSerializableExtra("weiBoCreateBean");
                repost(repostBean);
                break;
            case POST_SERVICE_REPLY_COMMENT:
                CommentReplyBean commentReplyBean = (CommentReplyBean) intent.getSerializableExtra("commentReplyBean");
                replyComment(commentReplyBean);
                break;
            case POST_SERVICE_COMMENT_STATUS:
                WeiBoCommentBean weiBoCommentBean = (WeiBoCommentBean) intent.getSerializableExtra("weiBoCommentBean");
                commentWeiBo(weiBoCommentBean);
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 发送图文微博
     */
    private void sendImgTextContent(final WeiBoCreateBean weiBoCreateBean) {
        if (weiBoCreateBean.content.isEmpty() && weiBoCreateBean.status == null) {
            weiBoCreateBean.content = "分享图片";
        } else if (weiBoCreateBean.content.isEmpty() && weiBoCreateBean.status != null) {
            weiBoCreateBean.content = "转发微博";
        }

        String url = "file:///" + weiBoCreateBean.selectImgList.get(0).getImageFile().getAbsolutePath();
        BitmapRequest bitmapRequest = new BitmapRequest(this, url, new BitmapRequest.Listener() {
            @Override
            public void loadBitmap(Bitmap bitmap) {
                if (bitmap == null) {
                    Toast.makeText(PostService.this, "获取图片失败", Toast.LENGTH_SHORT).show();
                }
                mStatusesAPI.upload(weiBoCreateBean.content, bitmap, "0", "0", new RequestListener() {
                    @Override
                    public void onComplete(String s) {
                        onRequestComplete();
                    }

                    @Override
                    public void onWeiboException(WeiboException e) {
                        onRequestError(e, "发送失败");
                    }
                });
            }
        });

        ImageLoader.getBitmap(bitmapRequest);
    }


    /**
     * 发送纯文字的微博
     */
    private void sendTextContent(WeiBoCreateBean weiBoCreateBean) {
        mStatusesAPI.update(weiBoCreateBean.content.toString(), "0.0", "0.0", new RequestListener() {
            @Override
            public void onComplete(String s) {
                onRequestComplete();
            }

            @Override
            public void onWeiboException(WeiboException e) {
                onRequestError(e, "发送失败");
            }
        });

    }

    /**
     * 转发一条微博
     */
    private void repost(WeiBoCreateBean weiBoCreateBean) {
        mStatusesAPI.repost(Long.valueOf(weiBoCreateBean.status.id), weiBoCreateBean.content, 0, new RequestListener() {
            @Override
            public void onComplete(String s) {
                onRequestComplete();
            }

            @Override
            public void onWeiboException(WeiboException e) {
                onRequestError(e, "转发失败");
            }
        });
    }

    /**
     * 评论一条微博
     *
     * @param weiBoCommentBean {@link WeiBoCreateBean}
     */
    public void commentWeiBo(WeiBoCommentBean weiBoCommentBean) {
        CommentsAPI commentsAPI = new CommentsAPI(mContext, Constants.APP_KEY, AccessTokenKeeper.readAccessToken(mContext));
        commentsAPI.create(weiBoCommentBean.content, Long.valueOf(weiBoCommentBean.status.id), false, new RequestListener() {
            @Override
            public void onComplete(String s) {
                onRequestComplete();
            }

            @Override
            public void onWeiboException(WeiboException e) {
                onRequestError(e, "评论失败");

            }
        });
    }

    /**
     * 回复一条评论
     *
     * @param commentReplyBean
     */
    public void replyComment(CommentReplyBean commentReplyBean) {
        CommentsAPI commentsAPI = new CommentsAPI(mContext, Constants.APP_KEY, AccessTokenKeeper.readAccessToken(mContext));
        commentsAPI.reply(Long.valueOf(commentReplyBean.comment.id), Long.valueOf(commentReplyBean.comment.status.id), commentReplyBean.content, false, false,
                new RequestListener() {
                    @Override
                    public void onComplete(String s) {
                        onRequestComplete();
                    }

                    @Override
                    public void onWeiboException(WeiboException e) {
                        onRequestError(e, "回复评论失败");
                    }
                });
    }

    public void onRequestComplete() {
        Log.d(TAG,"time4:"+System.currentTimeMillis());
        mSendNotifity.cancel(SEND_STATUS_SEND);
        showSuccessNotifiy();
        final Message message = Message.obtain();
        message.what = SEND_STATUS_SEND;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mHandler.sendMessage(message);
            }
        }, 2000);
    }

    public void onRequestError(WeiboException e, String errorRemind) {
        if (e.getMessage().contains("repeat content")) {
            Toast.makeText(PostService.this, errorRemind + "：请不要回复重复的内容",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(PostService.this, errorRemind ,Toast.LENGTH_SHORT).show();
        }
        mSendNotifity.cancel(SEND_STATUS_SEND);
        showErrorNotifiy();
        final Message message = Message.obtain();
        message.what = SEND_STATUS_ERROR;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mHandler.sendMessage(message);
            }
        }, 2000);
    }

    /**
     * 显示发送的notify
     */
    private void showSendNotifiy() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Intent intent = new Intent(this, WeiboMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent rightPendIntent = PendingIntent.getActivity(this, SEND_STATUS_SEND, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        String ticker = "正在发送";
        builder.setContentIntent(rightPendIntent);
        builder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.login_bg));
        builder.setSmallIcon(R.drawable.arrow);
        builder.setTicker(ticker);
        builder.setContentTitle("WeiSwift");
        builder.setContentText("正在发送");
        builder.setWhen(System.currentTimeMillis());
        builder.setAutoCancel(true);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setProgress(0, 0, true);
        Notification notification = builder.build();
        mSendNotifity = (NotificationManager) this.getSystemService(Activity.NOTIFICATION_SERVICE);
        mSendNotifity.notify(SEND_STATUS_SEND, notification);
    }

    /**
     * 发送成功的通知
     */
    private void showSuccessNotifiy() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Intent intent = new Intent(this, WeiboMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent rightPendIntent = PendingIntent.getActivity(this, SEND_STATUS_SUCCESS, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        String ticker = "发送成功";
        builder.setContentIntent(rightPendIntent);
        builder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.login_bg));
        builder.setSmallIcon(R.drawable.arrow);
        builder.setTicker(ticker);
        builder.setContentTitle("WeiSwift");
        builder.setContentText("发送成功");
        builder.setWhen(System.currentTimeMillis());
        builder.setAutoCancel(true);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        Notification notification = builder.build();
        // 发送该通知
        mSendNotifity = (NotificationManager) this.getSystemService(Activity.NOTIFICATION_SERVICE);
        mSendNotifity.notify(SEND_STATUS_SUCCESS, notification);
    }

    /**
     * 发送失败的通知
     */
    private void showErrorNotifiy() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Intent intent = new Intent(this, WeiboMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent rightPendIntent = PendingIntent.getActivity(this, SEND_STATUS_ERROR, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        String ticker = "发送失败";
        builder.setContentIntent(rightPendIntent);
        builder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.login_bg));
        builder.setSmallIcon(R.drawable.arrow);
        builder.setTicker(ticker);
        builder.setContentTitle("WeiSwift");
        builder.setContentText("发送失败");
        builder.setWhen(System.currentTimeMillis());
        builder.setAutoCancel(true);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        Notification notification = builder.build();
        // 发送该通知
        mSendNotifity = (NotificationManager) this.getSystemService(Activity.NOTIFICATION_SERVICE);
        mSendNotifity.notify(SEND_STATUS_ERROR, notification);
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mSendNotifity.cancelAll();
            stopSelf();

        }
    };

}
