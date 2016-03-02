package fr.an.dynadapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class is inspired from Eclipse platform AdapterManager 
 * ... but heavilly refactored to allow dynamic "type ids" not backed up java classes
 * 
 * See Eclipse documentation for a detailed explanation..
 * The purpose of it is to allow java "extends/implements" not at compile time, but at runtime.
 * It use several design patterns... mostly "Adapter"s obtained by a "Registry" of adapter "Factory"s.
 *  
 * 
 * @param <DT>  datatype in TypeHierarchy system for adaptable object (example: java fully qualified name of object)
 * @param <IId> interface id for adapters  (example: java fully qualified name of interface)
 * 
 * In original code from Eclipse Adapter, both DT and IId are using java Class<?> (or equivalent java fully qualified class name)
 * .. This is confusing and very limitating for dynamic system.
 * 
 * In a purely dynamic system, we may have:
 * DT = String... and TypeHierarchy may be a simple file declaration of DAG "type->superType(s)"
 * IId = String... for a serviceId in a ServiceLocator, then using a json schema RestInterface  
 */
public class DynAdapterManager<DT,IId> implements IDynAdapterManager<DT,IId>, IDynAdapterManagerSPI<DT,IId> {

    private final IDynDataTypeHierarchy<DT> dataTypeHierarchy;
    
    private final IDynInterfaceToLang<IId> interfaceToLang;
    
    /**
     * registered factories
     */
    private final Map<DT,List<IDynAdapterFactory<IId>>> factories = new HashMap<>(5);

    
    /** 
     * Cache of adapter factory: adaptableType DT -> interfaceId IID -> adapterFactory
     * 
     * Thread safety note: The outer map is synchronized using a synchronized
     * map wrapper class.  The inner map is not synchronized, but it is immutable
     * so synchronization is not necessary.
     */
    private Map<DT,Map<IId,IDynAdapterFactory<IId>>> adapterLookup;


    // ------------------------------------------------------------------------
    
    public DynAdapterManager(IDynDataTypeHierarchy<DT> typeHierarchy, IDynInterfaceToLang<IId> interfaceToJava) {
        this.dataTypeHierarchy = typeHierarchy;
        this.interfaceToLang = interfaceToJava;
    }

    // impements IAdapterManager
    // ------------------------------------------------------------------------

    @Override
    public /*<T> T*/Object getAdapter(Object adaptable, IId interfaceId) {
        DT adaptableType = dataTypeHierarchy.dataTypeOf(adaptable);
        IDynAdapterFactory<IId> factory = getFactories(adaptableType).get(interfaceId);
        /*T*/Object result = null;
        if (factory != null) {
            result = factory.getAdapter(adaptable, interfaceId);
        }
        if (result == null && interfaceToLang.isInstance(adaptable, interfaceId)) {
            return /*(T)*/ adaptable;
        }
        return result;
    }

    @Override
    public boolean hasAdapter(Object adaptable, IId interfaceId) {
        DT adaptableType = dataTypeHierarchy.dataTypeOf(adaptable);
        return getFactories(adaptableType).get(interfaceId) != null;
    }
    
    @Override
    public Set<IId> computeAdapterTypes(DT adaptableType) {
        Set<IId> types = getFactories(adaptableType).keySet();
        return new LinkedHashSet<>(types);
    }


    // implements IAdapterManagerSPI for register/unregister / cache
    // ------------------------------------------------------------------------
    
    @Override
    public synchronized void flushLookup() {
        adapterLookup = null;
    }

    @Override
    public synchronized void registerAdapters(IDynAdapterFactory<IId> factory, DT adaptableType) {
        doRegisterFactory(factory, adaptableType);
        flushLookup();
    }

    @Override
    public synchronized void unregisterAdapters(IDynAdapterFactory<IId> factory) {
        for (List<IDynAdapterFactory<IId>> ls : factories.values()) {
            ls.remove(factory);
        }
        flushLookup();
    }

    @Override
    public synchronized void unregisterAdapters(IDynAdapterFactory<IId> factory, DT adaptable) {
        List<IDynAdapterFactory<IId>> factoryList = factories.get(adaptable);
        if (factoryList == null)
            return;
        factoryList.remove(factory);
        flushLookup();
    }

    @Override
    public synchronized void registerAdapters(Collection<AdapterFactoryRegistration<DT,IId>> registrations) {
        for(AdapterFactoryRegistration<DT,IId> reg : registrations) {
            doRegisterFactory(reg.factory, reg.adaptableDataType);
        }
        flushLookup();
    }
    
    @Override
    public synchronized void unregisterAdapters(Collection<AdapterFactoryRegistration<DT,IId>> registrations) {
        for(AdapterFactoryRegistration<DT,IId> reg : registrations) {
            if (reg.adaptableDataType != null) {
                unregisterAdapters(reg.factory, reg.adaptableDataType);
            } else {
                unregisterAdapters(reg.factory);
            }
        }
        flushLookup();
    }
    
    public synchronized void unregisterAllAdapters() {
        factories.clear();
        flushLookup();
    }

    
    // internal
    // ------------------------------------------------------------------------

    protected void doRegisterFactory(IDynAdapterFactory<IId> factory, DT adaptableType) {
        List<IDynAdapterFactory<IId>> list = factories.get(adaptableType);
        if (list == null) {
            list = new ArrayList<>(5);
            factories.put(adaptableType, list);
        }
        list.add(factory);
    }
                
    protected IDynAdapterFactory<IId> getFactory(DT adaptableType, IId interfaceId) {
        return getFactories(adaptableType).get(interfaceId);
    }

    /**
     * get factories (indexed per interfaceId) for adaptable datatype.
     * it is lazyly computed for type or parent superType hierarchy, given all factories per type
     */
    private Map<IId,IDynAdapterFactory<IId>> getFactories(DT adaptableType) {
        //cache reference to lookup to protect against concurrent flush
        Map<DT,Map<IId,IDynAdapterFactory<IId>>> lookup = adapterLookup;
        if (lookup == null) {
            adapterLookup = lookup = Collections.synchronizedMap(new HashMap<>(30));
        }
        Map<IId,IDynAdapterFactory<IId>> res = lookup.get(adaptableType);
        if (res == null) {
            // calculate adapters for the class
            res = new HashMap<>(4);
            DT[] superTypes = dataTypeHierarchy.computeSuperTypesOrder(adaptableType);
            for (DT superType : superTypes) {
                addFactoriesFor(superType, res);
            }
            // cache the table
            lookup.put(adaptableType, res);
        }
        return res;
    }
    
    /**
     * Given a type name, add all of the factories that respond to those types into
     * the given table. Each entry will be keyed by the adapter class name (supplied in
     * IAdapterFactory<IId>.getAdapterList).
     */
    private void addFactoriesFor(DT adaptableType, Map<IId,IDynAdapterFactory<IId>> to) {
        List<IDynAdapterFactory<IId>> factoryList = factories.get(adaptableType);
        if (factoryList == null)
            return;
        for (IDynAdapterFactory<IId> factory : factoryList) {
            IId[] adapters = factory.getInterfaceIds();
            for (IId a : adapters) {
                if (to.get(a) == null) {
                    to.put(a, factory);
                }
            }
        }
    }
    

}