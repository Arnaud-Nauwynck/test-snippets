package fr.an.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.ConnectionEventListener;
import javax.sql.StatementEventListener;
import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;

public interface DataSource  extends CommonDataSource, Wrapper {
	Connection getConnection() throws SQLException;
	Connection getConnection(String username, String password) throws SQLException;
}

interface CommonDataSource {
    java.io.PrintWriter getLogWriter() throws SQLException;
    void setLogWriter(java.io.PrintWriter out) throws SQLException;
    void setLoginTimeout(int seconds) throws SQLException;
    int getLoginTimeout() throws SQLException;
    public Logger getParentLogger() throws SQLFeatureNotSupportedException;
}

interface Wrapper {
	<T> T unwrap(java.lang.Class<T> iface) throws java.sql.SQLException;
    boolean isWrapperFor(java.lang.Class<?> iface) throws java.sql.SQLException;
}


interface XADataSource extends CommonDataSource {
	XAConnection getXAConnection() throws SQLException;
	XAConnection getXAConnection(String user, String password) throws SQLException;
}

interface XAConnection extends PooledConnection {
	javax.transaction.xa.XAResource getXAResource() throws SQLException;
}

interface PooledConnection {
	Connection getConnection() throws SQLException;
	void close() throws SQLException;
	void addConnectionEventListener(ConnectionEventListener listener);
	void removeConnectionEventListener(ConnectionEventListener listener);
	public void addStatementEventListener(StatementEventListener listener);
	public void removeStatementEventListener(StatementEventListener listener);
}

interface XAResource {
    int prepare(Xid xid) throws XAException;
    void commit(Xid xid, boolean onePhase) throws XAException;
    void rollback(Xid xid) throws XAException;
    
    void start(Xid xid, int flags) throws XAException;
    void end(Xid xid, int flags) throws XAException;
    void forget(Xid xid) throws XAException;
    Xid[] recover(int flag) throws XAException;
    boolean isSameRM(XAResource xares) throws XAException;
    int getTransactionTimeout() throws XAException;
    boolean setTransactionTimeout(int seconds) throws XAException;
}

