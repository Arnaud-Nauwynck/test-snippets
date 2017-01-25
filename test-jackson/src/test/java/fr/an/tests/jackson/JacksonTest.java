package fr.an.tests.jackson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * All these test are ON-PURPOSE failing (or surprinsingly not)
 * 
 * The challenge is to understand which ones are KO, and which ones work... Then to fix them.
 * 
 * see http://arnaud-nauwynck.github.io/2017/01/25/test-jackson.html
 *
 * Remove the following "@Ignore", and try it
 */
@Ignore
public class JacksonTest extends AbstractJsonTest {
	
	public static class APojo {
		private int field1;

		public APojo(int field1) {
			this.field1 = field1;
		}

		public int getField1() {
			return field1;
		}

		public void setField1(int field1) {
			this.field1 = field1;
		}

	}
	
	@Test
	public void testPojo() {
		APojo a = new APojo(123);
		APojo cloneA = serializeToJsonThenToObj(a);
		Assert.assertEquals(a.getField1(), cloneA.getField1());
	}
	
	// ------------------------------------------------------------------------

	
	public static class APublic {
		public int field1;
	}
	
	@Test
	public void testAPublic() {
		APublic a = new APublic(); a.field1 = 123;
		APublic cloneA = serializeToJsonThenToObj(a);
		Assert.assertEquals(a.field1, cloneA.field1);
	}
	
	// ------------------------------------------------------------------------

	public static class ANoSetter {
		private int field1;

		public ANoSetter() {
		}
		public ANoSetter(int field1) {
			this.field1 = field1;
		}

		public int getField1() {
			return field1;
		}
		public void __setField1(int field1) {
			this.field1 = field1;
		}
	}
	
	@Test
	public void testANoSetter() {
		ANoSetter a = new ANoSetter(123);
		ANoSetter cloneA = serializeToJsonThenToObj(a);
		Assert.assertEquals(a.getField1(), cloneA.getField1());
	}


	// ------------------------------------------------------------------------

	public static class ADerivedGetter {
		public int field1;
		public int getDerivedField1() {
			return field1 + 10;
		}
		public boolean isField1Set() {
			return field1 != 0;
		}
	}
	
	@Test
	public void testADerivedGetter() {
		ADerivedGetter a = new ADerivedGetter(); a.field1 = 123;
		ADerivedGetter cloneA = serializeToJsonThenToObj(a);
		Assert.assertEquals(a.field1, cloneA.field1);
	}

	// ------------------------------------------------------------------------

	public static class ImmutableA {
		private final int field1;

		public ImmutableA(int field1) {
			this.field1 = field1;
		}

		public int getField1() {
			return field1;
		}

	}
	
	@Test
	public void testImmutableA() {
		ImmutableA a = new ImmutableA(123);
		ImmutableA cloneA = serializeToJsonThenToObj(a);
		Assert.assertEquals(a.getField1(), cloneA.getField1());
	}

	

	// ------------------------------------------------------------------------

	public static class ImmutableValueOfA {
		private static final ImmutableValueOfA[] instances = new ImmutableValueOfA[100];
		private static boolean calledFromValueOf = false;
		private final int field1;

		public static ImmutableValueOfA valueOf(int field1) {
			calledFromValueOf = true;
			try {
				if (0 <= field1 && field1 < 100) {
					if (null == instances[field1]) instances[field1] = new ImmutableValueOfA(field1);
					return instances[field1];
				}
				return new ImmutableValueOfA(field1);
			} finally {
				calledFromValueOf = false;
			}
		}

		private ImmutableValueOfA(int field1) {
			if (!calledFromValueOf) {
				throw new IllegalStateException("not called from valueOf!");
			}
			this.field1 = field1;
		}

		public int getField1() {
			return field1;
		}

	}
	
	@Test
	public void testImmutableValueOfA() {
		ImmutableValueOfA a2 = ImmutableValueOfA.valueOf(123);
		ImmutableValueOfA cloneA2 = serializeToJsonThenToObj(a2);
		Assert.assertEquals(a2.getField1(), cloneA2.getField1());
		
		ImmutableValueOfA a = ImmutableValueOfA.valueOf(23);
		ImmutableValueOfA cloneA = serializeToJsonThenToObj(a);
		Assert.assertSame(a, cloneA);		
	}
	
	// ------------------------------------------------------------------------

	public static class ABase {
		public int field1;

		public ABase(@JsonProperty("field1") int field1) {
			this.field1 = field1;
		}
		
	}
	public static class BExtendsBase extends ABase {
		public int field2;

		public BExtendsBase(@JsonProperty("field1") int field1, @JsonProperty("field2") int field2) {
			super(field1);
			this.field2 = field2;
		}
	}
	
	@Test
	public void testBExtendsA() {
		BExtendsBase a = new BExtendsBase(123, 234);
		BExtendsBase cloneA = serializeToJsonThenToObj(a);
		Assert.assertEquals(a.field1, cloneA.field1);
		Assert.assertEquals(a.field2, cloneA.field2);

		ABase a2 = a;
		ABase cloneA2 = serializeToJsonThenToObj(a2, ABase.class);
		Assert.assertEquals(BExtendsBase.class, cloneA2.getClass());
		Assert.assertEquals(a.field1, cloneA.field1);
		Assert.assertEquals(((BExtendsBase) a).field2, ((BExtendsBase) cloneA).field2);
	}

	// ------------------------------------------------------------------------

	public static class ListGenericExtendsA<T extends ABase> {
		public List<T> ls = new ArrayList<>();
	}
	
