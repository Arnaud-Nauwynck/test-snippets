package fr.an.tests.parquetmetadata.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import fr.an.tests.parquetmetadata.dto.parquet.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.parquet.column.EncodingStats;
import org.apache.parquet.column.statistics.BinaryStatistics;
import org.apache.parquet.column.statistics.BooleanStatistics;
import org.apache.parquet.column.statistics.DoubleStatistics;
import org.apache.parquet.column.statistics.FloatStatistics;
import org.apache.parquet.column.statistics.IntStatistics;
import org.apache.parquet.column.statistics.LongStatistics;
import org.apache.parquet.crypto.InternalFileDecryptor;
import org.apache.parquet.schema.Type;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.metadata.BlockMetaData;
import org.apache.parquet.hadoop.metadata.ColumnChunkMetaData;
import org.apache.parquet.hadoop.metadata.ColumnPath;
import org.apache.parquet.hadoop.metadata.FileMetaData;
import org.apache.parquet.hadoop.metadata.ParquetMetadata;
import org.apache.parquet.internal.hadoop.metadata.IndexReference;
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
import org.apache.parquet.schema.Type.Repetition;
import org.springframework.stereotype.Service;

import fr.an.tests.parquetmetadata.dto.parquet.ParquetLogicalType.ParquetLogicalTypeEnum;
import fr.an.tests.parquetmetadata.dto.parquet.ParquetLogicalType.ParquetTimeUnit;
import fr.an.tests.parquetmetadata.dto.parquet.ParquetStatisticsDTO.BinaryParquetStatisticsDTO;
import fr.an.tests.parquetmetadata.dto.parquet.ParquetStatisticsDTO.BooleanParquetStatisticsDTO;
import fr.an.tests.parquetmetadata.dto.parquet.ParquetStatisticsDTO.DoubleParquetStatisticsDTO;
import fr.an.tests.parquetmetadata.dto.parquet.ParquetStatisticsDTO.FloatParquetStatisticsDTO;
import fr.an.tests.parquetmetadata.dto.parquet.ParquetStatisticsDTO.IntParquetStatisticsDTO;
import fr.an.tests.parquetmetadata.dto.parquet.ParquetStatisticsDTO.LongParquetStatisticsDTO;
import fr.an.tests.parquetmetadata.dto.parquet.ParquetStatisticsDTO.StringParquetStatisticsDTO;
import fr.an.tests.parquetmetadata.util.LsUtils;
import lombok.val;

@Service
public class ParquetDTOConverter {

	public ParquetFileInfoDTO parquetFileReaderToDTO(ParquetFileReader fileReader) {
		ParquetFileInfoDTO res = new ParquetFileInfoDTO();

		FileMetaData fileMetaData = fileReader.getFileMetaData();
		fillFileMetadata(res, fileMetaData);
		
		ParquetMetadata footer = fileReader.getFooter();
		List<BlockMetaData> blocks = footer.getBlocks();
		MessageType schema = fileMetaData.getSchema();
		List<ParquetBlockMetadataDTO> blockMetadataDtos = LsUtils.map(blocks, b -> toBlockMetadataDTO(b, schema));
		res.setBlocks(blockMetadataDtos);


		// TODO


		return res;
	}

	public void fillFileMetadata(ParquetFileInfoDTO res, FileMetaData src) {
		res.setSchema(toSchemaElementDTOList(src.getSchema()));

		res.setKeyValueMetadata(new LinkedHashMap<>(src.getKeyValueMetaData()));
		
		res.setCreatedBy(src.getCreatedBy());

		InternalFileDecryptor fileDecryptor = src.getFileDecryptor();
		// TODO ..
		// ?? ParquetEncryptionAlgorithmDTO encryptionAlgorithm;
		// ?? byte[] footerSigningKeyMetadata;
		  
		// ?? List<ParquetColumnOrderDTO> columnOrders;

	}

