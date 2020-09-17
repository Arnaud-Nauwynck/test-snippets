package fr.an.dbcatalog.spark.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Pattern;

import fr.an.dbcatalog.impl.utils.ListUtils;
import lombok.val;

public class SparkPatternUtils {


	/**
	 * see spark scala StringUtils.filterPattern(List<String> names, String sparkPattern)
	 * @return
	 */
	public static List<String> filterPatternAndSort(List<String> names, String sparkPattern) {
		List<String> subPatternTexts = Arrays.asList(sparkPattern.trim().split("\\|"));
		List<Pattern> subPatterns = ListUtils.map(subPatternTexts, t -> Pattern.compile("(?i)" + t.replaceAll("\\*", ".*")));
		val tmpres = ListUtils.filter(names, n -> null != ListUtils.findFirstMatch(n, subPatterns));
		return new ArrayList<>(new TreeSet<>(tmpres)); // backward compatible with spark... also sort + unique
	}

}
