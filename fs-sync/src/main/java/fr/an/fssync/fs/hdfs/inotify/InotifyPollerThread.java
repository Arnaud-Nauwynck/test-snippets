package fr.an.fssync.fs.hdfs.inotify;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.hadoop.hdfs.DFSInotifyEventInputStream;
import org.apache.hadoop.hdfs.inotify.Event;
import org.apache.hadoop.hdfs.inotify.EventBatch;

public class InotifyPollerThread {

    private DFSInotifyEventInputStream inotifyStream;

    private Thread thread;
    private AtomicBoolean stopRequired = new AtomicBoolean();
    
    private InotifyEventHandler eventHandler;
    
    
    private long lastTxid = 0; // redundant with inotifyStream.lastReadTxid
    
    
    public InotifyPollerThread(DFSInotifyEventInputStream inotifyStream, InotifyEventHandler eventHandler) {
	this.inotifyStream = inotifyStream;
	this.eventHandler = eventHandler;
    }

    public void start() {
	if (null != this.thread) {
	    return;
	}
	this.thread = new Thread(() -> runPoll()); 
    }

    public void stop() {
	if (null == this.thread) {
	    return;
	}
	stopRequired.set(true);
	// wait stopped?
    }
    
    private void runPoll() {
	for(;;) {
	    if (stopRequired.get()) {
		break;
	    }
	    try {
		EventBatch polledRes = inotifyStream.poll(1, TimeUnit.SECONDS);
		if (polledRes != null) {
		    handlePolledEventBatch(polledRes);
		
		    long getTxidsBehindEstimate = inotifyStream.getTxidsBehindEstimate();
		
		}
		
		
	    } catch(Exception ex) {
		// ignore, no rethow!
	    }
	}
	
	// stopped
	this.thread = null;
    }

    private void handlePolledEventBatch(EventBatch eventBatch) {
	Event[] events = eventBatch.getEvents();
	this.lastTxid  = eventBatch.getTxid();

	eventHandler.handleInotifyEvents(events);
    }

    
//    public class DFSClient {
//	final ClientProtocol namenode;
//
//    	public DFSInotifyEventInputStream getInotifyEventStream() ..
//	public DFSInotifyEventInputStream getInotifyEventStream(long lastReadTxid) ..
//    }
//  
//    public static interface DFSInotifyEventInputStream {
//	public EventBatch poll() 
//		throws IOException, MissingEventsException;
//	public long getTxidsBehindEstimate();
//	public EventBatch poll(long time, TimeUnit tu) 
//		throws IOException, InterruptedException, MissingEventsException;
//	public EventBatch take() 
//		throws IOException, InterruptedException, MissingEventsException;
//
//    }
}
