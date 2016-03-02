package fr.an.eadapter;


public interface IAdapterManager {

    /**
     * Returns the types that can be obtained by converting <code>adaptableClass</code> 
     * via this manager. Converting means that subsequent calls to <code>getAdapter()</code>
     * or <code>loadAdapter()</code> could result in an adapted object.
     * <p>
     * Note that the returned types do not guarantee that
     * a subsequent call to <code>getAdapter</code> with the same type as an argument
     * will return a non-null result. If the factory's plug-in has not yet been
     * loaded, or if the factory itself returns <code>null</code>, then
     * <code>getAdapter</code> will still return <code>null</code>.
     * </p>
     * @param adaptableClass the adaptable class being queried  
     * @return an array of type names that can be obtained by converting 
     * <code>adaptableClass</code> via this manager. An empty array 
     * is returned if there are none.
     * @since 3.1
     */
    public String[] computeAdapterTypes(Class<?> adaptableClass);

    /**
     * Returns the class search order for a given class. The search order from a 
     * class with the definition <br>
     * <code>class X extends Y implements A, B</code><br>
     * is as follows:
     * <ul>
     * <li>the target's class: X
     * <li>X's superclasses in order to <code>Object</code>
     * <li>a breadth-first traversal of each class's interfaces in the
     * order returned by <code>getInterfaces</code> (in the example, X's 
     * superinterfaces then Y's superinterfaces) </li>
     * </ul>
     * 
     * @param clazz the class for which to return the class order. 
     * @return the class search order for the given class. The returned
     * search order will minimally  contain the target class.
     * @since 3.1
     */
    public Class<?>[] computeClassOrder(Class<?> clazz);

    /**
     * Returns an object which is an instance of the given class associated
     * with the given object. Returns <code>null</code> if no such object can
     * be found.
     * <p>
     * Note that this method will never cause plug-ins to be loaded. If the
     * only suitable factory is not yet loaded, this method will return <code>null</code>.
     * 
     * @param adaptable the adaptable object being queried (usually an instance
     * of <code>IAdaptable</code>)
     * @param adapterType the type of adapter to look up
     * @return an object castable to the given adapter type, or <code>null</code>
     * if the given adaptable object does not have an available adapter of the
     * given type
     */
    public <T> T getAdapter(Object adaptable, Class<T> adapterType);

    public Object getAdapter(Object adaptable, String adapterTypeName);

    public boolean hasAdapter(Object adaptable, String adapterTypeName);


    public void registerAdapters(IAdapterFactory factory, Class<?> adaptable);

    public void unregisterAdapters(IAdapterFactory factory);
    public void unregisterAdapters(IAdapterFactory factory, Class<?> adaptable);
}