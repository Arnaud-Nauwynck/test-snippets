package fr.an.dynadapter.alt;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import fr.an.dynadapter.typehiera.DefaultLangTypeHierarchy;
import fr.an.dynadapter.typehiera.ITypeHierarchy;

/**
 * extension of DynAdapterManager for handling multiple alternative implementations per interface
 * <p>
 * The purpose of it is to allow java "extends/implements" not at compile time, but at runtime, and using several implementations capabilities.<br/>
 * It use several design patterns... mostly "Adapter"s obtained by a "Registry" of adapter "Factory"s.
 * 
 * <h3>Typical usage scenario</h3>
 * In management of resources (Cmdb, MiDb..), there can be several ways of performing an action.<br/>
 * For example, given a "tomcat" server, there are 3 ways to stop it, 2 ways to start it..<br/>
 * 
 * ways to stop:
 * <ul>
 * <li>remote connect to host using SSH/WinRM/.. then execute in shell: "cd $TOMCAT_BASE; ./bin/shutdown.sh"</li>
 * <li>remote connect to host using SSH/WinRM/.. then execute in shell: "kill -9 $PID"</li>
 * <li>if manager app is deployed, send http command to manager hosted webapp: "curl -X POST .... http://$HOST:$PORT/manager/shutdown"</li>
 * <li>if using a monitoring agent(consul.io..), send command to agent ..</li>
 * </ul> 
 * So for the given interface id  
 * <PRE>
 * ItfId<IStopSupport> stopItfId = new ItfId<>(IStopSupport.class, "");
 * </PRE>
 * 
 * <PRE>
 * public interface IStopSupport {
 *   public void stop();
 * }
 * </PRE>
 * 
 * and the given manageable object instance descriptor (in pseudo JSON format) 
 * <PRE>
 *  { 
 *    id: "DEV/some-tomcat-123",
 *    type: "tomcat",
 *    hasManagerWebApp: true,
 *    hostConnection: {
 *      type: "ssh"
 *      hostname: "hostname.company.com",
 *      user: "tomcataccount123",
 *      sshCredential: "tomcataccount123.id_rsa"
 *    }
 *    port: 8443,
 *    webManagerConnection: {
 *      type: "httpConnection"
 *      user: "manager123",
 *      credential: "manager123-password",
 *      url: "https://$hostname:$port/manager"    
 *    }
 *  }
 * </PRE>
 * 
 * then having a reference to the object to stop it could be performed as:
 * <PRE>
 * DynAdapterAlternativesManager adapterManager = ...;
 * ResourceElt tomcatResource = ...; 
 * 
 * // best effort to try by looping on all alternatives
 * IStopSupport stopSupport = adapterManager.getAdapter(tomcatResource, IStopSupport.class); 
 * stopSupport.stop();
 * 
 * // same, using only "SSH+bin/shutdown" alternative
 * IStopSupport sshStopSupport = adapterManager.getAdapter(tomcatResource, IStopSupport.class, "ssh+bin/shutdown"); 
 * sshStopSupport.stop();
 * 
 * // same with test if supporting manager webapp
 * if (adapterManager.hasAdapter(tomcatResource, IStopSupport.class, "webManager")) {
 *    IStopSupport webManagerStopSupport = adapterManager.getAdapter(tomcatResource, IStopSupport.class, "webManager"); 
 *    webManagerStopSupport.stop();
 * }
 * </PRE>
 *
 * Querying object capabilities:
 * <PRE>
 * Class[] supportedInterfaces = adapterManager.getAdapterInterfaces(tomcatResource);
 * String[] alternativeNames = adapterManager.getAdapterAlternativeNames(tomcatResource, IStopSupport.class);
 * </PRE>
 * 
 * Using even less typed interfaces, equivalent code using java.lang.Runnable instead of typed IStopSupport:
 * <PRE>
 * // .. public interface Runnable { public void run(); }
 * ItfId<Runnable> stopItfId = new ItfId<>(Runnable.class, "stop")
 * 
 * Runnable stopAction = adapterManager.getAdapter(tomcatResource, stopItfId); 
 * stopAction.run();
 * 
 * Runnable sshStopAction = adapterManager.getAdapter(tomcatResource, stopItfId, "ssh+bin/shutdown"); 
 * sshStopAction.run();
 * </PRE>
 * 
 * 
 */
