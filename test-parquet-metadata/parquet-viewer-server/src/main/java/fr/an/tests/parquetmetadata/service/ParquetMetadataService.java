package fr.an.tests.parquetmetadata.service;

import com.google.common.collect.ImmutableList;
import fr.an.tests.parquetmetadata.dto.ParquetDataFileInfoDTO;
import fr.an.tests.parquetmetadata.dto.ScanDirFileMetadatasResultDTO;
import fr.an.tests.parquetmetadata.dto.ScanDirFileMetadatasResultDTO.PartitionAndFileDataInfoDTO;
import fr.an.tests.parquetmetadata.dto.ScanDirFileMetadatasResultDTO.PartitionScanStatisticsDTO;
import fr.an.tests.parquetmetadata.dto.parquet.ParquetFileInfoDTO;
import fr.an.tests.parquetmetadata.util.LsUtils;
import lombok.AllArgsConstructor;
import lombok.val;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.ParquetReadOptions;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.util.HadoopInputFile;
import org.apache.parquet.io.InputFile;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ParquetMetadataService {

	ParquetDTOConverter dtoConverter;
	
	Configuration hadoopConf = new Configuration();
	FileSystem hadoopFs;
	FileSystem hadoopLocalFs;
	
//	ExecutorService executorService = Executors.newFixedThreadPool(4);
	
	// ------------------------------------------------------------------------

	public ParquetMetadataService(ParquetDTOConverter dtoConverter) {
		this.dtoConverter = dtoConverter;
		try {
			hadoopFs = FileSystem.get(hadoopConf);
			hadoopLocalFs = FileSystem.get(stringToURI("file:///local"), hadoopConf);
		} catch(IOException ex) {
			throw new RuntimeException("Failed", ex);
		}
	}

	// ------------------------------------------------------------------------
	
	public ParquetFileInfoDTO readFileInfo(String file) {
		Path path = pathOrRelativeToHadoopPath(file);
		return readParquetInfoAndConvertToDTO(path);
	}

	private ParquetFileInfoDTO readParquetInfoAndConvertToDTO(Path path) {
		ParquetFileReader fileReader = openParquetFileReader(path);
		try {
			return dtoConverter.parquetFileReaderToDTO(fileReader);
		} finally {
			closeQuietly(fileReader);
		}
	}


	public ScanDirFileMetadatasResultDTO scanDirFileMetadata(String baseDir) {
		ScanDirFileMetadatasResultDTO res = new ScanDirFileMetadatasResultDTO();
		Path basePath = pathOrRelativeToHadoopPath(baseDir);
		
		// TOOPTIM parallel scan 
		List<PartitionAndFile> resFiles = new ArrayList<>();
		List<PartitionScanStatisticsDTO> partitionScanStatistics = new ArrayList<>();
		try {
			recursiveScanPartitionFiles(resFiles , ImmutableList.of(), partitionScanStatistics, basePath);
		} catch (Exception e) {
			throw new RuntimeException("Failed to scan dir", e);
		}
		res.setPartitionScanStatistics(partitionScanStatistics);
		
		List<PartitionAndFileDataInfoDTO> partFileInfos = new ArrayList<>();
		// only take first file schema (no merge)
		if (!resFiles.isEmpty()) {
			PartitionAndFile resFile0 = resFiles.remove(0); // remove and parse first file..
			Path file0Path = resFile0.fileStatus.getPath();
			ParquetFileInfoDTO fileInfo0 = readParquetInfoAndConvertToDTO(file0Path);
			res.setSchema(fileInfo0.getSchema());
			
			ParquetDataFileInfoDTO dataInfo0 = new ParquetDataFileInfoDTO(fileInfo0.getNumRows(), fileInfo0.getBlocks());
			PartitionAndFileDataInfoDTO partFileInfo0 = new PartitionAndFileDataInfoDTO(
					resFile0.partitions, file0Path.getName(), dataInfo0);
			partFileInfos.add(partFileInfo0);

			// TOOPTIM parallel read file (footer) + convert to DTO..
			List<PartitionAndFileDataInfoDTO> remainPartFileInfos = LsUtils.map(resFiles, // parse footer of remaining files 
					partAndFile -> readParquetInfoAndConvertToDTO(partAndFile));
			partFileInfos.addAll(remainPartFileInfos);
			res.setPartFileInfos(partFileInfos);

		} else {
			// no files!
			res.setPartFileInfos(new ArrayList<>());
		}
		
		return res;
	}

	private PartitionAndFileDataInfoDTO readParquetInfoAndConvertToDTO(PartitionAndFile partAndFile) {
		ParquetFileInfoDTO fileInfo = readParquetInfoAndConvertToDTO(partAndFile.fileStatus.getPath());
		
		ParquetDataFileInfoDTO dataInfo = new ParquetDataFileInfoDTO(fileInfo.getNumRows(), fileInfo.getBlocks());
		String fileName = partAndFile.fileStatus.getPath().getName();
		return new PartitionAndFileDataInfoDTO(partAndFile.partitions, fileName, dataInfo);
	}
	

	// ------------------------------------------------------------------------

	private static URI stringToURI(String uri) {
		try {
			return new URI(uri);
		} catch (URISyntaxException e) {
			throw new RuntimeException("bad URI syntax", e);
		}
	}
	
	private static Path pathOrRelativeToHadoopPath(String path) {
		String url = path;
		if (path.startsWith("./")) {
			url = "file:///" + new File(path).getAbsolutePath();
		}
		return new Path(url);
	}

	private ParquetFileReader openParquetFileReader(Path path) {
		InputFile inputFile = toInput(path);
		ParquetReadOptions readOptions = ParquetReadOptions.builder().build();

		ParquetFileReader fileReader;
		try {
			fileReader = ParquetFileReader.open(inputFile, readOptions);
		} catch (IOException ex) {
			throw new RuntimeException("", ex);
		}
		return fileReader;
	}

	private static void closeQuietly(ParquetFileReader fileReader) {
		if (fileReader != null) {
			try {
				fileReader.close();
			} catch (IOException e) {
				System.out.println("close, no rethrow " + e.getMessage());
			}
		}
	}

	
	private InputFile toInput(Path path) {
		try {
			return HadoopInputFile.fromPath(path, hadoopConf);
		} catch (IOException ex) {
			throw new RuntimeException("Invalid HadoopInputFile '" + path + "'", ex);
		}
	}

	@AllArgsConstructor
	public static class PartitionAndFile {
		public final ImmutableList<String> partitions;
		public final FileStatus fileStatus;
	}

	private void recursiveScanPartitionFiles(
			List<PartitionAndFile> res,
			ImmutableList<String> partitions,
			List<PartitionScanStatisticsDTO> partitionScanStatistics,
			Path path) throws FileNotFoundException, IOException {
		FileStatus[] childList = hadoopLocalFs.listStatus(path);
		for(val child: childList) {
			String childName = child.getPath().getName();
			if (childName.startsWith("_") || childName.startsWith(".")) {
				continue; // ignore meta files
			}
			if (child.isDirectory()) {
				int indexEq = childName.indexOf('=');
				if (indexEq == -1) {
					System.out.println("ignore bas part, expecting '=', got " + childName);
					continue;
				}
				String childPartitionCol = childName.substring(0, indexEq); 
				String childPartitionValue = childName.substring(indexEq + 1); 
				ImmutableList<String> childPartitions = LsUtils.immutableConcat(partitions, childPartitionValue);
				int childLevel = childPartitions.size();
				PartitionScanStatisticsDTO childStats;
				if (partitionScanStatistics.size() < childLevel) {
					// first enter level
					childStats = new PartitionScanStatisticsDTO();
					childStats.setPartitionColName(childPartitionCol);
				} else {
					childStats = partitionScanStatistics.get(childLevel);
					if (! childStats.getPartitionColName().equals(childPartitionCol)) {
						System.out.println("invalid partition colName.. expected " + childStats.getPartitionColName() + ", got " + childPartitionCol);
					}
				}
				childStats.count++;
				childStats.foundValues.add(childPartitionValue);
				
				// *** recurse ***
				recursiveScanPartitionFiles(res, childPartitions, partitionScanStatistics, child.getPath());
			} else {
				res.add(new PartitionAndFile(partitions, child));
			}
		}
	}

}
