package com.zhong.mobilephonetools;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhong.mobilephonetools.utils.StreamUtiles;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

public class StartupActivity extends Activity {

	protected static final String TAG = "MainActivity";
	/** Message what:进入主页 */
	protected static final int ENTER_HOME = 0;
	/** Message what:更新对话框 */
	protected static final int UPDATE_DIALOG = 1;
	/** Message what:网络错误 */
	protected static final int NETWORK_ERROR = 2;
	/** Message what:JSON错误 */
	protected static final int JSON_ERROR = 3;

	private TextView tv_main_version;
	
	private String apkurl;
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ENTER_HOME:// 进入主页
				enterHome();
				break;
			case UPDATE_DIALOG:// 更新对话框
				Toast.makeText(getApplicationContext(), "更新软件", 0).show();
				break;
			case NETWORK_ERROR:// 网络错误
				Toast.makeText(getApplicationContext(), "网络错误", 0).show();
				enterHome();
				break;
			case JSON_ERROR:// json错误
				Toast.makeText(StartupActivity.this, "json错误", 0).show();
				enterHome();
				break;
			}
		}

	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_startup);

		tv_main_version = (TextView) findViewById(R.id.tv_main_versionName);
		tv_main_version.setText("版本" + getVersionName());

		// 给启动页面添加个渐变动画
		AlphaAnimation animation = new AlphaAnimation(0.1f, 1.0f);
		animation.setDuration(1500);// 设置持续时间
		findViewById(R.id.layout_startup).startAnimation(animation);
		checkVersionUpdate();

	}

	/**
	 * 进入软件主页
	 */
	private void enterHome() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();// 关闭当前窗口，防止用户返回时两次进入软件
	};

	/**
	 * 在子线程中检查更新
	 */
	private void checkVersionUpdate() {
		new Thread() {
			public void run() {

				Message message = Message.obtain();
				long startTime = System.currentTimeMillis();
				try {

					URL url = new URL(getString(R.string.updateurl));

					HttpURLConnection conne = (HttpURLConnection) url.openConnection();
					conne.setRequestMethod("GET");
					conne.setReadTimeout(4000);
					int code = conne.getResponseCode();
					if (code == 200) {
						InputStream in = conne.getInputStream();
						// 得到服务器返回的json数据
						String result = StreamUtiles.formateStream2String(in);
						Log.i(TAG, "联网成功" + result);

						// 解折json数据
						JSONObject obj = new JSONObject(result);
						String version = (String) obj.get("version");
						String description = obj.getString("description");
						apkurl = obj.getString("apkurl");

						if (getVersionName().endsWith(version)) {// 版本一样，不用更新
							Log.i(TAG, "不需要更新");
							message.what = ENTER_HOME;

						} else {// 需要更新
							Log.i(TAG, "需要更新");
							message.what = UPDATE_DIALOG;
						}

					} else {
						Log.i(TAG, "系统繁忙");
						message.what = ENTER_HOME;
					}

				} catch (IOException e) {
					Log.i(TAG, "联网失败");
					e.printStackTrace();
					message.what = NETWORK_ERROR;

				} catch (JSONException e) {
					Log.i(TAG, "JSON失败");
					e.printStackTrace();
					message.what = JSON_ERROR;

				} finally {

					long endTime = System.currentTimeMillis();
					long dtime = endTime - startTime;
					if (dtime < 2000) {
						try {
							Thread.sleep(2000 - dtime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					handler.sendMessage(message);
				}

			};
		}.start();

	}

	/**
	 * 读取清单文件中的版本名称
	 * 
	 * @return 版本名称
	 */
	private String getVersionName() {
		try {
			PackageManager packageManager = getPackageManager();
			PackageInfo info = packageManager.getPackageInfo(getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}
