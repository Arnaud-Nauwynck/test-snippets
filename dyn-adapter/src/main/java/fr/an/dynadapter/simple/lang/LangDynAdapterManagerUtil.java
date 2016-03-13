package fr.an.dynadapter.simple.lang;

import fr.an.dynadapter.simple.DynAdapterManager;
import fr.an.dynadapter.typehiera.DefaultLangTypeHierarchy;

public class LangDynAdapterManagerUtil {

    public static DynAdapterManager<Class<?>,Class<?>> createInstance() {  
        return new DynAdapterManager<Class<?>,Class<?>>( 
                new DefaultLangTypeHierarchy(), 
                new DefaultDynInterfaceIdToLang());
    }
        
}
