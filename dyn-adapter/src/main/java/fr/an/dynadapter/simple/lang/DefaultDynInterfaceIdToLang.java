package fr.an.dynadapter.simple.lang;

import fr.an.dynadapter.simple.IDynInterfaceToLang;

public class DefaultDynInterfaceIdToLang implements IDynInterfaceToLang<Class<?>> {
    
    public boolean isInstance(Object obj, Class<?> interfaceId) {
        return interfaceId.isInstance(obj);
    }
    
}