package fr.an.log.logparser;

import org.antlr.v4.runtime.Token;

import fr.an.log.logparser.lexer.LogLexer;

public class TemplatizedPathHandler {
	
	private TemplatizedTokenTree tree;

	// private TemplatizedTokenTreeNode currNode; // redundant with elements[currElementIndex].node !!

	private static class PathElement {
		TemplatizedTokenTreeNode templatizedNode;
		// int tokenType ... = node.getKey().getTokenType()
		// Object templatizedValue ... = node.getKey().
		Object value;
	}
	
	private PathElement[] elements = new PathElement[15];
	private int currElementIndex;
	
	// ------------------------------------------------------------------------
	
	public TemplatizedPathHandler(TemplatizedTokenTree tree) {
		this.tree = tree;
		this.currElementIndex = 0;
		for(int i = 0; i < elements.length; i++) {
			elements[i] = new PathElement();
		}
		// reallocIncreaseElementArray();
		this.elements[currElementIndex].templatizedNode = tree.getRootNode(); 
		// this.currNode = tree.getRootNode();
	}
	
	// ------------------------------------------------------------------------
	
	public void handleNextToken(Token token) {
		PathElement parentPathElement = elements[currElementIndex];
		this.currElementIndex++;
		if (currElementIndex >= elements.length) {
			reallocIncreaseElementArray();
		}
		PathElement currPathElement = elements[currElementIndex];

		TemplatizedTokenKey childKey = extractTokenTemplateAndValue(token, currPathElement);

		// find or create child elt
		currPathElement.templatizedNode = parentPathElement.templatizedNode.findOrCreateChild(childKey);
		
		// TODO trigger intermediate actions ?
		// currPathElement.templatizedNode.incrCountAsNode();
		
	}


	public void endToken() {
		// TODO trigger actions ... 
		PathElement currPathElement = elements[currElementIndex];
		currPathElement.templatizedNode.incrCountAsLeaf();
		
		this.currElementIndex = 0;
	}
	
	
	private TemplatizedTokenKey extractTokenTemplateAndValue(Token token,
			PathElement currElt) {
		int tokenType = token.getType();
		String text = token.getText();
		Object value = text;
		Object templatizedValue = null;
		switch(tokenType) {
		case LogLexer.IDENT: 
			value = text;
			templatizedValue = text;
			break;
		case LogLexer.TEXT: 
			value = text; 
			templatizedValue = text;
			break;
		case LogLexer.NL:
			templatizedValue = null;
			break;
		case LogLexer.WS:
			templatizedValue = null;
			break;
		case LogLexer.STRING_LITERAL1: case LogLexer.STRING_LITERAL2: 
			templatizedValue = null;
			break;
		case LogLexer.STRING_LITERAL_LONG1: case LogLexer.STRING_LITERAL_LONG2:
			templatizedValue = null;
			break;
			
			
		case LogLexer.PUNCTUATION:
		case LogLexer.ASSIGN_OP: 
			templatizedValue = null;
			break;

		case LogLexer.INT: 
			value = Integer.parseInt(text);
			templatizedValue = null;
			break;
		case LogLexer.DECIMAL: case LogLexer.DOUBLE: 
			value = Double.parseDouble(text);
			templatizedValue = null;
			break;
		
		case LogLexer.DATE:
			value = null; // TODO...
			templatizedValue = null;
			break;
			
		case LogLexer.OPEN_BRACE: 
		case LogLexer.CLOSE_BRACE: 
		case LogLexer.OPEN_CURLY_BRACE: 
		case LogLexer.CLOSE_CURLY_BRACE: 
		case LogLexer.OPEN_SQUARE_BRACKET: 
		case LogLexer.CLOSE_SQUARE_BRACKET: 
			value = text;
			templatizedValue = text;
			// TODO enter mode for matching close brace/bracket..
			break;
		
		case LogLexer.OTHER:
			value = null; // TODO...
			templatizedValue = null;
			break;

		default:
			// Should not occur
			value = null; // TODO...
			templatizedValue = null;
			break;
				
		}
		
		currElt.value = value;

		TemplatizedTokenKey childKey = new TemplatizedTokenKey(tokenType, templatizedValue);
		return childKey;
	}

	private void reallocIncreaseElementArray() {
		// realloc bigger array..
		int newAllocSize = elements.length + 20;
		PathElement[] newelements = new PathElement[newAllocSize];
		System.arraycopy(elements, 0, newelements, 0,  elements.length);
		for (int i = elements.length; i < newAllocSize; i++) {
			newelements[i] = new PathElement();
		}
		this.elements = newelements;
	}
	
	
}