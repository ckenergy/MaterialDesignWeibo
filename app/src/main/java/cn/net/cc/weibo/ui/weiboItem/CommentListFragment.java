/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.net.cc.weibo.ui.weiboItem;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import cn.net.cc.weibo.R;
import cn.net.cc.weibo.ui.base.BaseAdapter;
import cn.net.cc.weibo.ui.base.BaseListFragment;
import cn.net.cc.weibo.ui.friends.ILoadListModel;
import cn.net.cc.weibo.imageLoad.ImageLoader;
import cn.net.cc.weibo.model.CommentListModelImp;
import cn.net.cc.weibo.util.DateUtil;
import cn.net.cc.weibo.ui.view.emojitextview.WeiBoContentTextUtil;
import cn.net.cc.weibo.ui.view.emojitextview.WeiBoTextClickListener;
import openapi.models.Comment;

public class CommentListFragment extends BaseListFragment<Comment>{

    private static final String TAG = "CommentListFragment";

    private static final String ID_PARAM = "id";

    long id;

    public static CommentListFragment newInstance(String id) {
        CommentListFragment fragment = new CommentListFragment();
        Bundle args = new Bundle();
        args.putString(ID_PARAM, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void init() {
        if (getArguments() != null) {
            String idStr = getArguments().getString(ID_PARAM);
            id = idStr == null ? 0 : Long.parseLong(idStr);
        }
        BaseAdapter adapter = new MyCommentAdapter(getContext(),R.layout.comment_item,new ArrayList<Comment>());
        setAdapter(adapter);
        ILoadListModel model = new CommentListModelImp(getContext(),this, new CommentListRequest(getContext(), id));
        setModel(model);
    }

    class MyCommentAdapter extends BaseAdapter<Comment> {

        public MyCommentAdapter(Context context, int layoutId, List<Comment> datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(final ViewHolder holder, Comment comment, int position) {
            holder.setText(R.id.name,comment.user.screen_name);
            holder.setText(R.id.from, DateUtil.dateFormat(comment.created_at));
            TextView content = holder.getView(R.id.content_txt);
            content.setText(WeiBoContentTextUtil.getWeiBoContent(comment.text,getContext(),content, new WeiBoTextClickListener(getContext())));
            ImageView icon = holder.getView(R.id.icon);
            ImageLoader.with(getContext(), comment.user.avatar_large, icon);

        }

        @Override
        public long getItemId(int position) {
            if (getItemCount() == 0 || position < 0 || position > getItemCount()-1 ){
                return 0;
            }
            return Long.parseLong(mDatas.get(position).id);
        }

    }

}