	@Test
	public void testListGenericExtendsA_base() {
		ListGenericExtendsA<ABase> a = new ListGenericExtendsA<>();
		ABase elt0 = new ABase(123);
		a.ls.add(elt0);
		ListGenericExtendsA<ABase> cloneA = serializeToJsonThenToObj(a);
		ABase cloneElt0 = cloneA.ls.get(0); 
		Assert.assertEquals(cloneElt0.field1, elt0.field1);
	}

	@Test
	public void testListGenericExtendsA_extends() {
		ListGenericExtendsA<BExtendsBase> a = new ListGenericExtendsA<>();
		BExtendsBase elt0 = new BExtendsBase(123, 234);
		a.ls.add(elt0);
		ListGenericExtendsA<BExtendsBase> cloneA = serializeToJsonThenToObj(a);
		BExtendsBase cloneElt0 = cloneA.ls.get(0); 
		Assert.assertEquals(cloneElt0.field1, elt0.field1);
	}

	@Test
	public void testListGenericExtendsA_runtime_extends() {
		ListGenericExtendsA<ABase> a = new ListGenericExtendsA<>();
		ABase elt0 = new ABase(123);
		a.ls.add(elt0);
		BExtendsBase elt1 = new BExtendsBase(123, 234);
		a.ls.add(elt1);
		ListGenericExtendsA<ABase> cloneA = serializeToJsonThenToObj(a);
		ABase cloneElt0 = cloneA.ls.get(0);
		Assert.assertEquals(elt0.field1, cloneElt0.field1);
		BExtendsBase cloneElt1 = (BExtendsBase) cloneA.ls.get(1);
		Assert.assertEquals(elt1.field1, cloneElt1.field1);
		Assert.assertEquals(elt1.field2, cloneElt1.field2);
	}

		
	// ------------------------------------------------------------------------

	public static class AAcceptUnknownProps {
		public int field1;
	}
	
	@Test
	public void testAAcceptUnknownProps() {
		String json = "{ \"field1\": 123, \"unknownProps\": true }";
		AAcceptUnknownProps a = fromJson(json, AAcceptUnknownProps.class);
		Assert.assertEquals(123, a.field1);
	}
	

	// ------------------------------------------------------------------------

	public static interface IA {
		int getField1();
	}
	
	public static class DefaultA implements IA {
		public int field1;
		public DefaultA(@JsonProperty("field1") int field1) {
			this.field1 = field1;
		}
		@Override 
		public int getField1() {
			return field1;
		}
	}
	
	@Test
	public void testIA() {
		IA a = new DefaultA(123);
		IA cloneA = serializeToJsonThenToObj(a, IA.class);
		Assert.assertEquals(a.getField1(), cloneA.getField1());
	}
	

	// ------------------------------------------------------------------------

	public static interface IAComplexHierarchy {
	}
	
	public static class DefaultAComplexHierarchy implements IAComplexHierarchy {
		public int field1;
		public DefaultAComplexHierarchy(@JsonProperty("field1") int field1) {
			this.field1 = field1;
		}
	}
	public static enum EnumAComplexHierarchy implements IAComplexHierarchy {
		ENUM1, ENUM2, ENUM3;
	}
	@Test
	public void testIAComplexHierarchy() {
		IAComplexHierarchy a = new DefaultAComplexHierarchy(123);
		IAComplexHierarchy cloneA = serializeToJsonThenToObj(a, IAComplexHierarchy.class);
		Assert.assertEquals(123, ((DefaultAComplexHierarchy) cloneA).field1);

		IAComplexHierarchy a2 = EnumAComplexHierarchy.ENUM1;
		IAComplexHierarchy cloneA2 = serializeToJsonThenToObj(a2, IAComplexHierarchy.class);
		Assert.assertSame(EnumAComplexHierarchy.ENUM1, cloneA2);
	}
	

	// ------------------------------------------------------------------------

	@SuppressWarnings("rawtypes")
	public static class AListRaw {
		public List/*Object*/ ls = new ArrayList();
	}

	@SuppressWarnings({ "unchecked" })
	@Test
	public void testAListRaw() {
		AListRaw a = new AListRaw();
		a.ls.add(new APojo(123));
		AListRaw cloneA = serializeToJsonThenToObj(a, AListRaw.class);
		APojo cloneAElt0 = (APojo) cloneA.ls.get(0);
		Assert.assertEquals(123, cloneAElt0.field1);
	}
	
	// ------------------------------------------------------------------------

	public static class AKey {
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + field1;
			result = prime * result + field2;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			AKey other = (AKey) obj;
			if (field1 != other.field1)
				return false;
			if (field2 != other.field2)
				return false;
			return true;
		}
		public final int field1, field2;
		public AKey(@JsonProperty("field1") int field1, @JsonProperty("field2")int field2) {
			this.field1 = field1;
			this.field2 = field2;
		}
		
	}
	
	public static class AKeyToIntMap {
		public Map<AKey,Integer> map = new HashMap<>();
	}

	@Test
	public void testAKeyToIntMap() {
		AKeyToIntMap a = new AKeyToIntMap();
		a.map.put(new AKey(123, 234), 1);
		AKeyToIntMap cloneA = serializeToJsonThenToObj(a, AKeyToIntMap.class);
		Assert.assertEquals(a.map.size(), cloneA.map.size());
	}

	// ------------------------------------------------------------------------

	public static class AParent {
		public List<AChild> child = new ArrayList<>();
	}
	public static class AChild {
		public AParent parent;
		public AChild(AParent parent) {
			this.parent = parent;
		}		
	}
	
	@Ignore
	@Test
	public void testParentChild() {
		AParent a = new AParent();
		AChild child = new AChild(a);
		a.child.add(child);
		AParent cloneA = serializeToJsonThenToObj(a, AParent.class);
		Assert.assertEquals(a.child.size(), cloneA.child.size());
	}
	
}
