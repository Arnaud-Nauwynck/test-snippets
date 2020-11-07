package fr.an.metastore.api.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.val;

public class MetastoreListUtils {

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
	

	/**
	 * see spark scala StringUtils.filterPattern(List<String> names, String sparkPattern)
	 * @return
	 */
	public static List<String> filterPatternAndSort(List<String> names, String sparkPattern) {
		List<String> subPatternTexts = Arrays.asList(sparkPattern.trim().split("\\|"));
		List<Pattern> subPatterns = MetastoreListUtils.map(subPatternTexts, t -> Pattern.compile("(?i)" + t.replaceAll("\\*", ".*")));
		val tmpres = MetastoreListUtils.filter(names, n -> null != MetastoreListUtils.findFirstMatch(n, subPatterns));
		return new ArrayList<>(new TreeSet<>(tmpres)); // backward compatible with spark... also sort + unique
	}

}
