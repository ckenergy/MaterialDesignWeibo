package cn.net.cc.weibo.base;

import android.content.Context;

import com.zhy.adapter.recyclerview.CommonAdapter;

import java.util.List;

import cn.net.cc.weibo.info.Constants;

/**
 * Created by chengkai on 2016/8/26.
 */
public abstract class BaseAdapter<T> extends CommonAdapter<T> implements IAdapterModel<T> {

    Context mContext;

    private boolean isShowLoad ;

    public BaseAdapter(Context context, int layoutId, List<T> datas) {
        super(context, layoutId, datas);
        this.mContext = context;
        isShowLoad = true;
    }

    public void addListNew(List<T> datas) {
        if (datas.size() < Constants.COUNT + 1) //此判断主要给weibolist，不适用comment retweet
            datas.addAll(mDatas);
        mDatas.clear();
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    public void addListMore(List<T> datas) {
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    public long getSinceId() {
        return getItemId(0);
    }

    public long getMaxId() {
        return getItemId(getItemCount()-1);
    }

    public Context getContext() {
        return mContext;
    }

    public List<T> getDatas() {
        return mDatas;
    }

    public boolean isShowLoad() {
        return isShowLoad;
    }

    public void setShowLoad(boolean showLoad) {
        isShowLoad = showLoad;
    }
}
