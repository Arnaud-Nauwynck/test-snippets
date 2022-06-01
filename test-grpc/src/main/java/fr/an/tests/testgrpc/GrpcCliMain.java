package fr.an.tests.testgrpc;

import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

import com.google.common.util.concurrent.ListenableFuture;
import com.salesforce.grpc.contrib.MoreFutures;

import fr.an.tests.testpgrpc.GreeterGrpc;
import fr.an.tests.testpgrpc.HelloReply;
import fr.an.tests.testpgrpc.HelloRequest;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GrpcCliMain {

	GreeterGrpc.GreeterBlockingStub blockingStub;
	GreeterGrpc.GreeterFutureStub asyncStub;
	GreeterGrpc.GreeterStub stub;
	
	public static void main(String[] args) {
		GrpcCliMain app = new GrpcCliMain();
		app.parseArgs(args);
		try {
			app.run();
		} catch (Exception ex) {
			log.error("Failed", ex);
		}
	}

	public void parseArgs(String[] args) {
		String host = "localhost";
		int port = 50051;
		Channel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
		this.blockingStub = GreeterGrpc.newBlockingStub(channel);
		this.asyncStub = GreeterGrpc.newFutureStub(channel);
		this.stub = GreeterGrpc.newStub(channel);
		log.info("call to channel.. " + channel);
	}

	public void run() throws Exception {
		log.info("testing input => output");
		sayHello_blockingStub();
		sayHello_asyncStub_get();
		sayHello_stub();
		
		log.info("testing input => stream output");
		sayHelloOutStream_blockingStub();
		sayHelloOutStream_async();
		
		log.info("testing stream input => single output");
		sayHelloInStream_stub();

		log.info("testing stream input => stream output");
		sayHelloInStreamOutSteam_stub();
		
	}

	public void sayHello_blockingStub() throws Exception {
		HelloRequest helloReq = HelloRequest.newBuilder().setName("you").build();
		
		HelloReply reply = blockingStub.sayHello(helloReq);
		
		log.info("sayHello => " + reply.getMessage());
	}

	public void sayHello_asyncStub_get() throws Exception {
		HelloRequest helloReq = HelloRequest.newBuilder().setName("you").build();
		ListenableFuture<HelloReply> respListenableFuture = asyncStub.sayHello(helloReq);
		
		// wrap in java.util.CompletableFuture, use .thenApply(), thenCombine(), ...  
		CompletableFuture<HelloReply> completableFuture = MoreFutures.toCompletableFuture(respListenableFuture);
		CompletableFuture<HelloReply> resFuture = completableFuture// .thenCombine(..) // combine with other futures
			.thenApply(x -> x); // transform when ready
		
		HelloReply reply = resFuture.get(); // (generally don't do that) async to blocking!
		log.info("sayHello (async block) => " + reply.getMessage());
	}

	public void sayHello_stub() throws Exception {
		HelloRequest helloReq = HelloRequest.newBuilder().setName("you").build();
		CountDownLatch latch = new CountDownLatch(1);
		stub.sayHello(helloReq, new StreamObserver<HelloReply>() {
			@Override
			public void onNext(HelloReply reply) {
				log.info("sayHello .. response => " + reply.getMessage());
			}
			@Override
			public void onCompleted() {
				log.info("sayHello .. onComplete");
				latch.countDown();
			}
			@Override
			public void onError(Throwable t) {
				log.info("sayHello .. onError", t);
				latch.countDown();
			}
		});
		latch.await();
		log.info(".. done wait sayHello");
	}

	public void sayHelloOutStream_blockingStub() throws Exception {
		log.info("sayHelloOutStream_blockingStub");
		HelloRequest helloReq = HelloRequest.newBuilder().setName("you").build();
		Iterator<HelloReply> resIter = blockingStub.sayHelloOutStream(helloReq);
		log.info(".. receive response(s):" + resIter);
		for(; resIter.hasNext();) {
			HelloReply resElt = resIter.next();
			log.info("receive server resp:" + resElt.getMessage());
		}
		log.info(".. done iterate on response");
	}

	public void sayHelloOutStream_async() throws Exception {
		log.info("sayHelloOutStream_async");
		HelloRequest helloReq = HelloRequest.newBuilder().setName("you").build();
		CountDownLatch latch = new CountDownLatch(1);
		stub.sayHelloOutStream(helloReq, new StreamObserver<HelloReply>() {
			int respCount = 0;
			
			@Override
			public void onNext(HelloReply reply) {
				int count = respCount++;
				log.info("sayHelloOutStream .. response[" + count + "] => " + reply.getMessage());
			}
			
			@Override
			public void onCompleted() {
				log.info("sayHelloOutStream .. onComplete");
				latch.countDown();
			}

			@Override
			public void onError(Throwable t) {
				log.info("sayHelloOutStream .. onError", t);
				latch.countDown();
			}
		});
		latch.await();
		log.info(".. done wait sayHelloOutStream");
	}

	
	
	public void sayHelloInStream_stub() throws Exception {
		log.info("sayHelloInStream_stub");
		CountDownLatch latch = new CountDownLatch(1);
		StreamObserver<HelloRequest> req$ = stub.sayHelloInStream(new StreamObserver<HelloReply>() {

			@Override
			public void onNext(HelloReply reply) {
				log.info("sayHelloInStream .. server onNext reply:" + reply.getMessage());
			}

			@Override
			public void onCompleted() {
				log.info("sayHelloInStream .. server onCompleted");
				latch.countDown();
			}

			@Override
			public void onError(Throwable t) {
				log.info("sayHelloInStream .. server onError", t);
				latch.countDown();
			}
		});
		
		HelloRequest helloReq = HelloRequest.newBuilder().setName("you").build();
		log.info("client send 5x request");
		for(int i = 0; i < 5; i++) {
			log.info("client send onNext");
			req$.onNext(helloReq);
		}

		log.info("await server complete");
		latch.await();
		// continue sending anyway..
		
		log.info("client send 5x (sleep+request)");
		for(int i = 0; i < 5; i++) {
			sleep(1000);
			log.info("client send onNext");
			req$.onNext(helloReq);
		}
		log.info("client send onCompleted");
		req$.onCompleted();


		log.info("");
	}

	public void sayHelloInStreamOutSteam_stub() throws Exception {
		log.info("sayHelloInStreamOutStream .. ");
		CountDownLatch latch = new CountDownLatch(1);
		StreamObserver<HelloRequest> req$ = stub.sayHelloInStreamOutStream(new StreamObserver<HelloReply>() {

			@Override
			public void onNext(HelloReply reply) {
				log.info("sayHelloInStreamOutStream .. server onNext reply:" + reply.getMessage());
			}

			@Override
			public void onCompleted() {
				log.info("sayHelloInStreamOutStream .. server onCompleted");
				latch.countDown();
			}

			@Override
			public void onError(Throwable t) {
				log.info("sayHelloInStreamOutStream .. server onError", t);
				latch.countDown();
			}
		});

		HelloRequest helloReq = HelloRequest.newBuilder().setName("you").build();
		log.info("client send 5x request");
		for(int i = 0; i < 5; i++) {
			log.info("client send onNext");
			req$.onNext(helloReq);
		}

		log.info("client send 5x (sleep+request)");
		for(int i = 0; i < 5; i++) {
			sleep(1000);
			log.info("client send onNext");
			req$.onNext(helloReq);
		}
		log.info("client send onCompleted");
		req$.onCompleted();
		
		log.info("await server complete");
		latch.await();

		log.info(".. sayHelloInStreamOutStream");
	}

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}

}