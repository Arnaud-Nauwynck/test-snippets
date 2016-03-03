package fr.an.dynadapter.simple;

import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.an.dynadapter.simple.lang.LangDynAdapterManagerUtil;
import fr.an.dynadapter.tstdynobj.IDynBar;
import fr.an.dynadapter.tstdynobj.TstDynDataTypeHierarchy;
import fr.an.dynadapter.tstdynobj.TstDynFooBarAdapter;
import fr.an.dynadapter.tstdynobj.TstDynInterfaceId;
import fr.an.dynadapter.tstdynobj.TstDynInterfaceIdToLang;
import fr.an.dynadapter.tstdynobj.TstDynObject;
import fr.an.dynadapter.tstdynobj.TstDynType;
import fr.an.dynadapter.tstdynobj.TstDynTypeUtils;
import fr.an.dynadapter.tstfoo.Foo;
import fr.an.dynadapter.tstfoo.IBar;

public class DynAdapterManagerTest {

    protected DynAdapterManager<TstDynType, TstDynInterfaceId> dynSut;
    protected DynAdapterManager<Class<?>, Class<?>> staticSut = LangDynAdapterManagerUtil.createInstanceFoo();
        
    @Before
    public void setup() {
        dynSut = new DynAdapterManager<>(new TstDynDataTypeHierarchy(), new TstDynInterfaceIdToLang());
        dynSut.registerAdapters(new TstDynFooBarAdapter.Factory(), TstDynTypeUtils.fooType);
    }

    @Test
    public void testGetAdapter() {
        // Prepare
        TstDynObject foo = new TstDynObject(TstDynTypeUtils.fooType);
        foo.setField("fooField", "foo");
        // Perform
        IDynBar bar = (IDynBar) dynSut.getAdapter(foo, TstDynTypeUtils.CST_IDynBar);
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
        IDynBar bar = (IDynBar) dynSut.getAdapter(foo2, TstDynTypeUtils.CST_IDynBar);
        String res = bar.getBarValue();
        // Post-check
        Assert.assertEquals("foo2", res);
    }

    @Test
    public void testGetAdapter_static() { 
        DynAdapterManager<Class<?>, Class<?>> sut = LangDynAdapterManagerUtil.createInstanceFoo();
        Foo foo = new Foo();
        foo.setFooValue("foo");
        IBar res = (IBar) sut.getAdapter(foo, IBar.class);
        Assert.assertNotNull(res);
        Assert.assertEquals("foo", res.getBarValue());
    }    
    
    @Test
    public void testHasAdapter_static() { 
        Object foo = new Foo();
        boolean res = staticSut.hasAdapter(foo, IBar.class);
        Assert.assertTrue(res);        
    }

    @Test
    public void testComputeAdapterTypes_static() {
        Set<Class<?>> types = staticSut.computeAdapterTypes(Foo.class);
        Assert.assertEquals(1, types.size());
        Assert.assertTrue(types.contains(IBar.class));
    }
    
}
