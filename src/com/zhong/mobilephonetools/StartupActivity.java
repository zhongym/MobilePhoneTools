package com.zhong.mobilephonetools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
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

/**
 * 启动界面
 * 
 * @author zhong
 *
 */
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

	private SharedPreferences sp;

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
		sp = getSharedPreferences("config", MODE_PRIVATE);

		tv_main_version.setText("版本" + getVersionName());

		// 给启动页面添加个渐变动画
		AlphaAnimation animation = new AlphaAnimation(0.1f, 1.0f);
		animation.setDuration(700);// 设置持续时间
		findViewById(R.id.layout_startup).startAnimation(animation);

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
		boolean wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
		boolean sim = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();

		boolean update = sp.getBoolean("update", false);// 得到用户设置是否自动更新的配置
		if (update) {// 如果设置为自动更新，
			Log.i(TAG, "用户设置自动更新，检查更新");
			if (wifi || sim) {// 有开启wifi或者互联网
				Log.i(TAG, "有可用网络，检查更新");

				checkVersionUpdate();

			} else {// 没有开启wifi或者互联网
				// 不检查更新，直接进入软件
				Log.i(TAG, "没有可用网络，不检查更新，直接进入软件");
				enterHome();
			}
		} else {// 不自动更新，直接进入
			Log.i(TAG, "用户设置不自动更新，不检查更新，直接进入软件");

			handler.postDelayed(new Runnable() {// 在启动页面等2s再进入主页面
						public void run() {
							enterHome();
						}
					}, 2000);
		}

		copyDB();// 将assets目录下的数据库address.db复制到data/data/包名/files/address.db

		installShortCut();// 创建快捷图标
	}

	/**
	 * 创建快捷图标
	 */
	private void installShortCut() {
		boolean shortcut = sp.getBoolean("shortcut", false);
		if (shortcut) {
			return;
		}

		Editor editor = sp.edit();
		// 发送广播的意图， 大吼一声告诉桌面，要创建快捷图标了
		Intent intent = new Intent();
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		// 快捷方式 要包含3个重要的信息 1，名称 2.图标 3.干什么事情
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "手机小管家");
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON,
				BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
		// 桌面点击图标对应的意图。
		Intent shortcutIntent = new Intent();
		shortcutIntent.setAction("android.intent.action.MAIN");
		shortcutIntent.addCategory("android.intent.category.LAUNCHER");
		shortcutIntent.setClassName(getPackageName(), "com.zhong.mobilephonetools.StartupActivity");
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		sendBroadcast(intent);
		editor.putBoolean("shortcut", true);
		editor.commit();
	}

	/**
	 * 将assets目录下的数据库address.db复制到data/data/包名/files/address.db
	 */
	private void copyDB() {

		File file = new File(getFilesDir(), "address.db");

		if (file.exists() && file.length() > 0) {
			Log.i(TAG, "address.db已经存在，需要复制");
		} else {
			InputStream is = null;
			OutputStream os = null;
			try {
				is = getAssets().open("address.db");
				os = new FileOutputStream(file);
				byte[] bu = new byte[1024];
				int len = 0;
				while ((len = is.read(bu)) != -1) {
					os.write(bu, 0, len);
				}

				Log.i(TAG, "复制数据库完成");
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (is != null) {
						is.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					if (os != null) {
						os.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	/**
	 * 下载新版本的apk进行更新安装
	 */
	protected void updateApk() {

		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("下载更新!");
		builder.setMessage(description);
		/** 点击返回键，或者其它空白地方：取消 */
		builder.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				enterHome();// 进入软件
				dialog.dismiss();// 关闭对话框
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
				long startTime = System.currentTimeMillis();
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

					long endTime = System.currentTimeMillis();
					long dtime = endTime - startTime;
					if (dtime < 1000) {
						try {
							Thread.sleep(1000 - dtime);
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
