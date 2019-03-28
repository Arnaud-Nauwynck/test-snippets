package fr.an.tests.sortedmergediff;

import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.Assert;
import org.junit.Test;

import fr.an.tests.sortedmergediff.DiffEntry.EqualsDiffEntry;
import fr.an.tests.sortedmergediff.DiffEntry.LeftOnlyDiffEntry;
import fr.an.tests.sortedmergediff.DiffEntry.RightOnlyDiffEntry;
import fr.an.tests.sortedmergediff.DiffEntry.UpdateDiffEntry;
import lombok.val;

public class IterSortMergeAlgorithmsTest {

	@Test
	public void testEquals() {
		String[] elts = new String[] { "aa 1", "ab 1", "ac 1", "ad 1"};
		Supplier<String> left = new EltArraySupplier<>(elts);
		Supplier<String> right = new EltArraySupplier<>(elts);
		Supplier<DiffEntry<String>> resDiff = IterSortMergeAlgorithms.mergeSorted(left, right, keyExtract);
		// assert no diff..
		for(int i = 0; i < elts.length; i++) {
			DiffEntry<String> de = resDiff.get();
			Assert.assertTrue(de instanceof EqualsDiffEntry);
		}
	}

	@Test
	public void testLeftOnly() {
		String[] leftElts = new String[] { "aa 1", "aaxx 1", "aaxy 1", "ab 1", "ac 1", "ad 1", "bxx 1"};
		String[] rightElts = new String[] { "aa 1", /*             */  "ab 1", "ac 1", "ad 1"};
		Supplier<String> left = new EltArraySupplier<>(leftElts);
		Supplier<String> right = new EltArraySupplier<>(rightElts);
		Supplier<DiffEntry<String>> resDiff = IterSortMergeAlgorithms.mergeSorted(left, right, keyExtract);
		// assert..
		assertEqualsDiff(resDiff.get(), "aa 1", 1, 1);
		assertLeftOnly(resDiff.get(), "aaxx 1", 2);
		assertLeftOnly(resDiff.get(), "aaxy 1", 3);
		assertEqualsDiff(resDiff.get(), "ab 1", 4, 2);
		assertEqualsDiff(resDiff.get(), "ac 1", 5, 3);
		assertEqualsDiff(resDiff.get(), "ad 1", 6, 4);
		assertLeftOnly(resDiff.get(), "bxx 1", 7);
		Assert.assertNull(resDiff.get());
	}

	@Test
	public void testRightOnly() {
		String[] leftElts = new String[] { "aa 1", /*             */  "ab 1", "ac 1", "ad 1"};
		String[] rightElts = new String[] { "aa 1", "aaxx 1", "aaxy 1", "ab 1", "ac 1", "ad 1", "bxx 1"};
		Supplier<String> left = new EltArraySupplier<>(leftElts);
		Supplier<String> right = new EltArraySupplier<>(rightElts);
		Supplier<DiffEntry<String>> resDiff = IterSortMergeAlgorithms.mergeSorted(left, right, keyExtract);
		// assert..
		assertEqualsDiff(resDiff.get(), "aa 1", 1, 1);
		assertRightOnly(resDiff.get(), "aaxx 1", 2);
		assertRightOnly(resDiff.get(), "aaxy 1", 3);
		assertEqualsDiff(resDiff.get(), "ab 1", 2, 4);
		assertEqualsDiff(resDiff.get(), "ac 1", 3, 5);
		assertEqualsDiff(resDiff.get(), "ad 1", 4, 6);
		assertRightOnly(resDiff.get(), "bxx 1", 7);
		Assert.assertNull(resDiff.get());
	}

	@Test
	public void testUpdateOnly() {
		String[] leftElts  = new String[] { "aa 1", "ab 1", "ac 1", "ad 1"};
		String[] rightElts = new String[] { "aa 2", "ab 1", "ac 2", "ad 2"};
		Supplier<String> left = new EltArraySupplier<>(leftElts);
		Supplier<String> right = new EltArraySupplier<>(rightElts);
		Supplier<DiffEntry<String>> resDiff = IterSortMergeAlgorithms.mergeSorted(left, right, keyExtract);
		// assert..
		assertUpdate(resDiff.get(), "aa 1", 1, "aa 2", 1);
		assertEqualsDiff(resDiff.get(), "ab 1", 2, 2);
		assertUpdate(resDiff.get(), "ac 1", 3, "ac 2", 3);
		assertUpdate(resDiff.get(), "ad 1", 4, "ad 2", 4);
		Assert.assertNull(resDiff.get());
	}

