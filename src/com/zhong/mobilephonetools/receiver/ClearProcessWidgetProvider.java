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
	 * 每次android:updatePeriodMillis="1800000"到时执行
	 * 由于默认情况下最小更新时间是半小时。所以我们要自已去编写更新代码
	 */
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);

	}

	/**
	 * 第一次被创建的时候执行
	 */
	public void onEnabled(Context context) {
		super.onEnabled(context);

		Intent service = new Intent(context, WidgetBgService.class);
		context.startService(service);
	}

	/**
	 * 最后一次删除时执行
	 */
	public void onDisabled(Context context) {
		super.onDisabled(context);
		Intent service = new Intent(context, WidgetBgService.class);
		context.stopService(service);
	}
}
