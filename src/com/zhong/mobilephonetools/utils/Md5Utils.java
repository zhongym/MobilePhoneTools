package com.zhong.mobilephonetools.utils;
import java.security.MessageDigest;

public class Md5Utils {

	public static String md5Password(String password) {
		try {

			MessageDigest digest = MessageDigest.getInstance("md5");
			byte[] result = digest.digest(password.getBytes());
			StringBuffer buffer = new StringBuffer();
			for (byte b : result) {
				// 0xff��ʮ�����ƣ�ʮ����Ϊ255

				int nuber = b & 0xff;
				String str = Integer.toHexString(nuber);
				if (str.length() == 1) {
					buffer.append("0");
				}

				buffer.append(str);
			}
			// �����MD5���ܵõ���ֵ
			return buffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

}
