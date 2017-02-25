package cn.net.cc.weibo.view.emojitextview;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import cn.net.cc.weibo.R;
import cn.net.cc.weibo.htmldecode.HtmlDecode;
import cn.net.cc.weibo.model.UrlModel;
import cn.net.cc.weibo.videos.VideoViewActivity;

/**
 * Created by chengkai on 2016/10/8.
 */
public class WeiBoTextClickListener extends WeiBoContentTextUtil.SimpleClickListener {

    private static final String TAG = "WeiBoTextClickListener";

    Context context;

    Handler handler;

    public class ProgressHandler extends Handler {

        public static final int SHOW_PROGRESS = 1;

        public static final int DISMISS_PROGRESS = 2;

        public ProgressHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case SHOW_PROGRESS:

                    showProgress();
                    break;
                case DISMISS_PROGRESS:
                    dismissProgress();
                    break;
            }
        }
    };

    ProgressDialog progressDialog;

    UrlModel urlModel;

    private void showProgress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
        }
        progressDialog.setMessage(context.getString(R.string.prloading));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void dismissProgress() {
        progressDialog.dismiss();
    }

    public WeiBoTextClickListener(Context context) {
        super(context);
        this.context = context;
        this.handler = new ProgressHandler(Looper.getMainLooper());
    }

    public UrlModel getModel() {
        if (urlModel == null) {
            urlModel = new UrlModel(context);
        }
        return urlModel;
    }

    @Override
    public void onClick(int type, final String name) {
        super.onClick(type, name);
        switch (type) {
            case WeiBoContentTextUtil.Section.TOPIC:
                break;
            case WeiBoContentTextUtil.Section.URL:
                if (name.length() > 19) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(name));
                    context.startActivity(browserIntent);
                    return;
                }
                handler.sendEmptyMessage(ProgressHandler.SHOW_PROGRESS);

                HtmlDecode.DecodeUrl(name, new HtmlDecode.decodeListener() {
                    @Override
                    public void succed(String urlStr) {
                        handler.sendEmptyMessage(ProgressHandler.DISMISS_PROGRESS);
//                        Toast.makeText(context, "urlStr", Toast.LENGTH_SHORT).show();
                        VideoViewActivity.StartThis(context,urlStr);
                    }

                    @Override
                    public void fail(String msg) {
                        handler.sendEmptyMessage(ProgressHandler.DISMISS_PROGRESS);
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(name));
                        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities
                                (browserIntent, PackageManager.MATCH_DEFAULT_ONLY);
                        if (resolveInfos != null&&resolveInfos.size() >0) {
                            context.startActivity(browserIntent);
                            Log.d(TAG,"resolveInfos:"+resolveInfos.size());
                        }else {
                            Toast.makeText(context,"没有可用浏览器",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                Log.d(TAG,name);
                break;
        }
    }
}
