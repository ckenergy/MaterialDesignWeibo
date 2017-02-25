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

package cn.net.cc.weibo.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cn.net.cc.weibo.base.BaseListFragment;
import cn.net.cc.weibo.model.FriendsModelImpl;
import cn.net.cc.weibo.friends.ILoadListModel;
import cn.net.cc.weibo.friends.WeiboItemAdapter;
import cn.net.cc.weibo.imageview.ImageActivity;
import cn.net.cc.weibo.weiboItem.WeiboItemDetailActivity;
import openapi.models.Status;

public class UserWeiBoListFragment extends BaseListFragment<Status>{

    private static final String TAG = "RetweetListFragment";

    private static final String ID_PARAM = "id";
    WeiboItemAdapter adapter;

//    String id;
    long id ;
    int type;

    public static UserWeiBoListFragment newInstance(long id) {
        UserWeiBoListFragment fragment = new UserWeiBoListFragment();
        Bundle args = new Bundle();
        args.putLong(ID_PARAM, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void init() {
        if (getArguments() != null) {
            id = getArguments().getLong(ID_PARAM);
        }
        adapter = new WeiboItemAdapter(getContext(),new ArrayList<Status>());
        adapter.setItemClickListener(new WeiboItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                Intent intent = new Intent(getContext(), WeiboItemDetailActivity.class);
                Status status = (Status) v.getTag();
                intent.putExtra(WeiboItemDetailActivity.PARAM_ID,status.id);
                startActivity(intent);
                Log.d(TAG, "position:"+position);
            }
        });
        adapter.setImageItemClickListener(new WeiboItemAdapter.OnImageItemClickListener() {
            @Override
            public void onItemClick(View v, int listPosition, int position) {
                Intent intent = new Intent(getContext(), ImageActivity.class);
                ArrayList<String> urls = adapter.getItem(listPosition).getPic_urlStr();
                if (urls == null) {
                    urls = adapter.getItem(listPosition).retweeted_status.getPic_urlStr();
                }
                intent.putExtra(ImageActivity.INDEX_PARAM, position);
                intent.putStringArrayListExtra(ImageActivity.URLS_PARAM, urls);
                startActivity(intent);
            }
        });
        adapter.setShowUser(false);
        setAdapter(adapter);
        ILoadListModel model = new FriendsModelImpl(getContext(), this,new UserWeiListModelRequest(getContext(),id));
        setModel(model);
    }

    @Override
    public void setListEnd(List<Status> datas) {
        super.setListEnd(datas);
    }
}
