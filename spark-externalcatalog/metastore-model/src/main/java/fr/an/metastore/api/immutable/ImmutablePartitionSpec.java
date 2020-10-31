package fr.an.metastore.api.immutable;

import java.util.Map;

import com.google.common.collect.ImmutableSortedMap;

import lombok.Getter;

/**
 * immutable class for partitionSpec
 */
public final class ImmutablePartitionSpec {

	@Getter
	private final ImmutableSortedMap<String,String> data;

	public ImmutablePartitionSpec(Map<String, String> data) {
		this.data = ImmutableSortedMap.copyOf(data);
	}

	// --------------------------------------------------------------------------------------------

	public boolean isEmpty() {
		return data.isEmpty();
	}

	// override java.lang.Object
	// --------------------------------------------------------------------------------------------

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImmutablePartitionSpec other = (ImmutablePartitionSpec) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return data.toString();
	}
	
}
