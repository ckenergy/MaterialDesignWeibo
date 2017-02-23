package cn.net.cc.weibo.friends;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import cn.net.cc.weibo.R;
import cn.net.cc.weibo.base.BaseAdapter;
import cn.net.cc.weibo.imageLoad.ImageLoader;
import cn.net.cc.weibo.post.PostService;
import cn.net.cc.weibo.post.idea.IdeaActivity;
import cn.net.cc.weibo.user.UserActivity;
import cn.net.cc.weibo.util.DateUtil;
import cn.net.cc.weibo.util.NumberChangeUtil;
import cn.net.cc.weibo.view.emojitextview.WeiBoContentTextUtil;
import cn.net.cc.weibo.view.emojitextview.WeiBoTextClickListener;
import openapi.models.Status;
import openapi.models.User;

/**
 * Created by chengkai on 2016/9/19.
 */
public class WeiboItemAdapter extends BaseAdapter<Status> {

    private static final String TAG = "WeiboItemAdapter";

    Context mContext;

    int columns = 3;

    boolean isShowUser = true;

    OnImageItemClickListener mImageItemClickListener;
    OnItemClickListener mItemClickListener;

    public WeiboItemAdapter(Context context, List<Status> datas) {
        this(context, R.layout.item_weibo, datas);
    }

    public WeiboItemAdapter(Context context, int layoutId, List<Status> datas) {
        super(context, layoutId, datas);
        this.mContext = context;
    }

    public boolean isShowUser() {
        return isShowUser;
    }

    public void setShowUser(boolean showUser) {
        isShowUser = showUser;
    }

