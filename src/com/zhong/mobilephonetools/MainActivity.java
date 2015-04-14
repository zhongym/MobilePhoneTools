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
 * �����������
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

	private String[] names = { "��������", "�ֻ�����", "ͨѶ��ʿ", "���̹���", "����ͳ��", "�ֻ�ɱ��" };
	/* "�������","�߼�����","��������" */

	private int[] ics = { R.drawable.main_item_clean, R.drawable.main_item_safe, R.drawable.main_item_callmsgsafe,
			R.drawable.main_item_taskmanager, R.drawable.main_item_netmanager, R.drawable.main_item_trojan };

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "mainactivity�û��ɼ�");

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
					Toast.makeText(MainActivity.this, "��������", 0).show();
					break;

				case 1:// �����ֻ�����ҳ��
					showLostFindDialog();
					break;
				case 2:
					Toast.makeText(MainActivity.this, "ͨѶ��ʿ", 0).show();
					break;
				case 3:

					Toast.makeText(MainActivity.this, "���̹���", 0).show();
					break;
				case 4:

					Toast.makeText(MainActivity.this, "����ͳ��", 0).show();
					break;
				case 5:

					Toast.makeText(MainActivity.this, "�ֻ�ɱ��", 0).show();
					break;
				}
			}

		});

	}

	/**
	 * <b>����ֻ������Ĵ���<br>
	 * 1����config�����ļ�ȡ��password����<br>
	 * 2���������Ϊ�գ������������������<br>
	 * 2��������벻Ϊ�գ������������������<br>
	 */
	private void showLostFindDialog() {
		// �������ļ��������룬�ҵ�-->�Ѿ����ã�û�ҵ�-->û������
		String password = sp.getString("password", null);

		if (TextUtils.isEmpty(password)) {// ��û���������룬��ʾ�û���������
			// ��ʾ��������ĶԻ���

			showSetPasswordDialog();

		} else {// �Ѿ��������룬���û���������
				// ��ʾ��������ĶԻ���
			showEnterPasswordDialog();

		}

	}

	/**
	 * <b>û�����ù�����--->��ʾ��������Ի���<b><br>
	 * 1:ȡ���û�������ٴ�����<br>
	 * 2�������һ������Ϊ�գ���ʾ�û����벻��Ϊ��<br>
	 * 3:�ж��ٴ����������Ƿ�һ��<br>
	 * 4�������һ������ʾ�û����������벻һ����<br>
	 * 5�����һ�����������뵽ʱconfig�����ļ��С������ֻ���������<br>
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
					Toast.makeText(MainActivity.this, "����Ϊ��", 0).show();
					return;
				}

				if (!password.equals(confirmPassword)) {
					Toast.makeText(MainActivity.this, "�ٴ����벻һ��", 0).show();
					return;
				}

				Editor editor = sp.edit();
				editor.putString("password", Md5Utils.md5Password(password));
				editor.commit();

				alertDialog.dismiss();//
				Log.i(TAG, "�����Ѿ����ã�����������");

				Intent intent = new Intent(MainActivity.this, LostFindActivity.class);
				startActivity(intent);

			}
		});

		bt_break.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				alertDialog.dismiss();// �رնԻ���
			}
		});

		builder.setView(view);
		alertDialog = builder.show();

	}

	/**
	 * <b> �Ѿ����ù�������-->��ʾ��������Ի���<b><br>
	 * 1:��ȡ����������<br>
	 * 2:�������Ϊ�գ���ʾ�û�������Ϊ�ա�<br>
	 * 3:������벻Ϊ�գ�ȡ��������config�ļ��е�����<br>
	 * 4���ж����������뱾�ر��������Ƿ�һ��<br>
	 * 5�����һ���������ֻ�����<br>
	 * 6����һ������ʾ���������<br>
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
					Toast.makeText(MainActivity.this, "����Ϊ��", 0).show();
					return;
				}

				String savePassword = sp.getString("password", null);

				if (Md5Utils.md5Password(password).equals(savePassword)) {
					alertDialog.dismiss();//
					Log.i(TAG, "������ȷ������������");

					Intent intent = new Intent(MainActivity.this, LostFindActivity.class);
					startActivity(intent);

				} else {
					Toast.makeText(MainActivity.this, "�������", 0).show();
					Log.i(TAG, "�����Ѵ���");
				}
			}
		});

		bt_break.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				alertDialog.dismiss();// �رնԻ���
			}
		});

		builder.setView(view);
		alertDialog = builder.show();

	}

	/**
	 * Ϊ�������GridView׼������
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
		Log.i(TAG, "mainactivity��������");
		super.onDestroy();
	}

}
