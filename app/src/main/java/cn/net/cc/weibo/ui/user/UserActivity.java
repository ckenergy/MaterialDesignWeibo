package cn.net.cc.weibo.ui.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ckenergy.transitionsview.transitions.BaseTransitionActivity;
import com.ckenergy.transitionsview.transitions.ITransferView;
import com.ckenergy.transitionsview.transitions.TransitionsHeleper;
import com.rey.material.widget.FloatingActionButton;

import cn.net.cc.weibo.R;
import cn.net.cc.weibo.imageLoad.ImageLoader;
import cn.net.cc.weibo.model.UserModel;
import cn.net.cc.weibo.ui.relationship.RelationshipActivity;
import cn.net.cc.weibo.util.NumberChangeUtil;
import cn.net.cc.weibo.util.UrlUtil;
import cn.net.cc.weibo.ui.weiboItem.AttitudeListFragment;
import cn.net.cc.weibo.ui.weiboItem.ILoadView;
import cn.net.cc.weibo.ui.weiboItem.WeiboItemDetailModel;
import cn.net.cc.weibo.ui.weiboItem.adapter.CommentPagerAdapter;
import openapi.models.User;

public class UserActivity extends BaseTransitionActivity implements UserModel.UserRequestListener, ILoadView{

    public static final String  TAG = "UserActivity";
    public static final String  PARAM = "id";
    public static final String ANIMATION_TYPE = "animation";

    public static final int TRANSFER = 1;

    private int animation_type;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private CommentPagerAdapter adapter;
    private AppBarLayout appBarLayout;
    private SwipeRefreshLayout refreshLayout;
    private TextView info;
    private Button follows, likes;
    private ImageView icon,titleBg;
    private FloatingActionButton floatButton;

    private WeiboItemDetailModel model;
    private UserModel userModel;

    private String nameStr;

    public static void startThis(Context context, String name) {
        Intent intent = new Intent(context, UserActivity.class);
        intent.putExtra(PARAM, name);
//        TransitionsHeleper.startActivity(context, intent, icon);
        context.startActivity(intent);
    }

    public static void startThis(View icon, String name) {
        Context context = icon.getContext();
        Intent intent = new Intent(context, UserActivity.class);
        intent.putExtra(PARAM, name);
        intent.putExtra(ANIMATION_TYPE, TRANSFER);
        TransitionsHeleper.startActivity(context, intent, icon);
//        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        findView();

        animation_type = getIntent().getIntExtra(ANIMATION_TYPE,0);

        if (animation_type == TRANSFER) {
            startMove(this, icon, new ITransferView.OnShowListener() {
                @Override public void onStart() {}

                @Override
                public void onEnd() {
                    load();
                }
            });
        }else {
            load();
        }

    }

    private void load() {
        nameStr = getIntent().getStringExtra(PARAM);
        Log.d(TAG,"id:"+nameStr);

        userModel = new UserModel(UserActivity.this, UserActivity.this);
        initListener();
    }

    @SuppressWarnings("ConstantConditions")
    private void findView() {
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        titleBg = (ImageView) findViewById(R.id.title_bg);
        icon = (ImageView) findViewById(R.id.icon);
        info = (TextView) findViewById(R.id.info);
        likes = (Button) findViewById(R.id.likes);
        follows = (Button) findViewById(R.id.follows);
        floatButton = (FloatingActionButton) findViewById(R.id.button_bt_float_wave);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public void initAdapter(long uid){
        adapter = new CommentPagerAdapter(getSupportFragmentManager());
        UserWeiBoListFragment userWeiBoListFragment = UserWeiBoListFragment.newInstance(uid);
        adapter.addFragment(new AttitudeListFragment(), getString(R.string.user_fragment_main));
        adapter.addFragment(userWeiBoListFragment, getString(R.string.user_fragment_weibo));
        adapter.addFragment(new AttitudeListFragment(), getString(R.string.user_fragment_pic));
        viewPager.setOffscreenPageLimit(adapter.getCount()-1);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(1);
    }

    private void initListener() {
        floatButton.setOnClickListener(listener);
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

    @Override
    public void setUser(User user) {
        toolbar.setTitle(user.screen_name);
        if (user.following) {
            floatButton.setLineMorphingState(1, true);
        }
        String friends = getString(R.string.likes)+ NumberChangeUtil.sizeChange(user.getFriends_count());
        String followers = getString(R.string.follows)+NumberChangeUtil.sizeChange(user.getFollowers_count());
        likes.setText(friends);
        follows.setText(followers);
        long uid = Long.parseLong(user.id);
        likes.setOnClickListener(new ToRelationshipOnClickListener(RelationshipActivity.LIKE, uid));
        follows.setOnClickListener(new ToRelationshipOnClickListener(RelationshipActivity.FOLLOW, uid));
        String infoStr;
        if (!TextUtils.isEmpty(user.getVerified_reason())) {
            infoStr = user.getVerified_reason();
        }else {
            infoStr = user.getDescription();
        }
        info.setText(infoStr);

        if (!TextUtils.isEmpty(user.getCover_image_phone())) {
            Log.d(TAG, "Cover_image_phone:"+user.getCover_image_phone());
            String[] titleBgUrls =  user.getCover_image_phone().split(";");
            String titleBgUrl = UrlUtil.userBg2whole(titleBgUrls[0]);
            Log.d(TAG, "titleBgUrl:"+titleBgUrl+",before:"+titleBgUrls[0]);
            ImageLoader.with(this, titleBgUrl, titleBg);
        }else {
            titleBg.setImageResource(R.drawable.user_bg);
        }
        if (animation_type != TRANSFER) {
            ImageLoader.with(this, user.getAvatar_large(), icon);
        }
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

    View.OnClickListener listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if(v instanceof FloatingActionButton){
                FloatingActionButton bt = (FloatingActionButton)v;
                bt.setLineMorphingState((bt.getLineMorphingState() + 1) % 2, true);
            }
        }
    };

    class ToRelationshipOnClickListener implements View.OnClickListener {

        int type;
        long uid;

        ToRelationshipOnClickListener(int type, long uid) {
            this.type = type;
            this.uid = uid;
        }

        @Override
        public void onClick(View v) {
            RelationshipActivity.startThis(UserActivity.this, type, uid);
        }
    }

}
