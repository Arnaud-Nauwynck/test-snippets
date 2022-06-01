package fr.an.hadoop.fs.dirserver.util;

import lombok.val;

public class PathUtils {

	public static String[] toChildPath(String[] pathElts, String name) {
		val pathEltCount = pathElts.length;
		val res = new String[pathEltCount+1];
		System.arraycopy(pathElts, 0, res, 0, pathEltCount);
		res[pathEltCount] = name;
		return res;
	}


}
