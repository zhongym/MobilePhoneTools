package com.zhong.mobilephonetools.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.zhong.mobilephonetools.R;
import com.zhong.mobilephonetools.dao.utils.NumberAddressQueryUtils;
/**
 * 
 * 显示号码归属地控件的服务：<br>
 * 一：外拨电话：<br>
 * <li><br>
 * 	   >>创建一个外拨电话的广播接收者OutCallPhoneReceiver，监听外拨电话，获得外拨号码，查询数据库取得归属地，传给自定义Toast显示<br>
 * 	   >>当服务被创建时，动态注册这个广播接收者，开启外拨电话归属地显示。当服务关闭时，动态解除广播的注册
 * 
 *  二：来电：<br>
 * <li><br>
 * >> 取得系统服务TelephonyManager电话管理者，然后添加电话状态监听器，编写MyPhoneStateListener类对电话状态进行监听<br>
 * >> 当TelephonyManager.CALL_STATE_RINGING:// 来电铃声响起时，将来电号码查询出归属地，传递给自定义Toast显示
 * 当服务启动时，注册监听器。当服务停止时，取消监听来电<br>
		telephonyManager.listen(listenerPhone, PhoneStateListener.LISTEN_NONE);
		listenerPhone = null;
 *
 */
public class NumberLocationService extends Service {

	private static final String TAG = "NumberLocationService";

	private TelephonyManager telephonyManager;

	private WindowManager windowManager;

	private MyPhoneStateListener listenerPhone;

	private OutCallPhoneReceiver receiver;

	private SharedPreferences sp;

	private WindowManager.LayoutParams params;
	
	/** 保存双击事件时间的数组 **/
	long[] mHits = new long[2];

	private View view;

	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "NumberLocationService服务开启了");
		telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		// 注册监听
		listenerPhone = new MyPhoneStateListener();
		telephonyManager.listen(listenerPhone, PhoneStateListener.LISTEN_CALL_STATE);// 第二个参数，要监听的内容

		// 动态注册广播接收者
		receiver = new OutCallPhoneReceiver();
		IntentFilter filter = new IntentFilter();
		// <action android:name="android.intent.action.NEW_OUTGOING_CALL"/>
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(receiver, filter);
	}

	/**
	 * 外拨电话广播接收者,监听外拨电话，显示电话的归属地<br>
	 * 权限 android.permission.PROCESS_OUTGOING_CALLS
	 */
	class OutCallPhoneReceiver extends BroadcastReceiver {

		public void onReceive(Context context, Intent intent) {
			String phone = getResultData();
			String location = NumberAddressQueryUtils.queryNumber(phone);
			// Toast.makeText(NumberLocationService.this, location,
			// Toast.LENGTH_LONG).show();
			myToast(location);
		}

	}

	/**
	 * 自定义号码归属地的toast样式 <uses-permission
	 * android:name="android.permission.SYSTEM_ALERT_WINDOW" />
	 */
	private void myToast(String location) {

		view = View.inflate(this, R.layout.activity_toastsype, null);
		TextView tv = (TextView) view.findViewById(R.id.tv_location);

		int a = sp.getInt("numberloactionstype", 0);
		// "半透明","活力橙","卫士蓝","金属灰","苹果绿"
		int[] ids = { R.drawable.call_locate_white, R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };
		view.setBackgroundResource(ids[a]);

		// 双击居中
		view.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();
				if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
					// 双击居中了。。。
					params.x = windowManager.getDefaultDisplay().getWidth() / 2 - view.getWidth() / 2;
					windowManager.updateViewLayout(view, params);
					Editor editor = sp.edit();
					editor.putInt("lastx", params.x);
					editor.commit();
				}
			}
		});

		// 拖动控件
		view.setOnTouchListener(new OnTouchListener() {
			// 定义手指的初始化位置
			int startX;
			int startY;

			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN:// 按下手指
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					Log.i(TAG, "手指摸到控件");
					break;

				case MotionEvent.ACTION_MOVE:// 手指移动
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();

					// 求出移动的偏移量
					int dx = newX - startX;
					int dy = newY - startY;

					Log.i(TAG, "手指在控件上移动");
					params.x += dx;
					params.y += dy;

					windowManager.updateViewLayout(view, params);

					// 考虑边界问题
					if (params.x < 0) {
						params.x = 0;
					}
					if (params.y < 0) {
						params.y = 0;
					}
					if (params.x > (windowManager.getDefaultDisplay().getWidth() - view.getWidth())) {
						params.x = (windowManager.getDefaultDisplay().getWidth() - view.getWidth());
					}
					if (params.y > (windowManager.getDefaultDisplay().getHeight() - view.getHeight())) {
						params.y = (windowManager.getDefaultDisplay().getHeight() - view.getHeight());
					}

					// 重新初始化手指的开始结束位置。
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();

					break;
				case MotionEvent.ACTION_UP:// 手指抬起
					// 记录控件距离屏幕左上角的坐标
					Log.i(TAG, "手指离开控件");
					Editor editor = sp.edit();
					editor.putInt("lastx", params.x);
					editor.putInt("lasty", params.y);
					editor.commit();
					break;

				}

				return false;
			}
		});

		tv.setText(location);

		// 窗体的参数就设置好了
		params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;

		//params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
			    | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

		params.format = PixelFormat.TRANSLUCENT;

		// 电话优先级的窗体类型，并要添加权限
		params.type = WindowManager.LayoutParams.TYPE_PHONE;

		// 与窗体左上角对其
		params.gravity = Gravity.TOP + Gravity.LEFT;

		params.y = sp.getInt("lasty", 0);
		params.x = sp.getInt("lastx", 0);
		windowManager.addView(view, params);
	}

	/**
	 * 自定义电话状态监听处理器
	 * 
	 * @author zhong
	 *
	 */
	private class MyPhoneStateListener extends PhoneStateListener {
		// 重写电话状态改变的方法
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);

			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:// 来电铃声响起
				String location = NumberAddressQueryUtils.queryNumber(incomingNumber);
				myToast(location);
				break;
			case TelephonyManager.CALL_STATE_IDLE:// 电话的空闲状态：挂电话、来电拒绝
				// 把这个View移除
				if (view != null) {
					windowManager.removeView(view);
				}
				break;
			}
		}

	}

	public void onDestroy() {
		Log.i(TAG, "NumberLocationService服务销毁了");
		super.onDestroy();
		// 取消监听来电
		telephonyManager.listen(listenerPhone, PhoneStateListener.LISTEN_NONE);
		listenerPhone = null;

		// 动态取消注册广播接收者
		unregisterReceiver(receiver);
		receiver = null;
	}

	public IBinder onBind(Intent intent) {
		return null;
	}

}
