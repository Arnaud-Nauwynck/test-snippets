package fr.an.tests.sortedmergediff;

import java.util.function.Function;
import java.util.function.Supplier;

import fr.an.tests.sortedmergediff.DiffEntry.DiffEntryType;
import fr.an.tests.sortedmergediff.DiffEntry.EqualsDiffEntry;
import fr.an.tests.sortedmergediff.DiffEntry.LeftOnlyDiffEntry;
import fr.an.tests.sortedmergediff.DiffEntry.RightOnlyDiffEntry;
import fr.an.tests.sortedmergediff.DiffEntry.UpdateDiffEntry;



public class IterSortMergeAlgorithms {

	public static <T,K extends Comparable<K>> Supplier<DiffEntry<T>> mergeSorted(
			Supplier<T> leftStream, 
			Supplier<T> rightStream,
			Function<T,K> keyExtract) {
		return new Supplier<DiffEntry<T>>() {
			T currLeft;
			T currRight;
			K currLeftKey;
			K currRightKey;
			int leftLineNum = 0;
			int rightLineNum = 0;
			{
				nextLeft();
				nextRight();
			}

			private void nextLeft() {
				currLeft = leftStream.get();
				if (currLeft != null) {
					currLeftKey = keyExtract.apply(currLeft);
				}
				leftLineNum++;
			}
			
			private void nextRight() {
				currRight = rightStream.get();
				if (currRight != null) {
					currRightKey = keyExtract.apply(currRight);
				}
				rightLineNum++;
			}
			
			@Override
			public DiffEntry<T> get() {
				DiffEntry<T> res;
				if (currLeft != null && currRight != null) {
					if (currLeft.equals(currRight)) {
						res = new EqualsDiffEntry<>(currLeft, leftLineNum, currRight, rightLineNum);
						nextLeft();
						nextRight();
					} else {
						int cmpKey = currLeftKey.compareTo(currRightKey);
						if (cmpKey == 0) {
							res = new UpdateDiffEntry<>(currLeft, leftLineNum, currRight, rightLineNum);
							nextLeft();
							nextRight();
						} else if (cmpKey < 0) {
							res = new LeftOnlyDiffEntry<>(currLeft, leftLineNum);
							nextLeft();
						} else {
							res = new RightOnlyDiffEntry<>(currRight, rightLineNum);							
							nextRight();
						}
					}
				} else if (currLeft != null) { // && currRight == null
					res = new LeftOnlyDiffEntry<>(currLeft, leftLineNum);
					nextLeft();
				} else if (currRight != null) {
					res = new RightOnlyDiffEntry<>(currRight, rightLineNum);							
					nextRight();
				} else { // both null
					return null; // finish
				}
				return res;
			}
		};
	}

	
	public static <T,K extends Comparable<K>> Supplier<T> applyPatchSorted(
			Supplier<T> leftStream, 
			Supplier<DiffEntry<T>> diffStream,
			Function<T,K> keyExtract) {
		return new Supplier<T>() {
			T currLeft;
			DiffEntry<T> currDiff;
			K currLeftKey;
			K currDiffKey;
			{
				nextLeft();
				nextDiff();
			}

			private void nextLeft() {
				currLeft = leftStream.get();
				if (currLeft != null) {
					currLeftKey = keyExtract.apply(currLeft);
				}
			}
			
			private void nextDiff() {
				currDiff = diffStream.get();
				if (currDiff != null) {
					currDiffKey = keyExtract.apply(currDiff.forKey());
				}
			}
			
			@Override
			public T get() {
				while(true) {
					T res;
					if (currLeft != null && currDiff != null) {
						int cmpKey = currLeftKey.compareTo(currDiffKey);
						if (cmpKey == 0) {
							DiffEntryType diffEntryType = currDiff.getDiffEntryType();
							if (diffEntryType == DiffEntryType.Update) {
								UpdateDiffEntry<T> d = (UpdateDiffEntry<T>) currDiff;
								res = d.right;
							} else if (diffEntryType == DiffEntryType.Equals) {
								EqualsDiffEntry<T> d = (EqualsDiffEntry<T>) currDiff;
								res = d.right;
							} else if (diffEntryType == DiffEntryType.LeftOnly) {
								// ??? should not occur
								nextLeft();
								nextDiff();
								res = null;
								continue; // skip left .. recurse
							} else {
								// ??? should not occur
								res = null;
							}
							nextLeft();
							nextDiff();
						} else if (cmpKey < 0) {
							// LeftOnlyDiffEntry<T> d = (LeftOnlyDiffEntry<T>) currDiff;
							nextLeft();
							nextDiff();
							res = null;
							continue; // skip left.. recurse (but prevent stack overflow..)
						} else { // cmpKey > 0
							RightOnlyDiffEntry<T> d = (RightOnlyDiffEntry<T>) currDiff;
							res = d.right;
							nextDiff();
						}
					} else if (currLeft != null) { // && currDiff == null
						res = currLeft; // assume Update for nulls diffs
						nextLeft();
					} else if (currDiff != null) {
						RightOnlyDiffEntry<T> d = (RightOnlyDiffEntry<T>) currDiff;
						res = d.right;
						nextDiff();
					} else { // both null
						return null; // finish
					}
					return res;
				}
			}
		};
	}

}
