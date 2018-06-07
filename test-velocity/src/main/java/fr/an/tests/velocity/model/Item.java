package fr.an.tests.velocity.model;

import java.util.Map;

public class Item {

	public final String id;
	public final String type;
	public final Map<String,Object> props;
	
	public Item(String id, String type, Map<String, Object> props) {
		this.id = id;
		this.type = type;
		this.props = props;
	}
	
	public String getId() {
		return this.id;
	}

	public String getType() {
		return this.id;
	}

	public Object getProp(String prop) {
		return props.get(prop);
	}

	public void setProp(String prop, Object value) {
		props.put(prop, value);
	}

}
