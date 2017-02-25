package cn.net.cc.weibo.message;

import android.support.v4.app.Fragment;
import android.view.Menu;

import cn.net.cc.weibo.R;
import cn.net.cc.weibo.base.BaseFragmentActivity;
import cn.net.cc.weibo.base.BaseListFragment;

public class MsgCommentActivity extends BaseFragmentActivity {

    private static final String TAG = "MsgCommentActivity";

    @Override
    protected void initView() {
        setBarTitle(R.string.comment_item_tome);
        showFragment(R.id.tome);
    }

    @Override
    public Fragment getFragment(int id) {
        BaseListFragment fragment = null;
        switch (id) {
            case R.id.tome:
                fragment = new MsgToMeFragment();
                break;
            case R.id.byme:
//                return new AttitudeListFragment();
                fragment = new MsgByMeFragment();
                break;
        }
        fragment.setLoadView(this);
        return fragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.comment_menu, menu);
        return true;
    }

}
