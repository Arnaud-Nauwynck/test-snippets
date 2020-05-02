package fr.an.tests.testgrpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.health.v1.HealthCheckResponse.ServingStatus;
import io.grpc.protobuf.services.ProtoReflectionService;
import io.grpc.services.HealthStatusManager;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import fr.an.tests.testgrpc.service.GrpcImplService;

public class GrpcServerMain {

	public static void main(String[] args) throws IOException, InterruptedException {
		int port = 50051;
		String hostname = null;
		if (args.length >= 1) {
			try {
				port = Integer.parseInt(args[0]);
			} catch (NumberFormatException ex) {
				System.err.println("Usage: [port [hostname]]");
				System.err.println("");
				System.err.println("  port      The listen port. Defaults to " + port);
				System.err.println("  hostname  The name clients will see in greet responses. ");
				System.err.println("            Defaults to the machine's hostname");
				System.exit(1);
			}
		}
		if (args.length >= 2) {
			hostname = args[1];
		}
		HealthStatusManager health = new HealthStatusManager();
		final Server server = ServerBuilder.forPort(port) //
				.addService(new GrpcImplService(hostname)) //
				.addService(ProtoReflectionService.newInstance()) //
				.addService(health.getHealthService()) // 
				.build();
		
		System.out.println("Listening on port " + port);
		server.start();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				server.shutdown();
				try {
					if (!server.awaitTermination(30, TimeUnit.SECONDS)) {
						server.shutdownNow();
						server.awaitTermination(5, TimeUnit.SECONDS);
					}
				} catch (InterruptedException ex) {
					server.shutdownNow();
				}
			}
		});

		health.setStatus("", ServingStatus.SERVING);
		server.awaitTermination();
	}

}
