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
 * ��������
 * 
 * @author zhong
 *
 */
public class StartupActivity extends Activity {

	protected static final String TAG = "MainActivity";
	/** Message what:������ҳ */
	protected static final int ENTER_HOME = 0;
	/** Message what:���¶Ի��� */
	protected static final int UPDATE_DIALOG = 1;
	/** Message what:������� */
	protected static final int NETWORK_ERROR = 2;
	/** Message what:JSON���� */
	protected static final int JSON_ERROR = 3;

	/** ��ʾ�汾 */
	private TextView tv_main_version;

	/** ��ʾ���ؽ��� */
	private TextView tv_main_downloadprocess;

	/** ������Ϣ��apkurl */
	private String apkurl;

	/** ������Ϣ������ */
	private String description;

	/** ������Ϣ�İ汾 */
	private String version;

	private SharedPreferences sp;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ENTER_HOME:// ������ҳ
				enterHome();
				break;
			case UPDATE_DIALOG:// ���¶Ի���
				updateApk();
				break;
			case NETWORK_ERROR:// �������
				Toast.makeText(getApplicationContext(), "�������", 0).show();
				enterHome();
				break;
			case JSON_ERROR:// json����
				Toast.makeText(StartupActivity.this, "json����", 0).show();
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

		tv_main_version.setText("�汾" + getVersionName());

		// ������ҳ����Ӹ����䶯��
		AlphaAnimation animation = new AlphaAnimation(0.1f, 1.0f);
		animation.setDuration(700);// ���ó���ʱ��
		findViewById(R.id.layout_startup).startAnimation(animation);

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
		boolean wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
		boolean sim = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();

		boolean update = sp.getBoolean("update", false);// �õ��û������Ƿ��Զ����µ�����
		if (update) {// �������Ϊ�Զ����£�
			Log.i(TAG, "�û������Զ����£�������");
			if (wifi || sim) {// �п���wifi���߻�����
				Log.i(TAG, "�п������磬������");

				checkVersionUpdate();

			} else {// û�п���wifi���߻�����
				// �������£�ֱ�ӽ������
				Log.i(TAG, "û�п������磬�������£�ֱ�ӽ������");
				enterHome();
			}
		} else {// ���Զ����£�ֱ�ӽ���
			Log.i(TAG, "�û����ò��Զ����£��������£�ֱ�ӽ������");

			handler.postDelayed(new Runnable() {// ������ҳ���2s�ٽ�����ҳ��
						public void run() {
							enterHome();
						}
					}, 2000);
		}

		copyDB();// ��assetsĿ¼�µ����ݿ�address.db���Ƶ�data/data/����/files/address.db

		installShortCut();// �������ͼ��
	}

	/**
	 * �������ͼ��
	 */
	private void installShortCut() {
		boolean shortcut = sp.getBoolean("shortcut", false);
		if (shortcut) {
			return;
		}

		Editor editor = sp.edit();
		// ���͹㲥����ͼ�� ���һ���������棬Ҫ�������ͼ����
		Intent intent = new Intent();
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		// ��ݷ�ʽ Ҫ����3����Ҫ����Ϣ 1������ 2.ͼ�� 3.��ʲô����
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "�ֻ�С�ܼ�");
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON,
				BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
		// ������ͼ���Ӧ����ͼ��
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
	 * ��assetsĿ¼�µ����ݿ�address.db���Ƶ�data/data/����/files/address.db
	 */
	private void copyDB() {

		File file = new File(getFilesDir(), "address.db");

		if (file.exists() && file.length() > 0) {
			Log.i(TAG, "address.db�Ѿ����ڣ���Ҫ����");
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

				Log.i(TAG, "�������ݿ����");
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
	 * �����°汾��apk���и��°�װ
	 */
	protected void updateApk() {

		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("���ظ���!");
		builder.setMessage(description);
		/** ������ؼ������������հ׵ط���ȡ�� */
		builder.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				enterHome();// �������
				dialog.dismiss();// �رնԻ���
			}
		});
		builder.setPositiveButton("���̸���", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Log.i(TAG, "���̸���");
				// �ж��Ƿ����sd��
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

					// ������������

					FinalHttp finalHttp = new FinalHttp();
					String fileName = Environment.getExternalStorageDirectory().getPath() + "/MobilePhoneTools"
							+ getVersionName() + ".apk";
					Log.i(TAG, fileName);

					finalHttp.download(apkurl, fileName, new AjaxCallBack<File>() {

						/** ����ʧ�ܵĻص����� */
						public void onFailure(Throwable t, int errorNo, String strMsg) {
							t.printStackTrace();
							super.onFailure(t, errorNo, strMsg);
							Toast.makeText(StartupActivity.this, "�Բ�������ʧ��", 1).show();
							enterHome();
						}

						/** �����еĻص����� */
						public void onLoading(long count, long current) {
							super.onLoading(count, current);
							tv_main_downloadprocess.setVisibility(View.VISIBLE);
							int process = (int) (current * 100 / count);
							tv_main_downloadprocess.setText("���ؽ���:" + process + "%");
						}

						/** ���سɹ��Ļص����� */
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
					// ��ʾ�û�����sd��
					Toast.makeText(StartupActivity.this, "û�ҵ��ڴ濨�������sd��", 0).show();
					return;
				}
			}
		});
		builder.setNegativeButton("�´θ���", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Log.i(TAG, "�´θ���");
				enterHome();
			}
		});

		builder.show();
	}

	/**
	 * ���������ҳ
	 */
	private void enterHome() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();// �رյ�ǰ���ڣ���ֹ�û�����ʱ���ν������
	};

	/**
	 * �����߳��м�����
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
						// �õ����������ص�json����
						String result = StreamUtiles.formateStream2String(in);
						Log.i(TAG, "�����ɹ�" + result);

						// ����json����
						JSONObject obj = new JSONObject(result);
						version = (String) obj.get("version");
						description = obj.getString("description");
						apkurl = obj.getString("apkurl");

						if (getVersionName().endsWith(version)) {// �汾һ�������ø���
							Log.i(TAG, "����Ҫ����");
							message.what = ENTER_HOME;

						} else {// ��Ҫ����
							Log.i(TAG, "��Ҫ����");
							message.what = UPDATE_DIALOG;
						}

					} else {
						Log.i(TAG, "ϵͳ��æ");
						message.what = ENTER_HOME;
					}

				} catch (IOException e) {
					Log.i(TAG, "����ʧ��");
					e.printStackTrace();
					message.what = NETWORK_ERROR;

				} catch (JSONException e) {
					Log.i(TAG, "JSONʧ��");
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
	 * ��ȡ�嵥�ļ��еİ汾����
	 * 
	 * @return �汾����
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
