package fr.an.test.reverseopenlineage.api;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import fr.an.test.reverseopenlineage.api.OpenLineageDTO.DataQualityMetricsInputDatasetFacet.DataQualityMetricsInputDatasetFacetColumnMetrics.DataQualityMetricsInputDatasetFacetColumnMetricsAdditional;
import lombok.Builder;
import lombok.Getter;


public class OpenLineageDTO {

	@Getter
	public static class BaseDTO<TProp> {
	    // @JsonAnySetter 
	    public Map<String, TProp> additionalProperties;
	}


	// RunEvent
	// ------------------------------------------------------------------------
	
	@Builder @Getter
	public static final class RunEvent {
		public URI producer, schemaURL; // "https://openlineage.io/spec/1-0-2/OpenLineage.json#/$defs/RunEvent"
		public String eventType;
		public ZonedDateTime eventTime;
		public Job job;
		public Run run;
		public List<InputDataset> inputs;
		public List<OutputDataset> outputs;
	}

	// Job
	// ------------------------------------------------------------------------
	
	public static final class Job {
		public String namespace, name;
		public JobFacets facets;
		
		@Builder @Getter
		public static final class JobFacets extends BaseDTO<JobFacet> {
			public SourceCodeLocationJobFacet sourceCodeLocation;
			public SQLJobFacet sql;
			public DocumentationJobFacet documentation;
		}
	}

	public interface JobFacet {
		URI get_producer();
		URI get_schemaURL();
		Map<String, Object> getAdditionalProperties();
	}
	
	@Builder @Getter
	public static class DefaultJobFacet extends BaseDTO<Object> implements JobFacet {
		public URI _producer, _schemaURL; // "https://openlineage.io/spec/1-0-2/OpenLineage.json#/$defs/JobFacet"
	}
	
	@Builder @Getter
	public static final class SourceCodeLocationJobFacet extends BaseDTO<Object> implements JobFacet {
		public URI _producer, _schemaURL; // "https://openlineage.io/spec/facets/1-0-0/SourceCodeLocationJobFacet.json#/$defs/SourceCodeLocationJobFacet"
		public String type;
		public URI url;
		public String repoUrl, path, version, tag, branch;
	}

	@Builder @Getter
	public static final class SQLJobFacet extends BaseDTO<Object> implements JobFacet {
		public URI _producer, _schemaURL; // "https://openlineage.io/spec/facets/1-0-0/SQLJobFacet.json#/$defs/SQLJobFacet"
		public String query;
	}
	
	@Builder @Getter
	public static final class DocumentationJobFacet extends BaseDTO<Object> implements JobFacet {
		public URI _producer, _schemaURL; // "https://openlineage.io/spec/facets/1-0-0/DocumentationJobFacet.json#/$defs/DocumentationJobFacet"
		public String description;
	}

	// Run
	// ------------------------------------------------------------------------
	
	public static final class Run {
		public UUID runId;
		public RunFacets facets;
		
		@Builder @Getter
		public static final class RunFacets extends BaseDTO<RunFacet> {
			public ParentRunFacet parent;
			public NominalTimeRunFacet nominalTime;
		}
	}

	public interface RunFacet {
		URI get_producer();
		URI get_schemaURL();
		Map<String, Object> getAdditionalProperties();
	}
	
	@Builder @Getter
	public static class DefaultRunFacet extends BaseDTO<Object> implements RunFacet {
		public URI _producer, _schemaURL; // "https://openlineage.io/spec/1-0-2/OpenLineage.json#/$defs/RunFacet"
	}

	@Builder @Getter
	public static final class NominalTimeRunFacet extends BaseDTO<Object> implements RunFacet {
		public URI _producer, _schemaURL; // "https://openlineage.io/spec/facets/1-0-0/NominalTimeRunFacet.json#/$defs/NominalTimeRunFacet"
		public ZonedDateTime nominalStartTime, nominalEndTime;
	}

	@Builder @Getter
	public static final class ParentRunFacet extends BaseDTO<Object> implements RunFacet {
		public URI _producer, _schemaURL; // "https://openlineage.io/spec/facets/1-0-0/ParentRunFacet.json#/$defs/ParentRunFacet"
		public ParentRunFacetJob job;
		public ParentRunFacetRun run;

		@Builder @Getter
		public static final class ParentRunFacetJob {
			public String namespace, name;
		}

		@Builder @Getter
		public static final class ParentRunFacetRun {
			public UUID runId;
		}
	}

	// DataSet (common to InputDataset | OutputDataset)
	// ------------------------------------------------------------------------

	public interface Dataset {
		String getNamespace();
		String getName();
		DatasetFacets getFacets();
	}

	@Builder @Getter
	public static final class DatasetFacets extends BaseDTO<DatasetFacet> {
		public DocumentationDatasetFacet documentation;
		public DatasourceDatasetFacet dataSource;
		public SchemaDatasetFacet schema;
	}

	public interface DatasetFacet {
		URI get_producer();
		URI get_schemaURL();
		Map<String, Object> getAdditionalProperties();
	}
	
	@Builder @Getter
	public static class DefaultDatasetFacet extends BaseDTO<Object> implements DatasetFacet {
		public URI _producer, _schemaURL; // "https://openlineage.io/spec/1-0-2/OpenLineage.json#/$defs/DatasetFacet"
	}

