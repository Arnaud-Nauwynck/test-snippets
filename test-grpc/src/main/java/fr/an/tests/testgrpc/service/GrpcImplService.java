package fr.an.tests.testgrpc.service;

import java.io.IOException;
import java.net.InetAddress;

import fr.an.tests.testpgrpc.GreeterGrpc;
import fr.an.tests.testpgrpc.HelloReply;
import fr.an.tests.testpgrpc.HelloRequest;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class GrpcImplService extends GreeterGrpc.GreeterImplBase {

	private final String serverName;

	public GrpcImplService(String serverName) {
		if (serverName == null) {
			serverName = determineHostname();
		}
		this.serverName = serverName;
	}

	@Override
	public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
		String reqName = req.getName();
		log.info("sayHello req.name:" + reqName);
		HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + reqName + ", from " + serverName).build();

		log.info(".. response (onNext+onComplete) sayHello req.name:" + reqName);
		responseObserver.onNext(reply);
		responseObserver.onCompleted();
	}

	private static String determineHostname() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (IOException ex) {
			return "localhost"; // should not occur
		}
	}

}