package fr.an.tests.mergesortpriorityqueue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import lombok.val;

public class AppMain {

	Random rand = new Random(0);

	public static void main(String[] args) {
		new AppMain().run(args);
	}

	public void run(String[] args) {
		int sortedListCount = 100;
		int listLen = 100;
		int stringLen = 8;
		
		System.out.println("generate random strings List List, sort sub list");
		List<List<String>> sortedFragList = new ArrayList<>();
		for (int i = 0; i < sortedListCount; i++) {
			sortedFragList.add(generateRandomSortedList(listLen, stringLen));
		}
		System.out.println("done generated + pre sort sub lists");
		
		List<List<String>> copySortedFragList = new ArrayList<>();
		List<String> copyAllList = new ArrayList<>();
		for(val src: sortedFragList) {
			copySortedFragList.add(new ArrayList<>(src));
			copyAllList.addAll(src);
		}
		
		long startMergeNanos = System.nanoTime();
		List<String> mergeSortList = mergeSort(sortedFragList);
		long nanosMerge = System.nanoTime() - startMergeNanos;
		
		long startSortAllNanos = System.nanoTime();
		copyAllList.sort(Comparator.naturalOrder());
		long nanosSortAll = System.nanoTime() - startSortAllNanos;
		// check equals
		int totalLen = copyAllList.size();
		for(int i = 0; i < totalLen; i++) {
			if (! mergeSortList.get(i).equals(copyAllList.get(i))) {
				throw new IllegalStateException("SHOULD NOT OCCUR");
			}
		}
		System.out.println("finish sortMerge:" + (nanosMerge/1000) + " µs, sortAll:" + (nanosSortAll/1000) + " µs");
	}

	private static class RemainSortedFrag {
		List<String> sortedFrag;
		int fromIndex;
		
		public RemainSortedFrag(List<String> sortedFrag) {
			this.sortedFrag = sortedFrag;
		}

		String first() {
			return sortedFrag.get(fromIndex);
		}
		void incrAndAddTo(List<String> res, int toPos) {
			res.addAll(sortedFrag.subList(fromIndex, toPos));
			fromIndex = toPos;
		}
		boolean isEmpty() {
			return fromIndex >= sortedFrag.size();
		}
	}

	private List<String> mergeSort(List<List<String>> sortedFragList) {
		int totalLen = 0;
		for(val ls : sortedFragList) {
			totalLen += ls.size(); 
		}
		List<String> res = new ArrayList<>(totalLen);
		
		PriorityQueue<RemainSortedFrag> remainFragSortedByFirst = new PriorityQueue<>(sortedFragList.size(), new RemainSortedFragByFirstComparator());
		for(val frag: sortedFragList) {
			remainFragSortedByFirst.add(new RemainSortedFrag(frag));
		}
		
		RemainSortedFrag currFrag = remainFragSortedByFirst.poll();
		for(;;) {
			RemainSortedFrag nextFrag = remainFragSortedByFirst.poll();
			if (nextFrag == null) {
				currFrag.incrAndAddTo(res, currFrag.sortedFrag.size());
				break;
			}
			String nextFragFirst = nextFrag.first();
			// find max position in currFrag (<= nextFragFirst)
			int foundPos = binarySearch(currFrag.sortedFrag, currFrag.fromIndex, currFrag.sortedFrag.size(), nextFragFirst);
			// assert foundPos != 0; 
			if (foundPos >= 0) {
				// found duplicate elt?
				currFrag.incrAndAddTo(res, foundPos);
			} else {
				int pos = -(foundPos+1);
				currFrag.incrAndAddTo(res, pos);
			}
			if (! currFrag.isEmpty()) {
				remainFragSortedByFirst.add(currFrag);
			}
			currFrag = nextFrag;
		}
		return res;
	}

	private static class RemainSortedFragByFirstComparator implements Comparator<RemainSortedFrag> {

		@Override
		public int compare(RemainSortedFrag o1, RemainSortedFrag o2) {
			String o1First = o1.first();
			String o2First = o2.first();
			return o1First.compareTo(o2First);
		}
		
	}

	// Copy&Paste from java.util.Arrays
	private static int binarySearch(List<String> a, int fromIndex, int toIndex, String key) {
		return binarySearch0(a, fromIndex, toIndex, key);
	}

	// idem java.util.Arrays.binarySearch()
	private static int binarySearch0(List<String> a, int fromIndex, int toIndex, String key) {
		int low = fromIndex;
		int high = toIndex - 1;
	
		while (low <= high) {
			int mid = (low + high) >>> 1;
			String midVal = a.get(mid);
	
			int comp = midVal.compareTo(key);
			if (comp < 0)
				low = mid + 1;
			else if (comp > 0)
				high = mid - 1;
			else
				return mid; // key found
		}
		return -(low + 1); // key not found.
	}


	private List<String> generateRandomSortedList(int listLen, int stringLen) {
		List<String> ls = new ArrayList<>(listLen);
		for (int j = 0; j < listLen; j++) {
			String randString = createRandomString(20);
			ls.add(randString);
		}
		ls.sort(Comparator.naturalOrder());
		return ls;
	}

	private String createRandomString(int stringLen) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < stringLen/3; i++) {
			sb.append(rand.nextInt(1000));
		}
		String randString = sb.toString();
		return randString;
	}

}
