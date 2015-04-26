package com.zhong.mobilephonetools.service;

import java.util.Timer;
import java.util.TimerTask;

import com.zhong.mobilephonetools.R;
import com.zhong.mobilephonetools.receiver.ClearProcessWidgetProvider;
import com.zhong.mobilephonetools.utils.SystemInfoUtils;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

public class WidgetBgService extends Service {

	protected static final String TAG = "WidgetBgService";
	private Timer timer;
	private TimerTask task;
	private AppWidgetManager awm;

	public IBinder onBind(Intent intent) {

		return null;
	}

	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "WidgetBgService服务开启了");
		// 得到管理AppWidget的对象
		awm = AppWidgetManager.getInstance(this);

		timer = new Timer();
		task = new TimerTask() {
			public void run() {
				Log.i(TAG, "执行WidgetBgService更新内容");

				ComponentName componentName = new ComponentName(WidgetBgService.this, ClearProcessWidgetProvider.class);
				// 得到小控件的布局文件
				RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);
				views.setTextViewText(R.id.process_count,
						"正在运行的软件:" + SystemInfoUtils.getRunningProcessCount(getApplicationContext()) + "个");

				long size = SystemInfoUtils.getAvaiMemory(getApplicationContext());
				views.setTextViewText(R.id.process_memory,
						"可用内存：" + Formatter.formatFileSize(getApplicationContext(), size));

				// 描述一个动作,这个动作是由另外的一个应用程序执行的.
				// 自定义一个广播事件,杀死后台进度的事件
				Intent intent = new Intent("com.zhong.action.KillAll");
				PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent,
						PendingIntent.FLAG_UPDATE_CURRENT);
				views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);

				awm.updateAppWidget(componentName, views);// 更新界面
			}
		};

		timer.schedule(task, 0, 3000);// 每3s钟执行一次任务
	}

	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "WidgetBgService服务停止了");
		timer.cancel();
		task.cancel();
		timer = null;
		task = null;
	}
}
