package cn.net.cc.weibo.ui.weiboItem;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import cn.net.cc.weibo.R;
import cn.net.cc.weibo.db.DatabaseManager;
import cn.net.cc.weibo.ui.friends.ImageGridAdapter;
import cn.net.cc.weibo.imageLoad.ImageLoader;
import cn.net.cc.weibo.ui.imageview.ImageActivity;
import cn.net.cc.weibo.post.PostService;
import cn.net.cc.weibo.post.idea.IdeaActivity;
import cn.net.cc.weibo.ui.user.UserActivity;
import cn.net.cc.weibo.util.DateUtil;
import cn.net.cc.weibo.util.NumberChangeUtil;
import cn.net.cc.weibo.ui.view.emojitextview.WeiBoContentTextUtil;
import cn.net.cc.weibo.ui.view.emojitextview.WeiBoTextClickListener;
import cn.net.cc.weibo.ui.weiboItem.adapter.CommentPagerAdapter;
import openapi.models.Status;
import openapi.models.User;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;


/*接口
1.更新评论，转发数
2.获取，更多评论
3.获取，更多转发
4.点赞
5.评论
6.转发*/
//comment bar
public class WeiboItemDetailActivity extends AppCompatActivity implements IWeiboItemDetailView, ILoadView {

    public static final String TAG = "WeiboItemActivity";
    public static final String PARAM_CONTENT = "content";
    public static final String PARAM_ID = "id";

    private ViewPager viewPager;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private CommentPagerAdapter adapter;
    private AppBarLayout appBarLayout;
    private SwipeRefreshLayout refreshLayout;

    private TextView name, contentTxt, from, retweetedContentTxt;
    private TextView time, retweetTime;
    private ImageView icon;
    private GridView contentImgs, retweetedContentImgs;
    private View retweetedView, container;
    private TabLayout.Tab retweetTitle, commentTitle, attitudeTitle;
    private Button retweet, comment;
    private CheckBox attitude;

    private WeiboItemDetailModel model;
    private Status status;
    private int columns = 3;
    private String id;

    public static void startThis(Context context, String id, Status status) {
        Intent intent = new Intent(context, WeiboItemDetailActivity.class);
        intent.putExtra(PARAM_ID, id);
        intent.putExtra(PARAM_CONTENT, status);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weibo_item);

