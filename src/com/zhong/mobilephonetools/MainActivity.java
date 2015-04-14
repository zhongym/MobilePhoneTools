package com.zhong.mobilephonetools;

import com.zhong.mobilephonetools.utils.Md5Utils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 软件的主界面
 * 
 * @author zhong
 *
 */
public class MainActivity extends BottomItemActivity {
	private static final String TAG = "MainActivity";

	private GridView gv;

	private EditText et_password;

	private EditText et_confirm_password;

	private Button bt_ok;

	private Button bt_break;

	private AlertDialog alertDialog;

	private SharedPreferences sp;

	private String[] names = { "缓存清理", "手机防盗", "通讯卫士", "进程管理", "流量统计", "手机杀毒" };
	/* "软件管理","高级工具","设置中心" */

	private int[] ics = { R.drawable.main_item_clean, R.drawable.main_item_safe, R.drawable.main_item_callmsgsafe,
			R.drawable.main_item_taskmanager, R.drawable.main_item_netmanager, R.drawable.main_item_trojan };

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "mainactivity用户可见");

		setContentView(R.layout.activity_main);
		gv = (GridView) findViewById(R.id.gv_main_item);
		sp = getSharedPreferences("config", MODE_PRIVATE);

		gv.setAdapter(new MyAdpter());
		// gv.setSelector(R.drawable.mian_item_layout_selector);

		gv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				view.setBackgroundResource(R.drawable.mian_item_layout_selector);

				switch (position) {
				case 0:
					Toast.makeText(MainActivity.this, "缓存清理", 0).show();
					break;

				case 1:// 进入手机防盗页面
					showLostFindDialog();
					break;
				case 2:
					Toast.makeText(MainActivity.this, "通讯卫士", 0).show();
					break;
				case 3:

					Toast.makeText(MainActivity.this, "进程管理", 0).show();
					break;
				case 4:

					Toast.makeText(MainActivity.this, "流量统计", 0).show();
					break;
				case 5:

					Toast.makeText(MainActivity.this, "手机杀毒", 0).show();
					break;
				}
			}

		});

	}

	/**
	 * <b>点击手机防盗的处理：<br>
	 * 1：从config配置文件取出password密码<br>
	 * 2：如果密码为空，即进入设置密码界面<br>
	 * 2：如果密码不为空，即进入输入密码界面<br>
	 */
	private void showLostFindDialog() {
		// 从配置文件查找密码，找到-->已经设置，没找到-->没有设置
		String password = sp.getString("password", null);

		if (TextUtils.isEmpty(password)) {// 还没有设置密码，提示用户设置密码
			// 显示设置密码的对话框

			showSetPasswordDialog();

		} else {// 已经设置密码，让用户输入密码
				// 显示输入密码的对话框
			showEnterPasswordDialog();

		}

	}

	/**
	 * <b>没有设置过密码--->显示设置密码对话框<b><br>
	 * 1:取出用户输入的再次密码<br>
	 * 2：如果有一次输入为空，提示用户密码不能为空<br>
	 * 3:判断再次输入密码是否一样<br>
	 * 4：如果不一样，提示用户“两次密码不一至”<br>
	 * 5：如果一样，保存密码到时config配置文件中。进入手机防盗界面<br>
	 */
	private void showSetPasswordDialog() {
		Log.i(TAG, "showSetPasswordDialog");
		AlertDialog.Builder builder = new Builder(this);
		View view = View.inflate(this, R.layout.dialog_set_password, null);
		et_password = (EditText) view.findViewById(R.id.et_password);
		et_confirm_password = (EditText) view.findViewById(R.id.et_confirm_password);
		bt_ok = (Button) view.findViewById(R.id.bt_ok);
		bt_break = (Button) view.findViewById(R.id.bt_break);

		bt_ok.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String password = et_password.getText().toString().trim();
				String confirmPassword = et_confirm_password.getText().toString().trim();

				if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
					Toast.makeText(MainActivity.this, "密码为空", 0).show();
					return;
				}

				if (!password.equals(confirmPassword)) {
					Toast.makeText(MainActivity.this, "再次密码不一至", 0).show();
					return;
				}

				Editor editor = sp.edit();
				editor.putString("password", Md5Utils.md5Password(password));
				editor.commit();

				alertDialog.dismiss();//
				Log.i(TAG, "密码已经设置，进入主介面");

				Intent intent = new Intent(MainActivity.this, LostFindActivity.class);
				startActivity(intent);

			}
		});

		bt_break.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				alertDialog.dismiss();// 关闭对话框
			}
		});

		builder.setView(view);
		alertDialog = builder.show();

	}

	/**
	 * <b> 已经设置过了密码-->显示输入密码对话框<b><br>
	 * 1:读取输入框的内容<br>
	 * 2:如果输入为空，提示用户“密码为空”<br>
	 * 3:如果输入不为空，取出保存在config文件中的密码<br>
	 * 4：判断输入密码与本地保存密码是否一样<br>
	 * 5：如果一样，进入手机防盗<br>
	 * 6：不一样，显示“密码错误”<br>
	 */
	private void showEnterPasswordDialog() {
		Log.i(TAG, "showEnterPasswordDialog");
		AlertDialog.Builder builder = new Builder(this);
		View view = View.inflate(this, R.layout.dialog_enter_password, null);
		et_password = (EditText) view.findViewById(R.id.et_password);
		bt_ok = (Button) view.findViewById(R.id.bt_ok);
		bt_break = (Button) view.findViewById(R.id.bt_break);

		bt_ok.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				String password = et_password.getText().toString().trim();
				if (TextUtils.isEmpty(password)) {
					Toast.makeText(MainActivity.this, "密码为空", 0).show();
					return;
				}

				String savePassword = sp.getString("password", null);

				if (Md5Utils.md5Password(password).equals(savePassword)) {
					alertDialog.dismiss();//
					Log.i(TAG, "密码正确，进入主介面");

					Intent intent = new Intent(MainActivity.this, LostFindActivity.class);
					startActivity(intent);

				} else {
					Toast.makeText(MainActivity.this, "密码错误", 0).show();
					Log.i(TAG, "密码已错误");
				}
			}
		});

		bt_break.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				alertDialog.dismiss();// 关闭对话框
			}
		});

		builder.setView(view);
		alertDialog = builder.show();

	}

	/**
	 * 为主界面的GridView准备数据
	 */
	private class MyAdpter extends BaseAdapter {

		public int getCount() {
			return names.length;
		}

		public Object getItem(int position) {
			return names[position];
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(MainActivity.this, R.layout.main_list_item, null);
			ImageView iv = (ImageView) view.findViewById(R.id.iv_list_item_main);
			TextView tv = (TextView) view.findViewById(R.id.tv_name_list_item_main);
			iv.setImageResource(ics[position]);
			tv.setText(names[position]);
			return view;
		}
	}

	protected void onDestroy() {
		Log.i(TAG, "mainactivity被销毁了");
		super.onDestroy();
	}

}
