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
 * <b>接收开机启动的广播接收者：android.intent.action.BOOT_COMPLETED<br>
 * 一：开机检查sim卡的序列号和之前绑定时的sim卡是否相同<br>
 * //取得配置文件中lostFindStartUp的值是否开启手机防盗<br>
 * //开启手机防盗sim更改才会发短信给安全号码<br>
 * 1：从config配置文件中取出保存的sim序列号<br>
 * 2：判断取出的序列号是否为空，不为空：已经开启sim绑定<br>
 * 3：取出当前sim卡的序列号比较<br>
 * 4：如果现在的sim序列号和保存的一样，没有更改sim卡<br>
 * 5：如果不一样，即发送信息给设置的案例号码<br>
 * 
 */
public class BootCompletedReceiver extends BroadcastReceiver {

	private static final String TAG = "BootCompletedReceiver";
	private SharedPreferences sp;

	public void onReceive(Context context, Intent intent) {
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		
		//取得配置文件 中是否开启手机防盗
		boolean lostFindStartUp = sp.getBoolean("lostFindStartUp", false);
		if (lostFindStartUp) {//开启手机防盗sim更改才会发短信给安全号码
			// 获得保存的sim卡序列号
			String savaSim = sp.getString("simSerialNumber", null) + "a";

			if (!TextUtils.isEmpty(savaSim)) {// 判断是否开启sim绑定功能
				// 获得当前sim序列号
				TelephonyManager mn = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				String sim = mn.getSimSerialNumber();
				// 判断是否一至
				if (sim.equals(savaSim)) {
					Log.i(TAG, "sim卡没有更改");
				} else {
					Log.i(TAG, "sim卡已经更改");

					String number = sp.getString("safeNumber", "");
					SmsManager.getDefault().sendTextMessage(number, null, "你的手机 更换了SIM卡", null, null);
					// Toast.makeText(context, "sim卡已经更改", 5).show();
				}
			}
		}
	}

}
