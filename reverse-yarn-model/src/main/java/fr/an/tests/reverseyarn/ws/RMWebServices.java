package fr.an.tests.reverseyarn.ws;

import java.util.Set;

import fr.an.tests.reverseyarn.dto.rmws.ActivitiesInfo;
import fr.an.tests.reverseyarn.dto.rmws.AppActivitiesInfo;
import fr.an.tests.reverseyarn.dto.rmws.AppAttemptInfo;
import fr.an.tests.reverseyarn.dto.rmws.AppAttemptsInfo;
import fr.an.tests.reverseyarn.dto.rmws.AppInfo;
import fr.an.tests.reverseyarn.dto.rmws.AppPriority;
import fr.an.tests.reverseyarn.dto.rmws.AppQueue;
import fr.an.tests.reverseyarn.dto.rmws.AppState;
import fr.an.tests.reverseyarn.dto.rmws.AppTimeoutInfo;
import fr.an.tests.reverseyarn.dto.rmws.AppTimeoutsInfo;
import fr.an.tests.reverseyarn.dto.rmws.ApplicationStatisticsInfo;
import fr.an.tests.reverseyarn.dto.rmws.ApplicationSubmissionContextInfo;
import fr.an.tests.reverseyarn.dto.rmws.AppsInfo;
import fr.an.tests.reverseyarn.dto.rmws.BulkActivitiesInfo;
import fr.an.tests.reverseyarn.dto.rmws.ClusterInfo;
import fr.an.tests.reverseyarn.dto.rmws.ClusterMetricsInfo;
import fr.an.tests.reverseyarn.dto.rmws.ClusterUserInfo;
import fr.an.tests.reverseyarn.dto.rmws.ContainerInfo;
import fr.an.tests.reverseyarn.dto.rmws.ContainersInfo;
import fr.an.tests.reverseyarn.dto.rmws.DelegationToken;
import fr.an.tests.reverseyarn.dto.rmws.LabelsToNodesInfo;
import fr.an.tests.reverseyarn.dto.rmws.NodeInfo;
import fr.an.tests.reverseyarn.dto.rmws.NodeLabelsInfo;
import fr.an.tests.reverseyarn.dto.rmws.NodeToLabelsEntryList;
import fr.an.tests.reverseyarn.dto.rmws.NodeToLabelsInfo;
import fr.an.tests.reverseyarn.dto.rmws.NodesInfo;
import fr.an.tests.reverseyarn.dto.rmws.RMQueueAclInfo;
import fr.an.tests.reverseyarn.dto.rmws.ReservationDeleteRequestInfo;
import fr.an.tests.reverseyarn.dto.rmws.ReservationSubmissionRequestInfo;
import fr.an.tests.reverseyarn.dto.rmws.ReservationUpdateRequestInfo;
import fr.an.tests.reverseyarn.dto.rmws.ResourceInfo;
import fr.an.tests.reverseyarn.dto.rmws.ResourceOptionInfo;
import fr.an.tests.reverseyarn.dto.rmws.SchedConfUpdateInfo;
import fr.an.tests.reverseyarn.dto.rmws.SchedulerTypeInfo;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;


/**
 * 
 * https://github.com/apache/hadoop/blob/trunk/hadoop-yarn-project/hadoop-yarn/hadoop-yarn-server/hadoop-yarn-server-resourcemanager/src/main/java/org/apache/hadoop/yarn/server/resourcemanager/webapp/RMWebServices.java#L228
 *
 */
public interface RMWebServices {

	public static final String BASE_PATH = "/ws/v1/cluster";

	@GET(BASE_PATH)
	public ClusterInfo get();

	@GET(BASE_PATH + "/info")
	public ClusterInfo getClusterInfo();

	@GET(BASE_PATH + "/userinfo")
	public ClusterUserInfo getClusterUserInfo();

	@GET(BASE_PATH + "/metrics")
	public ClusterMetricsInfo getClusterMetricsInfo();

	@GET(BASE_PATH + "/scheduler")
	public SchedulerTypeInfo getSchedulerInfo();

	@POST(BASE_PATH + "/scheduler/logs")
	public String dumpSchedulerLogs(@Field("time") String time);


	@GET(BASE_PATH + "/nodes")
	public NodesInfo getNodes(@Query("states") String states);


