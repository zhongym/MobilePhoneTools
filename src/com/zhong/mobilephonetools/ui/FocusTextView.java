package com.zhong.mobilephonetools.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
/**
 * 自定义一个view对象，只要继续相对就的view即可
 * @author zhong
 *
 */
public class FocusTextView extends TextView {

	/**
	 * 让这个view对你默认就焦点
	 */
	@Override
	public boolean isFocused() {
		return true;
	}
	
	public FocusTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public FocusTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public FocusTextView(Context context) {
		super(context);
	}

}
