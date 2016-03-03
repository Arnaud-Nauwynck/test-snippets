package fr.an.dynadapter.simple.lang;

import fr.an.dynadapter.simple.DynAdapterManager;
import fr.an.dynadapter.tstfoo.Foo;
import fr.an.dynadapter.tstfoo.FooBarAdapter;
import fr.an.dynadapter.typehiera.DefaultLangTypeHierarchy;

public class LangDynAdapterManagerUtil {

    public static DynAdapterManager<Class<?>,Class<?>> createInstance() {  
        return new DynAdapterManager<Class<?>,Class<?>>( 
                new DefaultLangTypeHierarchy(), 
                new DefaultDynInterfaceIdToLang());
    }
    
    public static DynAdapterManager<Class<?>,Class<?>> createInstanceFoo() {  
        DynAdapterManager<Class<?>,Class<?>> res = createInstance();
        res.registerAdapters(new FooBarAdapter.Factory(), Foo.class);
        return res;
    }
    
}
