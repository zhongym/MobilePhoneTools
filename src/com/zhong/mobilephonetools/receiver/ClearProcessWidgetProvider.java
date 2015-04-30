package com.zhong.mobilephonetools.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.zhong.mobilephonetools.service.WidgetBgService;

public class ClearProcessWidgetProvider extends AppWidgetProvider {

	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);

//		if (!ServiceUtils.isServiceRunnign(context, "com.zhong.mobilephonetools.service.WidgetBgService")) {
//			Intent service = new Intent(context, WidgetBgService.class);
//			context.startService(service);
//		}
		
	}

	/**
	 * ÿ��android:updatePeriodMillis="1800000"��ʱִ��
	 * ����Ĭ���������С����ʱ���ǰ�Сʱ����������Ҫ����ȥ��д���´���
	 */
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);

	}

	/**
	 * ��һ�α�������ʱ��ִ��
	 */
	public void onEnabled(Context context) {
		super.onEnabled(context);

		Intent service = new Intent(context, WidgetBgService.class);
		context.startService(service);
	}

	/**
	 * ���һ��ɾ��ʱִ��
	 */
	public void onDisabled(Context context) {
		super.onDisabled(context);
		Intent service = new Intent(context, WidgetBgService.class);
		context.stopService(service);
	}
}
