package com.zhong.mobilephonetools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
/**
 * 主界面最下面的一排按键，将其抽取出来。然后要用到这排按键的activity只要继承这个类就有按键对应的点击事件了。
 * @author zhong
 *
 */
public class BottomItemActivity extends Activity {

	private static final String TAG = "BottomItemActivity";
	protected ImageView iv_used;
	protected ImageView iv_software;
	protected ImageView iv_tool;
	protected ImageView iv_setting;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bottom_item);
		iv_used = (ImageView) findViewById(R.id.iv_used_bottom_item);
		iv_software = (ImageView) findViewById(R.id.iv_software_bottom_item);
		iv_tool = (ImageView) findViewById(R.id.iv_tool_bottom_item);
		iv_setting = (ImageView) findViewById(R.id.iv_setting_bottom_item);

		Log.i(TAG, "BottomItemActivity可见");
		// iv_setting.setOnClickListener(new MyonClickListtener());
	}

	/**
	 * 常用功能
	 * 
	 * @param view
	 */
	public void used(View view) {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		/*finish();*/
	}

	/**
	 * 软件管理
	 * 
	 * @param view
	 */
	public void software(View view) {
		//Toast.makeText(this, "software", 0).show();
		Intent intent=new Intent(this, AppManagerActivity.class);
		startActivity(intent);
	}

	/**
	 * 高级工具
	 * 
	 * @param view
	 */
	public void tool(View view) {

	//	Toast.makeText(this, "tool", 0).show();
		Intent intent=new Intent(this, AdvancedtoolsActivity.class);
		startActivity(intent);
	}

	/**
	 * 设置中心
	 * 
	 * @param view
	 */
	public void setting(View view) {
			Intent intent = new Intent(this, SettingActivity.class);
			startActivity(intent);
			/*finish();*/
	}
}
