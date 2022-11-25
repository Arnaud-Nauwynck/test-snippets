package fr.an.tests.asm.utils;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LsUtils {

	public static <TSrc,T> List<T> map(Collection<TSrc> srcs, Function<TSrc,T> func) {
		return srcs.stream().map(func).collect(Collectors.toList());
	}

	public static <T> List<T> filter(Collection<T> srcs, Predicate<T> func) {
		return srcs.stream().filter(func).collect(Collectors.toList());
	}

}
