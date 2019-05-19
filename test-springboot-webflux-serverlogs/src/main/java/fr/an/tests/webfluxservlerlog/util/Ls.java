package fr.an.tests.webfluxservlerlog.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public final class Ls {

	private Ls() {
	}
	
	public static <T,TSrc> List<T> map(Collection<TSrc> src, Function<TSrc,T> func) {
		List<T> res = new ArrayList<>(src.size());
		for(TSrc e : src) {
			res.add(func.apply(e));
		}
		return res;
	}
	
	public static <T> List<T> filter(Collection<T> src, Predicate<T> pred) {
		List<T> res = new ArrayList<>(src.size());
		for(T e : src) {
			if (pred.test(e)) {
				res.add(e);
			}
		}
		if (src.size() - res.size() > 1000) {
			res = new ArrayList<>(res); // realloc
		}
		return res;
	}

}
