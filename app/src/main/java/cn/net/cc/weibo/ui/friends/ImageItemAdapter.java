package cn.net.cc.weibo.ui.friends;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import cn.net.cc.weibo.R;
import cn.net.cc.weibo.imageLoad.ImageLoader;

/**
 * Created by chengkai on 2016/8/24.
 */
public class ImageItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "ImageItemAdapter";

    private ArrayList<String> mImageUrls;
    private Context mContext;
    private LayoutInflater inflater;
    private int columns = 3;
    private int widthPix = -1;
    private int oneOfItem = -1;
    private OnItemClickListener mOnItemClickListener;

    public ImageItemAdapter(Context context, ArrayList<String> imageUrls) {
        super();
        this.mContext = context;
        this.mImageUrls = imageUrls;
        inflater = LayoutInflater.from(context);
        Log.d(TAG,"start");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.image_item, parent, false);
        if (widthPix == -1) {
            widthPix = parent.getMeasuredWidth();
            oneOfItem = widthPix/columns;
        }
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (getItemCount() == 1) {
            layoutParams.width = widthPix;
            int height = mContext.getResources().getDimensionPixelOffset(R.dimen.main_image_size);
            layoutParams.height = height;
        }else{
            layoutParams.width = oneOfItem;
            layoutParams.height = oneOfItem;
        }
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ItemViewHolder viewHolder = (ItemViewHolder) holder;
        Log.d(TAG, "ImageItemAdapter:" + mImageUrls.get(position));
        String url = mImageUrls.get(position);
        ImageLoader.with(mContext, url, viewHolder.contentImgItem);
        viewHolder.contentImgItem.setTag(url);
        viewHolder.contentImgItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, position);
                }
                Log.d(TAG,"onClick");
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return mImageUrls.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView contentImgItem;

        public ItemViewHolder(View view) {
            super(view);
            contentImgItem = (ImageView) view.findViewById(R.id.img);
        }
    }

    public interface OnItemClickListener {

        public void onItemClick(View v, int position);
    }
}