package cn.net.cc.weibo.message;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import cn.net.cc.weibo.R;
import cn.net.cc.weibo.base.BaseAdapter;
import cn.net.cc.weibo.friends.ImageGridAdapter;
import cn.net.cc.weibo.imageLoad.ImageLoader;
import cn.net.cc.weibo.imageview.ImageActivity;
import cn.net.cc.weibo.util.DateUtil;
import cn.net.cc.weibo.view.emojitextview.WeiBoContentTextUtil;
import cn.net.cc.weibo.view.emojitextview.WeiBoTextClickListener;
import openapi.models.Comment;
import openapi.models.Status;
import openapi.models.User;

/**
 * Created by chengkai on 2016/10/20.
 */
public class MsgCommentAdapter extends BaseAdapter<Comment> {

    public MsgCommentAdapter(Context context, List<Comment> datas) {
        super(context, R.layout.message_comment_item, datas);
    }

    @Override
    protected void convert(final ViewHolder holder, Comment comment, int position) {
        holder.setText(R.id.name,comment.user.screen_name);
        TextView from = holder.getView(R.id.from);
        if(!TextUtils.isEmpty(comment.source)) {
            from.setText(Html.fromHtml(comment.source));
        }
        TextView time = holder.getView(R.id.time);
        if (!TextUtils.isEmpty(comment.created_at)) {
            time.setText(DateUtil.dateFormat(comment.created_at));
        }
        TextView content = holder.getView(R.id.content_txt);
        content.setText(WeiBoContentTextUtil.getWeiBoContent(comment.text,getContext(),content, new WeiBoTextClickListener(getContext())));
        ImageView icon = holder.getView(R.id.icon);
        ImageLoader.with(getContext(), comment.user.avatar_large, icon);

        setContent(holder,comment.status);

    }

    private void setContent(ViewHolder holder, Status status) {
        String contentTxt;
        User user = status.user;
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
        setContentTxt(contentTxt, getContext(), retweetedContentTxt);
        GridView gridView = holder.getView(R.id.retweeted_content_imgs);
        setImageList(gridView, status);
    }

    public void setContentTxt(String content, Context context, TextView textView) {
        textView.setText(WeiBoContentTextUtil.getWeiBoContent(content, context, textView, new WeiBoTextClickListener(context)));
    }

    private void setImageList(GridView gridView, final Status status) {
        if (status.getPic_urlStr() != null && status.getPic_urlStr().size() > 0) {
            gridView.setVisibility(View.VISIBLE);
            ArrayList<String> urls = status.getPic_urlStr();
            if (urls.size() == 1) {
                gridView.setNumColumns(1);
            }else {
                gridView.setNumColumns(3);
            }
            ImageGridAdapter adapter = new ImageGridAdapter(getContext(), status.getPic_urlStr());
            adapter.setOnItemClickListener(new ImageGridAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Intent intent = new Intent(getContext(), ImageActivity.class);
                    ArrayList<String> urls =  status.getPic_urlStr();
                    if (urls == null) {
                        urls =  status.retweeted_status.getPic_urlStr();
                    }
                    intent.putExtra(ImageActivity.INDEX_PARAM, position);
                    intent.putStringArrayListExtra(ImageActivity.URLS_PARAM, urls);
                    getContext().startActivity(intent);
                }
            });
            gridView.setAdapter(adapter);
        }else {
            gridView.setVisibility(View.GONE);
        }
    }

    @Override
    public long getItemId(int position) {
        if (getItemCount() == 0 || position < 0 || position > getItemCount()-1 ){
            return 0;
        }
        return Long.parseLong(mDatas.get(position).id);
    }
}
