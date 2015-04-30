package com.zhong.mobilephonetools.domain;

import android.graphics.drawable.Drawable;

public class AppInfo {

	private Drawable icon;
	private String name;
	private String packageName;
	private String versionCode;
	private long Size;
	private boolean inRom;
	private boolean userApp;

	public AppInfo() {
		super();
	}


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

	public String getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	public long getSize() {
		return Size;
	}

	public void setSize(long size) {
		Size = size;
	}


	public boolean isInRom() {
		return inRom;
	}


	public void setInRom(boolean inRom) {
		this.inRom = inRom;
	}


	public boolean isUserApp() {
		return userApp;
	}


	public void setUserApp(boolean userApp) {
		this.userApp = userApp;
	}


	@Override
	public String toString() {
		return "AppInfo [name=" + name + ", packageName=" + packageName + ", versionCode=" + versionCode + ", Size="
				+ Size + ", inRom=" + inRom + ", userApp=" + userApp + "]";
	}

	

}
