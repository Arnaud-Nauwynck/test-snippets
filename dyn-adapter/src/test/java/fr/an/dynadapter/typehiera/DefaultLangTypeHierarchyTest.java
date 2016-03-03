package fr.an.dynadapter.typehiera;

import org.junit.Assert;
import org.junit.Test;

import fr.an.dynadapter.tstfoo.Bar;
import fr.an.dynadapter.tstfoo.Bar2;
import fr.an.dynadapter.tstfoo.Foo;
import fr.an.dynadapter.tstfoo.Foo2;
import fr.an.dynadapter.tstfoo.IBar;
import fr.an.dynadapter.tstfoo.IBar2;

public class DefaultLangTypeHierarchyTest {

    protected DefaultLangTypeHierarchy sut = new DefaultLangTypeHierarchy();
    
    @Test
    public void testComputeSuperTypesOrder_Foo() {
        Class<?>[] res = sut.computeSuperTypesOrder(Foo.class);
        Assert.assertEquals(2, res.length);
        Assert.assertSame(Foo.class, res[0]);
        Assert.assertSame(Object.class, res[1]);
    }
    
    @Test
    public void testComputeSuperTypesOrder_Foo2() {
        Class<?>[] res = sut.computeSuperTypesOrder(Foo2.class);
        Assert.assertEquals(3, res.length);
        Assert.assertSame(Foo2.class, res[0]);
        Assert.assertSame(Foo.class, res[1]);
        Assert.assertSame(Object.class, res[2]);
    }
    
    @Test
    public void testComputeSuperTypesOrder_Bar2() {
        Class<?>[] res = sut.computeSuperTypesOrder(Bar2.class);
        Assert.assertEquals(5, res.length);
        int i = 0;
        Assert.assertSame(Bar2.class, res[i++]);
        Assert.assertSame(Bar.class, res[i++]);
        Assert.assertSame(Object.class, res[i++]);
        Assert.assertSame(IBar2.class, res[i++]);
        Assert.assertSame(IBar.class, res[i++]);
    }
}

