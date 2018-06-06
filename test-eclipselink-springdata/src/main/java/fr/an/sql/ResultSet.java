package fr.an.sql;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.DatabaseMetaData;
import java.sql.JDBCType;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Wrapper;
import java.util.Calendar;

public interface ResultSet extends Wrapper, AutoCloseable {

    void close()                         throws SQLException;
    boolean isClosed()                   throws SQLException;

    boolean next()                       throws SQLException;

    // access results by column index
    int getInt(int columnIndex)          throws SQLException;
    String getString(int columnIndex)    throws SQLException;
    // .. truncate: boolean, short, long, float .. bytes,Blob,Clob.. 

    // access results by column label
    int getInt(String columnLabel)       throws SQLException;
    String getString(String columnLabel) throws SQLException;
    // .. truncate: boolean, short, long, float .. bytes,Blob,Clob.. 
    
    boolean wasNull()                    throws SQLException;

    SQLWarning getWarnings()             throws SQLException;
    void clearWarnings()                 throws SQLException;

    String getCursorName()               throws SQLException;
    ResultSetMetaData getMetaData()      throws SQLException;
    int findColumn(String columnLabel)   throws SQLException;

    // Properties: fetchDirection, fetchSize, 
    // with getter only: type, concurrency, holdabitity

    // Navigation
    //---------------------------------------------------------------------
    boolean isBeforeFirst()              throws SQLException;
    // truncate: isAfterLast,iFirst,isLast,beforeFirst,afterLast,first,last
    int getRow()                         throws SQLException;
    boolean absolute( int row )          throws SQLException;
    boolean relative( int rows )         throws SQLException;
    boolean previous()                   throws SQLException;


    // Update columns by index / by name
    //---------------------------------------------------------------------
    boolean rowUpdated()                 throws SQLException;
    boolean rowInserted()                throws SQLException;
    boolean rowDeleted()                 throws SQLException;

    void updateNull(int columnIndex)     throws SQLException;
    void updateInt(int columnIndex, int x)    throws SQLException;
    // .. truncate: boolean, short, long, float .. bytes,Blob,Clob..

    void updateNull(String columnName)   throws SQLException;
    void updateInt(String columnName, int x)  throws SQLException;
    // .. truncate: boolean, short, long, float .. bytes,Blob,Clob..


}
