package fr.an.hadoop.ambarimetrics.ws;

import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.an.hadoop.ambarimetrics.ws.dto.AboutInfoDTO;
import fr.an.hadoop.ambarimetrics.ws.dto.AggregationResultDTO;
import fr.an.hadoop.ambarimetrics.ws.dto.ContainerMetricDTO;
import fr.an.hadoop.ambarimetrics.ws.dto.TimelineMetricMetadataDTO;
import fr.an.hadoop.ambarimetrics.ws.dto.TimelineMetricServiceSummaryDTO;
import fr.an.hadoop.ambarimetrics.ws.dto.TimelineMetricsDTO;
import fr.an.hadoop.ambarimetrics.ws.dto.TimelinePutResponseDTO;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

// @Path("/ws/v1/timeline")
// @Produces({ MediaType.APPLICATION_JSON })
public interface AmbariMetricsTimelineWS {

	public static final String BASE_PATH = "/ws/v1/timeline";

	/**
	 * Return the description of the timeline web services.
	 */
	@GET(BASE_PATH)
	public AboutInfoDTO about();

	/**
	 * Store the given metrics into the timeline store, and return errors that
	 * happened during storing.
	 */
	@POST(BASE_PATH + "/metrics")
	public TimelinePutResponseDTO postMetrics(@Body TimelineMetricsDTO metrics);

	/**
	 * Store the given metrics into the timeline store, and return errors that
	 * happened during storing.
	 */
	@POST(BASE_PATH + "/metrics/aggregated")
	public TimelinePutResponseDTO postAggregatedMetrics(@Body AggregationResultDTO metrics);

	@POST(BASE_PATH + "/containermetrics")
	public TimelinePutResponseDTO postContainerMetrics(@Body List<ContainerMetricDTO> metrics);

	/**
	 * Query for a set of different metrics satisfying the filter criteria. All
	 * query params are optional. The default limit will apply if none specified.
	 *
	 * @param metricNames Comma separated list of metrics to retrieve.
	 * @param appId       Application Id for the requested metrics.
	 * @param instanceId  Application instance id.
	 * @param hostname    Hostname where the metrics originated.
	 * @param startTime   Start time for the metric records retrieved.
	 * @param precision   Precision [ seconds, minutes, hours ]
	 * @param limit       limit on total number of {@link TimelineMetric} records
	 *                    retrieved.
	 * @return {@link @TimelineMetrics}
	 */
	@GET(BASE_PATH + "/metrics")
	public TimelineMetricsDTO getTimelineMetrics(
			@Query("metricNames") String metricNames, //
			@Query("appId") String appId, //
			@Query("instanceId") String instanceId, // 
			@Query("hostname") String hostname, //
			@Query("startTime") String startTime, //
			@Query("endTime") String endTime, //
			@Query("precision") String precision, //
			@Query("limit") String limit, //
			@Query("grouped") String grouped, //
			@Query("topN") String topN, //
			@Query("topNFunction") String topNFunction, //
			@Query("isBottomN") String isBottomN, //
			@Query("seriesAggregateFunction") String seriesAggregateFunction //
			);

	@GET(BASE_PATH + "metrics/{instanceId}")
	public TimelineMetricsDTO getTimelineMetricsForInstance( //
			@Path("instanceId") String instanceId, //
			@Query("metricNames") String metricNames, //
			@Query("appId") String appId, //
			@Query("hostname") String hostname, //
			@Query("startTime") String startTime, //
			@Query("endTime") String endTime, //
			@Query("precision") String precision, //
			@Query("limit") String limit, //
			@Query("grouped") String grouped, //
			@Query("topN") String topN, //
			@Query("topNFunction") String topNFunction,
			@Query("isBottomN") String isBottomN, //
			@Query("seriesAggregateFunction") String seriesAggregateFunction);

	@GET(BASE_PATH + "/metrics/summary")
	public TimelineMetricServiceSummaryDTO getTimelineMetricServiceSummary();

	@GET(BASE_PATH + "/metrics/metadata")
	public Map<String, List<TimelineMetricMetadataDTO>> getTimelineMetricMetadata( //
			@Query("appId") String appId, //
			@Query("metricName") String metricPattern, //
			@Query("includeAll") String includeBlacklistedMetrics);

	@GET(BASE_PATH + "/metrics/{instanceId}/metadata")
	public Map<String, List<TimelineMetricMetadataDTO>> getTimelineMetricMetadataForInstance( //
			@Query("appId") String appId, //
			@Query("metricName") String metricPattern, //
			@Query("includeAll") String includeBlacklistedMetrics);

	@GET(BASE_PATH + "/metrics/hosts")
	public Map<String, Set<String>> getHostedAppsMetadata();

	@GET(BASE_PATH + "/metrics/instance")
	public Map<String, Map<String, Set<String>>> getClusterHostsMetadata( //
			@Query("appId") String appId, //
			@Query("instanceId") String instanceId);

	@GET(BASE_PATH + "/metrics/{instanceId}/instance")
	public Map<String, Map<String, Set<String>>> getClusterHostsMetadataForInstance( //
			@Query("appId") String appId, //
			@Path("instanceId") String instanceId);

	@GET(BASE_PATH + "/metrics/uuid")
	public byte[] getUuid( //
			@Query("metricName") String metricName, //
			@Query("appId") String appId, //
			@Query("instanceId") String instanceId, //
			@Query("hostname") String hostname);

	/**
	 * This is a discovery endpoint that advertises known live collector instances.
	 * Note: It will always answer with current instance as live. This can be
	 * utilized as a liveliness pinger endpoint since the instance names are cached
	 * and thereby no synchronous calls result from this API
	 *
	 * @return List<String> hostnames</String>
	 */
	@GET(BASE_PATH + "/metrics/livenodes")
	public List<String> getLiveCollectorNodes();

}
