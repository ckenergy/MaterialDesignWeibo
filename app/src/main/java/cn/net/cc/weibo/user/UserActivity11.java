package cn.net.cc.weibo.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import cn.net.cc.weibo.R;
import cn.net.cc.weibo.imageLoad.ImageLoader;
import cn.net.cc.weibo.model.UserModel;
import cn.net.cc.weibo.weiboItem.AttitudeListFragment;
import cn.net.cc.weibo.weiboItem.ILoadView;
import cn.net.cc.weibo.weiboItem.WeiboItemDetailModel;
import cn.net.cc.weibo.weiboItem.adapter.CommentPagerAdapter;
import openapi.models.User;

public class UserActivity11 extends AppCompatActivity implements UserModel.UserRequestListener, ILoadView{

    public static final String  TAG = "UserActivity";
    public static final String  PARAM = "id";

    private ViewPager viewPager;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    CommentPagerAdapter adapter;
    AppBarLayout appBarLayout;
    SwipeRefreshLayout refreshLayout;

    WeiboItemDetailModel model;

    TextView info, follows, likes;
    ImageView icon, titleBg;

    UserModel userModel;

    String nameStr;

    public static void startThis(Context context, String name) {
        Intent intent = new Intent(context, UserActivity11.class);
        intent.putExtra(PARAM, name);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user11);

        findView();

        nameStr = getIntent().getStringExtra(PARAM);
        Log.d(TAG,"id:"+nameStr);

        userModel = new UserModel(this,this);
//        initAdapter();

        initListener();
    }

    @SuppressWarnings("ConstantConditions")
    private void findView() {
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
//        recyclerView = (RecyclerView) findViewById(R.id.list);
        titleBg = (ImageView) findViewById(R.id.title_bg);
        icon = (ImageView) findViewById(R.id.icon);
        info = (TextView) findViewById(R.id.info);
        likes = (TextView) findViewById(R.id.likes);
        follows = (TextView) findViewById(R.id.follows);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public void initAdapter(long uid){

        adapter = new CommentPagerAdapter(getSupportFragmentManager());
        UserWeiBoListFragment userWeiBoListFragment = UserWeiBoListFragment.newInstance(uid);
        adapter.addFragment(new AttitudeListFragment(), "主页");
        adapter.addFragment(userWeiBoListFragment, "微博");
        adapter.addFragment(new AttitudeListFragment(), "相册");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(1);
    }

    private void initListener() {

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                Log.d(TAG,"verticalOffset:"+verticalOffset);
//                Log.d(TAG,"getTotalScrollRange:"+appBarLayout.getTotalScrollRange());
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
                userModel.show(nameStr,true);
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                userModel.show(nameStr,true);
            }
        });
    }

    public static String sizeChange(int size) {
        float result = size / 10000;
        if(result < 0.1 ) {
            return String.valueOf(size);
        }
        if (result >= 100) {
            return String.format(Locale.CHINA,"%d万", (int)result);
        }
        return String.format(Locale.CHINA,"%.2f万", result);
    }

    @Override
    public void setUser(User user) {
        toolbar.setTitle(user.screen_name);
        String friends = getString(R.string.likes)+sizeChange(user.getFriends_count());
        String followers = getString(R.string.follows)+sizeChange(user.getFollowers_count());
        likes.setText(friends);
        follows.setText(followers);
        String infoStr;
        if (!TextUtils.isEmpty(user.getVerified_reason())) {
            infoStr = user.getVerified_reason();
        }else {
            infoStr = user.getDescription();
        }
        info.setText(infoStr);

        if (!TextUtils.isEmpty(user.getCover_image_phone())) {
            Log.d(TAG, "Cover_image_phone:"+user.getCover_image_phone());
            int index = user.getCover_image_phone().indexOf(";");
            String titleBgUrl = index > 0 ? user.getCover_image_phone().substring(0,index) : user.getCover_image_phone();
            ImageLoader.with(this, titleBgUrl, titleBg);
            Log.d(TAG, "titleBgUrl:"+titleBgUrl);
        }
        ImageLoader.with(this, user.getAvatar_large(), icon);
        long uid = Long.parseLong(user.id);
        initAdapter(uid);
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void setError(String error) {
        refreshLayout.setRefreshing(false);
        Log.d(TAG,error);
    }

    @Override
    public void setShowLoading(final boolean show) {
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(show);
            }
        });
    }
}