	@Builder @Getter
	public static final class DocumentationDatasetFacet extends BaseDTO<Object> implements DatasetFacet {
		public URI _producer, _schemaURL; // "https://openlineage.io/spec/facets/1-0-0/DocumentationDatasetFacet.json#/$defs/DocumentationDatasetFacet"
		public String description;
	}

	@Builder @Getter
	public static final class DatasourceDatasetFacet extends BaseDTO<Object> implements DatasetFacet {
		public URI _producer, _schemaURL; // "https://openlineage.io/spec/facets/1-0-0/DatasourceDatasetFacet.json#/$defs/DatasourceDatasetFacet"
		public String name;
		public URI uri;
	}

	@Builder @Getter
	public static final class SchemaDatasetFacet extends BaseDTO<Object> implements DatasetFacet {
		public URI _producer, _schemaURL; // "https://openlineage.io/spec/facets/1-0-0/SchemaDatasetFacet.json#/$defs/SchemaDatasetFacet"
		public List<SchemaDatasetFacetFields> fields;

		@Builder @Getter
		public static final class SchemaDatasetFacetFields { // no BaseDTO additionProperties
			public String type, name, description;
		}
	}

	
	// InputDataset
	// ------------------------------------------------------------------------
	
	@Builder @Getter
	public static final class InputDataset implements Dataset {
		public String namespace, name;
		public DatasetFacets facets;
		public InputDatasetInputFacets inputFacets;
	
		@Builder @Getter
		public static final class InputDatasetInputFacets extends BaseDTO<InputDatasetFacet> {
			public DataQualityAssertionsDatasetFacet dataQualityAssertions;
			public DataQualityMetricsInputDatasetFacet dataQualityMetrics;
		}
	}

	public interface InputDatasetFacet {
		URI get_producer();
		URI get_schemaURL();
		Map<String, Object> getAdditionalProperties();
	}

	@Builder @Getter
	public static class DefaultInputDatasetFacet extends BaseDTO<Object> implements InputDatasetFacet {
		public URI _producer, _schemaURL; // "https://openlineage.io/spec/1-0-2/OpenLineage.json#/$defs/InputDatasetFacet" 
	}

	@Builder @Getter
	public static final class DataQualityAssertionsDatasetFacet extends BaseDTO<Object> implements InputDatasetFacet {
		public URI _producer, _schemaURL; // "https://openlineage.io/spec/facets/1-0-0/DataQualityAssertionsDatasetFacet.json#/$defs/DataQualityAssertionsDatasetFacet"
		public List<DataQualityAssertionsDatasetFacetAssertions> assertions;

		@Builder @Getter
		public static final class DataQualityAssertionsDatasetFacetAssertions {
			public String assertion;
			public Boolean success;
			public String column;
		}
	}

	@Builder @Getter
	public static final class DataQualityMetricsInputDatasetFacet extends BaseDTO<Object> implements InputDatasetFacet {
		public URI _producer, _schemaURL; // "https://openlineage.io/spec/facets/1-0-0/DataQualityMetricsInputDatasetFacet.json#/$defs/DataQualityMetricsInputDatasetFacet"
		public Long rowCount, bytes;
		public DataQualityMetricsInputDatasetFacetColumnMetrics columnMetrics;

		public static final class DataQualityMetricsInputDatasetFacetColumnMetrics extends BaseDTO<DataQualityMetricsInputDatasetFacetColumnMetricsAdditional> {

			public static final class DataQualityMetricsInputDatasetFacetColumnMetricsAdditional {
				public Long nullCount;
				public Long distinctCount;
				public Double sum, count, min, max;
				public DataQualityMetricsInputDatasetFacetColumnMetricsAdditionalQuantiles quantiles;

				public static final class DataQualityMetricsInputDatasetFacetColumnMetricsAdditionalQuantiles extends BaseDTO<Double> {
					// cf additionalProperties Double
				}
			}
		}
	}

	// OutputDataset
	// ------------------------------------------------------------------------
	
	@Builder @Getter
	public static final class OutputDataset implements Dataset {
		public String namespace, name;
		public DatasetFacets facets;
		public OutputDatasetOutputFacets outputFacets;

		public static final class OutputDatasetOutputFacets extends BaseDTO<OutputDatasetFacet> {
			public OutputStatisticsOutputDatasetFacet outputStatistics;
		}
	}

	public interface OutputDatasetFacet {
		URI get_producer();
		URI get_schemaURL();
		Map<String, Object> getAdditionalProperties();
	}

	@Builder @Getter
	public static class DefaultOutputDatasetFacet extends BaseDTO<Object> implements OutputDatasetFacet {
		public URI _producer, _schemaURL; // "https://openlineage.io/spec/1-0-2/OpenLineage.json#/$defs/OutputDatasetFacet"
	}
	
	@Builder @Getter
	public static final class OutputStatisticsOutputDatasetFacet extends BaseDTO<Object> implements OutputDatasetFacet {
		public URI _producer, _schemaURL; // "https://openlineage.io/spec/facets/1-0-0/OutputStatisticsOutputDatasetFacet.json#/$defs/OutputStatisticsOutputDatasetFacet"
		public Long rowCount;
		public Long size;
	}

}
