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

	@Override
	public void sayHelloOutStream(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
		String reqName = req.getName();
		log.info("sayHello req.name:" + reqName);
		HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + reqName + ", from " + serverName).build();

		log.info(".. response 5x onNext + 5x (sleep + onNext) + onComplete) sayHello req.name:" + reqName);
		for(int i = 0; i < 5; i++) {
			responseObserver.onNext(reply);
		}
		for(int i = 0; i < 5; i++) {
			sleep(1000);
			responseObserver.onNext(reply);
		}
		
		responseObserver.onCompleted();
	}

	/**
	 * <pre>
	 * send * request stream =&gt; receive 1 response
	 * </pre>
	 */
	@Override
	public StreamObserver<HelloRequest> sayHelloInStream(
			StreamObserver<HelloReply> resp$) {
		// wait 5 request, then respond
		return new StreamObserver<HelloRequest>() {
			int reqCount = 0;
			@Override
			public void onNext(HelloRequest req) {
				reqCount++;
				String reqName = req.getName();
				if (reqCount < 5) {
					log.info("received client request[" + reqCount + "]" + reqName + " => wait " + (5-reqCount)+ " more request(s) .. ");
				} else {
					HelloReply reply = HelloReply.newBuilder().setMessage("Hello request[" + reqCount + "]" + reqName).build();
					log.info("received client request[" + reqCount + "]" + reqName + " => respond ");
					resp$.onNext(reply);
					resp$.onCompleted();
				}
			}

			@Override
			public void onCompleted() {
				log.info("received onComplete => send onComplete");
				resp$.onCompleted();
			}

			@Override
			public void onError(Throwable ex) {
				log.error("received Failure (from client?) => send onError", ex);
				resp$.onError(new RuntimeException("fail error send", ex));
			}
		};
	}

	/**
	 * <pre>
	 * send * request stream =&gt; receive * responses stream
	 * </pre>
	 */
	@Override
	public StreamObserver<HelloRequest> sayHelloInStreamOutStream(
			StreamObserver<HelloReply> resp$) {
		return new StreamObserver<HelloRequest>() {
			int reqCount = 0;
			@Override
			public void onNext(HelloRequest req) {
				reqCount++;
				String reqName = req.getName();
				if ((reqCount % 2) == 0) {
					log.info("received client request[" + reqCount + "]" + reqName + " => wait next req");
				} else {
					HelloReply reply = HelloReply.newBuilder().setMessage("Hello request[" + reqCount + "]" + reqName).build();
					log.info("received client request[" + reqCount + "]" + reqName + " => respond ");
					resp$.onNext(reply);
				}
			}

			@Override
			public void onCompleted() {
				log.info("received onComplete => send onComplete");
				resp$.onCompleted();
			}

			@Override
			public void onError(Throwable ex) {
				log.error("received Failure (from client?) => send onError", ex);
				resp$.onError(new RuntimeException("fail error send", ex));
			}
		};

	}

	private static String determineHostname() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (IOException ex) {
			return "localhost"; // should not occur
		}
	}

	private void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch(Exception ex) {
		}
	}

}