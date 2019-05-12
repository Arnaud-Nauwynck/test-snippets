package fr.an.test.fsscancomp.leveldb;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.fusesource.leveldbjni.JniDBFactory;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;

import fr.an.test.fsscancomp.img.ImageEntry;
import fr.an.test.fsscancomp.img.ImageEntryHandler;
import lombok.AllArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImageEntryLevelDBStorage implements Closeable {

	private File dbDir;
	private org.iq80.leveldb.DB db;
	
	private int updateSeqNum;
	
	private ImageEntryValueDataReader entryReader = new ImageEntryValueDataReader();
	private ImageEntryValueDataWriter entryWriter = new ImageEntryValueDataWriter();
	
	
	public ImageEntryLevelDBStorage(File dbDir) {
		this.dbDir = dbDir;
		
		open();
	}

	@Override
	public void close() {
		try {
			this.db.close();
		} catch (IOException ex) {
			log.warn("Failed to close..", ex);
		}
	}


	private void open() {
		readDbInfo();
		
		Options dbOptions = new Options();
		dbOptions.createIfMissing(true);
		try {
			this.db = JniDBFactory.factory.open(dbDir, dbOptions);
		} catch (Throwable ex) {
			try {
				log.error("Failed to init levelDB jni .. fallback to java, ex:" + ex.getMessage());
				this.db = Iq80DBFactory.factory.open(dbDir, dbOptions);
			} catch (Exception ex2) {
				throw new RuntimeException("Failed", ex);
			}
		}
	}

	private void readDbInfo() {
		File dbInfoFile = new File(dbDir, "db-info");
		if (!dbInfoFile.exists()) {
			this.updateSeqNum = 0;
		} else {
			String dbInfoContent;
			try {
				dbInfoContent = FileUtils.readFileToString(dbInfoFile, Charset.defaultCharset());
			} catch (IOException ex) {
				throw new RuntimeException("Failed", ex);
			}
			this.updateSeqNum = Integer.parseInt(dbInfoContent);
		}
	}
	
	private void writeDbInfo() {
		File dbInfoFile = new File(dbDir, "db-info");
		String newDbInfoContent = String.valueOf(updateSeqNum);
		try {
			FileUtils.write(dbInfoFile, newDbInfoContent, Charset.defaultCharset());
		} catch (IOException ex) {
			throw new RuntimeException("Failed", ex);
		}
	}

	public void initUpdateEntries() {
		this.updateSeqNum++;
	}

	public void finishUpdateEntries() {
		writeDbInfo();
		removeNotUpdated();
	}

	public void updateEntry(ImageEntry entry) {
		String path = entry.path;
		byte[] key = path.getBytes();
		byte[] entryData = db.get(key);

		boolean isFile = entry.isFile;
		String crc = null;
		if (entryData != null) {
			DbImageEntry dbe = this.entryReader.read(path, entryData);
			
			crc = dbe.crc;
			// force recompute crc if changed..
			if (isFile && dbe.imageEntry.lastModified != entry.lastModified) {
				crc = null; // TODO
			}
		}

		if (isFile && crc == null) {
			// TODO
		}
		
		DbImageEntry updateDbe = new DbImageEntry(entry, updateSeqNum, crc);
		byte[] updateData = entryWriter.write(updateDbe);
		db.put(key, updateData);
	}
	
	public static class ImageEntryLevelDBUpdater extends ImageEntryHandler {
		private ImageEntryLevelDBStorage dbStorage;
		
		public ImageEntryLevelDBUpdater(ImageEntryLevelDBStorage dbStorage) {
			this.dbStorage = dbStorage;
		}

		@Override
		public void handle(ImageEntry entry) {
			dbStorage.updateEntry(entry);
		}

		@Override
		public void close() {
			dbStorage.finishUpdateEntries();
		}
	
	}
	
	public void scan(Consumer<DbImageEntry> consumer) {
		for(DBIterator dbIter = db.iterator(); dbIter.hasNext(); ) {
			val kv = dbIter.next();
			String path = new String(kv.getKey());
			byte[] data = kv.getValue();
			val dbe = entryReader.read(path, data);
			
			consumer.accept(dbe);
		}
	}

	public List<DbImageEntry> removeNotUpdated() {
		List<DbImageEntry> toRemove = new ArrayList<>();
		// step 1: iter
		for(DBIterator dbIter = db.iterator(); dbIter.hasNext(); ) {
			val kv = dbIter.next();
			String path = new String(kv.getKey());
			byte[] data = kv.getValue();
			val dbe = entryReader.read(path, data);
			if (dbe.updateSeqNum != updateSeqNum) {
				toRemove.add(dbe);
			}
		}
		// step 2: remove..
		if (! toRemove.isEmpty()) {
			for(val dbe : toRemove) {
				val key = dbe.imageEntry.path.getBytes();
				db.delete(key);
			}
		}
		return toRemove;
	}
	

	
	@AllArgsConstructor
	public static class DbImageEntry {
		ImageEntry imageEntry;
		int updateSeqNum;
		String crc; // md5..
	}
	
	public static class ImageEntryValueDataReader {
		public DbImageEntry read(String path, byte[] data) {
			try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(data))) {
				boolean isFile = in.readBoolean();
				long lastModified = in.readLong();
				long length = (isFile)? in.readLong() : 0;
				String md5 = (isFile)? in.readUTF() : null;
				val e = new ImageEntry(isFile, path, lastModified, length, md5);
				
				int updateSeqNum = in.readInt();
				String crc = (isFile)? in.readUTF() : "";
				
				return new DbImageEntry(e, updateSeqNum, crc);
			} catch(IOException ex) {
				throw new RuntimeException("Failed", ex);
			}
		}
	}
	
	public static class ImageEntryValueDataWriter {
		public byte[] write(DbImageEntry dbe) {
			val e = dbe.imageEntry;
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			try (DataOutputStream out = new DataOutputStream(buffer)) {
				boolean isFile = e.isFile;
				out.writeBoolean(isFile);

				out.writeLong(e.lastModified);
				if (isFile) {
					out.writeLong(e.length);
				}
				
				out.writeInt(dbe.updateSeqNum);
				if (isFile) {
					out.writeUTF(dbe.crc != null? dbe.crc : "");
				}

			} catch(IOException ex) {
				throw new RuntimeException("Failed", ex);
			}
			return buffer.toByteArray();
		}
	}
	
}
