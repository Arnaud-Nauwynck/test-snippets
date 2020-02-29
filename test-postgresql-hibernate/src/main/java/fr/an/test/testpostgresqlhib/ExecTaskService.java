package fr.an.test.testpostgresqlhib;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.hibernate.LockOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ExecTaskService {

    @Autowired
    private ExecTaskRepository repo;

    @Autowired
    private DataSource ds;

    @Autowired
    private EntityManager em;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private XAService xa;

    public void insertData(int count) {
        for (int i = 0; i < count; i++) {
            ExecTaskEntity e = new ExecTaskEntity();
            e.setGroupId(i % 200);
            e.setStatus(ExecTaskStatus.TO_EXECUTE);
            repo.save(e);
        }
    }

    public List<ExecTaskEntity> pollLockTasksToExecute_jpa(int count) {
        String hql = "select t from ExecTaskEntity t where t.status = 0 ";
        return em.createQuery(hql, ExecTaskEntity.class).setMaxResults(count) // LIMIT count
                .setLockMode(LockModeType.PESSIMISTIC_WRITE) // select FOR UPDATE
                .setHint("javax.persistence.lock.timeout", LockOptions.SKIP_LOCKED)
                .getResultList();
    }

    public long pollLock1TaskToExecute_jpa() {
        String hql = "select t from ExecTaskEntity t where t.status = 0 ";
        List<ExecTaskEntity> tmp = em.createQuery(hql, ExecTaskEntity.class)
                .setMaxResults(1) // LIMIT count
                .setLockMode(LockModeType.PESSIMISTIC_WRITE) // select FOR UPDATE
                .setHint("javax.persistence.lock.timeout", LockOptions.SKIP_LOCKED)
                .getResultList();
        long res = 0;
        if (! tmp.isEmpty()) {
            res = tmp.get(0).getId();
        }
        return res;
    }

    public long pollLock1TaskToExecute_jdbc() {
        String sql = "SELECT t.id FROM exec_task t WHERE t.status = 0 " 
                + "LIMIT 1 " 
                + "FOR UPDATE of t SKIP LOCKED";
        Connection conn = null;
        try {
            conn = DataSourceUtils.getConnection(ds);

            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            long res = 0;
            if (rs.next()) {
                res = rs.getLong(1);
            }
            return res;
        } catch (SQLException ex) {
            throw new RuntimeException("", ex);
        } finally {
            try {
                DataSourceUtils.doReleaseConnection(conn, ds);
            } catch (SQLException e) {
            }
        }
    }

    public long pollLock1TaskToExecute_jdbc_storedproc() {
        String sql = "SELECT pollExecTask()";
        Connection conn = null;
        try {
            conn = DataSourceUtils.getConnection(ds);

            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            long res = 0;
            if (rs.next()) {
                res = rs.getLong(1);
            }
            return res;
        } catch (SQLException ex) {
            throw new RuntimeException("", ex);
        } finally {
            try {
                DataSourceUtils.doReleaseConnection(conn, ds);
            } catch (SQLException e) {
            }
        }
    }

    public long pollLock1TaskToExecute_jdbc_storedproc_delPrevTask(long prevTaskId) {
        String sql = "SELECT pollExecTask_updatePrev(?)";
        Connection conn = null;
        try {
            conn = DataSourceUtils.getConnection(ds);

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, prevTaskId);
            ResultSet rs = pstmt.executeQuery();
            long res = 0;
            if (rs.next()) {
                res = rs.getLong(1);
            }
            return res;
        } catch (SQLException ex) {
            throw new RuntimeException("", ex);
        } finally {
            try {
                DataSourceUtils.doReleaseConnection(conn, ds);
            } catch (SQLException e) {
            }
        }
    }


    
    public void xa_updateTaskStatus(long taskId) {
        xa.xanew(() -> {
            ExecTaskEntity task = repo.getOne(taskId);
            task.setStatus(ExecTaskStatus.DONE);
        });
    }

    public void xa_deleteTask(long taskId) {
        xa.xanew(() -> {
            repo.deleteById(taskId);
        });
    }

//    public List<FooEntity> findByIds(int[] ids) {
//        Connection conn = null;
//        try {
//            conn = DataSourceUtils.getConnection(ds);
//        
//            PgConnection pgConn = conn.unwrap(PgConnection.class);
//            
//            return res;
//        } catch(SQLException ex) {
//            throw new RuntimeException("", ex);
//        } finally {
//            try {
//                DataSourceUtils.doReleaseConnection(conn, ds);
//            } catch (SQLException e) {
//            }
//        }
//    }
//    

}
