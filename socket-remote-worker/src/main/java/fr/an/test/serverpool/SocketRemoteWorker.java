package fr.an.test.serverpool;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class SocketRemoteWorker extends DataProcessor {
	
	private static Logger log = LoggerFactory.getLogger(SocketRemoteWorker.class);
	
	private Socket socket;

	private String name;
	
	private DataInputStream socketDataInput;
	private DataOutputStream socketDataOutput;
	
	// ------------------------------------------------------------------------
	
	public SocketRemoteWorker(Socket socket) {
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

	// ------------------------------------------------------------------------

	public boolean isClosed() {
		boolean res = socket == null || socket.isClosed();
		return res;
	}

	public boolean pingAlive() {
		boolean res = socket != null && !socket.isClosed() 
				// ??? && !socketDataOutput.isClosed()
				;
		if (res) {
			try {
				res = sendPingAlive();
			} catch(Exception ex) {
				res = false;
			}
		}
		return res;
	}

	
	public boolean sendPingAlive() {
		boolean res = true;
		if (socket.isClosed()) {
			res = false;
		} else {
			try {
				socketDataOutput.writeUTF("pingAlive");
			} catch (IOException ex) {
				log.info("Failed to write pingAlive request .. assume connection is dead (ex:" + ex.getMessage() + ") => return isAlive():false");
				res = false;
			}
			if (res) {
				try {
					String response = socketDataInput.readUTF();
					if (response.equals("OK")) {
						res = true;
					} else if (response.equals("Exiting")) {
						res = false;
					} else {
						String msg = "Failed pingAlive(): protocol error, expecting processData response 'OK' or 'Exiting', got '" + response + "'";
						log.error(msg);
						throw new RuntimeException(msg);
					}
				} catch (IOException ex) {
					log.info("Failed to read pingAlive response .. assume connection is dead (ex:" + ex.getMessage() + ") => return isAlive():false");
					res = false;
				}
			}
		}
		return res;
	}

	
	@Override
	public String processData(String userName, String data) {
		String res;
		// Socket binary protocol:
		// => write UTF "processData", UTF userName, UTF data
		// <= read boolean (status), int (durationMillis)
		// if (status)  <= read UTF (result)
		// else if (!status)  <= read UTF (serverErrorCode), UTF (serverStackString) 
		try {
			socketDataOutput.writeUTF("processData");
			socketDataOutput.writeUTF(userName);
			socketDataOutput.writeUTF(data);

			socketDataOutput.flush();
		} catch (IOException ex) {
			String msg = "Failed processData(): error in socket.write processData request";
			log.error(msg, ex);
			throw new RuntimeException(msg, ex);
		}
		
		try {
			String response = socketDataInput.readUTF();
			int durationMillis = socketDataInput.readInt();
			if (response.equals("OK")) {
				res = socketDataInput.readUTF();
			} else if (response.equals("ERROR")) {
				String serverErrorCode = socketDataInput.readUTF();
				String serverStackString = socketDataInput.readUTF();
				String msg = "Failed processData() on socket RemoteWorker " + name 
						+ " : serverErrorCode=" + serverErrorCode + " serverErrorStackString=" + serverStackString;
				throw new RemoteWorkerExecutionException(msg, null,
						durationMillis, serverErrorCode, serverStackString);
			} else {
				// protocol error
				String msg = "Failed processData(): protocol error, expecting processData response 'OK' or 'ERROR', got '" + response + "'";
				log.error(msg);
				throw new RuntimeException(msg);
			}
		} catch (IOException ex) {
			String msg = "Failed processData(): error in socket.read processData response";
			log.error(msg, ex);
			throw new RuntimeException(msg, ex);
		}
		
		return res;
	}
	
	public String displayInfo(String args) {
		String res;
		try {
			socketDataOutput.writeUTF("displayInfo");
			socketDataOutput.writeUTF(args);

			socketDataOutput.flush();
		} catch (IOException ex) {
			String msg = "Failed processDisplayInfo(): error in socket.write processData request";
			log.error(msg, ex);
			throw new RuntimeException(msg, ex);
		}
		try {
			String response = socketDataInput.readUTF();
			int durationMillis = socketDataInput.readInt();
			if (response.equals("OK")) {
				res = socketDataInput.readUTF();
			} else if (response.equals("ERROR")) {
				String serverErrorCode = socketDataInput.readUTF();
				String serverStackString = socketDataInput.readUTF();
				String msg = "Failed processDisplayInfo() on socket RemoteWorker " + name 
						+ " : serverErrorCode=" + serverErrorCode + " serverErrorStackString=" + serverStackString;
				throw new RemoteWorkerExecutionException(msg, null,
						durationMillis, serverErrorCode, serverStackString);
			} else {
				// protocol error
				String msg = "Failed processDisplayInfo(): protocol error, expecting processData response 'OK' or 'ERROR', got '" + response + "'";
				log.error(msg);
				throw new RuntimeException(msg);
			}
		} catch (IOException ex) {
			String msg = "Failed processDisplayInfo(): error in socket.read processData response";
			log.error(msg, ex);
			throw new RuntimeException(msg, ex);
		}
		return res;
	}
	
	public String kill(String args) {
		String res;
		try {
			socketDataOutput.writeUTF("kill");
			socketDataOutput.writeUTF(args);

			socketDataOutput.flush();
		} catch (IOException ex) {
			String msg = "Failed processKill(): error in socket.write processData request";
			log.error(msg, ex);
			throw new RuntimeException(msg, ex);
		}
		try {
			String response = socketDataInput.readUTF();
			int durationMillis = socketDataInput.readInt();
			if (response.equals("OK")) {
				res = socketDataInput.readUTF();
			} else if (response.equals("ERROR")) {
				String serverErrorCode = socketDataInput.readUTF();
				String serverStackString = socketDataInput.readUTF();
				String msg = "Failed processKill() on socket RemoteWorker " + name 
						+ " : serverErrorCode=" + serverErrorCode + " serverErrorStackString=" + serverStackString;
				throw new RemoteWorkerExecutionException(msg, null,
						durationMillis, serverErrorCode, serverStackString);
			} else {
				// protocol error
				String msg = "Failed processKill(): protocol error, expecting processData response 'OK' or 'ERROR', got '" + response + "'";
				log.error(msg);
				throw new RuntimeException(msg);
			}
		} catch (IOException ex) {
			String msg = "Failed processKill(): error in socket.read processData response";
			log.error(msg, ex);
			throw new RuntimeException(msg, ex);
		}
		return res;
	}

}
