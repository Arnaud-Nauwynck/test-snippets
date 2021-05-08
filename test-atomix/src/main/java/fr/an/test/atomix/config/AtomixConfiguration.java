package fr.an.test.atomix.config;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.an.test.atomix.config.AppConfigParam.AtomicConfigParam;
import io.atomix.cluster.Node;
import io.atomix.cluster.discovery.BootstrapDiscoveryProvider;
import io.atomix.core.Atomix;
import io.atomix.protocols.raft.partition.RaftPartitionGroup;
import lombok.val;

@Configuration
public class AtomixConfiguration {

	@Autowired
	AppConfigParam appParam;

	@Bean
	public Atomix atomix() {
		AtomicConfigParam atomixParam = appParam.getAtomix();
		
		List<Node> atomixBootstrapNodes = map(atomixParam.getBootstrapNodes(), node -> Node.builder()
				.withId(node.getMemberId())
				.withHost(node.getHostname())
				.withPort(node.getPort())				
				.build());
		val bootstrapDiscovery = BootstrapDiscoveryProvider.builder()
			.withNodes(atomixBootstrapNodes);
		List<String> members = map(atomixParam.getBootstrapNodes(), node -> node.getMemberId());
		
		return Atomix.builder()
			  .withMemberId(atomixParam.getMemberId())
			  .withHost(atomixParam.getHostname())
			  .withPort(atomixParam.getPort())
			  .withMembershipProvider(bootstrapDiscovery.build())
			  .withManagementGroup(RaftPartitionGroup.builder("system")
			    .withNumPartitions(1)
			    .withMembers(members)
			    .build())
			  .withPartitionGroups(RaftPartitionGroup.builder("raft")
			    .withPartitionSize(3)
			    .withNumPartitions(3)
			    .withMembers(members)
			    .build())
			  .build();
	}
	
	public static <T,TDest> List<TDest> map(Collection<T> srcs, Function<T,TDest> mapFunc) {
		return srcs.stream().map(mapFunc).collect(Collectors.toList());
	}
}
