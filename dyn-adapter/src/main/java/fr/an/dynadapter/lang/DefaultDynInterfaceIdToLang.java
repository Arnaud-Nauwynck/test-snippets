package fr.an.dynadapter.lang;

import fr.an.dynadapter.IDynInterfaceToLang;

public class DefaultDynInterfaceIdToLang implements IDynInterfaceToLang<Class<?>> {
    
    public boolean isInstance(Object obj, Class<?> interfaceId) {
        return interfaceId.isInstance(obj);
    }
    
}