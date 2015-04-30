package com.zhong.mobilephonetools.dao.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppLockListOpenHelper extends SQLiteOpenHelper {

	public AppLockListOpenHelper(Context context) {
		super(context, "applock.db", null, 1);
	}
	
	/**
	 * 数据库第一次创建的时候被调用，用来创建表和数据库结构
	 */
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table applock (_id integer primary key autoincrement,packageName varchar(100))");
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}


}
