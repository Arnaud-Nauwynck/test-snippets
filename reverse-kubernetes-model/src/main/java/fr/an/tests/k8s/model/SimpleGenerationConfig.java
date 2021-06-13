package fr.an.tests.k8s.model;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsonschema2pojo.AnnotationStyle;
import org.jsonschema2pojo.DefaultGenerationConfig;
import org.jsonschema2pojo.SourceSortOrder;

import lombok.Data;

@Data
public class SimpleGenerationConfig extends DefaultGenerationConfig {

	private List<URL> sources = new ArrayList<>();
	
	private File targetDirectory = new File(".");

	private String targetPackage;

	private boolean includeToString = false;

	private boolean includeHashcodeAndEquals = false;

	private AnnotationStyle annotationStyle = AnnotationStyle.NONE;
	
	
	@Override
	public boolean isGenerateBuilders() {
		return false;
	}

	@Override
	public boolean isIncludeTypeInfo() {
		return false;
	}

	@Override
	public boolean isIncludeConstructorPropertiesAnnotation() {
		return false;
	}

	@Override
	public boolean isUsePrimitives() {
		return true;
	}

	@Override
	public Iterator<URL> getSource() {
		return sources.iterator();
	}

	@Override
	public File getTargetDirectory() {
		return targetDirectory;
	}

	@Override
	public String getTargetPackage() {
		return targetPackage;
	}

//	@Override
//	public char[] getPropertyWordDelimiters() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public boolean isUseLongIntegers() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean isUseBigIntegers() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean isUseDoubleNumbers() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean isUseBigDecimals() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
	@Override
	public boolean isIncludeHashcodeAndEquals() {
		return includeHashcodeAndEquals;
	}

	@Override
	public boolean isIncludeToString() {
		return includeToString;
	}
//
//	@Override
//	public String[] getToStringExcludes() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
	@Override
	public AnnotationStyle getAnnotationStyle() {
		return annotationStyle ;
	}
//
//	@Override
//	public boolean isUseTitleAsClassname() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public InclusionLevel getInclusionLevel() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Class<? extends Annotator> getCustomAnnotator() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Class<? extends RuleFactory> getCustomRuleFactory() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public boolean isIncludeJsr303Annotations() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean isIncludeJsr305Annotations() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean isUseOptionalForGetters() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public SourceType getSourceType() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public boolean isRemoveOldOutput() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public String getOutputEncoding() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public boolean isUseJodaDates() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean isUseJodaLocalDates() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean isUseJodaLocalTimes() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean isParcelable() {
//		return false;
//	}
//
//	@Override
//	public boolean isSerializable() {
//		return false;
//	}
//
//	@Override
//	public FileFilter getFileFilter() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public boolean isInitializeCollections() {
//		return false;
//	}
//
//	@Override
//	public String getClassNamePrefix() {
//		return null;
//	}

	@Override
	public String getClassNameSuffix() {
		return "";
	}

//	@Override
//	public String[] getFileExtensions() {
//		return null;
//	}

	@Override
	public boolean isIncludeConstructors() {
		return false;
	}

	@Override
	public boolean isConstructorsRequiredPropertiesOnly() {
		return false;
	}

	@Override
	public boolean isIncludeRequiredPropertiesConstructor() {
		return false;
	}

	@Override
	public boolean isIncludeAllPropertiesConstructor() {
		return false;
	}

	@Override
	public boolean isIncludeCopyConstructor() {
		return false;
	}

	@Override
	public boolean isIncludeAdditionalProperties() {
		return false;
	}

	@Override
	public boolean isIncludeGetters() {
		return false;
	}

	@Override
	public boolean isIncludeSetters() {
		return false;
	}

//	@Override
//	public String getTargetVersion() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public boolean isIncludeDynamicAccessors() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean isIncludeDynamicGetters() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean isIncludeDynamicSetters() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean isIncludeDynamicBuilders() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public String getDateTimeType() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public String getDateType() {
//		// TODO Auto-generated method stub
//		return null;
//	}

//	@Override
//	public String getTimeType() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public boolean isFormatDates() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean isFormatTimes() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean isFormatDateTimes() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public String getCustomDatePattern() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public String getCustomTimePattern() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public String getCustomDateTimePattern() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
	@Override
	public SourceSortOrder getSourceSortOrder() {
		return SourceSortOrder.FILES_FIRST;
	}
//
//	@Override
//	public Map<String, String> getFormatTypeMapping() {
//		return null;
//	}
//
//	@Override
//	public boolean isIncludeGeneratedAnnotation() {
//		return false;
//	}

}
