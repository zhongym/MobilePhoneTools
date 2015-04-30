package com.zhong.mobilephonetools.dao.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NumberBlackNameListOpenHelper extends SQLiteOpenHelper {

	public NumberBlackNameListOpenHelper(Context context) {
		super(context, "blackNumber.db", null, 1);
	}
	
	/**
	 * ���ݿ��һ�δ�����ʱ�򱻵��ã���������������ݿ�ṹ
	 */
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table blackNumber (_id integer primary key autoincrement,name varchar(10),number varchar(20),mode varchar(2))");
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}


}
