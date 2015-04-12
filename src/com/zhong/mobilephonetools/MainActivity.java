package com.zhong.mobilephonetools;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

	private TextView tv_main_version;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tv_main_version = (TextView) findViewById(R.id.tv_main_versionName);
		tv_main_version.setText("°æ±¾"+getVersionName());

	}

	private String getVersionName() {
		PackageManager packageManager = getPackageManager();
		try {
			PackageInfo info = packageManager.getPackageInfo(getPackageName(),0);
			
			return info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}
