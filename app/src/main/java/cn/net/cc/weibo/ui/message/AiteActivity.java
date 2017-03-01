package cn.net.cc.weibo.ui.message;

import android.support.v4.app.Fragment;
import android.view.Menu;

import cn.net.cc.weibo.R;
import cn.net.cc.weibo.ui.base.BaseFragmentActivity;
import cn.net.cc.weibo.ui.base.BaseListFragment;

public class AiteActivity extends BaseFragmentActivity {

    private static final String TAG = "MessageActivity";

    @Override
    protected void initView() {
        setBarTitle(R.string.aite_item_weibo);
        showFragment(R.id.weibo);
    }

    @Override
    public Fragment getFragment(int id) {
        BaseListFragment fragment = null;
        switch (id) {
            case R.id.weibo:
                fragment = new AiteWeiboFragment();
                break;
            case R.id.comment:
                fragment = new AiteCommentsFragment();
                break;
        }
        fragment.setLoadView(this);
        return fragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.message_menu, menu);
        return true;
    }
}
