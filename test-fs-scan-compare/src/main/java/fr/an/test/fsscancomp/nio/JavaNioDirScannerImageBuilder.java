package fr.an.test.fsscancomp.nio;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import fr.an.test.fsscancomp.img.ImageEntry;
import fr.an.test.fsscancomp.img.ImageEntryHandler;

public class JavaNioDirScannerImageBuilder {

	private ImageEntryHandler fileHandler;
	
	public JavaNioDirScannerImageBuilder(ImageEntryHandler fileHandler) {
		this.fileHandler = fileHandler;
	};

	public void scan(Path rootScanDir) {
		try {
			Files.walkFileTree(rootScanDir, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					boolean isFile = attrs.isRegularFile();
					long lastModif = attrs.lastModifiedTime().toMillis();
					long fileLength = (isFile)? attrs.size() : 0;
					ImageEntry e = new ImageEntry(isFile, file.toString(), lastModif, fileLength, null);

					fileHandler.handle(e);

					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			throw new RuntimeException("Failed", e);
		}
	}

}
