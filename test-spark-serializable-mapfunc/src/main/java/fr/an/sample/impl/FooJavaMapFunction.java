package fr.an.sample.impl;

import org.apache.spark.api.java.function.MapFunction;

import java.io.Serializable;

public class FooJavaMapFunction implements MapFunction<Integer, Integer> { // transitively implements Serializable

    @Override
    public Integer call(Integer row) {
        return FooUtils.foo(row);
    }

}
