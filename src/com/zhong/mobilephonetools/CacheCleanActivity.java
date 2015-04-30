package com.zhong.mobilephonetools;

import java.lang.reflect.Method;
import java.util.List;

import android.app.Activity;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.IPackageDataObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 缓存清理
 *
 */
public class CacheCleanActivity extends Activity {

	protected static final int SCANNGING = 0;
	protected static final int FINISH = 1;
	private ImageView iv_ratea;
	private TextView tv_scanning;
	private LinearLayout ll_layout_app_item;
	private ListView lv_app_item;
	private PackageManager pm;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clean_cache);

		iv_ratea = (ImageView) findViewById(R.id.iv_ratea);
		tv_scanning = (TextView) findViewById(R.id.tv_scanning);
		ll_layout_app_item = (LinearLayout) findViewById(R.id.ll_layout_app_item);
		// lv_app_item=(ListView) findViewById(R.id.lv_app_item);

		RotateAnimation rota = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		rota.setDuration(1000);// 一次动画的持续时间
		LinearInterpolator lin = new LinearInterpolator();// 匀速
		rota.setInterpolator(lin);// interpolator表示变化率，但不是运行速度
		rota.setRepeatCount(Animation.INFINITE);// 重复模式：无限重复
		iv_ratea.startAnimation(rota);

		pm = getPackageManager();

		new Thread() {
			public void run() {

				Method[] methods = PackageManager.class.getMethods();
				Method getPackageSizeInfo = null;
				for (Method method : methods) {
					if ("getPackageSizeInfo".equals(method.getName())) {
						getPackageSizeInfo = method;
					}
				}

				List<PackageInfo> infos = pm.getInstalledPackages(0);
				for (PackageInfo info : infos) {

					AppInfo appInfo = new AppInfo();

					final String packname = info.packageName;
					runOnUiThread(new Runnable() {
						public void run() {
							tv_scanning.setText("正在扫描:" + packname);

						}
					});

					appInfo.icon = info.applicationInfo.loadIcon(pm);
					final String name = info.applicationInfo.loadLabel(pm).toString();
					appInfo.packname = packname;
					appInfo.name = name;

					try {
						getPackageSizeInfo.invoke(pm, packname, new CodeSize(appInfo));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				runOnUiThread(new Runnable() {
					public void run() {
						int count = ll_layout_app_item.getChildCount();
						if (count < 1) {
							TextView child = new TextView(getApplicationContext());
							child.setText("没有应用缓存");
							child.setTextSize(25);
							child.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
							child.setGravity(Gravity.CENTER_HORIZONTAL);
							ll_layout_app_item.addView(child);
						}
						tv_scanning.setText("扫描完成");
						iv_ratea.clearAnimation();

					}
				});

			};
		}.start();

	}

	class AppInfo {
		String packname;
		Drawable icon;
		String name;
		boolean isCheck;
	}

	/**
	 * 实现IPackageStatsObserver.Stub，获取程序的大小
	 * 
	 * @author zhong
	 *
	 */
	private class CodeSize extends IPackageStatsObserver.Stub {

		AppInfo info;

		public CodeSize(AppInfo info) {
			super();
			this.info = info;
		}

		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {

			final long size = pStats.cacheSize;// /data/data/包名/cache缓存的大小
			if (size > 0) {

				runOnUiThread(new Runnable() {
					public void run() {
						View view = View.inflate(getApplicationContext(), R.layout.list_item_app_clean_cache, null);
						ImageView iv_appicon = (ImageView) view.findViewById(R.id.iv_appicon);
						TextView tv_appname = (TextView) view.findViewById(R.id.tv_appname);
						TextView tv_cache_size = (TextView) view.findViewById(R.id.tv_cache_size);
						final CheckBox cb_status = (CheckBox) view.findViewById(R.id.cb_status);

						iv_appicon.setImageDrawable(info.icon);
						tv_appname.setText(info.name);
						tv_cache_size.setText(Formatter.formatFileSize(getApplicationContext(), size));
						cb_status.setChecked(true);

						view.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								cb_status.setChecked(!cb_status.isChecked());
							}
						});

						ll_layout_app_item.addView(view, 0);
						// lv_app_item.addView(view, 0);
					}
				});

			}

		}
	}

	/**
	 * 清理全部
	 * 
	 * @param view
	 */
	public void cleanCache(View view) {
		Method[] methods = PackageManager.class.getMethods();
		for (Method method : methods) {
			if ("freeStorageAndNotify".equals(method.getName())) {
				try {
					method.invoke(pm, Integer.MAX_VALUE, new MypackDataObserver());
				} catch (Exception e) {
					e.printStackTrace();
				}

				runOnUiThread(new Runnable() {
					public void run() {
						ll_layout_app_item.removeAllViews();

						TextView child = new TextView(getApplicationContext());
						child.setText("清理完成");
						child.setTextSize(25);
						child.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
						child.setGravity(Gravity.CENTER_HORIZONTAL);
						ll_layout_app_item.addView(child);
					}
				});

				return;
			}
		}
	}

	private class MypackDataObserver extends IPackageDataObserver.Stub {
		public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
			System.out.println(packageName + succeeded);
		}
	}
}
