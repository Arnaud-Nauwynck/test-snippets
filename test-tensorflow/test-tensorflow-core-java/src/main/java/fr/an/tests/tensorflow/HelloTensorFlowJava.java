package fr.an.tests.tensorflow;

import org.tensorflow.ConcreteFunction;
import org.tensorflow.Signature;
import org.tensorflow.Tensor;
import org.tensorflow.TensorFlow;
import org.tensorflow.op.Ops;
import org.tensorflow.op.core.Placeholder;
import org.tensorflow.op.math.Add;
import org.tensorflow.types.TInt32;

public class HelloTensorFlowJava {

	/**
	 * 
	 * works in maven using "mvn exec:java"  
	 * 
	 * ... but does not work in eclipse !!
	 * 
	 * 
	 * adding explicit => 
	 * Warning: Could not load Loader: java.lang.UnsatisfiedLinkError: no jnijavacpp in java.library.path 
	 * Exception in thread "main" java.lang.UnsatisfiedLinkError: C:\Users\arnaud\.javacpp\cache\tensorflow-core-api-0.3.3-windows-x86_64.jar\org\tensorflow\internal\c_api\windows-x86_64\jnitensorflow.dll: Can't load AMD 64-bit .dll on a IA 32-bit platform
	 * 
	 * 
	 * Exception in thread "main" java.lang.UnsatisfiedLinkError: no jnitensorflow in java.library.path
	 * at java.lang.ClassLoader.loadLibrary(ClassLoader.java:1860)
	 */
	public static void main(String[] args) throws Exception {
		System.setProperty("org.tensorflow.NativeLibrary.DEBUG", "true");
		
		System.out.println("Hello TensorFlow " + TensorFlow.version());

		try (ConcreteFunction dbl = ConcreteFunction.create(HelloTensorFlowJava::dbl);
				TInt32 x = TInt32.scalarOf(10);
				Tensor dblX = dbl.call(x)) {
			System.out.println(x.getInt() + " doubled is " + ((TInt32) dblX).getInt());
		}
	}

	private static Signature dbl(Ops tf) {
		Placeholder<TInt32> x = tf.placeholder(TInt32.class);
		Add<TInt32> dblX = tf.math.add(x, x);
		return Signature.builder().input("x", x).output("dbl", dblX).build();
	}
}