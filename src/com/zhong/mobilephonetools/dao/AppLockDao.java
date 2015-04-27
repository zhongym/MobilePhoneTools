package com.zhong.mobilephonetools.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zhong.mobilephonetools.dao.utils.AppLockListOpenHelper;

public class AppLockDao {
	private AppLockListOpenHelper helper;

	public AppLockDao(Context context) {
		helper = new AppLockListOpenHelper(context);
	}

	public void add(String packageName) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("packageName", packageName);
		db.insert("applock", null, values);
		db.close();
	}

	public void delete(String packageName) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("applock", "packageName=?", new String[] { packageName });
		db.close();
	}

	public boolean find(String packageName) {
		SQLiteDatabase db = helper.getReadableDatabase();
		boolean result = false;
		Cursor cursor = db
				.query("applock", null, "packageName=?", new String[] { packageName }, null, null, null);
		if (cursor.moveToNext()) {
			result = true;
		}
		cursor.close();
		db.close();
		return result;
	}

	/**
	 * 查询全部的包名
	 * 
	 * @param packname
	 * @return
	 */
	public List<String> findAll() {
		List<String> protectPacknames = new ArrayList<String>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("applock", new String[] { "packageName" }, null, null, null, null, null);
		while (cursor.moveToNext()) {
			protectPacknames.add(cursor.getString(0));
		}
		cursor.close();
		db.close();
		return protectPacknames;
	}
}
