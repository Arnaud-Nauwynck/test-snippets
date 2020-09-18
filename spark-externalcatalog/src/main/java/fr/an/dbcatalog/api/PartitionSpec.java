package fr.an.dbcatalog.api;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import fr.an.dbcatalog.spark.util.ScalaCollUtils;
import lombok.Getter;
import lombok.val;

// PartitionSpec
public class PartitionSpec {
	@Getter
	Map<String,String> data;

	public PartitionSpec(Map<String, String> data) {
		this.data = Collections.unmodifiableMap(new TreeMap<>(data));
	}

	public static PartitionSpec fromScalaPartSpec(scala.collection.immutable.Map<String, String> spec) {
		val javaSpec = ScalaCollUtils.mapAsJavaMap(spec);
		return new PartitionSpec(javaSpec);
	}
	
	public boolean isEmpty() {
		return data.isEmpty();
	}

	@Override
	public int hashCode() {
		return data.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PartitionSpec other = (PartitionSpec) obj;
		return data.equals(other.data);
	}

	public String mkString(String sep) {
		return "" + data; // TODO
	}

	
}