	@GET(BASE_PATH + "/nodes/{nodeId}")
	public NodeInfo getNode(@Path("nodeId") String nodeId);

	@POST(BASE_PATH + "/nodes/{nodeId}/resource")
	public ResourceInfo updateNodeResource(
			@Path("nodeId") String nodeId,
			@Body ResourceOptionInfo resourceOption);

	@GET(BASE_PATH + "/apps")
	public AppsInfo getApps(
			@Query("state") String stateQuery,
			@Query("states") Set<String> statesQuery,
			@Query("finalStatus") String finalStatusQuery,
			@Query("user") String userQuery,
			@Query("queue") String queueQuery,
			@Query("limit") String limit,
			@Query("startedTimeBegin") String startedBegin,
			@Query("startedTimeEnd") String startedEnd,
			@Query("finishedTimeBegin") String finishBegin,
			@Query("finishedTimeEnd") String finishEnd,
			@Query("applicationTypes") Set<String> applicationTypes,
			@Query("applicationTags") Set<String> applicationTags,
			@Query("name") String name,
			@Query("deSelects") Set<String> unselectedFields);


	@GET(BASE_PATH + "/scheduler/activities")
	public ActivitiesInfo getActivities(
			@Query("nodeId") String nodeId,
			@Query("groupBy") String groupBy);

	@GET(BASE_PATH + "/scheduler/bulk-activities")
	public BulkActivitiesInfo getBulkActivities(
			@Query("groupBy") String groupBy,
			@Query("activitiesCount") Integer activitiesCount);


	@GET(BASE_PATH + "/scheduler/app-activities/{appid}")
	public AppActivitiesInfo getAppActivities(
			@Path("appid") String appId,
			@Query("maxTime") String time,
			@Query("requestPriorities") Set<String> requestPriorities,
			@Query("allocationRequestIds") Set<String> allocationRequestIds,
			@Query("groupBy") String groupBy,
			@Query("limit") String limit,
			@Query("actions") Set<String> actions,
			@Query("summarize") Boolean summarize);


	@GET(BASE_PATH + "/appstatistics")
	public ApplicationStatisticsInfo getAppStatistics(
			@Query("states") Set<String> stateQueries,
			@Query("applicationTypes") Set<String> typeQueries);


	@GET(BASE_PATH + "/apps/{appid}")
	public AppInfo getApp(
			@Path("appid") String appId,
			@Query("deSelects") Set<String> unselectedFields);

	@GET(BASE_PATH + "/apps/{appid}/appattempts")
	public AppAttemptsInfo getAppAttempts(
			@Path("appid") String appId);


	@GET(BASE_PATH + "/apps/{appid}/appattempts/{appattemptid}")
	public AppAttemptInfo getAppAttempt(
			@Path("appid") String appId,
			@Path("appattemptid") String appAttemptId);

	@GET(BASE_PATH + "/apps/{appid}/appattempts/{appattemptid}/containers")
	public ContainersInfo getContainers(
			@Path("appid") String appId,
			@Path("appattemptid") String appAttemptId);


	@GET(BASE_PATH + "/apps/{appid}/appattempts/{appattemptid}/containers/{containerid}")
	public ContainerInfo getContainer(
			@Path("appid") String appId,
			@Path("appattemptid") String appAttemptId,
			@Path("containerid") String containerId);

	@GET(BASE_PATH + "/apps/{appid}/state")
	public AppState getAppState(
			@Path("appid") String appId);


	@PUT(BASE_PATH + "/apps/{appid}/state")
	public Object updateAppState(AppState targetState,
			@Path("appid") String appId);

	@GET(BASE_PATH + "/get-node-to-labels")
	public NodeToLabelsInfo getNodeToLabels();

	@GET(BASE_PATH + "/label-mappings")
	public LabelsToNodesInfo getLabelsToNodes(
			@Query("labels") Set<String> labels);

	@POST(BASE_PATH + "/replace-node-to-labels")
	public Object replaceLabelsOnNodes(
			@Body NodeToLabelsEntryList newNodeToLabels);

	@POST(BASE_PATH + "/nodes/{nodeId}/replace-labels")
	public Object replaceLabelsOnNode(
			@Query("labels") Set<String> newNodeLabelsName,
			@Path("nodeId") String nodeId);



