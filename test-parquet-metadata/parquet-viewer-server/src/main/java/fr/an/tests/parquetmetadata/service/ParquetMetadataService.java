package fr.an.tests.parquetmetadata.service;


import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.lang.model.type.PrimitiveType;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.ParquetReadOptions;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.metadata.BlockMetaData;
import org.apache.parquet.hadoop.metadata.FileMetaData;
import org.apache.parquet.hadoop.metadata.ParquetMetadata;
import org.apache.parquet.hadoop.util.HadoopInputFile;
import org.apache.parquet.io.InputFile;
import org.apache.parquet.schema.GroupType;
import org.apache.parquet.schema.LogicalTypeAnnotation;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.OriginalType;
import org.apache.parquet.schema.Type;
import org.apache.parquet.schema.PrimitiveType.PrimitiveTypeName;
import org.apache.parquet.schema.Type.ID;
import org.apache.parquet.schema.Type.Repetition;
import org.springframework.stereotype.Service;

import fr.an.tests.parquetmetadata.dto.ParquetConvertedType;
import fr.an.tests.parquetmetadata.dto.ParquetFieldRepetitionType;
import fr.an.tests.parquetmetadata.dto.ParquetFileMetadataDTO;
import fr.an.tests.parquetmetadata.dto.ParquetKeyValueDTO;
import fr.an.tests.parquetmetadata.dto.ParquetLogicalType;
import fr.an.tests.parquetmetadata.dto.ParquetRowGroupDTO;
import fr.an.tests.parquetmetadata.dto.ParquetSchemaElementDTO;
import fr.an.tests.parquetmetadata.dto.ParquetType;
import lombok.val;

@Service
public class ParquetMetadataService {

	public ParquetFileMetadataDTO readFileMetadata(String file) {
		ParquetFileReader fileReader = createParquetFileReader(file);

		return parquetFileReaderToDTO(fileReader);
	}


	private ParquetFileReader createParquetFileReader(String file) {
		Configuration conf = new Configuration(); 
		// File uri = new File(file).toURI().toURL().toString();
		Path path = new Path(file);
		InputFile intputFile;
		try {
			intputFile = HadoopInputFile.fromPath(path, conf);
		} catch (IOException ex) {
			throw new RuntimeException("", ex);
		}
		ParquetReadOptions readOptions = ParquetReadOptions.builder().build();
		
		ParquetFileReader fileReader;
		try {
			fileReader = ParquetFileReader.open(intputFile, readOptions);
		} catch (IOException ex) {
			throw new RuntimeException("", ex);
		}
		return fileReader;
	}


	private ParquetFileMetadataDTO parquetFileReaderToDTO(ParquetFileReader fileReader) {
		ParquetFileMetadataDTO res = new ParquetFileMetadataDTO();
		
		FileMetaData fileMetaData = fileReader.getFileMetaData();
		ParquetMetadata fileFooter = fileReader.getFooter();
		List<BlockMetaData> rowGroups = fileReader.getRowGroups();

		// TODO
		
//		FileMetaData
//		  private final MessageType schema;
//		  private final Map<String, String> keyValueMetaData;
//		  private final String createdBy;
//		  private final InternalFileDecryptor fileDecryptor;
		
		return res;
	}
	
	public void toDTO(ParquetFileMetadataDTO res, FileMetaData src) {
		  // TODO? res.setVersion(src.);

		  /** Parquet schema for this file.  This schema contains metadata for all the columns.
		   * The schema is represented as a tree with a single root.  The nodes of the tree
		   * are flattened to a list by doing a depth-first traversal.
		   * The column metadata contains the path in the schema for that column which can be
		   * used to map columns to nodes in the schema.
		   * The first element is the root **/
		  res.setSchema(toSchemaElementDTOList(src.getSchema());

		  // .?? res.setNum_rows(src.get)

		  List<ParquetRowGroupDTO> row_groups;

		  List<ParquetKeyValueDTO> key_value_metadata;

		  res.setCreated_by(src.getCreatedBy());

		  //?? List<ParquetColumnOrderDTO> column_orders;

		  // ?? ParquetEncryptionAlgorithmDTO encryption_algorithm;

		  // ?? byte[] footer_signing_key_metadata;
	}
	
	public List<ParquetSchemaElementDTO> toSchemaElementDTOList(MessageType src) {
		List<String[]> pathes = src.getPaths();
		return map(pathes, path -> {
			Type type = src.getType(path);
			String name = StringUtils.join(path, ".");
			return toSchemaElementDTO(name, type);
		});
	}
	
	public ParquetSchemaElementDTO toSchemaElementDTO(String name, Type src) {
		val res = new ParquetSchemaElementDTO();
		// OriginalType srcOriginalType = src.getOriginalType(); // deprecated

		// Not set if the current element is a non-leaf node
		if (src instanceof GroupType) {
			org.apache.parquet.schema.GroupType groupType = (org.apache.parquet.schema.GroupType) src;
		} else {
			org.apache.parquet.schema.PrimitiveType primitiveType = (org.apache.parquet.schema.PrimitiveType) src;
			PrimitiveTypeName primeTypeName = primitiveType.getPrimitiveTypeName();
			
			res.setType_length(primitiveType.getTypeLength());

		}

		Repetition repetition = src.getRepetition();
		res.setRepetition_type(repetition != null? ParquetFieldRepetitionType.valueOf(repetition.name()) : null);

		/** Name of the field in the schema */
		String name;

		/**
		 * Nested fields. Since thrift does not support nested fields, the nesting is
		 * flattened to a single list by a depth-first traversal. The children count is
		 * used to construct the nested relationship. This field is not set when the
		 * element is a primitive type
		 */
		Integer num_children;

		/**
		 * When the schema is the result of a conversion from another model Used to
		 * record the original type to help with cross conversion.
		 */
		ParquetConvertedType converted_type;

		/**
		 * Used when this column contains decimal data. See the DECIMAL converted type
		 * for more details.
		 */
		Integer scale;
		Integer precision;

		/**
		 * When the original schema supports field ids, this will save the original
		 * field id in the parquet schema
		 */
		Integer field_id;

		/**
		 * The logical type of this SchemaElement
		 *
		 * LogicalType replaces ConvertedType, but ConvertedType is still required for
		 * some logical types to ensure forward-compatibility in format v1.
		 */
		ParquetLogicalType logicalType;

		
		private final String name;
		  private final Repetition repetition;
		  private final LogicalTypeAnnotation logicalTypeAnnotation;
		  private final ID id;
		  return res;
	}
	
	private static <TSrc,TDest> List<TDest> map(Collection<TSrc> src, Function<TSrc,TDest> func) {
		return src.stream().map(func).collect(Collectors.toList());
	}

}
