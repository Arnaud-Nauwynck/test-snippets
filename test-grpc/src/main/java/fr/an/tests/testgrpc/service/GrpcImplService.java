package fr.an.tests.testgrpc.service;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.InetAddress;

import fr.an.tests.testpgrpc.GreeterGrpc;
import fr.an.tests.testpgrpc.HelloReply;
import fr.an.tests.testpgrpc.HelloRequest;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
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
	public void sayHello_2(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
		String reqName = req.getName();
		log.info("sayHello req.name:" + reqName);
		HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + reqName + ", from " + serverName).build();

		log.info(".. response (onNext+onComplete) sayHello req.name:" + reqName);
		responseObserver.onNext(reply);
		responseObserver.onCompleted();
	}
	@AllArgsConstructor
	public static class FooDTO {
		String message;
	}
	public interface FooService {
		FooDTO saveHello(FooDTO src);
	}
	 @Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
	 @Retention(RetentionPolicy.RUNTIME)
	 @Documented
	 public @interface Autowired {
	 }
	 
	@Autowired
	FooService delegateXAService;
	
	@Override
	public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
		// Step 1/3:
		// validity check, extract/convert gRPC Request to internal classes (internal DTO)  
		long startTime = System.currentTimeMillis();
		String reqName = req.getName();
		log.info("sayHello name:" + reqName);
		FooDTO inDto = new FooDTO(reqName);
		
		// Step 2/3:
		// delegate to transactional service  (SOLID principle)
		FooDTO tmpres = delegateXAService.saveHello(inDto);
		
		// Step 3/3:
		// convert internal DTO to gRPC response, marshall response
		long millis = System.currentTimeMillis() - startTime;
		log.info(".. done sayHello, took " + millis);
		HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + tmpres.message).build();
		responseObserver.onNext(reply);
		responseObserver.onCompleted();
	}
	
	 @Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
	 @Retention(RetentionPolicy.RUNTIME)
	 @Documented
	 public @interface XXXProtocolMapping {
		 String path();
	 }
	
	 @Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
	 @Retention(RetentionPolicy.RUNTIME)
	 @Documented
	 public @interface XXXRequestBody {
	 }
	 @Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
	 @Retention(RetentionPolicy.RUNTIME)
	 @Documented
	 public @interface XXXResponse {
	 }
	 
	 interface XXXOutputResponse {
		 XXXOutputResponse status(int status);
		 XXXOutputResponse addHeader(String h, String v);
		 XXXOutputResponse body(Object obj);
	 }
	 
	@XXXProtocolMapping(path="/sayHello")
	public void sayHello(
			@XXXRequestBody HelloRequest req, 
			@XXXResponse XXXOutputResponse out) {
		// Step 1/3:
		// validity check, extract/convert XXX Request to internal classes (internal DTO)  
		long startTime = System.currentTimeMillis();
		String reqName = req.getName();
		log.info("sayHello name:" + reqName);
		FooDTO inDto = new FooDTO(reqName);
		
		// Step 2/3:
		// delegate to transactional service  (SOLID principle)
		FooDTO tmpres = delegateXAService.saveHello(inDto);
		
		// Step 3/3:
		// convert internal DTO to XXX response, marshall response
		long millis = System.currentTimeMillis() - startTime;
		log.info(".. done sayHello, took " + millis);
		HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + tmpres.message).build();
		out.status(200).addHeader("header1", "value1").body(reply);
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