package cn.net.cc.weibo.ui.login;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;

import cn.net.cc.weibo.R;
import cn.net.cc.weibo.bean.UserBean;
import cn.net.cc.weibo.info.Constants;
import cn.net.cc.weibo.ui.main.WeiboMainActivity;
import cn.net.cc.weibo.model.LoginModel;
import cn.net.cc.weibo.model.LoginModelImpl;
import cn.net.cc.weibo.util.BitmapUtil;
import cn.net.cc.weibo.util.FastBlur;


public class LoginActivity extends Activity implements OnClickListener, LoginView{

	private static final String TAG = "LoginActivity";

	private static final int ICON_IMAGE_SIZE = 150;
	private static final int BG_IMAGE_SIZE = 80;
	private static final int RADIUS = 20;
	private static final int PERMISSION_CODE = 1;

	private View decorView;
	private View addUser;
	private ImageView userIcon;
	private TextView userName;
	private String name;

	private LoginModel login;

	private boolean isLogin = false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		findViewById(R.id.add_user).setOnClickListener(this);
		userIcon = (ImageView) findViewById(R.id.user_icon);
		userName = (TextView) findViewById(R.id.user_name);
		userIcon.setOnClickListener(this);

		decorView = getWindow().getDecorView();

		login = new LoginModelImpl(this,this);
		if(!login.checkUser()) {
			useDefoultBg();
			showMessage("并没有用户，点击+添加");
		}else {
			ActivityCompat.requestPermissions(this,
					new String[]{Manifest.permission.INTERNET,
							Manifest.permission.ACCESS_WIFI_STATE,
							Manifest.permission.ACCESS_NETWORK_STATE,
							Manifest.permission.WRITE_EXTERNAL_STORAGE,
							Manifest.permission.READ_PHONE_STATE,
							Manifest.permission.WAKE_LOCK,
							Manifest.permission.READ_EXTERNAL_STORAGE},
					PERMISSION_CODE);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case PERMISSION_CODE:
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					// permission was granted, yay! Do the
					// contacts-related task you need to do.

				} else {
					//用户不同意，向用户展示该权限作用
					if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

					}
					// permission denied, boo! Disable the
					// functionality that depends on this permission.
				}
				break;
		}
	}

	private void showMessage(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
	
	public boolean isOnline() {
	    ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected();
	}
	
	private void useDefoultBg() {
		userIcon.setImageResource(R.drawable.login_bg);
		decorView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				InputStream inputStream = getResources().openRawResource(R.drawable.login_bg);
				Bitmap bitmap = BitmapUtil.fixBitmap(inputStream, 100, 100);
				setBg(bitmap);
			}
		}); 
	}
	
	private void setBg(Bitmap bitmap) {
		Log.d("setUser", "time1:"+System.currentTimeMillis());
		bitmap = BitmapUtil.fixBitmapToViewSize(bitmap, decorView.getWidth(), decorView.getHeight(), BG_IMAGE_SIZE);
		bitmap = FastBlur.doBlur(bitmap, RADIUS, true);
		Log.d("setBg", "time2:"+System.currentTimeMillis());
		Drawable drawable =new BitmapDrawable(getResources(),bitmap);
//		Log.d("oncreate", bitmap.isMutable()+"");
		decorView.setBackgroundDrawable(drawable);
	}

	/**
     * 当 SSO 授权 Activity 退出时，该函数被调用。
     * 
     * @see {@link Activity#onActivityResult}
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
        if (login != null) {
        	login.authorizeCallBack(requestCode, resultCode, data);
        }
        
    }

	void doLogin() {
//		login.doLogin();
		if(!isOnline()) {
			showMessage("网络未连接");
			return;
		}
		Intent intent = new Intent(this,WebViewActivity.class);
		intent.putExtra(WebViewActivity.URL_PARAM, Constants.authurl);
		startActivity(intent);
		finish();
	}

	void goMain() {
		if (isLogin) {
			WeiboMainActivity.startThis(this,name);
			finish();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_user:
			doLogin();
			break;
		case R.id.user_icon:
			goMain();
			break;

		}
	}

	@Override
	public void setError(String error) {
		showMessage(error);
	}

	@Override
	public void setUser(UserBean user) {
		Log.d(TAG, "setUser,"+user.icon);
		Bitmap bitmap = Bitmap.createBitmap(user.icon);
		name = user.name;
		userName.setText(user.name);
		isLogin = true;
		if(!bitmap.isMutable()) {
			bitmap = BitmapUtil.convertToMutable(bitmap);
		}
//		Bitmap bitmap = user.icon;
		int imgHeight = (ICON_IMAGE_SIZE*bitmap.getHeight()/bitmap.getWidth());
		bitmap = BitmapUtil.fixBitmap(bitmap, ICON_IMAGE_SIZE, imgHeight);
//		user.icon = bitmap;
		Bitmap bitmap1 = Bitmap.createBitmap(bitmap);
		setBg(bitmap1);
		userIcon.setImageBitmap(bitmap);
		
	}
	
}
