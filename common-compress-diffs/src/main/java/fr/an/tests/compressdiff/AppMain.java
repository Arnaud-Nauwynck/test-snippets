package fr.an.tests.compressdiff;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.compress.archivers.zip.GeneralPurposeBit;
import org.apache.commons.compress.archivers.zip.UnparseableExtraFieldData;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry.CommentSource;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry.NameSource;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipExtraField;

import lombok.AllArgsConstructor;
import lombok.Builder;

public class AppMain {

    public static void main(String[] args) {
	try {
	    AppMain app = new AppMain();
	    app.run(args);
	} catch (Exception ex) {
	    System.err.println("Failed");
	    ex.printStackTrace(System.err);
	}
    }

    
    private void run(String[] args) throws IOException {
	File leftFile = new File(args[0]);
	File rightFile = new File(args[1]);

	ZipFileChecksumInfo leftInfo = zipFileChecksumInfo(leftFile);
	ZipFileChecksumInfo rightInfo = zipFileChecksumInfo(rightFile);
	
	DiffResult diff = new DiffResult();
	
	// TODO compare entry orders..
	
	// scan left entries, find corresponding entry in right and remove from remainingRight
	Map<String,ZipEntryChecksum> remainRight = new HashMap<>(rightInfo.entryChecksums);
	for(ZipEntryChecksum leftEntry : leftInfo.entryChecksums.values()) {
	    String name = leftEntry.name;
	    ZipEntryChecksum rightEntry = remainRight.remove(name);
	    if (rightEntry != null) {
		// compare left-right entry by checksums   
		compareEntryChecksumInfo(diff, leftEntry, rightEntry);
	    } else {
		diff.leftOnlyEntry(leftEntry);
	    }
	}
	// scan remaining right entry
	for(ZipEntryChecksum rightEntry : remainRight.values()) {
	    diff.rightOnlyEntry(rightEntry);
	}
    }

    
    
    public static class DiffResult {
	DiffResultBuilder diffBuffer = new DiffResultBuilder();
	
	public void leftOnlyEntry(ZipEntryChecksum leftEntry) {
	    System.out.println("left only: " + leftEntry.name);
	}
	public void rightOnlyEntry(ZipEntryChecksum rightEntry) {
	    System.out.println("right only: " + rightEntry.name);
	}
	public void diffEntry(ZipEntryChecksum leftEntry, ZipEntryChecksum rightEntry, String diffText) {
	    System.out.println("diff entry: " 
		    + leftEntry.name
		    + " " + diffText);
	}
    }
    
    protected static class DiffResultBuilder {
	int diffCount = 0;
	Map<String,String> fieldDiffs = new HashMap<>();
	
	public void diff(String fieldName, boolean left, boolean right) {
	    if (left != right) {
		diffCount++;
		fieldDiffs.put(fieldName, left + "<>" + right + " ");
	    }
	}
	public void diff(String fieldName, int left, int right) {
	    if (left != right) {
		diffCount++;
		fieldDiffs.put(fieldName, left + "<>" + right + " ");
	    }
	}
	public void diff(String fieldName, long left, long right) {
	    if (left != right) {
		diffCount++;
		fieldDiffs.put(fieldName, left + "<>" + right + " ");
	    }
	}
	public void diff(String fieldName, Object left, Object right) {
	    if (! Objects.equals(left, right)) {
		diffCount++;
		fieldDiffs.put(fieldName, left + "<>" + right + " ");
	    }
	}

	
	public String asStringIfDiff() {
	    if (diffCount == 0) {
		return null;
	    }
	    return fieldDiffs.toString();
	}
	public void clear() {
	    diffCount = 0;
	    fieldDiffs.clear();
	}
    }

    
    

