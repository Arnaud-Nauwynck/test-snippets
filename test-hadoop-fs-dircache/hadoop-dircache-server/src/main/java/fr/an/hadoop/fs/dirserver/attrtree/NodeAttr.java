package fr.an.hadoop.fs.dirserver.attrtree;

import fr.an.hadoop.fs.dirserver.attrtree.attrinfo.AttrInfo;
import lombok.Getter;

@Getter
public class NodeAttr {

	public final AttrInfo<Object> attrInfo;

	private int fileId;
	private int fileOffset;
	
	private int lruCount;
	private int lruAmortizedCount;

	private long lastQueryTimestamp;
	private long lastModifTimestamp;
	
	private Object data;
	
	// ------------------------------------------------------------------------

	public NodeAttr(AttrInfo attrInfo, Object data) {
		this.attrInfo = attrInfo;
		this.data = data;
	}

	// ------------------------------------------------------------------------
	
	public String getName() {
		return attrInfo.name;
	}
	
}
