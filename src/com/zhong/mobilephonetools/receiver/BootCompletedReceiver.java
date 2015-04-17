package com.zhong.mobilephonetools.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

/**
 * <b>���տ��������Ĺ㲥�����ߣ�android.intent.action.BOOT_COMPLETED<br>
 * һ���������sim�������кź�֮ǰ��ʱ��sim���Ƿ���ͬ<br>
 * //ȡ�������ļ���lostFindStartUp��ֵ�Ƿ����ֻ�����<br>
 * //�����ֻ�����sim���ĲŻᷢ���Ÿ���ȫ����<br>
 * 1����config�����ļ���ȡ�������sim���к�<br>
 * 2���ж�ȡ�������к��Ƿ�Ϊ�գ���Ϊ�գ��Ѿ�����sim��<br>
 * 3��ȡ����ǰsim�������кűȽ�<br>
 * 4��������ڵ�sim���кźͱ����һ����û�и���sim��<br>
 * 5�������һ������������Ϣ�����õİ�������<br>
 * 
 */
public class BootCompletedReceiver extends BroadcastReceiver {

	private static final String TAG = "BootCompletedReceiver";
	private SharedPreferences sp;

	public void onReceive(Context context, Intent intent) {
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		
		//ȡ�������ļ� ���Ƿ����ֻ�����
		boolean lostFindStartUp = sp.getBoolean("lostFindStartUp", false);
		if (lostFindStartUp) {//�����ֻ�����sim���ĲŻᷢ���Ÿ���ȫ����
			// ��ñ����sim�����к�
			String savaSim = sp.getString("simSerialNumber", null) + "a";

			if (!TextUtils.isEmpty(savaSim)) {// �ж��Ƿ���sim�󶨹���
				// ��õ�ǰsim���к�
				TelephonyManager mn = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				String sim = mn.getSimSerialNumber();
				// �ж��Ƿ�һ��
				if (sim.equals(savaSim)) {
					Log.i(TAG, "sim��û�и���");
				} else {
					Log.i(TAG, "sim���Ѿ�����");

					String number = sp.getString("safeNumber", "");
					SmsManager.getDefault().sendTextMessage(number, null, "����ֻ� ������SIM��", null, null);
					// Toast.makeText(context, "sim���Ѿ�����", 5).show();
				}
			}
		}
	}

}
