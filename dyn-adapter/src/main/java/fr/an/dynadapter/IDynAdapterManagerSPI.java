package fr.an.dynadapter;

import java.util.Collection;

public interface IDynAdapterManagerSPI<DT,IId> {

    public void registerAdapters(IDynAdapterFactory<IId> factory, DT adaptableType);
    public void unregisterAdapters(IDynAdapterFactory<IId> factory, DT adaptableType);
    public void registerAdapters(Collection<AdapterFactoryRegistration<DT,IId>> registrations);
    public void unregisterAdapters(Collection<AdapterFactoryRegistration<DT,IId>> registrations);
    public void unregisterAdapters(IDynAdapterFactory<IId> factory);
    public void unregisterAllAdapters();
    
    public void flushLookup();
    
    public static class AdapterFactoryRegistration<DT,IId> {
        public final IDynAdapterFactory<IId> factory;
        public final DT adaptableDataType;
        
        public AdapterFactoryRegistration(IDynAdapterFactory<IId> factory, DT adaptableDataType) {
            this.factory = factory;
            this.adaptableDataType = adaptableDataType;
        }
        
    }
}