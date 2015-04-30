package com.zhong.mobilephonetools.dao.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppLockListOpenHelper extends SQLiteOpenHelper {

	public AppLockListOpenHelper(Context context) {
		super(context, "applock.db", null, 1);
	}
	
	/**
	 * ���ݿ��һ�δ�����ʱ�򱻵��ã���������������ݿ�ṹ
	 */
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table applock (_id integer primary key autoincrement,packageName varchar(100))");
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}


}
