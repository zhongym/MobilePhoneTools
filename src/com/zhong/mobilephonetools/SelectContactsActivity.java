package com.zhong.mobilephonetools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SelectContactsActivity extends Activity {

	private static final String TAG = "selectContactsActivity";
	private ListView lv_contacts;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "selectContactsActivity�û��ɼ�");
		setContentView(R.layout.activity_select_contacts);

		lv_contacts = (ListView) findViewById(R.id.lv_contacts);
		final List<Map<String, String>> data = getContacts();
		lv_contacts.setAdapter(new SimpleAdapter(this, data, R.layout.activity_contacts_item, new String[] { "name",
				"phone" }, new int[] { R.id.tv_name_acontacts_item, R.id.tv_phone_acontacts_item }));

		lv_contacts.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String phone = data.get(position).get("phone");
				String name = data.get(position).get("name");
				Intent intent = new Intent();
				intent.putExtra("phone", phone);
				intent.putExtra("name", name);
				setResult(0, intent);
				finish();// ѡ������رյ�ǰ����
			}
		});

	}

	/**
	 * <h1>��ȡϵͳ��ϵ��<h1><br>
	 * ÿ��map�����ϵ�˵����ƺͺ���name="zs",phone="123"
	 * 
	 * @return List <Map<String, String>> ��ϵ�˼���
	 * 
	 */
	private List<Map<String, String>> getContacts() {

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		ContentResolver resolver = this.getContentResolver();

		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");// raw_contact ���uri
		Uri dataUri = Uri.parse("content://com.android.contacts/data"); // data ���uri

		Cursor cursor = resolver.query(uri, new String[] { "contact_id" }, null, null, null);
		while (cursor.moveToNext()) {
			String id = cursor.getString(0);
			if (id != null) {
				Map<String, String> map = new HashMap<String, String>();

				Cursor dataCursor = resolver.query(dataUri, new String[] { "data1", "mimetype" }, "raw_contact_id=?",
						new String[] { id }, null);

				while (dataCursor.moveToNext()) {
					String data = dataCursor.getString(0);
					String mimetype = dataCursor.getString(1);
					if (!TextUtils.isEmpty(data)) {
						if ("vnd.android.cursor.item/name".equals(mimetype)) {
							// ��ϵ�˵�����
							map.put("name", data);
						} else if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
							// ��ϵ�˵ĵ绰����
							map.put("phone", data);
						}
					}
				}
				list.add(map);
				dataCursor.close();
			}
		}
		cursor.close();
		return list;
	}
}
