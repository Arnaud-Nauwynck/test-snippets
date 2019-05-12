package fr.an.test.fsscancomp.img;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.an.test.fsscancomp.img.codec.ImageEntryFragmentIncrDataReader;
import fr.an.test.fsscancomp.utils.ImageEntrySortedMergedFragmentsReader;


public class ImageFragmentsReader {

	public static void readImage(
			File imageBaseDir, String baseImageName,
			ImageEntryHandler imageHandler) {
		File[] imageFiles = imageBaseDir.listFiles((dir,name) -> 
			name.startsWith(baseImageName)
		);
		int maxHash = 0;
		Pattern pattern = Pattern.compile(baseImageName + "\\.([0-9]+)\\.[0-9]+");
		for(File imageFile : imageFiles) {
			Matcher m = pattern.matcher(imageFile.getName());
			if (m.matches()) {
				int hash = Integer.parseInt(m.group(1));
				maxHash = Math.max(maxHash, hash);
			}
		}
		System.out.println("hash:" + maxHash);

		Collection<ImageEntryFragmentIncrDataReader> fragReaders = new ArrayList<>();
		for(int hash = 0; hash <= maxHash; hash++) {
			int fragCount = 0;
			for(int frag = 0; ; frag++) {
				File hashFragFile = new File(imageBaseDir, baseImageName + "." + hash + "." + frag);
				if (! hashFragFile.exists()) {
					break;
				}
				fragCount = Math.max(fragCount, frag+1);
			}
			System.out.println("handle hash:" + hash + " => fragCount:" + fragCount);

			for(int frag = 0; frag < fragCount; frag++) {
				File hashFragFile = new File(imageBaseDir, baseImageName + "." + hash + "." + frag);
				InputStream in;
				try {
					in = new BufferedInputStream(new FileInputStream(hashFragFile));
				} catch (Exception ex) {
					throw new RuntimeException("", ex);
				}
				fragReaders.add(new ImageEntryFragmentIncrDataReader(in));
			}
		}
		
		System.out.println("read sorted fragments & merge all");

		try (ImageEntrySortedMergedFragmentsReader mergeReader = 
				new ImageEntrySortedMergedFragmentsReader(fragReaders)) {

			int countEntry = 0;
			ImageEntry entry;
			while(null != (entry = mergeReader.readEntry())) {
				imageHandler.handle(entry);
				countEntry++;
			}

			System.out.println(".. done, read " + countEntry + " entries");
		}
		
	}
	
}
