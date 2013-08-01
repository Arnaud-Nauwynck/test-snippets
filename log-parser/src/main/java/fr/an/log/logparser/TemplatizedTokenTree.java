package fr.an.log.logparser;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import fr.an.log.logparser.lexer.LogLexer;

public class TemplatizedTokenTree {
	
	private TemplatizedTokenTreeNode rootNode;
	
	// ------------------------------------------------------------------------

	public TemplatizedTokenTree() {
		TemplatizedTokenKey rootKey = new TemplatizedTokenKey(LogLexer.OTHER, null);
		rootNode = new TemplatizedTokenTreeNode(rootKey);
	}

	// ------------------------------------------------------------------------

	public TemplatizedPathHandler createTemplatizedPathHandler() {
		return new TemplatizedPathHandler(this);
	}

	public TemplatizedTokenTreeNode getRootNode() {
		return rootNode;
	}

	public void dumpCounts() {
		PrintStream out = System.out;
		List<TemplatizedTokenTreeNode> currPath = new ArrayList<TemplatizedTokenTreeNode>();
		currPath.add(rootNode);
		recursiveDumpNode(out, currPath, rootNode, false);
		
	}

	private void recursiveDumpNode(PrintStream out,
			List<TemplatizedTokenTreeNode> currPath,
			TemplatizedTokenTreeNode currNode,
			boolean detailTree) {
		
		TemplatizedTokenKey key = currNode.getKey();
		
		if (detailTree) {
			indent(out, currPath.size());
			out.append("" + key);
		}
		if (currNode.getCountAsLeaf() != 0) {
			if (detailTree) {
				out.append(" countAsLeaf:" + currNode.getCountAsLeaf());
				out.append("\n");
			}
			
			// print whole line template... 
			out.append("" + currNode.getCountAsLeaf() + " : ");
			dumpTemplatePath(out, currPath);
			out.append("\n");
		}
		
		LinkedHashMap<TemplatizedTokenKey, TemplatizedTokenTreeNode> childMap = currNode.getChildMap();
		if (!childMap.isEmpty()) {
			
			// *** recurse***
			for(TemplatizedTokenTreeNode child : childMap.values()) {
				// currPath.add(child);
				List<TemplatizedTokenTreeNode> childPath = new ArrayList<TemplatizedTokenTreeNode>();
				childPath.addAll(currPath);
				childPath.add(child);

				recursiveDumpNode(out, childPath, child, detailTree);

				// currPath.removeAt(currPath.size() - 1);
			}
			
		}
		
	}
	
	private void dumpTemplatePath(PrintStream out, List<TemplatizedTokenTreeNode> path) {
		int len = path.size();
		for(int i = 0; i < len; i++) {
			TemplatizedTokenTreeNode elt = path.get(i);
			
			TemplatizedTokenKey key = elt.getKey();
			dumpTemplateToken(out, key);
			
			if (i+1 < len) {
				out.append(" "); //?? cf WS...
			}
		}
	}

	private void dumpTemplateToken(PrintStream out, TemplatizedTokenKey key) {
		int tokenType = key.getTokenType();
		// out.append(LogLexer.tokenNames[tokenType]);
		Object templatizedValue = key.getTemplatizedValue();
		
		switch(tokenType) {
		case LogLexer.IDENT: 
			out.append("IDENT");
			break;
		case LogLexer.TEXT: 
			out.append("" + templatizedValue);
			break;
		case LogLexer.NL:
			out.append("\n");
			break;
		case LogLexer.WS:
			out.append(" ");
			break;
		case LogLexer.STRING_LITERAL1: case LogLexer.STRING_LITERAL2: 
			out.append("\"?\"");
			break;
		case LogLexer.STRING_LITERAL_LONG1: case LogLexer.STRING_LITERAL_LONG2:
			out.append("\"\"\"?\"\"\"");
			break;
			
		case LogLexer.PUNCTUATION:
			out.append("" + templatizedValue);
			break;
		case LogLexer.ASSIGN_OP: 
			out.append("" + templatizedValue);
			break;

		case LogLexer.INT: 
			out.append("?int");
			break;

		case LogLexer.DECIMAL: case LogLexer.DOUBLE: 
			out.append("?double");
			break;
		
		case LogLexer.DATE:
			out.append("?date");
			break;
			
		case LogLexer.OPEN_BRACE:
			out.append("(");
			break;
		case LogLexer.CLOSE_BRACE: 
			out.append(")");
			break;
		case LogLexer.OPEN_CURLY_BRACE: 
			out.append("{");
			break;
		case LogLexer.CLOSE_CURLY_BRACE: 
			out.append("}");
			break;
		case LogLexer.OPEN_SQUARE_BRACKET: 
			out.append("[");
			break;
		case LogLexer.CLOSE_SQUARE_BRACKET: 
			out.append("]");
			break;
		
		case LogLexer.OTHER:
			out.append("?other");
			break;

		default:
			// Should not occur
			out.append("??unknown");
			break;
				
		}
				
	}
	
	private void indent(PrintStream out, int indentLevel) {
		for (int i = 0; i < indentLevel; i++) {
			out.append("  ");
		}
	}
}
