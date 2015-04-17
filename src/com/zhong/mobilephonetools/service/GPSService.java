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
	// �õ�λ�÷���
	private LocationManager lm;
	private MyLocationListener listener;

	public void onCreate() {
		super.onCreate();
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);

		listener = new MyLocationListener();
		// ע�����λ�÷���
		// ��λ���ṩ����������
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String proveder = lm.getBestProvider(criteria, true);
		
		lm.requestLocationUpdates(proveder, 0, 0, listener);
	}

	public void onDestroy() {
		super.onDestroy();
		// ȡ������λ�÷���
		lm.removeUpdates(listener);
		listener = null;
	}

	class MyLocationListener implements LocationListener {

		/**
		 * ��λ�øı��ʱ��ص�
		 */

		@Override
		public void onLocationChanged(Location location) {
			String longitude = "j:" + location.getLongitude() + "\n";
			String latitude = "w:" + location.getLatitude() + "\n";
			String accuracy = "a" + location.getAccuracy() + "\n";
			// �����Ÿ���ȫ����

			SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString("lastlocation", longitude + latitude + accuracy);
			editor.commit();

		}

		/**
		 * ��״̬�����ı��ʱ��ص� ����--�ر� ���ر�--����
		 */
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

			
			
		}

		/**
		 * ĳһ��λ���ṩ�߿���ʹ����
		 */
		@Override
		public void onProviderEnabled(String provider) {

		}

		/**
		 * ĳһ��λ���ṩ�߲�����ʹ����
		 */
		@Override
		public void onProviderDisabled(String provider) {

		}

	}

	public IBinder onBind(Intent intent) {
		return null;
	}

}
