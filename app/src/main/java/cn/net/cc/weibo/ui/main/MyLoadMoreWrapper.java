package cn.net.cc.weibo.ui.main;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.adapter.recyclerview.wrapper.LoadMoreWrapper;

import cn.net.cc.weibo.R;
import cn.net.cc.weibo.ui.view.FootView;

/**
 * Created by chengkai on 2016/8/29.
 */
public class MyLoadMoreWrapper extends LoadMoreWrapper {

    private static final String TAG = "MyLoadMoreWrapper";

    /**
     * 上拉加载更多
     */
    public static final int PULLUP_LOAD_MORE = 0;
    /**
     * 正在加载中
     */
    public static final int LOADING_MORE = 1;
    /**没有更多了*/
    public static final int NO_MORE_DATA = 2;
    /**
     * 记录下拉状态
     */
    int load_more_status = PULLUP_LOAD_MORE;

    boolean isLoading = false;

    FootView.OnLoadListener onLoadListener;

    public MyLoadMoreWrapper(Context context, RecyclerView.Adapter adapter) {
        super(adapter);
        setLoadMoreView(R.layout.footview);

    }

    public void setShowLoadView(boolean isShow) {
        int viewId = isShow ? R.layout.footview : 0;
        setLoadMoreView(viewId);
    }

    public void setFootMoreListener(FootView.OnLoadListener onLoadListener) {
        this.onLoadListener = onLoadListener;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Log.d(TAG, "position:" + position + ",count:" + getItemCount());
        if (position + 1 == getItemCount() && getItemViewType(position) == ITEM_TYPE_LOAD_MORE) {
            Log.d(TAG, "onBindViewHolder");
            ViewHolder viewHolder = (ViewHolder) holder;
            FootView footView = (FootView) viewHolder.getConvertView();
            footView.setOnLoadListener(onLoadListener);
            if (!isLoading) {
                footView.showState(load_more_status);
                if (load_more_status == LOADING_MORE) {
                    isLoading = true;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if (super.getItemCount() == 1 ) {
            return 0;
        }
        return super.getItemCount();
    }

    /**
     * adapter绑定recyclerView时调用方法
     * 在这里设置更多
     *
     * @param recyclerView
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        Log.d(TAG, "onAttachedToRecyclerView");
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d(TAG, "onScrollStateChanged");
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (lastVisibleItem + 1 == getItemCount()) {
                        changeMoreStatus(LOADING_MORE);
                    } else {
                        if (!isLoading) {
                            changeMoreStatus(PULLUP_LOAD_MORE);
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }
        });
    }

    public void loadingNoMore() {
        changeMoreStatus(NO_MORE_DATA);
        isLoading = false;
    }

    public void loadingMoreFinish() {
        changeMoreStatus(PULLUP_LOAD_MORE);
        isLoading = false;
    }

    /**
     * 上拉加载更多
     * PULLUP_LOAD_MORE=0;
     * <P>正在加载中</P>
     * LOADING_MORE=1
     * <p>加载完成已经没有更多数据了</p>
     * NO_MORE_DATA=2
     *
     * @param status
     */
    public void changeMoreStatus(int status) {
        load_more_status = status;
        notifyDataSetChanged();
    }

}
