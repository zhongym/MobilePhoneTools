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
	 * ���Ź㲥�����ߣ�<br>
	 * ��鵽�յ�����Ϣ>>�õ����ź���>>��ѯ���ݿ�>>�Ƿ���Ҫ���ظú���Ķ���
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
					Log.i(TAG, "���ض���");
					abortBroadcast();// ��ֹ�����㲥
				}
			}
		}

	}

	public void onCreate() {
		super.onCreate();
		dao = new BlackNumberDao(this);
		manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		// ע��㲥������
		receiver = new InSmsSafeReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(1000);
		registerReceiver(receiver, filter);

		// ע��绰״̬�¼�
		listener = new CallSafeListener();
		manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	/**
	 * ���������
	 *
	 */
	class CallSafeListener extends PhoneStateListener {
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);

			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:// ������
				String result = dao.findMode(incomingNumber);
				if ("1".equals(result) || "3".equals(result)) {// �����Ҫ����
					Log.i(TAG, "�Ҷϵ绰��������");
					endCall();
				}
				break;

			}

		}
	}

	/**
	 * �Ҷϵ绰�Ĳ���
	 */
	public void endCall() {
		// IBinder iBinder = ServiceManager.getService(TELEPHONY_SERVICE);
		try {
			// ����servicemanager���ֽ���
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
		// ע��ע����Ź㲥������
		unregisterReceiver(receiver);
		// ע���������
		manager.listen(listener, TelephonyManager.DATA_ACTIVITY_NONE);
	}

	public IBinder onBind(Intent intent) {
		return null;
	}

}
