package fr.an.dynadapter.alt;

import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.an.dynadapter.tstfoo.Foo;
import fr.an.dynadapter.tstfoo.FooBarAdapter;
import fr.an.dynadapter.tstfoo.IBar;
import fr.an.dynadapter.typehiera.DefaultLangTypeHierarchy;

public class AdapterAlternativesManagerTest {

    protected AdapterAlternativesManager<Class<?>> staticSut;

    Foo foo = new Foo();

    @Before
    public void setup() {
        staticSut = new AdapterAlternativesManager<>(new DefaultLangTypeHierarchy());
        staticSut.registerAdapters(new FooBarAdapterAlternative1Factory(), Foo.class);
        staticSut.registerAdapters(new FooBarAdapterAlternative2Factory(), Foo.class);

        foo.setFooValue("foo");
    }
    
    @Test
    public void testComputeAdapterTypes() {
        Set<ItfId<?>> res = staticSut.computeAdapterTypes(Foo.class);
        Assert.assertEquals(1, res.size());
        Assert.assertEquals(new ItfId<>(IBar.class,  ""), res.iterator().next());
    }
    
    @Test
    public void testGetAdapter() {
        IBar bar = staticSut.getAdapter(foo, IBar.class);
        Assert.assertNotNull(bar);
        Assert.assertEquals("alt2-foo", bar.getBarValue());
    }
    
    @Test
    public void getAdapters() {
        Map<String,IBar> res = staticSut.getAdapters(foo, new ItfId<>(IBar.class), x -> true);
        Assert.assertEquals(2, res.size());
        IBar alt1Bar = res.get("alt1");
        IBar alt2Bar = res.get("alt2");
        Assert.assertEquals("alt1-foo", alt1Bar.getBarValue());
        Assert.assertEquals("alt2-foo", alt2Bar.getBarValue());
    }
    
    @Test 
    public void testComputeAdapterTypes_alt1() {
        Set<ItfId<?>> res = staticSut.computeAdapterTypes(Foo.class, "alt1");
        Assert.assertEquals(1, res.size());
        Assert.assertEquals(new ItfId<>(IBar.class,  ""), res.iterator().next());

        res = staticSut.computeAdapterTypes(Foo.class, "alt2");
        Assert.assertEquals(1, res.size());
        Assert.assertEquals(new ItfId<>(IBar.class), res.iterator().next());

        res = staticSut.computeAdapterTypes(Foo.class, "alt-unknown");
        Assert.assertTrue(res.isEmpty());
    }
    
    @Test
    public void testHasAdapter_alt() {
        Assert.assertTrue(staticSut.hasAdapter(foo, new ItfId<>(IBar.class), "alt1"));
        Assert.assertTrue(staticSut.hasAdapter(foo, new ItfId<>(IBar.class), x -> x.startsWith("alt")));
        Assert.assertFalse(staticSut.hasAdapter(foo, new ItfId<>(IBar.class), "alt-unknown"));
    }
    
    @Test
    public void testGetAdapterAlternatives() {
        Set<String> res = staticSut.getAdapterAlternatives(Foo.class, new ItfId<>(IBar.class));
        Assert.assertEquals(2, res.size());
        Assert.assertTrue(res.contains("alt1"));
        Assert.assertTrue(res.contains("alt2"));
    }
    
    // ------------------------------------------------------------------------
    
    protected static class FooBarAdapterAlternative1Factory implements IAdapterAlternativeFactory {

        @Override
        public Object getAdapter(Object adaptableObject, ItfId<?> interfaceId) {
            return new FooBarAdapter((Foo) adaptableObject) {
                @Override
                public String getBarValue() {
                    return "alt1-" + super.getBarValue();
                }
            };
        }

        @Override
        public ItfId<?>[] getInterfaceIds() {
            return new ItfId<?>[] { new ItfId<>(IBar.class, "") };
        }

        @Override
        public String getAlternativeName() {
            return "alt1";
        }
    }
    
    protected static class FooBarAdapterAlternative2Factory implements IAdapterAlternativeFactory {

        @Override
        public Object getAdapter(Object adaptableObject, ItfId<?> interfaceId) {
            return new FooBarAdapter((Foo) adaptableObject) {
                @Override
                public String getBarValue() {
                    return "alt2-" + super.getBarValue();
                }
            };
        }

        @Override
        public ItfId<?>[] getInterfaceIds() {
            return new ItfId<?>[] { new ItfId<>(IBar.class, "") };
        }

        @Override
        public String getAlternativeName() {
            return "alt2";
        }
    }
    
}
