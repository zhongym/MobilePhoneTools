package com.zhong.mobilephonetools.receiver;

import com.zhong.mobilephonetools.R;
import com.zhong.mobilephonetools.service.GPSService;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

/**
 * ���Ź㲥�����ߣ�android.provider.Telephony.SMS_RECEIVED<br>
 * ʱʱ���ϵͳ���յ�����Ϣ���ж��Ƿ����ֻ��������ܡ�<br>
 * �ж��Ƿ��ǰ�ȫ���뷢������Ϣ��<br>
 * ����ǰ�ȫ���뷢������Ϣ���������ַ���ƥ�䡣ƥ��ɹ������Ծ͵Ĳ�����<br>
 *
 */
public class SMSReceivcr extends BroadcastReceiver {

	private static final String TAG = "SMSReceivcr";
	private SharedPreferences sp;
	private DevicePolicyManager dpm;

	public void onReceive(Context context, Intent intent) {
		sp = context.getSharedPreferences("config", context.MODE_PRIVATE);
		dpm = (DevicePolicyManager) context.getSystemService(context.DEVICE_POLICY_SERVICE);

		boolean lostFindStartUp = sp.getBoolean("lostFindStartUp", false);
		String safeNumber = sp.getString("safeNumber", "");

		if (lostFindStartUp) {// �����ֻ�����

			Object[] objs = (Object[]) intent.getExtras().get("pdus");

			for (Object obj : objs) {

				SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
				String body = sms.getMessageBody();
				String sender = sms.getOriginatingAddress();

				Log.i(TAG, "�����ֻ�����");

				if (sender.contains(safeNumber)) {

					if ("#*location*#".equals(body)) {// ���gps����

						// ������ȡgps����
						Intent intent2 = new Intent(context, GPSService.class);
						context.startService(intent2);

						String lastlocation = sp.getString("lastlocation", null);
						if (TextUtils.isEmpty(lastlocation)) {
							SmsManager.getDefault().sendTextMessage(sender, null, "getting gps.....", null, null);
						} else {
							SmsManager.getDefault().sendTextMessage(sender, null, lastlocation, null, null);
						}

						abortBroadcast();// ��ֹ����㲥

					} else if ("#*alarm*#".equals(body)) {// ���ű�������
						Log.i(TAG, "���ű�������");
						MediaPlayer player = MediaPlayer.create(context, R.raw.zufc);
						// player.setLooping(true);//ѭ������
						player.setVolume(1.0f, 1.0f);
						player.start();
						abortBroadcast();// ��ֹ����㲥

					} else if ("#*wipedata*#".equals(body)) {// Զ����������

						// ���Sdcard�ϵ�����
						// dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
						// �ָ���������
						// dpm.wipeData(0);

					} else if ("#*lockscreen*#".equals(body)) {// Զ������
						String password = sp.getString("lockScreenPwd", null);
						Log.i(TAG, " Զ������" + password);
						dpm.lockNow();// ����
						dpm.resetPassword(password, 0);// ������������Ҫ�������������һ����
//						dpm.removeActiveAdmin(who);ȡ��ĳ��Ӧ�õİ�ȫ�豸Ȩ��
//						dpm.setPasswordMinimumLength(admin, length);
//						dpm.setPasswordHistoryLength(admin, length);
						
						abortBroadcast();// ��ֹ����㲥
					}

				}

			}
		}

	}

}
