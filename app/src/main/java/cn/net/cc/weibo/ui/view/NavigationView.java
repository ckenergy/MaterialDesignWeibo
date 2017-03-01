package cn.net.cc.weibo.ui.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import cn.net.cc.weibo.R;

/**
 * Created by chengkai on 2016/7/21.
 */
public class NavigationView extends FrameLayout {

    private static final String TAG = "NavigationView";

    private static final int TEME_DURATION = 200;

    /**上一次点击的点*/
    private int rdBeforeIndex = 0;
    /**每一次点击的距离长度*/
    private int length = 0;
    /**滑动起点*/
    private int toX;
    /**滑动终点*/
    private int formX;
    /**scaleAni2L.isRunning 期间总共点击了多远*/
    private int total = 0;

    private OnPageChangeListener mPageChangerListener;
    private ViewPager mViewPager;
    private ObjectAnimator scaleAni2Large;
    private AnimatorSet animSet;
    private ObjectAnimator scaleAni2Small;

    private View titleLine;

    private RadioGroup navigationBottom;

    public NavigationView(Context context) {
        this(context,null);
    }

    public NavigationView(Context context, AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public NavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private int getIdIndex(ViewGroup viewGroup, int id) {
        View view = findViewById(id);
        return viewGroup.indexOfChild(view);
    }

    private void init() {
        View view = inflate(getContext(), R.layout.navigation_bar, this);
        titleLine = findViewById(R.id.title_line);
        navigationBottom = (RadioGroup) findViewById(R.id.navigation_bottom);
        scaleAni2Small = ObjectAnimator.ofFloat(titleLine, "scaleX", 2, 1);
//        findViewById(R.id.friends).setSelected(true);

        navigationBottom.check(R.id.friends);
//        findViewById(R.id.friends).setOnClickListener(buttonChange);
//        findViewById(R.id.message).setOnClickListener(buttonChange);
//        findViewById(R.id.find).setOnClickListener(buttonChange);
//        findViewById(R.id.me).setOnClickListener(buttonChange);

        /*scaleAni2Small.addListener(new Animator.AnimatorListener() {
                    @Override public void onAnimationStart(Animator animation) {}

                    @Override public void onAnimationEnd(Animator animation) {
                        needFixAnimation = false;
                    }

                    @Override public void onAnimationCancel(Animator animation) {}

                    @Override public void onAnimationRepeat(Animator animation) {}
                });*/
        navigationBottom.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d(TAG, "checkedId:"+checkedId);
                int index = getIdIndex(group, checkedId);
                if (index == rdBeforeIndex) {
                    return;
                }
                if (mViewPager != null) {
                    mViewPager.setCurrentItem(index);
                }
                Log.d(TAG, "index:" + index);

                length = index - rdBeforeIndex;
                final int titleWidth = titleLine.getWidth();
                if (scaleAni2Large == null ) {
                    scaleAni2Large = ObjectAnimator.ofFloat(titleLine, "scaleX", 1, 2).setDuration(TEME_DURATION*2);
                    scaleAni2Large.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {}
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            int x = (int) titleLine.getX();
                            Log.d(TAG,"onAnimationEnd:"+x);
                            int width = titleWidth * total;
                            total = 0;
                            formX = x;
                            toX = toX+width;
                            ObjectAnimator scaleAniTranslateX = ObjectAnimator.ofFloat(titleLine, "x", formX, toX);
                            if (animSet == null) {
                                animSet = new AnimatorSet();
                                animSet.setDuration(TEME_DURATION);
                            }
                            animSet.play(scaleAniTranslateX).with(scaleAni2Small);
                            animSet.start();
                        }
                        @Override
                        public void onAnimationCancel(Animator animation) {}
                        @Override
                        public void onAnimationRepeat(Animator animation) {}
                    });
                }
                if (!scaleAni2Large.isRunning()) {
                    titleLine.setPivotX(0);
                    if (index < rdBeforeIndex) {
                        titleLine.setPivotX(titleWidth);
                    }
                    scaleAni2Large.start();
                }
                rdBeforeIndex = index;
                total += length;

            }
        });
    }

