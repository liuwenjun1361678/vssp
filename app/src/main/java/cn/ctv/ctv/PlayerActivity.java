package cn.ctv.ctv;

import java.util.Timer;
import java.util.TimerTask;


import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dj.ctv.R;

public class PlayerActivity extends Activity {
	private CustomVideoView vvPlayer;
	private TextView tvSeepSpeak;
	private RelativeLayout rlControler;
	private ProgressBar progress;
	private TextView tvTime;
	private boolean isLongPressKey = false;
	private String playUrl = "";
	private int played_len = 0;

	private Timer quickTimer = new Timer();
	private Timer runTimer = new Timer();
	private Boolean isQuicking = false;
	private int curQuickTimes = 0;
	private int maxQuickTimes = 32;

	private ProgressBar videoProgressBar;
	private Handler handlerMsg = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 100:
				if (playUrl != null && playUrl.length() > 0) {
					vvPlayer.setVideoURI(Uri.parse(playUrl));

					if (played_len > 0) {
						vvPlayer.seekTo(played_len);
					}
					vvPlayer.start();
				}
				break;
			}
		}
	};

	Runnable readyPlay = new Runnable() {
		@Override
		public void run() {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Message message = new Message();
			message.what = 100;
			handlerMsg.sendMessage(message);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player);
		// arrayList= new ArrayList<ArrInfo>();
		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		Bundle bundle = getIntent().getExtras();
		playUrl = bundle.getString("playUrl");// 播放网址
		Log.e("tagmsg", "" + playUrl);
		Intent intent = getIntent();
		played_len = intent.getIntExtra("played_len", 0);

		tvSeepSpeak = (TextView) findViewById(R.id.tvSeepSpeak);
		rlControler = (RelativeLayout) findViewById(R.id.rlControler);
		vvPlayer = (CustomVideoView) findViewById(R.id.vvPlayer);
		progress = (ProgressBar) findViewById(R.id.progress);
		tvTime = (TextView) findViewById(R.id.tvTime);
		// rlLoading = (RelativeLayout)findViewById(R.id.rlLoading);
		// ivLoadingBG = (ImageView)findViewById(R.id.ivLoadingBG);
		// tvLoadingTig = (TextView)findViewById(R.id.tvLoadingTig);

		videoProgressBar = (ProgressBar) this
				.findViewById(R.id.videoProgressBar);

		runTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						int total_len = vvPlayer.getDuration();
						int played_len = vvPlayer.getCurrentPosition();
						if (total_len > 0) {

							progress.setProgress(played_len);
							progress.setMax(total_len);
							tvTime.setText(getTime(played_len) + "/"
									+ getTime(total_len));

						}
						if (played_len > total_len - 3000 && total_len > 0) {
							finish();
						}
					}
				});
			}
		}, 0, 1000);

		vvPlayer.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
			}
		});
		// 监听播放完成的事件�?
		vvPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				Log.e("1111", "1111");

			}
		});

		// 监听视频装载完成的事件�?
		vvPlayer.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				// TODO Auto-generated method stub
				// mp.start();
				// mp.setLooping(true);
				videoProgressBar.setVisibility(View.GONE);
			}
		});

		// 监听播放发生错误时�?的事件�?
		vvPlayer.setOnErrorListener(new OnErrorListener() {

			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				videoProgressBar.setVisibility(View.GONE);
				return false;
			}
		});

		new Thread(readyPlay).start();
	}

	@Override
	protected void onStop() {
		quickTimer.cancel();
		runTimer.cancel();
		super.onStop();
	}

	public static String getTime(long oSecond) {
		StringBuffer sbTime = new StringBuffer();
		long tmpTime = oSecond / 3600000;
		int hour = (int) tmpTime;

		oSecond -= hour * 3600000;
		tmpTime = oSecond / 60000;
		int minite = (int) tmpTime;

		oSecond -= minite * 60000;
		int second = (int) (oSecond / 1000);
		String hourStr = (hour < 10) ? ("0" + hour) : String.valueOf(hour);
		sbTime.append(hourStr);
		sbTime.append(":");
		String miniteStr = (minite < 10) ? ("0" + minite) : String
				.valueOf(minite);
		sbTime.append(miniteStr);
		sbTime.append(":");
		String secondStr = (second < 10) ? ("0" + second) : String
				.valueOf(second);
		sbTime.append(secondStr);
		return sbTime.toString();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == 23) {// 确认
			if (isQuicking) {
				curQuickTimes = 0;
				quickTo(curQuickTimes);
			} else {
				if (vvPlayer.isPlaying()) {
					vvPlayer.pause();
					rlControler.setVisibility(View.VISIBLE);
				} else {
					vvPlayer.start();
					rlControler.setVisibility(View.GONE);
				}
			}
		} else if (keyCode == 126) {// 播放
			if (isQuicking) {
				curQuickTimes = 0;
				quickTo(curQuickTimes);
			} else {
				if (!vvPlayer.isPlaying()) {
					vvPlayer.start();
					rlControler.setVisibility(View.GONE);
				}
			}
		} else if (keyCode == 121) {
			if (vvPlayer.isPlaying()) {
				vvPlayer.pause();
				rlControler.setVisibility(View.VISIBLE);
			}
		} else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP) {
			finish();
		} else if (keyCode == 22 || keyCode == 125) {
			// 快进
			if (event.getRepeatCount() == 0) {// 识别长按短按的代码
				event.startTracking();
				Log.e("11111111111", "1111111");
				isLongPressKey = false;
				if (curQuickTimes == 0) {
					curQuickTimes = 4;
				} else if (curQuickTimes < -4) {
					curQuickTimes = curQuickTimes / 2;
				} else if (curQuickTimes == -4) {
					curQuickTimes = 0;
				} else {
					curQuickTimes *= 2;
				}
				if (curQuickTimes > maxQuickTimes) {
					curQuickTimes = 0;
				}
				quickTo(curQuickTimes);
			} else {
				Log.e("2222222222", "1111111");
				isLongPressKey = true;
			}

		} else if (keyCode == 21 || keyCode == 89) {
			if (event.getRepeatCount() == 0) {// 识别长按短按的代码
				event.startTracking();
				Log.e("11111111111", "1111111");
				isLongPressKey = false;
				if (curQuickTimes == 0) {
					curQuickTimes = -4;
				} else if (curQuickTimes > 4) {
					curQuickTimes = curQuickTimes / 2;
				} else if (curQuickTimes == 4) {
					curQuickTimes = 0;
				} else {
					curQuickTimes *= 2;
				}

				quickTo(curQuickTimes);
			} else {
				Log.e("2222222222", "1111111");
				isLongPressKey = true;
			}

		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == 22 || keyCode == 125) {
			if (isLongPressKey) {
				isLongPressKey = false;
			}
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		Log.e("222222222", "2222222222");
		isLongPressKey = true;
		// TODO Auto-generated method stub
		return super.onKeyLongPress(keyCode, event);
	}

	private void quickTo(final int times) {
		quickTimer.cancel();
		if (times == 0) {
			isQuicking = false;
			tvSeepSpeak.setText("");
			if (vvPlayer.isPlaying()) {
				rlControler.setVisibility(View.GONE);
			}
			return;
		} else {
			rlControler.setVisibility(View.VISIBLE);
		}

		if (times > 0) {
			tvSeepSpeak.setText("快进    " + times + "X");
		} else {
			tvSeepSpeak.setText("快�?    " + Math.abs(times) + "X");
		}

		quickTimer = new Timer();
		quickTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						int currentPosition = vvPlayer.getCurrentPosition();
						int toPosition = currentPosition + (times * 4000);
						if (toPosition < 0) {
							quickTo(curQuickTimes = 0);
						}

						if (toPosition > vvPlayer.getDuration()) {
							quickTo(curQuickTimes = 0);
						}
						vvPlayer.seekTo(toPosition);
					}
				});
			}
		}, 0, 1000);
		isQuicking = true;
	}

	private void showToast(String msg) {
		Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
		toast.show();
	}
}
