package fr.an.hadoop.fsimagetool.io;

import java.util.Comparator;

public class ImageEntry implements Comparable<ImageEntry> {
	
	public static final Comparator<ImageEntry> PATH_COMPARATOR = new Comparator<ImageEntry>() {
		public int compare(final ImageEntry l, final ImageEntry r) {
			return l.path.compareTo(r.path);
		}
	};
	
	public boolean isFile;
	public String path;
	public long lastModified;
	public long length;
	
	
	public ImageEntry(boolean isFile, String path, long lastModified, long length) {
		this.isFile = isFile;
		this.path = path;
		this.lastModified = lastModified;
		this.length = length;
	}


	@Override
	public int compareTo(ImageEntry other) {
		return path.compareTo(other.path);
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImageEntry other = (ImageEntry) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}
	
	
}