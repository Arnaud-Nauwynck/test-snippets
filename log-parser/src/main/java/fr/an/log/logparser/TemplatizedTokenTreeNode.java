package fr.an.log.logparser;

import java.util.LinkedHashMap;

/**
 * Prefix-Tree based on templatized token (type,null) or (type+value) 
 *
 */
public class TemplatizedTokenTreeNode {

	private final TemplatizedTokenKey key;

	private LinkedHashMap<TemplatizedTokenKey,TemplatizedTokenTreeNode> childMap = new LinkedHashMap<TemplatizedTokenKey,TemplatizedTokenTreeNode>();

	// IncrementCountLeafAction
	// CreateChildNodeAction
	
	private int countAsLeaf;
	
	// ------------------------------------------------------------------------

	public TemplatizedTokenTreeNode(TemplatizedTokenKey key) {
		super();
		this.key = key;
	}
	
	// ------------------------------------------------------------------------

	
	public TemplatizedTokenTreeNode findOrCreateChild(TemplatizedTokenKey childKey) {
		TemplatizedTokenTreeNode res = childMap.get(childKey);
		if (res == null) {
			// synchronized??
			res = new TemplatizedTokenTreeNode(childKey);
			childMap.put(childKey, res);
			
			// TODO trigger actions ?? 
			
		}
		return res;
	}


	public void incrCountAsLeaf() {
		countAsLeaf++;
	}

	// ------------------------------------------------------------------------
	
	public LinkedHashMap<TemplatizedTokenKey, TemplatizedTokenTreeNode> getChildMap() {
		return childMap;
	}

	public int getCountAsLeaf() {
		return countAsLeaf;
	}

	public void setCountAsLeaf(int countAsLeaf) {
		this.countAsLeaf = countAsLeaf;
	}

	public TemplatizedTokenKey getKey() {
		return key;
	}

	@Override
	public String toString() {
		return "TemplatizedTokenTreeNode [" + key + ", childCount:" + childMap.size() + ", countAsLeaf:" + countAsLeaf + "]";
	}

	
}
