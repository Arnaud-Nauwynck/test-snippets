package fr.an.dynadapter.dyntypes;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.an.dynadapter.DynAdapterManager;

public class TstDynAdapterManagerUtilTest {

    protected DynAdapterManager<TstDynType, TstDynInterfaceId> sut;
    /*pp*/ static final TstDynType fooType = new TstDynType("foo", null, null);
    /*pp*/ static final TstDynType foo2Type = new TstDynType("foo2", fooType, null);

    /*pp*/ static final TstDynInterfaceId CST_IDynBar = new TstDynInterfaceId("IDynBar"); 
    
    @Before
    public void setup() {
        sut = new DynAdapterManager<>(new TstDynDataTypeHierarchy(), new TstDynInterfaceIdToLang());

        sut.registerAdapters(new TstDynFooBarAdapter.Factory(), fooType);
    }

    @Test
    public void testGetAdapter() {
        // Prepare
        TstDynObject foo = new TstDynObject(fooType);
        foo.setField("fooField", "foo");
        // Perform
        IDynBar bar = (IDynBar) sut.getAdapter(foo, CST_IDynBar);
        String res = bar.getBarValue();
        // Post-check
        Assert.assertEquals("foo", res);
    }

    @Test
    public void testGetAdapter_Foo2() {
        // Prepare
        TstDynObject foo2 = new TstDynObject(foo2Type);
        foo2.setField("fooField", "foo2");
        // Perform
        IDynBar bar = (IDynBar) sut.getAdapter(foo2, CST_IDynBar);
        String res = bar.getBarValue();
        // Post-check
        Assert.assertEquals("foo2", res);
    }
}
