package fr.an.hadoop.fs.dirserver.attrtree.attrinfo;

import fr.an.hadoop.fs.dirserver.attrtree.encoder.AttrDataEncoder;

public class AttrInfo<T> {

	public final String name;
	public final Class<T> dataClass;

	public final AttrDataEncoder<T> attrDataEncoder;
	
	// ------------------------------------------------------------------------
	
	public AttrInfo(String name, Class<T> dataClass, AttrDataEncoder<T> attrDataEncoder) {
		this.name = name;
		this.dataClass = dataClass;
		this.attrDataEncoder = attrDataEncoder;
	}
	
	// ------------------------------------------------------------------------
	
}
