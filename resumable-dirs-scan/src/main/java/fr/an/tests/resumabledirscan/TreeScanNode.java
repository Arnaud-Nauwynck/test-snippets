package fr.an.tests.resumabledirscan;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public class TreeScanNode {

	private final TreeScanNode parent;
	private final String childName;
	private final Path file;
	
	private Map<String,TreeScanNode> child;
	
	private FileStats fileStats = new FileStats();
	
	protected int childScanedCount;
	private FileStats synthetizedChildFileStats = new FileStats();
	
	// ------------------------------------------------------------------------
	
	public TreeScanNode(TreeScanNode parent, String childName, Path file) {
		this.parent = parent;
		this.childName = childName;
		this.file = file;
	}
	
	// ------------------------------------------------------------------------

	public void putChildDirs(Collection<TreeScanNode> ls) {
		 child = new HashMap<>(ls.size());
		 for(TreeScanNode e : ls) {
			 child.put(e.childName, e);
		 }
	}

	public void addFileStat(long fileLastModif, long fileLen) {
		fileStats.addFile(fileLastModif, fileLen);
	}
	
	public void addDirStat(long fileLastModif) {
		fileStats.addDir(fileLastModif);
	}

	public void addSynthetizedChildFileStats(FileStats fs) {
		synthetizedChildFileStats.addStats(fs);
		this.childScanedCount++;
	}


	public String getPath() {
		StringBuilder sb = new StringBuilder();
		getPath(sb);
		return sb.toString();
	}

	protected void getPath(StringBuilder sb) {
		assert parent != null; // cf override
		parent.getPath(sb);
		sb.append("/");
		sb.append(childName);
	}

	public static class RootTreeScanNode extends TreeScanNode {
		public final Path rootPath;

		public RootTreeScanNode(Path rootPath) {
			super(null, "", rootPath);
			this.rootPath = rootPath;
		}

		@Override
		protected void getPath(StringBuilder sb) {
			sb.append(rootPath);
		}
	}


}
