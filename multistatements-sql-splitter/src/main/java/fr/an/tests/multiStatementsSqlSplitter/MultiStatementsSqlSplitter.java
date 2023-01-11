package fr.an.tests.multiStatementsSqlSplitter;

import java.util.ArrayList;
import java.util.List;

import lombok.val;

public class MultiStatementsSqlSplitter {

	  // Adapted splitSemiColon from Hive 2.3's CliDriver.splitSemiColon.
	  // Note: [SPARK-31595] if there is a `'` in a double quoted string, or a `"` in a single quoted
	  // string, the origin implementation from Hive will not drop the trailing semicolon as expected,
	  // hence we refined this function a little bit.
	  // Note: [SPARK-33100] Ignore a semicolon inside a bracketed comment in spark-sql.
	  public static List<TextWithLineNumber> splitSemiColon(String text) {
	    boolean insideSingleQuote = false;
	    boolean insideDoubleQuote = false;
	    boolean insideSimpleComment = false;
	    int bracketedCommentLevel = 0;
	    boolean escape = false;
	    int beginIndex = 0;
	    int beginLineNumber = 1; // in sync with beginIndex
	    boolean leavingBracketedComment = false;
	    boolean isStatement = false;
	    int currentLineNumber = 1;
	    List<TextWithLineNumber> ret = new ArrayList<TextWithLineNumber>();

	    // computed
	    boolean insideComment = insideSimpleComment || bracketedCommentLevel > 0;
//	    def insideBracketedComment: Boolean = bracketedCommentLevel > 0
//	    def insideComment: Boolean = insideSimpleComment || insideBracketedComment
//	    def statementInProgress(index: Int): Boolean = isStatement || (!insideComment &&
//	      index > beginIndex && !s"${line.charAt(index)}".trim.isEmpty)

	    final int textLen = text.length();
	    for (int index = 0; index < textLen; index++) {
	      // Checks if we need to decrement a bracketed comment level; the last character '/' of
	      // bracketed comments is still inside the comment, so `insideBracketedComment` must keep true
	      // in the previous loop and we decrement the level here if needed.
	      if (leavingBracketedComment) {
	        bracketedCommentLevel--;
	        insideComment = insideSimpleComment || bracketedCommentLevel > 0; // reeval
	        leavingBracketedComment = false;
	      }
	      char ch = text.charAt(index);
	      
	      if (ch == '\'' && !insideComment) {
	        // take a look to see if it is escaped
	        // See the comment above about SPARK-31595
	        if (!escape && !insideDoubleQuote) {
	          // flip the boolean variable
	          insideSingleQuote = !insideSingleQuote;
	        }
	      } else if (ch == '\"' && !insideComment) {
	        // take a look to see if it is escaped
	        // See the comment above about SPARK-31595
	        if (!escape && !insideSingleQuote) {
	          // flip the boolean variable
	          insideDoubleQuote = !insideDoubleQuote;
	        }
	      } else if (ch == '-') {
	        val hasNext = index + 1 < textLen;
	        if (insideDoubleQuote || insideSingleQuote || insideComment) {
	          // Ignores '-' in any case of quotes or comment.
	          // Avoids to start a comment(--) within a quoted segment or already in a comment.
	          // Sample query: select "quoted value --"
	          //                                    ^^ avoids starting a comment if it's inside quotes.
	        } else if (hasNext && text.charAt(index + 1) == '-') {
	          // ignore quotes and ; in simple comment
	          insideSimpleComment = true;
	          insideComment = true;
	        }
	      } else if (ch == ';') {
	        if (insideSingleQuote || insideDoubleQuote || insideComment) {
	          // do not split
	        } else {
	          if (isStatement) {
	            // split, do not include ; itself
	            ret.add(new TextWithLineNumber(text.substring(beginIndex, index), beginLineNumber));
	          }
	          beginIndex = index + 1;
	          beginLineNumber = currentLineNumber;
	          isStatement = false;
	        }
	      } else if (ch == '\n') {
	        // with a new line the inline simple comment should end.
	        if (!escape) {
	        	currentLineNumber++;
	        	insideSimpleComment = false;
	        	insideComment = insideSimpleComment || bracketedCommentLevel > 0; // reeval computed var
	        }
	      } else if (ch == '/' && !insideSimpleComment) {
	        val hasNext = index + 1 < textLen;
	        if (insideSingleQuote || insideDoubleQuote) {
	          // Ignores '/' in any case of quotes
	        } else if (bracketedCommentLevel > 0 && text.charAt(index - 1) == '*' ) {
	          // Decrements `bracketedCommentLevel` at the beginning of the next loop
	          leavingBracketedComment = true;
	        } else if (hasNext && text.charAt(index + 1) == '*') {
	          bracketedCommentLevel++;
	          insideComment = insideSimpleComment || bracketedCommentLevel > 0; // reeval
	        }
	      }
	      // set the escape
	      escape = (ch == '\\');

	      isStatement = isStatement || (!insideComment &&
	    	      index > beginIndex && Character.isWhitespace(ch));
	    }
	    // Check the last char is end of nested bracketed comment.
	    val endOfBracketedComment = leavingBracketedComment && bracketedCommentLevel == 1;
	    // Spark SQL support simple comment and nested bracketed comment in query body.
	    // But if Spark SQL receives a comment alone, it will throw parser exception.
	    // In Spark SQL CLI, if there is a completed comment in the end of whole query,
	    // since Spark SQL CLL use `;` to split the query, CLI will pass the comment
	    // to the backend engine and throw exception. CLI should ignore this comment,
	    // If there is an uncompleted statement or an uncompleted bracketed comment in the end,
	    // CLI should also pass this part to the backend engine, which may throw an exception
	    // with clear error message.
	    if (!endOfBracketedComment && (isStatement || bracketedCommentLevel > 0)) {
	      ret.add(new TextWithLineNumber(text.substring(beginIndex), beginLineNumber));
	    }
	    return ret;
	  }

}
