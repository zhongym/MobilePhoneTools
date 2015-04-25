package com.zhong.mobilephonetools;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhong.mobilephonetools.dao.BlackNumberDao;
import com.zhong.mobilephonetools.domain.BlackNumberInfo;

/**
 * 通讯卫士主页：<br>
 * 
 * <li>1：列出所有黑名单<br> <li>2：添加黑名单<br>
 * <ol>
 * 2.1手动输入 <br>
 * 2.2从联系人中添加
 * </ol> <li>3：移除黑名单<br> <li>4：修改黑名单<br>
 *
 */
public class CallSmsSafeActivity extends Activity {

	private static final String TAG = "CallSmsSafeActivity";

	private ListView lv_blackNumber;

	private BlackNumberDao dao;

	private List<BlackNumberInfo> infos;

	private BlackNumberAdapter adapter;

	/** 每次查询多少条 **/
	private int limit = 20;
	/** 从哪条开始查询 **/
	private int offset = 0;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "CallSmsSafeActivity被创建了");
		setContentView(R.layout.activity_call_sms_safe);

		dao = new BlackNumberDao(this);
		infos = dao.findAllForPage(limit, offset);

		lv_blackNumber = (ListView) findViewById(R.id.lv_black_number);

		adapter = new BlackNumberAdapter();
		lv_blackNumber.setAdapter(adapter);

		// 给listView每一条item添加点击事件
		lv_blackNumber.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				BlackNumberInfo info = infos.get(position);
				showUpdateBlackNumberDialog(info.getName(), info.getNumber(), info.getMode());// 黑名单修改框
			}
		});

		// 给listView添加滑动事件，当滑动到最后一条的时候，加载新的数据
		lv_blackNumber.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {

				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:// 空闲状态

					// 判断当前listview滚动的位置
					// 获取最后一个可见条目在集合里面的位置
					int lastpostion = lv_blackNumber.getLastVisiblePosition();

					// 一：分批加载，每次加载到的都添加到集合里面，用户可以看到之前加载的数据
					// // 集合里面有20个item 位置从0开始的 最后一个条目的位置 19
					// if(lastpostion==(infos.size()-1)){
					// offset+=limit;
					// infos.addAll(dao.findAllForPage(limit, offset));
					// adapter.notifyDataSetChanged();//通知数据处理器数据更新了
					// Log.i(TAG, "再向下加载20条数据");
					// }

					// 二：分页加载，每次加载出新的，旧的不再保留
					// 向下一页
					// 集合里面有20个item 位置从0开始的 最后一个条目的位置 19
					if (lastpostion == (infos.size() - 1)) {

						offset += limit;
						List<BlackNumberInfo> is = dao.findAllForPage(limit, offset);
						if (is.size() < limit) {
							infos.addAll(is);
						} else {
							infos = is;
						}
						adapter.notifyDataSetChanged();// 通知数据处理器数据更新了
						// 在adapter.notifyDataSetChanged();之后调用ListView.setSelection(0);就可以回到想要的位置
						lv_blackNumber.setSelection(1);
						Log.i(TAG, "再向下加载20条数据");
					}

					// 向上一页
					int firstVisiblePosition = lv_blackNumber.getFirstVisiblePosition();
					if (offset > 0 && firstVisiblePosition == 0) {
						offset -= limit;
						infos = dao.findAllForPage(limit, offset);
						adapter.notifyDataSetChanged();// 通知数据处理器数据更新了
						lv_blackNumber.setSelection(19);
						Log.i(TAG, "再向上加载20条数据");
					}

					break;
				case OnScrollListener.SCROLL_STATE_FLING:// 惯性滑行状态

					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 手指触摸滚动

					break;

				}

			}

			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

			}
		});
	}

	class BlackNumberAdapter extends BaseAdapter {

		public int getCount() {
			return infos.size();
		}

		public Object getItem(int position) {
			return infos.get(position);
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {

			View view;
			ViewHolder holder;
			// 复用页面不显示的view对象，不用再创建新的view对象。
			// convertView是刚刚在界面上不可见的哪一个，被传回来,刚打开当前activity时，convertview为null。
			if (convertView == null) {
				view = View.inflate(CallSmsSafeActivity.this, R.layout.black_number_item, null);

				holder = new ViewHolder();
				holder.tv_name = (TextView) view.findViewById(R.id.tv_black_number_name);
				holder.tv_mode = (TextView) view.findViewById(R.id.tv_black_number_mode);
				holder.iv_del = (ImageView) view.findViewById(R.id.iv_del_blacknumber);
				// 当孩子生出来的时候找到他们的引用，存放在记事本，放在父亲的口袋
				view.setTag(holder);

				Log.i(TAG, "创建新的view" + position);

			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
				Log.i(TAG, "复用view" + position);
			}

			BlackNumberInfo info = infos.get(position);
			String name = info.getName() + "(" + info.getNumber() + ")";
			holder.tv_name.setText(name);

			String mode = info.getMode();
			if ("1".equals(mode)) {
				holder.tv_mode.setText("电话拦截 ");
			} else if ("2".equals(mode)) {
				holder.tv_mode.setText("短信拦截");
			} else {
				holder.tv_mode.setText("全部拦截");
			}

			// 删除黑名单的事件
			holder.iv_del.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					AlertDialog.Builder builder = new Builder(CallSmsSafeActivity.this);
					builder.setTitle("警告");
					builder.setMessage("确定要删除这条记录么？");

					builder.setNegativeButton("取消", null);

					builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 删除数据库的内容
							dao.delete(infos.get(position).getNumber());
							// 更新界面。
							infos.remove(position);
							// 通知listview数据适配器更新
							adapter.notifyDataSetChanged();
						}
					});
					builder.show();
				}
			});

			return view;
		}
	}

	/**
	 * view对象的容器 记录孩子的内存地址。 相当于一个记事本
	 */
	class ViewHolder {
		TextView tv_name;
		TextView tv_mode;
		ImageView iv_del;
	}

	/**
	 * 单击添加黑名单的单击事件-->显示添加黑名单的方法对话框
	 */
	public void addBlackNumberWay(View view) {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("添加黑名单");
		String[] items = { "手工输入号码", "从联系人添加" };
		builder.setItems(items, new AlertDialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					showAddBlackNumberDialog("", "");// 显示添加黑名单号码操作框
					break;
				case 1:
					Intent intent = new Intent(CallSmsSafeActivity.this, SelectContactsActivity.class);
					startActivityForResult(intent, 0);
					break;
				}
			}
		});
		builder.show();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {
			String name = data.getStringExtra("name");
			String phone = data.getStringExtra("phone");
			if (phone != null) {
				showAddBlackNumberDialog(name, phone);// 显示添加黑名单号码操作框
			}
		}

	}

	private EditText et_name;
	private EditText et_number;
	private CheckBox cb_sms;
	private CheckBox cb_phone;
	private Button bt_ok;
	private Button bt_break;

	/**
	 * 显示更改黑名单号码操作框
	 * 
	 * @param name
	 *            输入框显示的名称
	 * @param number
	 *            输入框显示的号码
	 * @param mode
	 *            /**拦截模式: 1.电话拦截 2.短信拦截 3.全部拦截*
	 */
	private void showUpdateBlackNumberDialog(String name, String number, String mode) {
		AlertDialog.Builder builder = new Builder(this);
		final AlertDialog dialog = builder.create();

		View view = View.inflate(this, R.layout.dialog_add_black_number, null);
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();

		et_name = (EditText) view.findViewById(R.id.et_name);
		et_number = (EditText) view.findViewById(R.id.et_number);
		cb_sms = (CheckBox) view.findViewById(R.id.cb_sms);
		cb_phone = (CheckBox) view.findViewById(R.id.cb_phone);
		bt_ok = (Button) view.findViewById(R.id.bt_ok);
		bt_break = (Button) view.findViewById(R.id.bt_break);

		// CheckBox数据回显
		if ("1".equals(mode)) {
			cb_phone.setChecked(true);
		}

		if ("2".equals(mode)) {
			cb_sms.setChecked(true);
		}

		if ("3".equals(mode)) {
			cb_phone.setChecked(true);
			cb_sms.setChecked(true);
		}

		// editText数据回显
		et_name.setText(name);
		et_number.setText(number);
		et_number.setFocusable(false);// 号码是不能更改的

		bt_break.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		bt_ok.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String name = et_name.getText().toString().trim();
				String number = et_number.getText().toString().trim();
				if (TextUtils.isEmpty(number)) {
					Toast.makeText(CallSmsSafeActivity.this, "号码不能为空", 0).show();
					return;
				}

				String mode;
				if (cb_sms.isChecked() && cb_phone.isChecked()) {// 拦截全部
					mode = "3";
				} else if (cb_sms.isChecked()) {// 短信拦截
					mode = "2";
				} else if (cb_phone.isChecked()) {// 电话拦截
					mode = "1";
				} else {
					Toast.makeText(CallSmsSafeActivity.this, "请选择拦截模式", 0).show();
					return;
				}
				// 添加到时数据库
				dao.update(name, number, mode);

				// 更新listView的数据
				BlackNumberInfo info = new BlackNumberInfo(name, number, mode);
				infos.remove(info);
				infos.add(0, info);
				adapter.notifyDataSetChanged();
				dialog.dismiss();
			}
		});

	}

	/**
	 * 显示添加黑名单号码操作框
	 * 
	 * @param name
	 *            输入框显示的名称
	 * @param number
	 *            输入框显示的号码
	 * @param mode
	 *            /**拦截模式: 1.电话拦截 2.短信拦截 3.全部拦截*
	 */
	private void showAddBlackNumberDialog(String name, String number) {
		AlertDialog.Builder builder = new Builder(this);
		final AlertDialog dialog = builder.create();

		View view = View.inflate(this, R.layout.dialog_add_black_number, null);
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();

		et_name = (EditText) view.findViewById(R.id.et_name);
		et_number = (EditText) view.findViewById(R.id.et_number);
		cb_sms = (CheckBox) view.findViewById(R.id.cb_sms);
		cb_phone = (CheckBox) view.findViewById(R.id.cb_phone);
		bt_ok = (Button) view.findViewById(R.id.bt_ok);
		bt_break = (Button) view.findViewById(R.id.bt_break);

		et_name.setText(name);
		et_number.setText(number);

		bt_break.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		bt_ok.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String name = et_name.getText().toString().trim();
				String number = et_number.getText().toString().trim();
				if (TextUtils.isEmpty(number)) {
					Toast.makeText(CallSmsSafeActivity.this, "号码不能为空", 0).show();
					return;
				}

				String mode;
				if (cb_sms.isChecked() && cb_phone.isChecked()) {// 拦截全部
					mode = "3";
				} else if (cb_sms.isChecked()) {// 短信拦截
					mode = "2";
				} else if (cb_phone.isChecked()) {// 电话拦截
					mode = "1";
				} else {
					Toast.makeText(CallSmsSafeActivity.this, "请选择拦截模式", 0).show();
					return;
				}
				// 添加到时数据库
				dao.add(name, number, mode);

				// 更新listView的数据
				BlackNumberInfo info = new BlackNumberInfo(name, number, mode);
				infos.add(0, info);
				adapter.notifyDataSetChanged();
				dialog.dismiss();
			}
		});

	}
}
