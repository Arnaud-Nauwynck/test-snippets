package fr.an.sql;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.JDBCType;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLType;
import java.sql.SQLXML;
import java.sql.Types;
import java.util.Calendar;

public interface CallableStatement extends PreparedStatement {
	
	void registerOutParameter(int parameterIndex, int sqlType)                     throws SQLException;
	void registerOutParameter(int parameterIndex, int sqlType, int scale)          throws SQLException;
	void registerOutParameter (int parameterIndex, int sqlType, String typeName)   throws SQLException;
	void registerOutParameter(String parameterName, int sqlType)                   throws SQLException;
	void registerOutParameter(String parameterName, int sqlType, int scale)        throws SQLException;
	void registerOutParameter (String parameterName, int sqlType, String typeName) throws SQLException;

	void setInt(String parameterName, int x)       throws SQLException;
	void setString(String parameterName, String x) throws SQLException;
	// truncated.. setBoolean,Byte,Short,Date,... Blob,Clob,Array,..

	
	boolean wasNull() throws SQLException;
	void setNull (String parameterName, int sqlType, String typeName) throws SQLException;
	
	int getInt(int parameterIndex)                 throws SQLException;
	String getString(int parameterIndex)           throws SQLException;
	// truncated.. getBoolean,Byte,Short,Date,... Blob,Clob,Array,..

	int getInt(String parameterName)               throws SQLException;
	String getString(String parameterName)         throws SQLException;
	// truncated.. getBoolean,Byte,Short,Date,... Blob,Clob,Array,..
	




	//------------------------- JDBC 4.2 -----------------------------------
	default void setObject(String parameterName, Object x, SQLType targetSqlType,
			int scaleOrLength) throws SQLException {
		throw new SQLFeatureNotSupportedException("setObject not implemented");
	}
	default void setObject(String parameterName, Object x, SQLType targetSqlType)
			throws SQLException {
		throw new SQLFeatureNotSupportedException("setObject not implemented");
	}
	default void registerOutParameter(int parameterIndex, SQLType sqlType)
			throws SQLException {
		throw new SQLFeatureNotSupportedException("registerOutParameter not implemented");
	}
	default void registerOutParameter(int parameterIndex, SQLType sqlType,
			int scale) throws SQLException {
		throw new SQLFeatureNotSupportedException("registerOutParameter not implemented");
	}
	default void registerOutParameter (int parameterIndex, SQLType sqlType,
			String typeName) throws SQLException {
		throw new SQLFeatureNotSupportedException("registerOutParameter not implemented");
	}
	default void registerOutParameter(String parameterName, SQLType sqlType)
			throws SQLException {
		throw new SQLFeatureNotSupportedException("registerOutParameter not implemented");
	}
	default void registerOutParameter(String parameterName, SQLType sqlType,
			int scale) throws SQLException {
		throw new SQLFeatureNotSupportedException("registerOutParameter not implemented");
	}

	default void registerOutParameter (String parameterName, SQLType sqlType, String typeName) throws SQLException { throw new SQLFeatureNotSupportedException("registerOutParameter not implemented"); }

}