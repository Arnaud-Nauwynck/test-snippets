package fr.an.tests.testspark;

import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;

import fr.an.tests.testspark.util.LsUtils;
import lombok.extern.slf4j.Slf4j;
import scala.collection.Seq;

@Slf4j
public class SparkAppMain {

	private String appName = "SparkApp";
	private String master = "local[*]";
	
	private String baseDir = "D:/arn/hadoop/rootfs";
	private String baseInputDir = "/inputDir1";
	private String baseOutputDir = "/outputDir1";
//	private String baseInputDir = "/inputDir2";
//	private String baseOutputDir = "/outputDir2";
//	private String baseInputDir = "/inputDir3";
//	private String baseOutputDir = "/outputDir3";

	private int numPartitions = 2;
	private boolean skipWriteChecksumFiles = true;
	
	SparkSession spark;

	SparkContext sc; // = spark.sparkContext();
	JavaSparkContext jsc; // = JavaSparkContext.fromSparkContext(sc);
	Configuration hadoopConf; // = jsc.hadoopConfiguration();
	FileSystem hadoopFs; // = FileSystem.get(hadoopConf);

	
	public static void main(String[] args) {
		SparkAppMain app = new SparkAppMain();
		try {
			app.run();
			
			System.out.println("Finished");
		} catch(Exception ex) {
			System.err.println("Failed, exiting");
			ex.printStackTrace(System.err);
		}
	}

	private void run() throws Exception {
		SparkConf sparkConf = new SparkConf()
				.setAppName(appName)
				.setMaster(master)
				;
		
		this.spark = SparkSession.builder()
					.config(sparkConf)
					.getOrCreate();
		try {
			this.sc = spark.sparkContext();
			this.jsc = JavaSparkContext.fromSparkContext(sc);
			this.hadoopConf = jsc.hadoopConfiguration();
			this.hadoopFs = FileSystem.get(hadoopConf);
			
			// do not generate the "_SUCCESS" files..
			this.hadoopConf.set("mapreduce.fileoutputcommitter.marksuccessfuljobs", "false");

			// do not generate the ".crc" files.. (only on LocalFileSystem??)
			if (skipWriteChecksumFiles) {
				// hadoopFs.setVerifyChecksum(false);  ... when disabled ... Failed!!
				hadoopFs.setWriteChecksum(false);
			}
			
			doRunSparkContext(spark);
		
		} finally {
			spark.stop();
		}
	}

	private void doRunSparkContext(SparkSession spark) throws Exception {
		Path inputPath = new Path(baseDir + "/" + baseInputDir);
		Path outputPath = new Path(baseDir + "/" + baseOutputDir);
		
		FileStatus inputDirFileStatus = hadoopFs.getFileStatus(inputPath); // FileNotFoundException if not exist
		if (! inputDirFileStatus.isDirectory()) {
			log.error("inputDir '" + baseInputDir + "' is not a directory");
		}

		FileStatus outputDirFileStatus = hadoopFs.getFileStatus(outputPath); // FileNotFoundException if not exist
		if (! outputDirFileStatus.isDirectory()) {
			log.error("outputDir '" + baseOutputDir + "' is not a directory");
		}
		
		recursiveConcatFilesForDir(inputPath, outputPath);
	}

	private void recursiveConcatFilesForDir(Path inputDirPath, Path outputDirPath) throws Exception {
		FileStatus[] childLs = hadoopFs.listStatus(inputDirPath);
		List<FileStatus> childDirs = LsUtils.filter(childLs, f -> f.isDirectory());
		List<FileStatus> childFiles = LsUtils.filter(childLs, f -> f.isFile() && !f.getPath().getName().startsWith("."));
		
		if (childDirs != null && !childDirs.isEmpty()) {
			for(FileStatus childDir: childDirs) {
				log.info("recurse sub dir:" + childDir);
				Path inputChildPath = childDir.getPath();
				String childName = inputChildPath.getName();
				Path outputChildPath = new Path(outputDirPath, childName);
				
				if(!hadoopFs.exists(outputChildPath)) {
					hadoopFs.mkdirs(outputChildPath);
				}

				// *** recurse ***
				recursiveConcatFilesForDir(inputChildPath, outputChildPath);
			}
		}

		if (childFiles != null && !childFiles.isEmpty()) {
			log.info("detected " + childFiles.size() + " files in dir");
			// concat all parquet files 
			{ List<FileStatus> parquetChildFiles = LsUtils.filter(childFiles, f -> f.getPath().getName().endsWith(".parquet"));
				if (! parquetChildFiles.isEmpty()) {
					log.info("detected " + parquetChildFiles.size() + " parquet files in dir: " + parquetChildFiles);
					List<Path> inputParquetPaths = LsUtils.map(parquetChildFiles, f -> f.getPath());
					
					concatFiles(inputParquetPaths, outputDirPath, "parquet", "parquet");
	
					boolean debugForCompareTest = false;
					if (debugForCompareTest) {
						Path outputOrcDirPath = new Path(outputDirPath.getParent(), outputDirPath.getName() +"-orc");
						concatFiles(inputParquetPaths, outputOrcDirPath, "parquet", "orc");
					}
				}
			}
			// concat all orc files
			{ List<FileStatus> orcChildFiles = LsUtils.filter(childFiles, f -> f.getPath().getName().endsWith(".orc"));
				if (! orcChildFiles.isEmpty()) {
					log.info("detected " + orcChildFiles.size() + " orc files in dir: " + orcChildFiles);
					List<Path> inputOrcPaths = LsUtils.map(orcChildFiles, f -> f.getPath());
					
					concatFiles(inputOrcPaths, outputDirPath, "orc", "orc");
	
					boolean debugForCompareTest = false;
					if (debugForCompareTest) {
						Path outputParquetDirPath = new Path(outputDirPath.getParent(), outputDirPath.getName() +"-parquet");
						concatFiles(inputOrcPaths, outputParquetDirPath, "orc", "parquet");
					}
				}
			}			
		}
	}

	private void concatFiles(List<Path> inputPaths, 
			Path outputParentDirPath,
			String inputFormat, String outputFormat
			) {
		List<String> inputPathUris = LsUtils.map(inputPaths, f -> f.toUri().toString());
		Seq<String> scalaInputPaths = LsUtils.toScalaList(inputPathUris);
		String outputDir = outputParentDirPath.toUri().toString();
		log.info("spark write " + outputFormat + " to dir: '" + outputDir + "' " 
				+ "coalesce " + numPartitions 
				+ " from " + inputFormat + " files: " + inputPathUris);
		
		long startTime = System.currentTimeMillis();
		
		spark.read()
			.option("mergeSchema", true) // useless if all file have same schema..
			.format(inputFormat)
			.load(scalaInputPaths)
			.coalesce(numPartitions)
			.write()
			// .option("spark.sql.parquet.compression.codec", "snappy") // default
			.mode(SaveMode.Overwrite)
			.format(outputFormat)
			.save(outputDir);

		long millis = System.currentTimeMillis() - startTime;
		log.info(".. done write " + outputFormat + " to dir: '" + outputDir + "' .. took " + millis + " ms");
	}
	
}
