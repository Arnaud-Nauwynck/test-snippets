package fr.an.dynadapter.alt;

import java.util.Collection;

public interface IAdapterAlternativesManagerSPI<DT> {

    public void registerAdapters(IAdapterAlternativeFactory factory, DT adaptableType);
    public void unregisterAdapters(IAdapterAlternativeFactory factory, DT adaptableType);
    public void registerAdapters(Collection<AdapterAltFactoryRegistration<DT>> registrations);
    public void unregisterAdapters(Collection<AdapterAltFactoryRegistration<DT>> registrations);
    public void unregisterAdapters(IAdapterAlternativeFactory factory);
    public void unregisterAllAdapters();
    
    public void flushLookup();
    
    public static class AdapterAltFactoryRegistration<DT> {
        public final IAdapterAlternativeFactory factory;
        public final DT adaptableDataType;
        // public final String alternativeName;  ... cf IAdapterAlternativeFactory
        
        public AdapterAltFactoryRegistration(IAdapterAlternativeFactory factory, DT adaptableDataType) {
            this.factory = factory;
            this.adaptableDataType = adaptableDataType;
        }
        
    }
}