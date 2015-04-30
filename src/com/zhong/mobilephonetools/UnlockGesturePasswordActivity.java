package com.zhong.mobilephonetools;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhong.mobilephonetools.ui.LockPatternView;
import com.zhong.mobilephonetools.ui.LockPatternView.Cell;
import com.zhong.mobilephonetools.utils.LockPatternUtils;

public class UnlockGesturePasswordActivity extends Activity {
	private LockPatternView mLockPatternView;
	private int mFailedPatternAttemptsSinceLastTimeout = 0;
	private CountDownTimer mCountdownTimer = null;
	private Handler mHandler = new Handler();
	private TextView mHeadTextView;
	private Animation mShakeAnim;
	private PackageManager pm;
	private Toast mToast;
	private ImageView gesturepwd_unlock_face;
	private String packname;
	private TextView gesturepwd_unlock_forget;

	private void showToast(CharSequence message) {
		if (null == mToast) {
			mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
			mToast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			mToast.setText(message);
		}

		mToast.show();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gesturepassword_unlock);

		pm = getPackageManager();

		mLockPatternView = (LockPatternView) this.findViewById(R.id.gesturepwd_unlock_lockview);
		mLockPatternView.setOnPatternListener(mChooseNewLockPatternListener);
		mLockPatternView.setTactileFeedbackEnabled(true);
		mHeadTextView = (TextView) findViewById(R.id.gesturepwd_unlock_text);
		gesturepwd_unlock_face = (ImageView) findViewById(R.id.gesturepwd_unlock_face);
		mShakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake_x);
		gesturepwd_unlock_forget = (TextView) findViewById(R.id.gesturepwd_unlock_forget);

		packname = getIntent().getStringExtra("packname");
		// 设置解锁显示图标
		try {
			if (!TextUtils.isEmpty(packname) && !"AppLockActivity".equals(packname)) {
				ApplicationInfo info = pm.getApplicationInfo(packname, 0);
				gesturepwd_unlock_face.setImageDrawable(info.loadIcon(pm));
			}

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		// 忘记密码,输入密保问题界面
		gesturepwd_unlock_forget.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(UnlockGesturePasswordActivity.this, PwdQuestionActivity.class);
				startActivity(intent);
			}
		});

	}

	protected void onResume() {
		super.onResume();

		if (!App.getInstance().getLockPatternUtils().savedPatternExists()) {
			startActivity(new Intent(this, GuideGesturePasswordActivity.class));
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mCountdownTimer != null)
			mCountdownTimer.cancel();
	}

	private Runnable mClearPatternRunnable = new Runnable() {
		public void run() {
			mLockPatternView.clearPattern();
		}
	};

	protected LockPatternView.OnPatternListener mChooseNewLockPatternListener = new LockPatternView.OnPatternListener() {

		public void onPatternStart() {
			mLockPatternView.removeCallbacks(mClearPatternRunnable);
			patternInProgress();
		}

		public void onPatternCleared() {
			mLockPatternView.removeCallbacks(mClearPatternRunnable);
		}

		public void onPatternDetected(List<LockPatternView.Cell> pattern) {
			if (pattern == null)
				return;
			if (App.getInstance().getLockPatternUtils().checkPattern(pattern)) {
				// 输入密码正确
				mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Correct);

				if (packname == null) {
					return;
				}
				if ("AppLockActivity".equals(packname)) {
					// 进入程序锁主界面
					Intent intent = new Intent(UnlockGesturePasswordActivity.this, AppLockActivity.class);
					startActivity(intent);
					finish();
				} else {
					// 进入锁定的程序
					Intent intent = new Intent();
					intent.setAction("com.zhong.unlockApp");
					intent.putExtra("packname", packname);
					sendBroadcast(intent);
					finish();
				}

			} else {
				mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
				if (pattern.size() >= LockPatternUtils.MIN_PATTERN_REGISTER_FAIL) {
					mFailedPatternAttemptsSinceLastTimeout++;
					int retry = LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT
							- mFailedPatternAttemptsSinceLastTimeout;
					if (retry >= 0) {
						if (retry == 0)
							showToast("您已5次输错密码，30秒后再试");
						mHeadTextView.setText("密码错误，还可以再输 " + retry + "次 ");
						mHeadTextView.setTextColor(Color.RED);
						mHeadTextView.startAnimation(mShakeAnim);
					}

				} else {
					showToast("输入长度不够，请重试");
				}

				if (mFailedPatternAttemptsSinceLastTimeout >= LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT) {
					mHandler.postDelayed(attemptLockout, 2000);
				} else {
					mLockPatternView.postDelayed(mClearPatternRunnable, 2000);
				}
			}
		}

		public void onPatternCellAdded(List<Cell> pattern) {

		}

		private void patternInProgress() {
		}
	};
	Runnable attemptLockout = new Runnable() {

		@Override
		public void run() {
			mLockPatternView.clearPattern();
			mLockPatternView.setEnabled(false);
			mCountdownTimer = new CountDownTimer(LockPatternUtils.FAILED_ATTEMPT_TIMEOUT_MS + 1, 1000) {
				public void onTick(long millisUntilFinished) {
					int secondsRemaining = (int) (millisUntilFinished / 1000) - 1;
					if (secondsRemaining > 0) {
						mHeadTextView.setText(secondsRemaining + " 秒后重试");
					} else {
						mHeadTextView.setText("请绘制手势密");
						mHeadTextView.setTextColor(Color.WHITE);
					}

				}

				public void onFinish() {
					mLockPatternView.setEnabled(true);
					mFailedPatternAttemptsSinceLastTimeout = 0;
				}
			}.start();
		}
	};

	public void onBackPressed() {
		if ("AppLockActivity".equals(packname)) {
			finish();
		} else {
			// 回到桌面
			Intent intent = new Intent();
			intent.setAction("android.intent.action.MAIN");
			intent.addCategory("android.intent.category.HOME");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.addCategory("android.intent.category.MONKEY");
			startActivity(intent);
			// 所有的activity最小化 不会执行ondestory 只执行 onstop方法。
		}
	};

	protected void onStop() {
		super.onStop();
		finish();

	}
}
