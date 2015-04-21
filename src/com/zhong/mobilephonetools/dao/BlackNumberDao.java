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
	 * ��ѯ�������������Ƿ����
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
	 * ��ѯ���������������ģʽ
	 * 
	 * @param number
	 * @return ���غ��������ģʽ�����Ǻ��������뷵��null >> ����ģʽ 1.�绰���� 2.�������� 3.ȫ������
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
	 * ��ѯȫ������������
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
	 * ��Ӻ���������
	 * 
	 * @param number
	 *            ����������
	 * @param mode
	 *            ����ģʽ 1.�绰���� 2.�������� 3.ȫ������
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
	 * �޸ĺ��������������ģʽ
	 * 
	 * @param number
	 *            Ҫ�޸ĵĺ���������
	 * @param newmode
	 *            �µ�����ģʽ
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
	 * ɾ������������
	 * 
	 * @param number
	 *            Ҫɾ���ĺ���������
	 */
	public void delete(String number) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("blackNumber", "number=?", new String[] { number });
		db.close();
	}

}
