package com.zhong.mobilephonetools.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zhong.mobilephonetools.dao.utils.NumberBlackNameListOpenHelper;
import com.zhong.mobilephonetools.domain.BlackNumberInfo;

public class BlackNumberDao {

	private NumberBlackNameListOpenHelper helper;

	public BlackNumberDao(Context context) {
		helper = new NumberBlackNameListOpenHelper(context);
	}

	/**
	 * 查询黑名单号码是是否存在
	 * 
	 * @param number
	 * @return
	 */
	public boolean find(String number) {
		boolean result = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from blackNumber where number=?", new String[] { number });
		if (cursor.moveToNext()) {
			result = true;
		}
		cursor.close();
		db.close();
		return result;
	}

	/**
	 * 查询黑名单号码的拦截模式
	 * 
	 * @param number
	 * @return 返回号码的拦截模式，不是黑名单号码返回null >> 拦截模式 1.电话拦截 2.短信拦截 3.全部拦截
	 */
	public String findMode(String number) {
		String result = null;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select mode from blackNumber where number=?", new String[] { number });
		if (cursor.moveToNext()) {
			result = cursor.getString(0);
		}
		cursor.close();
		db.close();
		return result;
	}

	/**
	 * 查询全部黑名单号码
	 * 
	 * @return
	 */
	public List<BlackNumberInfo> findAll() {
		List<BlackNumberInfo> result = new ArrayList<BlackNumberInfo>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select name,number,mode from blackNumber order by _id desc", null);
		while (cursor.moveToNext()) {
			BlackNumberInfo info = new BlackNumberInfo();
			String name = cursor.getString(0);
			String number = cursor.getString(1);
			String mode = cursor.getString(2);
			info.setName(name);
			info.setMode(mode);
			info.setNumber(number);
			result.add(info);
		}
		cursor.close();
		db.close();
		return result;
	}

	/**
	 * 添加黑名单号码
	 * 
	 * @param number
	 *            黑名单号码
	 * @param mode
	 *            拦截模式 1.电话拦截 2.短信拦截 3.全部拦截
	 */
	public void add(String name, String number, String mode) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("name", name);
		values.put("number", number);
		values.put("mode", mode);
		db.insert("blackNumber", null, values);
		db.close();
	}

	/**
	 * 修改黑名单号码的拦截模式
	 * 
	 * @param number
	 *            要修改的黑名单号码
	 * @param newmode
	 *            新的拦截模式
	 */
	public void update(String name, String number, String newmode) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("name", name);
		values.put("mode", newmode);
		db.update("blackNumber", values, "number=?", new String[] { number });
		db.close();
	}

	/**
	 * 删除黑名单号码
	 * 
	 * @param number
	 *            要删除的黑名单号码
	 */
	public void delete(String number) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("blackNumber", "number=?", new String[] { number });
		db.close();
	}

}
