package cn.net.cc.weibo.ui.me;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.rey.material.widget.FloatingActionButton;

import cn.net.cc.weibo.R;
import cn.net.cc.weibo.ui.base.BaseFragment;
import cn.net.cc.weibo.imageLoad.ImageLoader;
import cn.net.cc.weibo.model.UserModel;
import cn.net.cc.weibo.post.PostService;
import cn.net.cc.weibo.post.idea.IdeaActivity;
import cn.net.cc.weibo.ui.relationship.RelationshipActivity;
import cn.net.cc.weibo.ui.user.UserWeiBoListFragment;
import cn.net.cc.weibo.util.NumberChangeUtil;
import cn.net.cc.weibo.util.UrlUtil;
import cn.net.cc.weibo.ui.weiboItem.AttitudeListFragment;
import cn.net.cc.weibo.ui.weiboItem.ILoadView;
import cn.net.cc.weibo.ui.weiboItem.WeiboItemDetailModel;
import cn.net.cc.weibo.ui.weiboItem.adapter.CommentPagerAdapter;
import openapi.models.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeFragment extends BaseFragment implements UserModel.UserRequestListener, ILoadView {

    public static final String  TAG = "UserFragment";
    public static final String  PARAM = "id";

    private static long DESTORY_VIEW_TIME;

    private static final long NEED_REFRESH_TIME = 6*1000;

    private ViewPager viewPager;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private CommentPagerAdapter adapter;
    private AppBarLayout appBarLayout;
    private SwipeRefreshLayout refreshLayout;
    private TextView info;
    private Button follows, likes;
    private ImageView icon,titleBg;

    private WeiboItemDetailModel model;

    private UserModel userModel;

    private String nameStr;
    
    public MeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserFragment.
     */
    public static MeFragment newInstance(String name) {
        MeFragment fragment = new MeFragment();
        Bundle args = new Bundle();
        args.putString(PARAM,name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            nameStr = getArguments().getString(PARAM);
        }
        Log.d(TAG,"name:"+nameStr);
        userModel = new UserModel(getContext(),this);
    }

    @Override
    public void fetchData() {
        boolean needRefresh = true;
        if(System.currentTimeMillis() - DESTORY_VIEW_TIME < NEED_REFRESH_TIME) {
            needRefresh = false;
        }
        final boolean finalNeedRefresh = needRefresh;
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
                userModel.show(nameStr, finalNeedRefresh);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        findView(view);
        initListener();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        DESTORY_VIEW_TIME = System.currentTimeMillis();
        setDataInitiated(false);
        Log.d(TAG,"onDestroyView,"+DESTORY_VIEW_TIME);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }

    @SuppressWarnings("ConstantConditions")
    private void findView(View view) {
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        appBarLayout = (AppBarLayout) view.findViewById(R.id.appbar);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        titleBg = (ImageView) view.findViewById(R.id.title_bg);
        icon = (ImageView) view.findViewById(R.id.icon);
        info = (TextView) view.findViewById(R.id.info);
        likes = (Button) view.findViewById(R.id.likes);
        follows = (Button) view.findViewById(R.id.follows);
        view.findViewById(R.id.button_bt_float_wave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), IdeaActivity.class);
                intent.putExtra(IdeaActivity.TYPE, PostService.POST_SERVICE_CREATE_WEIBO);
                startActivity(intent);
            }
        });
    }

    public void initAdapter(long uid){

        adapter = new CommentPagerAdapter(getFragmentManager());
        UserWeiBoListFragment userWeiBoListFragment = UserWeiBoListFragment.newInstance(uid);
        userWeiBoListFragment.setLoadView(this);
        adapter.addFragment(new AttitudeListFragment(), getString(R.string.user_fragment_main));
        adapter.addFragment(userWeiBoListFragment, getString(R.string.user_fragment_weibo));
        adapter.addFragment(new AttitudeListFragment(), getString(R.string.user_fragment_pic));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(1);
    }

    private void initListener() {
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                Log.d(TAG,"verticalOffset:"+verticalOffset);
                if (verticalOffset >= 0) {
                    refreshLayout.setEnabled(true);
                } else {
                    refreshLayout.setEnabled(false);
                }
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
        if (getActivity() == null) {
            return;
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
            ImageLoader.with(getContext(), titleBgUrl, titleBg);
            Log.d(TAG, "titleBgUrl:"+titleBgUrl+",before:"+titleBgUrls[0]);
        }else {
            titleBg.setImageResource(R.drawable.user_bg);
        }
        ImageLoader.with(getContext(), user.getAvatar_large(), icon);
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
            RelationshipActivity.startThis(getContext(), type, uid);
        }
    };
    
}