	@Test
	public void testDiffThenPatch_leftOnly() {
		String[] leftElts  = new String[] { "aa 1", "aaxx 1", "aaxy 1", "ab 1", "ac 1", "ad 1", "bxx 1"};
		String[] rightElts = new String[] { "aa 1", /*              */  "ab 1", "ac 1", "ad 1"};
		doTestDiffThenPatch(leftElts, rightElts);	
	}
	
	@Test
	public void testDiffThenPatch_rightOnly() {
		String[] leftElts  = new String[] { "aa 1", /*             */  "ab 1", "ac 1", "ad 1"};
		String[] rightElts = new String[] { "aa 1", "aaxx 1", "aaxy 1", "ab 1", "ac 1", "ad 1", "bxx 1"};
		doTestDiffThenPatch(leftElts, rightElts);	
	}
	
	@Test
	public void testDiffThenPatch_updateOnly() {
		String[] leftElts  = new String[] { "aa 1", "ab 1", "ac 1", "ad 1"};
		String[] rightElts = new String[] { "aa 2", "ab 1", "ac 2", "ad 2"};
		doTestDiffThenPatch(leftElts, rightElts);	
	}
	
	protected void doTestDiffThenPatch(String[] leftElts, String[] rightElts) {
		Supplier<String> left = new EltArraySupplier<>(leftElts);
		Supplier<String> right = new EltArraySupplier<>(rightElts);
		// diff
		Supplier<DiffEntry<String>> diffStream = IterSortMergeAlgorithms.mergeSorted(left, right, keyExtract);
		Supplier<String> leftAgainStream = new EltArraySupplier<>(leftElts);
		// apply patch from diff
		Supplier<String> actualRightStream = IterSortMergeAlgorithms.applyPatchSorted(
				leftAgainStream, diffStream, keyExtract);
		// check same
		for(int i = 0; i < rightElts.length; i++) {
			String actualRight = actualRightStream.get();
			String expectedRight = rightElts[i];
			Assert.assertEquals(actualRight, expectedRight);
			if (actualRight == null && expectedRight == null) {
				break;
			}
		}
	}
		
		
	public static Function<String,String> keyExtract = (line) -> {
		int indexSep = line.indexOf(' ');
		return (indexSep != -1)? line.substring(0, indexSep) : line;
	};
	
	public static class EltArraySupplier<T> implements Supplier<T> {
		T[] elts;
		int currIndex = 0;

		public EltArraySupplier(T... elts) {
			this.elts = elts;
		}
		
		@Override
		public T get() {
			if (currIndex < elts.length) {
				val res = elts[currIndex];
				currIndex++;
				return res;
			}
			return null;
		}
		
	}
	
	protected static <T> void assertLeftOnly(DiffEntry<T> actual, T expectedLeft, int expectedLeftLineNum) {
		Assert.assertTrue(actual instanceof LeftOnlyDiffEntry);
		LeftOnlyDiffEntry<T> a = (LeftOnlyDiffEntry<T>) actual;
		Assert.assertEquals(a.left, expectedLeft);
		Assert.assertEquals(a.leftLineNum, expectedLeftLineNum);
	}

	protected static <T> void assertRightOnly(DiffEntry<T> actual, T expectedRight, int expectedRightLineNum) {
		Assert.assertTrue(actual instanceof RightOnlyDiffEntry);
		RightOnlyDiffEntry<T> a = (RightOnlyDiffEntry<T>) actual;
		Assert.assertEquals(a.right, expectedRight);
		Assert.assertEquals(a.rightLineNum, expectedRightLineNum);
	}

	protected static <T> void assertEqualsDiff(DiffEntry<T> actual, 
			T expected, int expectedLeftLineNum, int expectedRightLineNum) {
		Assert.assertTrue(actual instanceof EqualsDiffEntry);
		EqualsDiffEntry<T> a = (EqualsDiffEntry<T>) actual;
		Assert.assertEquals(a.left, expected);
		Assert.assertEquals(a.leftLineNum, expectedLeftLineNum);
		Assert.assertEquals(a.right, expected);
		Assert.assertEquals(a.rightLineNum, expectedRightLineNum);
	}

	protected static <T> void assertUpdate(DiffEntry<T> actual, 
			T expectedLeft, int expectedLeftLineNum, 
			T expectedRight, int expectedRightLineNum) {
		Assert.assertTrue(actual instanceof UpdateDiffEntry);
		UpdateDiffEntry<T> a = (UpdateDiffEntry<T>) actual;
		Assert.assertEquals(a.left, expectedLeft);
		Assert.assertEquals(a.leftLineNum, expectedLeftLineNum);
		Assert.assertEquals(a.right, expectedRight);
		Assert.assertEquals(a.rightLineNum, expectedRightLineNum);
	}

}
