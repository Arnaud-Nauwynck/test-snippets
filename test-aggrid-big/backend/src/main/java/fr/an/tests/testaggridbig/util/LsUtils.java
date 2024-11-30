package fr.an.tests.testaggridbig.util;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LsUtils {

    public static <T,TDest> List<TDest> map(Collection<T> src, Function<T,TDest> func) {
        return src.stream().map(func).collect(Collectors.toList());
    }

}
