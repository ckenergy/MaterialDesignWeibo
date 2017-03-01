package cn.net.cc.weibo.ui.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import cn.net.cc.weibo.R;
import cn.net.cc.weibo.info.Constants;

/**
 * Created by wenmingvs on 16/5/12.
 */

public class WebViewActivity extends Activity implements WebViewActivityView {

    private static final String TAG = "WebViewActivity";

    public static final String URL_PARAM = "url";


    private Context mContext;
    private String sRedirectUri;
    private WebView mWeb;
    private String mLoginURL;
    private WebViewActivityPresentImp mWebViewActivityPresent;
//    private boolean mComeFromAccoutActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.webview_layout);
        mLoginURL = getIntent().getStringExtra(URL_PARAM);
        Log.d(TAG,"mLoginURL:"+mLoginURL);
//        mComeFromAccoutActivity = getIntent().getBooleanExtra("comeFromAccoutActivity", false);
        sRedirectUri = Constants.REDIRECT_URL;
        mWeb = (WebView) findViewById(R.id.webview);
        mWebViewActivityPresent = new WebViewActivityPresentImp(this);
        initWebView();
    }

    private void initWebView() {
        WebSettings settings = mWeb.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSaveFormData(false);
        settings.setSavePassword(false);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWeb.setWebViewClient(new MyWebViewClient());
        mWeb.loadUrl(mLoginURL);
        mWeb.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == android.view.KeyEvent.ACTION_DOWN) {
                    if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
                        if (mWeb.canGoBack()) {
                            mWeb.goBack();
                        } else {
//                            if (!mComeFromAccoutActivity) {
                                Intent intent = new Intent(WebViewActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            /*} else {
                                finish();
                            }*/

                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void startMainActivity() {
        Intent intent = new Intent(WebViewActivity.this, LoginActivity.class);
//        intent.putExtra("fisrtstart", true);
//        if (mComeFromAccoutActivity) {
//            intent.putExtra("comeFromAccoutActivity", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        }
        startActivity(intent);
        finish();
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d(TAG,"shouldOverrideUrlLoading,url:"+url);

            if (isUrlRedirected(url)) {
                view.stopLoading();
                mWebViewActivityPresent.handleRedirectedUrl(mContext, url);
            } else {
                view.loadUrl(url);
            }
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d(TAG,"onPageStarted,url:"+url);
            if (!url.equals("about:blank") && isUrlRedirected(url)) {
                view.stopLoading();
                mWebViewActivityPresent.handleRedirectedUrl(mContext, url);
                return;
            }
            super.onPageStarted(view, url, favicon);
        }
    }


    public boolean isUrlRedirected(String url) {
        return url.startsWith(sRedirectUri);
    }


}
