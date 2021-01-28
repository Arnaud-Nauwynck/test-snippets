package fr.an.tests.checkerframework;

import javax.annotation.Nullable;

import org.checkerframework.checker.nullness.qual.NonNull;

public class AppMain {

	public static void main(@Nullable String[] args) {
		
//		[WARNING] AppMain.java:[10,42] [argument.type.incompatible] incompatible types in argument.
//	  found   : @Initialized @Nullable String @Initialized @NonNull []
//	  required: @Initialized @NonNull String @Initialized @NonNull []
//	    System.out.println(countArgs(args));
		
		if (args != null) {
			// ok!
			System.out.println(countArgs(args));
		}
		
		
		// NO WARN here!
		System.out.println(countArgs2(args));
	}

	private static int countArgs(@NonNull String[] args) {
	    return args.length;
	}

	private static int countArgs2(String[] args) {
	    return args.length;
	}

}
