package fr.an.fssync.model;

public class FsPath implements Comparable<FsPath> {
    
    public static final FsPath ROOT = new FsPath(new String[0]);

    public final String[] elements;
    public final String uri;

    public FsPath(String[] elements) {
	this.elements = elements;
	this.uri = "/" + String.join("/", elements);
    }

    public static FsPath of(String path) {
	if (path.startsWith("/")) {
	    path = path.substring(1);
	}
	String[] pathElements = path.split("/");
	return new FsPath(pathElements);
    }

    public FsPath child(String childName) {
	int len = elements.length;
	String[] childElements = new String[len + 1];
	System.arraycopy(elements, 0, childElements, 0, len);
	childElements[len] = childName;
	return new FsPath(childElements);
    }
    
    public String toString() {
	return uri; // String.join("/", elements);
    }

    public String toUri() {
	return uri;
    }

    @Override
    public int hashCode() {
	return ((uri == null) ? 0 : uri.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	FsPath other = (FsPath) obj;
	if (uri == null) {
	    if (other.uri != null)
		return false;
	} else if (!uri.equals(other.uri))
	    return false;
	return true;
    }

    @Override
    public int compareTo(FsPath o) {
	return uri.compareTo(o.uri);
    }
    
}
