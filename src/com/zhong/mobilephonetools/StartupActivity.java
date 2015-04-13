package com.zhong.mobilephonetools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.zhong.mobilephonetools.utils.StreamUtiles;

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

	/** 显示版本 */
	private TextView tv_main_version;

	/** 显示下载进度 */
	private TextView tv_main_downloadprocess;

	/** 更新信息的apkurl */
	private String apkurl;

	/** 更新信息的描述 */
	private String description;

	/** 更新信息的版本 */
	private String version;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ENTER_HOME:// 进入主页
				enterHome();
				break;
			case UPDATE_DIALOG:// 更新对话框
				updateApk();
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

		tv_main_downloadprocess = (TextView) findViewById(R.id.tv_main_downloadprocess);

		tv_main_version = (TextView) findViewById(R.id.tv_main_versionName);
		tv_main_version.setText("版本" + getVersionName());
		// 给启动页面添加个渐变动画
		AlphaAnimation animation = new AlphaAnimation(0.1f, 1.0f);
		animation.setDuration(700);// 设置持续时间
		findViewById(R.id.layout_startup).startAnimation(animation);
		checkVersionUpdate();

	}

	/**
	 * 下载新版本的apk进行更新安装
	 */
	protected void updateApk() {

		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("下载更新!");
		builder.setMessage(description);
		/**点击返回键，或者其它空白地方：取消*/
		builder.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				enterHome();//进入软件
				dialog.dismiss();//关闭对话框
			}
		});
		builder.setPositiveButton("立刻更新", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Log.i(TAG, "立刻更新");
				// 判断是否挂载sd卡
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

					// 开启下载内容

					FinalHttp finalHttp = new FinalHttp();
					String fileName = Environment.getExternalStorageDirectory().getPath() + "/MobilePhoneTools"
							+ getVersionName() + ".apk";
					Log.i(TAG, fileName);

					finalHttp.download(apkurl, fileName, new AjaxCallBack<File>() {

						/** 下载失败的回调方法 */
						public void onFailure(Throwable t, int errorNo, String strMsg) {
							t.printStackTrace();
							super.onFailure(t, errorNo, strMsg);
							Toast.makeText(StartupActivity.this, "对不起，下载失败", 1).show();
							enterHome();
						}

						/** 下载中的回调方法 */
						public void onLoading(long count, long current) {
							super.onLoading(count, current);
							tv_main_downloadprocess.setVisibility(View.VISIBLE);
							int process = (int) (current * 100 / count);
							tv_main_downloadprocess.setText("下载进度:" + process + "%");
						}

						/** 下载成功的回调方法 */
						public void onSuccess(File t) {
							super.onSuccess(t);
							Intent intent = new Intent();
							intent.setAction("android.intent.action.VIEW");
							intent.addCategory("android.intent.category.DEFAULT");
							intent.setDataAndType(Uri.fromFile(t), "application/vnd.android.package-archive");
							startActivity(intent);
							finish();
						}
					});

				} else {
					// 提示用户挂载sd卡
					Toast.makeText(StartupActivity.this, "没找到内存卡，请挂载sd卡", 0).show();
					return;
				}
			}
		});
		builder.setNegativeButton("下次更新", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Log.i(TAG, "下次更新");
				enterHome();
			}
		});

		builder.show();
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
//				long startTime = System.currentTimeMillis();

				ConnectivityManager cm = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
				boolean wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
				boolean sim = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();

				if (wifi || sim) {// 有开启wifi或者互联网

					try {

						URL url = new URL(getString(R.string.updateurl));

						HttpURLConnection conne = (HttpURLConnection) url.openConnection();
						conne.setRequestMethod("GET");
						conne.setConnectTimeout(2000);
						int code = conne.getResponseCode();
						if (code == 200) {
							InputStream in = conne.getInputStream();
							// 得到服务器返回的json数据
							String result = StreamUtiles.formateStream2String(in);
							Log.i(TAG, "联网成功" + result);

							// 解折json数据
							JSONObject obj = new JSONObject(result);
							version = (String) obj.get("version");
							description = obj.getString("description");
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

						/*long endTime = System.currentTimeMillis();
						long dtime = endTime - startTime;
						if (dtime < 1000) {
							try {
								Thread.sleep(1000 - dtime);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}*/

						handler.sendMessage(message);
					}

				} else {//没有开启wifi或者互联网
					// 不检查更新，直接进入软件
					Log.i(TAG, "没有可用网络，不检查更新，直接进入软件");

				/*	long endTime = System.currentTimeMillis();
					long dtime = endTime - startTime;
					if (dtime < 1000) {
						try {
							Thread.sleep(1000 - dtime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}*/
					enterHome();
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
