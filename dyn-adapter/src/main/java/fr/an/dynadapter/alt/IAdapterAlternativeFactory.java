package fr.an.dynadapter.alt;

public interface IAdapterAlternativeFactory {

    public Object getAdapter(Object adaptableObject, ItfId<?> interfaceId);
    
    public ItfId<?>[] getInterfaceIds();
    
    public String getAlternativeName();

}
