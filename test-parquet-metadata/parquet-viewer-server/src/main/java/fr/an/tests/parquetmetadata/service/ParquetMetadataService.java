package fr.an.tests.parquetmetadata.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.ParquetReadOptions;
import org.apache.parquet.column.Encoding;
import org.apache.parquet.column.EncodingStats;
import org.apache.parquet.column.statistics.BinaryStatistics;
import org.apache.parquet.column.statistics.BooleanStatistics;
import org.apache.parquet.column.statistics.DoubleStatistics;
import org.apache.parquet.column.statistics.FloatStatistics;
import org.apache.parquet.column.statistics.IntStatistics;
import org.apache.parquet.column.statistics.LongStatistics;
import org.apache.parquet.crypto.InternalFileDecryptor;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.metadata.BlockMetaData;
import org.apache.parquet.hadoop.metadata.ColumnChunkMetaData;
import org.apache.parquet.hadoop.metadata.ColumnPath;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.hadoop.metadata.FileMetaData;
import org.apache.parquet.hadoop.metadata.ParquetMetadata;
import org.apache.parquet.hadoop.util.HadoopInputFile;
import org.apache.parquet.internal.hadoop.metadata.IndexReference;
import org.apache.parquet.io.InputFile;
import org.apache.parquet.schema.GroupType;
import org.apache.parquet.schema.LogicalTypeAnnotation;
import org.apache.parquet.schema.LogicalTypeAnnotation.BsonLogicalTypeAnnotation;
import org.apache.parquet.schema.LogicalTypeAnnotation.DecimalLogicalTypeAnnotation;
import org.apache.parquet.schema.LogicalTypeAnnotation.EnumLogicalTypeAnnotation;
import org.apache.parquet.schema.LogicalTypeAnnotation.IntLogicalTypeAnnotation;
import org.apache.parquet.schema.LogicalTypeAnnotation.IntervalLogicalTypeAnnotation;
import org.apache.parquet.schema.LogicalTypeAnnotation.JsonLogicalTypeAnnotation;
import org.apache.parquet.schema.LogicalTypeAnnotation.ListLogicalTypeAnnotation;
import org.apache.parquet.schema.LogicalTypeAnnotation.LogicalTypeAnnotationVisitor;
import org.apache.parquet.schema.LogicalTypeAnnotation.MapKeyValueTypeAnnotation;
import org.apache.parquet.schema.LogicalTypeAnnotation.MapLogicalTypeAnnotation;
import org.apache.parquet.schema.LogicalTypeAnnotation.StringLogicalTypeAnnotation;
import org.apache.parquet.schema.LogicalTypeAnnotation.TimeLogicalTypeAnnotation;
import org.apache.parquet.schema.LogicalTypeAnnotation.TimeUnit;
import org.apache.parquet.schema.LogicalTypeAnnotation.TimestampLogicalTypeAnnotation;
import org.apache.parquet.schema.LogicalTypeAnnotation.UUIDLogicalTypeAnnotation;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.PrimitiveType;
import org.apache.parquet.schema.PrimitiveType.PrimitiveTypeName;
import org.apache.parquet.schema.Type;
import org.apache.parquet.schema.Type.Repetition;
import org.springframework.stereotype.Service;

import fr.an.tests.parquetmetadata.dto.ParquetColumnChunkDTO;
import fr.an.tests.parquetmetadata.dto.ParquetColumnChunkMetaDataDTO;
import fr.an.tests.parquetmetadata.dto.ParquetEncoding;
import fr.an.tests.parquetmetadata.dto.ParquetFieldRepetitionType;
import fr.an.tests.parquetmetadata.dto.ParquetFileInfoDTO;
import fr.an.tests.parquetmetadata.dto.ParquetLogicalType;
import fr.an.tests.parquetmetadata.dto.ParquetLogicalType.ParquetLogicalTypeEnum;
import fr.an.tests.parquetmetadata.dto.ParquetLogicalType.ParquetTimeUnit;
import fr.an.tests.parquetmetadata.dto.ParquetPageEncodingStatsDTO;
import fr.an.tests.parquetmetadata.dto.ParquetPageType;
import fr.an.tests.parquetmetadata.dto.ParquetRowGroupDTO;
import fr.an.tests.parquetmetadata.dto.ParquetSchemaElementDTO;
import fr.an.tests.parquetmetadata.dto.ParquetSortingColumnDTO;
import fr.an.tests.parquetmetadata.dto.ParquetStatisticsDTO;
import fr.an.tests.parquetmetadata.dto.ParquetStatisticsDTO.BinaryParquetStatisticsDTO;
import fr.an.tests.parquetmetadata.dto.ParquetStatisticsDTO.BooleanParquetStatisticsDTO;
import fr.an.tests.parquetmetadata.dto.ParquetStatisticsDTO.DoubleParquetStatisticsDTO;
import fr.an.tests.parquetmetadata.dto.ParquetStatisticsDTO.FloatParquetStatisticsDTO;
import fr.an.tests.parquetmetadata.dto.ParquetStatisticsDTO.IntParquetStatisticsDTO;
import fr.an.tests.parquetmetadata.dto.ParquetStatisticsDTO.LongParquetStatisticsDTO;
import fr.an.tests.parquetmetadata.dto.ParquetStatisticsDTO.StringParquetStatisticsDTO;
import fr.an.tests.parquetmetadata.dto.ParquetType;
import lombok.val;

