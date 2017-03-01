package cn.net.cc.weibo.ui.friends;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.net.cc.weibo.R;
import cn.net.cc.weibo.imageLoad.ImageLoader;
import cn.net.cc.weibo.util.DensityUtil;
import cn.net.cc.weibo.util.DisplayUtil;

/**
 * Created by chengkai on 2016/8/24.
 */
public class ImageGridAdapter extends android.widget.BaseAdapter {

    private static final String TAG = "ImageItemAdapter";

    private ArrayList<String> mImageUrls;
    private Context mContext;
    private LayoutInflater inflater;
    private int columns = 3;
    private int widthPix = -1;
    private int oneOfItem = -1;
    private OnItemClickListener mOnItemClickListener;

    public ImageGridAdapter(Context context, ArrayList<String> imageUrls) {
        this.mContext = context;
        this.mImageUrls = imageUrls;
        inflater = LayoutInflater.from(context);
        Log.d(TAG,"start");
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getCount() {
        return mImageUrls.size();
    }

    @Override
    public String getItem(int i) {
        return mImageUrls.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, final ViewGroup parent) {
        final ItemViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.image_item, parent, false);
            holder = new ItemViewHolder(view);
            view.setTag(holder);
        }else {
            holder = (ItemViewHolder) view.getTag();
        }
        /*if (widthPix == -1) {
            widthPix = parent.getMeasuredWidth();
            oneOfItem = widthPix/columns;
        }*/
        Log.d(TAG,"widthPix:"+widthPix);
        if (widthPix <= 0) {
            widthPix = DisplayUtil.getWidthPix(mContext) - DensityUtil.dp2px(mContext,25);//25dp为父view边距
            oneOfItem = widthPix/columns;
        }
        ViewGroup.LayoutParams layoutParams = holder.contentImgItem.getLayoutParams();
        if (getCount() == 1) {
            layoutParams.width = widthPix;
            int height = mContext.getResources().getDimensionPixelOffset(R.dimen.main_image_size);
            layoutParams.height = height;
        }else{
            layoutParams.width = oneOfItem;
            layoutParams.height = oneOfItem;
        }

        Log.d(TAG, "ImageItemAdapter:" + mImageUrls.get(position));
        final String url = getItem(position);
        ImageLoader.with(mContext, url, holder.contentImgItem);
        holder.contentImgItem.setTag(url);
        if (url.endsWith("gif")) {
            holder.type.setVisibility(View.VISIBLE);
            holder.type.setText(R.string.gif);
        }

        holder.contentImgItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, position);
                }
                Log.d(TAG,"onClick");
            }
        });
        return view;
    }

    public class ItemViewHolder {
        ImageView contentImgItem;
        TextView type;

        public ItemViewHolder(View view) {
            contentImgItem = (ImageView) view.findViewById(R.id.img);
            type = (TextView) view.findViewById(R.id.type);
        }
    }

    public interface OnItemClickListener {

        public void onItemClick(View v, int position);
    }
}