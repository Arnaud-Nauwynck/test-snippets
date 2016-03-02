package fr.an.dynadapter.dyntypes;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.an.dynadapter.DynAdapterManager;
import fr.an.dynadapter.dyntypes.tst.IDynBar;
import fr.an.dynadapter.dyntypes.tst.TstDynDataTypeHierarchy;
import fr.an.dynadapter.dyntypes.tst.TstDynFooBarAdapter;
import fr.an.dynadapter.dyntypes.tst.TstDynInterfaceId;
import fr.an.dynadapter.dyntypes.tst.TstDynInterfaceIdToLang;
import fr.an.dynadapter.dyntypes.tst.TstDynObject;
import fr.an.dynadapter.dyntypes.tst.TstDynType;
import fr.an.dynadapter.dyntypes.tst.TstDynTypeUtils;


public class TstDynAdapterManagerUtilTest {

    protected DynAdapterManager<TstDynType, TstDynInterfaceId> sut;
    
    @Before
    public void setup() {
        sut = new DynAdapterManager<>(new TstDynDataTypeHierarchy(), new TstDynInterfaceIdToLang());

        sut.registerAdapters(new TstDynFooBarAdapter.Factory(), TstDynTypeUtils.fooType);
    }

    @Test
    public void testGetAdapter() {
        // Prepare
        TstDynObject foo = new TstDynObject(TstDynTypeUtils.fooType);
        foo.setField("fooField", "foo");
        // Perform
        IDynBar bar = (IDynBar) sut.getAdapter(foo, TstDynTypeUtils.CST_IDynBar);
        String res = bar.getBarValue();
        // Post-check
        Assert.assertEquals("foo", res);
    }

    @Test
    public void testGetAdapter_Foo2() {
        // Prepare
        TstDynObject foo2 = new TstDynObject(TstDynTypeUtils.foo2Type);
        foo2.setField("fooField", "foo2");
        // Perform
        IDynBar bar = (IDynBar) sut.getAdapter(foo2, TstDynTypeUtils.CST_IDynBar);
        String res = bar.getBarValue();
        // Post-check
        Assert.assertEquals("foo2", res);
    }
}
