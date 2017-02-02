package fr.an.tests.eclipselink.dto.querydsl;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.querydsl.collections.CollQuery;
import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.TemplateExpression;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;

import fr.an.tests.eclipselink.dto.HotelDTO;

public class QHotelDTOTest {

	@Test
	public void testQueryDslMetadata() {
		QHotelDTO qhotel = QHotelDTO.hotel;
		NumberPath<Long> qLongField = qhotel.id;
		QCityDTO qRef = qhotel.city;
		
		Assert.assertNotNull(qhotel);
		Assert.assertNotNull(qLongField);
		Assert.assertNotNull(qRef);
		PathMetadata qLongFieldMeta = qLongField.getMetadata(); // parent:QHotelDTO, pathType=PROPERTY, element="id", rootPath="hotel"
		Assert.assertEquals("id", qLongFieldMeta.getElement());
		
		AnnotatedElement qLongFieldAnnotatedElt = qLongField.getAnnotatedElement();
		com.querydsl.core.util.Annotations qLongFieldAnnotations = (com.querydsl.core.util.Annotations) qLongFieldAnnotatedElt;
		Annotation[] annotations = qLongFieldAnnotations.getAnnotations();
		Assert.assertEquals(0, annotations.length);
	}
	
	@Test
	public void testQueryDslCollections() {
		// execute in-memory filtering on Collections using generated-java->compile class->Method cache->invoke
		List<HotelDTO> hotels = new ArrayList<>();
		for(int i = 0; i < 10; i++) {
			HotelDTO bean = new HotelDTO();
			bean.setId((long) i);
			bean.setAddress("address " + i);
			hotels.add(bean);
		}
		
		QHotelDTO qhotel = QHotelDTO.hotel;
		List<HotelDTO> ls = new CollQuery<HotelDTO>().from(qhotel, hotels)
        	.where(qhotel.address.contains("address 1").or(qhotel.address.eq("address 2")))
        	.select(qhotel)
        	.fetch();
		Assert.assertEquals(2, ls.size());
		// dump java code generated, using debugguer breakpoint in DefaultEvaluatorFactory.createEvaluator(), then wrapped in JDKEvaluatorFactory.compile()
/*
public class Q_2058302188_1275614662_1275614662_1195259493_1195259493 {

    public static Iterable<fr.an.tests.eclipselink.dto.HotelDTO> eval(Iterable<fr.an.tests.eclipselink.dto.HotelDTO> hotel_, String a1, String a2) {
java.util.List<fr.an.tests.eclipselink.dto.HotelDTO> rv = new java.util.ArrayList<fr.an.tests.eclipselink.dto.HotelDTO>();
for (fr.an.tests.eclipselink.dto.HotelDTO hotel : hotel_) {
    try {
        if (hotel.getAddress().contains(a1) || com.querydsl.collections.CollQueryFunctions.equals(hotel.getAddress(), a2)) {
            rv.add(hotel);
        }
    } catch (NullPointerException npe) { }
}
return rv;    }

}
 */
	}
	
	@Test
	public void testQueryDslPredicate() {
		QHotelDTO qhotel = QHotelDTO.hotel;
		BooleanExpression predicate = qhotel.address.contains("address 1").or(qhotel.address.eq("address 2"));
		// toString => Operation contains(hotel.address,address 1) || hotel.address = address 2 
		
		// dump recusive
		StringBuilder sb = new StringBuilder();
		predicate.accept(new Visitor<Void,Void>() {
			@Override
			public Void visit(Constant<?> expr, Void context) {
				String textValue;
				Object cstValue = expr.getConstant();
				if (cstValue == null) {
					textValue = "<null>";		
				} else if (cstValue instanceof String) {
					textValue = "'" + cstValue + "'"; 
				} else {
					textValue = "" + cstValue; 
				}
				println("Constant", textValue);
				return null;
			}
			@Override
			public Void visit(FactoryExpression<?> expr, Void context) {
				println("FactoryExpression", expr);
				return null;
			}
			@Override
			public Void visit(Operation<?> expr, Void context) {
				println("Operation", expr);
				indent("Operation");
				Operator operator = expr.getOperator();
				List<Expression<?>> args = expr.getArgs();
				println("operator: '" + operator.name() + "'");
				println("args: " + args.size() + " elt(s)");
				indent();
				for(int i = 0; i < args.size(); i++) {
					println("arg[" + i + "]");
					Expression<?> arg = args.get(i);
					if (arg != null) {
						arg.accept(this, context);
					}
				}
				
				unindent();
				unindent("Operation");
				
				return null;
			}
			@Override
			public Void visit(ParamExpression<?> expr, Void context) {
				println("ParamExpression", expr);
				return null;
			}
			@Override
			public Void visit(Path<?> expr, Void context) {
				println("Path", expr);
				return null;
			}
			@Override
			public Void visit(SubQueryExpression<?> expr, Void context) {
				println("SubQueryExpression", expr);
				return null;
			}
			@Override
			public Void visit(TemplateExpression<?> expr, Void context) {
				println("TemplateExpression", expr);
				return null;
			}
			
			private int indentLevel;
			protected void indent() {
				indentLevel++;
			}
			protected void unindent() {
				indentLevel--;
			}
			protected void indent(String text) {
				println("{ " + text);
				indent();
			}
			protected void unindent(String text) {
				unindent();;
				println("} " + text);
			}
			protected void println(String type, Object obj) {
				println(type + " " + obj);
			}
			protected void println(String text) {
				for(int i = 0; i < indentLevel; i++) {
					sb.append("  ");
				}
				sb.append(text + " \n");
			}
		}, null);
		
		String dump = sb.toString();
		// System.out.println(dump);
		// dump =>
/*
 		{ Operation 
			  operator: 'OR' 
			  args: 2 elt(s) 
			    arg[0] 
			    Operation contains(hotel.address,address 1) 
			    { Operation 
			      operator: 'STRING_CONTAINS' 
			      args: 2 elt(s) 
			        arg[0] 
			        Path hotel.address 
			        arg[1] 
			        Constant 'address 1' 
			    } Operation 
			    arg[1] 
			    Operation hotel.address = address 2 
			    { Operation 
			      operator: 'EQ' 
			      args: 2 elt(s) 
			        arg[0] 
			        Path hotel.address 
			        arg[1] 
			        Constant 'address 2' 
			    } Operation 
			} Operation 

*/	
	}
	
}
