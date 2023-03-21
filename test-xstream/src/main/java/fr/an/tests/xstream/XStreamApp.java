package fr.an.tests.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;

public class XStreamApp {

	public static void main(String[] args) {
		IgnorableCtx ctx = new IgnorableCtx();
		A a = new A();
		a.ctx = ctx;
		a.ctx2 = ctx;
		// a.ctx3ExplicitOmmitAnnotation = ctx;

		{
			System.out.println("XStream ... default:");
			XStream xstream1 = new XStream();
			addDefaultAlias(xstream1);
			String xml1 = xstream1.toXML(a);
			System.out.println(xml1);
			System.out.println();
		}
		
		{
			System.out.println("XStream ... override ignore fields in IgnorableCtx");
			XStream xstream = new XStream();
			addDefaultAlias(xstream);
			xstream.registerConverter(new IgnorableCtxXStreamConverter());
			String xml = xstream.toXML(a);
			System.out.println(xml);
			System.out.println();
		}
		
		{
			System.out.println("XStream.omitField(A.class, 'ctx')");
			XStream xstream3 = new XStream();
			addDefaultAlias(xstream3);
			xstream3.omitField(A.class, "ctx");
			xstream3.omitField(A.class, "ctx2");
			String xml3 = xstream3.toXML(a);
			System.out.println(xml3);
			System.out.println();
		}

	
		{
			System.out.println("XStream ... JSON");
			XStream xstreamJson = new XStream(new JsonHierarchicalStreamDriver());
			addDefaultAlias(xstreamJson);
			xstreamJson.omitField(A.class, "ctx");
			xstreamJson.omitField(A.class, "ctx2");
			String json = xstreamJson.toXML(a);
			System.out.println(json);
			System.out.println();
		}

	}
	
	public static class IgnorableCtxXStreamConverter implements Converter {

		public boolean canConvert(Class type) {
			return IgnorableCtx.class.isAssignableFrom(type);
		}

		public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
			System.out.println("override XStream marshal for " + source + " => do nothing..");
		}

		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			System.out.println("override XStream unmarshal for .. => new ..");
			return new IgnorableCtx();
		}
		
	}
	
	public static void addDefaultAlias(XStream xstream) {
		xstream.alias("A", A.class);
	}
	
//	public static class AXStreamConverter extends Converter {
//
//		public boolean canConvert(Class type) {
//			return IgnorableCtx.class.isAssignableFrom(type);
//		}
//
//	}
	
	public static class IgnorableCtx {
		private int internalDetails = 1; // to be ignored..
		private IgnorableCtxImpl impl = new IgnorableCtxImpl(); // to be ignored
	}

	public static class IgnorableCtxImpl {
		private int internalDetails2 = 2; // to be ignored..
	}
	
	public static class A {
		private int okToSerialize;
		IgnorableCtx ctx;
		IgnorableCtx ctx2;

//		@XStreamOmitField // does not work??
//		IgnorableCtx ctx3ExplicitOmmitAnnotation;
	}

}
