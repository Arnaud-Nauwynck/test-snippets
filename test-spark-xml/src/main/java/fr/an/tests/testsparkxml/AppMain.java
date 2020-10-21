package fr.an.tests.testsparkxml;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.JavaTypeInference;
import org.apache.spark.sql.catalyst.expressions.GenericRow;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.StructType;

import fr.an.tests.testsparkxml.avro.User;
import fr.an.tests.testsparkxml.dto.UserDTO;
import fr.an.tests.testsparkxml.dto.UserJaxbDTO;
import fr.an.tests.testsparkxml.utils.SparkAvroSchemaUtils;
import lombok.val;
import scala.Tuple2;

public class AppMain {

	SparkSession spark;
	JavaSparkContext jsc;
	
	String inputFilename = "src/data/input-xml-lines.txt";
	
	public static void main(String[] args) {
		try {
			AppMain app = new AppMain();
			app.parseArgs(args);
			app.run();
			
			System.out.println("Finished");
		} catch(Exception ex) {
			System.err.println("Failed");
			ex.printStackTrace();
		}
	}

	private void parseArgs(String[] args) {
		for(int i = 0; i < args.length; i++) {
			String a = args[i];
			if (a.equals("--inputFile")) {
				this.inputFilename = args[++i];
			} else {
				throw new IllegalArgumentException("Unrecognized arg '" + a + "'");
			}
			
		}
		
	}

	public  void run() throws Exception {
		printSampleXmlLines();

		this.spark = SparkSession.builder()
				.appName("test-spark-xml")
				.master("local[1]")
//				.enableHiveSupport()
			    
				.getOrCreate();
		this.jsc = new JavaSparkContext(spark.sparkContext());
		try {
			runInSpark();
		} finally {
			spark.close(); // .stop(); ??
			this.spark = null;
			this.jsc = null;
		}
	}

	private void printSampleXmlLines() {
		for(int i = 1; i < 5; i++) {
			System.out.println(UserDTO.builder()
					.firstName("firstName-" + i)
					.lastName("lastName-" + i)
					.birthYear(2000 + i)
					.build()
					.toXml());
		}
	}

	private void runInSpark() throws Exception {
		runBasic();
		runInputFile();
		runInputFileXmlToAvroToDataSet();
		runInputFileXmlToAvroToDataSet2();
	}
	
	private void runBasic() throws Exception {
		List<String> xmls = new ArrayList<>();
		for(int i = 1; i < 5; i++) {
			xmls.add(UserDTO.builder()
					.firstName("firstName-" + i)
					.lastName("lastName-" + i)
					.birthYear(2000 + i)
					.build()
					.toXml());
		}
		Dataset<String> xmlDs = spark.createDataset(xmls, Encoders.STRING());
		JavaRDD<String> javaXmlsRDD = xmlDs.toJavaRDD();
		JavaRDD<UserDTO> dtoJRDD = javaXmlsRDD.map(line -> {
			// parse line as xml
			XMLStreamReader xmlReader = XMLInputFactory.newFactory().createXMLStreamReader(new StringReader(line));
			UserJaxbDTO parsed = UserJaxbDTO.jaxbUnmarshal(xmlReader);
			// create Row or avro 
			return UserDTO.builder()
					.firstName(parsed.getFirstName())
					.lastName(parsed.getLastName())
					.birthYear(parsed.getBirthYear())
					.build();
		});
		
		dtoJRDD.cache();
		
		long count = dtoJRDD.count();
		System.out.println("spark.read().textFile(" + inputFilename + ").map(line -> { jaxb.unmarshal ..}).count:" + count);

	}
	
	private void runInputFile() {
		Dataset<String> inputFileDs = spark.read().textFile(inputFilename);
		JavaRDD<String> jinputFileDs = inputFileDs.javaRDD();

		JavaRDD<UserDTO> dtoJRDD = jinputFileDs.map(line -> {
			// parse line as xml
			XMLStreamReader xmlReader = XMLInputFactory.newFactory().createXMLStreamReader(new StringReader(line));
			UserJaxbDTO parsed = UserJaxbDTO.jaxbUnmarshal(xmlReader);
			// create Row or avro 
			return UserDTO.builder()
					.firstName(parsed.getFirstName())
					.lastName(parsed.getLastName())
					.birthYear(parsed.getBirthYear())
					.build();
		});
		
		dtoJRDD.cache();
		
		long count = dtoJRDD.count();
		System.out.println("spark.read().textFile(" + inputFilename + ").map(line -> { jaxb.unmarshal ..}).count:" + count);
	
//		RDD<UserDTO> rdd = dtoJRDD.rdd();
//		rdd.
		
		// to DataSet<Row> ..
		Dataset<Row> ds = spark.createDataFrame(dtoJRDD, UserDTO.class);
		StructType sparkUserType = ds.schema();
		System.out.println("jrdd -> DataSet<UserDTO> -> schema:" + sparkUserType.toString());
		
		Schema userAvroSchema = SparkAvroSchemaUtils.sparkTypeToAvroSchema(sparkUserType);
		System.out.println("... -> to avro schema:" + userAvroSchema.toString());

		ds.show(2, false);
		System.out.println();
	}


