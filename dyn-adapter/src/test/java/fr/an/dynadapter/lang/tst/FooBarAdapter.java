package fr.an.dynadapter.lang.tst;

import fr.an.dynadapter.IDynAdapterFactory;

public class FooBarAdapter implements IBar {

    protected Foo fooDelegate;
    
    public FooBarAdapter(Foo fooDelegate) {
        this.fooDelegate = fooDelegate;
    }

    @Override
    public String getBarValue() {
        return fooDelegate.getFooValue();
    }
    
    public static class Factory implements IDynAdapterFactory<Class<?>> {

        @Override
        public /*<T> T*/ Object getAdapter(Object adaptableObject, Class<?> interfaceId) {
            assert interfaceId == IBar.class;
            return new FooBarAdapter((Foo) adaptableObject);
        }

        @Override
        public Class<?>[] getInterfaceIds() {
            return new Class<?>[] { IBar.class };
        }
        
    }
}
