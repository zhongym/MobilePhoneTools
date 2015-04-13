package com.zhong.mobilephonetools;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BottomItemActivity {
	
	private static final String TAG = "MainActivity";

	private GridView gv;
	
	private String[] names={
			"缓存清理","手机防盗","通讯卫士",
			"进程管理","流量统计","手机杀毒"
			/*"软件管理","高级工具","设置中心"*/
	};
	
	private int[] ics={
			R.drawable.main_item_clean,R.drawable.main_item_safe,R.drawable.main_item_callmsgsafe,
			R.drawable.main_item_taskmanager,R.drawable.main_item_netmanager,R.drawable.main_item_trojan
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		gv=(GridView) findViewById(R.id.gv_main_item);
		gv.setAdapter(new MyAdpter());
		
		Log.i(TAG, "mainactivity用户可见");
		gv.setSelector(R.drawable.mian_item_layout_selector);
		gv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				view.setBackgroundResource(R.drawable.mian_item_layout_selector);
				
				switch (position) {
				case 5:
					
					break;

				default:
					break;
				}
			}
		});
		
	}
	
	@Override
	protected void onDestroy() {
		Log.i(TAG, "mainactivity被销毁了");
		super.onDestroy();
	}
	
	private class MyAdpter extends BaseAdapter{

		public int getCount() {
			return names.length;
		}

		@Override
		public Object getItem(int position) {
			return names[position];
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view=View.inflate(MainActivity.this, R.layout.main_list_item, null);
			ImageView iv=(ImageView) view.findViewById(R.id.iv_list_item_main);
			TextView tv=(TextView) view.findViewById(R.id.tv_name_list_item_main);
			iv.setImageResource(ics[position]);
			tv.setText(names[position]);
			
			return view;
		}
		
	}
}
