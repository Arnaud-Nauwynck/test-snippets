package fr.an.dynadapter;

public interface IDynAdapterFactory<IId> {

    public /*<T> T*/ Object getAdapter(Object adaptableObject, IId interfaceId);
    
    public IId[] getInterfaceIds();

}
