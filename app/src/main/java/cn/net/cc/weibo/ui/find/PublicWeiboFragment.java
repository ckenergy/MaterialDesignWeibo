package cn.net.cc.weibo.ui.find;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import cn.net.cc.weibo.ui.base.BaseListFragment;
import cn.net.cc.weibo.ui.friends.ILoadListModel;
import cn.net.cc.weibo.ui.friends.WeiboItemAdapter;
import cn.net.cc.weibo.ui.imageview.ImageActivity;
import cn.net.cc.weibo.model.NoIdWeiboModel;
import cn.net.cc.weibo.ui.weiboItem.WeiboItemDetailActivity;
import openapi.models.Status;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PublicWeiboFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PublicWeiboFragment extends BaseListFragment<Status> {

    private static final String TAG = "AiteWeiboFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private WeiboItemAdapter adapter;

    private ILoadListModel publicWeiboModel;

    public PublicWeiboFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AiteWeiboFragment.
     */
    public static PublicWeiboFragment newInstance(String param1, String param2) {
        PublicWeiboFragment fragment = new PublicWeiboFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void init() {
        publicWeiboModel = new NoIdWeiboModel(getContext(),this, new PublicWeiboRequest(getContext()));
        adapter = new WeiboItemAdapter(getActivity(), new ArrayList<Status>());
        adapter.setShowLoad(false);
        adapter.setItemClickListener(new WeiboItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Status status = (Status) v.getTag();
                WeiboItemDetailActivity.startThis(getContext(), status.id, status);
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
        setAdapter(adapter);
        setModel(publicWeiboModel);

    }

}
