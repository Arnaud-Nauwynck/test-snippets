package fr.an.sql;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.JDBCType;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLTimeoutException;
import java.sql.SQLType;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Types;
import java.util.Calendar;

public interface PreparedStatement extends Statement {

    ResultSet executeQuery()                 throws SQLException;
    int executeUpdate()                      throws SQLException;

    // for batch statement
    void addBatch()                          throws SQLException;

    boolean execute()                        throws SQLException;
    default long executeLargeUpdate()        throws SQLException { throw new UnsupportedOperationException("executeLargeUpdate not implemented"); }

    ResultSetMetaData getMetaData()          throws SQLException;
    ParameterMetaData getParameterMetaData()               throws SQLException;

    void clearParameters()                                 throws SQLException;

    void setNull(int parameterIndex, int sqlType)          throws SQLException;
    void setObject(int parameterIndex, Object x, int targetSqlType)            throws SQLException;
    void setObject(int parameterIndex, Object x)           throws SQLException;

    void setBoolean(int parameterIndex, boolean x)         throws SQLException;
    void setInt(int parameterIndex, int x)                 throws SQLException;
    // .. truncated set types: byte, short, long, float, double, BigDecimal 
    void setString(int parameterIndex, String x)           throws SQLException;
    void setURL(int parameterIndex, java.net.URL x)        throws SQLException;
    void setDate(int parameterIndex, java.sql.Date x)      throws SQLException;
    void setTime(int parameterIndex, java.sql.Time x)      throws SQLException;
    void setTimestamp(int parameterIndex, java.sql.Timestamp x)                throws SQLException;

    void setClob(int parameterIndex, Clob x)               throws SQLException;
    // .. truncated: setAsciiStream(InputStream), setCharacterStream(Reader), setNClob() 
    // setBytes(byte[]), setBlob(Blob), BinaryStream(InputStream), setSQLXML..  
    
    void setRef(int parameterIndex, Ref x)                 throws SQLException;
    void setArray(int parameterIndex, Array x)             throws SQLException;
    void setRowId(int parameterIndex, RowId x)             throws SQLException;
    // ... truncated...
    
    
    void setNString(int parameterIndex, String value)                 throws SQLException;
    void setNCharacterStream(int parameterIndex, Reader value, long length)                      throws SQLException;
    void setNClob(int parameterIndex, NClob value)                    throws SQLException;
    void setDate(int parameterIndex, java.sql.Date x, Calendar cal)   throws SQLException;
    void setTime(int parameterIndex, java.sql.Time x, Calendar cal)   throws SQLException;
    void setTimestamp(int parameterIndex, java.sql.Timestamp x, Calendar cal) throws SQLException;
    void setClob(int parameterIndex, Reader reader, long length)      throws SQLException;
    void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException;
    void setNClob(int parameterIndex, Reader reader, long length)     throws SQLException;
    void setSQLXML(int parameterIndex, SQLXML xmlObject)              throws SQLException;
    void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException;
    void setAsciiStream(int parameterIndex, java.io.InputStream x, long length) throws SQLException;
    void setBinaryStream(int parameterIndex, java.io.InputStream x, long length)                      throws SQLException;
    void setCharacterStream(int parameterIndex, java.io.Reader reader, long length)                      throws SQLException;
    void setAsciiStream(int parameterIndex, java.io.InputStream x)    throws SQLException;
    void setBinaryStream(int parameterIndex, java.io.InputStream x)   throws SQLException;
    void setCharacterStream(int parameterIndex, java.io.Reader reader)throws SQLException;
    void setNCharacterStream(int parameterIndex, Reader value)        throws SQLException;
    void setClob(int parameterIndex, Reader reader)                   throws SQLException;
    void setBlob(int parameterIndex, InputStream inputStream) throws SQLException;
    void setNull (int parameterIndex, int sqlType, String typeName)   throws SQLException;
    default void setObject(int parameterIndex, Object x, SQLType targetSqlType, int scaleOrLength)                      throws SQLException { throw new SQLFeatureNotSupportedException("setObject not implemented"); }
    default void setObject(int parameterIndex, Object x, SQLType targetSqlType) throws SQLException { throw new SQLFeatureNotSupportedException("setObject not implemented"); }
}
