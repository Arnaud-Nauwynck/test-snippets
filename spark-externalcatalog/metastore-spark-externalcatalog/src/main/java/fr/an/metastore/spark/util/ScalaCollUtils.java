package fr.an.metastore.spark.util;


import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import scala.Function1;
import scala.Predef;
import scala.Tuple2;
import scala.collection.IterableLike;
import scala.collection.JavaConverters;
import scala.collection.mutable.ArrayBuffer;

public class ScalaCollUtils {

	public static <A> scala.collection.mutable.Seq<A> toScalaMutableSeq(scala.collection.Seq<A> ls) {
		ArrayBuffer<A> res = new ArrayBuffer<A>();
		res.insert(0, ls);
		return res;
	}

	public static <A> scala.collection.Seq<A> toScalaSeq(
			java.util.Collection<A> ls) {
		return JavaConverters.collectionAsScalaIterable(ls).toSeq();
	}


	
	public static <A, B> scala.collection.mutable.Map<A, B> toScalaMutableMap(
			java.util.Map<A, B> m) {
	    return JavaConverters.mapAsScalaMap(m);
	}

	public static <A, B> scala.collection.immutable.Map<A, B> toScalaImutableMap(
			java.util.Map<A, B> m
			) {
	    return JavaConverters.mapAsScalaMapConverter(m).asScala().toMap(
	      Predef.<Tuple2<A, B>>$conforms()
	    );
	}

	public static <A,B> java.util.Map<A,B> mapAsJavaMap(scala.collection.Map<A,B> scalaMap) {
		return JavaConverters.mapAsJavaMap(scalaMap);
	}
	

	
	public static <A,B> Function1<A,B> toScalaFunc(Function<A,B> func) {
		return new Function1<A,B>() {
			@Override
			public B apply(A t) {
				return func.apply(t);
			}
		};
	}

	public static <A> Function1<A,Void> toScalaFuncV(Consumer<A> func) {
		return new Function1<A,Void>() {
			@Override
			public Void apply(A t) {
				func.accept(t);
				return null;
			}
		};
	}

	public static <A> List<A> seqAsJavaList(scala.collection.Seq<A> ls) {
		return JavaConverters.seqAsJavaList(ls);
	}

	public static <A> void foreach(
			scala.collection.Seq<A> ls, Consumer<A> func) {
		Function1<A,Void> scalaFunc = toScalaFuncV(func);
		// ls.foreach(scalaFunc); // eclipse compile error: "ambiguous" !?
		IterableLike<A,?> ls2 = (IterableLike<A,?>) ls;
		ls2.foreach(scalaFunc);
	}

	public static <A,B> List<B> map(
			scala.collection.Seq<A> ls, Function<A,B> func) {
		return seqAsJavaList(ls).stream().map(func).collect(Collectors.toList());
	}

}
