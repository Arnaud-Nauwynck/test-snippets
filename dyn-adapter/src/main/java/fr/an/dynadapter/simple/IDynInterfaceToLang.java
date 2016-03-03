package fr.an.dynadapter.simple;

public interface IDynInterfaceToLang<IId> {

    // public Class<?> nativeTypeFor(IId interfaceId);

    public boolean isInstance(Object obj, IId interfaceId);
    
    // ------------------------------------------------------------------------

}
