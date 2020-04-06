package fr.an.fssync.sortedimgfrag.codec;

import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import fr.an.fssync.model.FsEntry;
import fr.an.fssync.model.FsEntryInfo;
import fr.an.fssync.model.visitor.FsEntryVisitor;

public class ImageEntryFragmentIncrDataWriter extends FsEntryVisitor implements Closeable {

    private DataOutputStream out;
    private StringBuilder prevPath = new StringBuilder(5000);

    public ImageEntryFragmentIncrDataWriter(OutputStream out) {
	this.out = new DataOutputStream(out);
    }

    @Override
    public void begin() {
    }

    @Override
    public void visit(FsEntry e) {
	try {
	    FsEntryInfo info = e.info;
	    char typeChar = info.iNodeTypeToChar();
	    out.writeChar(typeChar);
	    String path = e.path.toUri();

	    // find common part between path and prevPath
	    int commonPart = commonPart(path, prevPath);
	    int removeCount = prevPath.length() - commonPart;
	    String addPath = (commonPart < path.length()) ? path.substring(commonPart, path.length()) : ""; // should
													    // not occur

	    out.writeShort(removeCount);
	    out.writeUTF(addPath);

	    if (commonPart < prevPath.length()) {
		prevPath.delete(commonPart, prevPath.length());
	    }
	    prevPath.append(addPath);

	    out.writeLong(info.mtime);
	    switch(info.iNodeType) {
	    case FILE:
		out.writeLong(info.fileSize);
		out.writeUTF(info.md5);
		break;
	    case DIRECTORY:
	    	break;
	    case SYMLINK:
		out.writeUTF(info.symlinkTarget);
		break;
	    }

	    out.writeChar('\n');
	} catch (IOException ex) {
	    throw new RuntimeException("Failed", ex);
	}
    }

    private static int commonPart(String left, StringBuilder right) {
	int maxLen = Math.min(left.length(), right.length());
	for (int i = 0; i < maxLen; i++) {
	    if (left.charAt(i) != right.charAt(i)) {
		return i - 1;
	    }
	}
	return maxLen;
    }

    @Override
    public void end() {
    }
    
    @Override
    public void close() {
	try {
	    out.close();
	} catch (Exception ex) {
	    throw new RuntimeException("", ex);
	}
    }

}