public class AdapterAlternativesManager<DT> implements IAdapterAlternativesManager<DT>, IAdapterAlternativesManagerSPI<DT>, Closeable {

    private final ITypeHierarchy<DT> typeHierarchy;
        
    /**
     * registered factories
     * 
     * thread safety: explicit outer lock on "this"
     */
    private final Map<DT,List<IAdapterAlternativeFactory>> factories = new HashMap<>(5);

    
    /** 
     * Cache of adapter factory: adaptableType DT -> interfaceId ItfId<?> -> alternative -> adapterFactory
     */
    private Map<DT,Map<ItfId<?>,Map<String,IAdapterAlternativeFactory>>> adapterLookup;


    // ------------------------------------------------------------------------
    
    public AdapterAlternativesManager(ITypeHierarchy<DT> typeHierarchy) {
        this.typeHierarchy = typeHierarchy;
    }

    @Override
    public void close() {
        factories.clear();
        adapterLookup = null;
    }
    
    // implements IAdapterManager
    // ------------------------------------------------------------------------
    
    @Override
    public <T> T getAdapter(Object adaptable, ItfId<T> interfaceId) {
        // equivalent to inlined code... return getAdapter(adaptable, interfaceId, x -> true);
        DT adaptableType = typeHierarchy.dataTypeOf(adaptable);
        Map<String, IAdapterAlternativeFactory> a2f = getFactories(adaptableType, interfaceId);
        if (a2f != null) {
            for(Map.Entry<String,IAdapterAlternativeFactory> a2fEntry : a2f.entrySet()) {
                IAdapterAlternativeFactory factory = a2fEntry.getValue();
                Object tmpres =factory.getAdapter(adaptable, interfaceId);
                if (tmpres != null) {
                    return castAs(tmpres, interfaceId);
                }
            }
        }
        if (interfaceId.getName().equals("") 
                && interfaceId.interfaceClass.isInstance(adaptable)) {
            return castAs(adaptable, interfaceId);
        }
        return null;
    }
    
    @Override
    public <T> T getAdapter(Object adaptable, ItfId<T> interfaceId, Predicate<String> alternativePredicate) {
        DT adaptableType = typeHierarchy.dataTypeOf(adaptable);
        Map<String, IAdapterAlternativeFactory> a2f = getFactories(adaptableType, interfaceId);
        if (a2f != null) {
            for(Map.Entry<String,IAdapterAlternativeFactory> a2fEntry : a2f.entrySet()) {
                if (alternativePredicate.test(a2fEntry.getKey())) {
                    IAdapterAlternativeFactory factory = a2fEntry.getValue();
                    Object tmpres =factory.getAdapter(adaptable, interfaceId);
                    if (tmpres != null) {
                        return castAs(tmpres, interfaceId);
                    }
                }
            }
        }
        if (interfaceId.getName().equals("") 
                && interfaceId.interfaceClass.isInstance(adaptable)) {
            return castAs(adaptable, interfaceId);
        }
        return null;
    }

    @Override
    public <T> Map<String,T> getAdapters(Object adaptable, ItfId<T> interfaceId, Predicate<String> alternativePredicate) {
        Map<String,T> result = new LinkedHashMap<>();
        DT adaptableType = typeHierarchy.dataTypeOf(adaptable);
        Map<String, IAdapterAlternativeFactory> a2f = getFactories(adaptableType, interfaceId);
        if (a2f != null) {
            for(Map.Entry<String,IAdapterAlternativeFactory> a2fEntry : a2f.entrySet()) {
                if (alternativePredicate.test(a2fEntry.getKey())) {
                    IAdapterAlternativeFactory factory = a2fEntry.getValue();
                    Object tmpres = factory.getAdapter(adaptable, interfaceId);
                    if (tmpres != null) {
                        result.put(factory.getAlternativeName(), castAs(tmpres, interfaceId));
                    }
                }
            }
        }
        if (interfaceId.getName().equals("") 
                && interfaceId.interfaceClass.isInstance(adaptable)) {
            result.put("", castAs(adaptable, interfaceId));
        }
        return result;
    }
    
