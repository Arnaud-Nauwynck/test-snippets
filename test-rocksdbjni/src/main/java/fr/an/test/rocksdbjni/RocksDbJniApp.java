package fr.an.test.rocksdbjni;

import java.util.Arrays;

import org.rocksdb.FlushOptions;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RocksDbJniApp {

	private static Logger log = LoggerFactory.getLogger(RocksDbJniApp.class);

	String dataDir = "data";

	public static void main(String[] args) {
		try {
			RocksDbJniApp app = new RocksDbJniApp();
			app.run();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void run() {
		log.info("loads RocksDB native library");
		RocksDB.loadLibrary();

		// the Options class contains a set of configurable DB options
		// that determines the behaviour of the database.
		try (@SuppressWarnings("resource") final Options options = new Options().setCreateIfMissing(true)) {

			log.info("open RocksDB on folder '" + dataDir + "'");
			try (final RocksDB db = RocksDB.open(options, dataDir)) {

				log.info("test put/get keys");

				runWithDb(db);
				
				db.flush(new FlushOptions());
			}
		} catch (RocksDBException ex) {
			throw new RuntimeException("Failed", ex);
		}
		log.info("finished");
	}

	private void runWithDb(RocksDB db) {
		byte[] key1 = "key1".getBytes();
		byte[] value1 = "value1".getBytes();
		
		try {
			db.put(key1, value1);
		} catch (RocksDBException ex) {
			throw new RuntimeException("Failed to put key", ex);
		}
		
		byte[] checkValue1;
		try {
			checkValue1 = db.get(key1);
		} catch (RocksDBException ex) {
			throw new RuntimeException("Failed to get key", ex);
		}
		if (! Arrays.equals(checkValue1, value1)) {
			throw new IllegalStateException();
		}
	}
}
