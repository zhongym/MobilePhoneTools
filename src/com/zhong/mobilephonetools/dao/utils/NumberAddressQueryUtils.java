package com.zhong.mobilephonetools.dao.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NumberAddressQueryUtils {

	private static String path = "data/data/com.zhong.mobilephonetools/files/address.db";

	public static String queryNumber(String number) {
		if(number.length()<7){
			return number;
		}
		
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		String location = number;
		String sql = "select location from data2 where id=(select outKey from data1 where id=?)";
		Cursor cursor = db.rawQuery(sql, new String[] { number.substring(0, 7) });
		while (cursor.moveToNext()) {
			location = cursor.getString(0);
		}
		return location;
	}
}
