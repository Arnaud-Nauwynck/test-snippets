package fr.an.tests.detectobjectcycle;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeCheckObjectGraphVisitor {

	public static class Node {
		public final Class<?> type;
		private List<NodeRef> fromLinks = new ArrayList<>();
		private List<NodeRef> toLinks = new ArrayList<>();
		
		public Node(Class<?> type) {
			this.type = type;
		}
		public void addLinkTo(String field, Node to) {
			new NodeRef(this, to, field);
		}
		public List<NodeRef> getFromLinks() {
			return fromLinks;
		}
		public List<NodeRef> getToLinks() {
			return toLinks;
		}
		@Override
		public String toString() {
			return type.getName();
		}
		
	}
	
	private static class NodeRef {
		public final Node from;
		public final Node to;
		public final String field;
		
		public NodeRef(Node from, Node to, String field) {
			this.from = from;
			this.to = to;
			this.field = field;
			from.toLinks.add(this);
			to.fromLinks.add(this);
		}
		
	}

	private Node rootNode;
	private Map<Class<?>,Node> classNodes = new HashMap<>();
	
	// ------------------------------------------------------------------------

	public TypeCheckObjectGraphVisitor() {
	}

	// ------------------------------------------------------------------------

	public Node visitRoot(Class<?> clss) {
		Node clssNode = new Node(clss);
		classNodes.put(clss, clssNode);
		rootNode = clssNode;
		visitFields(clssNode);
		return clssNode;
	}
	
	private Node visit(Class<?> clss) {
		Node clssNode = classNodes.get(clss);
		if (clssNode == null) {
			clssNode = new Node(clss);
			classNodes.put(clss, clssNode);
			
			visitFields(clssNode);
		}
		return clssNode;
	}

	private void visitFields(Node clssNode) {
		Class<?> clss = clssNode.type;
		for(Class<?> currClss = clss; currClss != Object.class; currClss = currClss.getSuperclass()) {
			Field[] fields = currClss.getDeclaredFields();
			if (fields != null && fields.length > 0) {
				for(Field f : fields) {
					if (Modifier.isStatic(f.getModifiers())) {
						continue;
					}
					Class<?> fieldType = f.getType();
					if (fieldType.isArray()) {
						fieldType = fieldType.getComponentType();
					} else if (Collection.class.isAssignableFrom(fieldType)) {
						Type fieldGenericType = f.getGenericType();
						if (fieldGenericType instanceof ParameterizedType) {
							ParameterizedType parameterizedType = (ParameterizedType) fieldGenericType;
							fieldType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
						}
					} else if (Map.class.isAssignableFrom(fieldType)) {
						Type fieldGenericType = f.getGenericType();
						if (fieldGenericType instanceof ParameterizedType) {
							ParameterizedType parameterizedType = (ParameterizedType) fieldGenericType;
							fieldType = (Class<?>) parameterizedType.getActualTypeArguments()[1];
						}
					}

					Node fieldTypeNode = visit(fieldType);
					
					List<NodeRef> fromLinks = fieldTypeNode.getFromLinks();
					if (! fromLinks.isEmpty()) {
						System.out.println("detected new cycle: ");
						System.out.println(" " + clssNode + " -> " + f.getName() + " -> "+ fieldTypeNode);
						NodeRef prevNodeRef = fromLinks.get(0);
						System.out.println(" " + fromLinks.size() + " prev(s): [0]:" + prevNodeRef.from + " -> " + prevNodeRef.field + " -> "+ prevNodeRef.to);
						System.out.println();
					} else if (fieldTypeNode == rootNode) {
						System.out.println("detected new cycle to root: ");
						System.out.println(" " + clssNode + " -> " + f.getName() + " -> "+ fieldTypeNode);

					}
					
					clssNode.addLinkTo(f.getName(), fieldTypeNode);
				}
			}
		}
	}
	
}
 