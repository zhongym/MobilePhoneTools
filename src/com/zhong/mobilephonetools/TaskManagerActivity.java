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
 * 进程管理activity
 *
 */
public class TaskManagerActivity extends Activity {

	private static final String TAG = "TaskManagerActivity";

	/** 显示进程总数 **/
	private TextView tv_processCount;

	/** 显示内存信息 **/
	private TextView tv_memory;

	/** 显示加载页面 **/
	private LinearLayout ll_load;

	/** 进程条目 */
	private ListView lv_task_list;

	/** 浮在上面的状态条 */
	private TextView tv_status;

	/** 所有的进程 */
	private List<TaskInfo> allTaskInfos;

	/** 用户的进程 */
	private List<TaskInfo> userTaskInfos;

	/** 系统的进程 */
	private List<TaskInfo> sysytemTaskInfos;

	/** ListView的数据处理器 */
	private TaskManagerAdapter adapter;

	/** 可用内存 */
	private long avaiMem;

	/** 总内存 */
	private long totalMem;

	/** 进程总数 */
	private int processCount;

	/** 下面的一排按键 */
	private LinearLayout ll_button;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_manager);
		Log.i(TAG, "TaskManagerActivity被创建了");

		tv_processCount = (TextView) findViewById(R.id.tv_process_count);
		tv_memory = (TextView) findViewById(R.id.tv_memory);
		ll_load = (LinearLayout) findViewById(R.id.ll_load);
		lv_task_list = (ListView) findViewById(R.id.lv_task_list);
		tv_status = (TextView) findViewById(R.id.tv_status);
		ll_button = (LinearLayout) findViewById(R.id.ll_button);

		processCount = SystemInfoUtils.getRunningProcessCount(this);
		avaiMem = SystemInfoUtils.getAvaiMemory(this);
		totalMem = SystemInfoUtils.getTotalMemory(this);

		tv_processCount.setText("进程数：" + processCount + "个");
		tv_memory.setText("剩余/总内存：" + Formatter.formatFileSize(this, avaiMem) + "/"
				+ Formatter.formatFileSize(this, totalMem));

		initData();// 加载进程列表

		/**
		 * listView的滑动事件
		 */
		lv_task_list.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (userTaskInfos != null && sysytemTaskInfos != null) {
					if (firstVisibleItem <= userTaskInfos.size()) {
						tv_status.setText("用户进程：" + userTaskInfos.size() + "个");
					} else {
						tv_status.setText("系统进程：" + sysytemTaskInfos.size() + "个");
					}
				}

			}
		});

		/**
		 * 点击listView的每个条目，选中checkBox
		 */
		lv_task_list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TaskInfo taskInfo;
				if (position == 0) {// 用户进程的标签
					return;
				} else if (position == (userTaskInfos.size() + 1)) {
					return;
				} else if (position <= userTaskInfos.size()) {
					taskInfo = userTaskInfos.get(position - 1);
				} else {
					taskInfo = sysytemTaskInfos.get(position - 1 - userTaskInfos.size() - 1);
				}
				// 自己这个软件不能选择
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
	 * 加载数据
	 */
	private void initData() {
		ll_load.setVisibility(View.VISIBLE);// 显示加载页面
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
			if (position == 0) {// 用户进程：10个

				TextView textView = new TextView(TaskManagerActivity.this);
				textView.setBackgroundColor(Color.GRAY);
				textView.setTextColor(Color.WHITE);
				textView.setTextSize(18);
				textView.setText("用户进程：" + userTaskInfos.size() + "个");
				return textView;
			} else if (position == (1 + userTaskInfos.size())) {// 系统进程：10个

				TextView textView = new TextView(TaskManagerActivity.this);
				textView.setBackgroundColor(Color.GRAY);
				textView.setTextColor(Color.WHITE);
				textView.setTextSize(18);
				textView.setText("系统进程：" + sysytemTaskInfos.size() + "个");
				return textView;
			} else if (position <= userTaskInfos.size()) {// 用户进程item
				info = userTaskInfos.get(position - 1);
			} else {// 系统进程item
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
			hodler.tv_memsize.setText("占用内存：" + Formatter.formatFileSize(getApplicationContext(), info.getTotalMem()));
			hodler.cb_status.setChecked(info.isChecked());

			// 自己这个软件不显示选择框
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
	 * 全选功能
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
	 * 反选功能
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
	 * 清除进程功能
	 */
	public void clear(View view) {

		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

		int count = 0;
		long killMem = 0;
		// 记录那些被杀死的条目
		List<TaskInfo> killedTaskinfos = new ArrayList<TaskInfo>();
		for (TaskInfo info : allTaskInfos) {
			if (info.isChecked()) {// 被勾选的，杀死这个进程。
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
			Toast.makeText(this, "请选择要清除的进程", 0).show();
		} else {

			allTaskInfos.removeAll(killedTaskinfos);
			adapter.notifyDataSetChanged();

			Toast.makeText(this, "杀死了" + count + "个进程，释放了" + Formatter.formatFileSize(this, killMem) + "的内存", 0).show();

			processCount -= count;
			avaiMem += killMem;
			// 更新内存显示小标签
			tv_processCount.setText("进程数：" + processCount + "个");
			tv_memory.setText("剩余/总内存：" + Formatter.formatFileSize(this, avaiMem) + "/"
					+ Formatter.formatFileSize(this, totalMem));
		}
	}

	/**
	 * 设置功能
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
