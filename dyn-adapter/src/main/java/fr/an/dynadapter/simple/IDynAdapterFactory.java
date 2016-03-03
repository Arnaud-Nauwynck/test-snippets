package fr.an.dynadapter.simple;

public interface IDynAdapterFactory<IId> {

    public /*<T> T*/ Object getAdapter(Object adaptableObject, IId interfaceId);
    
    public IId[] getInterfaceIds();

}
