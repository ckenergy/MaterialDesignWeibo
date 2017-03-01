package cn.net.cc.weibo.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;

import cn.net.cc.weibo.R;

/**
 * Created by chengkai on 2016/8/4.
 */
public class FootView extends FrameLayout implements IFootView{

    private static final String TAG = "FootView";

    public static final String LOADING_MSG = "正在烘培...";
    public static final String PULLUP_LOAD_MSG = "上拉加载更多...";
    public static final String NO_MORE_MSG = "没有更多";
    //上拉加载更多
    public static final int  PULLUP_LOAD_MORE=0;
    //正在加载中
    public static final int  LOADING_MORE=1;
    //并没有最新微博
    public static final int  NO_MORE=2;

    private View view;
    private TextView messageTextview;
    private ProgressWheel progressBar;

    private OnLoadListener loadListener;

    private int state;

    public FootView(Context context) {
        this(context,null);
    }

    public FootView(Context context, AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public FootView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        view = inflate(context, R.layout.load_more,this);
        init();
    }

    private void init() {
        messageTextview = (TextView) view.findViewById(R.id.msg);
        progressBar = (ProgressWheel) view.findViewById(R.id.progress_bar);
    }

    @Override
    public void showState(int state) {
        this.state = state;
        switch (state){
            case PULLUP_LOAD_MORE:
                showPullLoad();
                break;
            case LOADING_MORE:
                showLoading();
                break;
            case NO_MORE:
                showNoMore();
                break;
        }
    }

    private void showNoMore() {
        progressBar.setVisibility(View.INVISIBLE);
        messageTextview.setText(NO_MORE_MSG);
    }

    private void hideLoading() {

    }

    public void showLoading() {
        Log.d(TAG,"showLoading");
        progressBar.setVisibility(View.VISIBLE);
        messageTextview.setText(LOADING_MSG);
        if (loadListener != null) {
            loadListener.onLoad();
        }
    }

    public void showPullLoad() {
        progressBar.setVisibility(View.INVISIBLE);
        messageTextview.setText(PULLUP_LOAD_MSG);
    }

    public OnLoadListener getOnLoadListener() {
        return loadListener;
    }

    public void setOnLoadListener(OnLoadListener loadListener) {
        this.loadListener = loadListener;
    }

    public interface OnLoadListener{

        public void onLoad();
    }
}