	@GET(BASE_PATH + "/get-node-labels")
	public NodeLabelsInfo getClusterNodeLabels();


	@POST(BASE_PATH + "/add-node-labels")
	public Object addToClusterNodeLabels(
			@Body NodeLabelsInfo newNodeLabels);


	@POST(BASE_PATH + "/remove-node-labels")
	public Object removeFromCluserNodeLabels(
			@Query("labels") Set<String> oldNodeLabels
			);

	@GET(BASE_PATH + "/nodes/{nodeId}/get-labels")
	public NodeLabelsInfo getLabelsOnNode(
			@Path("nodeId") String nodeId);



	@GET(BASE_PATH + "/apps/{appid}/priority")
	public AppPriority getAppPriority(
			@Path("appid") String appId);

	@PUT(BASE_PATH + "/apps/{appid}/priority")
	public Object updateApplicationPriority(
			@Body AppPriority targetPriority,
			@Path("appid") String appId);

	@GET(BASE_PATH + "/apps/{appid}/queue")
	public AppQueue getAppQueue(
			@Path("appid") String appId);


	@PUT(BASE_PATH + "/apps/{appid}/queue")
	public Object updateAppQueue(
			@Body AppQueue targetQueue,
			@Path("appid") String appId);


	@POST(BASE_PATH + "/apps/new-application")
	public Object createNewApplication();


	@POST(BASE_PATH + "/apps")
	public Object submitApplication(
			@Body ApplicationSubmissionContextInfo newApp);


	@POST(BASE_PATH + "/delegation-token")
	public Object postDelegationToken(
			@Body DelegationToken tokenData);

	@POST(BASE_PATH + "/delegation-token/expiration")
	public Object postDelegationTokenExpiration();

	@DELETE(BASE_PATH + "/delegation-token")
	public Object cancelDelegationToken();

	@POST(BASE_PATH + "/reservation/new-reservation")
	public Object createNewReservation();


	@POST(BASE_PATH + "/reservation/submit")
	public Object submitReservation(
			@Body ReservationSubmissionRequestInfo resContext);


	@POST(BASE_PATH + "/reservation/update")
	public Object updateReservation(
			@Body ReservationUpdateRequestInfo resContext);

	@POST(BASE_PATH + "/reservation/delete")
	public Object deleteReservation(
			@Body ReservationDeleteRequestInfo resContext);


	@GET(BASE_PATH + "/reservation/list")
	public Object listReservation(
			@Query("queue") String queue,
			@Query("reservation-id") String reservationId,
			@Query("start-time") Long startTime,
			@Query("end-time") Long endTime,
			@Query("include-resource-allocations") Boolean includeResourceAllocations
			);

	@GET(BASE_PATH + "/apps/{appid}/timeouts/{type}")
	public AppTimeoutInfo getAppTimeout(
			@Path("appid") String appId,
			@Path("type") String type);

	@GET(BASE_PATH + "/apps/{appid}/timeouts")
	public AppTimeoutsInfo getAppTimeouts(
			@Path("appid") String appId);


	@PUT(BASE_PATH + "/apps/{appid}/timeout")
	public Object updateApplicationTimeout(
			@Body AppTimeoutInfo appTimeout,
			@Path("appid") String appId);


	@GET(BASE_PATH + "/scheduler-conf/format")
	public Object formatSchedulerConfiguration();


	@POST(BASE_PATH + "/scheduler-conf/validate")
	public Object validateAndGetSchedulerConfiguration(
			@Body SchedConfUpdateInfo mutationInfo);

	@PUT(BASE_PATH + "/scheduler-conf")
	public Object updateSchedulerConfiguration(
			@Body SchedConfUpdateInfo mutationInfo);


	@GET(BASE_PATH + "/scheduler-conf")
	public Object getSchedulerConfiguration();

	@GET(BASE_PATH + "/scheduler-conf/version")
	public Object getSchedulerConfigurationVersion();

	@GET(BASE_PATH + "/queues/{queue}/access")
	public RMQueueAclInfo checkUserAccessToQueue(
			@Path("queue") String queue,
			@Query("user") String username,
			@Query("queue-acl-type") String queueAclType);


	@POST(BASE_PATH + "/containers/{containerid}/signal/{command}")
	public Object signalToContainer(
			@Path("containerid") String containerId,
			@Path("command") String command);

}
