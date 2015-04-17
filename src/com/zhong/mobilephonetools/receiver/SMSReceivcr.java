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
 * 短信广播接收者：android.provider.Telephony.SMS_RECEIVED<br>
 * 时时检测系统接收到的信息，判断是否开启手机防盗功能。<br>
 * 判断是否是安全号码发来的信息。<br>
 * 如果是安全号码发来的信息，将进行字符串匹配。匹配成功完成相对就的操作。<br>
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

		if (lostFindStartUp) {// 开启手机防盗

			Object[] objs = (Object[]) intent.getExtras().get("pdus");

			for (Object obj : objs) {

				SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
				String body = sms.getMessageBody();
				String sender = sms.getOriginatingAddress();

				Log.i(TAG, "开启手机防盗");

				if (sender.contains(safeNumber)) {

					if ("#*location*#".equals(body)) {// 获得gps数据

						// 开启获取gps服务
						Intent intent2 = new Intent(context, GPSService.class);
						context.startService(intent2);

						String lastlocation = sp.getString("lastlocation", null);
						if (TextUtils.isEmpty(lastlocation)) {
							SmsManager.getDefault().sendTextMessage(sender, null, "getting gps.....", null, null);
						} else {
							SmsManager.getDefault().sendTextMessage(sender, null, lastlocation, null, null);
						}

						abortBroadcast();// 中止这个广播

					} else if ("#*alarm*#".equals(body)) {// 播放报警音乐
						Log.i(TAG, "播放报警音乐");
						MediaPlayer player = MediaPlayer.create(context, R.raw.zufc);
						// player.setLooping(true);//循环播放
						player.setVolume(1.0f, 1.0f);
						player.start();
						abortBroadcast();// 中止这个广播

					} else if ("#*wipedata*#".equals(body)) {// 远程数据销毁

						// 清除Sdcard上的数据
						// dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
						// 恢复出厂设置
						// dpm.wipeData(0);

					} else if ("#*lockscreen*#".equals(body)) {// 远程锁屏
						String password = sp.getString("lockScreenPwd", null);
						Log.i(TAG, " 远程锁屏" + password);
						dpm.lockNow();// 锁屏
						dpm.resetPassword(password, 0);// 设置屏蔽密码要和上面这个方法一起用
//						dpm.removeActiveAdmin(who);取消某个应用的安全设备权限
//						dpm.setPasswordMinimumLength(admin, length);
//						dpm.setPasswordHistoryLength(admin, length);
						
						abortBroadcast();// 中止这个广播
					}

				}

			}
		}

	}

}
