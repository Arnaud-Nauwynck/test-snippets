package fr.an.persistence;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.LockTimeoutException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import javax.persistence.PessimisticLockException;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TransactionRequiredException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.metamodel.Metamodel;

@SuppressWarnings("rawtypes") 

public interface EntityManager {
    public void close();
    public boolean isOpen();

    // CRUD : Create, Read, (No-Update,) Delete
    public void persist(Object entity); //=INSERT + attach
    public void remove(Object entity); //=DELETE + detach

    // Query by ID
    public <T> T find(Class<T> entityClass, Object primaryKey); // cf also variants with props, lockMode
    public <T> T getReference(Class<T> entityClass, Object primaryKey); //=find + lazy
    // Queries using JPA-ql   
    public Query createQuery(String qlString);
    public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass);
    public Query createNamedQuery(String name);
    public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass);
    // Queries using SQL   
    public Query createNativeQuery(String sqlString);
    public Query createNativeQuery(String sqlString, Class resultClass);
    public Query createNativeQuery(String sqlString, String resultSetMapping);
    public StoredProcedureQuery createNamedStoredProcedureQuery(String name);
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName);
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName, Class... resultClasses);
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName, String... resultSetMappings);
    // Queries using dynamic criteria   
    public CriteriaBuilder getCriteriaBuilder();
    public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery); 
    public Query createQuery(CriteriaUpdate updateQuery);
    public Query createQuery(CriteriaDelete deleteQuery);
    
    
    public <T> T merge(T entity); // SHOULD not use.. prefer DTO<->Entity Mapper + setter on Entity
    public void refresh(Object entity); // cf also variants with props, lockMode
    public boolean contains(Object entity);
    public void detach(Object entity); 
    public void clear(); // detach all + forget XA changes
    
    public void flush();
    public void setFlushMode(FlushModeType flushMode);
    public FlushModeType getFlushMode();

    public void lock(Object entity, LockModeType lockMode); // cf also variant with props
    public LockModeType getLockMode(Object entity);


    public Metamodel getMetamodel();
    public EntityManagerFactory getEntityManagerFactory();
    public void setProperty(String propertyName, Object value);
    public Map<String, Object> getProperties();
    public <T> T unwrap(Class<T> cls); 

    public void joinTransaction();
    public boolean isJoinedToTransaction();
    public EntityTransaction getTransaction();

    public <T> EntityGraph<T> createEntityGraph(Class<T> rootType);
    public EntityGraph<?> createEntityGraph(String graphName);
    public  EntityGraph<?> getEntityGraph(String graphName);
    public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> entityClass);

}
