package fr.an.tests.testgrpc;

import fr.an.tests.testpgrpc.GreeterGrpc;
import fr.an.tests.testpgrpc.HelloReply;
import fr.an.tests.testpgrpc.HelloRequest;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GrpcCliMain {

	GreeterGrpc.GreeterBlockingStub blockingStub;
	GreeterGrpc.GreeterFutureStub asyncStub;

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
		log.info("call to channel.. " + channel);
	}

	public void run() throws Exception {
		sayHello();
		sayHello_asyncBlock();
	}

	public void sayHello() throws Exception {
		HelloRequest helloReq = HelloRequest.newBuilder().setName("you").build();
		HelloReply reply = blockingStub.sayHello(helloReq);
		log.info("sayHello => " + reply);
	}

	public void sayHello_asyncBlock() throws Exception {
	    HelloRequest helloReq = HelloRequest.newBuilder().setName("you").build();
	    HelloReply reply = asyncStub.sayHello(helloReq).get();
		log.info("sayHello (async block) => " + reply);
	  }

}