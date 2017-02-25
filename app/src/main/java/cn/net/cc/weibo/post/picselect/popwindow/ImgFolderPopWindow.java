package cn.net.cc.weibo.post.picselect.popwindow;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.util.ArrayList;

import cn.net.cc.weibo.R;
import cn.net.cc.weibo.post.picselect.adapter.ImgFolderAdapter;
import cn.net.cc.weibo.post.picselect.bean.AlbumFolderInfo;
import cn.net.cc.weibo.util.DisplayUtil;

/**
 * Created by wenmingvs on 16/5/6.
 */
public class ImgFolderPopWindow extends PopupWindow {

    private View mView;
    private ListView mListView;
    private int mWidth;
    private int mHeight;
    private Context mContext;
    private ArrayList<AlbumFolderInfo> mFolderList;
    public OnFolderClickListener onFolderClickListener;
    private int mCurrentFolder;


    public ImgFolderPopWindow(Context context, int width, int height, ArrayList<AlbumFolderInfo> folderList, int currentFolder) {
        this.mContext = context;
        this.mWidth = width;
        this.mHeight = height;
        this.mFolderList = folderList;
        this.mCurrentFolder = currentFolder;
        mView = LayoutInflater.from(context).inflate(R.layout.compose_picfolder_pop, null);
        setContentView(mView);
        initPopWindow();
        initListView();
        setUpListener();
    }

    private void initListView() {
        mListView = (ListView) mView.findViewById(R.id.listview);
        ImgFolderAdapter imgFolderAdapter = new ImgFolderAdapter(mContext, mFolderList, mCurrentFolder);
        mListView.setAdapter(imgFolderAdapter);
        mListView.getLayoutParams().height = mHeight;

    }


    public interface OnFolderClickListener {
        public void OnFolderClick(int position);
    }

    public void setOnFolderClickListener(OnFolderClickListener onFolderClickListener) {
        this.onFolderClickListener = onFolderClickListener;
    }

    private void setUpListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onFolderClickListener.OnFolderClick(position);
            }
        });
    }


    private void initPopWindow() {
        this.setWidth(mWidth);
        this.setHeight(DisplayUtil.getHeightPix(mContext));
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        this.setBackgroundDrawable(dw);
        this.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();

                Rect rect = new Rect(mListView.getLeft(), mListView.getTop(), mListView.getRight(), mListView.getBottom());
                if (!rect.contains((int) x, (int) y)) {
//                    LogUtil.d("不在里面");
                    dismiss();
                    return true;
                }
                return false;
            }
        });
    }
}

