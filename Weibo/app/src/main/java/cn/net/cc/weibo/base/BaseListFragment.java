package cn.net.cc.weibo.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import cn.net.cc.weibo.R;
import cn.net.cc.weibo.friends.ILoadListModel;
import cn.net.cc.weibo.main.MyLoadMoreWrapper;
import cn.net.cc.weibo.view.FootView;
import cn.net.cc.weibo.weiboItem.ILoadView;
import cn.net.cc.weibo.weiboItem.IRefresh;

/**
 * Created by chengkai on 2016/9/19.
 */
public abstract class BaseListFragment<T> extends BaseFragment implements IListView<T>, IRefresh{

    private static final String TAG = "BaseListFragment";

    BaseAdapter adapter;

    MyLoadMoreWrapper loadMoreWrapper;
    RecyclerView recyclerView;

    ILoadView loadView;
    ILoadListModel model;

    boolean isDestory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        loadMoreWrapper = new MyLoadMoreWrapper(getContext(), adapter);
        loadMoreWrapper.setShowLoadView(adapter.isShowLoad());
        loadMoreWrapper.setFootMoreListener(new FootView.OnLoadListener() {
            @Override
            public void onLoad() {
                long maxid = adapter.getMaxId();
                model.loadListMore(0, maxid, 0);
            }
        });
    }

    protected abstract void init();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ILoadView) {
            loadView = (ILoadView) context;
            Log.d(TAG,"onAttach loadView");
        }
    }

    public void setLoadView(ILoadView loadView) {
        this.loadView = loadView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isDestory = true;
        Log.d(TAG,"onDestroyView");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view  = getContentView(inflater, container);
        isDestory = false;
        if (view == null) {
            Log.d(TAG,"view is null, use default");
            RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_cheese_list, container, false);
            setupRecyclerView(recyclerView);
            view = recyclerView;

        }

        return view;
    }

    protected View getContentView(LayoutInflater inflater, ViewGroup container) {
        return null;
    };

    protected void setupRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(loadMoreWrapper);
    }

    @Override
    public void setListEnd(final List<T> datas) {
        showMessage("获取微博信息流成功, 条数: " + datas.size());
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                adapter.addListMore(datas);
                loadMoreWrapper.loadingMoreFinish();
                loadView.setShowLoading(false);
            }
        });
    }

    @Override
    public void setListNew(final List<T> datas) {
        showMessage("获取微博信息流成功, 条数: " + datas.size());
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                adapter.addListNew(datas);
                loadMoreWrapper.notifyDataSetChanged();
                loadView.setShowLoading(false);
            }
        });
    }

    @Override
    public void setNoMore() {
        showMessage("没有更多");
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                loadMoreWrapper.loadingNoMore();
                loadView.setShowLoading(false);
            }
        });
    }

    protected void showMessage(String msg) {
        if (!isDestory) {
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void setError(String error) {
        showMessage(error);
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                loadMoreWrapper.loadingMoreFinish();
                loadView.setShowLoading(false);
            }
        });

    }

    @Override
    public void fetchData() {
        loadView.setShowLoading(true);
        model.loadListMore(0,0,0);
//        loadRefresh();
    }

    @Override
    public void onRefresh() {
        loadRefresh();
    }

    private void loadRefresh() {
        model.loadListNew(adapter.getSinceId(),0,0);
    }

    public void setAdapter(BaseAdapter adapter) {
        this.adapter = adapter;
    }

    public void setModel(ILoadListModel model) {
        this.model = model;
    }
}
