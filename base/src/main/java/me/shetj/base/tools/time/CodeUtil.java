package me.shetj.base.tools.time;

import android.app.Activity;
import androidx.annotation.Keep;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

@Keep
public class CodeUtil {
	private TextView codeTV;
	private Timer timer;
	private TimerTask timerTask;
	private Activity activity;
	private int second;
	private static final int DEF_TIME_LENGTH = 60;
	private int timeLength = DEF_TIME_LENGTH;

	public CodeUtil(Activity activity, TextView tv) {
		codeTV = tv;
		this.activity = activity;
	}

	public CodeUtil(Activity activity, TextView tv, int timeLength) {
		codeTV = tv;
		this.activity = activity;
		this.timeLength = timeLength;
	}

	public void start() {
		stopTimer();
		second = timeLength;
		codeTV.post(new Runnable() {
			@Override
			public void run() {
				codeTV.setEnabled(false);
			}
		});

		timer = new Timer();
		timerTask = new CodeTimerTask();
		timer.scheduleAtFixedRate(timerTask, 0, 1000);
	}

	public int getSecond(){
		return second;
	}

	private void stopSendCode() {
		stopTimer();
		codeTV.post(new Runnable() {
			@Override
			public void run() {
				codeTV.setText("获取验证码");
				codeTV.setEnabled(true);
			}
		});
	}

	private void stopTimer() {
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}
		if (timerTask != null) {
			timerTask.cancel();
			timerTask = null;
		}
	}

	public void stop() {
		second = 0;
	}

	class CodeTimerTask extends TimerTask {

		@Override
		public void run() {
			if (second <= 0) {
				stop();
			} else {
				onWaiting();
			}
		}

		private void stop() {
			stopSendCode();
		}

		private void onWaiting() {
			codeTV.post(new Runnable() {
				@Override
				public void run() {
					codeTV.setText(String.format("%s秒后重发", second));
					second--;
				}
			});
		}
	}
}

