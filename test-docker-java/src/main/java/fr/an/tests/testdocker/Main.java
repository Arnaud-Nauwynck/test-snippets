package fr.an.tests.testdocker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import com.github.dockerjava.core.command.WaitContainerResultCallback;

public class Main {
	
	private static final Logger LOG = LoggerFactory.getLogger(Main.class);
	
	/**
	 * a simple test ro run
	 * <PRE>
	 * docker run -it debian bash -c "echo test..; sleep 5; echo test docker!"
	 * </PRE>
	 */
	public static void main(String[] args) {
		LOG.info("start test-docker");
		DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
//			    .withDockerHost("tcp://my-docker-host.tld:2376")
			    .withDockerTlsVerify(false)
//			    .withDockerCertPath("/home/user/.docker/certs")
//			    .withDockerConfig("/home/user/.docker")
//			    .withApiVersion("1.23")
//			    .withRegistryUrl("https://index.docker.io/v1/")
//			    .withRegistryUsername("dockeruser")
//			    .withRegistryPassword("ilovedocker")
//			    .withRegistryEmail("dockeruser@github.com")
			    .build();
		DockerClient dockerClient = DockerClientBuilder.getInstance(config).build();
		
		CreateContainerResponse container = dockerClient.createContainerCmd("debian")
				.withCmd("bash", "-c", "echo test..; sleep 5; echo test docker!")
				.exec();
		String containerId = container.getId();
		LOG.info("createContainer => " + containerId);
		
		dockerClient.startContainerCmd(containerId).exec();
        
		dockerClient.logContainerCmd(containerId)
			.withStdOut(true)
			.withStdErr(true)
			.withTailAll()
			.exec(new LogContainerResultCallback() {
				@Override
				public void onNext(Frame item) {
					System.out.println("(docker) " + item);
				}
			});

		WaitContainerResultCallback waitRes = dockerClient.waitContainerCmd(containerId)
        		.exec(new WaitContainerResultCallback());
		int exitcode = waitRes.awaitStatusCode();
        LOG.info("docker exitCode:" + exitcode);
        
		LOG.info("finished");
	}
}
