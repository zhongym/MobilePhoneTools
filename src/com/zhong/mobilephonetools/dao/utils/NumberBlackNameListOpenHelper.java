package com.zhong.mobilephonetools.dao.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class NumberBlackNameListOpenHelper extends SQLiteOpenHelper {

	public NumberBlackNameListOpenHelper(Context context) {
		super(context, "blackNumber.db", null, 1);
	}
	
	/**
	 * 数据库第一次创建的时候被调用，用来创建表和数据库结构
	 */
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table blackNumber (_id integer primary key autoincrement,name varchar(10),number varchar(20),mode varchar(2))");
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}


}
