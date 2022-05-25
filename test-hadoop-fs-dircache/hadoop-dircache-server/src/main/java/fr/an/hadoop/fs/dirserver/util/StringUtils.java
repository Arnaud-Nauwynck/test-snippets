package fr.an.hadoop.fs.dirserver.util;

public class StringUtils {


	public static int commonLen(String left, String right) {
		int i = 0;
		int len = Math.min(left.length(), right.length());
		for(; i < len; i++) {
			if (left.charAt(i) != right.charAt(i)) {
				return i;
			}
		}
		return len;
	}
	
}