@Service
public class ParquetMetadataService {

	public ParquetFileInfoDTO readFileInfo(String file) {
		ParquetFileReader fileReader = createParquetFileReader(file);

		return parquetFileReaderToDTO(fileReader);
	}

	private ParquetFileReader createParquetFileReader(String filePath) {
		Configuration conf = new Configuration();
		File file = new File(filePath);
		if (! file.exists()) {
			String msg = "File not found '" + filePath + "'";
			System.out.println(msg);
			throw new RuntimeException(msg);
		}
		String fileAbsPath = file.getAbsolutePath();
		String fileUrl = "file:///" + fileAbsPath;
		
		Path path = new Path(fileUrl);
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

	private ParquetFileInfoDTO parquetFileReaderToDTO(ParquetFileReader fileReader) {
		ParquetFileInfoDTO res = new ParquetFileInfoDTO();

		FileMetaData fileMetaData = fileReader.getFileMetaData();
		
		fillFileMetadata(res, fileMetaData);
		ParquetMetadata fileFooter = fileReader.getFooter();
		fillFooter(res, fileFooter);
		List<BlockMetaData> rowGroups = fileReader.getRowGroups();


		// TODO


		return res;
	}

	public void fillFileMetadata(ParquetFileInfoDTO res, FileMetaData src) {
		res.setSchema(toSchemaElementDTOList(src.getSchema()));

		res.setKey_value_metadata(new LinkedHashMap<>(src.getKeyValueMetaData()));
		
		res.setCreated_by(src.getCreatedBy());

		InternalFileDecryptor fileDecryptor = src.getFileDecryptor();
		// TODO ..
		// ?? ParquetEncryptionAlgorithmDTO encryption_algorithm;
		// ?? byte[] footer_signing_key_metadata;
		  
		// ?? List<ParquetColumnOrderDTO> column_orders;

	}

	public List<ParquetSchemaElementDTO> toSchemaElementDTOList(MessageType src) {
		List<String[]> pathes = src.getPaths();
		return map(pathes, path -> {
			Type type = src.getType(path);
			String name = StringUtils.join(path, ".");

			Integer num_children = null;
			if (type instanceof GroupType) {
				num_children = ((GroupType) type).getFieldCount();
			}

			ParquetSchemaElementDTO res = toSchemaElementDTO(name, type, num_children);
			return res;
		});
	}

	public ParquetSchemaElementDTO toSchemaElementDTO(String name, Type type, Integer num_children) {
		val res = new ParquetSchemaElementDTO();
		res.setName(name);
		
		// OriginalType srcOriginalType = src.getOriginalType(); // deprecated

		// type_length Not set if the current element is a non-leaf node
		if (type instanceof GroupType) {
			org.apache.parquet.schema.GroupType groupType = (org.apache.parquet.schema.GroupType) type;
			// should not occur..
		} else {
			org.apache.parquet.schema.PrimitiveType primitiveType = (org.apache.parquet.schema.PrimitiveType) type;
			PrimitiveTypeName primitiveTypeName = primitiveType.getPrimitiveTypeName();

			res.setType_length(primitiveType.getTypeLength());
			// res.setConverted_type(primitiveType.get);

		}

		LogicalTypeAnnotation logicalTypeAnnot = type.getLogicalTypeAnnotation();
		if (logicalTypeAnnot != null) {
			logicalTypeAnnot.accept(new LogicalTypeAnnotationVisitor<Void>() {
				@Override
				public Optional<Void> visit(StringLogicalTypeAnnotation stringLogicalType) {
					res.setType(ParquetType.BYTE_ARRAY);
					res.setLogicalType(ParquetLogicalType.StringType.INSTANCE);
					return Optional.empty();
				}
	
				@Override
				public Optional<Void> visit(MapLogicalTypeAnnotation mapLogicalType) {
					// res.setType(null);
					res.setLogicalType(ParquetLogicalType.MapType.INSTANCE);
					return Optional.empty();
				}
	
				@Override
				public Optional<Void> visit(ListLogicalTypeAnnotation listLogicalType) {
					// res.setType(null);
					res.setLogicalType(ParquetLogicalType.ListType.INSTANCE);
					return Optional.empty();
				}
	
				@Override
				public Optional<Void> visit(EnumLogicalTypeAnnotation enumLogicalType) {
					res.setType(ParquetType.INT32); // TOCHECK
					res.setLogicalType(ParquetLogicalType.EnumType.INSTANCE);
					return Optional.empty();
				}
	
				@Override
				public Optional<Void> visit(DecimalLogicalTypeAnnotation decimalLogicalType) {
					int scale = decimalLogicalType.getScale();
					int precision = decimalLogicalType.getPrecision();
					res.setType(ParquetType.FIXED_LEN_BYTE_ARRAY); //  : ParquetType.DOUBLE); // TOCHECK
					res.setLogicalType(new ParquetLogicalType.DecimalType(scale, precision));
					return Optional.empty();
				}
	
				@Override
				public Optional<Void> visit(TimeLogicalTypeAnnotation timeLogicalType) {
					TimeUnit parquetTimeUnit = timeLogicalType.getUnit();
					boolean isAdjustedToUTC = timeLogicalType.isAdjustedToUTC();
					ParquetLogicalType.ParquetTimeUnit unit;
					ParquetLogicalTypeEnum typeEnum;
					switch(parquetTimeUnit) {
					case NANOS: unit = ParquetTimeUnit.NANOS; typeEnum = ParquetLogicalTypeEnum.TIME_NANOS; break;
					case MICROS: unit = ParquetTimeUnit.MICROS; typeEnum = ParquetLogicalTypeEnum.TIME_MICROS; break;
					case MILLIS: unit = ParquetTimeUnit.MILLIS; typeEnum = ParquetLogicalTypeEnum.TIME_MILLIS; break;
					default: unit = ParquetTimeUnit.MILLIS; typeEnum = ParquetLogicalTypeEnum.TIME_MILLIS; break;
					}
					res.setType(ParquetType.INT64); // TOCHECK
					res.setLogicalType(new ParquetLogicalType.TimeParquetLogicalType(typeEnum, isAdjustedToUTC, unit));
					return Optional.empty();
				}
	
				@Override
				public Optional<Void> visit(TimestampLogicalTypeAnnotation timestampLogicalType) {
					TimeUnit parquetTimeUnit = timestampLogicalType.getUnit();
					boolean isAdjustedToUTC = timestampLogicalType.isAdjustedToUTC();
					ParquetLogicalType.ParquetTimeUnit unit;
					ParquetLogicalTypeEnum typeEnum;
					switch(parquetTimeUnit) {
					case NANOS: unit = ParquetTimeUnit.NANOS; typeEnum = ParquetLogicalTypeEnum.TIMESTAMP_NANOS; break;
					case MICROS: unit = ParquetTimeUnit.MICROS; typeEnum = ParquetLogicalTypeEnum.TIMESTAMP_MICROS; break;
					case MILLIS: unit = ParquetTimeUnit.MILLIS; typeEnum = ParquetLogicalTypeEnum.TIMESTAMP_MILLIS; break;
					default: unit = ParquetTimeUnit.MILLIS; typeEnum = ParquetLogicalTypeEnum.TIMESTAMP_MILLIS; break;
					}
					res.setType(ParquetType.INT64); // TOCHECK
					res.setLogicalType(new ParquetLogicalType.TimestampParquetLogicalType(typeEnum, isAdjustedToUTC, unit));
					return Optional.empty();
				}
	
				@Override
				public Optional<Void> visit(IntLogicalTypeAnnotation intLogicalType) {
					int bitWidth = intLogicalType.getBitWidth();
					boolean isSigned = intLogicalType.isSigned();
					res.setType(ParquetType.INT32);
					res.setLogicalType(new ParquetLogicalType.IntType(bitWidth, isSigned));
					return Optional.empty();
				}
	
				@Override
				public Optional<Void> visit(JsonLogicalTypeAnnotation jsonLogicalType) {
					res.setType(ParquetType.BYTE_ARRAY);
					res.setLogicalType(new ParquetLogicalType.JsonType());
					return Optional.empty();
				}
	
				@Override
				public Optional<Void> visit(BsonLogicalTypeAnnotation bsonLogicalType) {
					res.setType(ParquetType.BYTE_ARRAY);
					res.setLogicalType(new ParquetLogicalType.BsonType());
					return Optional.empty();
				}
	
				@Override
				public Optional<Void> visit(UUIDLogicalTypeAnnotation uuidLogicalType) {
					res.setType(ParquetType.FIXED_LEN_BYTE_ARRAY);
					res.setLogicalType(new ParquetLogicalType.UUIDType());
					return Optional.empty();
				}
	
				@Override
				public Optional<Void> visit(IntervalLogicalTypeAnnotation intervalLogicalType) {
					res.setType(ParquetType.FIXED_LEN_BYTE_ARRAY);
					// TODO res.setLogicalType(new ParquetLogicalType.IntervalLogicalType());
					return Optional.empty();
				}
	
				@Override
				public Optional<Void> visit(MapKeyValueTypeAnnotation mapKeyValueLogicalType) {
					res.setType(ParquetType.BYTE_ARRAY);
					res.setLogicalType(new ParquetLogicalType.MapType()); // TODO MapType.INSTANCE != MapKeyValue ?
					return Optional.empty();
				}
	
			});
		} else {
			// no logicalTypeAnnotation... primitive?
			if (type.isPrimitive()) {
				PrimitiveType primitive = type.asPrimitiveType();
				PrimitiveTypeName primitiveTypeName = primitive.getPrimitiveTypeName();
				switch(primitiveTypeName) {
				case BINARY: 
					res.setType(ParquetType.BYTE_ARRAY);
					break;
				case BOOLEAN: 
					res.setType(ParquetType.BOOLEAN);
					break;
				case DOUBLE: 
					res.setType(ParquetType.DOUBLE);
					break;
				case FLOAT: 
					res.setType(ParquetType.FLOAT);
					break;
				case INT32: 
					res.setType(ParquetType.INT32);
					break;
				case INT64: 
					res.setType(ParquetType.INT64);
					break;
				case INT96: 
					res.setType(ParquetType.INT96);
					break;
				case FIXED_LEN_BYTE_ARRAY: 
					res.setType(ParquetType.FIXED_LEN_BYTE_ARRAY);
					break;
				}
				
			} else {
				// should not occur?
			}
			
		}

		Repetition repetition = type.getRepetition();
		res.setRepetition_type(repetition != null ? ParquetFieldRepetitionType.valueOf(repetition.name()) : null);

		org.apache.parquet.schema.Type.ID fieldId = type.getId();
		res.setField_id(fieldId != null? fieldId.intValue() : null);
		
		res.setNum_children(num_children);

		return res;
	}

	public void fillFooter(ParquetFileInfoDTO res, ParquetMetadata src) {
		List<BlockMetaData> blocks = src.getBlocks();
		MessageType schema = src.getFileMetaData().getSchema();
		List<ParquetRowGroupDTO> row_groups = map(blocks, b -> toParquetRowGroupDTO(b, schema));
		res.setRow_groups(row_groups);
	}


	
	private ParquetRowGroupDTO toParquetRowGroupDTO(BlockMetaData src, MessageType schema) {
		ParquetRowGroupDTO res = new ParquetRowGroupDTO();
		res.setNum_rows(src.getRowCount());
		res.setTotal_byte_size(src.getTotalByteSize());
		res.setOrdinal(src.getOrdinal());
		// TODO String path

		List<ParquetColumnChunkDTO> columns = map(src.getColumns(), c -> {
			Type type = schema.getType(c.getPath().toArray());
			return toColumnChunkDTO(c, type);
		});
		res.setColumns(columns);

		// If set, specifies a sort ordering of the rows in this RowGroup. The sorting columns can be a subset of all the columns.
		List<ParquetSortingColumnDTO> sorting_columns = null; // TODO
		res.setSorting_columns(sorting_columns);
		
		// Byte offset from beginning of file to first page (data or dictionary) in this row group
		Long file_offset = null; // TODO 
		res.setFile_offset(file_offset);
		
		// Total byte size of all compressed (and potentially encrypted) column data in this row group **/
		long total_compressed_size = 0;
		for(val c: src.getColumns()) {
			total_compressed_size += c.getTotalSize(); // TOCHECK
		}
		res.setTotal_compressed_size(total_compressed_size);

		  
		return res;
	}

	private ParquetColumnChunkDTO toColumnChunkDTO(
			ColumnChunkMetaData src,
			Type column) {
		ParquetColumnChunkDTO res = new ParquetColumnChunkDTO();
		
		res.setColName(column.getName());
		res.setFile_path(null);

		/** Byte offset in file_path to the ColumnMetaData **/
		res.setFile_offset(src.getFirstDataPageOffset()); // TOCHECK

		/** Column metadata for this chunk. This is the same content as what is at
		 * file_path/file_offset.  Having it here has it replicated in the file
		 * metadata.
		 **/
		ParquetColumnChunkMetaDataDTO meta_data = toColumnChunkMetadataDTO(src, column);
		res.setMeta_data(meta_data);
		
		IndexReference offsetIndexRef = src.getOffsetIndexReference();
		if (offsetIndexRef != null) {
			res.setOffset_index_offset(offsetIndexRef.getOffset());
			res.setOffset_index_length(offsetIndexRef.getLength());
		}

		IndexReference columnIndexReference = src.getColumnIndexReference();
		if (columnIndexReference != null) {
			res.setColumn_index_offset(columnIndexReference.getOffset());
			res.setColumn_index_length(columnIndexReference.getLength());
		}

//		// TODO
//		if (src instanceof EncryptedColumnChunkMetaData) { //  ... package protected!
//			EncryptedColumnChunkMetaData srcEncrypt = (EncryptedColumnChunkMetaData) src;
//			
//			ParquetColumnCryptoMetaDataDTO crypto_metadata = null;
//			res.setCrypto_metadata(crypto_metadata);
//			
//			byte[] encrypted_column_metadata = null;
//			res.setEncrypted_column_metadata(src.getencrypted_column_metadata);
//		}
		
		return res;
	}

	private ParquetColumnChunkMetaDataDTO toColumnChunkMetadataDTO(ColumnChunkMetaData src, Type type) {
		ParquetColumnChunkMetaDataDTO res = new ParquetColumnChunkMetaDataDTO();
		
		EncodingStats srcEncodingStats = src.getEncodingStats();
		// ColumnChunkProperties srcProperties = codec, path, type, encoding ...
		CompressionCodecName srcCodec = src.getCodec();
		ColumnPath srcPath = src.getPath();
		PrimitiveTypeName srcPrimitiveType = src.getType();
		Set<Encoding> srcEncodings = src.getEncodings();

		IndexReference srcColumnIndexReference = src.getColumnIndexReference();
		IndexReference srcOffsetIndexReference = src.getOffsetIndexReference();

		long srcBloomFilterOffset = src.getBloomFilterOffset();

		/** Number of values in this column **/
		long num_values;
		
		res.setTotal_uncompressed_size(src.getTotalUncompressedSize());
		res.setTotal_compressed_size(src.getTotalSize());

		/** Optional key/value metadata **/
		Map<String,String> key_value_metadata;
		
		/** Byte offset from beginning of file to first data page **/
		Long data_page_offset;

		/** Byte offset from beginning of file to root index page **/
		Long index_page_offset;

		/** Byte offset from the beginning of file to first (only) dictionary page **/
		Long dictionary_page_offset;

		// TOCHECK ... only 3 sub-classes for columnChunk: Int,Long,Encrypted?
		ParquetStatisticsDTO<?> statistics = toStatisticsDTO(src.getStatistics(), srcPrimitiveType, type);
		res.setStatistics(statistics);
		
		if (srcEncodingStats != null) {
			res.setEncoding_stats(toEncodingStatsDTO(srcEncodingStats));
		}
		
		res.setBloom_filter_offset(srcBloomFilterOffset);

		return res;
	}


	private <T extends Comparable<T>> ParquetStatisticsDTO<T> toStatisticsDTO(
			org.apache.parquet.column.statistics.Statistics<T> src,
			PrimitiveTypeName primitiveType,
			Type type) {
		ParquetStatisticsDTO<?> res;
		long null_count = src.getNumNulls();
		Long distinct_count = null; // TODO ... not in java clas??
		
		if (src instanceof BinaryStatistics) {
			BinaryStatistics src2 = (BinaryStatistics) src;
			boolean isString = type.getLogicalTypeAnnotation().accept(new LogicalTypeAnnotationVisitor<Boolean>() {
				@Override
				public Optional<Boolean> visit(StringLogicalTypeAnnotation stringLogicalType) {
					return Optional.of(true);
				}
				@Override
				public Optional<Boolean> visit(EnumLogicalTypeAnnotation enumLogicalType) {
					return Optional.of(true);
				}
				@Override
				public Optional<Boolean> visit(JsonLogicalTypeAnnotation jsonLogicalType) {
					return Optional.of(true);
				}
			}).orElse(false);
			if (isString) {
				String min_value = src2.minAsString();
				String max_value = src2.maxAsString();
				res = new StringParquetStatisticsDTO(null_count, distinct_count, min_value, max_value);
			} else {
				byte[] min_value = src2.getMinBytes();
				byte[] max_value = src2.getMaxBytes();
				res = new BinaryParquetStatisticsDTO(null_count, distinct_count, min_value, max_value);
			}
		} else if (src instanceof BooleanStatistics) {
			BooleanStatistics src2 = (BooleanStatistics) src;
			boolean min_value = src2.getMin();
			boolean max_value = src2.getMax();
			res = new BooleanParquetStatisticsDTO(null_count, distinct_count, min_value, max_value);
		} else if (src instanceof DoubleStatistics) {
			DoubleStatistics src2 = (DoubleStatistics) src;
			double min_value = src2.getMin();
			double max_value = src2.getMax();
			res = new DoubleParquetStatisticsDTO(null_count, distinct_count, min_value, max_value);
		} else if (src instanceof FloatStatistics) {
			FloatStatistics src2 = (FloatStatistics) src;
			float min_value = src2.getMin();
			float max_value = src2.getMax();
			res = new FloatParquetStatisticsDTO(null_count, distinct_count, min_value, max_value);
		} else if (src instanceof IntStatistics) {
			IntStatistics src2 = (IntStatistics) src;
			int min_value = src2.getMin();
			int max_value = src2.getMax();
			res = new IntParquetStatisticsDTO(null_count, distinct_count, min_value, max_value);
		} else if (src instanceof LongStatistics) {
			LongStatistics src2 = (LongStatistics) src;
			long min_value = src2.getMin();
			long max_value = src2.getMax();
			res = new LongParquetStatisticsDTO(null_count, distinct_count, min_value, max_value);
			
		} else {
			System.out.println("should not occur: Statistics subtypes only Binary,Boolean,Double,Float,Int,Long");
			res = null;
		}
		
		@SuppressWarnings("unchecked")
		ParquetStatisticsDTO<T> res2 = (ParquetStatisticsDTO<T>) res;
		return res2;
	}

	private List<ParquetPageEncodingStatsDTO> toEncodingStatsDTO(EncodingStats src) {
		List<ParquetPageEncodingStatsDTO> res = new ArrayList<>();
		for(val e : src.getDataEncodings()) {
			int num = src.getNumDataPagesEncodedAs(e);
			ParquetEncoding parquetEncoding = ParquetEncoding.valueOf(e.name());
			res.add(new ParquetPageEncodingStatsDTO(ParquetPageType.DATA_PAGE, parquetEncoding, num));
		}
		for(val e : src.getDictionaryEncodings()) {
			int num = src.getNumDictionaryPagesEncodedAs(e);
			ParquetEncoding parquetEncoding = ParquetEncoding.valueOf(e.name());
			res.add(new ParquetPageEncodingStatsDTO(ParquetPageType.DICTIONARY_PAGE, parquetEncoding, num));
		}
		return res;
	}

	private static <TSrc, TDest> List<TDest> map(Collection<TSrc> src, Function<TSrc, TDest> func) {
		return src.stream().map(func).collect(Collectors.toList());
	}

}
