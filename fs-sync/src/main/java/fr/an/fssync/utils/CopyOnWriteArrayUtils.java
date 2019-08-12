package fr.an.fssync.utils;

import java.lang.reflect.Array;
import java.util.Arrays;

public class CopyOnWriteArrayUtils {

    public static <T> T[] withAdd(T[] src, T add) {
	int len = src.length;
        T[] res = Arrays.copyOf(src, len + 1);
        res[len] = add;
        return res;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] withRemove(T[] src, T remove) {
	int len = src.length;
	int index = Arrays.asList(src).indexOf(remove);
	if (index == -1) {
	    return src;
	}
        Class<T> componentType = (Class<T>) src.getClass().getComponentType();
	T[] res = (T[]) Array.newInstance(componentType , len - 1);
        System.arraycopy(src, 0, res, 0, index);
        int numMoved = len - index - 1;
        System.arraycopy(src, index + 1, res, index, numMoved);
        return res;
    }

}
