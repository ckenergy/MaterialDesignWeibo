package cn.net.cc.weibo.post.idea;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import cn.net.cc.weibo.R;
import cn.net.cc.weibo.imageLoad.ImageLoader;
import cn.net.cc.weibo.post.PostService;
import cn.net.cc.weibo.post.bean.CommentReplyBean;
import cn.net.cc.weibo.post.bean.WeiBoCommentBean;
import cn.net.cc.weibo.post.bean.WeiBoCreateBean;
import cn.net.cc.weibo.post.idea.imagelist.ImgListAdapter;
import cn.net.cc.weibo.post.picselect.activity.AlbumActivity;
import cn.net.cc.weibo.post.picselect.bean.AlbumFolderInfo;
import cn.net.cc.weibo.post.picselect.bean.ImageInfo;
import cn.net.cc.weibo.ui.view.emojitextview.WeiBoContentTextUtil;
import openapi.models.Comment;
import openapi.models.Status;
import openapi.models.User;

/**
 * Created by wenmingvs on 16/5/2.
 */
public class IdeaActivity extends Activity implements ImgListAdapter.OnFooterViewClickListener {

    private static final String TAG = "IdeaActivity";

    public static final String TYPE = "ideaType" ;
    public static final String STATUES = "status" ;
    public static final String COMMENT = "comment" ;

    private Context mContext;
    private Button mCancal;
    private Button mSendButton;
    private TextView publicbutton;
    private ImageView picture;
    private ImageView mentionbutton;
    private ImageView trendbutton;
    private ImageView emoticonbutton;
    private ImageView more_button;
    private EditText mEditText;
    private TextView mLimitTextView;
    private TextView mInputType;
    private LinearLayout mRepostlayout;
    private ImageView repostImg;
    private TextView repostName;
    private TextView repostContent;
    private RecyclerView mRecyclerView;
    private LinearLayout mIdea_linearLayout;

    private ArrayList<AlbumFolderInfo> mFolderList = new ArrayList<AlbumFolderInfo>();
    private ArrayList<ImageInfo> mSelectImgList = new ArrayList<ImageInfo>();
    private Status mStatus;
    private Comment mComment;
    private String mIdeaType;


    /**
     * 最多输入140个字符
     */
    private static final int TEXT_LIMIT = 140;

    /**
     * 在只剩下10个字可以输入的时候，做提醒
     */
    private static final int TEXT_REMIND = 10;

    public static void startThis(Context context, Status status, String type) {
        Intent intent = new Intent(context, IdeaActivity.class);
        intent.putExtra(TYPE, type);
        intent.putExtra(STATUES,status);
        context.startActivity(intent);
    }

