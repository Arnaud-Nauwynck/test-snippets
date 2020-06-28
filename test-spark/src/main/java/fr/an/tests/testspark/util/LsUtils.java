package fr.an.tests.testspark.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import scala.collection.JavaConverters;
import scala.collection.Seq;

public class LsUtils {

	public static <T,TDest> List<TDest> map(Collection<T> ls, Function<T,TDest> mapper) {
		return ls.stream().map(mapper).collect(Collectors.toList());
	}
	
	public static <T> List<T> filter(Collection<T> ls, Predicate<T> pred) {
		return ls.stream().filter(pred).collect(Collectors.toList());
	}	

	public static <T> List<T> filter(T[] ls, Predicate<T> pred) {
		return filter(Arrays.asList(ls), pred);
	}
	
	public static <T> Seq<T> toScalaList(List<T> src) {
        return JavaConverters.collectionAsScalaIterableConverter(src).asScala().toSeq();
    }
}
