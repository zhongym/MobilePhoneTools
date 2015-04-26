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
		Log.i(TAG, "WidgetBgService��������");
		// �õ�����AppWidget�Ķ���
		awm = AppWidgetManager.getInstance(this);

		timer = new Timer();
		task = new TimerTask() {
			public void run() {
				Log.i(TAG, "ִ��WidgetBgService��������");

				ComponentName componentName = new ComponentName(WidgetBgService.this, ClearProcessWidgetProvider.class);
				// �õ�С�ؼ��Ĳ����ļ�
				RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);
				views.setTextViewText(R.id.process_count,
						"�������е����:" + SystemInfoUtils.getRunningProcessCount(getApplicationContext()) + "��");

				long size = SystemInfoUtils.getAvaiMemory(getApplicationContext());
				views.setTextViewText(R.id.process_memory,
						"�����ڴ棺" + Formatter.formatFileSize(getApplicationContext(), size));

				// ����һ������,����������������һ��Ӧ�ó���ִ�е�.
				// �Զ���һ���㲥�¼�,ɱ����̨���ȵ��¼�
				Intent intent = new Intent("com.zhong.action.KillAll");
				PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent,
						PendingIntent.FLAG_UPDATE_CURRENT);
				views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);

				awm.updateAppWidget(componentName, views);// ���½���
			}
		};

		timer.schedule(task, 0, 3000);// ÿ3s��ִ��һ������
	}

	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "WidgetBgService����ֹͣ��");
		timer.cancel();
		task.cancel();
		timer = null;
		task = null;
	}
}
