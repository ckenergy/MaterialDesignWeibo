package cn.net.cc.weibo.relationship;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

import cn.net.cc.weibo.R;
import cn.net.cc.weibo.base.BaseAdapter;
import cn.net.cc.weibo.imageLoad.ImageLoader;
import cn.net.cc.weibo.user.UserActivity;
import openapi.models.User;

/**
 * Created by chengkai on 2016/10/20.
 */
public class RelationshipAdapter extends BaseAdapter<User> {

    public RelationshipAdapter(Context context, List<User> datas) {
        super(context, R.layout.user_item, datas);
    }

    @Override
    protected void convert(final ViewHolder holder, User user, int position) {
        holder.setText(R.id.name, user.screen_name);
        TextView info = holder.getView(R.id.info);
        String infoStr;
        if (!TextUtils.isEmpty(user.getVerified_reason())) {
            infoStr = user.getVerified_reason();
        }else {
            infoStr = user.getDescription();
        }
        info.setText(infoStr);
        ImageView icon = holder.getView(R.id.icon);
        ImageLoader.with(getContext(), user.avatar_large, icon);
        holder.getConvertView().setOnClickListener(new ToUserOnClickListener(icon, user.screen_name));
    }

    @Override
    public long getItemId(int position) {
        if (getItemCount() == 0 || position < 0 || position > getItemCount()-1 ){
            return 0;
        }
        return Long.parseLong(mDatas.get(position).id);
    }

    class ToUserOnClickListener implements View.OnClickListener {

        String name;
        View icon;
        ToUserOnClickListener(View icon, String name) {
            this.icon = icon;
            this.name = name;
        }


        @Override
        public void onClick(View v) {
            UserActivity.startThis(icon, name);
        }
    };
}
