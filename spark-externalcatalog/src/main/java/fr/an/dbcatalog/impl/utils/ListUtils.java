package fr.an.dbcatalog.impl.utils;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.val;

public class ListUtils {

	public static <TDest,TSrc> List<TDest> map(Collection<TSrc> srcs, Function<TSrc,TDest> mapFunc) {
		return srcs.stream().map(mapFunc).collect(Collectors.toList());
	}

	public static <T> List<T> filter(Collection<T> srcs, Predicate<T> predicateFunc) {
		return srcs.stream().filter(predicateFunc).collect(Collectors.toList());
	}


	public static Pattern findFirstMatch(String text, Collection<Pattern> patterns) {
		for(val p : patterns) {
			if (p.matcher(text).matches()) {
				return p;
			}
		}
		return null;
	}
}
