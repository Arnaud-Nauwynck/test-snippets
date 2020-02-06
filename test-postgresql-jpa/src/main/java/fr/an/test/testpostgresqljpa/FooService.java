package fr.an.test.testpostgresqljpa;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.postgresql.jdbc.PgConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;

import lombok.val;

@Service
@Transactional
public class FooService {

    @Autowired
    private FooRepository repo;

    @Autowired
    private DataSource ds;

    @Autowired
    private EntityManager em;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void insertData(int count) {
        for(int i = 0; i < count; i++) {
            FooEntity e = new FooEntity();
    
            repo.save(e);
        }
    }

    public List<FooEntity> findByIds(int[] ids) {
        Connection conn = null;
        try {
            conn = DataSourceUtils.getConnection(ds);
        
            PgConnection pgConn = conn.unwrap(PgConnection.class);
            Array idArray = pgConn.createArrayOf("int4", ids);
            
            String hql = "select f from FooEntity f "
                    + "where f.id = SQL('ANY(?)', ?1)";
            List<FooEntity> res = em.createQuery(hql, FooEntity.class)
                    .setParameter(1, idArray)
                    .getResultList();
            
            return res;
        } catch(SQLException ex) {
            throw new RuntimeException("", ex);
        } finally {
            try {
                DataSourceUtils.doReleaseConnection(conn, ds);
            } catch (SQLException e) {
            }
        }
    }
    
    public List<FooEntity> findByIds2(int[] ids) {
        Connection conn = null;
        try {
            conn = DataSourceUtils.getConnection(ds);
        
            PgConnection pgConn = conn.unwrap(PgConnection.class);
            Array idArray = pgConn.createArrayOf("int4", ids);
            
            String hql = "select f from FooEntity f "
                    + "where f.id in (" 
                    + "  SQL(' select UNNEST(?) ',?1)"   // TODO The object [{ ... }] ], of class [class org.postgresql.jdbc.PgArray], from mapping [org.eclipse.persistence.mappings.DirectToFieldMapping[id-->Foo.ID]] with descriptor [RelationalDescriptor(fr.an.test.testpostgresqljpa.FooEntity --> [DatabaseTable(Foo)])], could not be converted to [class java.lang.Integer].
                    + " )";
            List<FooEntity> res = em.createQuery(hql, FooEntity.class)
                    .setParameter(1, idArray)
                    .getResultList();
            
            return res;
        } catch(SQLException ex) {
            throw new RuntimeException("", ex);
        } finally {
            try {
                DataSourceUtils.doReleaseConnection(conn, ds);
            } catch (SQLException e) {
            }
        }
    }

    public static String arrayToPgArray(int[] ids) {
        int length = ids.length;
        StringBuilder sb = new StringBuilder(length * 8);
        sb.append("{");
        for(int i = 0; i < length; i++) {
            sb.append("\"" + ids[i] + "\"");
            if (i+1 < length) {
                sb.append(",");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    public List<FooEntity> findByIds_SQL_ANY_arrayStr(int[] ids) {
        String idsStr = arrayToPgArray(ids);
        String hql = "select f from FooEntity f "
                + "where f.id = SQL('ANY(string_to_array(?, ?))', ?1, ?2)";
        List<FooEntity> res = em.createQuery(hql, FooEntity.class)
                .setParameter(1, idsStr)
                .setParameter(2, ",")
                .getResultList();
        return res;
    }

    
    public List<FooEntity> findByIds_loop(int[] ids) {
        List<FooEntity> res = new ArrayList<>(ids.length);
        for(int i = 0; i < ids.length; i++) {
            Optional<FooEntity> foundI = repo.findById(ids[i]);
            if (foundI.isPresent()) {
                res.add(foundI.get());
            }
        }
        return res;
    }

    
    public void bulkInsert_unnest(int count) {
        String sql = "INSERT INTO foo (id, version, field_int) "
                // + "SELECT * FROM ( "
                + "SELECT NEXTVAL('SEQ_FOO'), UNNEST(?), UNNEST(?) "
                // + ") AS temptable"
                ;
        jdbcTemplate.execute(sql, (PreparedStatement pstmt) -> {
            Connection con = pstmt.getConnection();
            PgConnection pgConn = con.unwrap(PgConnection.class);
            int[] fieldVersionValues = new int[count];
            Array fieldVersionArray = pgConn.createArrayOf("int4", fieldVersionValues);
            int[] fieldIntValues = RandomUtils.randomIntArray(count, count);
            Array fieldIntArray = pgConn.createArrayOf("int4", fieldIntValues);
            
            pstmt.setArray(1, fieldVersionArray);
            pstmt.setArray(2, fieldIntArray);
            pstmt.execute();
            
            return (Void) null;
        });
    }
    
    public void bulkUpdate() {
//        UPDATE tablename
//        SET fieldname1=temptable.data
//        FROM (
//            SELECT UNNEST(ARRAY[1,2]) AS id,
//                   UNNEST(ARRAY['a', 'b']) AS data
//        ) AS temptable
//        WHERE tablename.id=temptable.id;
    }
}
