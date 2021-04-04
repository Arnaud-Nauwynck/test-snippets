package fr.an.tests.parquetmetadata.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

/**
 * Represents a element inside a schema definition.
 * 
 * - if it is a group (inner node) then type is undefined and num_children is defined 
 * - if it is a primitive type (leaf) then type is defined and num_children is undefined 
 * the nodes are listed in depth first traversal order.
 */
@Data
public class ParquetSchemaElementDTO {

	/** Name of the field in the schema */
	String name;

	/**
	 * Data type for this field. Not set if the current element is a non-leaf node
	 */
	ParquetType type;

	/**
	 * If type is FIXED_LEN_BYTE_ARRAY, this is the byte length of the vales.
	 * Otherwise, if specified, this is the maximum bit length to store any of the
	 * values. (e.g. a low cardinality INT col could have this set to 3). Note that
	 * this is in the schema, and therefore fixed for the entire file.
	 */
	@JsonInclude(Include.NON_NULL)
	Integer type_length;

	/**
	 * repetition of the field. The root of the schema does not have a
	 * repetition_type. All other nodes must have one
	 */
	ParquetFieldRepetitionType repetition_type;

	/**
	 * Nested fields. Since thrift does not support nested fields, the nesting is
	 * flattened to a single list by a depth-first traversal. The children count is
	 * used to construct the nested relationship. This field is not set when the
	 * element is a primitive type
	 */
	@JsonInclude(Include.NON_NULL)
	Integer num_children;

	/**
	 * When the schema is the result of a conversion from another model Used to
	 * record the original type to help with cross conversion.
	 */
	@JsonInclude(Include.NON_NULL)
	ParquetConvertedType converted_type;

	/**
	 * Used when this column contains decimal data. See the DECIMAL converted type
	 * for more details.
	 */
	@JsonInclude(Include.NON_NULL)
	Integer scale;

	@JsonInclude(Include.NON_NULL)
	Integer precision;

	/**
	 * When the original schema supports field ids, this will save the original
	 * field id in the parquet schema
	 */
	@JsonInclude(Include.NON_NULL)
	Integer field_id;

	/**
	 * The logical type of this SchemaElement
	 *
	 * LogicalType replaces ConvertedType, but ConvertedType is still required for
	 * some logical types to ensure forward-compatibility in format v1.
	 */
	@JsonInclude(Include.NON_NULL)
	ParquetLogicalType logicalType;

}
