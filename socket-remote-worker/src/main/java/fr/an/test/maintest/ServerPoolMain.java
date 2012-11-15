package fr.an.test.maintest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import fr.an.test.serverpool.RemoteWorkerPool;
import fr.an.test.serverpool.RemoteWorkerPoolServerRegistry;

public class ServerPoolMain {

	private int serverRegistryPort = 6789;
	
	private RemoteWorkerPool pool;
	private RemoteWorkerPoolServerRegistry registry;
	
	// ------------------------------------------------------------------------

	public ServerPoolMain() {
	}
	
	public static void main(String[] args) {
		try {
			ServerPoolMain app = new ServerPoolMain();
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
			if (arg.equals("--registerServerPort") || arg.equals("-p")) {
				serverRegistryPort = Integer.parseInt(args[++i]);
			} else {
				throw new IllegalArgumentException("unrecognise arg '" + arg + "'");
			}
		}		
	}
		
	public void runApp() {
		this.pool = new RemoteWorkerPool(); 
		this.registry = new RemoteWorkerPoolServerRegistry(pool, serverRegistryPort);
		
		registry.start(); // => start Thread!
		
		// join launched server thread.. do not exit main!
		// or use System.in for commands
		readInteractiveCommand();
	}

	public void readInteractiveCommand() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		for(;;) {
			String command  = prompt(reader, "enter command (processData | foreach-pingAlive | foreach-displayInfo | foreach-kill)> ");
			if (command == null) {
				System.out.println("OK, exiting");
				break;
			}
			
			if (command.equalsIgnoreCase("processData")) {
				final String userName = "testuser"; // prompt(reader, "enter userName>");
				final String data = prompt(reader, "enter data>");
				
				System.out.println("launch in new Thread... pool.executeProcessData()");
				new Thread() {
					public void run() {
						try {
							String res = pool.executeProcessData(userName, data);
							System.out.println("... done pool.executeProcessData() => res =" + res);
						} catch(Exception ex) {
							System.out.println("... Failed pool.executeProcessData() ex:" + ex.getMessage());
							ex.printStackTrace(System.out);
						}
						
					}
				}.start();
				
			} else if (command.equalsIgnoreCase("foreach-displayInfo")) {
				// TODO
				System.out.println("TODO ... foreach-displayInfo");
				
			} else if (command.equalsIgnoreCase("foreach-pingAlive")) {
				// TODO
				System.out.println("TODO ... foreach-pingAlive");

			} else if (command.equalsIgnoreCase("foreach-kill")) {
				// TODO
				System.out.println("TODO ... foreach-kill");
			}
		}
	}
	
	private String prompt(BufferedReader reader, String promptMsg) {
		System.out.println(promptMsg);
		try {
			String line = reader.readLine();
			return line;
		} catch (IOException ex) {
			throw new RuntimeException("Failed to read prompt", ex);
		}
	}
}
