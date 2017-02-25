package cn.net.cc.weibo.relationship;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.Menu;

import cn.net.cc.weibo.R;
import cn.net.cc.weibo.base.BaseFragmentActivity;
import cn.net.cc.weibo.base.BaseListFragment;
import cn.net.cc.weibo.util.AccessTokenManager;

public class RelationshipActivity extends BaseFragmentActivity {

    private static final String TAG = "RelationshipActivity";

    private static final String TYPE = "title_type";
    private static final String UID = "uid";

    public static final int FOLLOW = R.string.follows;
    public static final int LIKE = R.string.likes;

    long uid;

    public static void startThis(Context context, int type, long uid) {
        Intent intent = new Intent(context,RelationshipActivity.class);
        intent.putExtra(TYPE,type);
        intent.putExtra(UID,uid);
        context.startActivity(intent);
    }

    @Override
    protected void initView() {
        int type = getIntent().getIntExtra(TYPE,R.string.likes);
        long defaultId = Long.parseLong(AccessTokenManager.getAccessToken(this).getUid());
        uid = getIntent().getLongExtra(UID, defaultId);
        setBarTitle(type);
        showFragment(type);
    }

    @Override
    public Fragment getFragment(int id) {
        BaseListFragment fragment = null;
        switch (id) {
            case R.string.likes:
                fragment = LikesFragment.newInstance(uid);
                break;
            case R.string.follows:
                fragment = FollowsFragment.newInstance(uid);
                break;
        }
        fragment.setLoadView(this);
        return fragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.relationship_menu, menu);
        return true;
    }

}