	private void runInputFileXmlToAvroToDataSet() {
		Dataset<String> inputFileDs = spark.read().textFile(inputFilename);
		JavaRDD<String> jinputFileDs = inputFileDs.javaRDD();

		JavaRDD<Row> rowJRDD = jinputFileDs.map(line -> {
			// parse line as xml
			XMLStreamReader xmlReader = XMLInputFactory.newFactory().createXMLStreamReader(new StringReader(line));
			UserJaxbDTO parsed = UserJaxbDTO.jaxbUnmarshal(xmlReader);
			// create User avro (almost useless.. cf next for avroRecordToRow)
			val avroUser = fr.an.tests.testsparkxml.avro.User.newBuilder()
					.setFirstName(parsed.getFirstName())
					.setLastName(parsed.getLastName())
					.setBirthYear(parsed.getBirthYear())
					.build();
			return avroRecordToRow(avroUser); // bean to array.. same order as in schema
		});
		org.apache.avro.Schema avroSchema = User.SCHEMA$;
		StructType sparkUserType = (StructType) SparkAvroSchemaUtils.avroSchemaToSparkType(avroSchema);
		Dataset<Row> ds = spark.createDataFrame(rowJRDD, sparkUserType);
		
		val sparkSchema = ds.schema();
		System.out.println("... -> to spark schema:" + sparkSchema);
		
		ds.cache();
		long count = ds.count();
		System.out.println("count:" + count);
		ds.show(2, false);
		System.out.println();
	}

	private static Row avroRecordToRow(IndexedRecord record) {
		int len = record.getSchema().getFields().size();
		Object[] avroValues = new Object[len];
		for(int i = 0; i < len; i++) {
			avroValues[i] = record.get(i);
		}
		return new GenericRow(avroValues);
	}

	private void runInputFileXmlToAvroToDataSet2() {
		Dataset<String> inputFileDs = spark.read().textFile(inputFilename);
		JavaRDD<String> jinputFileDs = inputFileDs.javaRDD();

		JavaRDD<fr.an.tests.testsparkxml.avro.User> dtoJRDD = jinputFileDs.map(line -> {
			// parse line as xml
			XMLStreamReader xmlReader = XMLInputFactory.newFactory().createXMLStreamReader(new StringReader(line));
			UserJaxbDTO parsed = UserJaxbDTO.jaxbUnmarshal(xmlReader);
			// create User avro
			return fr.an.tests.testsparkxml.avro.User.newBuilder()
					.setFirstName(parsed.getFirstName())
					.setLastName(parsed.getLastName())
					.setBirthYear(parsed.getBirthYear())
					.build();
		});

		// to DataSet<Row> ..
		try {
			spark.createDataFrame(dtoJRDD, User.class);
			// .. not reached
		} catch(UnsupportedOperationException ex) {
			//??? works for java DTO beans, but not for Avro generated DTO ?!!
//		... ERROR java.lang.UnsupportedOperationException: Cannot have circular references in bean class, but got the circular reference of class class org.apache.avro.Schema
			System.out.println("Failed ..");
		
			// cf code: SparkSession.scala
//			def createDataFrame(rdd: RDD[_], beanClass: Class[_]): DataFrame = withActive {
//			    val attributeSeq: Seq[AttributeReference] = getSchema(beanClass)
//			    val className = beanClass.getName
//			    val rowRdd = rdd.mapPartitions { iter =>
//			    // BeanInfo is not serializable so we must rediscover it remotely for each partition.
//			      SQLContext.beansToRows(iter, Utils.classForName(className), attributeSeq)
//			    }
//			    Dataset.ofRows(self, LogicalRDD(attributeSeq, rowRdd.setName(rdd.name))(self))
//			 }
//		     private def getSchema(beanClass: Class[_]): Seq[AttributeReference] = {
//			    val (dataType, _) = JavaTypeInference.inferDataType(beanClass)
//			    dataType.asInstanceOf[StructType].fields.map { f =>
//			      AttributeReference(f.name, f.dataType, f.nullable)()
//			    }
//			  }
		}
		
		try {
			Tuple2<DataType, Object> inferDataType = JavaTypeInference.inferDataType(User.class);
			DataType dt = inferDataType._1;
			System.out.println("dataType:" + dt);
		} catch(Exception ex) {
			System.out.println("Failed JavaTypeInference.inferDataType(User.class): " + ex.getMessage());
		}

		org.apache.avro.Schema avroSchema = User.SCHEMA$;
		StructType sparkUserType = (StructType) SparkAvroSchemaUtils.avroSchemaToSparkType(avroSchema);
		
		int fieldsCount = sparkUserType.length();
		JavaRDD<Row> rowJRDD = dtoJRDD.map(userAvro -> {
			Object[] avroValues = new Object[fieldsCount];
			for(int i = 0; i < fieldsCount; i++) {
				avroValues[i] = userAvro.get(i);
			}
//			Object[] values = new Object[] {
//					userAvro.getFirstName(), userAvro.getLastName(), userAvro.getBirthYear()
//			};
			return new GenericRow(avroValues); // same as RowFactory.create(avroValues);
		});
		Dataset<Row> ds = spark.createDataFrame(rowJRDD.rdd(), sparkUserType);
		
		val sparkSchema = ds.schema();
		System.out.println("... -> to spark schema:" + sparkSchema);
		
		ds.cache();
		long count = ds.count();
		System.out.println("count:" + count);
		ds.show(2, false);
	}

}
