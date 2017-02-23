package android.support.design.widget;

import android.content.Context;
import android.support.annotation.VisibleForTesting;
import android.support.v4.view.ScrollingView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Field;

public class AppBarFlingFixBehavior extends AppBarLayout.Behavior {
    private static final int MAX_OFFSET_ANIMATION_DURATION = 600; // ms

    private ValueAnimatorCompat mOffsetAnimator;
    private String TAG = getClass().getSimpleName();

    public AppBarFlingFixBehavior() {
    }

    public AppBarFlingFixBehavior(Context context, AttributeSet attrs) {

        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout parent, AppBarLayout child,
                                       View directTargetChild, View target, int nestedScrollAxes) {
        // Return true if we're nested scrolling vertically, and we have scrollable children
        // and the scrolling view is big enough to scroll
        final boolean started = super.onStartNestedScroll(parent,child,directTargetChild,target,nestedScrollAxes);
                /*(nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0
                && child.hasScrollableChildren()
                && parent.getHeight() - directTargetChild.getHeight() <= child.getHeight()*/;

        if (started && mOffsetAnimator != null) {
            // Cancel any offset animation
            mOffsetAnimator.cancel();
        }

//        setLastNestedScrollingChildRef(null);
        return started;
    }

    private void setLastNestedScrollingChildRef(Object o) {
        try {
            Field field = AppBarLayout.Behavior.class.getDeclaredField("mLastNestedScrollingChildRef");
            field.setAccessible(true);
            field.set(this, o);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onNestedFling(final CoordinatorLayout coordinatorLayout,
                                 final AppBarLayout child, View target, float velocityX, float velocityY,
                                 boolean consumed) {
        Log.d(TAG, "velocityY:"+velocityY);
        int targetTop = target.getTop();
        int barTop = child.getTop();
        Log.d(TAG, "target top:"+targetTop+",barTop:"+barTop);

        Log.d(TAG, "target:"+target.getClass().getSimpleName());
        boolean flung = false;

//        RecyclerView recyclerView = (RecyclerView) target;
//
//        Log.d(TAG, "Offset:"+recyclerView.computeVerticalScrollOffset()+",Extent:"+recyclerView.computeVerticalScrollExtent()
//        +",Range:"+recyclerView.computeVerticalScrollRange());
        Log.d(TAG, "consumed:"+consumed);
        Log.d(TAG, "TotalScroll:"+child.getTotalScrollRange());
        if (!consumed) {
//            flung = fling(coordinatorLayout, child, -child.getTotalScrollRange(), 0, -velocityY);
            if (velocityY < 0) {
                final int targetScroll = + child.getDownNestedPreScrollRange();
//                Log.d(TAG, "targetScroll:" +targetScroll);
                animateOffsetTo(coordinatorLayout, child, targetScroll, velocityY);
                flung = true;
            } else {
                final int targetScroll = -child.getUpNestedPreScrollRange();
//                Log.d(TAG, "targetScroll:" +targetScroll);
                if (getTopBottomOffsetForScrollingSibling() > targetScroll) {
                    animateOffsetTo(coordinatorLayout, child, targetScroll, velocityY);
                    flung = true;
                }
            }
        } else {

            if (velocityY < 0) {
                if (target instanceof ScrollingView) {
                    final int targetScroll = + child.getDownNestedPreScrollRange();
                    ScrollingView scrollingView = (ScrollingView) target;
                    int offset = scrollingView.computeVerticalScrollOffset();
                    if (offset <= 0) {
                        animateOffsetTo(coordinatorLayout, child, targetScroll, velocityY);
                        flung = true;
                    }
                }
            }
        }

        if (flung) {
            setWasNestedFlung(flung);
        }else {
            flung = super.onNestedFling(coordinatorLayout,child,target,velocityX,velocityY,consumed);
        }
        return flung;//
    }

    private void setWasNestedFlung(boolean o) {
        try {
            Field field = AppBarLayout.Behavior.class.getDeclaredField("mWasNestedFlung");
            field.setAccessible(true);
            field.set(this, o);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void animateOffsetTo(final CoordinatorLayout coordinatorLayout,
                                 final AppBarLayout child, final int offset, float velocity) {
        final int distance = Math.abs(getTopBottomOffsetForScrollingSibling() - offset);

        final int duration;
        velocity = Math.abs(velocity);
        if (velocity > 0) {
            duration = 3 * Math.round(1000 * (distance / velocity));
        } else {
            final float distanceRatio = (float) distance / child.getHeight();
            duration = (int) ((distanceRatio + 1) * 150);
        }

        animateOffsetWithDuration(coordinatorLayout, child, offset, duration);
    }

    private void animateOffsetWithDuration(final CoordinatorLayout coordinatorLayout,
                                           final AppBarLayout child, final int offset, final int duration) {
        final int currentOffset = getTopBottomOffsetForScrollingSibling();
        if (currentOffset == offset) {
            if (mOffsetAnimator != null && mOffsetAnimator.isRunning()) {
                mOffsetAnimator.cancel();
            }
            return;
        }

        if (mOffsetAnimator == null) {
            mOffsetAnimator = ViewUtils.createAnimator();
            mOffsetAnimator.setInterpolator(AnimationUtils.DECELERATE_INTERPOLATOR);
            mOffsetAnimator.addUpdateListener(new ValueAnimatorCompat.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimatorCompat animator) {
                    setHeaderTopBottomOffset(coordinatorLayout, child,
                            animator.getAnimatedIntValue());
                }
            });
        } else {
            mOffsetAnimator.cancel();
        }

        mOffsetAnimator.setDuration(Math.min(duration, MAX_OFFSET_ANIMATION_DURATION));
        mOffsetAnimator.setIntValues(currentOffset, offset);
        mOffsetAnimator.start();
    }

    @VisibleForTesting
    boolean isOffsetAnimatorRunning() {
        return mOffsetAnimator != null && mOffsetAnimator.isRunning();
    }

}