package com.zhong.mobilephonetools.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class GPSService extends Service {
	// 用到位置服务
	private LocationManager lm;
	private MyLocationListener listener;

	public void onCreate() {
		super.onCreate();
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);

		listener = new MyLocationListener();
		// 注册监听位置服务
		// 给位置提供者设置条件
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String proveder = lm.getBestProvider(criteria, true);
		
		lm.requestLocationUpdates(proveder, 0, 0, listener);
	}

	public void onDestroy() {
		super.onDestroy();
		// 取消监听位置服务
		lm.removeUpdates(listener);
		listener = null;
	}

	class MyLocationListener implements LocationListener {

		/**
		 * 当位置改变的时候回调
		 */

		@Override
		public void onLocationChanged(Location location) {
			String longitude = "j:" + location.getLongitude() + "\n";
			String latitude = "w:" + location.getLatitude() + "\n";
			String accuracy = "a" + location.getAccuracy() + "\n";
			// 发短信给安全号码

			SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString("lastlocation", longitude + latitude + accuracy);
			editor.commit();

		}

		/**
		 * 当状态发生改变的时候回调 开启--关闭 ；关闭--开启
		 */
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

			
			
		}

		/**
		 * 某一个位置提供者可以使用了
		 */
		@Override
		public void onProviderEnabled(String provider) {

		}

		/**
		 * 某一个位置提供者不可以使用了
		 */
		@Override
		public void onProviderDisabled(String provider) {

		}

	}

	public IBinder onBind(Intent intent) {
		return null;
	}

}
