package fr.an.sample.impl;

import org.apache.spark.api.java.function.MapFunction;

/**
 * see corresponding bytecode:
 * javap -v target/classes/fr/an/sample/impl/FooLambdaFactory.class > src/main/java/fr/an/sample/impl/FooLambdaFactory.class-javap.txt
 */
public class FooLambdaFactory {

    public static MapFunction<Integer, Integer> mapFuncRef() {
        return FooUtils::foo;
    }

    public static MapFunction<Integer, Integer> mapFunc() {
        return x -> FooUtils.foo(x);
    }

    public static MapFunction<Integer, Integer> mapFuncInlined() {
        return x -> {
            if (x == null) return 0; // should not occur
            int value = x.intValue();
            return 2 * value;
        };
    }

}
