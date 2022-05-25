package fr.an.hadoop.fs.dirserver.attrtree;

public class NodeAttr {

	public final AttrInfo attrInfo;

	private int fileId;
	private int fileOffset;
	
	private int lruCount;
	private int lruAmortizedCount;

	private long lastQueryTimestamp;
	private long lastModifTimestamp;
	
	private Object data;
	
	// ------------------------------------------------------------------------
	
	public NodeAttr(AttrInfo attrInfo) {
		this.attrInfo = attrInfo;
	}

	// ------------------------------------------------------------------------
	
	public String getName() {
		return attrInfo.name;
	}
	
}