        findView();

//        status = (Status) getIntent().getSerializableExtra(PARAM_CONTENT);
        id = getIntent().getStringExtra(PARAM_ID);
        status = DatabaseManager.getEntity(id, DatabaseManager.STATUS_DB);
        if (status == null) {
            status = (Status) getIntent().getSerializableExtra(PARAM_CONTENT);
        }
//        Log.d(TAG,status.text);
        if (status != null) {
//            id = status.id;
//            Log.d(TAG,"id:"+status.id);
            initAdapter();
            setContent();
            model = new WeiboItemDetailModel(this, this);
            initListener();
        }
    }

    private void findView() {
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        name = (TextView) findViewById(R.id.name);
        icon = (ImageView) findViewById(R.id.icon);
        from = (TextView) findViewById(R.id.from);
        time = (TextView) findViewById(R.id.time);
        retweetTime = (TextView) findViewById(R.id.retweet_time);

        contentTxt = (TextView) findViewById(R.id.content_txt);
        contentImgs = (GridView) findViewById(R.id.content_imgs);
        retweetedView = findViewById(R.id.retweeted_status);
        retweetedContentImgs = (GridView) findViewById(R.id.retweeted_content_imgs);
        retweetedContentTxt = (TextView) findViewById(R.id.retweeted_content_txt);
        retweet = (Button) findViewById(R.id.retweet);
        comment = (Button) findViewById(R.id.comment);
        attitude = (CheckBox) findViewById(R.id.attitude);
        findViewById(R.id.line).setVisibility(View.GONE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public void initAdapter() {
        adapter = new CommentPagerAdapter(getSupportFragmentManager());
        RetweetListFragment retweetListFragment = RetweetListFragment.newInstance(id);
        CommentListFragment commentListFragment = CommentListFragment.newInstance(id);
        adapter.addFragment(retweetListFragment, "转发");
        adapter.addFragment(commentListFragment, "评论");
        adapter.addFragment(new AttitudeListFragment(), "赞");
        viewPager.setOffscreenPageLimit(adapter.getCount() - 1);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(1);
        retweetTitle = tabLayout.getTabAt(0);
        commentTitle = tabLayout.getTabAt(1);
        attitudeTitle = tabLayout.getTabAt(2);
    }

    private void initListener() {

        View.OnClickListener toUser = new UserOnClickListener(icon);
        name.setOnClickListener(toUser);
        icon.setOnClickListener(toUser);
        comment.setOnClickListener(toComment);
        retweet.setOnClickListener(toRepost);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset >= 0) {
                    refreshLayout.setEnabled(true);
                } else {
                    refreshLayout.setEnabled(false);
                }
            }
        });
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "onRefresh");
                model.loadComment(new String[]{id});
                if (adapter.getItem(viewPager.getCurrentItem()) instanceof IRefresh) {
                    IRefresh refresh = (IRefresh) adapter.getItem(viewPager.getCurrentItem());
                    refresh.onRefresh();
                }
            }
        });
    }

    View.OnClickListener toRepost = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            IdeaActivity.startThis(WeiboItemDetailActivity.this, status, PostService.POST_SERVICE_REPOST_STATUS);
        }
    };

    View.OnClickListener toComment = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            IdeaActivity.startThis(WeiboItemDetailActivity.this, status, PostService.POST_SERVICE_COMMENT_STATUS);
        }
    };

    class UserOnClickListener implements View.OnClickListener {

        View icon;

        UserOnClickListener(View icon) {
            this.icon = icon;
        }

        @Override
        public void onClick(View view) {
            User user = status.user;
            if (user == null && !TextUtils.isEmpty(status.uid)) {
                user = status.getUser();//从数据库获取
            }
            if (user != null) {
                UserActivity.startThis(icon, user.getName());
            }
        }
    }

    ;

    private void setContent() {
        User user = status.user;
        if (user == null && !TextUtils.isEmpty(status.uid)) {
            user = status.getUser();//从数据库获取
        }
        if (user != null) {
            name.setText(user.screen_name);
            ImageLoader.with(this, user.avatar_large, icon);
        }
        setCommentText(status.reposts_count, status.comments_count, status.attitudes_count);
        if (!TextUtils.isEmpty(status.source)) {
            from.setText(Html.fromHtml(status.source));
        }
        setWeiboContent(status);
        Status retweet_status = status.retweeted_status;
        if (retweet_status == null && !TextUtils.isEmpty(status.retweetedID)) {
            retweet_status = status.getRetweeted_status();
        }
        if (retweet_status != null) {
            retweetedView.setVisibility(View.VISIBLE);
            setWeiboRetweetContent(retweet_status);
            final Status finalRetweet_status = retweet_status;
            retweetedView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WeiboItemDetailActivity.startThis(WeiboItemDetailActivity.this, finalRetweet_status.id, finalRetweet_status);
                }
            });
        } else {
            retweetedView.setVisibility(View.GONE);
        }
    }

    private void setWeiboContent(Status status) {
        if (!TextUtils.isEmpty(status.created_at)) {
            time.setText(DateUtil.dateFormat(status.created_at));
        }
        setContentTxt(status.text, this, contentTxt);
        setImageList(contentImgs, status);
    }

    public void setContentTxt(String content, Context context, TextView textView) {
        textView.setText(WeiBoContentTextUtil.getWeiBoContent(content, context, textView, new WeiBoTextClickListener(context)));
    }

    private void setWeiboRetweetContent(Status status) {
        String contentTxt;
        if (status.user == null) {
            contentTxt = "微博已经被删除";
        } else {
            contentTxt = "@" + status.user.screen_name + " " + status.text;
        }
        if (!TextUtils.isEmpty(status.created_at)) {
            retweetTime.setText(DateUtil.dateFormat(status.created_at));
        }
        setContentTxt(contentTxt, this, retweetedContentTxt);
        setImageList(retweetedContentImgs, status);
    }

    private void setImageList(GridView gridView, Status status) {
        if (status.getPic_urlStr() != null && status.getPic_urlStr().size() > 0) {
            gridView.setVisibility(View.VISIBLE);
            ArrayList<String> urls = status.getPic_urlStr();
            if (urls.size() == 1) {
                gridView.setNumColumns(1);
            } else {
                gridView.setNumColumns(3);
            }
            ImageGridAdapter adapter = new ImageGridAdapter(this, status.getPic_urlStr());
            adapter.setOnItemClickListener(onItemClickListener);
            gridView.setAdapter(adapter);
        } else {
            gridView.setVisibility(View.GONE);
        }
    }

    ImageGridAdapter.OnItemClickListener onItemClickListener = new ImageGridAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {
            Intent intent = new Intent(WeiboItemDetailActivity.this, ImageActivity.class);
            ArrayList<String> urls = status.getPic_urlStr();
            if (urls == null) {
                urls = status.retweeted_status.getPic_urlStr();
            }
            intent.putExtra(ImageActivity.INDEX_PARAM, position);
            intent.putStringArrayListExtra(ImageActivity.URLS_PARAM, urls);
            startActivity(intent);
        }
    };

    private void setCommentText(int retweet, int comment, int attitude) {
        retweetTitle.setText(getString(R.string.retweet_number, NumberChangeUtil.sizeChange(retweet)));
        commentTitle.setText(getString(R.string.comment_number, NumberChangeUtil.sizeChange(comment)));
        attitudeTitle.setText(getString(R.string.attitude_number, NumberChangeUtil.sizeChange(attitude)));
    }

    @Override
    public void setComment(int retweet, int comment, int attitude) {
        setCommentText(retweet, comment, attitude);
    }

    @Override
    public void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    Subscriber<? super Integer> subscriber1;

    Action1<Integer> subscribe = new Action1<Integer>() {
        @Override
        public void call(Integer o) {
            model.loadComment(new String[]{id});
            Log.d(TAG, "loadComment");
        }
    };

    Subscription sub = Observable.create(new Observable.OnSubscribe<Integer>() {
        @Override
        public void call(Subscriber<? super Integer> subscriber) {
            subscriber1 = subscriber;
        }
    }).throttleFirst(10, TimeUnit.SECONDS).subscribe(subscribe);

    @Override
    public void setShowLoading(boolean show) {
//        RxView.clicks(null).throttleFirst()
        if (show && subscriber1 != null && !subscriber1.isUnsubscribed()) subscriber1.onNext(null);
        showRefresh(show);
        Log.d(TAG, "show:" + show);
    }

    private void showRefresh(final boolean show) {
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (show) {
                    if (!refreshLayout.isRefreshing()) {
                        refreshLayout.setRefreshing(true);
                    }
                } else {
                    refreshLayout.setRefreshing(false);
                }
            }
        });
    }
}
