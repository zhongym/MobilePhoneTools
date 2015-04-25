package com.zhong.mobilephonetools.utils;

import java.io.File;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlSerializer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

public class SmsUtils {

	public interface BackUpCallBack {

		/**
		 * ��ʼ���ݵ�ʱ�����ý��ȵ����ֵ
		 * 
		 * @param max
		 *            �ܽ���
		 */
		public void beforeBackup(int max);

		/**
		 * ���ݹ����У����ӽ���
		 * 
		 * @param progress
		 *            ��ǰ����
		 */
		public void onSmsBackup(int progress);

	}

	/**
	 * �õ����ŵ�����
	 * 
	 * @param context
	 * @return
	 */
	public static int smsCount(Context context) {
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse("content://sms/");
		Cursor cursor = resolver.query(uri, new String[] { "body" }, null, null, null);
		int max = cursor.getCount();
		cursor.close();
		return max;
	}

	/**
	 * ���ű���
	 * 
	 * @param applicationContext
	 * @throws Exception
	 */
	public static void backupSms(Context context, BackUpCallBack bb) throws Exception {

		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse("content://sms/");

		File file = new File(Environment.getExternalStorageDirectory(), "smsbackup.xml");
		FileOutputStream os = new FileOutputStream(file);

		XmlSerializer serializer = Xml.newSerializer();
		serializer.setOutput(os, "utf-8");
		serializer.startDocument("utf-8", true);
		serializer.startTag(null, "smss");

		Cursor cursor = resolver.query(uri, new String[] { "body", "address", "type", "date" }, null, null, null);
		int max = cursor.getCount();
		bb.beforeBackup(max);
		serializer.attribute(null, "max", max + "");

		int index = 0;
		while (cursor.moveToNext()) {

			String body = cursor.getString(0);
			String address = cursor.getString(1);
			String type = cursor.getString(2);
			String date = cursor.getString(3);

			serializer.startTag(null, "sms");

			serializer.startTag(null, "body");
			serializer.text(body);
			serializer.endTag(null, "body");

			serializer.startTag(null, "type");
			serializer.text(type);
			serializer.endTag(null, "type");

			serializer.startTag(null, "date");
			serializer.text(date);
			serializer.endTag(null, "date");

			serializer.startTag(null, "address");
			serializer.text(address);
			serializer.endTag(null, "address");

			serializer.endTag(null, "sms");
			index++;
			bb.onSmsBackup(index);
		}
		cursor.close();

		serializer.endTag(null, "smss");
		serializer.endDocument();
		os.close();

	}

	/**
	 * ��ԭ����
	 * 
	 * @param context
	 * @param flag
	 *            �Ƿ�����ԭ���Ķ���
	 */
	public static void restoreSms(Context context, boolean flag) {
		Uri uri = Uri.parse("content://sms/");
		if (flag) {
			context.getContentResolver().delete(uri, null, null);
		}
		// 1.��ȡsd���ϵ�xml�ļ�
		// Xml.newPullParser();

		// 2.��ȡmax

		// 3.��ȡÿһ��������Ϣ��body date type address

		// 4.�Ѷ��Ų��뵽ϵͳ��ϢӦ�á�

		ContentValues values = new ContentValues();
		values.put("body", "woshi duanxin de neirong");
		values.put("date", "1395045035573");
		values.put("type", "1");
		values.put("address", "5558");
		context.getContentResolver().insert(uri, values);
	}
}
