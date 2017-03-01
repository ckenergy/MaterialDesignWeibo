package cn.net.cc.weibo.ui.find;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import cn.net.cc.weibo.R;
import cn.net.cc.weibo.ui.base.BaseFragment;
import cn.net.cc.weibo.ui.hot.HotCommentActivity;
import cn.net.cc.weibo.ui.hot.HotRepostActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link FindFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FindFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "FindFragment";

    public FindFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FindFragment.
     */
    public static FindFragment newInstance(String param1, String param2) {
        FindFragment fragment = new FindFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find, container,false);
        view.findViewById(R.id.public_).setOnClickListener(this);
        view.findViewById(R.id.hot).setOnClickListener(this);
        view.findViewById(R.id.hot_comments).setOnClickListener(this);
        view.findViewById(R.id.hot_repost).setOnClickListener(this);
        view.findViewById(R.id.search).setOnClickListener(this);

        return view;
    }

    @Override
    public void fetchData() {
        Log.d(TAG,"init data find");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.hot_comments_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search:
                break;
            case R.id.public_:
                startActivity(new Intent(getContext(),PublicActivity.class));
                break;
            case R.id.hot_comments:
                startActivity(new Intent(getContext(),HotCommentActivity.class));
                break;
            case R.id.hot_repost:
                startActivity(new Intent(getContext(),HotRepostActivity.class));
                break;
        }
    }
}
