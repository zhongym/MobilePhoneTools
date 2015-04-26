package com.zhong.mobilephonetools;

import java.util.ArrayList;
import java.util.List;

import com.zhong.mobilephonetools.business.AppInfoProvider;
import com.zhong.mobilephonetools.domain.AppInfo;
import com.zhong.mobilephonetools.utils.DensityUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AppManagerActivity extends Activity implements OnClickListener {

	private static final String TAG = "AppManagerActivity";

	private ListView lv_applist;

	/** 显示内置内存大小 */
	private TextView tv_inMenory;

	/** 显示处置内存大小 */
	private TextView tv_outMenory;

	/** 显示加载进度页面 */
	private LinearLayout ll_load_app;

	private AppAdapter adapter;

	/** 所有应用 **/
	private List<AppInfo> appInfos;

	/** 用户应用 **/
	private List<AppInfo> userAppInfos;

	/** 系统应用 **/
	private List<AppInfo> systemAppInfos;

	/** 浮动的应用状态标签 */
	private TextView tv_tag;

	/** 弹出小窗口 **/
	private PopupWindow popupWindow;

	/** 启动应用 **/
	private LinearLayout ll_app_start;

	/** 删除应用 **/
	private LinearLayout ll_app_del;

	/** 分享应用 **/
	private LinearLayout ll_app_share;

	/** 点击listView条目时的哪个应用 **/
	private AppInfo appInfo;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		Log.i(TAG, "AppManagerActivity被创建");
		lv_applist = (ListView) findViewById(R.id.lv_applist);
		tv_inMenory = (TextView) findViewById(R.id.tv_in_menory);
		tv_outMenory = (TextView) findViewById(R.id.tv_out_menory);
		ll_load_app = (LinearLayout) findViewById(R.id.ll_load_app);
		tv_tag = (TextView) findViewById(R.id.tv_tag);

		tv_inMenory.setText("内部存储剩余："
				+ Formatter.formatFileSize(this, getAvailSpace(Environment.getDataDirectory().getAbsolutePath())));

		tv_outMenory.setText("外部存储剩余："
				+ Formatter.formatFileSize(this, getAvailSpace(Environment.getExternalStorageDirectory()
						.getAbsolutePath())));

		fillData();// 加载数据

		// 给listview注册一个滚动的监听器
		lv_applist.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			// 滚动的时候调用的方法。
			// firstVisibleItem 第一个可见条目在listview集合里面的位置。
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

				popuWindowDismiss();// 关闭弹出小窗口

				if (userAppInfos != null && systemAppInfos != null) {
					if (firstVisibleItem > userAppInfos.size()) {
						tv_tag.setText("系统应用：" + systemAppInfos.size() + "个");
					} else {
						tv_tag.setText("用户程序：" + userAppInfos.size() + "个");
					}
				}
			}
		});

		// 给listView的每个条目创建点击事件（点击弹出小窗口）
		lv_applist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				if (position == 0) {
					return;
				} else if (position == (1 + userAppInfos.size() + 1)) {
					return;
				} else if (position < userAppInfos.size()) {
					int newPosition = position - 1;
					appInfo = userAppInfos.get(newPosition);
				} else {

					int newPosition = position - 1 - userAppInfos.size() - 1;
					appInfo = systemAppInfos.get(newPosition);
				}

				Log.i(TAG, appInfo.getPackageName());

				popuWindowDismiss();// 关闭弹出小窗口
				// 配置小窗口
				View contentView = View.inflate(AppManagerActivity.this, R.layout.popupwindow_app_item, null);

				ll_app_start = (LinearLayout) contentView.findViewById(R.id.ll_app_item_start);
				ll_app_del = (LinearLayout) contentView.findViewById(R.id.ll_app_item_del);
				ll_app_share = (LinearLayout) contentView.findViewById(R.id.ll_app_item_share);

				ll_app_start.setOnClickListener(AppManagerActivity.this);
				ll_app_del.setOnClickListener(AppManagerActivity.this);
				ll_app_share.setOnClickListener(AppManagerActivity.this);

				popupWindow = new PopupWindow(contentView, -2, ViewGroup.LayoutParams.WRAP_CONTENT);// WRAP_CONTENT=-2
				// 动画效果的播放必须要求窗体有背景颜色。
				popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

				// 弹出小窗口
				int[] location = new int[2];
				view.getLocationOnScreen(location);
				int dip = 60;
				int xpx = DensityUtil.dip2px(getApplicationContext(), dip);
				popupWindow.showAtLocation(parent, Gravity.LEFT | Gravity.TOP, xpx, location[1]);
				// 缩放动画
				ScaleAnimation sa = new ScaleAnimation(0.3f, 1.0f, 0.3f, 1.0f, Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0.5f);
				sa.setDuration(200);
				contentView.startAnimation(sa);
			}

		});

	}

	/**
	 * 加载数据列表
	 */
	private void fillData() {
		ll_load_app.setVisibility(View.VISIBLE);// 显示加载进度页面
		new Thread() {// 加载所有应用程序要用好多时间的，放在子线程后台加载。先显示加载页面给用户看
			public void run() {
				appInfos = AppInfoProvider.getAppInfos(AppManagerActivity.this);
				userAppInfos = new ArrayList<AppInfo>();
				systemAppInfos = new ArrayList<AppInfo>();

				for (AppInfo info : appInfos) {
					if (info.isUserApp()) {
						userAppInfos.add(info);
					} else {
						systemAppInfos.add(info);
					}
				}

				runOnUiThread(new Runnable() {// 在主线程更新更新ui
					public void run() {
						if (adapter == null) {
							adapter = new AppAdapter();
							lv_applist.setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}
						ll_load_app.setVisibility(View.INVISIBLE);// 加载完成，隐藏加载进度页面
						tv_tag.setVisibility(View.VISIBLE);
					}
				});
			};
		}.start();
	}

	/**
	 * 关闭弹出小窗口
	 */
	private void popuWindowDismiss() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			popupWindow = null;
		}
	}

	/**
	 * 数据处理器类
	 */
	private class AppAdapter extends BaseAdapter {

		public int getCount() {
			return 1 + userAppInfos.size() + 1 + systemAppInfos.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			AppInfo info;
			if (position == 0) {// 显示用户应用个数的标签
				TextView tv = new TextView(AppManagerActivity.this);
				tv.setText("用户应用：" + userAppInfos.size() + "个");
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextSize(18);
				tv.setTextColor(Color.parseColor("#ffffff"));
				return tv;
			}

			if (position == 1 + userAppInfos.size()) {// 显示系统应用个数的标签
				TextView tv = new TextView(AppManagerActivity.this);
				tv.setText("系统应用：" + systemAppInfos.size() + "个");
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextSize(18);
				tv.setTextColor(Color.parseColor("#ffffff"));
				return tv;
			}

			if (position <= userAppInfos.size()) {// 用户应用

				int newPosition = position - 1;
				info = userAppInfos.get(newPosition);
			} else {// 系统应用

				int newPosition = position - 1 - userAppInfos.size() - 1;
				info = systemAppInfos.get(newPosition);

			}

			View view;
			ViewHolder viewHolder;
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(AppManagerActivity.this, R.layout.list_item_app, null);
				viewHolder = new ViewHolder();
				viewHolder.iv_icon = (ImageView) view.findViewById(R.id.iv_appicon);
				viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_appname);
				viewHolder.tv_verson = (TextView) view.findViewById(R.id.tv_versername);
				view.setTag(viewHolder);
			}

			viewHolder.iv_icon.setImageDrawable(info.getIcon());
			viewHolder.tv_name.setText(info.getName());
			
			String version = info.getVersionCode();
			if (version!=null&&version.length() > 21) {
				version = version.substring(0, 20);
			}
			viewHolder.tv_verson.setText("版本：" + version);

			return view;
		}
	}

	/**
	 * 用于存放每个view对象的子孩子，减少孩子的查询
	 */
	static class ViewHolder {
		TextView tv_name;
		TextView tv_verson;
		ImageView iv_icon;
	}

	/**
	 * 获得路径可用空间
	 * 
	 * @param path
	 *            路径
	 * @return 返回空间的大小long
	 */
	private long getAvailSpace(String path) {
		StatFs statFs = new StatFs(path);
		long size = statFs.getBlockSize();
		long avCount = statFs.getAvailableBlocks();
		return size * avCount;
	}

	/**
	 * 处理弹出窗口的点击事件
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_app_item_start:
			startApplication();// 启动某个应用程度
			break;

		case R.id.ll_app_item_del:// 删除应用

			if (appInfo.isUserApp()) {
				unInstallApplication();
			} else {
				Toast.makeText(this, "系统应用只有获取root权限才可以卸载", 0).show();
				// Runtime.getRuntime().exec("");
			}
			break;

		case R.id.ll_app_item_share:
			shareApplication();// 分享应用
			break;
		}
	}

	/**
	 * 分享应用
	 */
	private void shareApplication() {
		popuWindowDismiss();
		// Intent { act=android.intent.action.SEND typ=text/plain flg=0x3000000
		// cmp=com.android.mms/.ui.ComposeMessageActivity (has extras) } from
		// pid 256
		Intent intent = new Intent();
		intent.setAction("android.intent.action.SEND");
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "推荐您使用一款软件,名称叫：" + appInfo.getName());
		startActivity(intent);
	}

	/**
	 * 删除软件
	 */
	private void unInstallApplication() {
		popuWindowDismiss();
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		// intent.setAction("android.intent.action.DELETE");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(Uri.parse("package:" + appInfo.getPackageName()));// getPackageName()要卸载软件的包名
		startActivityForResult(intent, 0);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		fillData();// 删除软件后重新加载数据
	}

	/**
	 * 启动某个应用程度
	 */
	private void startApplication() {
		popuWindowDismiss();

		// 查询这个应用程序的入口activity。 把他开启起来。
		PackageManager pm = getPackageManager();
		// Intent intent = new Intent();
		// intent.setAction("android.intent.action.MAIN");
		// intent.addCategory("android.intent.category.LAUNCHER");
		// //查询出来了所有的手机上具有启动能力的activity。
		// List<ResolveInfo> infos = pm.queryIntentActivities(intent,
		// PackageManager.GET_INTENT_FILTERS);

		// 根据包名，找出这个用户的启动界面activity
		Intent intent = pm.getLaunchIntentForPackage(appInfo.getPackageName());
		if (intent != null) {
			startActivity(intent);
		} else {
			Toast.makeText(this, "不能启动当前应用", 0).show();
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		popuWindowDismiss();// 关闭还存在的弹窗
	}

}
