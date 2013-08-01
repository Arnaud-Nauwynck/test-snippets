package fr.an.log.logparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.Token;

import fr.an.log.logparser.lexer.LogLexer;

public class ParseFileToPrefixTreeApp {

	private static final boolean DEBUG = false;

	public static void main(String[] args) {
		String fileName = "test-log-dmesg.txt";
		File inputFile = new File(fileName);
		
		TemplatizedTokenTree tree = new TemplatizedTokenTree(); 
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));
			parseToTokenTree(reader, tree);
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (reader != null) try { reader.close(); } catch(IOException ex) {}
		}
		
		tree.dumpCounts();
	}

	private static void parseToTokenTree(BufferedReader reader, TemplatizedTokenTree tree) {
		String line = null;
		
		TemplatizedPathHandler tokenPathHandler = tree.createTemplatizedPathHandler();
		
		for(;;) {
			try {
				line = reader.readLine();
			} catch (IOException e) {
				break;
			}
			if (line == null) {
				break;
			}
			
			// line = line + "\n";
			ANTLRInputStream lineStream = new ANTLRInputStream(line);
			
			LogLexer lexer = new LogLexer(lineStream);
			
			List<? extends Token> tokens = lexer.getAllTokens();
			for(Token token : tokens) {
				if (DEBUG) {
					int tokenType = token.getType();
					String tokenName = LogLexer.tokenNames[tokenType];
					String text = token.getText();
					System.out.println("token:" + tokenName + " '" + text + "'" + " " + token );
				}

				tokenPathHandler.handleNextToken(token);
			}
			tokenPathHandler.endToken();
			
			lexer.reset();			
		}
	}
}
