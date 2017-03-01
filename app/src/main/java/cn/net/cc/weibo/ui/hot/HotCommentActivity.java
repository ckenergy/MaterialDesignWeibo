package cn.net.cc.weibo.ui.hot;

import android.support.v4.app.Fragment;
import android.view.Menu;

import cn.net.cc.weibo.R;
import cn.net.cc.weibo.ui.base.BaseFragmentActivity;
import cn.net.cc.weibo.ui.base.BaseListFragment;

public class HotCommentActivity extends BaseFragmentActivity {

    private static final String TAG = "HotCommentActivity";

    @Override
    protected void initView() {
        setBarTitle(R.string.hot_comments_daily);
        showFragment(R.id.comments_daily);
    }

    public Fragment getFragment(int id) {
        BaseListFragment fragment = null;
        switch (id) {
            case R.id.comments_daily:
                fragment = new HotCommentDailyFragment();
                break;
            case R.id.comments_weekly:
                fragment = new HotCommentWeeklyFragment();
                break;
        }
        fragment.setLoadView(this);
        return fragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.hot_comments_menu, menu);
        return true;
    }
}
