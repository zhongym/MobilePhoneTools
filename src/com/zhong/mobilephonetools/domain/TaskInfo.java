package com.zhong.mobilephonetools.domain;

import android.graphics.drawable.Drawable;

public class TaskInfo {

	/*** 应用图标 **/
	private Drawable icon;

	/*** 应用名称 **/
	private String name;

	/*** 包名 **/
	private String packageName;

	/** 占用的内存**/
	private long totalMem;
	
	/**true 用户应用 ，false系统应用*/
	private boolean userApp;
	
	/**是否被选中*/
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
