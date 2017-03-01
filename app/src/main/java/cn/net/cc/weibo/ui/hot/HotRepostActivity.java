package cn.net.cc.weibo.ui.hot;

import android.support.v4.app.Fragment;
import android.view.Menu;

import cn.net.cc.weibo.R;
import cn.net.cc.weibo.ui.base.BaseFragmentActivity;
import cn.net.cc.weibo.ui.base.BaseListFragment;

public class HotRepostActivity extends BaseFragmentActivity {

    private static final String TAG = "HotRepostActivity";

    @Override
    protected void initView() {
        setBarTitle(R.string.hot_repost_daily);
        showFragment(R.id.repost_daily);
    }

    public Fragment getFragment(int id) {
        BaseListFragment fragment = null;
        switch (id) {
            case R.id.repost_daily:
                fragment = new HotRepostDailyFragment();
                break;
            case R.id.repost_weekly:
                fragment = new HotRepostWeeklyFragment();
                break;
        }
        fragment.setLoadView(this);
        return fragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.hot_repost_menu,menu);
        return true;
    }
}