    @SuppressWarnings("unchecked")
    private <T> T castAs(Object adaptable, ItfId<T> interfaceId) {
        // not a real cast in java<7,8,9... waiting java 10 for templat etype reifications!
        // if ClassCastException occurs, it would be on (non generic) parent caller method!
        return (T) adaptable; 
    }
    
    @Override
    public <T> boolean hasAdapter(Object adaptable, ItfId<T> interfaceId, Predicate<String> alternativePredicate) {
        DT adaptableType = typeHierarchy.dataTypeOf(adaptable);
        Map<String, IAdapterAlternativeFactory> a2f = getFactories(adaptableType, interfaceId);
        if (a2f != null) {
            for(Map.Entry<String,IAdapterAlternativeFactory> a2fEntry : a2f.entrySet()) {
                if (alternativePredicate.test(a2fEntry.getKey())) {
                    // IAdapterAlternativeFactory factory = a2fEntry.getValue();
                    // result = factory.hasAdapter(adaptable, interfaceId);
                    return true;
                }
            }
        }
        if (interfaceId.getName().equals("") 
                && interfaceId.interfaceClass.isInstance(adaptable)) {
            return true;
        }
        return false;
    }
    
    @Override
    public <T> boolean hasAdapter(Object adaptable, ItfId<T> interfaceId, String alternativeName) {
        DT adaptableType = typeHierarchy.dataTypeOf(adaptable);
        return getFactory(adaptableType, interfaceId, alternativeName) != null;
    }
    
    @Override
    public Set<ItfId<?>> computeAdapterTypes(DT adaptableType, Predicate<String> alternativePredicate) {
        Map<ItfId<?>,Map<String,IAdapterAlternativeFactory>> i2a2f = getFactories(adaptableType);
        Set<ItfId<?>> res = new LinkedHashSet<>();
        for(Map.Entry<ItfId<?>,Map<String,IAdapterAlternativeFactory>> e : i2a2f.entrySet()) {
            ItfId<?> itfId = e.getKey();
            Map<String,IAdapterAlternativeFactory> a2f = e.getValue();
            for(Map.Entry<String,IAdapterAlternativeFactory> afEntry : a2f.entrySet()) {
                if (alternativePredicate.test(afEntry.getKey())) {
                    res.add(itfId);
                    break;
                }
            }
        }
        return res;
    }

    @Override
    public Set<ItfId<?>> computeAdapterTypes(DT adaptableType) {
        Set<ItfId<?>> types = getFactories(adaptableType).keySet();
        return new LinkedHashSet<>(types);
    }

    @Override
    public Set<String> getAdapterAlternatives(DT adaptableDataType, ItfId<?> interfaceId) {
        Set<String> res = new LinkedHashSet<>();
        Map<String, IAdapterAlternativeFactory> a2f = getFactories(adaptableDataType, interfaceId);
        if (a2f != null) {
            res.addAll(a2f.keySet());
        }
        return res;
    }
    
    // implements IAdapterManagerSPI for register/unregister / cache
    // ------------------------------------------------------------------------
    

    @Override
    public synchronized void flushLookup() {
        adapterLookup = null;
    }

    @Override
    public synchronized void registerAdapters(IAdapterAlternativeFactory factory, DT adaptableType) {
        doRegisterFactory(factory, adaptableType);
        flushLookup();
    }

    public synchronized void unregisterAdapters(IAdapterAlternativeFactory factory) {
        for (List<IAdapterAlternativeFactory> ls : factories.values()) {
            ls.remove(factory);
        }
        flushLookup();
    }

