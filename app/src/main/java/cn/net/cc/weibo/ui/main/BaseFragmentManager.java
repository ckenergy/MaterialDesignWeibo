package cn.net.cc.weibo.ui.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

/**
 * Created by chengkai on 2016/10/20.
 */
public abstract class BaseFragmentManager {

    private static final String TAG = "BaseFragmentManager";

    private FragmentTransaction transaction;
    private FragmentManager fragmentManager;

    private Fragment currentFragment;

    private int containerId;

    public BaseFragmentManager(FragmentManager fragmentManager, int containerId) {
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
    }

    public void show(int fragmentId) {
        transaction = fragmentManager.beginTransaction();
        String name = makeFragmentName(containerId, fragmentId);
        Fragment fragment = fragmentManager.findFragmentByTag(name);
        if (fragment != null) {
            Log.v(TAG, "Attaching item #" + fragmentId + ": f=" + fragment);
            transaction.attach(fragment);
        } else {
            fragment = getFragment(fragmentId);
            Log.v(TAG, "Adding item #" + fragmentId + ": f=" + fragment);
            transaction.add(containerId, fragment, makeFragmentName(containerId, fragmentId));
        }

        if (fragment != currentFragment) {
            if (currentFragment != null) {
                currentFragment.setMenuVisibility(false);
                currentFragment.setUserVisibleHint(false);
                transaction.detach(currentFragment);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            currentFragment = fragment;
        }
        transaction.commitNowAllowingStateLoss();
    }

    public Fragment getCurrentFragment() {
        return currentFragment;
    }

    private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }

    public abstract Fragment getFragment(int id);
}
