package fr.an.tests.spark.sql;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.val;

public class LsUtils {

	public <T> String mapStrJoin(Collection<T> ls, Function<T,String> toTextFunc, String join) {
		val texts = ls.stream().map(toTextFunc).collect(Collectors.toList());
		return String.join(join, texts);
	}

}
