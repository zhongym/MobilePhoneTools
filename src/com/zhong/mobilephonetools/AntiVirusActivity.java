package com.zhong.mobilephonetools;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 手机杀毒
 *
 */
public class AntiVirusActivity extends Activity {

	protected static final int SCANNGING = 0;
	protected static final int FINISH = 1;
	private ImageView iv_ratea;
	private TextView tv_scanning;
	private LinearLayout ll_layout_app_item;
	private PackageManager pm;
	
	private long startTime;
	private long endtTime;
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case SCANNGING:
				MessageContent content = (MessageContent) msg.obj;

				tv_scanning.setText("正在扫描：" + content.name);

				View view = View.inflate(getApplicationContext(), R.layout.list_item_app_anti_virus, null);
				ImageView iv = (ImageView) view.findViewById(R.id.iv_appicon);
				TextView tv = (TextView) view.findViewById(R.id.tv_appname);
				tv.setText(content.name);
				iv.setImageDrawable(content.icon);
				ll_layout_app_item.addView(view, 0);
				break;

			case FINISH:
				endtTime=System.currentTimeMillis();
				
				tv_scanning.setText("扫描完成");
				iv_ratea.clearAnimation();
				ShowDialog();
				break;
			}
		}

	};

	/**
	 * 显示完成对话框
	 */
	private void ShowDialog() {
		AlertDialog.Builder builder = new Builder(this);
		long time=(endtTime-startTime)/1000;
		builder.setMessage("扫描完成  没有发现病毒\n用时："+time+"秒");
		builder.setNegativeButton("完成", null);
		builder.show();
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anti_virus);

		iv_ratea = (ImageView) findViewById(R.id.iv_ratea);
		tv_scanning = (TextView) findViewById(R.id.tv_scanning);
		ll_layout_app_item = (LinearLayout) findViewById(R.id.ll_layout_app_item);

		RotateAnimation rota = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		rota.setDuration(1000);// 一次动画的持续时间
		LinearInterpolator lin = new LinearInterpolator();// 匀速
		rota.setInterpolator(lin);// interpolator表示变化率，但不是运行速度
		rota.setRepeatCount(Animation.INFINITE);// 重复模式：无限重复
		iv_ratea.startAnimation(rota);

		pm = getPackageManager();

		new Thread() {
			public void run() {
				List<PackageInfo> infos = pm.getInstalledPackages(0);
				startTime=System.currentTimeMillis();
				for (PackageInfo info : infos) {
					Drawable icon = info.applicationInfo.loadIcon(pm);
					String name = info.applicationInfo.loadLabel(pm).toString();

					MessageContent content = new MessageContent(icon, name);
					Message msg = Message.obtain();
					msg.what = SCANNGING;
					msg.obj = content;
					handler.sendMessage(msg);
					
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				Message msg = Message.obtain();
				msg.what = FINISH;
				handler.sendMessage(msg);
			};

		}.start();

	}

	class MessageContent {

		public MessageContent(Drawable icon, String name) {
			super();
			this.icon = icon;
			this.name = name;
		}

		Drawable icon;
		String name;
	}
}
