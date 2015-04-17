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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class selectContactsActivity extends Activity {

	private static final String TAG = "selectContactsActivity";
	private ListView lv_contacts;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "selectContactsActivity用户可见");
		setContentView(R.layout.activity_select_contacts);

		lv_contacts = (ListView) findViewById(R.id.lv_contacts);
		final List<Map<String, String>> data = getContacts();
		lv_contacts.setAdapter(new SimpleAdapter(this, data, R.layout.activity_contacts_item, new String[] { "name",
				"phone" }, new int[] { R.id.tv_name_acontacts_item, R.id.tv_phone_acontacts_item }));

		lv_contacts.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String phone = data.get(position).get("phone");
				Intent intent = new Intent();
				intent.putExtra("phone", phone);
				setResult(0, intent);
				finish();// 选择号码后关闭当前窗口
			}
		});

	}
	
	/**
	 * <h1>读取系统联系人<h1><br>
	 * 每个map存放联系人的名称和号码name="zs",phone="123"
	 * @return List <Map<String, String>> 联系人集合
	 * 
	 */
	private List<Map<String, String>> getContacts() {

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		ContentResolver resolver = this.getContentResolver();

		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");// raw_contact 表的uri
		Uri dataUri = Uri.parse("content://com.android.contacts/data"); // data 表的uri

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

					if ("vnd.android.cursor.item/name".equals(mimetype)) {
						// 联系人的姓名
						map.put("name", data);
					} else if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
						// 联系人的电话号码
						map.put("phone", data);
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
