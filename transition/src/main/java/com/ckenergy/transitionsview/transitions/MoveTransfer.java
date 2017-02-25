package com.ckenergy.transitionsview.transitions;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

public class MoveTransfer implements ITransferView {

    private final String TAG = getClass().getSimpleName();

    private long duration;

    private AnimatorSet startSet;

    private ObjectAnimator translationX;
    private ObjectAnimator translationY;
    private ObjectAnimator scaleX;
    private ObjectAnimator scaleY;

    private Animator.AnimatorListener startListener;

    public MoveTransfer() {
        duration = 300;
    }

    @Override
    public void start(MoveInfo bean, View child, final OnShowListener listener) {
        translationX = ObjectAnimator.ofFloat(child, "translationX", 0, bean.translationX);
        translationY = ObjectAnimator.ofFloat(child, "translationY", 0, bean.translationY);
        scaleX = ObjectAnimator.ofFloat(child, "scaleX", 1, 1.0f/bean.scale);
        scaleY = ObjectAnimator.ofFloat(child, "scaleY", 1, 1.0f/bean.scale);
        translationY.setInterpolator(new AccelerateInterpolator());
        List<Animator> animators = new ArrayList<>();
        animators.add(translationX);
        animators.add(translationY);
        animators.add(scaleX);
        animators.add(scaleY);
        startSet = new AnimatorSet();
        startSet.playTogether(animators);
        if (listener != null) {
            startListener = new Animator.AnimatorListener() {
                boolean isCancel;
                @Override public void onAnimationStart(Animator animation) {
                    isCancel = false;
                    listener.onStart();
                }

                @Override public void onAnimationEnd(Animator animation) {
                    if (!isCancel) {
                        listener.onEnd();
                    }
                }

                @Override public void onAnimationCancel(Animator animation) {
                    isCancel = true;
                }
                @Override public void onAnimationRepeat(Animator animation) {}
            };
            startSet.addListener(startListener);
        }
        startSet.setDuration(duration).start();
    }

    public void back(MoveInfo bean, View child, final OnShowListener listener) {
        if (startSet == null) {
            return;
        }
        float x = (Float) translationX.getAnimatedValue();
        float y = (Float) translationY.getAnimatedValue();
        Float scaleXF = (Float) scaleX.getAnimatedValue();
        Float scaleYF = (Float) scaleY.getAnimatedValue();
        if (!startSet.isRunning()) {
            x = bean.translationX;
            y = bean.translationY;
            scaleXF = scaleYF = 1.0f/bean.scale;
        }
        startSet.cancel();

        translationY.setInterpolator(new DecelerateInterpolator());
        translationX.setFloatValues(x, 0);
        translationY.setFloatValues(y, 0);
        scaleX.setFloatValues(scaleXF, 1);
        scaleY.setFloatValues(scaleYF, 1);

        startSet.removeListener(startListener);
        if (listener != null) {
            startSet.addListener(new Animator.AnimatorListener() {
                @Override public void onAnimationStart(Animator animation) {
                    listener.onStart();
                }

                @Override public void onAnimationEnd(Animator animation) {
                    listener.onEnd();
                }
                @Override public void onAnimationCancel(Animator animation) {}
                @Override public void onAnimationRepeat(Animator animation) {}
            });
        }
        long durationEnd =  (long)(duration*translationX.getAnimatedFraction());
        startSet.setDuration(durationEnd).start();
    }

}
