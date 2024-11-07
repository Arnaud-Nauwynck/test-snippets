package fr.an.sample.impl;

public class FooUtils {

    public static Integer foo(Integer x) {
        if (x == null) return 0; // should not occur
        int value = x.intValue();
        return 2 * value;
    }

}