    public static void startThis(Context context, Comment comment) {
        Intent intent = new Intent(context, IdeaActivity.class);
        intent.putExtra(TYPE, PostService.POST_SERVICE_REPLY_COMMENT);
        intent.putExtra(COMMENT,comment);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compose_idea_layout);
        mContext = this;
        mInputType = (TextView) findViewById(R.id.inputType);
        mCancal = (Button) findViewById(R.id.idea_cancal);
        mSendButton = (Button) findViewById(R.id.idea_send);
        publicbutton = (TextView) findViewById(R.id.publicbutton);
        picture = (ImageView) findViewById(R.id.picture);
        mentionbutton = (ImageView) findViewById(R.id.mentionbutton);
        trendbutton = (ImageView) findViewById(R.id.trendbutton);
        emoticonbutton = (ImageView) findViewById(R.id.emoticonbutton);
        more_button = (ImageView) findViewById(R.id.more_button);
        mEditText = (EditText) findViewById(R.id.idea_content);
        mLimitTextView = (TextView) findViewById(R.id.limitTextView);
        mRepostlayout = (LinearLayout) findViewById(R.id.repost_layout);
        repostImg = (ImageView) findViewById(R.id.repost_img);
        repostName = (TextView) findViewById(R.id.repost_name);
        repostContent = (TextView) findViewById(R.id.repost_content);
        mRecyclerView = (RecyclerView) findViewById(R.id.ImgList);
        mIdea_linearLayout = (LinearLayout) findViewById(R.id.idea_linearLayout);
        mIdeaType = getIntent().getStringExtra(TYPE);
        mStatus = (Status) getIntent().getSerializableExtra(STATUES);
        mComment = (Comment) getIntent().getSerializableExtra(COMMENT);
        mInputType.setText(mIdeaType);
        initContent();
        setUpListener();
        mEditText.setTag(false);
        if (getIntent().getBooleanExtra("startAlumbAcitivity", false)) {
            Intent intent = new Intent(IdeaActivity.this, AlbumActivity.class);
            intent.putExtra("selectedImglist", mSelectImgList);
            startActivityForResult(intent, 0);
        }
        mEditText.post(new Runnable() {
            @Override
            public void run() {
                setLimitTextColor(mLimitTextView, mEditText.getText().toString());
                mEditText.requestFocus();
            }
        });
    }


    /**
     * 填充内容，
     * 1. 转发的内容是转发微博，
     * 2. 转发的内容是原创微博，
     */
    private void initContent() {
        switch (mIdeaType) {
            case PostService.POST_SERVICE_CREATE_WEIBO:
                break;
            case PostService.POST_SERVICE_REPOST_STATUS:
                //填充转发的内容
                repostWeiBo();
                break;
            case PostService.POST_SERVICE_COMMENT_STATUS:
                break;
            case PostService.POST_SERVICE_REPLY_COMMENT:
                break;
        }
    }


    /**
     * 填充转发的内容
     */
    private void repostWeiBo() {

        if (mStatus == null) {
            return;
        }
        mRepostlayout.setVisibility(View.VISIBLE);
        mEditText.setHint("说说分享的心得");

        //1. 转发的内容是转发微博
        Status retweetedStatus = mStatus.retweeted_status;
        if (mStatus.retweeted_status == null && mStatus.retweetedID != null) {
            retweetedStatus = mStatus.getRetweeted_status();
        }
        if (retweetedStatus != null) {
            mEditText.setText(WeiBoContentTextUtil.getWeiBoContent("//@" + mStatus.user.name + ":" + mStatus.text, mContext, mEditText ,null));
            fillContent(mStatus.retweeted_status, repostImg, repostName, repostContent);
            mEditText.setSelection(0);
        }else { //2. 转发的内容是原创微博
            fillContent(mStatus, repostImg, repostName, repostContent);
        }
        changeSendButtonBg();
    }

    private void fillContent(Status status, ImageView contentImage, TextView repostName, TextView content) {
        User user = status.user;
        if (user == null) {
            user = status.getUser();
        }
        repostName.setText("//@"+user.screen_name);
        content.setText(status.text);
        String conImgUrl;
        if (status.getPic_urlStr() != null && status.getPic_urlStr().size() > 0) {
            conImgUrl = status.getPic_urlStr().get(0);
        }else {
            conImgUrl = user.avatar_large;
        }
        ImageLoader.with(this, conImgUrl, contentImage);
    }

    /**
     * 设置监听事件
     */
    private void setUpListener() {
        mCancal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IdeaActivity.this, AlbumActivity.class);
                intent.putExtra("selectedImglist", mSelectImgList);
                startActivityForResult(intent, 0);
            }
        });
        mentionbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditText.getText().insert(mEditText.getSelectionStart(), "@");
            }
        });
        trendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditText.getText().insert(mEditText.getSelectionStart(), "##");
                mEditText.setSelection(mEditText.getSelectionStart() - 1);
            }
        });
        emoticonbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ToastUtil.showShort(mContext, "正在开发此功能...");
            }
        });
        more_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ToastUtil.showShort(mContext, "正在开发此功能...");
            }
        });
        mEditText.addTextChangedListener(watcher);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"time1:"+System.currentTimeMillis());
                //在发微博状态下，如果发的微博没有图片，且也没有文本内容，识别为空
                if (!isRetweetWeiBoState() && mStatus == null && mSelectImgList.size() == 0 && mEditText.getText().toString().trim().isEmpty()) {
                    Toast.makeText(mContext, "发送的内容不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (calculateWeiboLength(mEditText.getText().toString()) > TEXT_LIMIT) {
                    Toast.makeText(mContext, "文本超出限制" + TEXT_LIMIT + "个字！请做调整",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mSelectImgList.size() > 1) {
                    Toast.makeText(mContext, "由于新浪的限制，第三方微博客户端只允许上传一张图，请做调整",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(mContext, PostService.class);
                Bundle bundle = new Bundle();

                switch (mIdeaType) {
                    case PostService.POST_SERVICE_CREATE_WEIBO:
                        WeiBoCreateBean weiboBean = new WeiBoCreateBean(mEditText.getText().toString(), mSelectImgList);
                        intent.putExtra("postType", PostService.POST_SERVICE_CREATE_WEIBO);
                        bundle.putSerializable("weiBoCreateBean", weiboBean);
                        intent.putExtras(bundle);
                        break;
                    case PostService.POST_SERVICE_REPOST_STATUS:
                        WeiBoCreateBean repostBean = new WeiBoCreateBean(mEditText.getText().toString(), mSelectImgList, mStatus);
                        intent.putExtra("postType", PostService.POST_SERVICE_REPOST_STATUS);
                        bundle.putSerializable("weiBoCreateBean", repostBean);
                        intent.putExtras(bundle);
                        break;
                    case PostService.POST_SERVICE_COMMENT_STATUS:
                        intent.putExtra("postType", PostService.POST_SERVICE_COMMENT_STATUS);
                        WeiBoCommentBean weiBoCommentBean = new WeiBoCommentBean(mEditText.getText().toString(), mStatus);
                        bundle.putSerializable("weiBoCommentBean", weiBoCommentBean);
                        intent.putExtras(bundle);
                        break;
                    case PostService.POST_SERVICE_REPLY_COMMENT:
                        intent.putExtra("postType", PostService.POST_SERVICE_REPLY_COMMENT);
                        CommentReplyBean commentReplyBean = new CommentReplyBean(mEditText.getText().toString(), mComment);
                        bundle.putSerializable("commentReplyBean", commentReplyBean);
                        intent.putExtras(bundle);
                        break;
                }
                Log.d(TAG,"time2:"+System.currentTimeMillis());
                startService(intent);
                finish();
            }
        });

    }


    /**
     * 根据输入的文本数量，决定发送按钮的背景
     */
    private void changeSendButtonBg() {
        //如果有文本，或者有图片，或者是处于转发微博状态
        if (mEditText.getText().toString().length() > 0 || mSelectImgList.size() > 0 || (isRetweetWeiBoState())) {
            highlightSendButton();
        } else {
            sendNormal();
        }
    }

    private void highlightSendButton() {
        mSendButton.setTextColor(Color.BLACK);
        mSendButton.setEnabled(true);
    }

    private void sendNormal() {
        mSendButton.setTextColor(Color.parseColor("#b3b3b3"));
        mSendButton.setEnabled(false);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (data != null) {
                    mSelectImgList = (ArrayList<ImageInfo>) data.getSerializableExtra("selectImgList");
                    initImgList();
                    changeSendButtonBg();
                }
                break;
        }
    }


    public void initImgList() {
        mRecyclerView.setVisibility(View.VISIBLE);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3);
        ImgListAdapter imgListAdapter = new ImgListAdapter(mSelectImgList, mContext);
        imgListAdapter.setOnFooterViewClickListener(this);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(imgListAdapter);
    }

    @Override
    public void OnFooterViewClick() {
        Intent intent = new Intent(IdeaActivity.this, AlbumActivity.class);
        intent.putExtra("selectedImglist", mSelectImgList);
        startActivityForResult(intent, 0);
    }

    private TextWatcher watcher = new TextWatcher() {
        private CharSequence inputString;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            inputString = s;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            changeSendButtonBg();
            setLimitTextColor(mLimitTextView, inputString.toString());
        }
    };


    /**
     * 计算微博文本的长度，统计是否超过140个字，其中中文和全角的符号算1个字符，英文字符和半角字符算半个字符
     *
     * @param c
     * @return 微博的长度，结果四舍五入
     */
    public long calculateWeiboLength(CharSequence c) {
        double len = 0;
        for (int i = 0; i < c.length(); i++) {
            int temp = (int) c.charAt(i);
            if (temp > 0 && temp < 127) {
                len += 0.5;
            } else {
                len++;
            }
        }
        return Math.round(len);
    }

    public void setLimitTextColor(TextView limitTextView, String content) {
        long length = calculateWeiboLength(content);
        if (length > TEXT_LIMIT) {
            long outOfNum = length - TEXT_LIMIT;
            limitTextView.setTextColor(Color.parseColor("#e03f22"));
            limitTextView.setText("-" + outOfNum + "");
        } else if (length == TEXT_LIMIT) {
            limitTextView.setText(0 + "");
            limitTextView.setTextColor(Color.parseColor("#929292"));
        } else if (TEXT_LIMIT - length <= TEXT_REMIND) {
            limitTextView.setText(TEXT_LIMIT - length + "");
            limitTextView.setTextColor(Color.parseColor("#929292"));
        } else {
            limitTextView.setText("");
        }
    }

    /**
     * 判断此页是处于转发微博还是发微博状态
     *
     * @return
     */
    public boolean isRetweetWeiBoState() {
        if (mStatus != null) {
            return true;
        } else {
            return false;
        }
    }


}
