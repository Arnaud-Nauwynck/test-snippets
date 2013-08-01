package fr.an.log.logparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;

import fr.an.log.logparser.lexer.LogLexer;

public class LexerApp {

	public static void main(String[] args) {
		BufferedReader stdinReader = new BufferedReader(new InputStreamReader(System.in));
        String line = null;
        for(;;) {
        	System.out.println("enter line> ");
        	try {
				line = stdinReader.readLine();
			} catch (IOException e) {
				break;
			}
        	if (line == null) {
        		break;
        	}
        	
        	line = line + "\n";
        	CharStream lineStream = new ANTLRInputStream(line);
        	
        	LogLexer lexer = new LogLexer(lineStream);
        
        	List<? extends Token> tokens = lexer.getAllTokens();
        	for(Token token : tokens) {
        		int tokenType = token.getType();
        		String tokenName = LogLexer.tokenNames[tokenType];
        		String text = token.getText();
        		System.out.println("token:" + tokenName + " '" + text + "'"
        				 + " " + token 
        				 );
        	}
        }
		
	}
}
