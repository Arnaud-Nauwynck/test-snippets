package fr.an.test.atomix.service;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.an.test.atomix.dto.FooMeth1RequestDTO;
import fr.an.test.atomix.dto.FooMeth1ResponseDTO;
import io.atomix.cluster.Member;
import io.atomix.cluster.MemberId;
import io.atomix.cluster.messaging.ClusterCommunicationService;
import io.atomix.cluster.messaging.MessagingService;
import io.atomix.core.Atomix;
import io.atomix.core.election.Leader;
import io.atomix.core.election.LeaderElection;
import io.atomix.core.election.Leadership;
import io.atomix.core.election.LeadershipEvent;
import io.atomix.protocols.raft.MultiRaftProtocol;
import io.atomix.protocols.raft.ReadConsistency;
import io.atomix.utils.net.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FooDispatchLeaderStatefullService {

	@Autowired
	private AtomixService delegate;
	
	@Autowired
	Atomix atomix;
	
	@Autowired
	ObjectMapper jsonMapper;
	
	private Member locaMember;
	private MemberId localMemberId;
	
	final int partitionsCount = 3; 
	private Part[] partitions;
	
	private Executor executor = Executors.newFixedThreadPool(2);
	
	@RequiredArgsConstructor
	private static class Part {
		final int index;
		LeaderElection<String> leaderElection;
	}
	
	@PostConstruct()
	public void init() {
		this.locaMember = atomix.getMembershipService().getLocalMember();
		this.localMemberId = this.locaMember.id(); 
		partitions = new Part[partitionsCount];
		for(int i = 0; i < partitionsCount; i++) {
			val part = new Part(i);
			part.leaderElection = getLeaderElection("part-" + i);
			part.leaderElection.addListener(event -> onPartitionLeadershipEvent(part, event));
			part.leaderElection.run(localMemberId.id());
			partitions[i]  = part;
		}
		
		MessagingService messagingService = atomix.getMessagingService();
		BiFunction<Address, byte[], byte[]> handler_meth1 = (address,msgBytes) -> handleAsMaster_meth1(address,msgBytes); 
		messagingService.registerHandler("meth1", handler_meth1, executor);
		
		ClusterCommunicationService communicationService = atomix.getCommunicationService();
		// messagingService.registerHandler("meth1_event", handler_meth1_event, executor);
		communicationService.subscribe("meth1_event",
				bytes -> fromJsonBytes(bytes, Meth1Event.class),
				event -> handleAsFollower_meth1Event(event), 
				executor);
	}
	
	public FooMeth1ResponseDTO meth1(FooMeth1RequestDTO req) {
		String topic = req.getTopic();
		int partitionIndex = Math.abs(topic.hashCode()) % partitionsCount;
		LeaderElection<String> leaderElection = partitions[partitionIndex].leaderElection;
		Leadership<String> leadership = leaderElection.getLeadership();
		Leader<String> leader = leadership.leader();
		String leaderMemberId = leader.id();
		log.info("meth1 topic:" + topic + " => partitionIndex:" + partitionIndex + " => curr leader: " + leaderMemberId);

		if (localMemberId.id().equals(leaderMemberId)) {
			log.info("curr Leader .. handle request");
			
			FooMeth1ResponseDTO res = doMeth1(req);
			
			return res;
		} else {
			log.info("curr follower .. redispath to leader");
			Member leaderMember = atomix.getMembershipService().getMember(leaderMemberId);
			Address leaderAddress = leaderMember.address();
			MessagingService messagingService = atomix.getMessagingService();
			
			val masterReq = new Meth1MasterRequest(req);
			byte[] masterReqBytes = toJsonBytes(masterReq);
			
			byte[] masterRespBytes = messagingService.sendAndReceive(leaderAddress, "meth1", masterReqBytes).join();
			
			Meth1MasterResponse masterResp = fromJsonBytes(masterRespBytes, Meth1MasterResponse.class);
			FooMeth1ResponseDTO res = masterResp.getRes();
			log.info("return (from leader): " + res.msg);
			return res;
		}
	}

	private <T> byte[] toJsonBytes(T value) {
		try {
			return jsonMapper.writeValueAsBytes(value);
		} catch (JsonProcessingException ex) {
			throw new RuntimeException("", ex);
		}
	}
	private <T> T fromJsonBytes(byte[] value, Class<T> clss) {
		try {
			return jsonMapper.readValue(value, clss);
		} catch (IOException ex) {
			throw new RuntimeException("", ex);
		}
	}
	
	private FooMeth1ResponseDTO doMeth1(FooMeth1RequestDTO req) {
		FooMeth1ResponseDTO res = new FooMeth1ResponseDTO();
		res.msg = " from " + localMemberId + ":" + req.msg;
		
		// also notify all other (followers)
		ClusterCommunicationService communicationService = atomix.getCommunicationService();
		val broadcastEvent = new Meth1Event(req, res);
		communicationService.broadcast("meth1_event", broadcastEvent
				, e -> toJsonBytes(e)
				);
		
		return res;
	}
	
	@Data @NoArgsConstructor @AllArgsConstructor
	public static class Meth1MasterRequest {
		// TOADD authentication userId, ..
		FooMeth1RequestDTO req;
	}
	@Data @NoArgsConstructor @AllArgsConstructor
	public static class Meth1MasterResponse {
		// TOADD time stats (cf also broadcasted msg..)
		FooMeth1ResponseDTO res;
	}
	
	protected byte[] handleAsMaster_meth1(Address fromAddress, byte[] msgRequest) {
		val request = fromJsonBytes(msgRequest, Meth1MasterRequest.class);
		FooMeth1RequestDTO req = request.req;
		log.info("handle as master meth1 req msg:" + req.msg);
		
		FooMeth1ResponseDTO res = doMeth1(req);
		
		val masterResp = new Meth1MasterResponse(res);
		val resBytes = toJsonBytes(masterResp);
		return resBytes;
	}

	@Data @NoArgsConstructor @AllArgsConstructor
	public static class Meth1Event {
		// TOADD authentication userId, ..
		FooMeth1RequestDTO req;
		// response may not be broadcasted... only "side-effects" change
		FooMeth1ResponseDTO res;
	}
	
	protected void handleAsFollower_meth1Event(Meth1Event event) {
		val req = event.req;
		log.info("notifyMeth1_asFollower topic:" + req.topic + " msg:" + req.msg + " , resp:" + event.req.msg);
	}

	LeaderElection<String> getLeaderElection(String election) {
		Atomix atomix = delegate.getAtomix();
		return atomix.<String>leaderElectionBuilder(election)
				  .withProtocol(MultiRaftProtocol.builder()
				    .withReadConsistency(ReadConsistency.LINEARIZABLE)
				    .build())
				  .build();
	}

	protected void onPartitionLeadershipEvent(Part part, LeadershipEvent<String> event) {
		Leadership<String> newLeadership = event.newLeadership();
		Leader<String> leader = newLeadership.leader();
		String leaderId = ((leader != null)? leader.id() : null);
		Leadership<String> oldLeadership = event.newLeadership();
		Leader<String> oldLeader = oldLeadership.leader();
		String oldLeaderId = ((oldLeader != null)? oldLeader.id() : null);
		log.info("partition " + part.index + " leadership event, "
				+ " " + event.topic()
				+ " newLeader:" + leaderId
				+ " (old:" + oldLeaderId + ")"
				);
	}
	
}
