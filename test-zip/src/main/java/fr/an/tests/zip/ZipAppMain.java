package fr.an.tests.zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipMethod;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.deflate64.Deflate64CompressorInputStream;
import org.apache.commons.compress.utils.BoundedSeekableByteChannelInputStream;
import org.apache.commons.compress.utils.InputStreamStatistics;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import lombok.AllArgsConstructor;
import lombok.val;

public class ZipAppMain {

	public static void main(String[] args) {
		val app = new ZipAppMain();
		val file = new File("target/test.zip");
		app.run(file, 1000);
	}
	
	public void run(File file, int entryCount) {

		writeZipFileWithZipOutputStream(file, entryCount);
		
		readZipFileWithInputStream(file);
		
		readZipFileWithIndex(file);

		byte[] zipContent;
		try {
			zipContent = FileUtils.readFileToByteArray(file);
		} catch (IOException ex) {
			throw new RuntimeException("", ex);
		}
		SeekableByteChannel seekableZipContent = new ReadOnlySeekableByteChannel(zipContent);

		readZipWithCommonsCompressSeekableIO(seekableZipContent, false);// ignoreLocalFileHeader=false
		
		System.out.println("REDO test read using ignoreLocalFileHeader=true");
		readZipWithCommonsCompressSeekableIO(seekableZipContent, true);
	}

	private void writeZipFileWithZipOutputStream(File file, int entryCount) {
		try (val out = new BufferedOutputStream(new FileOutputStream(file))) {
			ZipOutputStream zipOut = new ZipOutputStream(out);
			
			for(int i = 0; i < entryCount; i++) {
				ZipEntry ze = new ZipEntry("entry-" + i + ".txt");
				zipOut.putNextEntry(ze);
				
				String entryContent = "entry " + i + "\n";
				zipOut.write(entryContent.getBytes(StandardCharsets.UTF_8));
				
				zipOut.closeEntry();
			}
			
			zipOut.finish(); // write zip index
			zipOut.flush(); // flush buffer
			
		} catch (FileNotFoundException ex) {
			throw new RuntimeException("Failed", ex);
		} catch (IOException ex) {
			throw new RuntimeException("Failed", ex);
		}
	}

	private void readZipFileWithInputStream(File file) {
		try (val in = new BufferedInputStream(new FileInputStream(file))) {
			ZipInputStream zipIn = new ZipInputStream(in);
			
			for (ZipEntry ze = zipIn.getNextEntry(); ze != null; ze = zipIn.getNextEntry()) {
				System.out.println("next entry: '" + ze.getName() + "' " 
						+ " crc:" + ze.getCrc()
						+ " size:" + ze.getSize()
						+ " compressedSize:" + ze.getCompressedSize()
						);
				val entryContentBytes = IOUtils.toByteArray(zipIn);
				val entryContent = new String(entryContentBytes, StandardCharsets.UTF_8);
				System.out.println("content: " + entryContent);
				
				zipIn.closeEntry();
			}
			
		} catch (FileNotFoundException ex) {
			throw new RuntimeException("Failed", ex);
		} catch (IOException ex) {
			throw new RuntimeException("Failed", ex);
		}
	}

	private static void readZipFileWithIndex(File file) {
		try (val zipFile = new ZipFile(file)) {
		
			val entryCount = zipFile.size();
			System.out.println("entries.size:" + entryCount);
			val entries = zipFile.entries();
			
			List<ZipEntry> cachedEntries = new ArrayList<>();
			for(ZipEntry ze = entries.nextElement(); entries.hasMoreElements(); ze = entries.nextElement()) {
				System.out.println("entry: '" + ze.getName() + "'" 
						+ " crc:" + ze.getCrc()
						+ " size:" + ze.getSize()
						+ " compressedSize:" + ze.getCompressedSize()
						);
				cachedEntries.add(ze);
			}

			val rand = new Random(0);
			for(int i = 0; i < 10; i++ ) {
				val idx = rand.nextInt(entryCount);
				val ze = cachedEntries.get(idx);
				
				try (InputStream zipIn = zipFile.getInputStream(ze)) {
					System.out.println("read random index:" + idx);
					
					val entryContentBytes = IOUtils.toByteArray(zipIn);
					val entryContent = new String(entryContentBytes, StandardCharsets.UTF_8);
					System.out.println("content: " + entryContent);
				}
			}
			
		} catch (ZipException ex) {
			throw new RuntimeException("Failed", ex);
		} catch (IOException ex) {
			throw new RuntimeException("Failed", ex);
		}
		
	}

