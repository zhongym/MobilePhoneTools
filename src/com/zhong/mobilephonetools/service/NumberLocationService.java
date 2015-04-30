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
 * ��ʾ��������ؿؼ��ķ���<br>
 * һ���Ⲧ�绰��<br>
 * <li><br>
 * 	   >>����һ���Ⲧ�绰�Ĺ㲥������OutCallPhoneReceiver�������Ⲧ�绰������Ⲧ���룬��ѯ���ݿ�ȡ�ù����أ������Զ���Toast��ʾ<br>
 * 	   >>�����񱻴���ʱ����̬ע������㲥�����ߣ������Ⲧ�绰��������ʾ��������ر�ʱ����̬����㲥��ע��
 * 
 *  �������磺<br>
 * <li><br>
 * >> ȡ��ϵͳ����TelephonyManager�绰�����ߣ�Ȼ����ӵ绰״̬����������дMyPhoneStateListener��Ե绰״̬���м���<br>
 * >> ��TelephonyManager.CALL_STATE_RINGING:// ������������ʱ������������ѯ�������أ����ݸ��Զ���Toast��ʾ
 * ����������ʱ��ע���������������ֹͣʱ��ȡ����������<br>
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
	
	/** ����˫���¼�ʱ������� **/
	long[] mHits = new long[2];

	private View view;

	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "NumberLocationService��������");
		telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		// ע�����
		listenerPhone = new MyPhoneStateListener();
		telephonyManager.listen(listenerPhone, PhoneStateListener.LISTEN_CALL_STATE);// �ڶ���������Ҫ����������

		// ��̬ע��㲥������
		receiver = new OutCallPhoneReceiver();
		IntentFilter filter = new IntentFilter();
		// <action android:name="android.intent.action.NEW_OUTGOING_CALL"/>
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(receiver, filter);
	}

	/**
	 * �Ⲧ�绰�㲥������,�����Ⲧ�绰����ʾ�绰�Ĺ�����<br>
	 * Ȩ�� android.permission.PROCESS_OUTGOING_CALLS
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
	 * �Զ����������ص�toast��ʽ <uses-permission
	 * android:name="android.permission.SYSTEM_ALERT_WINDOW" />
	 */
	private void myToast(String location) {

		view = View.inflate(this, R.layout.activity_toastsype, null);
		TextView tv = (TextView) view.findViewById(R.id.tv_location);

		int a = sp.getInt("numberloactionstype", 0);
		// "��͸��","������","��ʿ��","������","ƻ����"
		int[] ids = { R.drawable.call_locate_white, R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };
		view.setBackgroundResource(ids[a]);

		// ˫������
		view.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();
				if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
					// ˫�������ˡ�����
					params.x = windowManager.getDefaultDisplay().getWidth() / 2 - view.getWidth() / 2;
					windowManager.updateViewLayout(view, params);
					Editor editor = sp.edit();
					editor.putInt("lastx", params.x);
					editor.commit();
				}
			}
		});

		// �϶��ؼ�
		view.setOnTouchListener(new OnTouchListener() {
			// ������ָ�ĳ�ʼ��λ��
			int startX;
			int startY;

			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN:// ������ָ
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					Log.i(TAG, "��ָ�����ؼ�");
					break;

				case MotionEvent.ACTION_MOVE:// ��ָ�ƶ�
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();

					// ����ƶ���ƫ����
					int dx = newX - startX;
					int dy = newY - startY;

					Log.i(TAG, "��ָ�ڿؼ����ƶ�");
					params.x += dx;
					params.y += dy;

					windowManager.updateViewLayout(view, params);

					// ���Ǳ߽�����
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

					// ���³�ʼ����ָ�Ŀ�ʼ����λ�á�
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();

					break;
				case MotionEvent.ACTION_UP:// ��ָ̧��
					// ��¼�ؼ�������Ļ���Ͻǵ�����
					Log.i(TAG, "��ָ�뿪�ؼ�");
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

		// ����Ĳ��������ú���
		params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;

		//params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
			    | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

		params.format = PixelFormat.TRANSLUCENT;

		// �绰���ȼ��Ĵ������ͣ���Ҫ���Ȩ��
		params.type = WindowManager.LayoutParams.TYPE_PHONE;

		// �봰�����ϽǶ���
		params.gravity = Gravity.TOP + Gravity.LEFT;

		params.y = sp.getInt("lasty", 0);
		params.x = sp.getInt("lastx", 0);
		windowManager.addView(view, params);
	}

	/**
	 * �Զ���绰״̬����������
	 * 
	 * @author zhong
	 *
	 */
	private class MyPhoneStateListener extends PhoneStateListener {
		// ��д�绰״̬�ı�ķ���
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);

			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:// ������������
				String location = NumberAddressQueryUtils.queryNumber(incomingNumber);
				myToast(location);
				break;
			case TelephonyManager.CALL_STATE_IDLE:// �绰�Ŀ���״̬���ҵ绰������ܾ�
				// �����View�Ƴ�
				if (view != null) {
					windowManager.removeView(view);
				}
				break;
			}
		}

	}

	public void onDestroy() {
		Log.i(TAG, "NumberLocationService����������");
		super.onDestroy();
		// ȡ����������
		telephonyManager.listen(listenerPhone, PhoneStateListener.LISTEN_NONE);
		listenerPhone = null;

		// ��̬ȡ��ע��㲥������
		unregisterReceiver(receiver);
		receiver = null;
	}

	public IBinder onBind(Intent intent) {
		return null;
	}

}
