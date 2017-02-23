package cn.net.cc.weibo.post.picselect.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.rey.material.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

import cn.net.cc.weibo.R;
import cn.net.cc.weibo.imageLoad.ImageLoader;
import cn.net.cc.weibo.post.picselect.bean.ImageInfo;

public class GirdViewAdapter extends BaseAdapter {

    private List<ImageInfo> mDatas = new ArrayList<ImageInfo>();
    private ArrayList<ImageInfo> mSelectImgList = new ArrayList<ImageInfo>();
    private OnImgSelectListener mOnImgSelectListener;

    private Context mContext;
    private LayoutInflater layoutInflater;

    public GirdViewAdapter(Context context, List<ImageInfo> data, ArrayList<ImageInfo> selectImgList) {
        this.mContext = context;
        this.mDatas = data;
        this.mSelectImgList = selectImgList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public ImageInfo getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.compose_pic_grid_item, parent,false);
            convertView.setTag(new ViewHolder(convertView));
        }
        initializeViews((ImageInfo) getItem(position), (ViewHolder) convertView.getTag(), convertView);

        return convertView;
    }

    private void initializeViews(final ImageInfo imageInfo, final ViewHolder holder, View convertView) {

        holder.select_img.setChecked(imageInfo.isSelected());
        if (imageInfo.isSelected()) {
            holder.itemImg.setColorFilter(Color.parseColor("#77000000"));
        } else {
            holder.itemImg.setColorFilter(null);
        }

        View.OnClickListener selecterListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //图片被选中
                if (imageInfo.isSelected()) {
                    holder.itemImg.setColorFilter(null);
                    imageInfo.setIsSelected(false);
                    holder.select_img.setChecked(false);
                    deleteSelectImg(mSelectImgList, imageInfo);
                    mOnImgSelectListener.OnDisSelect(mSelectImgList);
                }
                //图片没有被选中
                else {
                    if (mSelectImgList.size() >= 9) {
                        Toast.makeText(mContext, "最多选择9张图片",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    holder.itemImg.setColorFilter(Color.parseColor("#77000000"));
                    holder.select_img.setChecked(true);
                    imageInfo.setIsSelected(true);
                    addToSelectImgList(mSelectImgList, imageInfo);
                    mOnImgSelectListener.OnSelect(mSelectImgList);
                }
            }
        };
        holder.select_img.setOnClickListener(selecterListener);
        convertView.setOnClickListener(selecterListener);
        ImageLoader.with(mContext,"file:///" + imageInfo.getImageFile().getAbsolutePath(), holder.itemImg);
    }

    protected class ViewHolder {
        private ImageView itemImg;
        private CheckBox select_img;

        public ViewHolder(View view) {
            itemImg = (ImageView) view.findViewById(R.id.item_img);
            select_img = (CheckBox) view.findViewById(R.id.select_img);
        }
    }

    /**
     * 监听图片勾选的事件
     */
    public interface OnImgSelectListener {
        public void OnDisSelect(ArrayList<ImageInfo> imageInfos);

        public void OnSelect(ArrayList<ImageInfo> imageInfos);
    }

    public void setOnImgSelectListener(OnImgSelectListener onImgSelectListener) {
        this.mOnImgSelectListener = onImgSelectListener;
    }

    /**
     * 第二次打开相册，搜索过后，即使同一张图片，对应的引用的地址和上一次已经发生变化，不可以直接equal来比较，要根据绝对路径名来比较才行
     *
     * @param selectImgList
     * @param imageInfo
     */
    public void deleteSelectImg(ArrayList<ImageInfo> selectImgList, ImageInfo imageInfo) {

        for (int i = 0; i < selectImgList.size(); i++) {
            if (selectImgList.get(i).getImageFile().getAbsolutePath().equals(imageInfo.getImageFile().getAbsolutePath())) {
                selectImgList.remove(i);
            }
        }
    }

    /**
     * 第二次打开相册，搜索过后，即使同一张图片，对应的引用的地址和上一次已经发生变化，不可以直接equal来比较，要根据绝对路径来比较才行
     *
     * @param selectImgList
     * @param imageInfo
     */
    public void addToSelectImgList(ArrayList<ImageInfo> selectImgList, ImageInfo imageInfo) {
        for (int i = 0; i < selectImgList.size(); i++) {
            if (selectImgList.get(i).getImageFile().getAbsolutePath().equals(imageInfo.getImageFile().getAbsolutePath())) {
                //如果selectlist中已经存在此图片，就不重复进行添加了
                return;
            }
        }
        selectImgList.add(imageInfo);

    }

}
