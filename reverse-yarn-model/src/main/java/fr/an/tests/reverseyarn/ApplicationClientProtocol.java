package fr.an.tests.reverseyarn;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.an.tests.reverseyarn.dto.ApplicationId;
import fr.an.tests.reverseyarn.dto.ApplicationSubmissionContext;
import fr.an.tests.reverseyarn.dto.NodeId;
import fr.an.tests.reverseyarn.dto.NodeLabel;
import fr.an.tests.reverseyarn.dto.NodeReport;
import fr.an.tests.reverseyarn.dto.NodeState;
import fr.an.tests.reverseyarn.dto.QueueInfo;
import fr.an.tests.reverseyarn.dto.QueueUserACLInfo;
import fr.an.tests.reverseyarn.dto.ReservationDefinition;
import fr.an.tests.reverseyarn.dto.ReservationId;
import fr.an.tests.reverseyarn.dto.Resource;
import fr.an.tests.reverseyarn.dto.YarnClusterMetrics;
import lombok.Data;

public interface ApplicationClientProtocol extends ApplicationBaseProtocol {

	public GetNewApplicationResponse getNewApplication(GetNewApplicationRequest request)
			throws YarnException, IOException;

	public SubmitApplicationResponse submitApplication(SubmitApplicationRequest request)
			throws YarnException, IOException;

	public KillApplicationResponse forceKillApplication(KillApplicationRequest request)
			throws YarnException, IOException;

	public GetClusterMetricsResponse getClusterMetrics(GetClusterMetricsRequest request)
			throws YarnException, IOException;

	public GetClusterNodesResponse getClusterNodes(GetClusterNodesRequest request) throws YarnException, IOException;

	public GetQueueInfoResponse getQueueInfo(GetQueueInfoRequest request) throws YarnException, IOException;

	public GetQueueUserAclsInfoResponse getQueueUserAcls(GetQueueUserAclsInfoRequest request)
			throws YarnException, IOException;

	public MoveApplicationAcrossQueuesResponse moveApplicationAcrossQueues(MoveApplicationAcrossQueuesRequest request)
			throws YarnException, IOException;

	public ReservationSubmissionResponse submitReservation(ReservationSubmissionRequest request)
			throws YarnException, IOException;

	public ReservationUpdateResponse updateReservation(ReservationUpdateRequest request)
			throws YarnException, IOException;

	public ReservationDeleteResponse deleteReservation(ReservationDeleteRequest request)
			throws YarnException, IOException;

	public GetNodesToLabelsResponse getNodeToLabels(GetNodesToLabelsRequest request) throws YarnException, IOException;

	public GetLabelsToNodesResponse getLabelsToNodes(GetLabelsToNodesRequest request) throws YarnException, IOException;

	public GetClusterNodeLabelsResponse getClusterNodeLabels(GetClusterNodeLabelsRequest request)
			throws YarnException, IOException;

	// ------------------------------------------------------------------------

	@Data
	public static class GetNewApplicationRequest {
		// empty
	}

	@Data
	public static class GetNewApplicationResponse {
		public ApplicationId applicationId;
		public Resource maximumResourceCapability;
	}

	@Data
	public static class SubmitApplicationRequest {
		public ApplicationSubmissionContext applicationSubmissionContext;
	}

	@Data
	public static class SubmitApplicationResponse {
		// empty
	}

	@Data
	public static class KillApplicationRequest {
		public ApplicationId applicationId;
		public String diagnostics;
	}

	@Data
	public static class KillApplicationResponse {
		boolean isKillCompleted;
	}

	@Data
	public static class GetClusterMetricsRequest {
		// empty
	}

	@Data
	public static class GetClusterMetricsResponse {
		public YarnClusterMetrics metrics;
	}

	@Data
	public static class GetClusterNodesRequest {
		EnumSet<NodeState> nodeStates;
	}

	@Data
	public static class GetClusterNodesResponse {
		List<NodeReport> nodeReports;
	}

	@Data
	public static class GetQueueInfoRequest {
		public String queueName;
		public boolean includeApplications;
		public boolean includeChildQueues;
		public boolean recursive;
	}

	@Data
	public static class GetQueueInfoResponse {
		public QueueInfo queueInfo;
	}

	@Data
	public static class GetQueueUserAclsInfoRequest {
		// empty
	}

	@Data
	public static class GetQueueUserAclsInfoResponse {
		List<QueueUserACLInfo> queueUserAclsList;
	}

	@Data
	public static class MoveApplicationAcrossQueuesRequest {
		ApplicationId appId;
		String targetQueue;
	}

	@Data
	public static class MoveApplicationAcrossQueuesResponse {
		// empty
	}

	@Data
	public static class ReservationSubmissionRequest {
		ReservationDefinition reservationDefinition;
		String queueName;
		ReservationId reservationId;
	}

	@Data
	public static class ReservationSubmissionResponse {
		// empty
	}

	@Data
	public static class ReservationUpdateRequest {
		ReservationDefinition reservationDefinition;
		ReservationId reservationId;
	}

	@Data
	public static class ReservationUpdateResponse {
		// empty
	}

	@Data
	public static class ReservationDeleteRequest {
		ReservationId reservationId;
	}

	@Data
	public static class ReservationDeleteResponse {
		// empty
	}

	@Data
	public static class GetNodesToLabelsRequest {
		// empty
	}

	@Data
	public static class GetNodesToLabelsResponse {
		Map<NodeId, Set<String>> nodeToLabels;
	}

	@Data
	public static class GetLabelsToNodesRequest {
		Set<String> nodeLabels;
	}

	@Data
	public static class GetLabelsToNodesResponse {
		Map<String, Set<NodeId>> labelsToNodes;
	}

	@Data
	public static class GetClusterNodeLabelsRequest {
		// empty
	}

	@Data
	public static class GetClusterNodeLabelsResponse {
		List<NodeLabel> nodeLabelList;
	}

}
