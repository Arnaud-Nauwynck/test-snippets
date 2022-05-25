package fr.an.hadoop.fs.dirserver.util;

import org.junit.Assert;
import org.junit.Test;

public class StringUtilsTest {

	@Test
	public void testCommon() {
		Assert.assertEquals(3, StringUtils.commonLen("abcX", "abcY"));
		Assert.assertEquals(3, StringUtils.commonLen("abcX", "abc"));
		Assert.assertEquals(3, StringUtils.commonLen("abc", "abcY"));
		Assert.assertEquals(3, StringUtils.commonLen("abc", "abc"));
		Assert.assertEquals(0, StringUtils.commonLen("X", "Y"));
		Assert.assertEquals(0, StringUtils.commonLen("X", ""));
		Assert.assertEquals(0, StringUtils.commonLen("", "Y"));
	}
}
