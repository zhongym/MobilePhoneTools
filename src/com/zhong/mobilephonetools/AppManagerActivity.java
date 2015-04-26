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

	/** ��ʾ�����ڴ��С */
	private TextView tv_inMenory;

	/** ��ʾ�����ڴ��С */
	private TextView tv_outMenory;

	/** ��ʾ���ؽ���ҳ�� */
	private LinearLayout ll_load_app;

	private AppAdapter adapter;

	/** ����Ӧ�� **/
	private List<AppInfo> appInfos;

	/** �û�Ӧ�� **/
	private List<AppInfo> userAppInfos;

	/** ϵͳӦ�� **/
	private List<AppInfo> systemAppInfos;

	/** ������Ӧ��״̬��ǩ */
	private TextView tv_tag;

	/** ����С���� **/
	private PopupWindow popupWindow;

	/** ����Ӧ�� **/
	private LinearLayout ll_app_start;

	/** ɾ��Ӧ�� **/
	private LinearLayout ll_app_del;

	/** ����Ӧ�� **/
	private LinearLayout ll_app_share;

	/** ���listView��Ŀʱ���ĸ�Ӧ�� **/
	private AppInfo appInfo;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		Log.i(TAG, "AppManagerActivity������");
		lv_applist = (ListView) findViewById(R.id.lv_applist);
		tv_inMenory = (TextView) findViewById(R.id.tv_in_menory);
		tv_outMenory = (TextView) findViewById(R.id.tv_out_menory);
		ll_load_app = (LinearLayout) findViewById(R.id.ll_load_app);
		tv_tag = (TextView) findViewById(R.id.tv_tag);

		tv_inMenory.setText("�ڲ��洢ʣ�ࣺ"
				+ Formatter.formatFileSize(this, getAvailSpace(Environment.getDataDirectory().getAbsolutePath())));

		tv_outMenory.setText("�ⲿ�洢ʣ�ࣺ"
				+ Formatter.formatFileSize(this, getAvailSpace(Environment.getExternalStorageDirectory()
						.getAbsolutePath())));

		fillData();// ��������

		// ��listviewע��һ�������ļ�����
		lv_applist.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			// ������ʱ����õķ�����
			// firstVisibleItem ��һ���ɼ���Ŀ��listview���������λ�á�
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

				popuWindowDismiss();// �رյ���С����

				if (userAppInfos != null && systemAppInfos != null) {
					if (firstVisibleItem > userAppInfos.size()) {
						tv_tag.setText("ϵͳӦ�ã�" + systemAppInfos.size() + "��");
					} else {
						tv_tag.setText("�û�����" + userAppInfos.size() + "��");
					}
				}
			}
		});

		// ��listView��ÿ����Ŀ��������¼����������С���ڣ�
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

				popuWindowDismiss();// �رյ���С����
				// ����С����
				View contentView = View.inflate(AppManagerActivity.this, R.layout.popupwindow_app_item, null);

				ll_app_start = (LinearLayout) contentView.findViewById(R.id.ll_app_item_start);
				ll_app_del = (LinearLayout) contentView.findViewById(R.id.ll_app_item_del);
				ll_app_share = (LinearLayout) contentView.findViewById(R.id.ll_app_item_share);

				ll_app_start.setOnClickListener(AppManagerActivity.this);
				ll_app_del.setOnClickListener(AppManagerActivity.this);
				ll_app_share.setOnClickListener(AppManagerActivity.this);

				popupWindow = new PopupWindow(contentView, -2, ViewGroup.LayoutParams.WRAP_CONTENT);// WRAP_CONTENT=-2
				// ����Ч���Ĳ��ű���Ҫ�����б�����ɫ��
				popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

				// ����С����
				int[] location = new int[2];
				view.getLocationOnScreen(location);
				int dip = 60;
				int xpx = DensityUtil.dip2px(getApplicationContext(), dip);
				popupWindow.showAtLocation(parent, Gravity.LEFT | Gravity.TOP, xpx, location[1]);
				// ���Ŷ���
				ScaleAnimation sa = new ScaleAnimation(0.3f, 1.0f, 0.3f, 1.0f, Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0.5f);
				sa.setDuration(200);
				contentView.startAnimation(sa);
			}

		});

	}

	/**
	 * ���������б�
	 */
	private void fillData() {
		ll_load_app.setVisibility(View.VISIBLE);// ��ʾ���ؽ���ҳ��
		new Thread() {// ��������Ӧ�ó���Ҫ�úö�ʱ��ģ��������̺߳�̨���ء�����ʾ����ҳ����û���
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

				runOnUiThread(new Runnable() {// �����̸߳��¸���ui
					public void run() {
						if (adapter == null) {
							adapter = new AppAdapter();
							lv_applist.setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}
						ll_load_app.setVisibility(View.INVISIBLE);// ������ɣ����ؼ��ؽ���ҳ��
						tv_tag.setVisibility(View.VISIBLE);
					}
				});
			};
		}.start();
	}

	/**
	 * �رյ���С����
	 */
	private void popuWindowDismiss() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			popupWindow = null;
		}
	}

	/**
	 * ���ݴ�������
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
			if (position == 0) {// ��ʾ�û�Ӧ�ø����ı�ǩ
				TextView tv = new TextView(AppManagerActivity.this);
				tv.setText("�û�Ӧ�ã�" + userAppInfos.size() + "��");
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextSize(18);
				tv.setTextColor(Color.parseColor("#ffffff"));
				return tv;
			}

			if (position == 1 + userAppInfos.size()) {// ��ʾϵͳӦ�ø����ı�ǩ
				TextView tv = new TextView(AppManagerActivity.this);
				tv.setText("ϵͳӦ�ã�" + systemAppInfos.size() + "��");
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextSize(18);
				tv.setTextColor(Color.parseColor("#ffffff"));
				return tv;
			}

			if (position <= userAppInfos.size()) {// �û�Ӧ��

				int newPosition = position - 1;
				info = userAppInfos.get(newPosition);
			} else {// ϵͳӦ��

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
			viewHolder.tv_verson.setText("�汾��" + version);

			return view;
		}
	}

	/**
	 * ���ڴ��ÿ��view������Ӻ��ӣ����ٺ��ӵĲ�ѯ
	 */
	static class ViewHolder {
		TextView tv_name;
		TextView tv_verson;
		ImageView iv_icon;
	}

	/**
	 * ���·�����ÿռ�
	 * 
	 * @param path
	 *            ·��
	 * @return ���ؿռ�Ĵ�Сlong
	 */
	private long getAvailSpace(String path) {
		StatFs statFs = new StatFs(path);
		long size = statFs.getBlockSize();
		long avCount = statFs.getAvailableBlocks();
		return size * avCount;
	}

	/**
	 * ���������ڵĵ���¼�
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_app_item_start:
			startApplication();// ����ĳ��Ӧ�ó̶�
			break;

		case R.id.ll_app_item_del:// ɾ��Ӧ��

			if (appInfo.isUserApp()) {
				unInstallApplication();
			} else {
				Toast.makeText(this, "ϵͳӦ��ֻ�л�ȡrootȨ�޲ſ���ж��", 0).show();
				// Runtime.getRuntime().exec("");
			}
			break;

		case R.id.ll_app_item_share:
			shareApplication();// ����Ӧ��
			break;
		}
	}

	/**
	 * ����Ӧ��
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
		intent.putExtra(Intent.EXTRA_TEXT, "�Ƽ���ʹ��һ�����,���ƽУ�" + appInfo.getName());
		startActivity(intent);
	}

	/**
	 * ɾ�����
	 */
	private void unInstallApplication() {
		popuWindowDismiss();
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		// intent.setAction("android.intent.action.DELETE");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(Uri.parse("package:" + appInfo.getPackageName()));// getPackageName()Ҫж������İ���
		startActivityForResult(intent, 0);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		fillData();// ɾ����������¼�������
	}

	/**
	 * ����ĳ��Ӧ�ó̶�
	 */
	private void startApplication() {
		popuWindowDismiss();

		// ��ѯ���Ӧ�ó�������activity�� ��������������
		PackageManager pm = getPackageManager();
		// Intent intent = new Intent();
		// intent.setAction("android.intent.action.MAIN");
		// intent.addCategory("android.intent.category.LAUNCHER");
		// //��ѯ���������е��ֻ��Ͼ�������������activity��
		// List<ResolveInfo> infos = pm.queryIntentActivities(intent,
		// PackageManager.GET_INTENT_FILTERS);

		// ���ݰ������ҳ�����û�����������activity
		Intent intent = pm.getLaunchIntentForPackage(appInfo.getPackageName());
		if (intent != null) {
			startActivity(intent);
		} else {
			Toast.makeText(this, "����������ǰӦ��", 0).show();
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		popuWindowDismiss();// �رջ����ڵĵ���
	}

}