	private static final long OFFSET_UNKNOWN = -1;
	
	private void readZipWithCommonsCompressSeekableIO(SeekableByteChannel seekable, boolean ignoreLocalFileHeader) {
		System.out.println("read ZipFile using commons-compress SeekableByteChannel..");

		val entryOffsets = new ArrayList<SimpleEntryOffset>();

		String archiveName = "dummy-seekable.zip";
		try (val zipFile = new org.apache.commons.compress.archivers.zip.ZipFile(seekable, archiveName,
				"UTF8", true, ignoreLocalFileHeader)) {

			System.out.println("getEntries..");
			val cachedEntries = new ArrayList<ZipArchiveEntry>();
			
			val entries = zipFile.getEntries();
			
			// zipFile.getEntriesInPhysicalOrder();
			for(ZipArchiveEntry ze = entries.nextElement(); entries.hasMoreElements(); ze = entries.nextElement()) {
				long dataOffset = ze.getDataOffset(); // need to call zipFile.getDataOffset(ze) ... which is private method!!
				if (dataOffset == OFFSET_UNKNOWN) {
					System.out.println("pre-compute entry dataOffset !");
					try (InputStream zipIn = zipFile.getInputStream(ze)) { // => call zipFile.setDataoffset(ze) ... which is private method!!
					}
					dataOffset = ze.getDataOffset();
					if (dataOffset == OFFSET_UNKNOWN) {
						throw new UnsupportedOperationException("TODO unsupported.. ");
					}
				}

				long compressedSize = ze.getCompressedSize();
				System.out.println("entry: '" + ze.getName() + "'" 
						+ " crc:" + ze.getCrc()
						+ " offset:" + dataOffset
						+ " size:" + ze.getSize()
						+ " compressedSize:" + compressedSize
						);
				cachedEntries.add(ze);
				
				entryOffsets.add(new SimpleEntryOffset(ze.getName(), dataOffset, compressedSize, ze.getMethod()));
				
			}
			
			int entryCount = cachedEntries.size();
			
			System.out.println("random access..");
			val rand = new Random(0);
			for(int i = 0; i < 10; i++ ) {
				val idx = rand.nextInt(entryCount);
				val ze = cachedEntries.get(idx);
				
				try (InputStream zipIn = zipFile.getInputStream(ze)) {
					System.out.println("read random index:" + idx);
					
					val entryContentBytes = IOUtils.toByteArray(zipIn);
					val entryContent = new String(entryContentBytes, StandardCharsets.UTF_8);
					System.out.println("content: " + entryContent);
				}
			}
						
		} catch (IOException ex) {
			throw new RuntimeException("Failed", ex);
		}
		
		System.out.println("random access using simple entry offset..");
		val rand = new Random(0);
		val entryOffsetsCount = entryOffsets.size();  
		for(int i = 0; i < 10; i++ ) {
			val idx = rand.nextInt(entryOffsetsCount);
			val ze = entryOffsets.get(idx);
			
			try (InputStream zipIn = createZipInputStreamFromSimpleOffset(seekable, ze)) {
				System.out.println("read random index:" + idx);
				
				val entryContentBytes = IOUtils.toByteArray(zipIn);
				val entryContent = new String(entryContentBytes, StandardCharsets.UTF_8);
				System.out.println("content: " + entryContent);
			} catch(IOException ex) {
				throw new RuntimeException("Failed", ex);
			}
		}
	}

	@AllArgsConstructor
	public static class SimpleEntryOffset {
		public final String name;
		public final long dataOffset;
		public final long compressedSize;
		public final int method;
	}
	
