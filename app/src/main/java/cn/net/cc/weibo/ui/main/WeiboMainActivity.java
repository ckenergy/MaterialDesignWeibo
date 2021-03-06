package cn.net.cc.weibo.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

import cn.net.cc.weibo.R;
import cn.net.cc.weibo.ui.find.FindFragment;
import cn.net.cc.weibo.ui.friends.FriendsFragment;
import cn.net.cc.weibo.ui.me.MeFragment;
import cn.net.cc.weibo.ui.message.MessageFragment;
import cn.net.cc.weibo.ui.view.NavigationView;


public class WeiboMainActivity extends FragmentActivity implements NavigationView.OnPageChangeListener{

	public static final String PARAM = "name";

	private ViewPager pagerContent;
	private NavigationView navigationBar;

	private FragmentAdapter adapter;
	private ArrayList<Fragment> list = new ArrayList<Fragment>();

	private String name;

	public static void startThis(Context context, String name) {
		Intent intent = new Intent(context,WeiboMainActivity.class);
		intent.putExtra(PARAM,name);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weibo_main);

		pagerContent = (ViewPager) findViewById(R.id.pager_content);
		navigationBar = (NavigationView) findViewById(R.id.navigation_bar);
		name = getIntent().getStringExtra(PARAM);

		MeFragment userFragment = MeFragment.newInstance(name);
		list.add(new FriendsFragment());
		list.add(new MessageFragment());
		list.add(new FindFragment());
		list.add(userFragment);
		adapter = new FragmentAdapter(getSupportFragmentManager(),list);
		pagerContent.setOffscreenPageLimit(list.size()-1);
		pagerContent.setAdapter(adapter);
		navigationBar.setViewPager(pagerContent);
		navigationBar.setPageChangerListener(this);
	}

	@Override
	public void onPageScrolled(int i, float v, int i1) {

	}

	@Override
	public void onPageSelected(int i) {

	}

	@Override
	public void onPageScrollStateChanged(int i) {

	}

}