//    boolean needFixAnimation = false;

    /*OnClickListener buttonChange = new OnClickListener() {
        @Override
        public void onClick(View view) {
//            int checkedId = view.getId();
//            Log.d(TAG, "checkedId:"+checkedId);
            needFixAnimation = true;
            int index = navigationBottom.indexOfChild(view);//getIdIndex(navigationBottom, checkedId);
            if (index == rdBeforeIndex) {
                return;
            }
            if (mViewPager != null) {
                mViewPager.setCurrentItem(index);
            }
            Log.d(TAG, "index:" + index);

            length = index - rdBeforeIndex;
            final int titleWidth = titleLine.getWidth();
            if (scaleAni2Large == null ) {
                scaleAni2Large = ObjectAnimator.ofFloat(titleLine, "scaleX", 1, 2).setDuration(TEME_DURATION*2);
                scaleAni2Large.addListener(new Animator.AnimatorListener() {
                    @Override public void onAnimationStart(Animator animation) {}
                    @Override public void onAnimationEnd(Animator animation) {
                        int x = (int) titleLine.getTranslationX();
                        Log.d(TAG,"onAnimationEnd:"+x);
                        int width = titleWidth * total;
                        total = 0;
                        formX = x;
                        toX = toX+width;
                        ObjectAnimator scaleAniTranslateX = ObjectAnimator.ofFloat(titleLine, "x", formX, toX);
                        if (animSet == null) {
                            animSet = new AnimatorSet();
                            animSet.setDuration(TEME_DURATION);
                        }
                        animSet.play(scaleAniTranslateX).with(scaleAni2Small);
                        animSet.start();
                    }
                    @Override public void onAnimationCancel(Animator animation) {}
                    @Override public void onAnimationRepeat(Animator animation) {}
                });
            }
            if (!scaleAni2Large.isRunning()) {
                titleLine.setPivotX(0);
                if (index < rdBeforeIndex) {
                    titleLine.setPivotX(titleWidth);
                }
                scaleAni2Large.start();
            }
            rdBeforeIndex = index;
            total += length;


        }
    };*/

    ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (mPageChangerListener != null) {
                mPageChangerListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            /*if (!needFixAnimation) {
                final int titleWidth = titleLine.getWidth();
                int x = (int) titleLine.getX();
                float offset = titleWidth*(positionOffset+position);
                titleLine.setTranslationX(offset);
            }*/
            Log.d(TAG,"onPageScrolled,position:"+position+",positionOffset:+"+positionOffset+",positionOffsetPixels:"+positionOffsetPixels);
        }

        @Override
        public void onPageSelected(int position) {
            if (mPageChangerListener != null) {
                mPageChangerListener.onPageSelected(position);
            }
            if (position != rdBeforeIndex) {
                checked(position);
//                rdBeforeIndex = position;
            }
            Log.d(TAG,"onPageSelected,index:"+position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (mPageChangerListener != null) {
                mPageChangerListener.onPageScrollStateChanged(state);
            }
            Log.d(TAG,"State:"+state);
        }
    };

    public void checked(int index) {
        /*for (int i=0; i<navigationBottom.getChildCount(); i++) {
            navigationBottom.getChildAt(i).setSelected(false);
        }
        navigationBottom.getChildAt(index).setSelected(true);*/
        int id = navigationBottom.getChildAt(index).getId();
        navigationBottom.check(id);
    }

    public void setViewPager(ViewPager viewPager) {
        this.mViewPager = viewPager;
        mViewPager.addOnPageChangeListener(mPageChangeListener);
    }

    public void setPageChangerListener(OnPageChangeListener pageChangerListener) {
        this.mPageChangerListener = pageChangerListener;
    }

    public interface OnPageChangeListener {
        public void onPageScrolled(int i, float v, int i1) ;

        public void onPageSelected(int i) ;

        public void onPageScrollStateChanged(int i) ;
    }

}
