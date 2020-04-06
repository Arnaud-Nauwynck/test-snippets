package fr.an.tests.overthere;

import java.io.ByteArrayOutputStream;

import org.junit.Test;

import com.xebialabs.overthere.CmdLine;
import com.xebialabs.overthere.OverthereConnection;
import com.xebialabs.overthere.OverthereProcess;
import com.xebialabs.overthere.local.LocalConnection;
import com.xebialabs.overthere.util.CapturingOverthereExecutionOutputHandler;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalHostTest {

	private String cygwinBashExe = 
			"C:/cygwin64/bin/"
			+ "bash.exe";
	
	@Test
	public void testLocalExecCygin() throws Exception {
		try (OverthereConnection conn = LocalConnection.getLocalConnection()) {
			CmdLine cmdLine = CmdLine.build(cygwinBashExe, "-c");
			cmdLine.addNested(CmdLine.build("echo", "'Hello World'"));
			int exitCode = conn.execute(cmdLine);
			log.info("=> exitCode: " + exitCode 
					+ " (streams to console..)"
					);
		}
	}

	@Test
	public void testLocalExecCygin_outputHandler() throws Exception {
		try (OverthereConnection conn = LocalConnection.getLocalConnection()) {
			CmdLine cmdLine = CmdLine.build(cygwinBashExe, "-c");
			cmdLine.addNested(CmdLine.build("echo", "'Hello World'"));
			val stdoutHandler = CapturingOverthereExecutionOutputHandler.capturingHandler();
			val stderrHandler = CapturingOverthereExecutionOutputHandler.capturingHandler();

			int exitCode = conn.execute(stdoutHandler, stderrHandler, cmdLine);
			val stdout = stdoutHandler.getOutput();
			val stderr = stderrHandler.getOutput();
			log.info("=> exitCode: " + exitCode 
					+ " (captured streams char lines..)"
					+ ((!stdout.isEmpty())? ", stdout: '" + stdout + "'" : "")
					+ ((!stderr.isEmpty())? ", stderr: '" + stderr + "'" : "")
					);
		}
	}

	@Test
	public void testLocalExecCygin_consumeAsStreams() throws Exception {
		try (OverthereConnection conn = LocalConnection.getLocalConnection()) {

			CmdLine cmdLine = CmdLine.build(cygwinBashExe, "-c");
			cmdLine.addNested(CmdLine.build("echo", "'Hello World'"));
			// => C:/cygwin64/bin/bash.exe -c "echo "'Hello World'""
			
			OverthereProcess process = conn.startProcess(cmdLine);

			val stdoutHandler = new ByteArrayOutputStream();
			val stderrHandler = new ByteArrayOutputStream();
			int exitCode = OverthereProcessUtils.consumeStreamsWaitFor(process, 
					stdoutHandler, stderrHandler, 
					cmdLine.toString());

			String stdout = stdoutHandler.toString(); // Charset.defaultCharset().name());
			String stderr = stderrHandler.toString(); // Charset.defaultCharset().name());
			log.info("=> exitCode: " + exitCode 
					+ ((!stdout.isEmpty())? ", stdout: '" + stdout + "'" : "")
					+ ((!stderr.isEmpty())? ", stderr: '" + stderr + "'" : "")
					);
		}
	}
}
