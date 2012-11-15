package fr.an.test.serverpool;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class WorkerPoolEntry {

	
	private static Logger log = LoggerFactory.getLogger(WorkerPoolEntry.class);
	
	private RemoteWorkerPool owner;
		
	private final int childId;
	private String childName;
	
	private Object lock = new Object();

	private DataProcessor executor;

	private boolean markForRemoveFromPool;
	private Thread currUsedByThread;

	private Date enlistDate;
	private Date firstUsedDate;
	private Date lastUsedDate;
	private String lastContextualUser;
	

	// usage Stats
	private SimpleTimeStats processedTimeStats = new SimpleTimeStats();
	private SimpleTimeStats processedSuccessedTimeStats = new SimpleTimeStats();
	private SimpleTimeStats processedErrorsTimeStats = new SimpleTimeStats();
	
	private UsedInfo reachedMaxDurationUsedInfo; 
	private UsedInfo reachedMaxDurationSucessedUsedInfo; 
	private UsedInfo reachedMaxDurationFailureUsedInfo; 

	private UsedInfo lastUsedInfo; 

	private UsedInfo firstErrorUsedInfo; 
	private UsedInfo lastErrorUsedInfo; 
	
	public static class SimpleTimeStats {
		private int count;
		private int totalDurationMillis;
		private int totalSquareDurationMillis;
		
		public void incr(long duration) {
			this.count++;
			this.totalDurationMillis += duration;
			this.totalSquareDurationMillis += (duration * duration);
		}

		public int getCount() {
			return count;
		}

		public int getTotalDurationMillis() {
			return totalDurationMillis;
		}

		public int getTotalSquareDurationMillis() {
			return totalSquareDurationMillis;
		}
		
	}
	
	public static class UsedInfo {
		Date time;
		long durationMillis;
		String user;
		String processData;
		String returnedData;
		Throwable exception;
		
		public UsedInfo(Date time, long durationMillis, String user,
				String processData, String returnedData, Throwable exception) {
			super();
			this.time = time;
			this.durationMillis = durationMillis;
			this.user = user;
			this.processData = processData;
			this.returnedData = returnedData;
			this.exception = exception;
		}

		public Date getTime() {
			return time;
		}

		public long getDurationMillis() {
			return durationMillis;
		}

		public String getUser() {
			return user;
		}

		public String getProcessData() {
			return processData;
		}

		public String getReturnedData() {
			return returnedData;
		}

		public Throwable getException() {
			return exception;
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	public WorkerPoolEntry(RemoteWorkerPool owner, int childId, String childName, DataProcessor executor) {
		this.owner = owner; 
		this.childId = childId;
		this.childName = childName; 
		this.executor = executor;
	}

	public void dispose() {
		this.owner = null;
		this.executor = null;
	}
	
	// ------------------------------------------------------------------------
	
	public boolean isClosedConn() {
		boolean res = executor.isClosed();
		return res;
	}

	public boolean processPingAlive() {
		boolean res = executor != null && executor.pingAlive();
		return res;
	}
	
	public String processData(String userName, String data) {
		String res;
		long timeBefore = System.currentTimeMillis();
		try {
			log.info("executing processData(..) on Worker " + childId + " " + childName);
			
			res = executor.processData(userName, data);
			
			long timeMillis = System.currentTimeMillis() - timeBefore;
			log.info("... done executing processData(..) on Worker " + childId + " " + childName +" ... took " + timeMillis + " ms");
			
			incrStatsDoneProcessedData(timeMillis, userName, data, res);
		} catch(RuntimeException ex) {
			long timeMillis = System.currentTimeMillis() - timeBefore;
			log.error("... Failed executing processData(..) on Worker " + childId + " " + childName +" ... took " + timeMillis + " ms", ex);
			incrStatsFailedProcessedData(timeMillis, userName, data, ex);
			throw ex;
		}
		
		return res;
	}

	
	// Stats 
	// ------------------------------------------------------------------------
	
	private void incrStatsDoneProcessedData(long durationMillis, String userName, String data, String result) {
		this.processedTimeStats.incr(durationMillis);
		this.processedSuccessedTimeStats.incr(durationMillis);

		if (this.lastContextualUser == null) {
			this.lastContextualUser = userName;
		}
		if (this.firstUsedDate == null) {
			this.firstUsedDate = new Date();
		}

		UsedInfo usedInfo = new UsedInfo(new Date(), durationMillis, userName, data, result, null);
		if (reachedMaxDurationUsedInfo == null || reachedMaxDurationUsedInfo.getDurationMillis() < durationMillis) {
			this.reachedMaxDurationUsedInfo = usedInfo;
		}
		if (reachedMaxDurationSucessedUsedInfo == null || reachedMaxDurationSucessedUsedInfo.getDurationMillis() < durationMillis) {
			this.reachedMaxDurationSucessedUsedInfo = usedInfo;
		}
		
		lastUsedInfo = usedInfo;
	}

	private void incrStatsFailedProcessedData(long durationMillis, String userName, String data, Exception ex) {
		this.processedTimeStats.incr(durationMillis);
		this.processedErrorsTimeStats.incr(durationMillis);

		if (this.lastContextualUser == null) {
			this.lastContextualUser = userName;
		}
		if (this.firstUsedDate == null) {
			this.firstUsedDate = new Date();
		}

		UsedInfo usedInfo = new UsedInfo(new Date(), durationMillis, userName, data, null, ex);
		if (reachedMaxDurationUsedInfo == null || reachedMaxDurationUsedInfo.getDurationMillis() < durationMillis) {
			this.reachedMaxDurationUsedInfo = usedInfo;
		}
		if (reachedMaxDurationFailureUsedInfo == null || reachedMaxDurationFailureUsedInfo.getDurationMillis() < durationMillis) {
			this.reachedMaxDurationFailureUsedInfo = usedInfo;
		}

		if (firstErrorUsedInfo == null) {
			this.firstErrorUsedInfo = usedInfo;
		}
		this.lastErrorUsedInfo = usedInfo;
	}


	// ------------------------------------------------------------------------
	
	/*pp*/ Object getLock() {
		return lock;
	}
	
	public RemoteWorkerPool getOwner() {
		return owner;
	}

	public String getChildName() {
		return childName;
	}

	public void setChildName(String childName) {
		this.childName = childName;
	}

	public boolean isMarkForRemoveFromPool() {
		synchronized(lock) {
			return markForRemoveFromPool;
		}
	}

	public void setMarkForRemoveFromPool(boolean p) {
		synchronized(lock) {
			this.markForRemoveFromPool = p;
		}
	}

	public boolean isCurrAvailable() {
		synchronized(lock) {
			return currUsedByThread == null
					&& !markForRemoveFromPool
					&& executor != null; //disposed
		}		
	}
	
	public Thread getCurrUsedByThread() {
		return currUsedByThread;
	}

	public void setCurrUsedByThread(Thread currUsedByThread) {
		this.currUsedByThread = currUsedByThread;
	}

	public int getChildId() {
		return childId;
	}
	
	public DataProcessor getExecutor() {
		return executor;
	}

	public void setExecutor(DataProcessor executor) {
		this.executor = executor;
	}

	public Date getEnlistDate() {
		return enlistDate;
	}

	public void setEnlistDate(Date enlistDate) {
		this.enlistDate = enlistDate;
	}

	public String getLastContextualUser() {
		return lastContextualUser;
	}

	public void setLastContextualUser(String lastContextualUser) {
		this.lastContextualUser = lastContextualUser;
	}

	public Date getFirstUsedDate() {
		return firstUsedDate;
	}

	public Date getLastUsedDate() {
		return lastUsedDate;
	}

	public SimpleTimeStats getProcessedTimeStats() {
		return processedTimeStats;
	}

	public SimpleTimeStats getProcessedSuccessedTimeStats() {
		return processedSuccessedTimeStats;
	}

	public SimpleTimeStats getProcessedErrorsTimeStats() {
		return processedErrorsTimeStats;
	}

	public UsedInfo getReachedMaxDurationUsedInfo() {
		return reachedMaxDurationUsedInfo;
	}

	public UsedInfo getReachedMaxDurationSucessedUsedInfo() {
		return reachedMaxDurationSucessedUsedInfo;
	}

	public UsedInfo getReachedMaxDurationFailureUsedInfo() {
		return reachedMaxDurationFailureUsedInfo;
	}

	public UsedInfo getLastUsedInfo() {
		return lastUsedInfo;
	}

	public UsedInfo getFirstErrorUsedInfo() {
		return firstErrorUsedInfo;
	}

	public UsedInfo getLastErrorUsedInfo() {
		return lastErrorUsedInfo;
	}

	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return "WorkerPoolEntry [" + childId + "]";
	}
	
	
}