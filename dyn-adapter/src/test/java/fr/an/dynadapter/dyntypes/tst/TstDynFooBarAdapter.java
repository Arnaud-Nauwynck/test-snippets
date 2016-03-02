package fr.an.dynadapter.dyntypes.tst;

import fr.an.dynadapter.IDynAdapterFactory;

public class TstDynFooBarAdapter implements IDynBar {

    private TstDynObject dynFooDelegate;

    public TstDynFooBarAdapter(TstDynObject p) {
        this.dynFooDelegate = p;
    }

    @Override
    public String getBarValue() {
        return (String) dynFooDelegate.getField("fooField");
    }
    
    public static class Factory implements IDynAdapterFactory<TstDynInterfaceId> {

        @Override
        public /*<T> T*/ Object getAdapter(Object adaptableObject, TstDynInterfaceId interfaceId) {
            assert interfaceId == TstDynTypeUtils.CST_IDynBar;
            return new TstDynFooBarAdapter((TstDynObject) adaptableObject);
        }

        @Override
        public TstDynInterfaceId[] getInterfaceIds() {
            return new TstDynInterfaceId[] { TstDynTypeUtils.CST_IDynBar };
        }
        
    }
}
