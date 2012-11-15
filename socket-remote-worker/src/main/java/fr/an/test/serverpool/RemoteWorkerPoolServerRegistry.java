package fr.an.test.serverpool;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteWorkerPoolServerRegistry {
	
	private static Logger log = LoggerFactory.getLogger(RemoteWorkerPoolServerRegistry.class);
	
	private RemoteWorkerPool pool;
	
	private int socketPortNumber;

	private ServerSocket currServerSocket;
	private Thread currServerRegistryThread;
	
	private Object lock = new Object();
	private boolean threadShouldStop = false;
	
	// ------------------------------------------------------------------------

	public RemoteWorkerPoolServerRegistry(RemoteWorkerPool pool, int socketPortNumber) {
		this.pool = pool;
		this.socketPortNumber = socketPortNumber; 
	}
	
	// ------------------------------------------------------------------------

	public void start() {
		if (currServerSocket != null) {
			log.warn("already started .. do nothing!");
			return;
		}
		
		try {
			this.currServerSocket = new ServerSocket(socketPortNumber);
		} catch (IOException ex) {
			throw new RuntimeException("Failed start() : can not open ServerSocket!", ex);
		}
		this.currServerRegistryThread = new Thread(new Runnable() {
			public void run() {
				doRunServerSocketThread();
			}
		});
		this.currServerRegistryThread.start();
	}

	public void stop() {
		if (currServerSocket == null) {
			log.warn("already stoped .. do nothing!");
			return;
		}
		try {
			currServerSocket.close();
		} catch(Exception ex) {
		}
		this.currServerSocket = null;
		
	}
	
	// ------------------------------------------------------------------------
	
	private void doRunServerSocketThread() {
		try {
			currServerSocket.setSoTimeout(10000);
		} catch (SocketException ex) {
			log.error("Failed serverSocket.setSoTimeout() .. ignore, no rethrow??!", ex);
		}
		
		for(;;) {
			Socket clientSocket = null;
			try {
				clientSocket = currServerSocket.accept();
			} catch(java.net.SocketTimeoutException ex) {
				// timeout .. loop again if thread should continue
			} catch (IOException ex) {
				log.warn("serversocket.accept() failed ... ignore, no rethrow!", ex);
			}
						
			if (clientSocket != null) {
				handleNewClientSocket(clientSocket);
			}
			
			
			synchronized(lock) {
				if (threadShouldStop) {
					log.debug("detected threadShouldStop => stopping thread");
					this.currServerRegistryThread = null; // stop infinitte loop = exit "this" thread
					break;
				}
			}
		}
	}

	private void handleNewClientSocket(Socket clientSocket) {
		SocketAddress remoteSocketAddress = clientSocket.getRemoteSocketAddress();
		String workerName = "SocketWorker-" + remoteSocketAddress; 
		log.info("handleNewClientSocket from " + remoteSocketAddress);
		
		DataProcessor worker = new SocketRemoteWorker(clientSocket);
		
		// TODO read socket header..
		
		pool.addPooledRemoteWorker(workerName, worker);
	}
}
