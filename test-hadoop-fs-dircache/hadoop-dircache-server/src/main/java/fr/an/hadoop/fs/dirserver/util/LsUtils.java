package fr.an.hadoop.fs.dirserver.util;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LsUtils {

	public static <TSrc,TDest> List<TDest> map(Collection<TSrc> src, Function<TSrc,TDest> func) {
		return src.stream().map(func).collect(Collectors.toList());
	}
}
