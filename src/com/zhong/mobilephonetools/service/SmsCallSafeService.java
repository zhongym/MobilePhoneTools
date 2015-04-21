package com.zhong.mobilephonetools.service;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.zhong.mobilephonetools.dao.BlackNumberDao;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

public class SmsCallSafeService extends Service {
	private static final String TAG = "SmsCallSafeService";

	private BlackNumberDao dao;

	private InSmsSafeReceiver receiver;

	private TelephonyManager manager;

	private CallSafeListener listener;

	/**
	 * 短信广播接收者：<br>
	 * 检查到收到的信息>>得到来信号码>>查询数据库>>是否需要拦截该号码的短信
	 */
	class InSmsSafeReceiver extends BroadcastReceiver {

		public void onReceive(Context context, Intent intent) {

			Object[] pdus = (Object[]) intent.getExtras().get("pdus");
			for (Object pdu : pdus) {
				SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
				String number = sms.getOriginatingAddress();
				String mode = dao.findMode(number);
				Log.i(TAG, "number"+number +"mode"+mode);
				
				if ("2".equals(mode) || "3".equals(mode)) {
					Log.i(TAG, "拦截短信");
					abortBroadcast();// 中止这条广播
				}
			}
		}

	}

	public void onCreate() {
		super.onCreate();
		dao = new BlackNumberDao(this);
		manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		// 注册广播接收者
		receiver = new InSmsSafeReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(1000);
		registerReceiver(receiver, filter);

		// 注册电话状态事件
		listener = new CallSafeListener();
		manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	/**
	 * 来电监听器
	 *
	 */
	class CallSafeListener extends PhoneStateListener {
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);

			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:// 响铃了
				String result = dao.findMode(incomingNumber);
				if ("1".equals(result) || "3".equals(result)) {// 如果需要拦截
					Log.i(TAG, "挂断电话。。。。");
					endCall();
				}
				break;

			}

		}
	}

	/**
	 * 挂断电话的操作
	 */
	public void endCall() {
		// IBinder iBinder = ServiceManager.getService(TELEPHONY_SERVICE);
		try {
			// 加载servicemanager的字节码
			Class clazz = SmsCallSafeService.class.getClassLoader().loadClass("android.os.ServiceManager");
			Method method = clazz.getDeclaredMethod("getService", String.class);
			IBinder ibinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
			ITelephony.Stub.asInterface(ibinder).endCall();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onDestroy() {
		super.onDestroy();
		// 注销注册短信广播接收者
		unregisterReceiver(receiver);
		// 注销来电监听
		manager.listen(listener, TelephonyManager.DATA_ACTIVITY_NONE);
	}

	public IBinder onBind(Intent intent) {
		return null;
	}

}
