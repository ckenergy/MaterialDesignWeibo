package cn.net.cc.weibo.base;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import cn.net.cc.weibo.R;
import cn.net.cc.weibo.main.BaseFragmentManager;
import cn.net.cc.weibo.weiboItem.ILoadView;
import cn.net.cc.weibo.weiboItem.IRefresh;

public abstract class BaseFragmentActivity extends AppCompatActivity implements ILoadView {

    private static final String TAG = "MessageActivity";

    Toolbar toolbar;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basefragment);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);

        initView();
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Menu item click 的監聽事件一樣要設定在 setSupportActionBar 才有作用
        toolbar.setOnMenuItemClickListener(onMenuItemClick);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                if (fragmentManager.getCurrentFragment() instanceof IRefresh) {
                    IRefresh refresh = (IRefresh) fragmentManager.getCurrentFragment();
                    refresh.onRefresh();
                }
            }
        });

    }


    protected abstract void initView();

    public void setBarTitle(int id) {
        toolbar.setTitle(id);
    }

    public void setBarTitle(String title) {
        toolbar.setTitle(title);
    }

    public void showFragment(int id) {
        fragmentManager.show(id);
    }

    BaseFragmentManager fragmentManager = new BaseFragmentManager(getSupportFragmentManager(), R.id.content) {
        @Override
        public Fragment getFragment(int id) {
            return BaseFragmentActivity.this.getFragment(id);
        }
    };

    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            toolbar.setTitle(menuItem.getTitle());

            int itemId = menuItem.getItemId();
            fragmentManager.show(itemId);
            return true;
        }
    };

    protected abstract Fragment getFragment(int id) ;

    @Override
    public void setShowLoading(final boolean show) {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(show);
            }
        });
    }
}
