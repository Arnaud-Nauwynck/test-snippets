package fr.an.test.testmetaschema.utils;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.apache.avro.Schema;
import org.apache.avro.compiler.specific.SpecificCompiler;
import org.apache.avro.compiler.specific.SpecificCompiler.FieldVisibility;
import org.apache.avro.generic.GenericData.StringType;

import lombok.Getter;
import lombok.Setter;

/**
 * see avro-tools
 */
public class AvroSpecificCompilerHelper {

	private CompilerParams compilerParams;
	
	@Getter @Setter
	public static class CompilerParams {
	    StringType stringType = StringType.CharSequence;
	    boolean useLogicalDecimal = false;
	    Optional<String> encoding = Optional.empty();
	    Optional<String> templateDir = Optional.empty();
	    Optional<FieldVisibility> fieldVisibility = Optional.empty();
	    boolean enableDecimalLogicalType;
	    
	    public void applyToCompiler(SpecificCompiler compiler) {
	    	compiler.setStringType(stringType);
	    	templateDir.ifPresent(compiler::setTemplateDir);
	    	compiler.setEnableDecimalLogicalType(enableDecimalLogicalType);
	    	encoding.ifPresent(compiler::setOutputCharacterEncoding);
	    	fieldVisibility.ifPresent(compiler::setFieldVisibility);
	    }

	}

	public AvroSpecificCompilerHelper(CompilerParams compilerParams) {
		this.compilerParams = compilerParams;
	}

	public AvroSpecificCompilerHelper() {
		this(new CompilerParams());
	}

	public void compileToDestination(
			Schema schema,
			File outputDir
			) throws IOException {
		
		File tempAvroSchemaFile = File.createTempFile("test", "-avro.json");
		try {
			ParquetToAvroSchemaUtils.writeToLocalFile(schema, tempAvroSchemaFile);
			
			SpecificCompiler compiler = new SpecificCompiler(schema);
			compilerParams.applyToCompiler(compiler);
			
			compiler.compileToDestination(tempAvroSchemaFile, outputDir);

		} finally {
			tempAvroSchemaFile.delete();
		}
	}
}