    @Override
    protected void convert(final ViewHolder holder, final Status status, int position) {
        Log.d(TAG, "onBindViewHolder:"+status.id +status.created_at);
        showUser(status, holder);
        com.rey.material.widget.Button retweet = holder.getView(R.id.retweet);
        com.rey.material.widget.Button comment = holder.getView(R.id.comment);
        com.rey.material.widget.Button attitude = holder.getView(R.id.attitude);
        retweet.setText(NumberChangeUtil.sizeChange(status.reposts_count));
        comment.setText(NumberChangeUtil.sizeChange(status.comments_count));
        attitude.setText(NumberChangeUtil.sizeChange(status.attitudes_count));
        Drawable retweeDdrawable = mContext.getResources().getDrawable(R.drawable.bar_retweet);
        Drawable commentDdrawable = mContext.getResources().getDrawable(R.drawable.bar_comment);
        Drawable likeDdrawable = mContext.getResources().getDrawable(R.drawable.bar_like);
        int width = mContext.getResources().getDimensionPixelSize(R.dimen.bar_drawable_size);
        int left = 0;
        int top = 0;
        retweeDdrawable.setBounds(left,top,left+width,top+width);
        retweet.setCompoundDrawables(retweeDdrawable,null,null,null);
        int commentTop = top+5;
        commentDdrawable.setBounds(left,commentTop,left+width,commentTop+width);
        comment.setCompoundDrawables(commentDdrawable,null,null,null);
        likeDdrawable.setBounds(left,top,left+width,top+width);
        attitude.setCompoundDrawables(likeDdrawable,null,null,null);
        TextView from = holder.getView(R.id.from);
        if(!TextUtils.isEmpty(status.source)) {
            from.setText(Html.fromHtml(status.source));
        }
        ItemClickListener itemClickListener = new ItemClickListener(position);
        setDetailListener(holder.getConvertView(), status, itemClickListener);

        retweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IdeaActivity.startThis(mContext, status, PostService.POST_SERVICE_REPOST_STATUS);
            }
        });
        setDetailListener(comment, status, itemClickListener);
        setDetailListener(attitude, status, itemClickListener);

        setWeiboContent(holder, status,position);
        Status retweet_status = status.retweeted_status;
        if (retweet_status == null && !TextUtils.isEmpty(status.retweetedID)) {
            retweet_status = status.getRetweeted_status();
        }
        View retweetedView = holder.getView(R.id.retweeted_status);
        if (retweet_status != null) {
            retweetedView.setVisibility(View.VISIBLE);
            setDetailListener(retweetedView, retweet_status, itemClickListener);
            setWeiboRetweetContent(holder, retweet_status, position);
        }else {
            retweetedView.setVisibility(View.GONE);
        }
    }

    private void setDetailListener(View view, Status status, View.OnClickListener itemClickListener) {
        view.setTag(status);
        view.setOnClickListener(itemClickListener);
    }

    private void showUser(Status status, ViewHolder holder) {
        View userView = holder.getView(R.id.user_view);
        if (isShowUser) {
            userView.setVisibility(View.VISIBLE);
            User user = getUser(status);
            if (user != null) {
                TextView name = holder.getView(R.id.name);
                name.setText(user.screen_name);
                ImageView icon = holder.getView(R.id.icon);
                ImageLoader.with(getContext(), user.avatar_large, icon);
                name.setTag(user.screen_name);
                icon.setTag(user.screen_name);
                View.OnClickListener toUser = new UserOnClickListener(icon);
                name.setOnClickListener(toUser);
                icon.setOnClickListener(toUser);
            }
        }else {
            userView.setVisibility(View.GONE);
        }
    }

    private void setWeiboContent(ViewHolder holder, Status status, final int listPosition) {
        TextView contentTxt = holder.getView(R.id.content_txt);
        TextView time = holder.getView(R.id.time);
        if (!TextUtils.isEmpty(status.created_at)) {
            time.setText(DateUtil.dateFormat(status.created_at));
        }
        setContentTxt(status.text, mContext, contentTxt);
        GridView contentImgs = holder.getView(R.id.content_imgs);
        setImagesList(contentImgs,status,listPosition);
    }

    private User getUser(Status status) {
        User user = status.user;
        if (user == null && !TextUtils.isEmpty(status.uid)) {
            user = status.getUser();//从数据库获取
        }
        return user;
    }

    public void setContentTxt(String content, Context context, TextView textView) {
        textView.setText(WeiBoContentTextUtil.getWeiBoContent(content, context, textView, new WeiBoTextClickListener(context)));
    }

    private void setWeiboRetweetContent(ViewHolder holder, final Status status, final int listPosition) {
        String contentTxt;
        User user = getUser(status);
        if (user == null) {
            contentTxt = "微博已经被删除";
        }else {
            contentTxt = "@"+status.user.screen_name +" "+ status.text;
        }
        TextView time = holder.getView(R.id.retweet_time);
        if (!TextUtils.isEmpty(status.created_at)) {
            time.setText(DateUtil.dateFormat(status.created_at));
        }
        TextView retweetedContentTxt = holder.getView(R.id.retweeted_content_txt);
        setContentTxt(contentTxt, mContext, retweetedContentTxt);
        GridView retweetedContentImgs = holder.getView(R.id.retweeted_content_imgs);
        setImagesList(retweetedContentImgs, status, listPosition);
    }

    private void setImagesList(GridView gridView, Status status, final int listPosition) {
        if (status.pic_urlStr != null)
            Log.d(TAG,"urlStr size"+status.pic_urlStr.size());
        if (status.pic_urls != null)
            Log.d(TAG,"size"+status.pic_urls.size());
        if (status.getPic_urlStr() != null && status.getPic_urlStr().size() > 0) {
            gridView.setVisibility(View.VISIBLE);
            ArrayList<String> urls = status.getPic_urlStr();
            if (urls.size() == 1) {
                gridView.setNumColumns(1);
            }else {
                gridView.setNumColumns(3);
            }
            ImageGridAdapter adapter = new ImageGridAdapter(mContext,urls);
            gridView.setAdapter(adapter);
            adapter.setOnItemClickListener(new ImageGridAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    if (mImageItemClickListener != null) {
                        mImageItemClickListener.onItemClick(v, listPosition, position);
                    }
                }
            });
            gridView.setTag(status);
            gridView.setOnTouchListener(new RecyclerViewListener(listPosition));
        }else {
            gridView.setVisibility(View.GONE);
        }
    }

    class UserOnClickListener implements View.OnClickListener {

        View icon;
        public UserOnClickListener(View icon) {
            this.icon = icon;
        }

        @Override
        public void onClick(View view) {
            String name = (String) view.getTag();
            UserActivity.startThis(icon, name);
        }
    };

    class RecyclerViewListener implements View.OnTouchListener {

        int listPosition;
        RecyclerViewListener(int listPosition) {
            this.listPosition = listPosition;
        }

        int downX= -1,downY=-1;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downX = (int) motionEvent.getX();
                    downY = (int) motionEvent.getY();
                    Log.d(TAG, "downX:"+downX+",downY:"+downY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.d(TAG, "MotionEvent.ACTION_MOVE");
                    break;
                case MotionEvent.ACTION_UP:
                    int currentX = (int) motionEvent.getX();
                    int currentY = (int) motionEvent.getY();
                    int slop = ViewConfiguration.get(view.getContext()).getScaledTouchSlop();
                    if (Math.abs(currentX-downX) < slop && Math.abs(currentY-downY) < slop) {
                        if (mItemClickListener != null) {
                            mItemClickListener.onItemClick(view, listPosition);
                        }
                        return true;
                    }
                    break;
            }

            return false;
        }
    }

    class ItemClickListener implements View.OnClickListener {
        int position;
        ItemClickListener(int position) {
            this.position = position;
        }
        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(view, position);
            }
        }
    };

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    public void setImageItemClickListener(OnImageItemClickListener imageItemClickListener) {
        this.mImageItemClickListener = imageItemClickListener;
    }

    public interface OnImageItemClickListener {

        public void onItemClick(View v, int listPosition, int imagePosition);
    }

    public interface OnItemClickListener {

        public void onItemClick(View v, int position);
    }

    @Override
    public long getItemId(int position) {
        if (getItemCount() == 0 || position < 0 || position > getItemCount()-1 ){
            return 0;
        }
        return Long.parseLong(mDatas.get(position).id);
    }

    public Status getItem(int position) {
        return getDatas().get(position);
    }

}
