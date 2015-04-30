package com.zhong.mobilephonetools.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhong.mobilephonetools.R;

public class SettingClickView extends RelativeLayout {

	private TextView tv_content;
	private TextView tv_description;
	private String desc_on;
	private String desc_off;

	private void iniView(Context context) {
		View.inflate(context, R.layout.setting_click_view, this);
		tv_content = (TextView) findViewById(R.id.tv_content_setting_click_item);
		tv_description = (TextView) findViewById(R.id.tv_description_setting_click_item);

	}

	public SettingClickView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		iniView(context);
	}

	public SettingClickView(Context context, AttributeSet attrs) {
		super(context, attrs);
		iniView(context);
		String namespace = "http://schemas.android.com/apk/res/com.zhong.mobilephonetools";
		String title = attrs.getAttributeValue(namespace, "title");
		desc_on = attrs.getAttributeValue(namespace, "desc_on");
		desc_off = attrs.getAttributeValue(namespace, "desc_off");
		tv_content.setText(title);
		tv_description.setText(desc_off);
	}

	public SettingClickView(Context context) {
		super(context);
		iniView(context);

	}


	/**
	 * 设置组合控件的状态,单选框的状态就是这个自定义组件的状态
	 */
	public void setCheck(boolean b) {
		if (b) {
			setDescription(desc_on);
		} else {
			setDescription(desc_off);
		}
	}

	/**
	 * 设置 组合控件的描述信息
	 */
	public void setDescription(String description) {
		tv_description.setText(description);
	}

	public void setTitle(String title) {
		tv_content.setText(title);
	}

}
