package fr.an.tests.sortedmergediff;

import java.io.File;
import java.io.Writer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SortMergeUtil {

	public static <T> void sortDiffStreams(
			Supplier<T> inLeft, Supplier<T> inRight, 
			Consumer<DiffEntry<T>> out,
			Function<T,Comparable<?>> keyExtract) {
		// TODO
	}

	public static <T> void sortMergeStreams(
			Supplier<T> inLeft, Supplier<T> inRight, 
			Consumer<T> out,
			Function<T,Comparable<?>> keyExtract) {
		// TODO
	}
	
	
	public static <T,Diff> void sortApplyPatchStreams(
			Supplier<T> inLeft, Supplier<DiffEntry<T>> inLeftToRight,
			Consumer<T> outRight,
			Function<T,Comparable<?>> keyExtract) {

	}

	
	public static void sortDiffFileLines(
			File inLeft, File inRight, 
			Writer outDiff,
			Function<String,Comparable<?>> keyExtract) {
		// TODO
	}

	public static void sortMergeFileLines(
			File inLeft, File inRight,
			File outMerged,
			Function<String,Comparable<?>> keyExtract) {
		// TODO
	}

	public static void sortApplyPatchFile(
			File inLeft, File inLeftToRightPatch,
			File  outRight,
			Function<String,Comparable<?>> keyExtract) {
		
	}

}
