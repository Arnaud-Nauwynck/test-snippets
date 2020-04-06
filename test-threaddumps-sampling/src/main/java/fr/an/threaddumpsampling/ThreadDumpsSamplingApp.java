package fr.an.threaddumpsampling;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import com.hp.ts.rnd.tool.perf.threads.sampler.ThreadSampler;
import com.hp.ts.rnd.tool.perf.threads.sampler.ThreadSamplerFactory;
import com.hp.ts.rnd.tool.perf.threads.sampler.ThreadSamplingHandler;
import com.hp.ts.rnd.tool.perf.threads.sampler.ThreadSamplingService;
import com.hp.ts.rnd.tool.perf.threads.sampler.ThreadSamplingState;
import com.hp.ts.rnd.tool.perf.threads.sampler.ThreadSamplings;
import com.hp.ts.rnd.tool.perf.threads.ThreadStackTrace;
import com.hp.ts.rnd.tool.perf.threads.calltree.CallTreeAnalyzer;
import com.hp.ts.rnd.tool.perf.threads.jstack.JstackOutputParser;
import com.hp.ts.rnd.tool.perf.threads.jstack.JstackThreadSamplerFactory;
import com.hp.ts.rnd.tool.perf.threads.util.Utils;
import com.sun.tools.attach.VirtualMachine;

import sun.tools.attach.HotSpotVirtualMachine;

public class ThreadDumpsSamplingApp {

	public static void main(String[] args) {
		try {
			ThreadDumpsSamplingApp app = new ThreadDumpsSamplingApp();
			app.run(args);
		} catch(Exception ex) {
			System.err.println("Failed");
			ex.printStackTrace(System.err);
			System.exit(-1);
		}
	}

	public void run(String[] args) throws Exception {
		String pidStr = args[0];
		int pid = Integer.parseInt(pidStr);
		
		runThreadDumps(pid, 100, 10L); // 100 x 10ms = 1s
	}

	private static void runThreadDumps(int pid, int repeatCount, long sleepMillisInterval) throws Exception {
		ThreadSampler jstackThreadSampler = ThreadSamplings.createJstackThreadSamplerFactory(pid).getSampler();
		ThreadSamplingService samplingService = ThreadSamplings.createSimpleSamplingService(jstackThreadSampler, 
				5, //samplingDurationSeconds,
				10 // samplingPeriodMillis
				);
		
//		ThreadSamplingHandler handler = ThreadSamplings.createCallTree(System.out);
//		samplingService.executeSampling(handler);
	}
	
	private static void runThreadDumps_explicit(String pid, int repeatCount, long sleepMillisInterval) throws Exception {
		HotSpotVirtualMachine hotspotVM = attachHotspotVM(pid);
        
		CallTreeAnalyzer callTreeStats = new CallTreeAnalyzer();
		
        for(int i = 0; i < repeatCount; i++ ) {
        	long prevTime = System.currentTimeMillis();
	        
        	InputStream in = null;
        	try {
        		in = hotspotVM.remoteDataDump(new Object[0]);
	
        		ThreadSamplingState samplingState = parseThreadDump(in);
		        callTreeStats.addThreadSampling(samplingState);
		        
        	} finally {
        		if (in != null) {
        			in.close();
        		}
        	}
        
	        long currTime = System.currentTimeMillis();
	        long dumpTimeMillis = currTime - prevTime;
	        System.out.println("stack dump " + dumpTimeMillis + " ms");
	        long remainSleep = sleepMillisInterval - dumpTimeMillis;
	        if (remainSleep > 0) {
	        	Thread.sleep(remainSleep);
	        }
        }
        
        hotspotVM.detach();
        
        callTreeStats.print(System.out);
    }

	private static ThreadSamplingState parseThreadDump(InputStream in) throws IOException, UnsupportedEncodingException {
		ThreadSamplingState samplingState = new ThreadSamplingState();
		JstackOutputParser parser = new JstackOutputParser(in);
		parser.parseThreadDumpSampling(samplingState);
		return samplingState;
	}
	
	private static HotSpotVirtualMachine attachHotspotVM(String pid) {
		VirtualMachine vm = null;
        try {
            vm = VirtualMachine.attach(pid);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to attach to pid: " + pid, ex);
        }
        return (HotSpotVirtualMachine) vm;
	}
	
}
