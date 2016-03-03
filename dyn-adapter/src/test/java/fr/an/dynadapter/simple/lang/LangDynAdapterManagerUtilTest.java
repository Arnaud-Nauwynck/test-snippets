package fr.an.dynadapter.simple.lang;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.an.dynadapter.simple.DynAdapterManager;
import fr.an.dynadapter.simple.lang.LangDynAdapterManagerUtil;
import fr.an.dynadapter.tstfoo.Foo;
import fr.an.dynadapter.tstfoo.Foo2;
import fr.an.dynadapter.tstfoo.FooBarAdapter;
import fr.an.dynadapter.tstfoo.IBar;

public class LangDynAdapterManagerUtilTest {

    protected DynAdapterManager<Class<?>, Class<?>> sut;

    @Before
    public void setup() {
        sut = LangDynAdapterManagerUtil.createInstance();
        sut.registerAdapters(new FooBarAdapter.Factory(), Foo.class);
    }

    @Test
    public void testGetAdapter() {
        // Prepare
        Foo foo = new Foo();
        foo.setFooValue("foo");
        // Perform
        IBar bar = (IBar) sut.getAdapter(foo, IBar.class);
        String res = bar.getBarValue();
        // Post-check
        Assert.assertEquals(foo.getFooValue(), res);
    }

    @Test
    public void testGetAdapter_Foo2() {
        // Prepare
        Foo2 foo2 = new Foo2();
        foo2.setFooValue("foo");
        // Perform
        IBar bar = (IBar) sut.getAdapter(foo2, IBar.class);
        String res = bar.getBarValue();
        // Post-check
        Assert.assertEquals(foo2.getFooValue(), res);
    }
}
