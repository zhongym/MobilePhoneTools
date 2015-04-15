package com.zhong.mobilephonetools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.Toast;

public abstract class BaseSetupActivity extends Activity {
	// 1.����һ������ʶ����
	private GestureDetector gd;// ����̽����
	
	private static final String TAG = "BaseSetupActivity";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//2.ʵ�����������ʶ����
		gd = new GestureDetector(this, new SimpleOnGestureListener() {

			// ������Ļ velocityX:x��ļ��ٶ� e1������ȥ�ĵ�һ���� e2:���һ����
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

				if ((e2.getRawX() - e1.getRawX()) > 20) {
					// ˵����ָ�����ķ����� -------> ������һҳ����
					Log.i(TAG, "˵����ָ�����ķ�����    -------> ������һҳ����");
					doPrevious();
					return true;
				}

				if ((e1.getRawX() - e2.getRawX()) > 20) {
					// ˵����ָ�����ķ����� <------- ������һҳ����
					Log.i(TAG, "˵����ָ�����ķ����� <------- ������һҳ����");
					doNext();
					return true;
				}

				// ��ֹ�ϻ�����
				if (Math.abs(e1.getRawY() - e2.getRawY()) > 200) {
					Log.i(TAG, "�������»��Ĳ���");
					return true;
				}

				//������X��������������
				
				if(Math.abs(velocityX)<200){
					Log.i(TAG, "������̫����");
					return true;
				}
				
				
				return super.onFling(e1, e2, velocityX, velocityY);
			}


			// ������Ļ
			public void onLongPress(MotionEvent e) {
				super.onLongPress(e);
			}

			// ˫����Ļ
			public boolean onDoubleTapEvent(MotionEvent e) {
				return super.onDoubleTapEvent(e);
			}

			// ������Ļ
			public boolean onSingleTapConfirmed(MotionEvent e) {
				return super.onSingleTapConfirmed(e);
			}

		});
	}
	
	
	
	/**
	 //3: activity���յ�ҳ�洫�����ĵ���¼���Ȼ�󴫸�GestureDetector����
	 */
	public boolean onTouchEvent(MotionEvent event) {
		gd.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
	
	
	
	/**
	 * ��һ����������¼�
	 * @param view
	 */
	public void next(View view) {
		doNext();
	}


	/**
	 * ��һ����������¼�
	 * @param view
	 */
	public void previous(View view){
		doPrevious();
	}

	public abstract void doNext();

	public abstract void doPrevious(); 

}
