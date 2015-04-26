package com.zhong.mobilephonetools;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhong.mobilephonetools.business.TaskInfoProvider;
import com.zhong.mobilephonetools.domain.TaskInfo;
import com.zhong.mobilephonetools.utils.SystemInfoUtils;

/**
 * ���̹���activity
 *
 */
public class TaskManagerActivity extends Activity {

	private static final String TAG = "TaskManagerActivity";

	/** ��ʾ�������� **/
	private TextView tv_processCount;

	/** ��ʾ�ڴ���Ϣ **/
	private TextView tv_memory;

	/** ��ʾ����ҳ�� **/
	private LinearLayout ll_load;

	/** ������Ŀ */
	private ListView lv_task_list;

	/** ���������״̬�� */
	private TextView tv_status;

	/** ���еĽ��� */
	private List<TaskInfo> allTaskInfos;

	/** �û��Ľ��� */
	private List<TaskInfo> userTaskInfos;

	/** ϵͳ�Ľ��� */
	private List<TaskInfo> sysytemTaskInfos;

	/** ListView�����ݴ����� */
	private TaskManagerAdapter adapter;

	/** �����ڴ� */
	private long avaiMem;

	/** ���ڴ� */
	private long totalMem;

	/** �������� */
	private int processCount;

	/** �����һ�Ű��� */
	private LinearLayout ll_button;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_manager);
		Log.i(TAG, "TaskManagerActivity��������");

		tv_processCount = (TextView) findViewById(R.id.tv_process_count);
		tv_memory = (TextView) findViewById(R.id.tv_memory);
		ll_load = (LinearLayout) findViewById(R.id.ll_load);
		lv_task_list = (ListView) findViewById(R.id.lv_task_list);
		tv_status = (TextView) findViewById(R.id.tv_status);
		ll_button = (LinearLayout) findViewById(R.id.ll_button);

		processCount = SystemInfoUtils.getRunningProcessCount(this);
		avaiMem = SystemInfoUtils.getAvaiMemory(this);
		totalMem = SystemInfoUtils.getTotalMemory(this);

		tv_processCount.setText("��������" + processCount + "��");
		tv_memory.setText("ʣ��/���ڴ棺" + Formatter.formatFileSize(this, avaiMem) + "/"
				+ Formatter.formatFileSize(this, totalMem));

		initData();// ���ؽ����б�

		/**
		 * listView�Ļ����¼�
		 */
		lv_task_list.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (userTaskInfos != null && sysytemTaskInfos != null) {
					if (firstVisibleItem <= userTaskInfos.size()) {
						tv_status.setText("�û����̣�" + userTaskInfos.size() + "��");
					} else {
						tv_status.setText("ϵͳ���̣�" + sysytemTaskInfos.size() + "��");
					}
				}

			}
		});

		/**
		 * ���listView��ÿ����Ŀ��ѡ��checkBox
		 */
		lv_task_list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TaskInfo taskInfo;
				if (position == 0) {// �û����̵ı�ǩ
					return;
				} else if (position == (userTaskInfos.size() + 1)) {
					return;
				} else if (position <= userTaskInfos.size()) {
					taskInfo = userTaskInfos.get(position - 1);
				} else {
					taskInfo = sysytemTaskInfos.get(position - 1 - userTaskInfos.size() - 1);
				}
				// �Լ�����������ѡ��
				if (getPackageName().equals(taskInfo.getPackageName())) {
					return;
				}

				ViewHodler holder = (ViewHodler) view.getTag();
				if (taskInfo.isChecked()) {
					taskInfo.setChecked(false);
					holder.cb_status.setChecked(false);
				} else {
					taskInfo.setChecked(true);
					holder.cb_status.setChecked(true);
				}

			}
		});
	}

	/**
	 * ��������
	 */
	private void initData() {
		ll_load.setVisibility(View.VISIBLE);// ��ʾ����ҳ��
		new Thread() {
			public void run() {
				allTaskInfos = TaskInfoProvider.getAllRunningTaskInfo(getApplicationContext());
				userTaskInfos = new ArrayList<TaskInfo>();
				sysytemTaskInfos = new ArrayList<TaskInfo>();

				for (TaskInfo info : allTaskInfos) {
					if (info.isUserApp()) {
						userTaskInfos.add(info);
					} else {
						sysytemTaskInfos.add(info);
					}
				}
				runOnUiThread(new Runnable() {
					public void run() {
						adapter = new TaskManagerAdapter();
						lv_task_list.setAdapter(adapter);

						ll_load.setVisibility(View.INVISIBLE);
						tv_status.setVisibility(View.VISIBLE);
						ll_button.setVisibility(View.VISIBLE);
					}
				});
			};
		}.start();

	}

	private class TaskManagerAdapter extends BaseAdapter {
		public int getCount() {
			SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
			
			if (sp.getBoolean("showSystemProcess", false)) {
				return 1 + userTaskInfos.size() + 1 + sysytemTaskInfos.size();
			} else {
				return 1 + userTaskInfos.size();
			}
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			TaskInfo info;
			if (position == 0) {// �û����̣�10��

				TextView textView = new TextView(TaskManagerActivity.this);
				textView.setBackgroundColor(Color.GRAY);
				textView.setTextColor(Color.WHITE);
				textView.setTextSize(18);
				textView.setText("�û����̣�" + userTaskInfos.size() + "��");
				return textView;
			} else if (position == (1 + userTaskInfos.size())) {// ϵͳ���̣�10��

				TextView textView = new TextView(TaskManagerActivity.this);
				textView.setBackgroundColor(Color.GRAY);
				textView.setTextColor(Color.WHITE);
				textView.setTextSize(18);
				textView.setText("ϵͳ���̣�" + sysytemTaskInfos.size() + "��");
				return textView;
			} else if (position <= userTaskInfos.size()) {// �û�����item
				info = userTaskInfos.get(position - 1);
			} else {// ϵͳ����item
				int newPostition = position - 1 - userTaskInfos.size() - 1;
				info = sysytemTaskInfos.get(newPostition);
			}

			View view;
			ViewHodler hodler;
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				hodler = (ViewHodler) view.getTag();
			} else {
				view = View.inflate(getApplicationContext(), R.layout.list_item_task, null);

				hodler = new ViewHodler();
				hodler.tv_taskname = (TextView) view.findViewById(R.id.tv_task_name);
				hodler.tv_memsize = (TextView) view.findViewById(R.id.tv_memsize);
				hodler.cb_status = (CheckBox) view.findViewById(R.id.cb_status);
				hodler.iv_icon = (ImageView) view.findViewById(R.id.iv_task);

				view.setTag(hodler);
			}

			hodler.iv_icon.setImageDrawable(info.getIcon());
			hodler.tv_taskname.setText(info.getName());
			hodler.tv_memsize.setText("ռ���ڴ棺" + Formatter.formatFileSize(getApplicationContext(), info.getTotalMem()));
			hodler.cb_status.setChecked(info.isChecked());

			// �Լ�����������ʾѡ���
			if (getPackageName().equals(info.getPackageName())) {
				hodler.cb_status.setVisibility(View.INVISIBLE);
			} else {
				hodler.cb_status.setVisibility(View.VISIBLE);
			}

			return view;
		}

	}

	static class ViewHodler {
		ImageView iv_icon;
		TextView tv_taskname;
		TextView tv_memsize;
		CheckBox cb_status;
	}

	/**
	 * ȫѡ����
	 */
	public void selectAll(View view) {

		for (TaskInfo info : allTaskInfos) {
			if (getPackageName().equals(info.getPackageName())) {
				continue;
			}
			info.setChecked(true);
		}
		adapter.notifyDataSetChanged();
	}

	/**
	 * ��ѡ����
	 */
	public void selectOpp(View view) {
		for (TaskInfo info : allTaskInfos) {
			if (getPackageName().equals(info.getPackageName())) {
				continue;
			}
			info.setChecked(!info.isChecked());
		}
		adapter.notifyDataSetChanged();
	}

	/**
	 * ������̹���
	 */
	public void clear(View view) {

		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

		int count = 0;
		long killMem = 0;
		// ��¼��Щ��ɱ������Ŀ
		List<TaskInfo> killedTaskinfos = new ArrayList<TaskInfo>();
		for (TaskInfo info : allTaskInfos) {
			if (info.isChecked()) {// ����ѡ�ģ�ɱ��������̡�
				am.killBackgroundProcesses(info.getPackageName());
				if (info.isUserApp()) {
					userTaskInfos.remove(info);
				} else {
					sysytemTaskInfos.remove(info);
				}

				count++;
				killMem += info.getTotalMem();
				killedTaskinfos.add(info);
			}
		}

		if (count == 0) {
			Toast.makeText(this, "��ѡ��Ҫ����Ľ���", 0).show();
		} else {

			allTaskInfos.removeAll(killedTaskinfos);
			adapter.notifyDataSetChanged();

			Toast.makeText(this, "ɱ����" + count + "�����̣��ͷ���" + Formatter.formatFileSize(this, killMem) + "���ڴ�", 0).show();

			processCount -= count;
			avaiMem += killMem;
			// �����ڴ���ʾС��ǩ
			tv_processCount.setText("��������" + processCount + "��");
			tv_memory.setText("ʣ��/���ڴ棺" + Formatter.formatFileSize(this, avaiMem) + "/"
					+ Formatter.formatFileSize(this, totalMem));
		}
	}

	/**
	 * ���ù���
	 */
	public void enterSetting(View view) {
		Intent intent = new Intent(this, TaskManagerSettingActivity.class);
		startActivityForResult(intent, 0);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		adapter.notifyDataSetChanged();
	}
}
