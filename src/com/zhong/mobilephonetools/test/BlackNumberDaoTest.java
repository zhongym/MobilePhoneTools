package com.zhong.mobilephonetools.test;

import java.util.List;
import java.util.Random;

import com.zhong.mobilephonetools.dao.BlackNumberDao;
import com.zhong.mobilephonetools.dao.utils.NumberBlackNameListOpenHelper;
import com.zhong.mobilephonetools.domain.BlackNumberInfo;

import android.test.AndroidTestCase;

public class BlackNumberDaoTest extends AndroidTestCase {
	public void testCreateDB() throws Exception {
		NumberBlackNameListOpenHelper helper = new NumberBlackNameListOpenHelper(getContext());
		helper.getWritableDatabase();
	}

	public void testAdd() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		long basenumber = 13500000000l;
		Random random = new Random();
		for (int i = 0; i < 100; i++) {
			dao.add("zhong"+i,String.valueOf(basenumber + i), String.valueOf(random.nextInt(3) + 1));
		}
	}

	public void testFindAll() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		List<BlackNumberInfo> infos = dao.findAll();
		for (BlackNumberInfo info : infos) {
			System.out.println(info.toString());
		}
	}

	public void testDelete() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		dao.delete("110");
	}

	public void testUpdate() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		dao.update("zhon","110", "2");
	}

	public void testFind() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		boolean result = dao.find("110");
		assertEquals(true, result);
	}
}