	public List<ParquetSchemaElementDTO> toSchemaElementDTOList(MessageType src) {
		List<String[]> pathes = src.getPaths();
		return LsUtils.map(pathes, path -> {
			Type type = src.getType(path);
			String name = StringUtils.join(path, ".");

			Integer numChildren = null;
			if (type instanceof GroupType) {
				numChildren = ((GroupType) type).getFieldCount();
			}

			ParquetSchemaElementDTO res = toSchemaElementDTO(name, type, numChildren);
			return res;
		});
	}

	public ParquetSchemaElementDTO toSchemaElementDTO(String name, Type type, Integer numChildren) {
		val res = new ParquetSchemaElementDTO();
		res.setName(name);
		
		// OriginalType srcOriginalType = src.getOriginalType(); // deprecated

		// typeLength Not set if the current element is a non-leaf node
		if (type instanceof GroupType) {
			org.apache.parquet.schema.GroupType groupType = (org.apache.parquet.schema.GroupType) type;
			// should not occur..
		} else {
			org.apache.parquet.schema.PrimitiveType primitiveType = (org.apache.parquet.schema.PrimitiveType) type;
			PrimitiveTypeName primitiveTypeName = primitiveType.getPrimitiveTypeName();

			res.setTypeLength(primitiveType.getTypeLength());
			// res.setConvertedType(primitiveType.get);

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
		res.setRepetitionType(repetition != null ? ParquetFieldRepetitionType.valueOf(repetition.name()) : null);

		org.apache.parquet.schema.Type.ID fieldId = type.getId();
		res.setFieldId(fieldId != null? fieldId.intValue() : null);
		
		res.setNumChildren(numChildren);

		return res;
	}


	private ParquetBlockMetadataDTO toBlockMetadataDTO(BlockMetaData src, MessageType schema) {
		ParquetBlockMetadataDTO res = new ParquetBlockMetadataDTO();

		List<ParquetColumnChunkMetaDataDTO> colChunkMetadataDtos = LsUtils.map(src.getColumns(), c -> {
			ColumnPath colPath = c.getPath();
			String[] colPathArray = colPath.toArray();
			String colFqn = String.join(".", Arrays.asList(colPathArray));
			Type colType = schema.getType(colPathArray);
			return toColumnChunkMetaDataDTO(c, colFqn, colType);
		});
		res.setColumns(colChunkMetadataDtos);

		res.setRowCount(src.getRowCount());
		res.setTotalByteSize(src.getTotalByteSize());
		res.setPath(src.getPath());
		res.setOrdinal(src.getOrdinal());
		res.setRowIndexOffset(src.getRowIndexOffset());
		return res;
	}

	private ParquetColumnChunkMetaDataDTO toColumnChunkMetaDataDTO(
			ColumnChunkMetaData src,
			String colFqn,
			Type colType) {
		ParquetColumnChunkMetaDataDTO res = new ParquetColumnChunkMetaDataDTO();
		res.setRowGroupOrdinal(src.getRowGroupOrdinal());
		res.setEncodingStats(toEncodingStatDTO(src.getEncodingStats()));
		// TODO res.setProperties(toColumnChunkPropertiesDTO(src.getProperties()));

		res.setColumnIndexReference(toIndexReferenceDTO(src.getColumnIndexReference()));
		res.setOffsetIndexReference(toIndexReferenceDTO(src.getOffsetIndexReference()));
		res.setBloomFilterOffset(src.getBloomFilterOffset());

		// field used in sub-class IntColumnChunkMetaData and LongColumnChunkMetaData
		// cf also EncryptedColumnChunkMetaData
		res.setFirstDataPageOffset(src.getFirstDataPageOffset());
		res.setDictionaryPageOffset(src.getDictionaryPageOffset());
		res.setValueCount(src.getValueCount());
		res.setTotalSize(src.getTotalSize());
		res.setTotalUncompressedSize(src.getTotalUncompressedSize());

		res.setStatistics(toStatisticsDTO(src.getStatistics(), colType));

//		// TODO
//		if (src instanceof EncryptedColumnChunkMetaData) { //  ... package protected!
//			EncryptedColumnChunkMetaData srcEncrypt = (EncryptedColumnChunkMetaData) src;
//			
//			ParquetColumnCryptoMetaDataDTO cryptoMetadata = null;
//			res.setCryptoMetadata(cryptoMetadata);
//			
//			byte[] encryptedColumnMetadata = null;
//			res.setEncryptedColumnMetadata(src.getencryptedColumnMetadata);
//		}
		
		return res;
	}

	private ParquetIndexReferenceDTO toIndexReferenceDTO(IndexReference src) {
		if (src == null) {
			return null;
		}
		return new ParquetIndexReferenceDTO(src.getOffset(), src.getLength());
	}

	private ParquetEncodingStatsDTO toEncodingStatDTO(EncodingStats src) {
		if (src == null) {
			return null;
		}
		Map</*Encoding*/String, Number> dictStats = new LinkedHashMap<>();
		for(val enc : src.getDictionaryEncodings()) {
			dictStats.put(enc.name(), src.getNumDictionaryPagesEncodedAs(enc));
		}
		Map</*Encoding*/String, Number> dataStats = new LinkedHashMap<>();
		for(val enc : src.getDataEncodings()) {
			dictStats.put(enc.name(), src.getNumDataPagesEncodedAs(enc));
		}
		boolean usesV2Pages = src.usesV2Pages();
		return  new ParquetEncodingStatsDTO(dictStats, dataStats, usesV2Pages);
	}


	private <T extends Comparable<T>> ParquetStatisticsDTO<T> toStatisticsDTO(
			org.apache.parquet.column.statistics.Statistics<T> src,
			Type type
	) {
		ParquetStatisticsDTO<?> res;
		long nullCount = src.getNumNulls();
		Long distinctCount = null; // TODO ... not in java clas??
		
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
				String minValue = src2.minAsString();
				String maxValue = src2.maxAsString();
				res = new StringParquetStatisticsDTO(nullCount, distinctCount, minValue, maxValue);
			} else {
				byte[] minValue = src2.getMinBytes();
				byte[] maxValue = src2.getMaxBytes();
				res = new BinaryParquetStatisticsDTO(nullCount, distinctCount, minValue, maxValue);
			}
		} else if (src instanceof BooleanStatistics) {
			BooleanStatistics src2 = (BooleanStatistics) src;
			boolean minValue = src2.getMin();
			boolean maxValue = src2.getMax();
			res = new BooleanParquetStatisticsDTO(nullCount, distinctCount, minValue, maxValue);
		} else if (src instanceof DoubleStatistics) {
			DoubleStatistics src2 = (DoubleStatistics) src;
			double minValue = src2.getMin();
			double maxValue = src2.getMax();
			res = new DoubleParquetStatisticsDTO(nullCount, distinctCount, minValue, maxValue);
		} else if (src instanceof FloatStatistics) {
			FloatStatistics src2 = (FloatStatistics) src;
			float minValue = src2.getMin();
			float maxValue = src2.getMax();
			res = new FloatParquetStatisticsDTO(nullCount, distinctCount, minValue, maxValue);
		} else if (src instanceof IntStatistics) {
			IntStatistics src2 = (IntStatistics) src;
			int minValue = src2.getMin();
			int maxValue = src2.getMax();
			res = new IntParquetStatisticsDTO(nullCount, distinctCount, minValue, maxValue);
		} else if (src instanceof LongStatistics) {
			LongStatistics src2 = (LongStatistics) src;
			long minValue = src2.getMin();
			long maxValue = src2.getMax();
			res = new LongParquetStatisticsDTO(nullCount, distinctCount, minValue, maxValue);
			
		} else {
			System.out.println("should not occur: Statistics subtypes only Binary,Boolean,Double,Float,Int,Long");
			res = null;
		}
		
		@SuppressWarnings("unchecked")
		ParquetStatisticsDTO<T> res2 = (ParquetStatisticsDTO<T>) res;
		return res2;
	}

}