	/**
	 * Returns an InputStream for reading the contents of the given entry.
	 *
	 * @param ze the entry to get the stream for.
	 * @return a stream to read the entry from. The returned stream
	 * implements {@link InputStreamStatistics}.
	 * @throws IOException if unable to create an input stream from the zipentry
	 */
	public InputStream createZipInputStreamFromSimpleOffset(SeekableByteChannel seekable, SimpleEntryOffset ze) throws IOException {
        final InputStream is = new BufferedInputStream( // useless here
        		new BoundedSeekableByteChannelInputStream(ze.dataOffset, ze.compressedSize, seekable)
        		);
        switch (ZipMethod.getMethodByCode(ze.method)) {
            case STORED:
                return is;
//            case UNSHRINKING:
//                return new UnshrinkingInputStream(is); // ... class is package protected !!!
//            case IMPLODING: // ... class is package protected !!!
//                try {
//                    return new ExplodingInputStream(ze.getGeneralPurposeBit().getSlidingDictionarySize(),
//                            ze.getGeneralPurposeBit().getNumberOfShannonFanoTrees(), is);
//                } catch (final IllegalArgumentException ex) {
//                    throw new IOException("bad IMPLODE data", ex);
//                }
            case DEFLATED:
                final Inflater inflater = new Inflater(true);
                // Inflater with nowrap=true has this odd contract for a zero padding
                // byte following the data stream; this used to be zlib's requirement
                // and has been fixed a long time ago, but the contract persists so
                // we comply.
                // https://docs.oracle.com/javase/7/docs/api/java/util/zip/Inflater.html#Inflater(boolean)
                byte[] ONE_ZERO_BYTE = new byte[1];
                return new InflaterInputStream(new SequenceInputStream(is, new ByteArrayInputStream(ONE_ZERO_BYTE)), inflater);

            case BZIP2:
                return new BZip2CompressorInputStream(is);
            case ENHANCED_DEFLATED:
                return new Deflate64CompressorInputStream(is);
            
            case AES_ENCRYPTED:
            case EXPANDING_LEVEL_1:
            case EXPANDING_LEVEL_2:
            case EXPANDING_LEVEL_3:
            case EXPANDING_LEVEL_4:
            case JPEG:
            case LZMA:
            case PKWARE_IMPLODING:
            case PPMD:
            case TOKENIZATION:
            case UNKNOWN:
            case WAVPACK:
            case XZ:
            default:
                throw new UnsupportedOperationException("unsupported entry compression method:" + ZipMethod.getMethodByCode(ze.method));
        }
	}
	
	// ------------------------------------------------------------------------
	
	public static class ReadOnlySeekableByteChannel implements SeekableByteChannel {
		private final byte[] data;
		private long position;
		private boolean opened = true;
		private boolean verboseIO = true;
		
	    public ReadOnlySeekableByteChannel(byte[] data) {
			this.data = data;
		}

	    @Override
	    public long position() {
	    	return this.position;
	    }
	
	    @Override
	    public SeekableByteChannel position(long newPosition) throws IOException {
	    	if (verboseIO) {
	    		if (newPosition == -1) {
	    			// error!
	    			System.out.println("(verboseIO) ERROR seek pos:" + newPosition);
	    		}
	    		System.out.println("(verboseIO) seek pos:" + newPosition);
	    	}
	    	this.position = newPosition;
	    	return this;
	    }
	
	    @Override
	    public long size() {
	    	return data.length;
	    }

		@Override
	    public int read(ByteBuffer dst) throws IOException {
			int dstRemain = dst.remaining();
	        if (dstRemain <= 0) {
	            return 0;
	        }
	        int available = (int) (data.length - position);
	        int count = Math.min(dstRemain, available);
	        val prevPos = position;
	        
	        dst.put(data, (int)position, count);
	        position += count;
	        
	        if (verboseIO) {
	        	System.out.println("(verboseIO) seekable read pos:" + prevPos + " count:" + count + " newPos: " + position);
	        }
	        
	        return count;
		}
	    
	    @Override
	    public int write(ByteBuffer src) throws IOException {
	    	throw new UnsupportedOperationException("read-only");
	    }
	
	    @Override
	    public SeekableByteChannel truncate(long size) throws IOException {
	    	throw new UnsupportedOperationException("read-only");
	    }

		@Override
		public boolean isOpen() {
			return opened;
		}

		@Override
		public void close() throws IOException {
			this.opened = false;
		}
	}
	
}
