package fr.an.tests.overthere;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.slf4j.MDC;

import com.xebialabs.overthere.CmdLine;
import com.xebialabs.overthere.OverthereProcess;
import com.xebialabs.overthere.RuntimeIOException;
import com.xebialabs.overthere.util.OverthereUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OverthereProcessUtils {

	/**
	 * inspired from BaseOverthereConnection.execute(), but consume raw Stream, not Reader chars
	 * 
	 * @param process
	 * @param stdoutHandler
	 * @param stderrHandler
	 * @param commandLine
	 * @return
	 */
    public static int consumeStreamsWaitFor(
    		OverthereProcess process,
    		OutputStream stdoutHandler, 
    		OutputStream stderrHandler,
    		String displayStreamThreadName // cf .. commandLine.toString()
    		) {
        Thread stdoutReaderThread = null;
        Thread stderrReaderThread = null;
        final CountDownLatch latch = new CountDownLatch(2);
        try {
        	if (displayStreamThreadName == null) {
        		displayStreamThreadName = "";
        	}
            Map<String, String> mdcContext =  MDC.getCopyOfContextMap();
            stdoutReaderThread = getThread("stdout", displayStreamThreadName, stdoutHandler, process.getStdout(), latch, mdcContext);
            stdoutReaderThread.start();

            stderrReaderThread = getThread("stderr", displayStreamThreadName, stderrHandler, process.getStderr(), latch, mdcContext);
            stderrReaderThread.start();

            try {
                latch.await();
                return process.waitFor();
            } catch (InterruptedException exc) {
                Thread.currentThread().interrupt();

                log.info("Execution interrupted, destroying the process.");
                process.destroy();

                throw new RuntimeIOException("Execution interrupted", exc);
            }
        } finally {
            quietlyJoinThread(stdoutReaderThread);
            quietlyJoinThread(stderrReaderThread);
        }
    }

    private static void quietlyJoinThread(final Thread thread) {
        if (thread != null) {
            try {
                // interrupt the thread in case it is stuck waiting for output that will never come
                thread.interrupt();
                thread.join();
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static Thread getThread(
    		final String streamName, 
    		final String commandLine, 
    		final OutputStream outputHandler, 
    		final InputStream consumeStream, 
    		final CountDownLatch latch, 
    		final Map<String, String> mdcContext) {
        Thread t = new Thread(String.format("%s reader", streamName)) {
            @Override
            public void run() {
                Map<String, String> previous = MDC.getCopyOfContextMap();
                setMDCContext(mdcContext);
                latch.countDown();
                try {
                	for(;;) {
	                    int b = consumeStream.read();
	                    if (b == -1) {
	                    	break; // EOF
	                    }
	                    outputHandler.write(b);
                	}
                } catch (Exception exc) {
                    log.error(String.format("An exception occured reading %s while executing [%s] on %s", streamName, commandLine, this), exc);
                } finally {
                	OverthereUtils.closeQuietly(consumeStream);
                    setMDCContext(previous);
                }
            }

            private void setMDCContext(Map<String, String> context) {
                if (context == null) {
                    MDC.clear();
                } else {
                    MDC.setContextMap(context);
                }
            }
        };
        t.setDaemon(true);
        return t;
    }

}
