/*
 * Copyright (C) 2013 yixia.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.net.cc.weibo.ui.videos;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;

import cn.net.cc.weibo.R;
import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class VideoViewActivity extends Activity implements MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener {

	private static final String TAG = "VideoViewDemo";
	private static final String PATH_PARAM = "path";
	private String path;
	private VideoView mVideoView;

	private ProgressWheel pb;
	private TextView downloadRateView, loadRateView;

	private ImageView img ;

	public static void StartThis(Context context, String path) {
		Intent intent = new Intent(context,VideoViewActivity.class);
		intent.putExtra(PATH_PARAM,path);
		context.startActivity(intent);
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		if (!LibsChecker.checkVitamioLibs(this))
			return;
		setContentView(R.layout.videoview);
		mVideoView = (VideoView) findViewById(R.id.surface_view);
		downloadRateView = (TextView) findViewById(R.id.download_rate);
		loadRateView = (TextView) findViewById(R.id.load_rate);
		pb = (ProgressWheel) findViewById(R.id.probar);
		img = (ImageView) findViewById(R.id.img);

		path = getIntent().getStringExtra(PATH_PARAM);
//		path = "http://ws.acgvideo.com/a/84/11651026-1.mp4?wsTime=1479639576&wsSecret2=7bebef964cc891a5a43e6e98ddb630b6&oi=2043096855&rate=80";
		Log.d(TAG,"path:"+path);
		if (path == null) {
			return;
		}
		/*
		 * Alternatively,for streaming media you can use
		 * mVideoView.setVideoURI(Uri.parse(URLstring));
		 */
		Log.d(TAG,path);
		mVideoView.setVideoPath(path);
		mVideoView.setBufferSize(512 * 1024);
		mVideoView.setMediaController(new MediaController(this));
		mVideoView.requestFocus();
		mVideoView.setOnBufferingUpdateListener(this);
		mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mediaPlayer) {
				// optional need Vitamio 4.0
				mediaPlayer.setPlaybackSpeed(1.0f);
			}
		});
		mVideoView.setOnInfoListener(this);

		/*Bitmap bitmap = null;

		if (bitmap == null) {
			Log.d(TAG,"bitmap is null");
			return;
		}*/
//		img.setImageBitmap(bitmap);

	}

	public void openVideo(View View) {
	  mVideoView.setVideoPath(path);
	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		switch (what) {
			case MediaPlayer.MEDIA_INFO_BUFFERING_START:
				if (mVideoView.isPlaying()) {
					mVideoView.pause();
					pb.setVisibility(View.VISIBLE);
					downloadRateView.setText("");
					loadRateView.setText("");
					downloadRateView.setVisibility(View.VISIBLE);
					loadRateView.setVisibility(View.VISIBLE);

				}
				break;
			case MediaPlayer.MEDIA_INFO_BUFFERING_END:
				mVideoView.start();
				pb.setVisibility(View.GONE);
				downloadRateView.setVisibility(View.GONE);
				loadRateView.setVisibility(View.GONE);
				break;
			case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
				downloadRateView.setText("" + extra + "kb/s" + "  ");
				break;
		}
		return true;
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		loadRateView.setText(getString(R.string.percent,percent));
	}

}
