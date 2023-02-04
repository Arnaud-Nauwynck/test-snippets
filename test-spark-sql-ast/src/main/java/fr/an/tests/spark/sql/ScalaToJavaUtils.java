package fr.an.tests.spark.sql;

import java.util.List;

import scala.collection.JavaConverters;

public class ScalaToJavaUtils {

	
	public static <T> List<T> toJavaList(scala.collection.Seq<T> scala) {
		return JavaConverters.seqAsJavaListConverter(scala).asJava();
	}
	
	public static <T> List<T> immutableToJavaList(scala.collection.immutable.Seq<T> scala) {
		return JavaConverters.seqAsJavaListConverter(scala).asJava();
	}

	public static <T> List<T> mutabletoJavaList(scala.collection.mutable.Seq<T> scala) {
		return JavaConverters.seqAsJavaListConverter(scala).asJava();
	}
	
}
