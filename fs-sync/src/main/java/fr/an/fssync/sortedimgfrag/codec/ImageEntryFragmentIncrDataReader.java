package fr.an.fssync.sortedimgfrag.codec;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import fr.an.fssync.model.FsEntry;
import fr.an.fssync.model.FsEntryInfo;
import fr.an.fssync.model.FsPath;
import lombok.val;

public class ImageEntryFragmentIncrDataReader implements Closeable {

    private DataInputStream in;
    private FsPath prevPath = FsPath.ROOT;

    public ImageEntryFragmentIncrDataReader(InputStream in) {
	this.in = new DataInputStream(in);
    }

    @Override
    public void close() {
	try {
	    in.close();
	} catch (IOException e) {
	}
    }

    public FsEntry readEntry() {
	char typeChar;
	try {
	    typeChar = in.readChar();
	} catch (EOFException ex) {
	    return null;
	} catch (IOException ex) {
	    throw new RuntimeException("", ex);
	}

	try {
	    int removeCharCount = in.readShort();
	    String addPath = in.readUTF();

	    // to optim: FsPath -> String -> FsPath
	    String pathUri = prevPath.uri;
	    int commonPart = pathUri.length() - removeCharCount;
	    if (commonPart < pathUri.length()) {
		pathUri = pathUri.substring(0, commonPart);
	    }
	    pathUri += addPath;
	    FsPath path = FsPath.of(pathUri);

	    val info = FsEntryInfo.builder();

	    info.mtime(in.readLong());
	    switch(typeChar) {
	    case 'f':
		info.fileSize(in.readLong());
		info.md5(in.readUTF());
		break;
	    case 'd':
	    	break;
	    case 'l':
		info.symlinkTarget(in.readUTF());
		break;
	    }

	    // TODO

	    in.readChar(); // '\n'

	    return new FsEntry(path, info.build());
	} catch (IOException ex) {
	    throw new RuntimeException("Failed", ex);
	}
    }

}
