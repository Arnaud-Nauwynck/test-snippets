package fr.an.dynadapter.lang;

import fr.an.dynadapter.DynAdapterManager;

public class LangDynAdapterManagerUtil {

    public static DynAdapterManager<Class<?>,Class<?>> createInstance() {  
        return new DynAdapterManager<Class<?>,Class<?>>( 
                new DefaultLangDynDataTypeHierarchy(), 
                new DefaultDynInterfaceIdToLang());
    }
    
}
