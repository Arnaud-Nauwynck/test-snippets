package fr.iut.tp2;

import java.util.List;
import java.util.Map.Entry;

public class PrintJsonVisitor implements JsonNodeVisitor2<String> {


	@Override
	public String caseBoolean(BooleanJsonNode p) {
		return (p.isValue())? "true" : "false";
	}


	@Override
	public String caseNull(NullJsonNode p) {
		return "null";
	}


	@Override
	public String caseNumeric(NumericJsonNode p) {
		Number value = p.getValue();
		return (value != null)? value.toString() : "0.0";
	}

	@Override
	public String caseText(TextJsonNode p) {
		String value = p.getValue();
		String replace = escapeJsonText(value);
		return "\"" + replace + "\"";
	}

	private static String escapeJsonText(String value) {
		return value.replace("\\", "\\\\").replace("\"", "\\\"");
	}

	@Override
	public String caseObject(ObjectJsonNode p) {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		boolean isFirst = true;
		for(Entry<String, JsonNode> e: p.getValue().entrySet()) {
			if (isFirst) {
				isFirst = false;
			} else {
				sb.append(",");
			}
			sb.append("\"" + escapeJsonText(e.getKey()) + "\":");
			sb.append(e.getValue().toJson());
		}
		sb.append("}");
		return sb.toString();
	}

	
	@Override
	public String caseArray(ArrayJsonNode p) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		List<JsonNode> child = p.getValue();
		int len = child.size();
		for(int i = 0; i < len; i++) {
			JsonNode elt = child.get(i);
			String childRes = elt.accept(this);
			sb.append(childRes);
			if (i + 1 < len) {
				sb.append(",");
			}
		}
		sb.append("]");
		return sb.toString();
	}

}
