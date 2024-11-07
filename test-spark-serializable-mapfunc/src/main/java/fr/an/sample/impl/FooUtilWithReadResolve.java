package fr.an.sample.impl;

import lombok.val;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;

import java.io.ObjectStreamException;
import java.io.Serializable;

public class FooUtilWithReadResolve implements Serializable {

    protected Object readResolve() throws ObjectStreamException {
        System.out.println("should not occur: called readResolve() from implicit java.io.Serializable on FooUtilWithReadResolve with static methods only");
        return this;
    }

    public static Integer foo(Integer x) {
        return 2*x;
    }

    public static void testMap(Dataset<Integer> intDataset) {
        System.out.println("calling intDataset.map((MapFunction<Integer, Integer>) FooUtilWithReadResolve::foo)");
        val mappedDataset = intDataset.map((MapFunction<Integer, Integer>) FooUtilWithReadResolve::foo, Encoders.INT());
        mappedDataset.show(3);
    }
}
