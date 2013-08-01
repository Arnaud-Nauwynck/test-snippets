package fr.an.log.logparser;

import java.io.Serializable;

import fr.an.log.logparser.lexer.LogLexer;

public final class TemplatizedTokenKey implements Serializable, Comparable<TemplatizedTokenKey> {

	/** */
	private static final long serialVersionUID = 1L;
	
	private final int tokenType;
	private final Object templatizedValue;
	
	// ------------------------------------------------------------------------
	
	public TemplatizedTokenKey(int tokenType, Object value) {
		super();
		this.tokenType = tokenType;
		this.templatizedValue = value;
	}

	// ------------------------------------------------------------------------

	public int getTokenType() {
		return tokenType;
	}

	public Object getTemplatizedValue() {
		return templatizedValue;
	}

	// ------------------------------------------------------------------------
	
	public int compareTo(TemplatizedTokenKey other) {
		int res = 0;
		if (tokenType != other.tokenType) {
			res = (tokenType < other.tokenType)? -1 : +1;
		}
		if (res == 0 && templatizedValue instanceof Comparable) {
			res = ((Comparable) templatizedValue).compareTo(other);
		}
		
		return res;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = tokenType;
		result = prime * result + ((templatizedValue == null) ? 0 : templatizedValue.hashCode());
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
		TemplatizedTokenKey other = (TemplatizedTokenKey) obj;
		if (tokenType != other.tokenType)
			return false;
		if (templatizedValue == null) {
			if (other.templatizedValue != null)
				return false;
		} else if (!templatizedValue.equals(other.templatizedValue))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[" + LogLexer.tokenNames[tokenType] + ", " + templatizedValue + "]";
	}
	
}
