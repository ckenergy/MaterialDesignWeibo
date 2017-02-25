package cn.net.cc.weibo.http;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.net.cc.weibo.http.okhttp.OkHttpClientFactory;
import cn.net.cc.weibo.http.okhttp.ProgressListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class HttpManager {

	private static final String TAG = "HttpManager";
//	private static class LazyHolder{
//		private static HttpManager INSTANCE = new HttpManager();
//	}

	static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);

	private HttpManager(){
		
	}
	
	public void init(Context context) {
	}
	
//	public static HttpManager getInstance() {
//		return LazyHolder.INSTANCE;
//	}
	
	public static void downFile(final String url, final OnDownListener listener, final ProgressListener progressListener) {

		fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
				Request request = new Request.Builder().url(url).build();
				Call call = OkHttpClientFactory.getProgressBarClient(progressListener).newCall(request);
				call.enqueue(new Callback() {
					@Override
					public void onFailure(Call call, IOException e) {
						listener.onFail(e.getMessage());
					}

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						InputStream is = null;
						byte[] buf = new byte[2048];
						int len = 0;
						FileOutputStream fos = null;
						String fileName = OkHttpClientFactory.getPicassoImageCacheFull(url);
						File file = new File(fileName);
						try {
							long total = response.body().contentLength();
							long current = 0;
							is = response.body().byteStream();
							fos = new FileOutputStream(file);
							while ((len = is.read(buf)) != -1) {
								current += len;
								fos.write(buf, 0, len);
							}
							fos.flush();
							listener.onSuccess(fileName);
						} catch (IOException e) {
							Log.e(TAG, e.toString());
							listener.onFail(e.getMessage());
						} finally {
							try {
								if (is != null) {
									is.close();
								}
								if (fos != null) {
									fos.close();
								}
							} catch (IOException e) {
								Log.e(TAG, e.toString());
							}
						}
					}
				});
            }
        });

	}

	public interface OnDownListener {
		public void onSuccess(String file);
		public void onFail(String erro);
	}

}
