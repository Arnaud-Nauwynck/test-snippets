package fr.an.tests.testdocker;

import java.util.List;
import java.util.Map;

public class FS {

	
	public static abstract class File {
		public abstract FileSystem getFileSystem();
		public abstract int getFD();
	}
	
	public static abstract class RegularFile extends File {
		// read(), write() => cf FileSystem.readFile(this), writeFile(this) .. 
	}
	
	public static abstract class Dir extends File {
		// list(), mkdir() => cf FileSystem.listDir(), mkdir() ..
	}
	
	public static abstract class FileSystem {
		// readFile(), writeFile(), ..
		// listDir(), mkdir(), ..
	}
	
	
	
	
	
	public static abstract class MountDir extends Dir {
		FileSystem fileSystem;
		Map<String,Object> mountOptions;
		// => delegate file/dir operations to fileSystem..
	}

	public static abstract class UnionMountDir extends MountDir {
		List<UnionFSLayer> layers; // <= init from mountOptions
		static class UnionFSLayer {
			Dir delegateDir;
			String readWriteMode; Map<String,Object> layerOptions;
		}
	}
	public static class UnionFsMountFileSystem extends FileSystem {
		// => interpret mountOptions as layers for read-write, read-only
		// ... and delegate read(),write(),list(),..  to underlying real dirs
	}
	

	
	public static abstract class Driver {
	}
	
	// example: "/dev/sdc0" ... device for accessing hard-disk driver
	public static abstract class DeviceFile extends File {
		Driver driver;
		int minor, major; Map<String,Object> deviceOptions;
		// readblock(), writeblock()  or readchar(), writechar()
	}

	public static abstract class LoopbackDeviceFile extends DeviceFile {
		File loopFile;
		Map<String,Object> loopbackOptions;
		// => delegate device read..()/write..() to loopFile
	}

	
}
