package cn.net.cc.weibo.ui.imageview;


import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.github.lzyzsd.circleprogress.DonutProgress;

import java.io.IOException;

import cn.net.cc.weibo.R;
import cn.net.cc.weibo.ui.base.BaseFragment;
import cn.net.cc.weibo.http.okhttp.OkHttpClientFactory;
import cn.net.cc.weibo.http.okhttp.ProgressListener;
import cn.net.cc.weibo.imageLoad.BitmapRequest;
import cn.net.cc.weibo.imageLoad.ImageLoader;
import cn.net.cc.weibo.model.GifModel;
import cn.net.cc.weibo.model.IGifModel;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageFragment extends BaseFragment implements IImageView{

    public static final String TAG = "ImageFragment";

    private static final int UPDATE_PROGRESS = 1;

    private static final int LOAD_COMPLETE = 2;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String URL_PARAM = "url";
//    private static final String BITMAP_PARAM = "param2";

    private String url;
    private Bitmap bitmap;

    private SubsamplingScaleImageView longImageView;
    private ImageView imageView;
    private DonutProgress progress ;
    private GifImageView gifView;

    private ImageProgressListener listener;

    private ImageHandler handler;

    boolean isGif = false;

    private IGifModel model;

    @Override
    public void setGif(String file) {
        loadGif(file);
    }

    @Override
    public void setFail(String erro) {
        Toast.makeText(getContext(),"load fail",Toast.LENGTH_SHORT).show();
    }

    class ImageHandler extends Handler{

        DonutProgress progress;
        ImageHandler(DonutProgress progress) {
            this.progress = progress;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_PROGRESS:
                    int current = msg.arg1;
                    int total = msg.arg2;
                    int percent = 100*current/total;
                    Log.d(TAG,"percent:"+percent);
                    if (progress != null) {
                        progress.setProgress(percent);
                    }
                    break;
                case LOAD_COMPLETE:
//                    if (isGif()) {
//                        loadGif(url);
//                    }
                    break;
            }
        }
    }

    static class ImageProgressListener implements ProgressListener {

        Handler handler;
        ImageProgressListener(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void update(long bytesRead, long contentLength, boolean done) {
            if (handler != null) {
                Message message =handler.obtainMessage(UPDATE_PROGRESS);
                message.arg1 = (int) bytesRead;
                message.arg2 = (int) contentLength;
                handler.sendMessage(message);
                if (done) {
//                    handler.sendEmptyMessage(LOAD_COMPLETE);
                }
            }
        }
    }

    public ImageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    public static ImageFragment newInstance(String param1) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString(URL_PARAM, param1);
//        args.putParcelable(BITMAP_PARAM, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString(URL_PARAM);
//            bitmap = getArguments().getParcelable(BITMAP_PARAM);
        }
        model = new GifModel();

    }

    @Override
    public void fetchData() {
        Log.d(TAG, "fetchData");
        if (!TextUtils.isEmpty(url)) {
            getImage();
        }
    }

    private void loadGif(String FileName) {
        GifDrawable gifDrawable = null;
        try {
            gifDrawable = new GifDrawable(FileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG,"gif");
        gifView.setVisibility(View.VISIBLE);
        gifView.setImageDrawable(gifDrawable);
        progress.setVisibility(View.GONE);
    }

    private boolean isGif () {
        return isGif;
    }

    private void getImage() {
        url = replace2big(url);
        if (url.endsWith("gif")){
            isGif = true;
            ImageLoader.getGif(model,url,this,listener);
            progress.setVisibility(View.VISIBLE);
            return;
        }
        BitmapRequest request = new BitmapRequest(getContext(), url, new BitmapRequest.Listener() {
            @Override
            public void loadBitmap(Bitmap bitmap) {
                if (getActivity() == null) {
                    return;
                }
                if (bitmap == null) {
                    progress.setVisibility(View.GONE);
                    return;
                }
                Log.d(TAG, "width:"+bitmap.getWidth()+",height:"+bitmap.getHeight());
                int ivWidth = imageView.getMeasuredWidth();
                int ivHeight = imageView.getMeasuredHeight();
                if (ivHeight==0 || ivWidth == 0) {
                    ivHeight = 3;
                    ivWidth = 1;//如果imageview还没有测量好，就判断bitmap的高是大于宽的3倍，就设为大图，否则就按ih<iw/bw*bh
                }
                if (bitmap.getWidth()*ivHeight < bitmap.getHeight()*ivWidth) {
                    longImageView.setVisibility(View.VISIBLE);
                    String imageFile = OkHttpClientFactory.getPicassoImageCacheFull(url);
                    displayLongPic(imageFile,longImageView);
                    bitmap.recycle();
                }else {
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageBitmap(bitmap);
                }
                progress.setVisibility(View.GONE);
            }
        });

        request.setProgressListener(listener);
        request.setBigImage(true);
        ImageLoader.getBitmap(request);
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image,container,false);

        imageView = (ImageView) view.findViewById(R.id.img);
        longImageView = (SubsamplingScaleImageView) view.findViewById(R.id.longImg);
        progress = (DonutProgress) view.findViewById(R.id.progress);
        gifView = (GifImageView) view.findViewById(R.id.gifView);
        handler = new ImageHandler(progress);
        listener = new ImageProgressListener(handler);
        longImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        setDataInitiated(false);
        longImageView.recycle();
        imageView.setImageBitmap(null);
        listener.handler = null;
//        handler.progress = null;
    }

    private void displayLongPic(Bitmap bitmap, SubsamplingScaleImageView longImg) {
        longImg.setDoubleTapZoomDuration(100);
        longImg.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
        longImg.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);
        longImg.setImage(ImageSource.bitmap(bitmap),new ImageViewState(0, new PointF(0, 0), 0));

    }

    private void displayLongPic(String url, SubsamplingScaleImageView longImg) {
        longImg.setDoubleTapZoomDuration(100);
        longImg.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
        longImg.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);
        longImg.setImage(ImageSource.uri(url),new ImageViewState(0, new PointF(0, 0), 0));
        longImageView.setOnImageEventListener(new SubsamplingScaleImageView.OnImageEventListener() {
            @Override
            public void onReady() {
                Log.d(TAG, "onReady");
            }

            @Override
            public void onImageLoaded() {
                Log.d(TAG, "onImageLoaded");
            }
            @Override public void onPreviewLoadError(Exception e) {}
            @Override public void onImageLoadError(Exception e) {}
            @Override public void onTileLoadError(Exception e) {}
        });
    }

    private String replace2big(String url) {
        return url.replace("bmiddle","large");
    }

}
