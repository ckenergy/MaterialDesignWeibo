package cn.net.cc.weibo.find;

import android.support.v4.app.Fragment;

import cn.net.cc.weibo.R;
import cn.net.cc.weibo.base.BaseFragmentActivity;
import cn.net.cc.weibo.base.BaseListFragment;

public class PublicActivity extends BaseFragmentActivity {

    private static final String TAG = "MessageActivity";

    @Override
    protected void initView() {
        setBarTitle(R.string.find_public);
        showFragment(R.id.public_);
    }

    @Override
    public Fragment getFragment(int id) {
        BaseListFragment fragment = null;
        switch (id) {
            case R.id.public_:
                fragment = new PublicWeiboFragment();
                break;
        }
        fragment.setLoadView(this);
        return fragment;
    }

}
