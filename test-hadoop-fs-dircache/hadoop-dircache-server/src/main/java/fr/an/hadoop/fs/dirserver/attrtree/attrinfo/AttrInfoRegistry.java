package fr.an.hadoop.fs.dirserver.attrtree.attrinfo;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import lombok.val;

public class AttrInfoRegistry {

	private Map<String,AttrInfo<Object>> attrs = new HashMap<>();
	
	// ------------------------------------------------------------------------
	
	// empty, for test only
	public AttrInfoRegistry() {
	}
	
	public AttrInfoRegistry(Map<String, AttrInfo<Object>> attrs) {
		this.attrs = new HashMap<>(attrs);
	}

	// ------------------------------------------------------------------------
	
	public AttrInfo<Object> getByName(String name) {
		val res = attrs.get(name);
		if (res == null) {
			throw new IllegalArgumentException("attr not found by name '" + name + "'");
		}
		return res;
	}

	public ImmutableMap<String,AttrInfo<Object>> getAttrs() {
		return ImmutableMap.copyOf(attrs);
	}

}
