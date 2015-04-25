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
 * ͨѶ��ʿ��ҳ��<br>
 * 
 * <li>1���г����к�����<br> <li>2����Ӻ�����<br>
 * <ol>
 * 2.1�ֶ����� <br>
 * 2.2����ϵ�������
 * </ol> <li>3���Ƴ�������<br> <li>4���޸ĺ�����<br>
 *
 */
public class CallSmsSafeActivity extends Activity {

	private static final String TAG = "CallSmsSafeActivity";

	private ListView lv_blackNumber;

	private BlackNumberDao dao;

	private List<BlackNumberInfo> infos;

	private BlackNumberAdapter adapter;

	/** ÿ�β�ѯ������ **/
	private int limit = 20;
	/** ��������ʼ��ѯ **/
	private int offset = 0;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "CallSmsSafeActivity��������");
		setContentView(R.layout.activity_call_sms_safe);

		dao = new BlackNumberDao(this);
		infos = dao.findAllForPage(limit, offset);

		lv_blackNumber = (ListView) findViewById(R.id.lv_black_number);

		adapter = new BlackNumberAdapter();
		lv_blackNumber.setAdapter(adapter);

		// ��listViewÿһ��item��ӵ���¼�
		lv_blackNumber.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				BlackNumberInfo info = infos.get(position);
				showUpdateBlackNumberDialog(info.getName(), info.getNumber(), info.getMode());// �������޸Ŀ�
			}
		});

		// ��listView��ӻ����¼��������������һ����ʱ�򣬼����µ�����
		lv_blackNumber.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {

				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:// ����״̬

					// �жϵ�ǰlistview������λ��
					// ��ȡ���һ���ɼ���Ŀ�ڼ��������λ��
					int lastpostion = lv_blackNumber.getLastVisiblePosition();

					// һ���������أ�ÿ�μ��ص��Ķ���ӵ��������棬�û����Կ���֮ǰ���ص�����
					// // ����������20��item λ�ô�0��ʼ�� ���һ����Ŀ��λ�� 19
					// if(lastpostion==(infos.size()-1)){
					// offset+=limit;
					// infos.addAll(dao.findAllForPage(limit, offset));
					// adapter.notifyDataSetChanged();//֪ͨ���ݴ��������ݸ�����
					// Log.i(TAG, "�����¼���20������");
					// }

					// ������ҳ���أ�ÿ�μ��س��µģ��ɵĲ��ٱ���
					// ����һҳ
					// ����������20��item λ�ô�0��ʼ�� ���һ����Ŀ��λ�� 19
					if (lastpostion == (infos.size() - 1)) {

						offset += limit;
						List<BlackNumberInfo> is = dao.findAllForPage(limit, offset);
						if (is.size() < limit) {
							infos.addAll(is);
						} else {
							infos = is;
						}
						adapter.notifyDataSetChanged();// ֪ͨ���ݴ��������ݸ�����
						// ��adapter.notifyDataSetChanged();֮�����ListView.setSelection(0);�Ϳ��Իص���Ҫ��λ��
						lv_blackNumber.setSelection(1);
						Log.i(TAG, "�����¼���20������");
					}

					// ����һҳ
					int firstVisiblePosition = lv_blackNumber.getFirstVisiblePosition();
					if (offset > 0 && firstVisiblePosition == 0) {
						offset -= limit;
						infos = dao.findAllForPage(limit, offset);
						adapter.notifyDataSetChanged();// ֪ͨ���ݴ��������ݸ�����
						lv_blackNumber.setSelection(19);
						Log.i(TAG, "�����ϼ���20������");
					}

					break;
				case OnScrollListener.SCROLL_STATE_FLING:// ���Ի���״̬

					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// ��ָ��������

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
			// ����ҳ�治��ʾ��view���󣬲����ٴ����µ�view����
			// convertView�Ǹո��ڽ����ϲ��ɼ�����һ������������,�մ򿪵�ǰactivityʱ��convertviewΪnull��
			if (convertView == null) {
				view = View.inflate(CallSmsSafeActivity.this, R.layout.black_number_item, null);

				holder = new ViewHolder();
				holder.tv_name = (TextView) view.findViewById(R.id.tv_black_number_name);
				holder.tv_mode = (TextView) view.findViewById(R.id.tv_black_number_mode);
				holder.iv_del = (ImageView) view.findViewById(R.id.iv_del_blacknumber);
				// ��������������ʱ���ҵ����ǵ����ã�����ڼ��±������ڸ��׵Ŀڴ�
				view.setTag(holder);

				Log.i(TAG, "�����µ�view" + position);

			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
				Log.i(TAG, "����view" + position);
			}

			BlackNumberInfo info = infos.get(position);
			String name = info.getName() + "(" + info.getNumber() + ")";
			holder.tv_name.setText(name);

			String mode = info.getMode();
			if ("1".equals(mode)) {
				holder.tv_mode.setText("�绰���� ");
			} else if ("2".equals(mode)) {
				holder.tv_mode.setText("��������");
			} else {
				holder.tv_mode.setText("ȫ������");
			}

			// ɾ�����������¼�
			holder.iv_del.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					AlertDialog.Builder builder = new Builder(CallSmsSafeActivity.this);
					builder.setTitle("����");
					builder.setMessage("ȷ��Ҫɾ��������¼ô��");

					builder.setNegativeButton("ȡ��", null);

					builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// ɾ�����ݿ������
							dao.delete(infos.get(position).getNumber());
							// ���½��档
							infos.remove(position);
							// ֪ͨlistview��������������
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
	 * view��������� ��¼���ӵ��ڴ��ַ�� �൱��һ�����±�
	 */
	class ViewHolder {
		TextView tv_name;
		TextView tv_mode;
		ImageView iv_del;
	}

	/**
	 * ������Ӻ������ĵ����¼�-->��ʾ��Ӻ������ķ����Ի���
	 */
	public void addBlackNumberWay(View view) {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("��Ӻ�����");
		String[] items = { "�ֹ��������", "����ϵ�����" };
		builder.setItems(items, new AlertDialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					showAddBlackNumberDialog("", "");// ��ʾ��Ӻ��������������
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
				showAddBlackNumberDialog(name, phone);// ��ʾ��Ӻ��������������
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
	 * ��ʾ���ĺ��������������
	 * 
	 * @param name
	 *            �������ʾ������
	 * @param number
	 *            �������ʾ�ĺ���
	 * @param mode
	 *            /**����ģʽ: 1.�绰���� 2.�������� 3.ȫ������*
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

		// CheckBox���ݻ���
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

		// editText���ݻ���
		et_name.setText(name);
		et_number.setText(number);
		et_number.setFocusable(false);// �����ǲ��ܸ��ĵ�

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
					Toast.makeText(CallSmsSafeActivity.this, "���벻��Ϊ��", 0).show();
					return;
				}

				String mode;
				if (cb_sms.isChecked() && cb_phone.isChecked()) {// ����ȫ��
					mode = "3";
				} else if (cb_sms.isChecked()) {// ��������
					mode = "2";
				} else if (cb_phone.isChecked()) {// �绰����
					mode = "1";
				} else {
					Toast.makeText(CallSmsSafeActivity.this, "��ѡ������ģʽ", 0).show();
					return;
				}
				// ��ӵ�ʱ���ݿ�
				dao.update(name, number, mode);

				// ����listView������
				BlackNumberInfo info = new BlackNumberInfo(name, number, mode);
				infos.remove(info);
				infos.add(0, info);
				adapter.notifyDataSetChanged();
				dialog.dismiss();
			}
		});

	}

	/**
	 * ��ʾ��Ӻ��������������
	 * 
	 * @param name
	 *            �������ʾ������
	 * @param number
	 *            �������ʾ�ĺ���
	 * @param mode
	 *            /**����ģʽ: 1.�绰���� 2.�������� 3.ȫ������*
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
					Toast.makeText(CallSmsSafeActivity.this, "���벻��Ϊ��", 0).show();
					return;
				}

				String mode;
				if (cb_sms.isChecked() && cb_phone.isChecked()) {// ����ȫ��
					mode = "3";
				} else if (cb_sms.isChecked()) {// ��������
					mode = "2";
				} else if (cb_phone.isChecked()) {// �绰����
					mode = "1";
				} else {
					Toast.makeText(CallSmsSafeActivity.this, "��ѡ������ģʽ", 0).show();
					return;
				}
				// ��ӵ�ʱ���ݿ�
				dao.add(name, number, mode);

				// ����listView������
				BlackNumberInfo info = new BlackNumberInfo(name, number, mode);
				infos.add(0, info);
				adapter.notifyDataSetChanged();
				dialog.dismiss();
			}
		});

	}
}
