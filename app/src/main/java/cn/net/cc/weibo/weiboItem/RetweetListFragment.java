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

package cn.net.cc.weibo.weiboItem;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import cn.net.cc.weibo.R;
import cn.net.cc.weibo.base.BaseAdapter;
import cn.net.cc.weibo.base.BaseListFragment;
import cn.net.cc.weibo.friends.ILoadListModel;
import cn.net.cc.weibo.imageLoad.ImageLoader;
import cn.net.cc.weibo.model.RetweetListModelImp;
import cn.net.cc.weibo.util.DateUtil;
import cn.net.cc.weibo.view.emojitextview.WeiBoContentTextUtil;
import cn.net.cc.weibo.view.emojitextview.WeiBoTextClickListener;
import openapi.models.Status;

public class RetweetListFragment extends BaseListFragment<Status> {

    private static final String TAG = "RetweetListFragment";

    private static final String ID_PARAM = "id";

    long id ;

    public static RetweetListFragment newInstance(String id) {
        RetweetListFragment fragment = new RetweetListFragment();
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
        BaseAdapter adapter = new RetweetAdapter(getContext(),R.layout.comment_item,new ArrayList<Status>());
        setAdapter(adapter);
        ILoadListModel model = new RetweetListModelImp(getContext(),this,id);
        setModel(model);
    }

    class RetweetAdapter extends BaseAdapter<Status> {

        public RetweetAdapter(Context context, int layoutId, List<Status> datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(final ViewHolder holder, Status retweet, int position) {
            holder.setText(R.id.name,retweet.user.screen_name);
            holder.setText(R.id.from, DateUtil.dateFormat(retweet.created_at));
            TextView content = holder.getView(R.id.content_txt);
            setContentTxt(retweet.text, getContext(), content);
            ImageView icon = holder.getView(R.id.icon);
            ImageLoader.with(getContext(), retweet.user.avatar_large, icon);

        }

        public void setContentTxt(String content, Context context, TextView textView) {
            textView.setText(WeiBoContentTextUtil.getWeiBoContent(content, context, textView, new WeiBoTextClickListener(context)));
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
