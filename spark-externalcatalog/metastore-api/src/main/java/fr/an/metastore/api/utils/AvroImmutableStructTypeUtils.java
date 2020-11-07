package fr.an.metastore.api.utils;

import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.Schema.Type;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMap;

import fr.an.metastore.api.immutable.ImmutableStructType;
import fr.an.metastore.api.immutable.ImmutableStructType.ImmutableDataType;
import fr.an.metastore.api.immutable.ImmutableStructType.ImmutableStructField;
import static fr.an.metastore.api.utils.MetastoreListUtils.map;

public class AvroImmutableStructTypeUtils {

	public static ImmutableStructType fromAvroSchema(Schema schema) {
		List<Field> avroFields = schema.getFields();
		List<ImmutableStructField> structFields = map(avroFields, f -> avroToStructField(f));
		
		ImmutableStructType res = new ImmutableStructType(ImmutableList.copyOf(structFields));
		res.setAsAvroSchema(schema);
		return res;
	}

	private static ImmutableStructField avroToStructField(Field avroField) {
		String name = avroField.name();
		Schema fieldSchema = avroField.schema();
		// check if fieldSchema is Union("null", other)
		Type fieldType = fieldSchema.getType();
		boolean nullable = false;
		Schema fieldTypeUnderlying = fieldSchema; 
		if (fieldType == Type.NULL) {
			nullable = true;
		} else if (fieldType == Type.UNION) {
			List<Schema> unionTypes = fieldSchema.getTypes();
			if (unionTypes.size() == 2) {
				Schema t0 = unionTypes.get(0);
				Schema t1 = unionTypes.get(1);
				if (t0.getType() == Type.NULL) {
					fieldTypeUnderlying = t1;
					nullable = true;
				} else if (t1.getType() == Type.NULL) {
					fieldTypeUnderlying = t0;
					nullable = true;
				}
			}
		}
		
		String typeName = fieldType.getName();
		// TODO
		ImmutableDataType dataType = new ImmutableDataType(typeName);
		
	    ImmutableSortedMap<String,String> data = ImmutableSortedMap.of();
	    if (fieldTypeUnderlying.getType() == Type.ENUM) {
	    	// TODO data = ..
	    }
	    
		return new ImmutableStructField(name, dataType, nullable, data);
	}

}
