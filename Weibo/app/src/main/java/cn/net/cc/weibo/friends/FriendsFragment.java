package cn.net.cc.weibo.friends;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import cn.net.cc.weibo.R;
import cn.net.cc.weibo.base.BaseListFragment;
import cn.net.cc.weibo.imageview.ImageActivity;
import cn.net.cc.weibo.model.FriendsModelImpl;
import cn.net.cc.weibo.weiboItem.ILoadView;
import cn.net.cc.weibo.weiboItem.IRefresh;
import cn.net.cc.weibo.weiboItem.WeiboItemDetailActivity;
import openapi.models.Status;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends BaseListFragment<Status> implements ILoadView {

    private static final String TAG = "FriendsFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<Status> mDatas = new ArrayList<>();
    private WeiboItemAdapter adapter;

    ILoadListModel friendsModel;

    /**微博类型*/
    int type = 0;

    public FriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    protected void init() {

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        friendsModel = new FriendsModelImpl(getActivity(), this, new FriendsRequest(getContext()));
        adapter = new WeiboItemAdapter(getActivity(), mDatas);

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
        setAdapter(adapter);
        setModel(friendsModel);
        setLoadView(this);

    }

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container) {

        View view = inflater.inflate(R.layout.fragment_friends,container,false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.test_recycler_view);
        setupRecyclerView(recyclerView);
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                IRefresh refresh = FriendsFragment.this;
                refresh.onRefresh();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            /*throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");*/
        }
        Log.d(TAG,"onCreateView");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        Log.d(TAG,"onCreateView");
    }

    @Override
    public void setShowLoading(final boolean show) {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(show);
            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
