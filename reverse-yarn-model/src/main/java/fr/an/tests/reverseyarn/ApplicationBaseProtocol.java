package fr.an.tests.reverseyarn;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import fr.an.tests.reverseyarn.dto.ApplicationAttemptId;
import fr.an.tests.reverseyarn.dto.ApplicationAttemptReport;
import fr.an.tests.reverseyarn.dto.ApplicationId;
import fr.an.tests.reverseyarn.dto.ApplicationReport;
import fr.an.tests.reverseyarn.dto.ApplicationsRequestScope;
import fr.an.tests.reverseyarn.dto.ContainerId;
import fr.an.tests.reverseyarn.dto.ContainerReport;
import fr.an.tests.reverseyarn.dto.LongRange;
import fr.an.tests.reverseyarn.dto.Token;
import fr.an.tests.reverseyarn.dto.YarnApplicationState;

/**
 * <p>
 * The protocol between clients and the <code>ResourceManager</code> or
 * <code>ApplicationHistoryServer</code> to get information on applications,
 * application attempts and containers.
 * </p>
 *
 */
public interface ApplicationBaseProtocol {

	public GetApplicationReportResponse getApplicationReport(GetApplicationReportRequest request)
			throws YarnException, IOException;

	public GetApplicationsResponse getApplications(GetApplicationsRequest request) throws YarnException, IOException;

	public GetApplicationAttemptReportResponse getApplicationAttemptReport(GetApplicationAttemptReportRequest request)
			throws YarnException, IOException;

	public GetApplicationAttemptsResponse getApplicationAttempts(GetApplicationAttemptsRequest request)
			throws YarnException, IOException;

	public GetContainerReportResponse getContainerReport(GetContainerReportRequest request)
			throws YarnException, IOException;

	public GetContainersResponse getContainers(GetContainersRequest request) throws YarnException, IOException;

	public GetDelegationTokenResponse getDelegationToken(GetDelegationTokenRequest request)
			throws YarnException, IOException;

	public RenewDelegationTokenResponse renewDelegationToken(RenewDelegationTokenRequest request)
			throws YarnException, IOException;

	public CancelDelegationTokenResponse cancelDelegationToken(CancelDelegationTokenRequest request)
			throws YarnException, IOException;

	// ------------------------------------------------------------------------

	public static class GetApplicationReportRequest {
		public ApplicationId applicationId;
	}

	public static class GetApplicationReportResponse {
		public ApplicationReport applicationReport;
	}

	public static class GetApplicationsRequest {
		public Set<String> applicationTypes;
		public EnumSet<YarnApplicationState> applicationStates;
		public Set<String> users;
		public Set<String> queues;
		public long limit;
		public LongRange startRange, finishRange;
		public Set<String> applicationTags;
		public ApplicationsRequestScope scope;
	}

	public static class GetApplicationsResponse {
		public List<ApplicationReport> applications;
	}

	public static class GetApplicationAttemptReportRequest {
		public ApplicationAttemptId applicationAttemptId;
	}

	public static class GetApplicationAttemptReportResponse {
		public ApplicationAttemptReport applicationAttemptReport;
	}

	public static class GetApplicationAttemptsRequest {
		public ApplicationId applicationId;
	}

	public static class GetApplicationAttemptsResponse {
		public List<ApplicationAttemptReport> applicationAttempts;
	}

	public static class GetContainerReportRequest {
		public ContainerId containerId;
	}

	public static class GetContainerReportResponse {
		public ContainerReport containerReport;
	}

	public static class GetContainersRequest {
		public ApplicationAttemptId applicationAttemptId;
	}

	public static class GetContainersResponse {
		public List<ContainerReport> containerList;
	}

	public static class GetDelegationTokenRequest {
		public String renewer;
	}

	public static class GetDelegationTokenResponse {
		public Token rmDelegationToken;
	}

	public static class RenewDelegationTokenRequest {
		public Token delegationToken;
	}

	public static class RenewDelegationTokenResponse {
		public long nextExpirationTime;
	}

	public static class CancelDelegationTokenRequest {
		public Token delegationToken;
	}

	public static class CancelDelegationTokenResponse {
	}

}
