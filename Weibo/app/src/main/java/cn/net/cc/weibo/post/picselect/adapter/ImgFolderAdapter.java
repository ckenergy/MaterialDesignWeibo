package cn.net.cc.weibo.post.picselect.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.net.cc.weibo.R;
import cn.net.cc.weibo.imageLoad.ImageLoader;
import cn.net.cc.weibo.post.picselect.bean.AlbumFolderInfo;

public class ImgFolderAdapter extends BaseAdapter {

    private List<AlbumFolderInfo> mDatas = new ArrayList<AlbumFolderInfo>();
    private Context context;
    private LayoutInflater layoutInflater;
    private int mCurrentFolder;

    public ImgFolderAdapter(Context context, List<AlbumFolderInfo> datas, int currentFolder) {
        this.context = context;
        this.mDatas = datas;
        this.layoutInflater = LayoutInflater.from(context);
        this.mCurrentFolder = currentFolder;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public AlbumFolderInfo getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.compose_picfolder_pop_item, parent,false);
            convertView.setTag(new ViewHolder(convertView));
        }
        initializeViews(getItem(position), (ViewHolder) convertView.getTag(), position);
        return convertView;
    }

    private void initializeViews(AlbumFolderInfo object, ViewHolder holder, int position) {
        if (position == mCurrentFolder) {
            holder.folderlayout.setBackgroundColor(Color.parseColor("#f2f2f2"));
        } else {
            holder.folderlayout.setBackgroundColor(Color.parseColor("#ffffff"));
        }

        ImageLoader.with(context,"file:///" + object.getFrontCover().getAbsolutePath(), holder.firstpic);

        holder.foldername.setText(object.getFolderName());
        holder.num.setText("(" + object.getImageInfoList().size() + ")");
    }

    protected class ViewHolder {
        private LinearLayout folderlayout;
        private ImageView firstpic;
        private TextView foldername;
        private TextView num;

        public ViewHolder(View view) {
            folderlayout = (LinearLayout) view.findViewById(R.id.folderlayout);
            firstpic = (ImageView) view.findViewById(R.id.firstpic);
            foldername = (TextView) view.findViewById(R.id.foldername);
            num = (TextView) view.findViewById(R.id.num);
        }
    }

}
