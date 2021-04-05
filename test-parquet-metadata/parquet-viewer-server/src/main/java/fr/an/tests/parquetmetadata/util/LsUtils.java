package fr.an.tests.parquetmetadata.util;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

public class LsUtils {

	public static <TSrc, TDest> List<TDest> map(Collection<TSrc> src, Function<TSrc, TDest> func) {
		return src.stream().map(func).collect(Collectors.toList());
	}

	public static <T> ImmutableList<T> immutableConcat(Iterable<? extends T> ls, T last) {
		return ImmutableList.<T>builder().addAll(ls).add(last).build();
	}
}
