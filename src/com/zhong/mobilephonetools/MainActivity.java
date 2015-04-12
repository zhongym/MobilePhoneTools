package com.zhong.mobilephonetools;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
	
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
			View view=View.inflate(MainActivity.this, R.layout.list_item_main, null);
			ImageView iv=(ImageView) view.findViewById(R.id.iv_list_item_main);
			TextView tv=(TextView) view.findViewById(R.id.tv_name_list_item_main);
			iv.setImageResource(ics[position]);
			tv.setText(names[position]);
			
			return view;
		}
		
	}
}
