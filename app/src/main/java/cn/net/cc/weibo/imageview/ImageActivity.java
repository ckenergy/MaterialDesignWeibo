package cn.net.cc.weibo.imageview;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.net.cc.weibo.R;
import cn.net.cc.weibo.main.FragmentAdapter;

public class ImageActivity extends AppCompatActivity {

    public static final String TAG = "ImageActivity";
    public static final String INDEX_PARAM = "index";
    public static final String URLS_PARAM = "urls";
//    public static final String BITMAP_PARAM = "bitmap";

    ViewPager viewPager;
    TextView indexTxt;

//    Bitmap bitmap;
    List<String> urls;
//    String bitUrl;
    int index;

    String indexStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        viewPager = (ViewPager) findViewById(R.id.image_pager);
        indexTxt = (TextView) findViewById(R.id.index);
        index = getIntent().getIntExtra(INDEX_PARAM,0);
        urls = getIntent().getStringArrayListExtra(URLS_PARAM);
//        bitmap = getIntent().getParcelableExtra(BITMAP_PARAM);
//        imageView.setImageBitmap(bitmap);
        if (urls != null) {
            int length = urls.size();
            List<Fragment> fragmentList = new ArrayList<>();
            for (String url : urls) {
                fragmentList.add(ImageFragment.newInstance(url));
            }
            FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(),fragmentList);
            viewPager.setAdapter(adapter);
            if (length > 1) {
                indexStr = "/"+length;
                viewPager.setCurrentItem(index);
                String title = index+1+indexStr;
                indexTxt.setText(title);
            }else {
                indexTxt.setVisibility(View.GONE);
            }
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                String title = position+1+indexStr;
                indexTxt.setText(title);
            }
            @Override public void onPageScrollStateChanged(int state) {}
        });
    }

}
