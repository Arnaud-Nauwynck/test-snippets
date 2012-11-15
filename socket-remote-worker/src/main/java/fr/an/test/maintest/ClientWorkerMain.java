package fr.an.test.maintest;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import fr.an.test.clientworker.RemoteWorkerExecutorSocket;
import fr.an.test.serverpool.DataProcessor;

public class ClientWorkerMain {

	private String serverRegistryHost = "localhost";
	private int serverRegistryPort = 6789;
	
	private DataProcessor dataProcessor = new DummyDataProcessor();

	// ------------------------------------------------------------------------

	public ClientWorkerMain() {
	}
	
	public static void main(String[] args) {
		try {
			ClientWorkerMain app = new ClientWorkerMain();
			app.parseArgs(args);
			app.runApp();

			System.out.println("Finished .. exiting(0)");
			System.exit(0);
		} catch(Exception ex) {
			System.err.println("Failed .. exiting(-1)");
			ex.printStackTrace(System.err);
			System.exit(-1);
		}
	}

	// ------------------------------------------------------------------------


	public void parseArgs(String[] args) {
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.equals("--registerServerHost") || arg.equals("-s")) {
				serverRegistryHost = args[++i];
			} else if (arg.equals("--registerServerPort") || arg.equals("-p")) {
				serverRegistryPort = Integer.parseInt(args[++i]);
			} else {
				throw new IllegalArgumentException("unrecognise arg '" + arg + "'");
			}
		}		
	}
		
	public void runApp() {
		
		
		RemoteWorkerExecutorSocket sockerHandler = new RemoteWorkerExecutorSocket(null, dataProcessor);

		boolean needReconnectToServer = false;
		
		for(;;) {
			if (needReconnectToServer) {
				for (int retryCount = 0; retryCount < 3; retryCount++) {
					try { 
						Socket socket = openServerRegistrySocket();

						sockerHandler.reInitSocket(socket);
						sockerHandler.writeRegistrationHeaderToServer();

						needReconnectToServer = false;
						break;
					} catch(Exception ex) {
						System.out.println("Failed to connect to server ... retry " + retryCount + " / " + 3);
						try {
							Thread.sleep(1*1000);
						} catch(InterruptedException ex2) {
							// ignore, no rethrow!
						}
					}
				}
				if (needReconnectToServer) {
					System.out.println("Failed to connect => sleep few seconds");
					try {
						Thread.sleep(30*1000);
					} catch(InterruptedException ex) {
						// ignore, no rethrow!
					}
					continue;
				}
			}

			try {
				
				// block curr thread forever...
				sockerHandler.runThreadLoopDispatch();
				
			} catch(Exception ex) {
				// connection lost with server => try reconnecting ...
				sockerHandler.disposeSocket();
				needReconnectToServer = true;
			}
			
		}
	}

	private Socket openServerRegistrySocket() {
		Socket socket;
		try {
			socket = new Socket(serverRegistryHost, serverRegistryPort);
		} catch (UnknownHostException ex) {
			throw new RuntimeException("Failed runApp() : cannot socket.open() " 
					+ "to register self worker into registryServer hostname '" + serverRegistryHost + "', port:" + serverRegistryPort, ex);
		} catch (IOException ex) {
			throw new RuntimeException("Failed runApp() : cannot socket.open() " 
					+ "to register self worker into registryServer hostname '" + serverRegistryHost + "', port:" + serverRegistryPort, ex);
		}
		System.out.println("connected to server");
		return socket;
	}

	
		
}
