package com.zhong.mobilephonetools;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.Toast;

public abstract class BaseSetupActivity extends Activity {

	private static final String TAG = "BaseSetupActivity";

	// 1.定义一个手势识别器
	private GestureDetector gd;// 手势探测器

	protected SharedPreferences sp;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);

		// 2.实例化这个手势识别器
		gd = new GestureDetector(this, new SimpleOnGestureListener() {
			// 滑动屏幕 velocityX:x轴的加速度 e1：按下去的第一个点 e2:最后一个点
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

				if ((e2.getRawX() - e1.getRawX()) > 20) {
					// 说明手指滑动的方向是 -------> 进行上一页操作
					Log.i(TAG, "说明手指滑动的方向是    -------> 进行上一页操作");
					doPrevious();
					return true;
				}

				if ((e1.getRawX() - e2.getRawX()) > 20) {
					// 说明手指滑动的方向是 <------- 进行下一页操作
					Log.i(TAG, "说明手指滑动的方向是 <------- 进行下一页操作");
					doNext();
					return true;
				}

				// 禁止上滑操作
				if (Math.abs(e1.getRawY() - e2.getRawY()) > 200) {
					Log.i(TAG, "这是上下滑的操作");
					return true;
				}
				// 屏蔽在X滑动很慢的情形

				if (Math.abs(velocityX) < 200) {
					Log.i(TAG, "滑动得太慢了");
					return true;
				}
				return super.onFling(e1, e2, velocityX, velocityY);
			}

			// 长按屏幕
			public void onLongPress(MotionEvent e) {
				super.onLongPress(e);
			}

			// 双击屏幕
			public boolean onDoubleTapEvent(MotionEvent e) {
				return super.onDoubleTapEvent(e);
			}

			// 单击屏幕
			public boolean onSingleTapConfirmed(MotionEvent e) {
				return super.onSingleTapConfirmed(e);
			}

		});
	}

	/**
	 * //3: activity接收到页面传回来的点击事件，然后传给GestureDetector处理
	 */
	public boolean onTouchEvent(MotionEvent event) {
		gd.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	/**
	 * 下一步按键点击事件
	 * 
	 * @param view
	 */
	public void next(View view) {
		doNext();
	}

	/**
	 * 上一步按键点击事件
	 * 
	 * @param view
	 */
	public void previous(View view) {
		doPrevious();
	}

	public abstract void doNext();

	public abstract void doPrevious();

}
