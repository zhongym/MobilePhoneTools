package com.zhong.mobilephonetools.domain;

import android.graphics.drawable.Drawable;

public class TaskInfo {

	/*** Ӧ��ͼ�� **/
	private Drawable icon;

	/*** Ӧ������ **/
	private String name;

	/*** ���� **/
	private String packageName;

	/** ռ�õ��ڴ�**/
	private long totalMem;
	
	/**true �û�Ӧ�� ��falseϵͳӦ��*/
	private boolean userApp;
	
	/**�Ƿ�ѡ��*/
	private boolean checked;

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public long getTotalMem() {
		return totalMem;
	}

	public void setTotalMem(long totalMem) {
		this.totalMem = totalMem;
	}

	public boolean isUserApp() {
		return userApp;
	}

	public void setUserApp(boolean userApp) {
		this.userApp = userApp;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

}
