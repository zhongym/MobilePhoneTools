package com.zhong.mobilephonetools.utils;
import java.security.MessageDigest;

public class Md5Utils {

	public static String md5Password(String password) {
		try {

			MessageDigest digest = MessageDigest.getInstance("md5");
			byte[] result = digest.digest(password.getBytes());
			StringBuffer buffer = new StringBuffer();
			for (byte b : result) {
				// 0xff是十六进制，十进制为255

				int nuber = b & 0xff;
				String str = Integer.toHexString(nuber);
				if (str.length() == 1) {
					buffer.append("0");
				}

				buffer.append(str);
			}
			// 这就是MD5加密得到的值
			return buffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

}