    @AllArgsConstructor
    public static class ZipFileChecksumInfo {
	List<String> orderedEntries;
	Map<String,ZipEntryChecksum> entryChecksums;
    }
    
    
    public static ZipFileChecksumInfo zipFileChecksumInfo(File file) throws IOException {
	try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
	    return zipFileChecksumInfo(in);
	}
    }
    
    public static ZipFileChecksumInfo zipFileChecksumInfo(InputStream in) throws IOException {
	List<String> orderedEntries = new ArrayList<>();
	Map<String, ZipEntryChecksum> entryChecksums = new HashMap<>();
	@SuppressWarnings("resource")
	ZipArchiveInputStream zin = new ZipArchiveInputStream(in);
	ZipArchiveEntry entry;
        while ((entry = zin.getNextZipEntry()) != null) {
            String name = entry.getName();
            orderedEntries.add(name);
            entryChecksums.put(name, zipEntryChecksumInfo(entry));
        }	
	return new ZipFileChecksumInfo(orderedEntries, entryChecksums);
    }

    
    @Builder
    public static class ZipEntryChecksum {
	public final String name;

	public final long xdostime;
        public final FileTime mtime;     // last modification time, from extra field data
        public final FileTime atime;     // last access time, from extra field data
        public final FileTime ctime;     // creation time, from extra field data
        public final long crc;      // crc-32 of entry data
        public final long size;     // uncompressed size of entry data
        public final long csize;    // compressed size of entry data
        public final int method;    // compression method
        public final int flag = 0;       // general purpose flag
        public final byte[] extra;       // optional extra field data for entry
        public final String comment;     // optional comment string for entry

	public final int internalAttributes;
	public final int versionRequired;
	public final int versionMadeBy;
	public final int platform;
	public final int rawFlag;
	public final long externalAttributes;
	public final int alignment;
	public final ZipExtraField[] extraFields;
	public final UnparseableExtraFieldData unparseableExtra;
	public final byte[] rawName;
	public final GeneralPurposeBit gpb; // = new GeneralPurposeBit();
	public final long localHeaderOffset;
	public final long dataOffset;
	public final boolean isStreamContiguous;
	public final NameSource nameSource;
	public final CommentSource commentSource;

    }

    private static ZipEntryChecksum zipEntryChecksumInfo(ZipArchiveEntry entry) {
	return ZipEntryChecksum.builder()
		.name(entry.getName())
		// ?? .xdostime(entry.getTime())
	        .mtime(entry.getLastModifiedTime())
	        .atime(entry.getLastAccessTime())
	        .ctime(entry.getCreationTime())
	        .crc(entry.getCrc())
	        .size(entry.getSize())
	        .csize(entry.getCompressedSize())
	        .method(entry.getMethod())
	        // ?? .flag(entry.getFlag())
	        .extra(entry.getExtra())
	        .comment(entry.getComment())
	        
		.internalAttributes(entry.getInternalAttributes())
		.versionRequired(entry.getVersionRequired())
		.versionMadeBy(entry.getVersionMadeBy())
		.platform(entry.getPlatform())
		.rawFlag(entry.getRawFlag())
		.externalAttributes(entry.getExternalAttributes())
		// ??.alignment(entry.getAli)
		.extraFields(entry.getExtraFields())
		.unparseableExtra(entry.getUnparseableExtraFieldData()) // ??
		.rawName(entry.getRawName())
		.gpb(entry.getGeneralPurposeBit())
		// ?? .localHeaderOffset(entry.getLocalFileDataExtra())
		.dataOffset(entry.getDataOffset())
		.isStreamContiguous(entry.isStreamContiguous())
		.nameSource(entry.getNameSource())
		.commentSource(entry.getCommentSource())

		.build();
    }
    
    private static void compareEntryChecksumInfo(DiffResult diff, ZipEntryChecksum left, ZipEntryChecksum right) {
	DiffResultBuilder b = diff.diffBuffer;

        b.diff("size", left.size, right.size);
        if (left.size == right.size) {
            // compare only if size matches
            b.diff("crc", left.crc, right.crc);
            if (left.crc == right.crc) {
                b.diff("csize", left.csize, right.csize);
            }
        }
	// long xdostime;
        b.diff("mtime", left.mtime, right.mtime);
        b.diff("atime", left.atime, right.atime);
        b.diff("ctime", left.ctime, right.ctime);
        b.diff("method", left.method, right.method);
        // flag
        // byte[] extra;       // optional extra field data for entry
        b.diff("comment", left.comment, right.comment);
	
	b.diff("internalAttributes", left.internalAttributes, right.internalAttributes);
	b.diff("versionRequired", left.versionRequired, right.versionRequired);
	b.diff("versionMadeBy", left.versionMadeBy, right.versionMadeBy);
	b.diff("platform", left.platform, right.platform);
	b.diff("rawFlag", left.rawFlag, right.rawFlag);
	b.diff("externalAttributes", left.externalAttributes, right.externalAttributes);
	b.diff("alignment", left.alignment, right.alignment);
	// TODO
//	public final ZipExtraField[] extraFields;
//	public final UnparseableExtraFieldData unparseableExtra;
//	public final byte[] rawName;
//	public final GeneralPurposeBit gpb; // = new GeneralPurposeBit();
	b.diff("localHeaderOffset", left.localHeaderOffset, right.localHeaderOffset);
	// should ignore after first diff !!! b.diff("dataOffset", left.dataOffset, right.dataOffset);
	b.diff("isStreamContiguous", left.isStreamContiguous, right.isStreamContiguous);
//	public final NameSource nameSource;
//	public final CommentSource commentSource;
    
	String diffStr = b.asStringIfDiff();
	if (diffStr != null) {
	    diff.diffEntry(left, right, diffStr);
	}
	b.clear();
    }

}
