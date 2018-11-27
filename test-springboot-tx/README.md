
# Test for using springframework (with springboot) Thread-Local Transaction resource + Synchronisation

used springframework API:
- TransactionSynchronizationManager.getResource(Object resourceKey);
- TransactionSynchronizationManager.bindResource(Object resourceKey, Object resourceValue);
- TransactionSynchronizationManager.unbindResource(Object resourceKey);
- TransactionSynchronizationManager.registerSynchronization(TransactionSynchronization trransactionSynchronisation);
- interface TransactionSynchronization

```
package org.springframework.transaction.support;

public abstract class TransactionSynchronizationManager {
	// .. all static methods / static ThreadLocal fields

	//-------------------------------------------------------------------------
	// Management of transaction-associated resource handles
	//-------------------------------------------------------------------------
	public static Map<Object, Object> getResourceMap();
	public static boolean hasResource(Object key);
	public static Object getResource(Object key);
	public static void bindResource(Object key, Object value) throws IllegalStateException;
	public static Object unbindResource(Object key) throws IllegalStateException;
	public static Object unbindResourceIfPossible(Object key);

	//-------------------------------------------------------------------------
	// Management of transaction synchronizations
	//-------------------------------------------------------------------------
	public static boolean isSynchronizationActive() {
	public static void initSynchronization() throws IllegalStateException;
	public static void registerSynchronization(TransactionSynchronization synchronization) throws IllegalStateException;
	public static List<TransactionSynchronization> getSynchronizations() throws IllegalStateException;
	public static void clearSynchronization() throws IllegalStateException;

	//-------------------------------------------------------------------------
	// Exposure of transaction characteristics
	//-------------------------------------------------------------------------
	public static void setCurrentTransactionName(@Nullable String name);
	public static String getCurrentTransactionName();
	public static void setCurrentTransactionReadOnly(boolean readOnly);
	public static boolean isCurrentTransactionReadOnly();
	public static void setCurrentTransactionIsolationLevel(@Nullable Integer isolationLevel);
	public static Integer getCurrentTransactionIsolationLevel();
	public static void setActualTransactionActive(boolean active);
	public static boolean isActualTransactionActive();
	public static void clear();
	
}

interface TransactionSynchronization {  
	void suspend(); void resume(); 
	void flush();
	void beforeCommit(boolean readOnly); void afterCommit()
	void beforeCompletion(); void afterCompletion(int status);  // status = STATUS_{COMMITTED=0 / ROLLED_BACK=1 / UNKNOWN=2 }
}
```

Executing code => logs + comments:


### Test call transaction
```
AppCommandRunner  : (1) run @Transactional foo() ..
TxService         : creating TxResource 1
 .. bound to ThreadLocal/XA => get or create TxResource => return same reference 
TxService         : creating UnboundedTxResource 1
.. bound to ThreadLocal/XA => get or createUnbounded TxResource => return same reference 
TxService         : synchronisationListener1 Tx beforeCommit(false)..
TxService         : synchronisationListener2 Tx beforeCommit(false)..
TxService         : synchronisationListener1 Tx beforeCompletion..
TxService         : synchronisationListener2 Tx beforeCompletion..
TxService         : synchronisationListener1 Tx afterCommit..
TxService         : synchronisationListener2 Tx afterCommit..
TxService         : synchronisationListener1 Tx afterCompletion(0)
TxService         : synchronisationListener2 Tx afterCompletion(0)
TxService         : bindCleanup for txResource Tx afterCompletion(0) => unbindResource
 .. explicit cleanup after commit => unbind TxResource 1 (but not UnboundTxResource) 
 .. no need to unbind transactionSynchronisation(s) ... they are managed by spring PlateformTransactionManager
```


### Test repeat call another transaction on same Thread 
```
AppCommandRunner  : (2) run @Transactional foo() ..
TxService         : creating TxResource 2
 .. TxResource was unbounded => created new one
 .. notice: UnboundedTxResource was not unbounded => is not recreated!
TxService         : synchronisationListener1 Tx beforeCommit(false)..
TxService         : synchronisationListener2 Tx beforeCommit(false)..
TxService         : synchronisationListener1 Tx beforeCompletion..
TxService         : synchronisationListener2 Tx beforeCompletion..
TxService         : synchronisationListener1 Tx afterCommit..
TxService         : synchronisationListener2 Tx afterCommit..
TxService         : synchronisationListener1 Tx afterCompletion(0)
TxService         : synchronisationListener2 Tx afterCompletion(0)
TxService         : bindCleanup for txResource Tx afterCompletion(0) => unbindResource
```

### Test call rolling-back transaction 
```
AppCommandRunner  : (3) run @Transactional fooEx()..
TxService         : creating TxResource 3
TxService         : synchronisationListener1 Tx beforeCompletion..
TxService         : synchronisationListener2 Tx beforeCompletion..
TxService         : synchronisationListener1 Tx afterCompletion(1)
TxService         : synchronisationListener2 Tx afterCompletion(1)
TxService         : bindCleanup for txResource Tx afterCompletion(1) => unbindResource
. explicit cleanup even if colling-back => unbind TxResource 1 (but not UnboundTxResource) 
AppCommandRunner  : ok got exception, rolled-backed..rolling back..
```

### Test after rolled-back on same Thread
```
AppCommandRunner  : (4) run @Transactional foo() ..
TxService         : creating TxResource 4
TxService         : synchronisationListener1 Tx beforeCommit(false)..
TxService         : synchronisationListener2 Tx beforeCommit(false)..
TxService         : synchronisationListener1 Tx beforeCompletion..
TxService         : synchronisationListener2 Tx beforeCompletion..
TxService         : synchronisationListener1 Tx afterCommit..
TxService         : synchronisationListener2 Tx afterCommit..
TxService         : synchronisationListener1 Tx afterCompletion(0)
TxService         : synchronisationListener2 Tx afterCompletion(0)
TxService         : bindCleanup for txResource Tx afterCompletion(0) => unbindResource
``` 

