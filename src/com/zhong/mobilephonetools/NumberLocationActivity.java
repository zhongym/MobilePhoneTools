package com.zhong.mobilephonetools;

import com.zhong.mobilephonetools.dao.utils.NumberAddressQueryUtils;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NumberLocationActivity extends Activity {

	private static final String TAG = "NumberLocationActivity";
	private EditText et_number;
	private TextView tv_result;
	
	private Vibrator vibrator;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_numberlocation);
		Log.i(TAG, "NumberLocationActivity用户可见");
		et_number = (EditText) findViewById(R.id.et_number_numberlocation);
		tv_result = (TextView) findViewById(R.id.tv_result_numberloaction);
		
		//获得系统的振动器
		vibrator=(Vibrator) getSystemService(VIBRATOR_SERVICE);
		
		et_number.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s != null && s.toString().length() > 7) {
					String result = NumberAddressQueryUtils.queryNumber(s.toString());
					tv_result.setText(result);
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});
	}

	public void query(View view) {
		String number = et_number.getText().toString().trim();
		Log.i(TAG, "号码:" + number);
		if (TextUtils.isEmpty(number) || number.length() < 7) {
			
			Animation animation=AnimationUtils.loadAnimation(this, R.anim.et_shake);
			et_number.startAnimation(animation);
//			vibrator.vibrate(milliseconds);
			
			long[] pattern={500,500}; //振动500ms,停止500ms
//			long[] pattern={500,500,200,800}; //振动500ms,停止500ms,振动200，停止800
//			vibrator.vibrate(pattern, repeat); repeat重复的次数，-1表示不重复
			vibrator.vibrate(pattern, 1);//
			
			Toast.makeText(this, "号码长度在大于7才能查询", 0).show();
			return;
		}
		String location = NumberAddressQueryUtils.queryNumber(number);
		tv_result.setText("查询结果：" + location);
		Log.i(TAG, "号码:" + number + "是" + location + "的");
	}
}
