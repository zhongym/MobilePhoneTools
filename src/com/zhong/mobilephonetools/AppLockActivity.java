package com.zhong.mobilephonetools;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhong.mobilephonetools.business.AppInfoProvider;
import com.zhong.mobilephonetools.dao.AppLockDao;
import com.zhong.mobilephonetools.domain.AppInfo;

/**
 * ���̹���activity
 *
 */
public class AppLockActivity extends Activity {

	private static final String TAG = "TaskManagerActivity";

	/** ��ʾ����ҳ�� **/
	private LinearLayout ll_load_app;

	/** ������Ŀ */
	private ListView lv_applist;

	/** ���������״̬�� */
	private TextView tv_tag;

	/** ���еĳ��� */
	private List<AppInfo> allAppInfos;

	/** �������� */
	private List<AppInfo> lockAppInfos;

	/** û�г��� */
	private List<AppInfo> unLockAppInfos;

	/** ListView�����ݴ����� */
	private AppInfoAdapter adapter;

	private AppLockDao dao;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_lock);
		Log.i(TAG, "AppLockActivity��������");

		ll_load_app = (LinearLayout) findViewById(R.id.ll_load_app);
		lv_applist = (ListView) findViewById(R.id.lv_applist);
		tv_tag = (TextView) findViewById(R.id.tv_tag);

		dao = new AppLockDao(this);

		initData();// ���ؽ����б�

		/**
		 * listView�Ļ����¼�
		 */
		lv_applist.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (lockAppInfos != null && unLockAppInfos != null) {
					if (firstVisibleItem <= lockAppInfos.size()) {
						tv_tag.setText("�Ѽ�������" + lockAppInfos.size() + "��");
					} else {
						tv_tag.setText("δ��������" + unLockAppInfos.size() + "��");
					}
				}

			}
		});

		/**
		 * ���listView��ÿ����Ŀ��ѡ��checkBox
		 */
		lv_applist.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				AppInfo appInfo;
				if (position == 0) {// �û����̵ı�ǩ
					return;
				} else if (position == (lockAppInfos.size() + 1)) {
					return;
				} else if (position <= lockAppInfos.size()) {
					appInfo = lockAppInfos.get(position - 1);
				} else {
					appInfo = unLockAppInfos.get(position - 1 - lockAppInfos.size() - 1);
				}

				if (getPackageName().equals(appInfo.getPackageName())) {
					return;
				}

				if (dao.find(appInfo.getPackageName())) {
					dao.delete(appInfo.getPackageName());
					lockAppInfos.remove(appInfo);
					unLockAppInfos.add(appInfo);
				} else {
					dao.add(appInfo.getPackageName());
					lockAppInfos.add(appInfo);
					unLockAppInfos.remove(appInfo);
				}
				adapter.notifyDataSetChanged();
			}
		});
	}

	/**
	 * ��������
	 */
	private void initData() {
		ll_load_app.setVisibility(View.VISIBLE);// ��ʾ����ҳ��
		new Thread() {
			public void run() {
				allAppInfos = AppInfoProvider.getAppInfosNoSize(getApplicationContext());
				lockAppInfos = new ArrayList<AppInfo>();
				unLockAppInfos = new ArrayList<AppInfo>();

				for (AppInfo info : allAppInfos) {
					if (dao.find(info.getPackageName())) {
						lockAppInfos.add(info);
					} else {
						unLockAppInfos.add(info);
					}

				}
				runOnUiThread(new Runnable() {
					public void run() {
						adapter = new AppInfoAdapter();
						lv_applist.setAdapter(adapter);

						ll_load_app.setVisibility(View.INVISIBLE);
						tv_tag.setText("�Ѽ�������" + lockAppInfos.size() + "��");
						tv_tag.setVisibility(View.VISIBLE);
					}
				});
			};
		}.start();

	}

	private class AppInfoAdapter extends BaseAdapter {

		public int getCount() {
			return 1 + lockAppInfos.size() + 1 + unLockAppInfos.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			AppInfo info;
			if (position == 0) {// �Ѽ�������10��

				TextView textView = new TextView(AppLockActivity.this);
				textView.setBackgroundColor(Color.GRAY);
				textView.setTextColor(Color.WHITE);
				textView.setTextSize(18);
				textView.setText("�Ѽ�������" + lockAppInfos.size() + "��");
				return textView;
			} else if (position == (1 + lockAppInfos.size())) {// δ��������10��

				TextView textView = new TextView(AppLockActivity.this);
				textView.setBackgroundColor(Color.GRAY);
				textView.setTextColor(Color.WHITE);
				textView.setTextSize(18);
				textView.setText("δ��������" + unLockAppInfos.size() + "��");
				return textView;
			} else if (position <= lockAppInfos.size()) {// �û�����item
				info = lockAppInfos.get(position - 1);
			} else {// ϵͳ����item
				int newPostition = position - 1 - lockAppInfos.size() - 1;
				info = unLockAppInfos.get(newPostition);
			}

			View view;
			ViewHodler hodler;
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				hodler = (ViewHodler) view.getTag();
			} else {
				view = View.inflate(getApplicationContext(), R.layout.list_item_app_lock, null);

				hodler = new ViewHodler();
				hodler.iv_appicon = (ImageView) view.findViewById(R.id.iv_appicon);
				hodler.tv_appname = (TextView) view.findViewById(R.id.tv_appname);
				hodler.iv_lock = (ImageView) view.findViewById(R.id.iv_lock);

				view.setTag(hodler);
			}
			hodler.iv_appicon.setImageDrawable(info.getIcon());
			hodler.tv_appname.setText(info.getName());

			// �Լ�����������ʾѡ���
			if (dao.find(info.getPackageName())) {
				hodler.iv_lock.setImageResource(R.drawable.locked);
			} else {
				hodler.iv_lock.setImageResource(R.drawable.unlock);
			}

			return view;
		}

	}

	static class ViewHodler {
		ImageView iv_appicon;
		TextView tv_appname;
		ImageView iv_lock;
	}

	/**
	 * ���������������
	 */
	public void appLockSet(View view) {

		Intent intent = new Intent(this, AppLockSettingActivity.class);
		startActivity(intent);
	}
}
