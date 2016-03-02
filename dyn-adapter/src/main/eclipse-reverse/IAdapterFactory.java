package fr.an.eadapter;

public interface IAdapterFactory {

    public <T> T getAdapter(Object adaptableObject, Class<T> adapterType);
    
    public Class<?>[] getAdapterList();
    
}
