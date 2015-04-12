package com.zhong.mobilephonetools.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtiles {
	/**
	 * 将一个字节流转换成String类型
	 * 
	 * @param in
	 *            字节流
	 * @return 这个流对应的String
	 * @throws IOException
	 */
	public static String formateStream2String(InputStream in) throws IOException {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int len = 0;
		byte[] bu = new byte[1024];
		while ((len = in.read(bu)) > 0) {
			bos.write(bu, 0, len);
		}
		in.close();
		String result = bos.toString();
		return result;
	}
}
