package com.ckenergy.transitionsview.transitions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.HashMap;

public class TransitionsHeleper {

    private final String TAG = getClass().getSimpleName();

    public static final String TRANSITION_FLAG = "transition";

    public static final int TRANSITION_TYPE = 1;

    private static TransitionsHeleper INSTANCE;

    private static HashMap<String, MoveInfo> staticMap = new HashMap<>();

    private TransitionsHeleper() {
    }

    public static TransitionsHeleper getInstance() {
        if (INSTANCE == null) {
            synchronized (TransitionsHeleper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TransitionsHeleper();
                }
            }
        }
        return INSTANCE;
    }

    public static void startActivity(Context context, Intent intent, View view) {
        startActivity(context, null, intent, view);
    }

    public static void startActivity(final Context context, final Class<?> startActivity, final View view) {
        startActivity(context, startActivity, null, view);
    }

    private static void startActivity(final Context context, final Class<?> startActivity, Intent intent, final View view) {
        if (intent == null) {
            intent = new Intent(context, startActivity);
        }
        if (view == null || !(context instanceof Activity)) {
            context.startActivity(intent);
            return;
        }
//        intent.putExtra(TRANSITION_FLAG, TRANSITION_TYPE);
        final Rect rect = new Rect();
        //get statusBar height
        final MoveInfo bean = new MoveInfo();
        view.getWindowVisibleDisplayFrame(rect);
        bean.statusBarHeight = rect.top;
        final Activity activity = (Activity) context;
        final Intent finalIntent = intent;
        view.post(new Runnable() {
            @Override
            public void run() {
                view.getGlobalVisibleRect(rect);
                bean.originPoint.set(rect.left, rect.top);
                bean.originWidth = view.getWidth();
                bean.originHeight = view.getHeight();
                bean.bitmap = createBitmap(view, bean.originWidth, bean.originHeight, false);
                finalIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                staticMap.put(finalIntent.getComponent().getClassName(), bean);
                activity.startActivity(finalIntent);
                activity.overridePendingTransition(0, 0);
            }
        });
    }

    public boolean back(ITransferView moveMethod, final String tag, final ImageView targetView) {
        return back(moveMethod, tag, targetView, null);
    }

    public boolean back(ITransferView moveMethod, final String tag, final ImageView targetView, final ITransferView.OnShowListener listener) {
        final MoveInfo bean = staticMap.get(tag);
        if (bean == null || moveMethod == null) {
            return false;
        }
        targetView.setImageBitmap(null);

        int[] position = new int[2];
        targetView.getLocationOnScreen(position);
        Log.d(TAG,"top:"+position[1]+",left:"+position[0]);
        if (position[0] < -targetView.getWidth()) {
            position[0] = 0;
        }
        if (position[1] < -targetView.getHeight()) {
            position[1] = 0;
        }
        bean.targetPoint.set(position[0], position[1]);
        bean.translationY = -(bean.originPoint.y + (int) (bean.targetHeight * bean.scale) / 2
                - bean.targetPoint.y - bean.targetHeight / 2);
        bean.translationX = -(bean.originPoint.x + bean.originWidth / 2 - bean.targetPoint.x - bean.targetWidth / 2);
        bean.targetHeight = targetView.getHeight();
        bean.scale =  1.0f*bean.originHeight/bean.targetHeight;

        bean.moveLayout.setVisibility(View.VISIBLE);
        View moveView = bean.moveLayout.getChildAt(0);
        moveView.setTranslationX(bean.translationX);
        moveView.setTranslationY(bean.translationY);

        moveMethod.back(bean, moveView, new ITransferView.OnShowListener() {
            @Override
            public void onStart() {
                if (listener != null) {
                    listener.onStart();
                }
            }

            @Override
            public void onEnd() {
                if (listener != null) {
                    listener.onEnd();
                }
                unBind(tag);
            }
        });
        return true;
    }

    /*public boolean backMove(final ImageView imageView) {
        if (imageView == null) {
            return false;
        }
        if (!(imageView.getContext() instanceof Activity)) {
            return false;
        }
        final Activity activity = (Activity) imageView.getContext();
        IMoveMethod.OnShowListener back = new IMoveMethod.OnShowListener() {
            @Override public void onStart() {}

            @Override
            public void onEnd() {
                activity.finish();
                activity.overridePendingTransition(0,0);
            }
        };
        boolean isfinish = TransitionsHeleper.getInstance().back(this.getClass().getName(), imageView, back);

        return isfinish;
    }*/

    public void show(ITransferView moveMethod, final Activity activity, final ImageView targetView) {
        show(moveMethod, activity, targetView, null);
    }

    public void show(final ITransferView moveMethod, final Activity activity, final ImageView targetView, final ITransferView.OnShowListener listener) {
        if (targetView == null || activity == null) {
            listener.onEnd();
            return;
        }
        final MoveInfo bean = staticMap.get(activity.getClass().getName());
        if (bean == null || moveMethod == null) {
            listener.onEnd();
            return;
        }

        final ViewGroup parent = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (activity.getWindow().getStatusBarColor() == 0 ||
                    (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS & activity.getWindow().getAttributes().flags) != 0) {
                bean.statusBarHeight = 0;
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if ((WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS & activity.getWindow().getAttributes().flags) != 0) {
                bean.statusBarHeight = 0;
            }
        }
        targetView.post(new Runnable() {
            @Override
            public void run() {

                final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                final FrameLayout moveLayout = new FrameLayout(activity);
                parent.addView(moveLayout, params);
                Rect rect = new Rect();
                targetView.getGlobalVisibleRect(rect);
                bean.targetPoint.set(rect.left, rect.top);
                bean.targetWidth = rect.width();
                bean.targetHeight = rect.height();
                ImageView moveView = new ImageView(activity);
                if (bean.bitmap != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        moveView.setImageDrawable(new BitmapDrawable(bean.bitmap));
                    } else {
                        moveView.setBackgroundDrawable(new BitmapDrawable(bean.bitmap));
                    }
                }
                bean.scale =  1.0f*bean.originHeight/bean.targetHeight;

                FrameLayout.LayoutParams moveParams = new FrameLayout.LayoutParams(bean.originWidth, bean.originHeight);
                moveParams.setMargins(bean.originPoint.x, bean.originPoint.y-parent.getTop()-bean.statusBarHeight, 0, 0);

                moveLayout.addView(moveView,moveParams);
                bean.moveLayout = moveLayout;
                bean.translationY = -(bean.originPoint.y + (int) (bean.targetHeight * bean.scale) / 2
                        - bean.targetPoint.y - bean.targetHeight / 2);
                bean.translationX = -(bean.originPoint.x + bean.originWidth / 2 - bean.targetPoint.x - bean.targetWidth / 2);

                Log.d(TAG,bean.toString());

                float scale = bean.scale;

                moveView.setScaleX(scale);
                moveView.setScaleY(scale);
                moveView.setTranslationX(bean.translationX);
                moveView.setTranslationY(bean.translationY);

                moveMethod.start(bean, moveView, new ITransferView.OnShowListener() {
                    @Override
                    public void onStart() {
                        if (listener != null) {
                            listener.onStart();
                        }
                    }

                    @Override
                    public void onEnd() {
                        if (listener != null) {
                            listener.onEnd();
                        }
                        targetView.setImageBitmap(bean.bitmap);
                        moveLayout.setVisibility(View.GONE);
                    }
                });

            }
        });
    }

    public static void unBind(String tag) {
        if (staticMap.get(tag) != null) {
            MoveInfo bean = staticMap.get(tag);
            if (bean != null) {
                bean.bitmap = null;
                bean.moveLayout = null;
                staticMap.remove(tag);
            }
        }
    }


    private static Bitmap createBitmap(View view, int width, int height, boolean needOnLayout) {
        Bitmap bitmap = null;
        if (view != null) {
            view.clearFocus();
            view.setPressed(false);

            boolean willNotCache = view.willNotCacheDrawing();
            view.setWillNotCacheDrawing(false);

            // Reset the drawing cache background color to fully transparent
            // for the duration of this operation
            int color = view.getDrawingCacheBackgroundColor();
            view.setDrawingCacheBackgroundColor(0);
            float alpha = view.getAlpha();
            view.setAlpha(1.0f);

            if (color != 0) {
                view.destroyDrawingCache();
            }

            int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
            view.measure(widthSpec, heightSpec);
            if (needOnLayout) {
                view.layout(0, 0, width, height);
            }
            view.buildDrawingCache();
            Bitmap cacheBitmap = view.getDrawingCache();
            if (cacheBitmap == null) {
                Log.e("view.ProcessImageToBlur", "failed getViewBitmap(" + view + ")",
                        new RuntimeException());
                return null;
            }
            bitmap = Bitmap.createBitmap(cacheBitmap);
            // Restore the view
            view.setAlpha(alpha);
            view.destroyDrawingCache();
            view.setWillNotCacheDrawing(willNotCache);
            view.setDrawingCacheBackgroundColor(color);
        }
        return bitmap;
    }
}
