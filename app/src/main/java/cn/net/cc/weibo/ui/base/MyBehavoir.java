package cn.net.cc.weibo.ui.base;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by chengkai on 2017/1/4.
 */
public class MyBehavoir extends AppBarLayout.Behavior {

    public MyBehavoir(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onNestedFling(final CoordinatorLayout coordinatorLayout,
                                 final AppBarLayout child, View target, float velocityX, float velocityY,
                                 boolean consumed) {
        Log.d(getClass().getSimpleName(),"velocityY:"+velocityY+",consumed:"+consumed);
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
    }

}
