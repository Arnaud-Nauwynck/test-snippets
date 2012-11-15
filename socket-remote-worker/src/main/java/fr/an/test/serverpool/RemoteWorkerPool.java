package fr.an.test.serverpool;

import java.net.ConnectException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteWorkerPool {
	
	private static Logger log = LoggerFactory.getLogger(RemoteWorkerPool.class);
	
	private Object lock = new Object();
	
	private int childIdGenerator = 1;
	
	private List<WorkerPoolEntry> pooledConnections = new ArrayList<WorkerPoolEntry>();

//	private List<WorkerPoolEntry> freeConnections = new ArrayList<WorkerPoolEntry>();
//	private List<WorkerPoolEntry> busyConnections = new ArrayList<WorkerPoolEntry>();

	private int delayMillisMaxWaitConnection = 60000; // 1 minute !
	
	// ------------------------------------------------------------------------
	
	public RemoteWorkerPool() {
	}
	
	// ------------------------------------------------------------------------
	
	public String executeProcessData(String userName, String data) throws WorkerPoolException {
		String res;

		WorkerPoolEntry conn = null;
		synchronized(lock) {
			lockedPurgeClosedConnections();
			conn = lockedTryTakeAvailableConn();
		}
		if (conn != null) {
			// OK, found a connection, but do test a PingAlive outside of lock before sending real requests...
			boolean pingAlive; 
			try {
				pingAlive = conn.processPingAlive();
			} catch(Exception ex) {
				pingAlive = false;
			}
			if (!pingAlive) {
				// take lock again, to remove this connection, and try get another one!!
				conn.dispose();
				synchronized(lock) {
					pooledConnections.remove(conn);
					// retry get one
					conn = lockedTryTakeAvailableConn();
				}
			}
		}
		if (conn == null) {
			// did not found an available connection! => wait few seconds, and retry
			// first redo within synchronized lock!!
			synchronized(lock) {
				conn = lockedTryTakeAvailableConn();
				if (conn == null) {
					try {
						lock.wait(delayMillisMaxWaitConnection);
					} catch(InterruptedException ex) {
						// ignore, no rethrow!
					}
					// redo after wait
					conn = lockedTryTakeAvailableConn();
				}
			}
		}
		if (conn == null) {
			throw new WorkerPoolException("Did not find an available Worker connection ... retry later!");
		}
		
		// ok, taken a connection from pool => do process, then return connection to pool  (or mark for remove from pool?)
		boolean succeedReturnToPool = true;
		try {
			// do processData() + log + handle time stats
			res = conn.processData(userName, data);
			
		} catch(RuntimeException ex) {
			// TODO... detect if error is fatal for this connection ... (connection lost, etc...) 
			succeedReturnToPool = ! shouldRemoveFromPoolOnError(ex);
			throw ex;
		} finally {
			if (succeedReturnToPool && conn.isMarkForRemoveFromPool()) {
				succeedReturnToPool = false;
			}
			if (succeedReturnToPool) {
				synchronized(conn.getLock()) {
					conn.setCurrUsedByThread(null);
				}
				synchronized(lock) {
					lock.notifyAll();
				}
			} else {
				log.debug("removing RemoteWorker " + conn + " from connection pool after fatal error...");
				synchronized(conn.getLock()) {
					conn.setCurrUsedByThread(null);
					conn.dispose();
				}
				synchronized(lock) {
					pooledConnections.remove(conn);
					log.warn("removing RemoteWorker " + conn + " from connection pool after fatal error => still " + pooledConnections.size() + " elt(s)");
					// TODO.. test to recreate new pooled connections (asynchronously)
					lock.notifyAll();
				}
				
			}
		}
		
		return res;
	}

	private WorkerPoolEntry lockedTryTakeAvailableConn() {
		WorkerPoolEntry conn = null;
		for (WorkerPoolEntry elt : pooledConnections) {
			synchronized(elt.getLock()) {
				if (elt.isCurrAvailable()) {
					elt.setCurrUsedByThread(Thread.currentThread());
					conn = elt;
					break;
				}
			}
		}
		return conn;
	}

	private void lockedPurgeClosedConnections() {
		for (Iterator<WorkerPoolEntry> iterator = pooledConnections.iterator(); iterator.hasNext();) {
			WorkerPoolEntry elt = iterator.next();
			synchronized(elt.getLock()) {
				if (elt.isClosedConn()) {
					elt.setMarkForRemoveFromPool(true);
					iterator.remove();
				}
			}
		}
	}
	
	protected boolean shouldRemoveFromPoolOnError(Throwable ex) {
		Throwable cause = ex.getCause();
		if (ex instanceof ConnectException || cause instanceof ConnectException) {
			return true;
		}
		else if (ex instanceof SocketException || cause instanceof SocketException) {
			return true;
		}
		return false; 
	}
	
	// ------------------------------------------------------------------------
	
	private int generateNewChildId() {
		synchronized(this) {
			return childIdGenerator++;
		}
	}
	
	public int addPooledRemoteWorker(String displayName, DataProcessor worker) {
		int childId = generateNewChildId();
		synchronized(lock) {
			WorkerPoolEntry conn = new WorkerPoolEntry(this, childId, displayName, worker);
			conn.setEnlistDate(new Date());
			
			pooledConnections.add(conn);
			lock.notifyAll();
		}
		return childId;
	}
	
	protected void removePooledRemoteWorker(DataProcessor obj) {
		synchronized(lock) {
			// find if already in free list... 
			WorkerPoolEntry found = null;
			for (WorkerPoolEntry conn : pooledConnections) {
				if (conn.getExecutor() == obj) {
					found = conn;
					break;
				}
			}
			if (found != null) {
				found.setMarkForRemoveFromPool(true);
				pooledConnections.remove(found);
			}
		}
	}
	
}