    @Override
    public synchronized void unregisterAdapters(IAdapterAlternativeFactory factory, DT adaptable) {
        List<IAdapterAlternativeFactory> factoryList = factories.get(adaptable);
        if (factoryList == null)
            return;
        factoryList.remove(factory);
        flushLookup();
    }

    @Override
    public synchronized void registerAdapters(Collection<AdapterAltFactoryRegistration<DT>> registrations) {
        for(AdapterAltFactoryRegistration<DT> reg : registrations) {
            doRegisterFactory(reg.factory, reg.adaptableDataType);
        }
        flushLookup();
    }
    
    @Override
    public synchronized void unregisterAdapters(Collection<AdapterAltFactoryRegistration<DT>> registrations) {
        for(AdapterAltFactoryRegistration<DT> reg : registrations) {
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

    protected void doRegisterFactory(IAdapterAlternativeFactory factory, DT adaptableType) {
        List<IAdapterAlternativeFactory> list = factories.get(adaptableType);
        if (list == null) {
            list = new ArrayList<>(5);
            factories.put(adaptableType, list);
        }
        list.add(factory);
    }
                
    protected Map<String,IAdapterAlternativeFactory> getFactories(DT adaptableType, ItfId<?> interfaceId) {
        Map<ItfId<?>, Map<String, IAdapterAlternativeFactory>> i2a2f = getFactories(adaptableType);
        Map<String, IAdapterAlternativeFactory> res = i2a2f.get(interfaceId);
        if (res == null) {
            res = new HashMap<>(); //? use Collections.emptyMap()
            i2a2f.put(interfaceId, res);
        }
        return res;
    }

    protected IAdapterAlternativeFactory getFactory(DT adaptableType, ItfId<?> interfaceId, String alternativeName) {
        return getFactories(adaptableType, interfaceId).get(alternativeName);
    }

    /**
     * get factories (indexed per interfaceId) for adaptable datatype.
     * it is lazyly computed for type or parent superType hierarchy, given all factories per type
     */
    private Map<ItfId<?>,Map<String,IAdapterAlternativeFactory>> getFactories(DT adaptableType) {
        // cache reference to lookup to protect against concurrent flush
        Map<DT,Map<ItfId<?>,Map<String,IAdapterAlternativeFactory>>> lookup = adapterLookup;
        if (lookup == null) {
            adapterLookup = lookup = Collections.synchronizedMap(new HashMap<>(30));
        }
        Map<ItfId<?>,Map<String,IAdapterAlternativeFactory>> res = lookup.get(adaptableType);
        if (res == null) {
            // calculate adapters for the class
            res = new HashMap<>(4);
            DT[] superTypes = typeHierarchy.computeSuperTypesOrder(adaptableType);
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
     * IAdapterFactory<ItfId<?>>.getAdapterList).
     */
    private void addFactoriesFor(DT adaptableType, Map<ItfId<?>,Map<String,IAdapterAlternativeFactory>> to) {
        List<IAdapterAlternativeFactory> factoryList = factories.get(adaptableType);
        if (factoryList == null)
            return;
        for (IAdapterAlternativeFactory factory : factoryList) {
            ItfId<?>[] itfIds = factory.getInterfaceIds();
            
            // also register all superInterfaces of ItfIds
            Set<ItfId<?>> allItfIds = new LinkedHashSet<>();
            DefaultLangTypeHierarchy itfHiera = new DefaultLangTypeHierarchy();
            for(ItfId<?> itfId : itfIds) {
                Class<?>[] allItfSuperTypes = itfHiera.computeSuperTypesOrder(itfId.getInterfaceClass());
                for(Class<?> itfSuperType : allItfSuperTypes) {
                    allItfIds.add(ItfId.of(itfSuperType, itfId.getName()));
                }
            }
            
            String alternativeName = factory.getAlternativeName();
            for (ItfId<?> itfId : allItfIds) {
                Map<String, IAdapterAlternativeFactory> a2f = to.get(itfId);
                if (a2f == null) {
                    a2f = new HashMap<>();
                    to.put(itfId, a2f);
                }
                a2f.put(alternativeName, factory);
            }
        }
    }
    
}
