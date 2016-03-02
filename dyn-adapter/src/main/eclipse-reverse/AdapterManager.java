package fr.an.eadapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class AdapterManager implements IAdapterManager {
    /** 
     * Cache of adapters for a given adaptable class. Maps String  -> Map
     * (adaptable class name -> (adapter class name -> factory instance))
     * Thread safety note: The outer map is synchronized using a synchronized
     * map wrapper class.  The inner map is not synchronized, but it is immutable
     * so synchronization is not necessary.
     */
    private Map<String,Map<String,IAdapterFactory>> adapterLookup;

    /**
     * Cache of classes for a given type name. Avoids too many loadClass calls.
     * (factory -> (type name -> Class)).
     * Thread safety note: Since this structure is a nested hash map, and both
     * the inner and outer maps are mutable, access to this entire structure is
     * controlled by the classLookupLock field.  Note the field can still be
     * nulled concurrently without holding the lock.
     */
    private Map<IAdapterFactory,Map<String,Class<?>>> classLookup;

    /**
     * The lock object controlling access to the classLookup data structure.
     */
    private final Object classLookupLock = new Object();

    /**
     * Cache of class lookup order (Class -> Class[]). This avoids having to compute often, and
     * provides clients with quick lookup for instanceOf checks based on type name.
     * Thread safety note: The map is synchronized using a synchronized
     * map wrapper class.  The arrays within the map are immutable.
     */
    private Map<Class<?>,Class<?>[]> classSearchOrderLookup;

    /**
     * Map of factories, keyed by <code>String</code>, fully qualified class name of
     * the adaptable class that the factory provides adapters for. Value is a <code>List</code>
     * of <code>IAdapterFactory</code>.
     */
    private final Map<String,List<IAdapterFactory>> factories = new HashMap<>(5);

    private final ArrayList<IAdapterManagerProvider> lazyFactoryProviders = new ArrayList<>(1);

    private static final AdapterManager singleton = new AdapterManager();

    public static AdapterManager getDefault() {
        return singleton;
    }

    /**
     * Private constructor to block instance creation.
     */
    private AdapterManager() {
    }

    /**
     * Given a type name, add all of the factories that respond to those types into
     * the given table. Each entry will be keyed by the adapter class name (supplied in
     * IAdapterFactory.getAdapterList).
     */
    private void addFactoriesFor(String typeName, Map<String,IAdapterFactory> table) {
        List<IAdapterFactory> factoryList = getFactories().get(typeName);
        if (factoryList == null)
            return;
        for (int i = 0, imax = factoryList.size(); i < imax; i++) {
            IAdapterFactory factory = factoryList.get(i);
            if (factory instanceof IAdapterFactoryExt) {
                String[] adapters = ((IAdapterFactoryExt) factory).getAdapterNames();
                for (int j = 0; j < adapters.length; j++) {
                    if (table.get(adapters[j]) == null)
                        table.put(adapters[j], factory);
                }
            } else {
                Class<?>[] adapters = factory.getAdapterList();
                for (int j = 0; j < adapters.length; j++) {
                    String adapterName = adapters[j].getName();
                    if (table.get(adapterName) == null)
                        table.put(adapterName, factory);
                }
            }
        }
    }

    private void cacheClassLookup(IAdapterFactory factory, Class<?> clazz) {
        synchronized (classLookupLock) {
            //cache reference to lookup to protect against concurrent flush
            Map<IAdapterFactory,Map<String,Class<?>>> lookup = classLookup;
            if (lookup == null)
                classLookup = lookup = new HashMap<IAdapterFactory,Map<String,Class<?>>>(4);
            Map<String,Class<?>> classes = lookup.get(factory);
            if (classes == null) {
                classes = new HashMap<String,Class<?>>(4);
                lookup.put(factory, classes);
            }
            classes.put(clazz.getName(), clazz);
        }
    }

    private Class<?> cachedClassForName(IAdapterFactory factory, String typeName) {
        synchronized (classLookupLock) {
            Class<?> clazz = null;
            //cache reference to lookup to protect against concurrent flush
            Map<IAdapterFactory,Map<String,Class<?>>> lookup = classLookup;
            if (lookup != null) {
                Map<String,Class<?>> classes = lookup.get(factory);
                if (classes != null) {
                    clazz = classes.get(typeName);
                }
            }
            return clazz;
        }
    }

    /**
     * Returns the class with the given fully qualified name, or null
     * if that class does not exist or belongs to a plug-in that has not
     * yet been loaded.
     */
    private Class<?> classForName(IAdapterFactory factory, String typeName) {
        Class<?> clazz = cachedClassForName(factory, typeName);
        if (clazz == null) {
            if (factory instanceof IAdapterFactoryExt)
                factory = ((IAdapterFactoryExt) factory).loadFactory(false);
            if (factory != null) {
                try {
                    clazz = factory.getClass().getClassLoader().loadClass(typeName);
                } catch (ClassNotFoundException e) {
                    // it is possible that the default bundle classloader is unaware of this class
                    // but the adaptor factory can load it in some other way. See bug 200068.
                    if (typeName == null)
                        return null;
                    Class<?>[] adapterList = factory.getAdapterList();
                    clazz = null;
                    for (int i = 0; i < adapterList.length; i++) {
                        if (typeName.equals(adapterList[i].getName())) {
                            clazz = adapterList[i];
                            break;
                        }
                    }
                    if (clazz == null)
                        return null; // class not yet loaded
                }
                cacheClassLookup(factory, clazz);
            }
        }
        return clazz;
    }

    @Override
    public String[] computeAdapterTypes(Class<?> adaptable) {
        Set<String> types = getFactories(adaptable).keySet();
        return (String[]) types.toArray(new String[types.size()]);
    }

    /**
     * Computes the adapters that the provided class can adapt to, along
     * with the factory object that can perform that transformation. Returns 
     * a table of adapter class name to factory object.
     * @param adaptable
     */
    private Map<String,IAdapterFactory> getFactories(Class<?> adaptable) {
        //cache reference to lookup to protect against concurrent flush
        Map<String,Map<String,IAdapterFactory>> lookup = adapterLookup;
        if (lookup == null)
            adapterLookup = lookup = Collections.synchronizedMap(new HashMap<>(30));
        Map<String,IAdapterFactory> table = lookup.get(adaptable.getName());
        if (table == null) {
            // calculate adapters for the class
            table = new HashMap<>(4);
            Class<?>[] classes = computeClassOrder(adaptable);
            for (int i = 0; i < classes.length; i++)
                addFactoriesFor(classes[i].getName(), table);
            // cache the table
            lookup.put(adaptable.getName(), table);
        }
        return table;
    }

    /**
     * Returns the super-type search order starting with <code>adaptable</code>. 
     * The search order is defined in this class' comment.
     */
    public Class<?>[] computeClassOrder(Class<?> adaptable) {
        Class<?>[] classes = null;
        //cache reference to lookup to protect against concurrent flush
        Map<Class<?>,Class<?>[]> lookup = classSearchOrderLookup;
        if (lookup == null)
            classSearchOrderLookup = lookup = Collections.synchronizedMap(new HashMap<>());
        else
            classes = lookup.get(adaptable);
        // compute class order only if it hasn't been cached before
        if (classes == null) {
            classes = doComputeClassOrder(adaptable);
            lookup.put(adaptable, classes);
        }
        return classes;
    }

    /**
     * Computes the super-type search order starting with <code>adaptable</code>. 
     * The search order is defined in this class' comment.
     */
    private Class<?>[] doComputeClassOrder(Class<?> adaptable) {
        List<Class<?>> classes = new ArrayList<>();
        Class<?> clazz = adaptable;
        Set<Class<?>> seen = new HashSet<>(4);
        //first traverse class hierarchy
        while (clazz != null) {
            classes.add(clazz);
            clazz = clazz.getSuperclass();
        }
        //now traverse interface hierarchy for each class
        Class<?>[] classHierarchy = (Class[]) classes.toArray(new Class[classes.size()]);
        for (int i = 0; i < classHierarchy.length; i++)
            computeInterfaceOrder(classHierarchy[i].getInterfaces(), classes, seen);
        return (Class[]) classes.toArray(new Class[classes.size()]);
    }

    private void computeInterfaceOrder(Class<?>[] interfaces, Collection<Class<?>> classes, Set<Class<?>> seen) {
        List<Class<?>> newInterfaces = new ArrayList<>(interfaces.length);
        for (int i = 0; i < interfaces.length; i++) {
            Class<?> interfac = interfaces[i];
            if (seen.add(interfac)) {
                //note we cannot recurse here without changing the resulting interface order
                classes.add(interfac);
                newInterfaces.add(interfac);
            }
        }
        for (Iterator<Class<?>> it = newInterfaces.iterator(); it.hasNext();)
            computeInterfaceOrder(it.next().getInterfaces(), classes, seen);
    }

    /**
     * Flushes the cache of adapter search paths. This is generally required whenever an
     * adapter is added or removed.
     * <p>
     * It is likely easier to just toss the whole cache rather than trying to be smart
     * and remove only those entries affected.
     * </p>
     */
    public synchronized void flushLookup() {
        adapterLookup = null;
        classLookup = null;
        classSearchOrderLookup = null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAdapter(Object adaptable, Class<T> adapterType) {
        IAdapterFactory factory = (IAdapterFactory) getFactories(adaptable.getClass()).get(adapterType.getName());
        T result = null;
        if (factory != null)
            result = factory.getAdapter(adaptable, adapterType);
        if (result == null && adapterType.isInstance(adaptable))
            return (T) adaptable;
        return result;
    }

    @Override
    public Object getAdapter(Object adaptable, String adapterType) {
        return getAdapter(adaptable, adapterType, false);
    }

    /**
     * Returns an adapter of the given type for the provided adapter.
     * @param adaptable the object to adapt
     * @param adapterType the type to adapt the object to
     * @param force <code>true</code> if the plug-in providing the
     * factory should be activated if necessary. <code>false</code>
     * if no plugin activations are desired.
     */
    private Object getAdapter(Object adaptable, String adapterType, boolean force) {
        IAdapterFactory factory = (IAdapterFactory) getFactories(adaptable.getClass()).get(adapterType);
        if (force && factory instanceof IAdapterFactoryExt)
            factory = ((IAdapterFactoryExt) factory).loadFactory(true);
        Object result = null;
        if (factory != null) {
            Class<?> clazz = classForName(factory, adapterType);
            if (clazz != null)
                result = factory.getAdapter(adaptable, clazz);
        }
        if (result == null && adaptable.getClass().getName().equals(adapterType))
            return adaptable;
        return result;
    }

    @Override
    public boolean hasAdapter(Object adaptable, String adapterTypeName) {
        return getFactories(adaptable.getClass()).get(adapterTypeName) != null;
    }

    @Override
    public synchronized void registerAdapters(IAdapterFactory factory, Class<?> adaptable) {
        registerFactory(factory, adaptable.getName());
        flushLookup();
    }

    // @Override ?
    public void registerFactory(IAdapterFactory factory, String adaptableType) {
        List<IAdapterFactory> list = factories.get(adaptableType);
        if (list == null) {
            list = new ArrayList<>(5);
            factories.put(adaptableType, list);
        }
        list.add(factory);
    }

    @Override
    public synchronized void unregisterAdapters(IAdapterFactory factory) {
        for (Iterator<List<IAdapterFactory>> it = factories.values().iterator(); it.hasNext();)
            it.next().remove(factory);
        flushLookup();
    }

    @Override
    public synchronized void unregisterAdapters(IAdapterFactory factory, Class<?> adaptable) {
        List<IAdapterFactory> factoryList = factories.get(adaptable.getName());
        if (factoryList == null)
            return;
        factoryList.remove(factory);
        flushLookup();
    }

    /*
     * Shuts down the adapter manager by removing all factories
     * and removing the registry change listener. Should only be
     * invoked during platform shutdown.
     */
    public synchronized void unregisterAllAdapters() {
        factories.clear();
        flushLookup();
    }

    public void registerLazyFactoryProvider(IAdapterManagerProvider factoryProvider) {
        synchronized (lazyFactoryProviders) {
            lazyFactoryProviders.add(factoryProvider);
        }
    }

    public boolean unregisterLazyFactoryProvider(IAdapterManagerProvider factoryProvider) {
        synchronized (lazyFactoryProviders) {
            return lazyFactoryProviders.remove(factoryProvider);
        }
    }

    public Map<String,List<IAdapterFactory>> getFactories() {
        synchronized (lazyFactoryProviders) {
            while (lazyFactoryProviders.size() > 0) {
                IAdapterManagerProvider provider = lazyFactoryProviders.remove(0);
                if (provider.addFactories(this))
                    flushLookup();
            }
        }
        return factories;
    }
}