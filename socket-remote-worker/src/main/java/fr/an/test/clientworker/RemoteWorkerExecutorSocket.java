package fr.an.test.clientworker;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.an.test.serverpool.DataProcessor;

/**
 *
 */
public class RemoteWorkerExecutorSocket {

	
	private static Logger log = LoggerFactory
			.getLogger(RemoteWorkerExecutorSocket.class);
	
	private DataProcessor delegate;
	
	private Socket socket;

	private DataInputStream socketDataInput;
	private DataOutputStream socketDataOutput;
	
	// ------------------------------------------------------------------------
	
	public RemoteWorkerExecutorSocket(Socket socket, DataProcessor delegate) {
		this.delegate = delegate;
		if (socket != null) {
			reInitSocket(socket);
		}
	}

	public void reInitSocket(Socket socket) {
		this.socket = socket;
		try {
			this.socketDataInput = new DataInputStream(socket.getInputStream());
		} catch (IOException ex) {
			throw new RuntimeException("Failed to get socket.inputStream", ex);
		}
		try {
			this.socketDataOutput = new DataOutputStream(socket.getOutputStream());
		} catch (IOException ex) {
			throw new RuntimeException("Failed to get socket.outputStream", ex);
		}
	}
	
	public void disposeSocket() {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				// ignore, no rethrow!
			}
			this.socketDataOutput = null;
			this.socketDataInput = null; 
			this.socket = null;
		}
	}
	

	// ------------------------------------------------------------------------
	
	public void writeRegistrationHeaderToServer() {
		// TODO ...
	}
	
	public void runThreadLoopDispatch() {
		for(;;) {
			String command;
			try {
				command = socketDataInput.readUTF();
			} catch (IOException ex) {
				throw new RuntimeException("Failed runLoopDispatch(): socket.read for request command", ex);
			}
		
			if (command.equals("pingAlive")) {
				handlePingAliveRequest();
			} else if (command.equals("processData")) {
				handleProcessDataRequest();
			} else if (command.equals("displayInfo")) {
				handleDisplayInfoRequest();
			} else if (command.equals("kill")) {
				handleKillRequest();
			}
		}
	}

	// ------------------------------------------------------------------------

	protected void handlePingAliveRequest() {
		try {
			socketDataOutput.writeUTF("OK");
		} catch (IOException ex) {
			String msg = "Failed to write pingAlive response";
			log.info(msg);
			throw new RuntimeException(msg, ex);
		}
	}
	
	protected void handleProcessDataRequest() {
		// Socket binary protocol:
		// => write UTF "processData", UTF userName, UTF data
		// <= read boolean (status), int (durationMillis)
		// if (status)  <= read UTF (result)
		// else if (!status)  <= read UTF (serverErrorCode), UTF (serverStackString) 
		String userName;
		String data;
		try {
			userName = socketDataInput.readUTF();
			data = socketDataInput.readUTF();
		} catch (IOException ex) {
			String msg = "Failed handleProcessData(): error in socket.read processData request";
			log.error(msg, ex);
			throw new RuntimeException(msg, ex);
		}
		
		String returnedRes = null;
		Exception returnedException = null; 
		int durationMillis;

		long timeBefore = System.currentTimeMillis();
		try {
			returnedRes = delegate.processData(userName, data);
			
			durationMillis = (int) (System.currentTimeMillis() - timeBefore);
		} catch(Exception ex) {
			// underlying exception => no rethrow!!! write response as applicative error
			returnedException = ex;
			durationMillis = (int) (System.currentTimeMillis() - timeBefore);
		}

		try {
			if (returnedException == null) {
				// ok
				socketDataOutput.writeUTF("OK");
				socketDataOutput.writeInt(durationMillis);
				socketDataOutput.writeUTF(returnedRes);
			} else {
				// error code
				StringWriter exWriter = new StringWriter();
				returnedException.printStackTrace(new PrintWriter(exWriter));
				String serverStackTraceString = exWriter.toString();
				
				socketDataOutput.writeUTF("ERROR");
				socketDataOutput.writeInt(durationMillis);
				socketDataOutput.writeUTF(returnedException.getMessage());
				socketDataOutput.writeUTF(serverStackTraceString);
			}
		} catch (IOException ex) {
			String msg = "Failed handleProcessDataRequest(): error in socket.write processData response";
			log.error(msg, ex);
			throw new RuntimeException(msg, ex);
		}
	}
	
	public void handleDisplayInfoRequest() {
		String args;
		try {
			args = socketDataInput.readUTF();
		} catch (IOException ex) {
			String msg = "Failed handleKillRequest(): error in socket.read processData request";
			log.error(msg, ex);
			throw new RuntimeException(msg, ex);
		}
		
		// TODO use args.. to display specific info
		StringWriter res = new StringWriter();
		res.append("displayInfo for '" + args + "':\n");
		res.append("system properties= " + System.getProperties());
		
		try {
			socketDataOutput.writeUTF("OK");
			socketDataOutput.writeUTF(res.toString());
		} catch (IOException ex) {
			String msg = "Failed handleKillRequest(): error in socket.write processData response";
			log.error(msg, ex);
			throw new RuntimeException(msg, ex);
		}
	}
	
	public void handleKillRequest() {
		String args;
		try {
			args = socketDataInput.readUTF();
		} catch (IOException ex) {
			String msg = "Failed handleKillRequest(): error in socket.read processData request";
			log.error(msg, ex);
			throw new RuntimeException(msg, ex);
		}
		try {
			socketDataOutput.writeUTF("OK");
		} catch (IOException ex) {
			String msg = "Failed handleKillRequest(): error in socket.write processData response";
			log.error(msg, ex);
			throw new RuntimeException(msg, ex);
		}
		
		// close all, then "suicide" by throwing Exception..
		try {
			socketDataOutput.flush();
		} catch (IOException e) {
			// ignore, no rethrow!
		}
		disposeSocket();
		
		// "suicide" by throwing Exception..
		throw new RuntimeException("received killed command '" + args + "'");
	}
	
}
